package com.example.lineofduty.domain.product.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.product.dto.request.ProductCreateRequest;
import com.example.lineofduty.domain.product.dto.request.ProductUpdateRequest;
import com.example.lineofduty.domain.product.dto.response.ProductCreateResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetAllResponse;
import com.example.lineofduty.domain.product.dto.response.ProductGetOneResponse;
import com.example.lineofduty.domain.product.dto.response.ProductUpdateResponse;
import com.example.lineofduty.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<GlobalResponse<ProductGetOneResponse>> getProduct(@PathVariable Long productId) {

        ProductGetOneResponse response = productService.getProduct(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ONE_SUCCESS, response));
    }

    @GetMapping("/products")
    public ResponseEntity<GlobalResponse<PageResponse<ProductGetAllResponse>>> getProDuctList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ProductGetAllResponse> products = productService.getProductList(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ALL_SUCCESS, PageResponse.from(products)));
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<GlobalResponse<ProductUpdateResponse>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest request) {

        ProductUpdateResponse response = productService.updateProduct(request, productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_UPDATE_SUCCESS, response));
    }
}
