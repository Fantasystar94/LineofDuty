package com.example.lineofduty.domain.product.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.product.dto.request.ProductCreateRequest;
import com.example.lineofduty.domain.product.dto.response.ProductCreateResponse;
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
    public ResponseEntity<GlobalResponse<ProductCreateResponse>> createProductTest(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductCreateResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_CREATE_SUCCESS, response));
    }
}
