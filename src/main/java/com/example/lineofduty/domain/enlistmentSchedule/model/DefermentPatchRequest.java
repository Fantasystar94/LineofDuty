package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefermentPatchRequest {
    private DefermentStatus decisionStatus; // APPROVED / REJECTED
}
