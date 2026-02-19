package com.example.lineofduty.domain.payment.dto;

import lombok.Getter;

@Getter
public class TossCancelRequest {

    private String cancelReason;

    public TossCancelRequest(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
