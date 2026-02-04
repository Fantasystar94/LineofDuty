package com.example.lineofduty.domain.product.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.fileUpload.FileUploadResponse;
import com.example.lineofduty.domain.fileUpload.FileUploadService;
import com.example.lineofduty.domain.product.dto.request.ProductRequest;
import com.example.lineofduty.domain.product.dto.response.ProductResponse;
import com.example.lineofduty.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileUploadService fileUploadService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/products")
    public ResponseEntity<GlobalResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_CREATE_SUCCESS, response));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<GlobalResponse> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ONE_SUCCESS, response));
    }

    @GetMapping("/products")
    public ResponseEntity<GlobalResponse> getProDuctList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction, @RequestParam(required = false) String keyword) {
        Page<ProductResponse> products = productService.getProductList(page, size, sort, direction, keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ALL_SUCCESS, PageResponse.from(products)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<GlobalResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(request, productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_UPDATE_SUCCESS, response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws IOException {

        FileUploadResponse fileResponse = fileUploadService.fileUpload(file);
        productService.updateProductImage(productId, fileResponse.getUrl());

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.PRODUCT_UPDATE_SUCCESS, fileResponse));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<GlobalResponse> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(GlobalResponse.successNodata(SuccessMessage.PRODUCT_DELETE_SUCCESS));
    }
}
