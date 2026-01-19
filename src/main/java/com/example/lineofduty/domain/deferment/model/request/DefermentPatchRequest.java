package com.example.lineofduty.domain.deferment.model.request;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefermentPatchRequest {
    private DefermentStatus decisionStatus; // APPROVED / REJECTED
}
