package com.example.lineofduty.domain.order.dto;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderCreateRequest {

    @NotNull(message = ValidationMessage.NOT_BLANK_PRODUCT_ID)
    private Long productId;
    @NotNull(message = ValidationMessage.NOT_BLANK_QUANTITY)
    private Long quantity;
}
