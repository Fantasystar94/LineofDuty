package com.example.lineofduty.domain.enlistmentSchedule.repository;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleReadResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentSchedule.enlistmentSchedule;
@Repository
@RequiredArgsConstructor
public class QueryEnlistmentScheduleRepository {

    private final JPAQueryFactory jpaQueryFactory;
    public List<EnlistmentScheduleReadResponse> getEnlistmentListSortBy(Pageable pageable, LocalDate now) {
        return jpaQueryFactory
                .select(Projections.constructor(EnlistmentScheduleReadResponse.class,
                        enlistmentSchedule.id,
                        enlistmentSchedule.enlistmentDate,
                        enlistmentSchedule.remainingSlots
                        ))
                .from(enlistmentSchedule)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(enlistmentSchedule.remainingSlots.ne(0), enlistmentSchedule.enlistmentDate.gt(now))
                .orderBy(enlistmentSchedule.enlistmentDate.asc())
                .fetch();
    }

    public List<EnlistmentScheduleReadResponse> searchEnlistment(LocalDate startDate, LocalDate endDate, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (startDate != null) {
            builder.and(enlistmentSchedule.enlistmentDate.goe(startDate));
        }

        if (endDate != null) {
            builder.and(enlistmentSchedule.enlistmentDate.loe(endDate));
        }

        return jpaQueryFactory
                .select(Projections.constructor(EnlistmentScheduleReadResponse.class,
                        enlistmentSchedule.id,
                        enlistmentSchedule.enlistmentDate,
                        enlistmentSchedule.remainingSlots
                        ))
                .from(enlistmentSchedule)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
