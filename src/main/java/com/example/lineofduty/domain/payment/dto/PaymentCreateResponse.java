package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentCreateResponse {

    private final Long paymentId;
    private final String paymentKey;
    private final String orderName;
    private final String orderNumber;
    private final Long totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public PaymentCreateResponse(Long paymentId, String paymentKey, String orderName, String orderNumber, Long totalPrice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PaymentCreateResponse from(Payment payment) {

        return new PaymentCreateResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getOrder().getOrderName(),
                payment.getOrder().getOrderNumber(),
                payment.getTotalPrice(),
                payment.getCreatedAt(),
                payment.getModifiedAt()
        );
    }
}
