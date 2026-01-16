package com.example.lineofduty.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderUpdateRequest {

    private Long productId;
    private Long quantity;
}
