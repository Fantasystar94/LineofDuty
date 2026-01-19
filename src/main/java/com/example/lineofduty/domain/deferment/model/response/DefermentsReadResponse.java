package com.example.lineofduty.domain.deferment.model.response;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import com.example.lineofduty.domain.deferment.Deferment;
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
    LocalDate requestedUntil;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public static DefermentsReadResponse from(Deferment deferment) {
        return new DefermentsReadResponse(
                deferment.getId(),
                deferment.getReason(),
                deferment.getStatus(),
                deferment.getRequestedUntil(),
                deferment.getCreatedAt(),
                deferment.getModifiedAt()
        );
    }
}
