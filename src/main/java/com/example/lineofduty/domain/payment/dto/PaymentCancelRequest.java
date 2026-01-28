package com.example.lineofduty.domain.payment.dto;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PaymentCancelRequest {

    @NotBlank(message = ValidationMessage.NOT_BLANK_CANCEL_REASON)
    @Size(min = 1, max = 200)
    String cancelReason;
}
