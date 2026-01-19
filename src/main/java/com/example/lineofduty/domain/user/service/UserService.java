package com.example.lineofduty.domain.user.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.user.dto.UserUpdateRequest;
import com.example.lineofduty.domain.user.dto.UserAdminResponse;
import com.example.lineofduty.domain.user.dto.UserResponse;
import com.example.lineofduty.domain.user.dto.UserWithdrawResponse;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 내 정보 조회
    public UserResponse getMyProfile(Long userId) {
        return new UserResponse(findUserById(userId));
    }

    // 2. 내 정보 수정
    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = findUserById(userId);

        if (StringUtils.hasText(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
            }
        }

        String newPassword = user.getPassword();
        if (StringUtils.hasText(request.getPassword())) {
            newPassword = passwordEncoder.encode(request.getPassword());
        }

        user.updateProfile(request.getEmail(), request.getUsername(), newPassword);

        return new UserResponse(user);
    }

    // 3. 회원 탈퇴
    @Transactional
    public void withdrawUser(Long userId) {
        User user = findUserById(userId);
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }
        user.updateIsDeleted(); // BaseEntity 메서드
    }

    // ------------------ [관리자] ------------------

    // 4. 회원 전체 조회
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserAdminResponse::new)
                .collect(Collectors.toList());
    }

    // 5. 상세 조회
    public UserAdminResponse getUserById(Long userId) {
        return new UserAdminResponse(findUserById(userId));
    }

    // 6. 관리자 본인 탈퇴
    @Transactional
    public UserWithdrawResponse withdrawAdmin(Long adminId) {
        User user = findUserById(adminId);
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }
        user.updateIsDeleted(); // BaseEntity 메서드

        return new UserWithdrawResponse(user.getId(), true, LocalDateTime.now());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));
    }
}