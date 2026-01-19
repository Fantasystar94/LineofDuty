package com.example.lineofduty.domain.order.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.order.dto.*;
import com.example.lineofduty.domain.order.repository.OrderRepository;
import com.example.lineofduty.domain.orderItem.repository.OrderItemRepository;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Order;
import com.example.lineofduty.entity.OrderItem;
import com.example.lineofduty.entity.Product;
import com.example.lineofduty.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 주문서(주문 포함) 생성
    @Transactional
    public OrderCreateResponse createOrderService(OrderCreateRequest request, Long userId) {

        // 찾는 상품이 있는 놈인지 찾아
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND)
        );

        /*
         만들어져있는 주문서가 있는지 확인해
         1. 주문서가 있다면 그 주문서를 사용해
         2. 주문서가 없다면 주문서를 새로 만들어
        */
        Order order = orderRepository.findOrderByUserId(userId).orElseGet(
                () -> createNewOrder(userId)    // 빈 주문서 생성
        );

        // request를 기반으로 주문(orderItem)을 만들어
        OrderItem orderItem = new OrderItem(product, order, (long) product.getPrice(), request.getQuantity());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // 주문서에 주문을 추가해
        order.addOrderItem(savedOrderItem);

        return OrderCreateResponse.from(order);
    }

    // 주문서(order) 조회
    @Transactional(readOnly = true)
    public OrderGetResponse getOrderService(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        return OrderGetResponse.from(order);
    }

    // 주문(orderItem) 조회
    @Transactional(readOnly = true)
    public OrderItemGetResponse getOrderItemService(Long orderId, Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        return OrderItemGetResponse.from(orderId, orderItem);
    }

    // 주문 수정
    @Transactional
    public OrderUpdateResponse updateOrderService(Long orderId, Long orderItemId, OrderUpdateRequest request) {

        // 주문서를 찾아
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // 수정할 주문(orderItem)을 선택해
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // request대로 주문을 수정해
        Product product = orderItem.getProduct();
        long quantity = orderItem.getQuantity();
        if (request.getProductId() != null) {

            product = productRepository.findById(request.getProductId()).orElseThrow(
                    () -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND)
            );
            orderItem.setProduct(product);
        }

        if (request.getQuantity() != null) {

            quantity = request.getQuantity();
            orderItem.setQuantity(quantity);
        }

        // 상품 변경으로 인한 총금액 수정
        long changedTotalPrice = 0;
        for (OrderItem item : order.getOrderItems()) {
            changedTotalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        order.setTotalPrice(changedTotalPrice);

        return OrderUpdateResponse.from(orderItem);
    }

    // 주문 취소(삭제)
    @Transactional
    public void deleteOrderService(Long orderId, Long userId) {

        Order order = orderRepository.findById(1L).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        Long orderUserId = order.getUser().getId();
        if (!orderUserId.equals(1L)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        orderRepository.delete(order);
    }

    // 빈 주문서 생성
    private Order createNewOrder(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
        );

        long totalPrice = 0;
        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order(user, totalPrice, orderItemList);
        return orderRepository.save(order);
    }
}
