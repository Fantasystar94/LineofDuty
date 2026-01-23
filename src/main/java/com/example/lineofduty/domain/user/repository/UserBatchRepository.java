package com.example.lineofduty.domain.user.repository;

import com.example.lineofduty.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
        INSERT INTO users
        (username, email, password, role, resident_number, is_deleted, created_at, modified_at)
        VALUES (?, ?, ?, ?, ?, false, now(), now())
    """;

    public void batchInsert(List<User> users) {

        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                users,
                users.size(),
                (ps, user) -> {
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPassword());
                    ps.setString(4, user.getRole().name());
                    ps.setString(5, user.getResidentNumber());
                }
        );
    }

}
