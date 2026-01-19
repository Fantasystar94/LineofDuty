package com.example.lineofduty.domain.enlistmentSchedule.repository;
import com.example.lineofduty.domain.enlistmentApplication.model.response.EnlistmentApplicationReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleReadResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentSchedule.enlistmentSchedule;
@Repository
@RequiredArgsConstructor
public class QueryEnlistmentScheduleRepository {

    private final JPAQueryFactory jpaQueryFactory;
    public List<EnlistmentScheduleReadResponse> getEnlistmentListSortBy(LocalDate now) {
        return jpaQueryFactory
                .select(Projections.constructor(EnlistmentScheduleReadResponse.class,
                        enlistmentSchedule.id,
                        enlistmentSchedule.enlistmentDate,
                        enlistmentSchedule.remainingSlots
                        ))
                .from(enlistmentSchedule)
                .where(enlistmentSchedule.remainingSlots.ne(0), enlistmentSchedule.enlistmentDate.gt(now))
                .orderBy(enlistmentSchedule.enlistmentDate.asc())
                .fetch();
    }
}
