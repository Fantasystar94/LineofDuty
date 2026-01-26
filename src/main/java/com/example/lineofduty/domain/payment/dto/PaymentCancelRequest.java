package com.example.lineofduty.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PaymentCancelRequest {

    @NotBlank
    @Size(min = 1, max = 200)
    String cancelReason;
}
