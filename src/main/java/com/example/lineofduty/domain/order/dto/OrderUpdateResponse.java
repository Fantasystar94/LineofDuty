package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.entity.OrderItem;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderUpdateResponse {
    private final Long orderItemId;
    private final Long productId;
    private final Long quantity;
    private final Long price;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderUpdateResponse(Long orderItemId, Long productId, Long quantity, Long price, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderUpdateResponse from(OrderItem orderItem) {

        return new OrderUpdateResponse(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                (long) orderItem.getProduct().getPrice(),
                orderItem.getCreatedAt(),
                orderItem.getModifiedAt()
        );
    }
}
