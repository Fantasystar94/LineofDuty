package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentConfirmRequest {

    @NotNull(message = ValidationMessage.NOT_BLANK_ORDER_NUMBER)
    private String orderNumber;
}