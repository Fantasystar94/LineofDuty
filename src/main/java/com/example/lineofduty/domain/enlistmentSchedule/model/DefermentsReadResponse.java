package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import com.example.lineofduty.domain.enlistmentSchedule.Deferment;
import com.example.lineofduty.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DefermentsReadResponse {
    Long defermentsId;
    String reason;
    DefermentStatus status;
    LocalDate changedDate;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;
    String username;
    public static DefermentsReadResponse from(Deferment deferment, User user) {
        return new DefermentsReadResponse(
                deferment.getId(),
                deferment.getReason(),
                deferment.getStatus(),
                deferment.getChangedDate(),
                deferment.getCreatedAt(),
                deferment.getModifiedAt(),
                user.getUsername()
        );
    }
}
