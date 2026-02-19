package com.example.lineofduty.domain.qna.service;


import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {
    
    private final StringRedisTemplate redisTemplate;
    
    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    
    public void checkPostLimit(String userId) {
        String key = "post_limit:" + userId;
        long limit = 2;
        long timeout = 60;

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        
        Long count = operations.increment(key);
        
        if (count != null && count == 1 ) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
        
        if (count != null && count > limit) {
            throw new CustomException(ErrorMessage.ANTI_PLAQUE_FUNCTION);
        }
    }
}

