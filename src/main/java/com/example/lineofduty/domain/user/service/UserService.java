package com.example.lineofduty.domain.user.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentApplicationRepository;
import com.example.lineofduty.domain.token.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

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

        // 변경할 이메일 없으면 기존 이메일 유지
        String newEmail = (request.getEmail() != null) ? request.getEmail() : user.getEmail();

        // (카카오회원) 비밀번호 검사 없이 변경되도록
        if (user.getKakaoId() != null) {
            // 중복체크
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
            }
            user.updateProfile(newEmail, null); // 이메일만 변경
        } else {

            // 기존 비밀번호 검증
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new CustomException(ErrorMessage.INVALID_AUTH_INFO);
            }

            // 이메일 중복 검사
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
                }
            }

            // 새 비밀번호 암호화
            String encodedPassword = null;
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                encodedPassword = passwordEncoder.encode(request.getNewPassword());
            }

            user.updateProfile(request.getEmail(), encodedPassword);

        }

        return new UserResponse(user);
    }

    // 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(Long userId, String profileImageUrl) {
        User user = findUserById(userId);

        user.updateProfileImage(profileImageUrl);
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawUser(Long userId, String password) {
        User user = findUserById(userId);

        // 비밀번호 불일치 시 예외처리
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorMessage.PASSWORD_MISMATCH);
        }

        user.withdrawUser();

        refreshTokenRepository.deleteById(userId);
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
    public UserWithdrawResponse withdrawAdmin(Long adminId, String password) {
        User user = findUserById(adminId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorMessage.PASSWORD_MISMATCH);
        }

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
