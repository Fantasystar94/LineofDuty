package com.example.lineofduty.domain.enlistmentSchedule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BulkDefermentProcessResponse {

    private int targetCount;
    private int processedCount;
}
