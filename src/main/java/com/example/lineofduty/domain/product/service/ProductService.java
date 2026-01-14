package com.example.lineofduty.domain.product.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.dto.request.ProductCreateRequest;
import com.example.lineofduty.domain.product.dto.response.ProductCreateResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetOneResponse;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductCreateResponse createProduct(ProductCreateRequest request) {

        // Product 생성
        Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getStock(), ApplicationStatus.ProductStatus.ON_SALE);

        // 저장
        Product savedProduct = productRepository.save(product);

        return ProductCreateResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductGetOneResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        if (product.isDeleted()) {
            throw new CustomException(ErrorMessage.PRODUCT_NOT_FOUND);
        }

        return ProductGetOneResponse.from(product);
    }
}
