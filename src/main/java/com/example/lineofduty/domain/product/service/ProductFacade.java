package com.example.lineofduty.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final RedissonClient redissonClient;
    private final ProductService productService;

    private static final String LOCK_PREFIX = "product:";

    public void decreaseStock(Long productId, Long quantity) {

        RLock lock = redissonClient.getLock(LOCK_PREFIX + productId);

        try {
            lock.lock();  // leaseTime 제거 (WatchDog 사용)

            productService.decreaseStock(productId, quantity);

        } finally {
            lock.unlock();
        }
    }

    public void increaseStock(Long productId, Long quantity) {

        RLock lock = redissonClient.getLock(LOCK_PREFIX + productId);

        try {
            lock.lock();

            productService.increaseStock(productId, quantity);

        } finally {
            lock.unlock();
        }
    }
}
