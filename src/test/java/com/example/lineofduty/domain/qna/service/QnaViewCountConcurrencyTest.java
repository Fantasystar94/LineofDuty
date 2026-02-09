package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.qna.Qna;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("concurrency")
class QnaViewCountConcurrencyTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private UserRepository userRepository;

    private Long qnaId;

    @BeforeEach
    void setUp() {
        User user = new User("test@example.com", "password", "nickname", Role.ROLE_USER);
        userRepository.save(user);

        Qna qna = new Qna("title", "content", user);
        qnaRepository.save(qna);
        qnaId = qna.getId();
    }

    @AfterEach
    void tearDown() {
        qnaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("동시성 이슈 발생 테스트 - 100명이 동시에 조회할 때 조회수가 100이 되지 않음")
    void concurrencyIssueTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        System.out.println("=== 동시성 이슈 테스트 시작 (Thread Count: " + threadCount + ") ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    qnaService.qnaInquiry(qnaId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        Qna qna = qnaRepository.findById(qnaId).orElseThrow();
        System.out.println("예상 조회수: " + threadCount);
        System.out.println("실제 조회수: " + qna.getViewCount());
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("============================================================");
    }

    @Test
    @DisplayName("비관적 락 적용 테스트 - 100명이 동시에 조회할 때 조회수가 정확히 100이 됨")
    void pessimisticLockTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("=== 비관적 락 테스트 시작 (Thread Count: " + threadCount + ") ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    qnaService.qnaInquiryWithPessimisticLock(qnaId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        Qna qna = qnaRepository.findById(qnaId).orElseThrow();
        System.out.println("예상 조회수: " + threadCount);
        System.out.println("실제 조회수: " + qna.getViewCount());
        System.out.println("결과: " + (qna.getViewCount() == threadCount ? "성공" : "실패"));
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("============================================================");
        
        assertThat(qna.getViewCount()).isEqualTo(100L);
    }

    @Test
    @DisplayName("낙관적 락 적용 테스트 - 충돌 발생 시 예외 발생 확인")
    void optimisticLockTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        System.out.println("=== 낙관적 락 테스트 시작 (Thread Count: " + threadCount + ") ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    qnaService.qnaInquiryWithOptimisticLock(qnaId);
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.getAndIncrement();
                    // System.out.println("충돌 발생! (낙관적 락 예외)"); // 로그가 너무 많아질 수 있어 주석 처리
                } catch (Exception e) {
                    System.out.println("기타 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        Qna qna = qnaRepository.findById(qnaId).orElseThrow();
        System.out.println("총 시도 횟수: " + threadCount);
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패(충돌) 횟수: " + failCount.get());
        System.out.println("DB 반영된 조회수: " + qna.getViewCount());
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("============================================================");

        assertThat(qna.getViewCount()).isEqualTo(successCount.get());
        if (threadCount > 1) {
             assertThat(failCount.get()).isGreaterThan(0);
        }
    }
}
