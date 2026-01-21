package com.example.lineofduty.domain.dashboard;
import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.dashboard.model.DashboardDefermentsSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardPendingSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardScheduleSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardSummaryResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final QueryDashboardRepository queryDashboardRepository;

    /**
     * 전체 요약
     * */
    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(UserDetail userDetail) {

        validateAdmin(userDetail.getUser().getId());

        return queryDashboardRepository.summary();
    }

    /**
     * 이번 주 입영일정 요약
     * */
    @Transactional(readOnly = true)
    public List<DashboardScheduleSummaryResponse> summaryScheduleOfThisWeek(UserDetail userDetail) {

        validateAdmin(userDetail.getUser().getId());

        LocalDate today = LocalDate.now();

        return queryDashboardRepository.summaryScheduleOfThisWeek(today);
    }

    /**
     * 입영 요청, 연기 요청 요약
     * */
    @Transactional(readOnly = true)
    public DashboardPendingSummaryResponse summaryPending(UserDetail userDetail) {

        validateAdmin(userDetail.getUser().getId());

        return queryDashboardRepository.summaryPending();
    }

    /**
     * 연기 요청 사유 요약
     * */
    @Transactional(readOnly = true)
    public List<DashboardDefermentsSummaryResponse> summaryDeferments(UserDetail userDetail) {

        validateAdmin(userDetail.getUser().getId());

        return queryDashboardRepository.defermentsSummary();
    }

    private void validateAdmin(Long adminId) {
        userRepository.findByIdAndRole(adminId, Role.ROLE_ADMIN).orElseThrow(()-> new CustomException(ErrorMessage.ACCESS_DENIED));
    }
}
