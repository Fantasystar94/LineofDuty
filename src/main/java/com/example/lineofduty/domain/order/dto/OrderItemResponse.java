package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.domain.orderItem.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponse {
    private final Long productId;
    private final Long quantity;
    private final Long price;

    public OrderItemResponse(Long productId, Long quantity, Long price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemResponse from(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getOrderPrice()
        );
    }
}
