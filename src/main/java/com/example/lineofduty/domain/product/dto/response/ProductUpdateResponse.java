package com.example.lineofduty.domain.product.dto.response;

import com.example.lineofduty.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductUpdateResponse {

    private Long productId;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductUpdateResponse from(Product product) {
        return new ProductUpdateResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt(),
                product.getModifiedAt()
        );
    }
}
