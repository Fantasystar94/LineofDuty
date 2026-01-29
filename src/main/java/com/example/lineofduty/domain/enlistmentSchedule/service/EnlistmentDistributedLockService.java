package com.example.lineofduty.domain.enlistmentSchedule.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.enlistmentSchedule.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnlistmentDistributedLockService {

    private final RedisLockService redisLockService;

    public EnlistmentScheduleCreateResponse applyWithDistributedLock(
            Long userId,
            EnlistmentScheduleCreateRequest request
    ) {

        String lockKey = "lock:enlistment:schedule:" + request.getScheduleId();

        int retry = 0;
        int maxRetry = 10;

        while (retry < maxRetry) {
            boolean locked = redisLockService.lock(lockKey, 3000); // 3초 TTL

            if (locked) {
                try {
                    throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
                } finally {
                    redisLockService.unlock(lockKey);
                }
            }
            retry++;
            try {
                Thread.sleep(50); // 짧은 backoff
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
            }
        }
        throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
    }
}
