package com.example.lineofduty.distributedLock;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.model.enums.ProductStatus;
import com.example.lineofduty.domain.product.Product;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.product.service.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Tag("concurrency")
class ProductServiceConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 생성 (재고 100개)
        testProduct = new Product(
                "테스트 상품",
                "동시성 테스트용 상품",
                10000L,
                100L,
                ProductStatus.ON_SALE
        );
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 100명이 1개씩 구매 시도 - 재고 감소 동시성 테스트")
    void concurrentDecreaseStock_100Requests() throws InterruptedException {
        // Given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(testProduct.getId(), 1L);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("재고 감소 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== 테스트 결과 ===");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("예상 재고: " + (100 - successCount.get()));

        // 성공한 요청 수만큼 재고가 감소했는지 확인
        assertThat(updatedProduct.getStock()).isEqualTo(100 - successCount.get());
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("동시에 50명이 2개씩 구매 시도 - 재고 부족 상황 테스트")
    void concurrentDecreaseStock_OutOfStock() throws InterruptedException {
        // Given
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger outOfStockCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(testProduct.getId(), 2L);
                    successCount.incrementAndGet();
                } catch (CustomException e) {
                    outOfStockCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("예상치 못한 에러: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== 재고 부족 테스트 결과 ===");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("재고 부족 횟수: " + outOfStockCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());

        // 100개 재고에서 2개씩 구매하므로 최대 50명만 성공 가능
        assertThat(successCount.get()).isLessThanOrEqualTo(50);
        assertThat(updatedProduct.getStock()).isEqualTo(100 - (successCount.get() * 2L));
    }

    @Test
    @DisplayName("동시 구매 후 취소 - 재고 증가 동시성 테스트")
    void concurrentIncreaseStock() throws InterruptedException {
        // Given - 먼저 재고를 50개로 감소시킴
        productService.decreaseStock(testProduct.getId(), 50L);

        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // When - 30명이 동시에 1개씩 재고 증가 (취소 시뮬레이션)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.increaseStock(testProduct.getId(), 1L);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("재고 증가 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== 재고 증가 테스트 결과 ===");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("예상 재고: " + (50 + successCount.get()));

        assertThat(updatedProduct.getStock()).isEqualTo(50 + successCount.get());
        assertThat(successCount.get()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("구매와 취소가 동시에 발생 - 혼합 동시성 테스트")
    void concurrentDecreaseAndIncreaseStock() throws InterruptedException {
        // Given
        int decreaseThreadCount = 30;
        int increaseThreadCount = 20;
        int totalThreadCount = decreaseThreadCount + increaseThreadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(totalThreadCount);
        AtomicInteger decreaseSuccessCount = new AtomicInteger(0);
        AtomicInteger increaseSuccessCount = new AtomicInteger(0);

        // When - 구매(감소) 30건
        for (int i = 0; i < decreaseThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(testProduct.getId(), 1L);
                    decreaseSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("감소 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // When - 취소(증가) 20건
        for (int i = 0; i < increaseThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.increaseStock(testProduct.getId(), 1L);
                    increaseSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("증가 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        long expectedStock = 100 - decreaseSuccessCount.get() + increaseSuccessCount.get();

        System.out.println("=== 혼합 동시성 테스트 결과 ===");
        System.out.println("감소 성공: " + decreaseSuccessCount.get());
        System.out.println("증가 성공: " + increaseSuccessCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("예상 재고: " + expectedStock);

        assertThat(updatedProduct.getStock()).isEqualTo(expectedStock);
    }

    @Test
    @DisplayName("재고가 정확히 0이 되는 경우 - SOLD_OUT 상태 확인")
    void stockBecomesZero_StatusChangesToSoldOut() throws InterruptedException {
        // Given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // When - 100개 재고를 100명이 1개씩 구매
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(testProduct.getId(), 1L);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("구매 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== SOLD_OUT 상태 테스트 결과 ===");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("상품 상태: " + updatedProduct.getStatus());

        if (updatedProduct.getStock() == 0) {
            assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
        }
    }

    @Test
    @DisplayName("SOLD_OUT 상태에서 재고 증가 시 ON_SALE로 변경")
    void soldOutProduct_IncreaseStock_StatusChangesToOnSale() throws InterruptedException {
        // Given - 재고를 0으로 만듦
        productService.decreaseStock(testProduct.getId(), 100L);
        Product soldOutProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(soldOutProduct.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

        // When - 재고 증가
        productService.increaseStock(testProduct.getId(), 10L);

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== 상태 변경 테스트 결과 ===");
        System.out.println("재고: " + updatedProduct.getStock());
        System.out.println("상태: " + updatedProduct.getStatus());

        assertThat(updatedProduct.getStock()).isEqualTo(10L);
        assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("대량 동시 요청 - 500명이 동시에 구매 시도")
    void massiveConcurrentRequests() throws InterruptedException {
        // Given
        // 재고를 500개로 증가
        productService.increaseStock(testProduct.getId(), 400L);

        int threadCount = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(testProduct.getId(), 1L);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();

        // Then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("=== 대량 동시 요청 테스트 결과 ===");
        System.out.println("총 요청 수: " + threadCount);
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");

        assertThat(updatedProduct.getStock()).isEqualTo(500 - successCount.get());
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
    }
}