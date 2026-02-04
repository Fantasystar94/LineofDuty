package com.example.lineofduty.domain.dashboard;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.dashboard.model.DashboardDefermentsSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardRequestedSummaryResponse;
import com.example.lineofduty.domain.dashboard.model.DashboardSummaryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
		"requestedEnlistments": 320, //요청대기중인 입영 요청
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
                                        .where(enlistmentApplication.applicationStatus.eq(ApplicationStatus.REQUESTED)),
                                JPAExpressions
                                        .select(enlistmentSchedule.remainingSlots.sum().longValue())
                                        .from(enlistmentSchedule)
                                )
                        )
                .from(user)
                .limit(1)
                .fetchOne();

    }

    public DashboardRequestedSummaryResponse summaryRequest() {

        NumberExpression<Long> requestedEnlistments =
                Expressions.cases()
                        .when(enlistmentApplication.applicationStatus.eq(ApplicationStatus.REQUESTED))
                        .then(1L)
                        .otherwise(0L)
                        .sum();

        NumberExpression<Long> confirmedEnlistments =
                Expressions.cases()
                        .when(enlistmentApplication.applicationStatus.eq(ApplicationStatus.CONFIRMED))
                        .then(1L)
                        .otherwise(0L)
                        .sum();

        return jpaQueryFactory
                .select(Projections.constructor(DashboardRequestedSummaryResponse.class,
                        requestedEnlistments,
                        confirmedEnlistments
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
