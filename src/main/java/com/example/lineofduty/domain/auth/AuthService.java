package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.auth.dto.request.LoginRequest;
import com.example.lineofduty.domain.auth.dto.request.SignupRequest;
import com.example.lineofduty.domain.token.RefreshToken;
import com.example.lineofduty.domain.token.RefreshTokenRepository;
import com.example.lineofduty.domain.token.TokenResponse;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorMessage.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByResidentNumber(request.getResidentNumber())) {
            throw new CustomException(ErrorMessage.DUPLICATE_RESIDENT_NUMBER);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role = request.isAdmin() ? Role.ROLE_ADMIN : Role.ROLE_USER;

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                role,
                request.getResidentNumber()
        );

        return userRepository.save(user).getId();
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
        // 탈퇴 여부 확인
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        // 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getRole());

        // Refresh Token 저장 (기존 토큰 있으면 update)
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(refreshToken, user.getId()));

        refreshTokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    // 토큰 재발급
    @Transactional
    public TokenResponse reissue(String refreshTokenStr) {

        String token = refreshTokenStr;

        if (StringUtils.hasText(token) && token.startsWith(JwtUtil.BEARER_PREFIX)) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new CustomException(ErrorMessage.INVALID_AUTH_INFO);
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(JwtUtil.BEARER_PREFIX + token)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        User user = userRepository.findById(refreshTokenEntity.getUserId())
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getRole());

        return new TokenResponse(newAccessToken, JwtUtil.BEARER_PREFIX + token);
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

}
