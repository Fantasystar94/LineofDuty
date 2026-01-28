package com.example.lineofduty.domain.enlistmentSchedule.model;

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
    private Long scheduleId;
}
