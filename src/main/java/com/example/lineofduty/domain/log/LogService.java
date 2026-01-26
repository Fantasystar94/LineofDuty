package com.example.lineofduty.domain.log;

import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Long userId, String action, String status, String errorMessage, String requestData) {

        SystemLog log = new SystemLog(userId, action, errorMessage, requestData);

        logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<SystemLogResponse> searchLogs(int page, int size, String sort, String username) {
        String sortProperty = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            sortProperty = sortParams[0];
            if (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])) {
                sortDirection = Sort.Direction.ASC;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));
        Page<SystemLog> logPage;

        if (username == null || username.trim().isEmpty()) {
            logPage = logRepository.findAll(pageable);
        } else {
            logPage = logRepository.searchSystemLogsByUsername(username, pageable);
        }

        return logPage.map(log -> {
            String user = "Unknown";
            if (log.getUserId() != null) {
                user = userRepository.findById(log.getUserId())
                        .map(User::getUsername)
                        .orElse("Unknown");
            }
            return SystemLogResponse.of(log, user);
        });
    }
}
