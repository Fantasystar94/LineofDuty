package com.example.lineofduty.domain.order.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.order.dto.*;
import com.example.lineofduty.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<GlobalResponse<OrderCreateResponse>> createOrder(@RequestBody OrderCreateRequest request) { // @AuthenicationPrincipal Long userId

        OrderCreateResponse response = orderService.createOrderService(request, 1L);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.ORDER_CREATE_SUCCESS, response));
    }

    // 주문 목록 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<GlobalResponse<OrderGetResponse>> getOrder(@PathVariable Long orderId) {

        OrderGetResponse response = orderService.getOrderService(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.ORDER_GET_SUCCESS, response));
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}/orderItems/{orderItemId}")
    public ResponseEntity<GlobalResponse<OrderItemGetResponse>> getOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {

        OrderItemGetResponse response = orderService.getOrderItemService(orderId, orderItemId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.ORDER_GET_SUCCESS, response));
    }

    // 주문 상태 수정
    @PatchMapping("/{orderId}/orderItems/{orderItemId}")
    public ResponseEntity<GlobalResponse<OrderUpdateResponse>> updateOrder(@PathVariable Long orderId, @PathVariable Long orderItemId, @RequestBody OrderUpdateRequest request) {

        OrderUpdateResponse response = orderService.updateOrderService(orderId, orderItemId, request);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.ORDER_UPDATE_SUCCESS, response));
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<GlobalResponse<Void>> deleteOrder(@PathVariable Long orderId, Long userId) {       // @AuthenicationPrincipal Long userId

        orderService.deleteOrderService(orderId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.successNodata(SuccessMessage.ORDER_DELETE_SUCCESS));
    }
}
