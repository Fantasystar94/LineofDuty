package com.example.lineofduty.domain.enlistmentApplication.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.example.lineofduty.entity.QEnlistmentApplication.enlistmentApplication;
import static com.example.lineofduty.entity.QUser.user;
import static com.example.lineofduty.entity.QEnlistmentSchedule.enlistmentSchedule;
@Repository
@RequiredArgsConstructor
public class QueryEnlistmentApplicationRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public boolean validateApplication(Long userId, Long scheduleId) {

        jpaQueryFactory.select(enlistmentApplication)
                .from(enlistmentApplication)
                .join(enlistmentApplication.).on(enlistmentApplication.userId.eq(userId))
                .join(enlistmentApplication).on(enlistmentApplication.scheduleId.eq(scheduleId))
                .fetch();

    }

}
