package com.example.lineofduty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "enlistment_schedules")
@Getter
@Setter
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
        this.remainingSlots--;
    }

    public void restoreSlot() {
        this.remainingSlots += 1;
    }
}
