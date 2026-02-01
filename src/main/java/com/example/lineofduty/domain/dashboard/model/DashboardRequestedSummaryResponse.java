package com.example.lineofduty.domain.dashboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRequestedSummaryResponse {

    private Long requestedEnlistments;
    private Long confirmedEnlistments;

}
