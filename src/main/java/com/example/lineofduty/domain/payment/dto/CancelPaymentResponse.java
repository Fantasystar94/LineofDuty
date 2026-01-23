package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;
import lombok.Getter;

@Getter
public class CancelPaymentResponse {

    private final Long paymentId;
    private final PaymentStatus paymentStatus;
    private final Long amount;
    private final String paymentKey;

    public CancelPaymentResponse(Long paymentId, PaymentStatus paymentStatus, Long amount, String paymentKey) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }

    public static CancelPaymentResponse from(Payment payment) {
        return new CancelPaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getPaymentKey()
        );
    }
}
