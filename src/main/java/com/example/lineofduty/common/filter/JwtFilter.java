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

        // 토큰 추출
        String token = resolveToken(request);

        // DB에러 등 났을 때 EntryPoint로 넘어가지 않게
        try {
            // 토큰 유효성 검사
            if (token != null & jwtUtil.validateToken(token)) {

                Long userId = jwtUtil.extractUserId(token);
                UserDetails userDetails = userDetailService.loadUserById(userId);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                log.info("인증 성공 : userId : {}", userId);
            }
        } catch (Exception e) {
            log.error("JWT 필터 내부 오류 발생 : {} ", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // 토큰만 꺼내는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtUtil.BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
