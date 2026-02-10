package com.example.lineofduty.domain.enlistmentSchedule;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enlistment_schedules",
    indexes = {
        @Index(
                name = "idx_schedule_date",
                columnList = "enlistment_date"
        )
    }
)
@Getter
@NoArgsConstructor
public class EnlistmentSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "enlistment_date", nullable = false)
    private LocalDate enlistmentDate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "remaining_slots", nullable = false)
    private Integer remainingSlots;

    public void slotDeduct() {
        if (remainingSlots <= 0) {
            throw new CustomException(ErrorMessage.NO_REMAINING_SLOTS);
        }
        this.remainingSlots--;
    }

    public EnlistmentSchedule(LocalDate enlistmentDate) {
        this.enlistmentDate = enlistmentDate;
        this.capacity = 100;
        this.remainingSlots = 100;
    }

    public void bulkDeduct(int count) {
        this.remainingSlots = this.remainingSlots - count;
    }

    public void restoreSlot() {
        this.remainingSlots += 1;
    }


    public void setRemainingSlots(int slots) {
        this.remainingSlots = slots;
    }
}
