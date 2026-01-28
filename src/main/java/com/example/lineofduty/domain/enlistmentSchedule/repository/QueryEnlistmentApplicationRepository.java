package com.example.lineofduty.domain.enlistmentSchedule.repository;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentApplicationReadResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentApplication.enlistmentApplication;
import static com.example.lineofduty.domain.enlistmentSchedule.QEnlistmentSchedule.enlistmentSchedule;
import static com.example.lineofduty.domain.user.QUser.user;

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
                        enlistmentApplication.modifiedAt,
                        JPAExpressions
                                .select(user.username)
                                .from(user)
                                .where(user.id.eq(enlistmentApplication.userId))
                        ))
                .from(enlistmentApplication)
                .leftJoin(enlistmentSchedule)
                .on(enlistmentApplication.scheduleId.eq(enlistmentSchedule.id))
                .where(enlistmentApplication.applicationStatus.eq(ApplicationStatus.REQUESTED))
                .fetch();
    }

    public EnlistmentApplicationReadResponse getApplicationWithUser(Long userId, Long applicationId) {

        return jpaQueryFactory
                .select(Projections.constructor(EnlistmentApplicationReadResponse.class,
                        enlistmentApplication.id,
                        enlistmentApplication.enlistmentDate,
                        enlistmentApplication.applicationStatus,
                        enlistmentApplication.createdAt,
                        enlistmentApplication.modifiedAt,
                        JPAExpressions
                                .select(user.username)
                                .from(user)
                                .where(user.id.eq(enlistmentApplication.userId))
                        ))
                .from(enlistmentApplication)
                .where(
                        enlistmentApplication.id.eq(applicationId),
                        (enlistmentApplication.userId.eq(userId))
                )
                .fetchOne();
    }

}
