package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;

import java.time.OffsetDateTime;

public class PaymentGetResponse {

    private final PaymentStatus status;
    private final String orderId;
    private final String paymentKey;
    private final Long amount;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;

    public PaymentGetResponse(PaymentStatus status, String orderId, String paymentKey, Long amount, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.status = status;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static PaymentGetResponse from(Payment payment) {
        return new PaymentGetResponse(
                payment.getStatus(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getRequestedAt(),
                payment.getApprovedAt()
        );
    }
}