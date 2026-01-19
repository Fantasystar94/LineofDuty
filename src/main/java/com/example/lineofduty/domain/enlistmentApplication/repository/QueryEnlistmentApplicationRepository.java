package com.example.lineofduty.domain.enlistmentApplication.repository;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentApplication.model.response.EnlistmentApplicationReadResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.lineofduty.entity.QEnlistmentApplication.enlistmentApplication;
import static com.example.lineofduty.entity.QEnlistmentSchedule.enlistmentSchedule;
@Repository
@RequiredArgsConstructor
public class QueryEnlistmentApplicationRepository {

    private final JPAQueryFactory jpaQueryFactory;


    public List<EnlistmentApplicationReadResponse> getApplicationListWithEnlistmentDate() {

        return jpaQueryFactory
                .select(Projections.constructor(EnlistmentApplicationReadResponse.class,
                        enlistmentApplication.id,
                        enlistmentApplication.enlistmentDate,
                        enlistmentApplication.applicationStatus,
                        enlistmentApplication.createdAt,
                        enlistmentApplication.modifiedAt
                        ))
                .from(enlistmentApplication)
                .leftJoin(enlistmentSchedule)
                .on(enlistmentApplication.scheduleId.eq(enlistmentSchedule.id))
                .where(enlistmentApplication.applicationStatus.eq(ApplicationStatus.PENDING))
                .fetch();
    }

}
