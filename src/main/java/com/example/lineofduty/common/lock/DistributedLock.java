package com.example.lineofduty.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    // 릭의 이름
    String key();

    // 락 획득 대기 시간
    long waitTime() default 5L;

    long leaseTime() default 3L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
