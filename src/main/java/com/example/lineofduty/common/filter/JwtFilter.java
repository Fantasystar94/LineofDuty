package com.example.lineofduty.common.filter;

import com.example.lineofduty.common.util.JwtUtil;
import com.example.lineofduty.domain.user.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String url = request.getRequestURI();

        // 헤더에서 토큰 꺼내기
        String tokenValue = request.getHeader(JwtUtil.AUTHORIZATION);

        System.out.println("----------------------------------------");
        System.out.println("1. 요청 URL: " + url);
        System.out.println("2. 헤더 값 확인: " + tokenValue);

        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(jwtUtil.BEARER_PREFIX)) {

            // 2. Bearer 제거
            String token = tokenValue.substring(7);
            System.out.println("3. 추출된 토큰: " + token);

            // 3. 토큰 검증
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                System.out.println("4. 토큰 검증 성공 ID: " + userId);

                UserDetails userDetails = userDetailService.loadUserById(userId);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                System.out.println("5. SecurityContext에 인증 정보 저장 완료");

                log.info("인증 성공 : userId : {}", userId);
            } else {
                log.error("인증 실패 : 유효하지 않은 토큰");
            }

        } else {
            System.out.println("헤더가 없거나 'Bearer '로 시작하지 않음");
        }
        System.out.println("----------------------------------------");

        filterChain.doFilter(request, response);

    }
}
