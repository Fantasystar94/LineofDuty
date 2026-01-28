package com.example.lineofduty.domain.user.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentApplicationRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.dto.UserAdminResponse;
import com.example.lineofduty.domain.user.dto.UserResponse;
import com.example.lineofduty.domain.user.dto.UserUpdateRequest;
import com.example.lineofduty.domain.user.dto.UserWithdrawResponse;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnlistmentApplicationRepository enlistmentApplicationRepository;

    // userId로 조회
    private User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        // 탈퇴한 유저 조회 불가
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        return user;
    }

    // 내 정보 조회
    public UserResponse getMyProfile(Long userId) {
        User user = findUserById(userId);
        return new UserResponse(user);
    }

    // 내 정보 수정
    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = findUserById(userId);

        // 이메일 중복 검사 (이메일 변경시에만)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
            }
        }

        // 비밀번호 암호화 (입력된 경우에만)
        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        user.updateProfile(request.getEmail(), request.getUsername(), encodedPassword);

        return new UserResponse(user);
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawUser(Long userId) {
        User user = findUserById(userId);
        user.withdrawUser();
    }

    // ------------------ [관리자] ------------------

    // 회원 전체 조회
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserAdminResponse response = new UserAdminResponse(user);
                    fillEnlistmentInfo(response, user.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // 특정 회원 상세 조회 (탈퇴한 유저도 확인 가능)
    public UserAdminResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        UserAdminResponse response = new UserAdminResponse(user);

        fillEnlistmentInfo(response, user.getId());

        return response;
    }

    // 관리자 본인 탈퇴
    @Transactional
    public UserWithdrawResponse withdrawAdmin(Long adminId) {
        User user = findUserById(adminId);
        user.withdrawUser();
        return new UserWithdrawResponse(
                user.getId(),
                user.getEmail(),
                user.isDeleted(),
                user.getDeletedAt()
        );
    }

    // 입영 정보
    private void fillEnlistmentInfo(UserAdminResponse response, Long userId) {
        enlistmentApplicationRepository.findByUserId(userId)
                .ifPresent(response::setEnlistmentInfo);
    }

}