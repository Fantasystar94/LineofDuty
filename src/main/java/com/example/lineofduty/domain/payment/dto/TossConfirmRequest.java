package com.example.lineofduty.domain.payment.dto;

import lombok.Getter;

@Getter
public class TossConfirmRequest {

    private String paymentKey;
    private String orderId;
    private Long amount;

    public TossConfirmRequest(String paymentKey, String orderId, Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
