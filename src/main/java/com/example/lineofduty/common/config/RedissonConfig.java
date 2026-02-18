package com.example.lineofduty.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.redisson.connection-pool-size:64}")
    private int connectionPoolSize;

    @Value("${spring.data.redis.redisson.connection-minimum-idle-size:10}")
    private int connectionMinimumIdleSize;

    @Value("${spring.data.redis.redisson.idle-connection-timeout:10000}")
    private int idleConnectionTimeout;

    @Value("${spring.data.redis.redisson.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${spring.data.redis.redisson.timeout:3000}")
    private int timeout;

    @Value("${spring.data.redis.redisson.retry-attempts:3}")
    private int retryAttempts;

    @Value("${spring.data.redis.redisson.retry-interval:1500}")
    private int retryInterval;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        String address = "redis://" + redisHost + ":" + redisPort;

        config.useSingleServer()
                .setAddress(address)
                .setPassword(redisPassword.isEmpty() ? null : redisPassword)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval)
                .setPingConnectionInterval(30000)
                .setKeepAlive(true);

        return Redisson.create(config);
    }
}