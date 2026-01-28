package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class PaymentConfirmResponse {

    private final Long paymentId;
    private final String paymentKey;
    private final String status;
    private final Long amount;
    private final String orderId;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;

    public PaymentConfirmResponse(Long paymentId, String paymentKey, String status, long amount, String orderId, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static PaymentConfirmResponse from(Payment payment) {
        return new PaymentConfirmResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getStatus().toString(),
                payment.getAmount(),
                payment.getOrderId(),
                payment.getRequestedAt(),
                payment.getApprovedAt()
        );
    }
}
