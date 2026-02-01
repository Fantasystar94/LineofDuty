package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class PaymentConfirmResponse {

    private final String userName;
    private final Long paymentId;
    private final String paymentKey;
    private final PaymentStatus status;
    private final String orderNumber;
    private final String orderName;
    private final Long totalPrice;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;

    public PaymentConfirmResponse(String userName, Long paymentId, String paymentKey, PaymentStatus status, String orderNumber, String orderName, Long totalPrice, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.userName = userName;
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.status = status;
        this.orderNumber = orderNumber;
        this.orderName = orderName;
        this.totalPrice = totalPrice;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static PaymentConfirmResponse from(Payment payment) {
        return new PaymentConfirmResponse(
                payment.getOrder().getUser().getUsername(),
                payment.getId(),
                payment.getPaymentKey(),
                payment.getStatus(),
                payment.getOrder().getOrderNumber(),
                payment.getOrder().getOrderName(),
                payment.getTotalPrice(),
                payment.getRequestedAt(),
                payment.getApprovedAt()
        );
    }
}
