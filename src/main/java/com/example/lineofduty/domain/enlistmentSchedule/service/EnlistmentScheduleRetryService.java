package com.example.lineofduty.domain.enlistmentSchedule.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnlistmentScheduleRetryService {
    private final EnlistmentLockTestService service;

    public EnlistmentScheduleCreateResponse withdrawRetry(Long userId, EnlistmentScheduleCreateRequest request) {

        int retry = 0;
        int maxRetry = 10;

        while (retry < maxRetry) {
            try {

                return service.applyEnlistmentTestOptimisticLock(userId, request);

            } catch (ObjectOptimisticLockingFailureException e) {

                retry++;

                if (retry >= maxRetry) {
                    throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
                }
            }
        }
        throw new CustomException(ErrorMessage.SCHEDULE_CONFLICT);
    }
}
