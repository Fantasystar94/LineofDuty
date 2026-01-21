package com.example.lineofduty.domain.order.repository;

import com.example.lineofduty.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderByUserIdAndStatusTrue(Long userId);

    Optional<Order> findByIdAndStatusTrue(Long orderId);
}
