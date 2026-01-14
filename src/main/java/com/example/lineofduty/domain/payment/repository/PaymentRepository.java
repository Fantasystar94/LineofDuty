package com.example.lineofduty.domain.payment.repository;

import com.example.lineofduty.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
