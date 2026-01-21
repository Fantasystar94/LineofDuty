package com.example.lineofduty.domain.dashboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardScheduleSummaryResponse {
    private Long scheduledId;
    private LocalDate enlistmentDate;
    private Integer capacity;
    private Integer remainingSlots;
    private Double fillRate;
}
