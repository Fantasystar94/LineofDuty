package com.example.lineofduty.domain.product.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.dto.request.ProductCreateRequest;
import com.example.lineofduty.domain.product.dto.request.ProductUpdateRequest;
import com.example.lineofduty.domain.product.dto.response.ProductCreateResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetAllResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetOneResponse;
import com.example.lineofduty.domain.product.dto.response.ProductUpdateResponse;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Product;
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
    public ProductCreateResponse createProduct(ProductCreateRequest request) {
        Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getStock(), ApplicationStatus.ProductStatus.ON_SALE);
        Product savedProduct = productRepository.save(product);

        return ProductCreateResponse.from(savedProduct);
    }

    // 상품 단건 조회
    @Transactional(readOnly = true)
    public ProductGetOneResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        if (product.isDeleted()) {
            throw new CustomException(ErrorMessage.PRODUCT_NOT_FOUND);
        }

        return ProductGetOneResponse.from(product);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ProductGetAllResponse> getProductList(Pageable pageable) {
        Page<Product> products = productRepository.findAllByIsDeletedFalse(pageable);

        return products.map(ProductGetAllResponse::from);
    }

    // 상품 수정
    @Transactional
    public ProductUpdateResponse updateProduct(ProductUpdateRequest request, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        if (product.isDeleted()) {
            throw new CustomException((ErrorMessage.PRODUCT_NOT_FOUND));
        }
        product.update(request);
        productRepository.saveAndFlush(product);

        return ProductUpdateResponse.from(product);
    }

}

