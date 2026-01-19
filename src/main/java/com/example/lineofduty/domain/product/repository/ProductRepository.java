package com.example.lineofduty.domain.product.repository;

import com.example.lineofduty.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
