package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.entity.OrderItem;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderItemGetResponse {
    private final Long orderId;
    private final Long productId;
    private final Long quantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderItemGetResponse(Long orderId, Long productId, Long quantity, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderItemGetResponse from(Long orderId, OrderItem orderItem) {
        return new OrderItemGetResponse(
                orderId,
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getCreatedAt(),
                orderItem.getModifiedAt()
        );
    }
}
