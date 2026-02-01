package com.example.lineofduty.domain.orderItem;

import lombok.Getter;

@Getter
public class OrderItemResponse {
    private final Long orderItemId;
    private final Long productId;
    private final String productName;
    private final Long price;
    private final Long quantity;

    public OrderItemResponse(Long orderItemId, Long productId, String productName, Long price, Long quantity) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItemResponse from(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getOrderPrice(),
                orderItem.getQuantity()
        );
    }
}
