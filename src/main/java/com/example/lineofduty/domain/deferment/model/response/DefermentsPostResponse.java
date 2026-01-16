package com.example.lineofduty.domain.deferment.model.response;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.common.model.enums.DefermentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DefermentsPostResponse {
    private Long defermentId;
    private ApplicationStatus status;
    private LocalDate requestedAt;
}
