package com.example.lineofduty.domain.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.example.lineofduty.domain..controller.*.*(..))")
    public void controller() {}

    @Around("controller()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                log.info("Request: {} {} | Time: {}ms", request.getMethod(), request.getRequestURI(), executionTime);
        }
    }
}
