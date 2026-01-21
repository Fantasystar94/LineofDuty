package com.example.lineofduty.domain.dashboard.model;
import com.example.lineofduty.common.model.enums.DefermentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DashboardDefermentsSummaryResponse {
    private DefermentStatus reason;
    private Long count;
}
