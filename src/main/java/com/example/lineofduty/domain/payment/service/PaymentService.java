package com.example.lineofduty.domain.payment.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.order.repository.OrderRepository;
import com.example.lineofduty.domain.payment.dto.PaymentCreateRequest;
import com.example.lineofduty.domain.payment.dto.PaymentCreateResponse;
import com.example.lineofduty.domain.payment.repository.PaymentRepository;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.domain.payment.Payment;

import com.example.lineofduty.domain.product.Product;
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

        // 이미 결제한 주문인지 확인해
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new CustomException(ErrorMessage.ALREADY_PAID_ORDER);
        }

        // 니가 이 결제에 접근 권한을 가지고 있는지 확인해
        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        // 주문서(order)에서 주문 내역(List<orderItem>)을 가져와
        List<OrderItem> orderItemList = order.getOrderItems();

        // 주문 내역(List<orderItem>)에 맞추어서 재고(product) 차감해
        for (OrderItem orderItem : orderItemList) {
            Product product = orderItem.getProduct();

            // 재고가 부족하면 예외 출력
            if (product.getStock() < orderItem.getQuantity()) {
                throw new CustomException(ErrorMessage.OUT_OF_STOCK);
            }
            product.updateStock(product.getStock() - orderItem.getQuantity());
        }

        // 결제 기록(Payment) 남기고 결제 끝난 주문서는 삭제해
        Payment payment = new Payment(order);
        paymentRepository.save(payment);
        orderRepository.delete(order);
        return PaymentCreateResponse.from(payment);
    }
}
