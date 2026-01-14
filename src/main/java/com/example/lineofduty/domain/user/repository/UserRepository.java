package com.example.lineofduty.domain.user.repository;

import com.example.lineofduty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
