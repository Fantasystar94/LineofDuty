package com.example.lineofduty.domain.payment.repository;

import com.example.lineofduty.common.exception.ValidationMessage;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.payment.Payment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrder(Order order);

    Optional<Payment> findPaymentByPaymentKey(@NotNull(message = ValidationMessage.NOT_BLANK_PAYMENT_KEY) String paymentKey);

    Optional<Payment> findPaymentByOrderId(String orderIdString);
}
