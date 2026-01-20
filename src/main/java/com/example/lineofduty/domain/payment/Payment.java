package com.example.lineofduty.domain.payment;

import com.example.lineofduty.entity.BaseEntity;
import com.example.lineofduty.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Payment(Order order) {
        this.order = order;
        this.totalPrice = order.getTotalPrice();
    }
}
