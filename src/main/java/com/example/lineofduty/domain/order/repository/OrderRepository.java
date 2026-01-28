package com.example.lineofduty.domain.order.repository;

import com.example.lineofduty.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndStatusTrue(Long userId);

    Optional<Order> findByIdAndStatusTrue(Long orderId);

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);
}
