package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.orderItem.OrderItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreateResponse {

    private final Long id;
    private final Long userId;
    private final List<OrderItemResponse> orderItems;
    private final Long totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderCreateResponse(Long id, Long userId, List<OrderItem> orderItems, Long totalPrice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.orderItems = orderItems.stream().map(OrderItemResponse::from).toList();
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(),
                order.getUser().getId(),
                order.getOrderItems(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getModifiedAt()
        );
    }
}
