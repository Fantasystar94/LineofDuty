package com.example.lineofduty.domain.log;

import com.example.lineofduty.domain.user.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class LogRepositoryImpl implements LogRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SystemLog> searchSystemLogsByUsername(String username, Pageable pageable) {
        QSystemLog systemLog = QSystemLog.systemLog;
        QUser user = QUser.user;

        List<SystemLog> content = queryFactory
                .selectFrom(systemLog)
                .leftJoin(user).on(systemLog.userId.eq(user.id))
                .where(usernameContains(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(systemLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(systemLog.count())
                .from(systemLog)
                .leftJoin(user).on(systemLog.userId.eq(user.id))
                .where(usernameContains(username));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression usernameContains(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return QUser.user.username.containsIgnoreCase(username);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends SystemLog> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends SystemLog> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<SystemLog> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public SystemLog getOne(Long aLong) {
        return null;
    }

    @Override
    public SystemLog getById(Long aLong) {
        return null;
    }

    @Override
    public SystemLog getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends SystemLog> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SystemLog> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends SystemLog> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends SystemLog> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends SystemLog> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SystemLog> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SystemLog, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends SystemLog> S save(S entity) {
        return null;
    }

    @Override
    public <S extends SystemLog> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<SystemLog> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<SystemLog> findAll() {
        return List.of();
    }

    @Override
    public List<SystemLog> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(SystemLog entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends SystemLog> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<SystemLog> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<SystemLog> findAll(Pageable pageable) {
        return null;
    }
}
