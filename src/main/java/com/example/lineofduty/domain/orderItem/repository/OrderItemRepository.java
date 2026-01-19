package com.example.lineofduty.domain.orderItem.repository;

import com.example.lineofduty.domain.orderItem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
