package com.example.lineofduty.domain.product.dto.response;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ProductResponse {

    private Long productId;
    private String name;
    private String description;
    private Long price;
    private Long stock;
    private ApplicationStatus.ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
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
