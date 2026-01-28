package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.orderItem.OrderItemResponse;
import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class PaymentGetResponse {

    private final String paymentKey;
    private final String orderName;
    private final String orderNumber;
    private final List<OrderItemResponse> orderItemList;
    private final Long totalPrice;
    private final PaymentStatus status;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;

    public PaymentGetResponse(String paymentKey, String orderName, String orderNumber, List<OrderItemResponse> orderItemList, PaymentStatus status, Long totalPrice, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.orderItemList = orderItemList;
        this.status = status;
        this.totalPrice = totalPrice;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static PaymentGetResponse from(Payment payment) {
        return new PaymentGetResponse(
                payment.getPaymentKey(),
                payment.getOrder().getOrderName(),
                payment.getOrder().getOrderNumber(),
                payment.getOrder()
                        .getOrderItemList().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                payment.getStatus(),
                payment.getTotalPrice(),
                payment.getRequestedAt(),
                payment.getApprovedAt()
        );
    }
}