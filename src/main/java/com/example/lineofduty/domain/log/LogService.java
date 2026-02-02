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
    public void saveLog(Long userId, String action, String errorMessage, String requestData) {

        SystemLog log = new SystemLog(userId, action, errorMessage, requestData);

        logRepository.save(log);
    }

}
