package com.example.lineofduty.common.scheduler;

import com.example.lineofduty.domain.auth.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupScheduler {

    private final EmailVerificationRepository emailVerificationRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void cleanupExpiredVerificationCodes() {

        log.info("만료된 이메일 인증 데이터 삭제 시작");

        // 현재 시간 기준으로 만료된 데이터 삭제 요청
        emailVerificationRepository.deleteExpiredData(LocalDateTime.now());

        log.info("만료된 이메일 인증 데이터 청소 완료");
    }

}
