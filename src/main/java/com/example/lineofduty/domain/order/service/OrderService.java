package com.example.lineofduty.domain.order.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.order.dto.*;
import com.example.lineofduty.domain.order.repository.OrderRepository;
import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.domain.orderItem.repository.OrderItemRepository;
import com.example.lineofduty.domain.product.Product;
import com.example.lineofduty.domain.product.repository.ProductRepository;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private static final String CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "-_";
    private static final SecureRandom random = new SecureRandom();

    // 주문서(주문 포함) 생성
    @Transactional
    public OrderCreateResponse createOrderService(OrderCreateRequest request, Long userId) {

        // 찾는 상품이 있는 놈인지 찾아
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND)
        );

        if (request.getQuantity() > product.getStock()) {
            throw new CustomException(ErrorMessage.OUT_OF_STOCK);
        }

        /*
         만들어져있는 주문서가 있는지 확인해
         1. 사용 가능한 주문서가 있다면 그 주문서를 사용해
         2. 주문서가 없다면 주문서를 새로 만들어
        */
        Order order = orderRepository.findByUserIdAndStatusTrue(userId).orElseGet(
                () -> createNewOrder(userId, product)    // 빈 주문서 생성
        );

        // 주문서에 추가하려는 product가 이미 존재하는지 확인하고 있으면 주문 수량만 올리기
        OrderItem orderItem = null;
        for (OrderItem item : order.getOrderItemList()) {
            if (item.getProduct().getId().equals(request.getProductId())) {
                orderItem = item;
                break;
            }
        }

        // product가 없다면 request를 기반으로 주문(orderItem)을 만들어
        if (orderItem == null) {

            orderItem = new OrderItem(product, order, product.getPrice(), request.getQuantity());
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            order.addOrderItem(savedOrderItem);
            order.updateOrderName(createOrderName(order, product));
        } else if (orderItem.getQuantity() + request.getQuantity() <= product.getStock()) {

            orderItem.addQuantity(request.getQuantity());
            long changedTotalPrice = order.getTotalPrice() + request.getQuantity() * product.getPrice();
            order.updateTotalPrice(changedTotalPrice);
        } else {

            throw new CustomException(ErrorMessage.OUT_OF_STOCK);
        }

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

    // 주문 수정
    @Transactional
    public OrderUpdateResponse updateOrderService(Long orderId, Long orderItemId, OrderUpdateRequest request) {

        // 주문서를 찾아
        Order order = orderRepository.findByIdAndStatusTrue(orderId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // 수정할 주문(orderItem)을 선택해
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // request대로 주문을 수정해
        if (request.getProductId() != null) {

            Product product = productRepository.findById(request.getProductId()).orElseThrow(
                    () -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND)
            );
            orderItem.updateProduct(product);
        }

        if (request.getQuantity() != null) {

            long quantity = request.getQuantity();
            orderItem.updateQuantity(quantity);
        }

        // 상품 변경으로 인한 총금액 수정
        long changedTotalPrice = 0;
        long totalProductAmount = 0;
        for (OrderItem item : order.getOrderItemList()) {
            changedTotalPrice += item.getProduct().getPrice() * item.getQuantity();
            totalProductAmount += item.getQuantity();
        }
        order.updateTotalPrice(changedTotalPrice);
        order.updateOrderName(orderItem.getProduct().getName() + " 외 " + totalProductAmount+ "건");

        return OrderUpdateResponse.from(orderItem);
    }

    // 주문 취소(삭제)
    @Transactional
    public void deleteOrderService(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        Long orderUserId = order.getUser().getId();
        if (!orderUserId.equals(userId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        orderRepository.delete(order);
    }

    // 빈 주문서 생성
    private Order createNewOrder(Long userId, Product product) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
        );

        long totalPrice = 0;
        List<OrderItem> orderItemList = new ArrayList<>();
        String orderName = product.getName();
        String orderNumber = createOrderNumber();
        // 이미 존재하는 orderNumber라면 다시 작성
        while (orderRepository.existsByOrderNumber(orderNumber)) {
            orderNumber = createOrderNumber();
        }
        Order order = new Order(user, orderName, orderNumber, totalPrice, orderItemList);
        return orderRepository.save(order);
    }

    // orderNumber 생성
    private String createOrderNumber() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int index = random.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(index));
        }
        return sb.toString();
    }

    // OrderName 생성
    private String createOrderName(Order order, Product product) {

        long totalAmount = 0;
        for (OrderItem item : order.getOrderItemList()) {
            totalAmount += item.getQuantity();
        }

        return product.getName() + " 외 " + totalAmount+ "건";
    }
}
