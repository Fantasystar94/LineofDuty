package com.example.lineofduty.domain.payment.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.payment.dto.*;
import com.example.lineofduty.domain.payment.service.PaymentService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "Payment API", description = "결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    // 결재 요청
    @PostMapping
    @Operation(summary = "결제 요청", description = "결제를 요청합니다.")
    public ResponseEntity<GlobalResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request, @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        PaymentCreateResponse response = paymentService.createPaymentService(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.PAYMENT_CREATE_SUCCESS, response));
    }

    // 결제 승인
    @PostMapping("/confirm")
    @Operation(summary = "결제 승인", description = "요청된 결제를 승인합니다.")
    public ResponseEntity<GlobalResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {

        PaymentConfirmResponse response = paymentService.confirmPaymentService(request);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_CONFIRM_SUCCESS, response));
    }

    // 결제 조회 (paymentKey)
    @GetMapping("/{paymentKey}")
    @Operation(summary = "결제 조회 (PaymentKey)", description = "PaymentKey를 사용하여 결제 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> getPaymentByPaymentKey(
            @Parameter(description = "결제 키", required = true) @PathVariable String paymentKey,
            @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        getPaymentResponse response = paymentService.getPaymentByPaymentKeyService(paymentKey, userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_GET_SUCCESS, response));
    }

    // 결제 조회 (orderIdString)
    @GetMapping("/orders/{orderIdString}")
    @Operation(summary = "결제 조회 (OrderId)", description = "주문 ID(문자열)를 사용하여 결제 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> getPaymentByOrderId(
            @Parameter(description = "주문 ID (문자열)", required = true) @PathVariable String orderIdString,
            @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        getPaymentResponse response = paymentService.getPaymentByOrderIdService(orderIdString, userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_GET_SUCCESS, response));
    }

    // 결제 취소
    @PostMapping("/{paymentKey}/cancel")
    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    public ResponseEntity<GlobalResponse> cancelPayment(
            @Parameter(description = "결제 키", required = true) @PathVariable String paymentKey,
            @AuthenticationPrincipal UserDetail userDetail) {

        long userId = userDetail.getUser().getId();
        CancelPaymentResponse response = paymentService.cancelPaymentService(paymentKey, userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_CANCEL_SUCCESS, response));
    }

}
