package com.example.lineofduty.domain.product.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.dto.request.ProductRequest;
import com.example.lineofduty.domain.product.dto.response.ProductResponse;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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

        Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getStock(), ApplicationStatus.ProductStatus.ON_SALE);
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
    public Page<ProductResponse> getProductList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

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

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorMessage.PRODUCT_NOT_FOUND);
        }

        productRepository.deleteById(productId);
    }

}

