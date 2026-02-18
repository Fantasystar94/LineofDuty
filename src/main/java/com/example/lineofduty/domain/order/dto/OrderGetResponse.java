package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.orderItem.OrderItemResponse;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderGetResponse {

    private final Long userId;
    private final String userName;
    private final Long orderId;
    private final String orderNumber;
    private final List<OrderItemResponse> orderItemList;
    private final Long totalPrice;
    private final Boolean isOrderCompleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderGetResponse(Long userId, String userName, Long orderId, String orderNumber, List<OrderItemResponse> orderItemResponseList, Long totalPrice, Boolean isOrderCompleted, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userId = userId;
        this.userName = userName;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderItemList = orderItemResponseList;
        this.totalPrice = totalPrice;
        this.isOrderCompleted = isOrderCompleted;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderGetResponse from(Order order) {
        return new OrderGetResponse(
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getId(),
                order.getOrderNumber(),
                order.getOrderItemList().stream().map(OrderItemResponse::from).toList(),
                order.getTotalPrice(),
                order.isOrderCompleted(),
                order.getCreatedAt(),
                order.getModifiedAt()
        );
    }
}