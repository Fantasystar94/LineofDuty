package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.domain.payment.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentCreateResponse {

    private final Long paymentId;
    private final Long orderId;
    private final Long totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public PaymentCreateResponse(Long paymentId, Long orderId, Long totalPrice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PaymentCreateResponse from(Payment payment) {
        return new PaymentCreateResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getTotalPrice(),
                payment.getCreatedAt(),
                payment.getModifiedAt()
        );
    }
}
