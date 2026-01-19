package com.example.lineofduty.domain.product;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.dto.request.ProductRequest;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus.ProductStatus status;

    public Product(String name, String description, Long price, Long stock, ApplicationStatus.ProductStatus status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public void updateStock(Long newStock) {
        this.stock = newStock;
        updateStatusBasedOnStock();
    }

    private void updateStatusBasedOnStock() {
        if (this.stock == 0) {
            this.status = ApplicationStatus.ProductStatus.SOLD_OUT;
        } else if (this.stock > 0 && this.status == ApplicationStatus.ProductStatus.SOLD_OUT) {
            this.status = ApplicationStatus.ProductStatus.ON_SALE;
        }
    }

    public void update(ProductRequest request) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getDescription() != null) this.description = request.getDescription();
        if (request.getPrice() != null) this.price = request.getPrice();
        if (request.getStock() != null) {
            updateStock(request.getStock());
        }
    }
}
