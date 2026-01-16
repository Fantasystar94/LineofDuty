package com.example.lineofduty.domain.payment.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.order.repository.OrderRepository;
import com.example.lineofduty.domain.payment.dto.PaymentCreateRequest;
import com.example.lineofduty.domain.payment.dto.PaymentCreateResponse;
import com.example.lineofduty.domain.payment.repository.PaymentRepository;
import com.example.lineofduty.entity.Order;
import com.example.lineofduty.entity.OrderItem;
import com.example.lineofduty.entity.Payment;
import com.example.lineofduty.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentCreateResponse createPaymentService(PaymentCreateRequest request, Long userId) {

        // 결제할 주문서(order)를 찾아
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // 니가 이 결제에 접근 권한을 가지고 있는지 확인해
        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        // 주문서(order)에서 주문 내역(List<orderItem>)을 가져와
        List<OrderItem> orderItemList = order.getOrderItems();

        // 주문 내역(List<orderItem>)에 맞추어서 재고(product) 차감해
        for (OrderItem orderItem : orderItemList) {
            Product product = orderItem.getProduct();
            product.setStock((int) (product.getStock() - orderItem.getQuantity()));
        }

        // 결제 기록(Payment) 남기고 주문서는 삭제해
        Payment payment = new Payment(order);
        paymentRepository.save(payment);
        orderRepository.delete(order);
        return PaymentCreateResponse.from(payment);
    }
}
