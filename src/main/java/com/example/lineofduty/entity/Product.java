package com.example.lineofduty.entity;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.product.dto.request.ProductUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Entity
@Table(name = "products")
@Getter
@Setter
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
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus.ProductStatus status;

   public Product(String name, String description, Integer price, Integer stock, ApplicationStatus.ProductStatus status) {
       this.name = name;
       this.description = description;
       this.price = price;
       this.stock = stock;
       this.status = status;
   }

   public void update(ProductUpdateRequest request) {
       if (request.getName() != null) this.name = request.getName();
       if (request.getDescription() != null) this.description = request.getDescription();
       if (request.getPrice() != null) this.price = request.getPrice();
       if (request.getPrice() != null) this.stock = request.getStock();
   }
}
