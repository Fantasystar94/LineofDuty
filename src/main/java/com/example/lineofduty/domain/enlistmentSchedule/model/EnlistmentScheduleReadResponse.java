package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnlistmentScheduleReadResponse {

    private Long scheduleId;
    private LocalDate enlistmentDate;
    private Integer remainingSlots;

    public static EnlistmentScheduleReadResponse from(EnlistmentSchedule enlistmentSchedule) {
        return new EnlistmentScheduleReadResponse(
                enlistmentSchedule.getId(),
            enlistmentSchedule.getEnlistmentDate(),
            enlistmentSchedule.getRemainingSlots()
        );
    }
}
