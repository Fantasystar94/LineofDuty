package com.example.lineofduty.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final RedissonClient redissonClient;
    private final ProductService productService;

    private static final String LOCK_PREFIX = "product:";

    public void decreaseStock(Long productId, Long quantity) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + productId);
        lock.lock();

        try {
            productService.decreaseStock(productId, quantity);

        } catch (Exception e) {
            lock.unlock();
            throw e;
        }

        //트랜잭션 커밋 이후 락 해제 등록
        registerUnlockAfterCommit(lock);
    }

    public void increaseStock(Long productId, Long quantity) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + productId);
        lock.lock();

        try {
            productService.increaseStock(productId, quantity);
        } catch (Exception e) {
            lock.unlock();
            throw e;
        }

        //트랜잭션 커밋 이후 락 해제 등록
        registerUnlockAfterCommit(lock);
    }

    // 트랜잭션 동기화가 활성화된 경우: 커밋 또는 롤백 완료 후 락 해제
    // 트랜잭션 동기화가 없는 경우: 즉시 락 해제
    private void registerUnlockAfterCommit(RLock lock) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            });
        } else {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
