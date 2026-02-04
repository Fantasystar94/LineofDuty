package com.example.lineofduty.domain.kakao;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.token.RefreshToken;
import com.example.lineofduty.domain.token.RefreshTokenRepository;
import com.example.lineofduty.domain.token.TokenResponse;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 인가 코드 받아서 로그인 처리까지
    @Transactional
    public TokenResponse kakaoLogin(String code) {

        try {
            // 인가 코드로 카카오 엑세스 토큰 요청
            String kakaoAccessToken = getKakaoAccessToken(code);

            // 카카오 액세스 토큰으로 카카오 유저 ID 요청
            KakaoUserInfo kakaoUserInfo = getkakaoUserInfo(kakaoAccessToken);

            User user = registerOrLogin(kakaoUserInfo);

            // JWT 토큰 발급
            String accessToken = jwtUtil.generateToken(user.getId(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getRole());

            // RefreshToken 저장
            RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                    .orElse(new RefreshToken(refreshToken, user.getId()));

            refreshTokenEntity.updateToken(refreshToken);
            refreshTokenRepository.save(refreshTokenEntity);

            return new TokenResponse(accessToken, refreshToken);

        } catch (RestClientException e) {

            log.error("카카오 API 호출 실패 : {}", e.getMessage());
            throw new CustomException(ErrorMessage.KAKAO_LOGIN_FAILED);
        }
    }

    private String getKakaoAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoTokenResponse.class
        );

        if (response.getBody() == null || response.getBody().getAccessToken() == null) {
            throw new CustomException(ErrorMessage.INVALID_AUTH_INFO);
        }

        return response.getBody().getAccessToken();
    }

    private KakaoUserInfo getkakaoUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                KakaoUserInfo.class
        );

        if (response.getBody() == null) {
            throw new CustomException(ErrorMessage.USER_NOT_FOUND);
        }

        return response.getBody();
    }

    private User registerOrLogin(KakaoUserInfo kakaoUserInfo) {

        Long kakaoId = kakaoUserInfo.getId();
        String nickname = kakaoUserInfo.getProperties().getNickname();

        User user = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (user == null) {
            // 신규 가입
            String email = "kakao_" + kakaoId + "@lineofduty.com";
            String password = UUID.randomUUID().toString(); // 비밀번호는 랜덤 처리

            user = new User(email, nickname, passwordEncoder.encode(password), Role.ROLE_USER);
            user.setKakaoId(kakaoId);

            userRepository.save(user);
        }

        // 탈퇴한 유저 체크
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        return user;
    }


}
