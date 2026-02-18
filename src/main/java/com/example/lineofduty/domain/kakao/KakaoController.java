package com.example.lineofduty.domain.kakao;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.token.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @Value("${frontend.url}")
    private String frontendUrl;

    // 카카오 로그인 콜백
    @GetMapping("/callback")
    public void kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

        TokenResponse token = kakaoService.kakaoLogin(code);

        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;

        String accessToken = java.net.URLEncoder.encode(token.getAccessToken(), java.nio.charset.StandardCharsets.UTF_8);
        String refreshToken = java.net.URLEncoder.encode(token.getRefreshToken(), java.nio.charset.StandardCharsets.UTF_8);

        String redirectUrl = base + "/oauth/kakao/callback"
                + "#accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);
    }
}
