package com.example.lineofduty.domain.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Long userId, String action, String status, String errorMessage, String requestData) {
        SystemLog log = new SystemLog(userId, action, status, errorMessage, requestData);
        logRepository.save(log);
    }
}
