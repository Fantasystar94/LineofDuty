package com.example.lineofduty.domain.enlistmentSchedule.service;
import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.common.model.enums.DefermentStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import com.example.lineofduty.domain.enlistmentSchedule.model.*;
import com.example.lineofduty.domain.enlistmentSchedule.repository.DefermentRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentApplicationRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.QueryEnlistmentApplicationRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.QueryEnlistmentScheduleRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.domain.enlistmentSchedule.Deferment;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnlistmentScheduleService {

    private final EnlistmentScheduleRepository scheduleRepository;
    private final QueryEnlistmentScheduleRepository queryscheduleRepository;
    private final EnlistmentApplicationRepository applicationRepository;
    private final QueryEnlistmentApplicationRepository queryEnlistmentApplicationRepository;
    private final UserRepository userRepository;
    private final LocalDate today = LocalDate.now();
    private final DefermentRepository defermentRepository;

    /*
     * 입영 가능 일정 조회
     * */
    @Transactional(readOnly = true)
    public Page<EnlistmentScheduleReadResponse> getEnlistmentList(Pageable pageable) {

        return queryscheduleRepository.getEnlistmentListSortBy(pageable, LocalDate.now());

    }

    /*
     * 입영 가능 일정 조회 - 단건
     * */
    @Transactional(readOnly = true)
    public EnlistmentScheduleReadResponse getEnlistment(Long scheduleId) {

        EnlistmentSchedule schedule = getSchedule(scheduleId);

        return EnlistmentScheduleReadResponse.from(schedule);
    }


    /*
     * 입영 신청 - v2 / 동시성 비관락
     * */
    @Transactional
    public EnlistmentScheduleCreateResponse applyEnlistment(Long userId, EnlistmentScheduleCreateRequest request) {

        User user = getUser(userId);
        //유저엔티티에 입영일을 가지고있다면 쿼리 하나 줄일 수 있음.

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

        //선택한 입영일자가 오늘 이전인지, 슬롯이 아직 남아있는지
        if (!scheduleRepository.checkUserCanApplyToEnlistmentDate(today, request.getScheduleId())) {
            throw new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND);
        }

        //신청 생성
        EnlistmentApplication enlistmentApplication = new EnlistmentApplication(user.getId(),schedule.getId(),schedule.getEnlistmentDate());

        applicationRepository.save(enlistmentApplication);

        //슬롯 차감
        schedule.slotDeduct();

        return EnlistmentScheduleCreateResponse.from(enlistmentApplication);
    }

    /*
     * 입영 신청 목록 조회 - v1 / Authentication 없음
     * */
    @Transactional(readOnly = true)
    public List<EnlistmentApplicationReadResponse> getApplicationList() {

        return queryEnlistmentApplicationRepository.getApplicationListWithEnlistmentDate();

    }

    /*
     * 입영 신청 단건 조회 - v1 / Authentication 없음
     * */
    @Transactional(readOnly = true)
    public EnlistmentApplicationReadResponse getApplication(Long userId, Long applicationId) {

        validApplication(applicationId);

        return queryEnlistmentApplicationRepository.getApplicationWithUser(userId, applicationId);
    }

    /*
     * 입영 신청 취소 - v1
     * */
    @Transactional
    public EnlistmentApplicationReadResponse cancelApplication(Long userId, Long applicationId) {

        getUser(userId);

        EnlistmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 본인 검증
        if (!application.getUserId().equals(userId)) {
            throw new CustomException(ErrorMessage.USER_NOT_FOUND);
        }

        // 3. 상태 검증
        if (application.isRequested())  {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 4. 상태 변경
        application.cancel();

        // 5. 슬롯 복구
        EnlistmentSchedule schedule = scheduleRepository.findById(application.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));

        schedule.restoreSlot();

        return queryEnlistmentApplicationRepository.getApplicationWithUser(userId, applicationId);
    }

    /*
     * 입영 신청 승인 - v1
     * */
    @Transactional
    public EnlistmentApplicationReadResponse approveApplication(Long applicationId) {


        // 1. 신청 조회
        EnlistmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 상태 검증
        if (application.isRequested()) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 3. 상태 변경
        application.confirm();

        return queryEnlistmentApplicationRepository.getApplicationWithUser(application.getUserId(), applicationId);
    }

    /*
     * 입영 신청 일괄 승인
     * */
    @Transactional
    public List<EnlistmentApplicationReadResponse> approveApplicationBulk() {

        List<EnlistmentApplication> applicationList = applicationRepository.findEnlistmentApplicationByApplicationStatus(ApplicationStatus.REQUESTED);

        applicationList.forEach(EnlistmentApplication::confirm);

        List<Long> applicationIds = applicationList.stream()
                .map(EnlistmentApplication::getId)
                .toList();

        return queryEnlistmentApplicationRepository
                .findApplicationsWithUser(applicationIds);

    }

    /*
     * 입영 연기 요청 - v1
     * */
    @Transactional
    public DefermentsReadResponse defermentsSchedule(Long userId, DefermentsPostRequest request) {

        User user = getUser(userId);

        // 1. 신청 조회
        EnlistmentApplication application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 상태 검증
        if (application.isRequested()) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        EnlistmentSchedule schedule = getSchedule(request.getScheduleId());

        // 4. 저장
        Deferment deferment = new Deferment(
                application,
                userId,
                request.getReasonDetail(),
                request.getDefermentStatus(),
                schedule.getEnlistmentDate()
        );
        defermentRepository.save(deferment);

        return DefermentsReadResponse.from(deferment,user);
    }

    /*
     * 입영 연기 다건조회 - v1
     * */
    @Transactional(readOnly = true)
    public Page<DefermentsReadResponse> getDefermentList(Pageable pageable) {

        return defermentRepository.findDefermentList(pageable);
    }

    /*
     * 입영 연기 단건조회 - v1
     * */
    @Transactional(readOnly = true)
    public DefermentsReadResponse getDeferment(Long userId, Long defermentId) {
        User user = getUser(userId);
        Deferment deferment = defermentRepository
                .findByIdAndUserId(defermentId, userId).orElseThrow(
                        ()-> new CustomException(ErrorMessage.DEFERMENT_NOT_FOUND)
                );

        return DefermentsReadResponse.from(deferment, user);
    }

    /*
     * 입영 연기 요청 승인 / 반려 - v1
     * 관리자 전용
     */
    @Transactional
    public EnlistmentApplicationReadResponse processDeferment(Long defermentsId, DefermentPatchRequest request) {

        // 1. 연기 요청 조회
        Deferment deferment = defermentRepository.findWithApplication(defermentsId)
                .orElseThrow(()-> new CustomException(ErrorMessage.DEFERMENT_NOT_FOUND));

        // 2. 신청 조회
        EnlistmentApplication application = deferment.getApplication();

        User user = getUser(deferment.getUserId());

        // 2. 상태 검증
        if (application.isConfirmed()) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 4. 승인 / 반려 처리
        if (request.getDecisionStatus() == DefermentStatus.APPROVED) {

            //바뀔 입영 스케쥴
            EnlistmentSchedule schedule = scheduleRepository
                    .findByEnlistmentDate(deferment.getChangedDate());
            //슬롯 차감
            schedule.slotDeduct();

            //이전 스케쥴
            EnlistmentSchedule preSchedule = scheduleRepository
                    .findById(application.getScheduleId()).orElseThrow(() -> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));
            //슬롯 되돌리기
            preSchedule.restoreSlot();

            application.applyDeferredDate(schedule.getEnlistmentDate());

            application.confirm();

        } else if (request.getDecisionStatus() == DefermentStatus.REJECTED) {

            application.reject();

        } else {

            throw new CustomException(ErrorMessage.INVALID_DEFERMENT_STATUS);

        }

        return queryEnlistmentApplicationRepository.getApplicationWithUser(user.getId(), application.getId());
    }

    @Transactional(readOnly = true)
    public Page<EnlistmentScheduleReadResponse> searchEnlistment(LocalDate startDate, LocalDate endDate, Pageable pageable) {

        return queryscheduleRepository.searchEnlistment(startDate, endDate, pageable);

    }

    @Transactional
    public BulkDefermentProcessResponse processDefermentBulk(DefermentStatus decisionStatus) {

        // 1. 벌크 대상 조회
        List<EnlistmentApplication> lists =
                applicationRepository.findRequestedWithDefermentAndSchedule(
                        ApplicationStatus.REQUESTED
                );

        if (lists.isEmpty()) {
            return new BulkDefermentProcessResponse(0, 0);
        }

        // 2. REJECTED는 즉시 종료 (슬롯/스케줄 로직 없음)
        if (decisionStatus == DefermentStatus.REJECTED) {
            lists.forEach(EnlistmentApplication::reject);
            return new BulkDefermentProcessResponse(lists.size(), lists.size());
        }

        // 3. APPROVED
        // 날짜 기준 정렬 (같은 날짜가 연속되도록)
        lists.sort(Comparator.comparing(
                a -> a.getDeferment().getChangedDate()
        ));

        // 4. 기존 스케줄들 미리 로딩 (ID 기반)
        List<Long> oldScheduleIds = lists.stream()
                .map(EnlistmentApplication::getScheduleId)
                .distinct()
                .toList();

        Map<Long, EnlistmentSchedule> oldScheduleMap =
                scheduleRepository.findAllById(oldScheduleIds)
                        .stream()
                        .collect(Collectors.toMap(
                                EnlistmentSchedule::getId,
                                s -> s
                        ));

        LocalDate currentDate = null;
        EnlistmentSchedule currentNewSchedule = null;
        int countForDate = 0;
        int processedCount = 0;

        for (EnlistmentApplication app : lists) {

            LocalDate changedDate = app.getDeferment().getChangedDate();

            // 날짜가 바뀌는 순간
            if (!changedDate.equals(currentDate)) {

                // 이전 날짜 슬롯 차감 (첫 루프 제외)
                if (currentNewSchedule != null) {
                    currentNewSchedule.bulkDeduct(countForDate);
                }

                // 새 날짜 스케줄 로딩 (날짜당 1번)
                currentDate = changedDate;
                currentNewSchedule =
                        scheduleRepository.findByEnlistmentDate(changedDate);

                countForDate = 0;
            }

            // 기존 스케줄 슬롯 복구
            EnlistmentSchedule oldSchedule =
                    oldScheduleMap.get(app.getScheduleId());
            oldSchedule.restoreSlot();

            // 신청 반영
            app.applyDeferredDate(changedDate);
            app.confirm();

            countForDate++;
            processedCount++;
        }

        // 마지막 날짜 슬롯 차감
        if (currentNewSchedule != null) {
            currentNewSchedule.bulkDeduct(countForDate);
        }

        return new BulkDefermentProcessResponse(lists.size(), processedCount);
    }




    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));
    }

    private void validApplication(Long applicationId) {
        applicationRepository.findById(applicationId).orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));
    }

    private EnlistmentSchedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));
    }

    private EnlistmentSchedule getScheduleWithLock(Long scheduleId) {
        return scheduleRepository.findByIdWithLock(scheduleId).orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));
    }
}
