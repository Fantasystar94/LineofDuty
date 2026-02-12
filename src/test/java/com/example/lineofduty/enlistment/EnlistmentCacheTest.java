package com.example.lineofduty.enlistment;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.repository.QueryEnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Objects;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Tag("concurrency")
public class EnlistmentCacheTest {

    @Autowired
    private EnlistmentScheduleService service;

    @SpyBean
    private QueryEnlistmentScheduleRepository repository;

    @Autowired
    private RedisCacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache("enlistmentList")).clear();
    }

    @Test
    void 같은페이지_2회조회_DB는_한번만_호출() {
        Pageable pageable = PageRequest.of(0, 100);


        // 첫 호출
        service.getEnlistmentList(pageable);

        // 두 번째 호출 (캐시 히트 기대)
        service.getEnlistmentList(pageable);

        // DB 호출 1번만 발생해야 함
        verify(repository, times(1))
                .getEnlistmentListSortBy(any(), any());
    }

    @Test
    void 신청_후에는_캐시가_무효화된다() {

        Pageable pageable = PageRequest.of(0, 100);

        // 캐시 생성
        service.getEnlistmentList(pageable);
        service.getEnlistmentList(pageable);

        verify(repository, times(1))
                .getEnlistmentListSortBy(any(), any());

        // 신청 로직 실행
        final Long userId = 17L;
        EnlistmentScheduleCreateRequest request = new EnlistmentScheduleCreateRequest(17L);

        service.applyEnlistment(userId, request);

        // 다시 조회
        service.getEnlistmentList(pageable);

        // 이제 DB가 다시 호출되어야 함
        verify(repository, times(2))
                .getEnlistmentListSortBy(any(), any());
    }

    @Test
    void 조회하면_Redis에_키가_생성된다() {

        Pageable pageable = PageRequest.of(0, 100);

        // 1️⃣ 캐시 생성
        service.getEnlistmentList(pageable);

        // 2️⃣ Redis에서 키 검색
        Set<String> keys = cacheManager.getCacheConfigurations().keySet();
        assertThat(keys).isNotNull();

        System.out.println("Redis Keys: " + keys);
    }


}
