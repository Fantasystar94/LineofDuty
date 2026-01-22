package com.example.lineofduty.domain.log;

import com.example.lineofduty.domain.user.dto.UserDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final LogService logService;

    @Pointcut("execution(* com.example.lineofduty.domain..controller.*.*(..))")
    public void controller() {}

    @Around("controller()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String args = Arrays.toString(joinPoint.getArgs());

        Long userId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetail) {
                userId = ((UserDetail) authentication.getPrincipal()).getUser().getId();
            }
        } catch (Exception e) {
            // 인증 정보 가져오기 실패 시 무시 (비로그인 요청 등)
        }

        try {
            // 성공 시에는 DB 저장 없이 결과만 반환
            return joinPoint.proceed();
        } catch (Exception e) {
            // 실패 로그 DB 저장
            logService.saveLog(userId, method + " " + uri, "FAIL", e.getMessage(), args);

            // 에러 로그 파일 저장
            log.error("API Request Failed - UserID: {}, Method: {}, URI: {}, Error: {}", userId, method, uri, e.getMessage(), e);

            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            // 일반 로그 파일 저장 (성공/실패 여부와 관계없이 실행 시간 등 기록)
            log.info("Request: {} {} | Time: {}ms", method, uri, executionTime);
        }
    }
}
