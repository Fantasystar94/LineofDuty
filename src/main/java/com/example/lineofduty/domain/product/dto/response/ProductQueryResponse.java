package com.example.lineofduty.domain.product.dto.response;

import com.example.lineofduty.common.model.enums.ProductStatus;
import com.example.lineofduty.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductQueryResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Long price;
    private final Long stock;
    private final ProductStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ProductQueryResponse from(Product product) {
        return new ProductQueryResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getModifiedAt()
        );
    }
}
