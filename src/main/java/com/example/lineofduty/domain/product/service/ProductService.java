package com.example.lineofduty.domain.product.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ProductStatus;
import com.example.lineofduty.domain.product.dto.request.ProductRequest;
import com.example.lineofduty.domain.product.dto.response.ProductResponse;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    // 락 설정 상수
    private static final long LOCK_WAIT_TIME = 10L;
    private static final long LOCK_LEASE_TIME = 5L;
    private static final TimeUnit LOCK_TIME_UNIT = TimeUnit.SECONDS;

    // 상품 등록
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (request.getName() == null || request.getDescription() == null) {
            throw new CustomException(ErrorMessage.MISSING_PRODUCT_NAME_OR_DESCRIPTION);
        }

        if (request.getPrice() <= 0) {
            throw new CustomException(ErrorMessage.INVALID_PRICE);
        }

        if (request.getStock() <= 0) {
            throw new CustomException(ErrorMessage.INVALID_STOCK);
        }

        Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getStock(), ProductStatus.ON_SALE);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    // 상품 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        return ProductResponse.from(product);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductList(int page, int size, String sort, String direction, String keyword) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Product> products;

        if (keyword == null || keyword.trim().isEmpty()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.searchByKeyword(keyword, pageable);
        }

        return products.map(ProductResponse::from);
    }

    // 상품 수정
    @Transactional
    public ProductResponse updateProduct(ProductRequest request, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        product.update(request);
        productRepository.saveAndFlush(product);

        return ProductResponse.from(product);
    }

    // 상품 이미지 업데이트
    @Transactional
    public void updateProductImage(Long productId, String productImageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        product.updateProductImage(productImageUrl);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorMessage.PRODUCT_NOT_FOUND);
        }

        productRepository.deleteById(productId);
    }

    // 재고 감소 (분산 락 적용 - 주문 시 사용)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(Long productId, Long quantity) {
        String lockKey = "product:stock:" + productId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;

        try {
            acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, LOCK_TIME_UNIT);

            if (!acquired) {
                throw new CustomException(ErrorMessage.LOCK_ACQUISITION_FAILED);
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

            if (product.getStock() < quantity) {
                throw new CustomException(ErrorMessage.OUT_OF_STOCK);
            }

            product.updateStock(product.getStock() - quantity);
            productRepository.saveAndFlush(product);
        } catch (InterruptedException e) {
            throw new CustomException(ErrorMessage.LOCK_INTERRUPTED);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (IllegalMonitorStateException ignored) {
                }
            }
        }
    }

        // 재고 증가 (분산 락 적용 - 주문 취소 시 사용)
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void increaseStock (Long productId, Long quantity){
            String lockKey = "product:stock:" + productId;
            RLock lock = redissonClient.getLock(lockKey);
            boolean acquired = false;

            try {
                acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, LOCK_TIME_UNIT);

                if (!acquired) {
                    throw new CustomException(ErrorMessage.LOCK_ACQUISITION_FAILED);
                }

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

                product.updateStock(product.getStock() + quantity);
                productRepository.saveAndFlush(product);
            } catch (InterruptedException e) {
                throw new CustomException(ErrorMessage.LOCK_INTERRUPTED);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (IllegalMonitorStateException ignored) {
                }
            }
        }
    }
}

