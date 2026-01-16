package com.example.lineofduty.domain.product.dto.request;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductUpdateRequest {

    @NotBlank(message = ValidationMessage.MISSING_PRODUCT_NAME)
    private String name;

    private String description;

    @NotNull(message = ValidationMessage.INVALID_PRICE)
    @Min(value = 0, message = ValidationMessage.INVALID_PRICE)
    private Integer price;

    @NotNull(message = ValidationMessage.INVALID_STOCK)
    @Min(value = 0, message = ValidationMessage.INVALID_STOCK)
    private Integer stock;
}
