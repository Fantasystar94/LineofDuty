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
        String className = joinPoint.getTarget().getClass().getName();

        Long userId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetail) {
                userId = ((UserDetail) authentication.getPrincipal()).getUser().getId();
            }
        } catch (Exception ignored) {

        }

        try {
            return joinPoint.proceed();

        } catch (Exception e) {
            // 1. 에러 관련 로그 저장
            log.error("API Request Failed - UserID: {}, Method: {}, URI: {}, Error: {}", userId, method, uri, e.getMessage());

            // 2. 데이터베이스에 저장
            if (isCriticalDomain(className)) {
                log.error("=== Error DB Logging {} ===", className);
                logService.saveLog(userId, method + " " + uri, e.getMessage(), args);
            }

            throw e;

        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.info("Request: {} {} | Time: {}ms", method, uri, executionTime);
        }
    }


    private boolean isCriticalDomain(String className) {
        return className.contains(".payment.") || className.contains(".enlistmentSchedule.");
    }
}
