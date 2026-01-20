package com.example.lineofduty.domain.enlistmentSchedule.service;
import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.common.model.enums.DefermentStatus;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.deferment.model.request.DefermentsPostRequest;
import com.example.lineofduty.domain.deferment.model.response.DefermentsReadResponse;
import com.example.lineofduty.domain.deferment.repository.DefermentRepository;
import com.example.lineofduty.domain.enlistmentApplication.model.response.EnlistmentApplicationReadResponse;
import com.example.lineofduty.domain.enlistmentApplication.repository.EnlistmentApplicationRepository;
import com.example.lineofduty.domain.enlistmentApplication.repository.QueryEnlistmentApplicationRepository;
import com.example.lineofduty.domain.enlistmentSchedule.model.request.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleCreateResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.repository.QueryEnlistmentScheduleRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.domain.deferment.Deferment;
import com.example.lineofduty.domain.enlistmentApplication.EnlistmentApplication;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
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
    @Transactional
    public List<EnlistmentScheduleReadResponse> getEnlistmentList() {

        return queryscheduleRepository.getEnlistmentListSortBy(LocalDate.now());

    }

    /*
     * 입영 가능 일정 조회 - 단건
     * */
    @Transactional
    public EnlistmentScheduleReadResponse getEnlistment(Long scheduleId) {

        EnlistmentSchedule schedule = scheduleValidate(scheduleId);

        return EnlistmentScheduleReadResponse.from(schedule);
    }


    /*
     * 입영 신청 - v1 / Authentication 없음
     * */
    @Transactional
    public EnlistmentScheduleCreateResponse applyEnlistment(Long userId, EnlistmentScheduleCreateRequest request) {

        User user = userValidate(userId);
        //유저엔티티에 입영일을 가지고있다면 쿼리 하나 줄일 수 있음.

        //유저가 하나라도 점유하고 있는지 검증
        if (applicationRepository.existsByUserIdAndStatus(userId)) {
            throw new CustomException(ErrorMessage.DUPLICATE_SCHEDULE);
        }

        EnlistmentSchedule schedule = scheduleValidate(request.getScheduleId());

        //신청 이력이 있는지
        if (applicationRepository.existsByUserIdAndScheduleId(userId, request.getScheduleId())) {
            throw new CustomException(ErrorMessage.DUPLICATE_SCHEDULE);
        }

        //선택한 입영일자가 오늘 이전인지, 슬롯이 아직 남아있는지
        if (!scheduleRepository.checkUserCanApplyToEnlistmentDate(today, request.getScheduleId())) {
            throw new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND);
        }

        //신청 저장
        EnlistmentApplication enlistmentApplication = new EnlistmentApplication(ApplicationStatus.PENDING,user.getId(),schedule.getId(),schedule.getEnlistmentDate());

        applicationRepository.save(enlistmentApplication);

        //슬롯 차감
        schedule.slotDeduct();
        scheduleRepository.save(schedule);

        return EnlistmentScheduleCreateResponse.from(enlistmentApplication);
    }

    /*
     * 입영 신청 목록 조회 - v1 / Authentication 없음
     * */
    @Transactional
    public List<EnlistmentApplicationReadResponse> getApplicationList() {

        return queryEnlistmentApplicationRepository.getApplicationListWithEnlistmentDate();

    }

    /*
     * 입영 신청 단건 조회 - v1 / Authentication 없음
     * */
    @Transactional
    public EnlistmentApplicationReadResponse getApplication(Long scheduleId) {
        EnlistmentApplication application = applicationRepository.findById(scheduleId).orElseThrow(()->new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));

        return EnlistmentApplicationReadResponse.from(application);
    }

    /*
     * 입영 신청 취소 - v1 / Authentication 없음
     * */
    @Transactional
    public EnlistmentApplicationReadResponse cancelApplication(Long userId, Long applicationId) {

        userValidate(userId);

        EnlistmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 본인 검증
        if (!application.getUserId().equals(userId)) {
            throw new CustomException(ErrorMessage.USER_NOT_FOUND);
        }

        // 3. 상태 검증
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND);
        }

        // 4. 상태 변경
        application.changeStatus(ApplicationStatus.CANCELED);

        // 5. 슬롯 복구
        EnlistmentSchedule schedule = scheduleRepository.findById(application.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));

        schedule.restoreSlot();

        return EnlistmentApplicationReadResponse.from(application);
    }

    /*
     * 입영 신청 승인 - v1 / Authentication 없음
     * */
    @Transactional
    public EnlistmentApplicationReadResponse approveApplication(Long userId, Long applicationId) {

        userValidate(userId);

        // 1. 신청 조회
        EnlistmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 상태 검증
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 3. 상태 변경
        application.changeStatus(ApplicationStatus.CONFIRMED);

        return EnlistmentApplicationReadResponse.from(application);
    }

    /*
     * 입영 연기 요청 - v1 / Authentication 없음
     * */
    @Transactional
    public EnlistmentApplicationReadResponse defermentsSchedule(Long userId, DefermentsPostRequest request) {

        userValidate(userId);

        // 1. 신청 조회
        EnlistmentApplication application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 상태 검증
        if (application.getApplicationStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 3. 상태 변경
        application.changeStatus(ApplicationStatus.REQUESTED);

        // 4. 저장
        Deferment deferment = new Deferment(
                application.getId(),
                userId,
                request.getReasonDetail(),
                request.getDefermentStatus(),
                request.getRequestedUntil()
        );
        defermentRepository.save(deferment);

        return EnlistmentApplicationReadResponse.from(application);
    }

    /*
     * 입영 연기 다건조회 - v1 / Authentication 없음
     * */
    @Transactional
    public Page<DefermentsReadResponse> getDefermentList(Long userId, Pageable pageable) {

        userValidate(userId);

        Page<Deferment> page = defermentRepository.findAll(pageable);

        return page.map(DefermentsReadResponse::from);
    }

    /*
     * 입영 연기 단건조회 - v1 / Authentication 없음
     * */
    @Transactional
    public DefermentsReadResponse getDeferment(Long userId, Long defermentId) {

        Deferment deferment = defermentRepository
                .findByIdAndUserId(defermentId, userId).orElseThrow(
                        ()-> new CustomException(ErrorMessage.DEFERMENT_NOT_FOUND)
                );

        return DefermentsReadResponse.from(deferment);
    }

    /*
     * 입영 연기 요청 승인 / 반려 - v1
     * 관리자 전용
     */
    @Transactional
    public EnlistmentApplicationReadResponse processDeferment(
            Long adminId,
            Long applicationId,
            DefermentStatus decisionStatus   // APPROVED / REJECTED
    ) {

        // 0. 관리자 검증 (v1 스타일)
        adminValidate(adminId);

        // 1. 신청 조회
        EnlistmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.APPLICATION_NOT_FOUND));

        // 2. 상태 검증
        if (application.getApplicationStatus() != ApplicationStatus.REQUESTED) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }

        // 3. 연기 요청 조회
        Deferment deferment = defermentRepository.findByApplicationId(applicationId)
                .orElseThrow(()-> new CustomException(ErrorMessage.DEFERMENT_NOT_FOUND));

        // 4. 승인 / 반려 처리
        if (decisionStatus == DefermentStatus.APPROVED) {
            application.changeStatus(ApplicationStatus.DEFERRED);
            deferment.approve();
        } else if (decisionStatus == DefermentStatus.REJECTED) {
            deferment.reject();
        } else {
            throw new CustomException(ErrorMessage.INVALID_DEFERMENT_STATUS);
        }

        return EnlistmentApplicationReadResponse.from(application);
    }


    private User userValidate(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));
    }

    private User adminValidate(Long adminId) {
        return userRepository.findByIdAndRole(adminId, Role.ROLE_ADMIN).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));
    }

    private EnlistmentSchedule scheduleValidate(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(()-> new CustomException(ErrorMessage.SCHEDULE_NOT_FOUND));
    }
}
