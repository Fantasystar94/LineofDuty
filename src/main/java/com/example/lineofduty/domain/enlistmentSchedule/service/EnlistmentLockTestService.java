package com.example.lineofduty.domain.enlistmentSchedule.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateResponse;
import com.example.lineofduty.domain.enlistmentSchedule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnlistmentLockTestService {

    private final EnlistmentScheduleRepository scheduleRepository;
    private final EnlistmentApplicationRepository applicationRepository;


    /*
     * 입영 신청 - v2 / 동시성 비관락
     * */
    @Transactional
    public EnlistmentScheduleCreateResponse applyEnlistmentTest(
            Long userId,
            EnlistmentScheduleCreateRequest request
    ) {
        EnlistmentSchedule schedule = getScheduleWithLock(request.getScheduleId());

        if (applicationRepository.existsByUserIdAndApplicationStatusIn(
                userId,
                List.of(ApplicationStatus.REQUESTED, ApplicationStatus.CONFIRMED)
        )) {
            throw new CustomException(ErrorMessage.DUPLICATE_SCHEDULE);
        }

        if (schedule.getRemainingSlots() <= 0) {
            throw new CustomException(ErrorMessage.NO_REMAINING_SLOTS);
        }


        EnlistmentApplication application =
                new EnlistmentApplication(
                        userId,
                        schedule.getId(),
                        schedule.getEnlistmentDate()
                );

        schedule.slotDeduct();

        applicationRepository.save(application);

        return EnlistmentScheduleCreateResponse.from(application);
    }


    /**
     * 입영신청 / 낙관락
     * */
    @Transactional
    public EnlistmentScheduleCreateResponse applyEnlistmentTestOptimisticLock(Long userId, EnlistmentScheduleCreateRequest request) {

        EnlistmentSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));

        if (applicationRepository.existsByUserIdAndApplicationStatusIn(
                userId, List.of(ApplicationStatus.REQUESTED, ApplicationStatus.CONFIRMED))) {
            throw new CustomException(ErrorMessage.DUPLICATE_SCHEDULE);
        }

        EnlistmentApplication application = new EnlistmentApplication(
                userId, schedule.getId(), schedule.getEnlistmentDate()
        );

        schedule.slotDeduct();
        applicationRepository.save(application);

        scheduleRepository.flush(); // ✅ 낙관락 충돌을 여기서 터뜨리기

        return EnlistmentScheduleCreateResponse.from(application);
    }

    private EnlistmentSchedule getScheduleWithLock(Long scheduleId) {
        return scheduleRepository.findByIdWithLock(scheduleId).orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));
    }

}
