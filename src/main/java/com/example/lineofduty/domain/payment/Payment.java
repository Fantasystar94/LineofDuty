package com.example.lineofduty.domain.payment;

import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(unique = true, nullable = false)
    private String paymentKey;

    @Column(unique = true)
    private String orderNumber;

    // 결제 상태 (READY, IN_PROGRESS, DONE, CANCELED, ABORTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.READY;

    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;

    public Payment(Order order, String paymentKey) {
        this.order = order;
        this.totalPrice = order.getTotalPrice();
        this.paymentKey = paymentKey;
        this.orderNumber = order.getOrderNumber();
    }

    public void updateStatus(PaymentStatus paymentStatus) {
        this.status = paymentStatus;
    }

    // toss-api로 response 받아서 payment 업데이트
    public void updateByResponse(PaymentStatus paymentStatus, String paymentKey, Long amount, OffsetDateTime requestedAt, OffsetDateTime approvedAt) {
        this.status = paymentStatus;
        this.paymentKey = paymentKey;
        this.totalPrice = amount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}