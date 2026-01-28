package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentCreateRequest {

    @NotNull(message = ValidationMessage.NOT_SELECTED_ORDER_ID)
    private Long orderId;

    @NotNull(message = ValidationMessage.NOT_BLANK_PAYMENT_KEY)
    private String paymentKey;
}
