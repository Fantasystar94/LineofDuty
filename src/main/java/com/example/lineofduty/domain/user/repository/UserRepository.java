package com.example.lineofduty.domain.user.repository;

import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByResidentNumber(String residentNumber);

    Optional<User> findByIdAndRole(Long id, Role role);

}
