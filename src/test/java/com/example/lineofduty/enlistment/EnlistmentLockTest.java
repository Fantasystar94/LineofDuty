package com.example.lineofduty.enlistment;

import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.lineofduty.common.exception.ErrorMessage.DUPLICATE_SCHEDULE;
import static com.example.lineofduty.common.exception.ErrorMessage.NO_REMAINING_SLOTS;

@SpringBootTest
public class EnlistmentLockTest {

    @Autowired
    EnlistmentScheduleService enlistmentScheduleService;
    @Autowired
    private EnlistmentScheduleRepository enlistmentScheduleRepository;

    private int exceptionCount = 0;
    private final Long scheduleId = 17L;
    AtomicInteger noSlot = new AtomicInteger();
    AtomicInteger duplicate = new AtomicInteger();
    @Test
    void 비관락_테스트_정상작동() {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> {
            try{
                enlistmentScheduleService.applyEnlistment(15L, new EnlistmentScheduleCreateRequest(scheduleId));
            } catch (Exception e) {
                exceptionCount++;
                System.out.println("예외 발생: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try{
                enlistmentScheduleService.applyEnlistment(16L, new EnlistmentScheduleCreateRequest(scheduleId));
            } catch (Exception e) {
                exceptionCount++;
                System.out.println("예외 발생: " + e.getMessage());
            }
        };

        Runnable task3 = () -> {
            try{
                enlistmentScheduleService.applyEnlistment(17L, new EnlistmentScheduleCreateRequest(scheduleId));
            } catch (Exception e) {
                exceptionCount++;
                System.out.println("예외 발생: " + e.getMessage());
            }
        };

        // 동시에 실행
        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        EnlistmentSchedule enlistmentSchedule =  enlistmentScheduleRepository.findById(scheduleId).orElseThrow(()-> new RuntimeException("실패"));
        System.out.println("터진 예외 처리 갯수 :" + exceptionCount);
        System.out.println("남은 슬롯 : " + enlistmentSchedule.getRemainingSlots());
    }

    @Test
    void 비관락_대량_테스트() throws InterruptedException {
        int threadCount = 50;   //스레드
        int requestCount = 500; //총 요청

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            Long userId = 10000L + i; // 유저는 전부 다르게

            executor.submit(() -> {
                try {
                    startLatch.await();
                    enlistmentScheduleService.applyEnlistmentTest(
                            userId,
                            new EnlistmentScheduleCreateRequest(scheduleId)
                    );
                } catch (Exception e) {
                    exceptionCount++;
                    System.out.println("예외 발생: " + e.getMessage());
                    if (Objects.equals(e.getMessage(), NO_REMAINING_SLOTS.getMessage())) {
                        noSlot.incrementAndGet();
                    } else if (Objects.equals(e.getMessage(), DUPLICATE_SCHEDULE.getMessage())) {
                        duplicate.incrementAndGet();
                    }
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        endLatch.await();

        executor.shutdown();

        EnlistmentSchedule schedule =
                enlistmentScheduleRepository.findById(scheduleId).orElseThrow();
        System.out.println("발생한 예외처리 : "+exceptionCount);
        System.out.println("noSlot : " + noSlot);
        System.out.println("duplicate : "+ duplicate);
        System.out.println("남은 슬롯: " + schedule.getRemainingSlots());

    }

}
