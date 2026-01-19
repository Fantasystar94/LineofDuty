package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.auth.dto.request.LoginRequest;
import com.example.lineofduty.domain.auth.dto.request.SignupRequest;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByResidentNumber(request.getResidentNumber())) {
            throw new IllegalArgumentException("이미 등록된 주민등록번호입니다.");
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
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (user.isDeleted()) {
            throw new IllegalArgumentException("탈퇴한 사용자입니다.");
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

}
