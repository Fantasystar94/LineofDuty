package com.example.lineofduty.domain.product.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.product.dto.request.ProductCreateRequest;
import com.example.lineofduty.domain.product.dto.response.ProductCreateResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetOneResponse;
import com.example.lineofduty.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping("/admin/products")
    public ResponseEntity<GlobalResponse<ProductCreateResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {

        ProductCreateResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_CREATE_SUCCESS, response));
    }

    @GetMapping("/products/{productId}")
    private ResponseEntity<GlobalResponse<ProductGetOneResponse>> getProduct(@PathVariable Long productId) {

        ProductGetOneResponse response = productService.getProduct(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ONE_SUCCESS, response));
    }
}
