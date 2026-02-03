package com.example.lineofduty.domain.kakao;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.token.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 로그인 콜백
    @GetMapping("/callback")
    public ResponseEntity<GlobalResponse> kakaoLogin(@RequestParam("code") String code) {

        TokenResponse tokenResponse = kakaoService.kakaoLogin(code);

        return ResponseEntity.ok(
                GlobalResponse.success(SuccessMessage.AUTH_LOGIN_SUCCESS, tokenResponse));
    }
}
