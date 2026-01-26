package com.example.lineofduty.domain.product.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.product.dto.request.ProductRequest;
import com.example.lineofduty.domain.product.dto.response.ProductResponse;
import com.example.lineofduty.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/products")
    @Operation(summary = "상품 등록", description = "관리자가 새로운 상품을 등록합니다.")
    public ResponseEntity<GlobalResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_CREATE_SUCCESS, response));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "상품 단건 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ONE_SUCCESS, response));
    }

    @GetMapping("/products")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 페이징하여 조회합니다.")
    public ResponseEntity<GlobalResponse> getProDuctList(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향 (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ProductResponse> products = productService.getProductList(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_GET_ALL_SUCCESS, PageResponse.from(products)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/products/{productId}")
    @Operation(summary = "상품 수정", description = "관리자가 상품 정보를 수정합니다.")
public ResponseEntity<GlobalResponse> updateProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(request, productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.PRODUCT_UPDATE_SUCCESS, response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/products/{productId}")
    @Operation(summary = "상품 삭제", description = "관리자가 상품을 삭제합니다.")
    public ResponseEntity<GlobalResponse> deleteProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(GlobalResponse.successNodata(SuccessMessage.PRODUCT_DELETE_SUCCESS));
    }
}
