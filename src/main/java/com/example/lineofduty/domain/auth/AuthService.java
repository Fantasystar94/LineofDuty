package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.auth.dto.request.LoginRequest;
import com.example.lineofduty.domain.auth.dto.request.SignupRequest;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.domain.user.User;
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
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorMessage.INVALID_AUTH_INFO));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorMessage.INVALID_AUTH_INFO);
        }
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

}
