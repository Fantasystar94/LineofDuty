package com.example.lineofduty.domain.dashboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSummaryResponse {

    private Long totalUsers;
    private Long confirmedEnlistments;
    private Long requestedEnlistments;
    private Long totalRemainingSlots;
}