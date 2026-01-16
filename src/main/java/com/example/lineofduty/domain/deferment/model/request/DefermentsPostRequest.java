package com.example.lineofduty.domain.deferment.model.request;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DefermentsPostRequest {
    private Long applicationId;
    private DefermentStatus defermentStatus;
    private String reasonDetail;
    private LocalDate requestedUntil;
}
