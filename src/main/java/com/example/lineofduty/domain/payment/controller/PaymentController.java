package com.example.lineofduty.domain.payment.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.payment.dto.PaymentCreateRequest;
import com.example.lineofduty.domain.payment.dto.PaymentCreateResponse;
import com.example.lineofduty.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결재 요청
    @PostMapping
    public ResponseEntity<GlobalResponse<PaymentCreateResponse>> createPayment(@RequestBody PaymentCreateRequest request) {    // @AuthenicationPrincipal Long userId

        PaymentCreateResponse response = paymentService.createPaymentService(request, 1L);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.PAYMENT_CREATE_SUCCESS, response));
    }
}
