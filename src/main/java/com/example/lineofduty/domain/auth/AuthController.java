package com.example.lineofduty.domain.auth;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.auth.dto.request.LoginRequest;
import com.example.lineofduty.domain.auth.dto.request.SignupRequest;
import com.example.lineofduty.domain.token.TokenRequest;
import com.example.lineofduty.domain.token.TokenResponse;
import com.example.lineofduty.domain.user.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

        TokenResponse tokenResponse = authService.login(request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.AUTH_LOGIN_SUCCESS, tokenResponse));
    }

    // 재발급
    @PostMapping("/reissue")
    public ResponseEntity<GlobalResponse> reissue(@RequestBody TokenRequest request) {
        TokenResponse response = authService.reissue(request.getRefreshToken());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.AUTH_REISSUE_SUCCESS, response));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse> logout(@AuthenticationPrincipal UserDetail userDetail) {
        authService.logout(userDetail.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.AUTH_LOGOUT_SUCCESS, null));
    }

}
