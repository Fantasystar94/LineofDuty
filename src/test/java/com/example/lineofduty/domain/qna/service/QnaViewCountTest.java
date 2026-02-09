package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.domain.qna.Qna;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.common.model.enums.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Tag("concurrency")
public class QnaViewCountTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private UserRepository userRepository;

    private Long qnaId;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 및 게시글 생성
        User user = new User("test1@test1.com", "testUser", "password", Role.ROLE_USER);
        userRepository.save(user);

        Qna qna = new Qna("테스트 제목", "테스트 내용", user);
        qnaRepository.save(qna);
        qnaId = qna.getId();
    }

    @AfterEach
    public void tearDown() {
        qnaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 100명이 조회했을 때 조회수 증가 테스트 (락 미적용)")
    void 락이_적용되지_않은_경우_동시성테스트() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

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

        Qna qna = qnaRepository.findById(qnaId).orElseThrow();
        
        System.out.println("최종 조회수: " + qna.getViewCount());

        assertEquals(100, qna.getViewCount());
    }
}
