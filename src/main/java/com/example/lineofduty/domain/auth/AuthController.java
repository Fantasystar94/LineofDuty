package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.exception.GlobalExceptionHandler;
import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.auth.dto.request.LoginRequest;
import com.example.lineofduty.domain.auth.dto.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.cfg.MapperBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MapperBuilder mapperBuilder;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<GlobalResponse> signup(@Valid @RequestBody SignupRequest request) {

        Long userId = authService.signup(request);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.USER_CREATE_SUCCESS, data));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<GlobalResponse> login(@Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION, token)
                .body(GlobalResponse.success(SuccessMessage.AUTH_LOGIN_SUCCESS, Map.of("token", token)));
    }

}
