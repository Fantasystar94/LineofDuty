package com.example.lineofduty.domain.dashboard;
import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.dashboard.model.DashboardDefermentsSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardPendingSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardScheduleSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardSummaryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentSchedule.enlistmentSchedule;
import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentApplication.enlistmentApplication;
import static com.example.lineofduty.domain.user.QUser.user;
import static com.example.lineofduty.domain.enlistmentSchedule.QDeferment.deferment;

@Repository
@RequiredArgsConstructor
public class QueryDashboardRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public DashboardSummaryResponse summary() {

        /*
        * 		"totalUsers": 12340, //가입된 토탈 유저
		"confirmedEnlistments": 8120, //승인된 입영 요청
		"pendingApplications": 320, //요청대기중인 입영 요청
		"totalRemainingSlots": 48 //남아있는 총 입영가능 슬롯
        * */
        return jpaQueryFactory
                .select(
                        Projections.constructor(DashboardSummaryResponse.class,
                                JPAExpressions
                                        .select(user.count())
                                        .from(user),
                                JPAExpressions
                                        .select(enlistmentApplication.count())
                                        .from(enlistmentApplication)
                                        .where(enlistmentApplication.applicationStatus.eq(ApplicationStatus.CONFIRMED)),
                                JPAExpressions
                                        .select(enlistmentApplication.count())
                                        .from(enlistmentApplication)
                                        .where(enlistmentApplication.applicationStatus.eq(ApplicationStatus.PENDING)),
                                JPAExpressions
                                        .select(enlistmentSchedule.remainingSlots.sum().longValue())
                                        .from(enlistmentSchedule)
                                )
                        )
                .from(user)
                .limit(1)
                .fetchOne();

    }

    public List<DashboardScheduleSummaryResponse> summaryScheduleOfThisWeek(LocalDate today) {

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));


            /*
            * "scheduleId": 2,
			"enlistmentDate": "2025-03-22",
			"capacity": 100,
			"remainingSlots": 10,
			"fillRate": 90
            * */
            return jpaQueryFactory
                    .select(Projections.constructor(DashboardScheduleSummaryResponse.class,
                            enlistmentSchedule.id,
                            enlistmentSchedule.enlistmentDate,
                            enlistmentSchedule.capacity,
                            enlistmentSchedule.remainingSlots,
                            enlistmentSchedule.capacity
                                    .subtract(enlistmentSchedule.remainingSlots)
                                    .doubleValue()
                                    .divide(enlistmentSchedule.capacity.doubleValue())
                                    .multiply(100.0)
                            ))
                    .from(enlistmentSchedule)
                    .where(enlistmentSchedule.enlistmentDate.goe(startOfWeek).and(enlistmentSchedule.enlistmentDate.loe(lastOfWeek)))
                    .fetch();


    }

    public DashboardPendingSummaryResponse summaryPending() {

        NumberExpression<Long> pendingEnlistments =
                Expressions.cases()
                        .when(enlistmentApplication.applicationStatus.eq(ApplicationStatus.PENDING))
                        .then(1L)
                        .otherwise(0L)
                        .sum();

        NumberExpression<Long> pendingDeferments =
                Expressions.cases()
                        .when(enlistmentApplication.applicationStatus.eq(ApplicationStatus.DEFERRED))
                        .then(1L)
                        .otherwise(0L)
                        .sum();

        return jpaQueryFactory
                .select(Projections.constructor(DashboardPendingSummaryResponse.class,
                        pendingEnlistments,
                        pendingDeferments
                        ))
                .from(enlistmentApplication)
                .fetchOne();
    }

    public List<DashboardDefermentsSummaryResponse> defermentsSummary() {

        return jpaQueryFactory
                .select(Projections.constructor(DashboardDefermentsSummaryResponse.class,
                        deferment.status,
                        deferment.count()
                        ))
                .from(deferment)
                .groupBy(deferment.status)
                .fetch();
    }
}
