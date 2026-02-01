package com.example.lineofduty.domain.payment.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.payment.dto.*;
import com.example.lineofduty.domain.payment.service.PaymentService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결재 요청
    @PostMapping
    public ResponseEntity<GlobalResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request, @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        PaymentCreateResponse response = paymentService.createPaymentService(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.PAYMENT_CREATE_SUCCESS, response));
    }

    // 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<GlobalResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {

        PaymentConfirmResponse response = paymentService.confirmPaymentService(request);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_CONFIRM_SUCCESS, response));
    }

    // 결제 조회 (paymentKey)
    @GetMapping("/{paymentKey}")
    public ResponseEntity<GlobalResponse> getPaymentByPaymentKey(@PathVariable String paymentKey) {

        PaymentGetResponse response = paymentService.getPaymentByPaymentKeyService(paymentKey);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_GET_SUCCESS, response));
    }

    // 결제 조회 (orderNumber)
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<GlobalResponse> getPaymentByOrderId(@PathVariable String orderNumber) {

        PaymentGetResponse response = paymentService.getPaymentByOrderIdService(orderNumber);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_GET_SUCCESS, response));
    }

    // 결제 취소
    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<GlobalResponse> cancelPayment(@Valid @RequestBody PaymentCancelRequest request, @PathVariable String paymentKey, @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        PaymentCancelResponse response = paymentService.cancelPaymentService(request, paymentKey, userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_CANCEL_SUCCESS, response));
    }

}
