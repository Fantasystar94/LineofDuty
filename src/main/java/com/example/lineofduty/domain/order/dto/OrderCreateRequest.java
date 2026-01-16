package com.example.lineofduty.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderCreateRequest {

    //    @NotBlank(message = "productId는 필수 입력 요소입니다.")
    private Long productId;
    //    @NotBlank(message = "quantity는 필수 입력 요소입니다.")
    private Long quantity;
}
