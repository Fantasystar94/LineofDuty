package com.example.lineofduty.domain.enlistmentSchedule.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentApplication.repository.EnlistmentApplicationRepository;
import com.example.lineofduty.domain.enlistmentSchedule.model.request.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.repository.QueryEnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.EnlistmentApplication;
import com.example.lineofduty.entity.EnlistmentSchedule;
import com.example.lineofduty.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnlistmentScheduleService {

    private final EnlistmentScheduleRepository scheduleRepository;
    private final QueryEnlistmentScheduleRepository queryscheduleRepository;
    private final EnlistmentApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    /*
     * 입영 가능 일정 조회
     * */
    public List<EnlistmentScheduleReadResponse> getEnlistmentList(Long userId) {

        userValidate(userId);

        return queryscheduleRepository.getEnlistmentListSortBy(LocalDate.now());

    }

    public EnlistmentScheduleReadResponse getEnlistment(Long userId, Long scheduleId) {

        userValidate(userId);

        EnlistmentSchedule schedule = scheduleRepository
                .findById(scheduleId)
                .orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));

        return EnlistmentScheduleReadResponse.from(schedule);
    }

    public EnlistmentScheduleReadResponse applyEnlistment(Long userId, EnlistmentScheduleCreateRequest request) {

        User user = userValidate(userId);

        EnlistmentSchedule schedule = scheduleRepository
                .findById(request.getScheduleId())
                .orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));


        if (applicationRepository.existsByUserId(userId) && applicationRepository.existsByScheduleId(request.getScheduleId())) {
            throw new CustomException(ErrorMessage.DUPLICATE_SCHEDULE);
        }

        EnlistmentApplication enlistmentApplication = new EnlistmentApplication(ApplicationStatus.PENDING,user.getId(),schedule.getId());

        applicationRepository.save(enlistmentApplication);

        return null;
    }

    private User userValidate(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));
    }

}
