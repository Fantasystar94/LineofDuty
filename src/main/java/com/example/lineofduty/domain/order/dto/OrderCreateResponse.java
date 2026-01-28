package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.orderItem.OrderItemResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreateResponse {

    private final Long id;
    private final Long userId;
    private final List<OrderItemResponse> orderItemList;
    private final String orderName;
    private final Long totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderCreateResponse(Long id, Long userId, String orderName, List<OrderItem> orderItemList, Long totalPrice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.orderName = orderName;
        this.orderItemList = orderItemList.stream().map(OrderItemResponse::from).toList();
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(),
                order.getUser().getId(),
                order.getOrderName(),
                order.getOrderItemList(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getModifiedAt()
        );
    }
}
