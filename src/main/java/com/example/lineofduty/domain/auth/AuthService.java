package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.auth.dto.LoginRequest;
import com.example.lineofduty.domain.auth.dto.SignupRequest;
import com.example.lineofduty.domain.token.RefreshToken;
import com.example.lineofduty.domain.token.RefreshTokenRepository;
import com.example.lineofduty.domain.token.TokenResponse;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${admin.token}")
    private String adminToken;

    // 회원가입
    @Transactional
    public void signup(SignupRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        // 권한 결정
        Role role = Role.ROLE_USER;
        if (request.isAdmin()) {
            if (!request.getAdminToken().equals(this.adminToken)) {
                throw new CustomException(ErrorMessage.INVALID_ADMIN_TOKEN);
            }
            role = Role.ROLE_ADMIN;
        }

        // 이미 존재하는 이메일 처리
        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // 활동 중인 유저면 가입불가
            if (!user.isDeleted()) {
                throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
            }

            // 탈퇴한 유저면 복구
            user.restore(
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role
            );
            return;
        }

        // 신규가입
        User user = new User(
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        userRepository.save(user);
    }

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorMessage.INVALID_AUTH_INFO));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorMessage.INVALID_AUTH_INFO);
        }
        // 탈퇴 유저 로그인 차단
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        // 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getRole());

        // DB에 Refresh Token 저장 (기존 토큰 있으면 update)
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(refreshToken, user.getId()));

        refreshTokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {

        if (refreshTokenRepository.existsById(userId)) {
            refreshTokenRepository.deleteById(userId);
        }
    }

    // 토큰 재발급
    @Transactional
    public TokenResponse reissue(String refreshTokenStr) {

        if (!jwtUtil.validateToken(refreshTokenStr)) {
            throw new CustomException(ErrorMessage.INVALID_TOKEN);
        }

        Long userId = jwtUtil.extractUserId(refreshTokenStr);

        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow( () -> new CustomException(ErrorMessage.USER_LOGOUT));

        if (!savedToken.getToken().equals(refreshTokenStr)) {
            throw new CustomException(ErrorMessage.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        // 탈퇴한 유저 재발급 차단
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getRole());

        return new TokenResponse(newAccessToken, refreshTokenStr);
    }

}
