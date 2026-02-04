package com.example.lineofduty.enlistment;

import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentLockTestService;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleRetryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.lineofduty.common.exception.ErrorMessage.DUPLICATE_SCHEDULE;
import static com.example.lineofduty.common.exception.ErrorMessage.NO_REMAINING_SLOTS;

@SpringBootTest
public class EnlistmentLockTest {

    @Autowired
    EnlistmentLockTestService enlistmentScheduleService;
    @Autowired
    private EnlistmentScheduleRepository enlistmentScheduleRepository;

    @Autowired
    EnlistmentScheduleRetryService enlistmentScheduleRetryService;
    private int exceptionCount = 0;
    private final Long scheduleId = 20L;

    @Test
    void 비관락_테스트_정상작동() {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> {
            try{
                enlistmentScheduleService.applyEnlistmentTest(18L, new EnlistmentScheduleCreateRequest(scheduleId));
            } catch (Exception e) {
                exceptionCount++;
                System.out.println("예외 발생: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try{
                enlistmentScheduleService.applyEnlistmentTest(19L, new EnlistmentScheduleCreateRequest(scheduleId));
            } catch (Exception e) {
                exceptionCount++;
                System.out.println("예외 발생: " + e.getMessage());
            }
        };

        Runnable task3 = () -> {
            try{
                enlistmentScheduleService.applyEnlistmentTest(20L, new EnlistmentScheduleCreateRequest(scheduleId));
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

    private void resetSlots(Long scheduleId, int slots) {
        EnlistmentSchedule schedule =
                enlistmentScheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setRemainingSlots(slots);
        enlistmentScheduleRepository.saveAndFlush(schedule);
    }

    @Test
    void 비관락_슬롯2개_3명동시신청() throws InterruptedException {

        // given
        resetSlots(scheduleId, 2);

        int requestCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger noSlot = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            Long userId = 20000L + i;

            executor.submit(() -> {
                try {
                    startLatch.await();
                    enlistmentScheduleService.applyEnlistmentTest(
                            userId,
                            new EnlistmentScheduleCreateRequest(scheduleId)
                    );
                    success.incrementAndGet();
                } catch (Exception e) {
                    if (e.getMessage().equals(NO_REMAINING_SLOTS.getMessage())) {
                        noSlot.incrementAndGet();
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

        // then
        System.out.println("=== 슬롯2 / 3명 테스트 ===");
        System.out.println("성공: " + success.get());
        System.out.println("noSlot: " + noSlot.get());
        System.out.println("남은 슬롯: " + schedule.getRemainingSlots());

        assert success.get() == 2;
        assert noSlot.get() == 1;
        assert schedule.getRemainingSlots() == 0;
    }

    @Test
    void 비관락_슬롯0개_요청차단() throws InterruptedException {

        // given
        resetSlots(scheduleId, 0);

        int requestCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger noSlot = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            Long userId = 30000L + i;

            executor.submit(() -> {
                try {
                    startLatch.await();
                    enlistmentScheduleService.applyEnlistmentTest(
                            userId,
                            new EnlistmentScheduleCreateRequest(scheduleId)
                    );
                    success.incrementAndGet();
                } catch (Exception e) {
                    if (e.getMessage().equals(NO_REMAINING_SLOTS.getMessage())) {
                        noSlot.incrementAndGet();
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

        // then
        System.out.println("=== 슬롯0 테스트 ===");
        System.out.println("성공: " + success.get());
        System.out.println("noSlot: " + noSlot.get());
        System.out.println("남은 슬롯: " + schedule.getRemainingSlots());

        assert success.get() == 0;
        assert noSlot.get() == requestCount;
        assert schedule.getRemainingSlots() == 0;
    }


    @Test
    void 비관락_대량_테스트() throws InterruptedException {

        int threadCount = 50;
        int requestCount = 500;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger noSlot = new AtomicInteger();
        AtomicInteger duplicate = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            Long userId = 10000L + i;

            executor.submit(() -> {
                try {

                    startLatch.await();

                    enlistmentScheduleService.applyEnlistmentTest(
                            userId,
                            new EnlistmentScheduleCreateRequest(scheduleId)
                    );

                    success.incrementAndGet();

                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    if (e.getMessage().equals(NO_REMAINING_SLOTS.getMessage())) {
                        noSlot.incrementAndGet();
                    } else if (e.getMessage().equals(DUPLICATE_SCHEDULE.getMessage())) {
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

        long endTime = System.currentTimeMillis();

        EnlistmentSchedule schedule =
                enlistmentScheduleRepository.findByIdTest(scheduleId);

        System.out.println("====== JUnit 동시성 테스트 결과 ======");
        System.out.println("전략: 비관락");
        System.out.println("총 요청: " + requestCount);
        System.out.println("성공: " + success.get());
        System.out.println("실패: " + exceptionCount.get());
        System.out.println("noSlot: " + noSlot.get());
        System.out.println("duplicate: " + duplicate.get());
        System.out.println("남은 슬롯: " + schedule.getRemainingSlots());
        System.out.println("총 소요 시간(ms): " + (endTime - startTime));
    }


    @Test
    void 낙관락_대량_테스트() throws InterruptedException {

        int threadCount = 50;
        int requestCount = 500;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger noSlot = new AtomicInteger();
        AtomicInteger duplicate = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();
        AtomicInteger cantFindReason = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            Long userId = 10000L + i;

            executor.submit(() -> {
                try {

                    startLatch.await();

                    enlistmentScheduleRetryService.withdrawRetry(
                            userId,
                            new EnlistmentScheduleCreateRequest(scheduleId)
                    );

                    success.incrementAndGet();

                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    if (e.getMessage().equals(NO_REMAINING_SLOTS.getMessage())) {
                        noSlot.incrementAndGet();
                    } else if (e.getMessage().equals(DUPLICATE_SCHEDULE.getMessage())) {
                        duplicate.incrementAndGet();
                    } else {
                        System.out.println(e.getMessage());
                        cantFindReason.incrementAndGet();
                    }
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();

        EnlistmentSchedule schedule = enlistmentScheduleRepository.findById(scheduleId).orElseThrow();

        System.out.println("====== JUnit 동시성 테스트 결과 ======");
        System.out.println("전략: 낙관락");
        System.out.println("총 요청: " + requestCount);
        System.out.println("성공: " + success.get());
        System.out.println("실패: " + exceptionCount.get());
        System.out.println("noSlot: " + noSlot.get());
        System.out.println("duplicate: " + duplicate.get());
        System.out.println("알수없는 이유: " + cantFindReason.get());
        System.out.println("남은 슬롯: " + schedule.getRemainingSlots());
        System.out.println("총 소요 시간(ms): " + (endTime - startTime));
    }

}
