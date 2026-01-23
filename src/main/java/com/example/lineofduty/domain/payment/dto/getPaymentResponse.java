package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;

import java.time.OffsetDateTime;

public class getPaymentResponse {

    private final Long paymentId;
    private final PaymentStatus status;
    private final String orderId;
    private final String paymentKey;
    private final Long amount;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;

    public getPaymentResponse(Long paymentId, PaymentStatus status, String orderId, String paymentKey, Long amount, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.paymentId = paymentId;
        this.status = status;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static getPaymentResponse from(Payment payment) {
        return new getPaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getRequestedAt(),
                payment.getApprovedAt()
        );
    }
}