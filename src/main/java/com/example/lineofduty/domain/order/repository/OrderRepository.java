package com.example.lineofduty.domain.order.repository;

import com.example.lineofduty.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
