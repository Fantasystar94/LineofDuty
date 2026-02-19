package com.example.lineofduty.domain.order.repository;

import com.example.lineofduty.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndIsOrderCompletedFalse(Long userId);

    Optional<Order> findByIdAndIsOrderCompletedFalse(Long orderId);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query(value = "SELECT * FROM orders " +
            "WHERE user_id = :userId " +
            "ORDER BY order_id DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Order> findLastUsingOrder(Long userId);
}