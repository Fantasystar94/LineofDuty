package com.example.lineofduty.domain.payment.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.CustomTossResponseException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.order.repository.OrderRepository;
import com.example.lineofduty.domain.payment.dto.PaymentCreateRequest;
import com.example.lineofduty.domain.payment.dto.PaymentCreateResponse;
import com.example.lineofduty.domain.payment.repository.PaymentRepository;
import com.example.lineofduty.domain.order.Order;
import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.domain.payment.Payment;
import com.example.lineofduty.domain.payment.PaymentStatus;
import com.example.lineofduty.domain.payment.dto.*;
import com.example.lineofduty.domain.payment.repository.PaymentRepository;
import com.example.lineofduty.domain.product.Product;
import com.example.lineofduty.domain.product.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;

    @Value("${TOSS_SECRET_KEY}")
    private String secretKey;

    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TOSS_GET_BY_PAYMENTKEY_URL = "https://api.tosspayments.com/v1/payments";
    private static final String TOSS_GET_BY_ORDERID_URL = "https://api.tosspayments.com/v1/payments/orders";

    @Transactional
    public PaymentCreateResponse createPaymentService(PaymentCreateRequest request, Long userId) {

        // 결제할 주문서(order)를 찾아
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(
                () -> new CustomException(ErrorMessage.ORDER_NOT_FOUND)
        );

        // 이미 결제한 주문인지 확인해
        if (paymentRepository.existsByOrder(order)) {
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
            productService.decreaseStock(
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity()
            );
        }

        // 결제 기록(Payment) 남기기
        Payment payment = new Payment(order, request.getOrderIdString());
        paymentRepository.save(payment);
        return PaymentCreateResponse.from(payment);
    }

    // 결제 승인
    @Transactional
    public PaymentConfirmResponse confirmPaymentService(PaymentConfirmRequest request) {
        log.info(request.getOrderId());
        log.info(request.getPaymentKey());
        log.info(String.valueOf(request.getAmount()));
        // 승인할 결제(Payment) 찾아
        Payment payment = paymentRepository.findPaymentByOrderId(request.getOrderId()).orElseThrow(
                () -> new CustomException(ErrorMessage.NOT_FOUND_PAYMENT)
        );

        // 이미 승인된 결제일 경우
        if (payment.getStatus() == PaymentStatus.DONE) {
            throw new CustomException(ErrorMessage.ALREADY_PROCESSED_PAYMENT);
        }

        // 이미 취소된 결제일 경우
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new CustomException(ErrorMessage.ALREADY_CANCELED_PAYMENT);
        }

        // 결제할 금액 맞는지 확인해
        if (request.getAmount() != payment.getAmount()) {
            throw new CustomException(ErrorMessage.INVALID_AMOUNT_PAYMENT);
        }

        // 토스로 결제 요청 보내
        String body = String.format("""
                        {
                            "paymentKey": "%s",
                            "orderId": "%s",
                            "amount": %d
                        }
                        """,
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        try {

            String encodedKey = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(TOSS_CONFIRM_URL))
                    .header("Authorization", "Basic " + encodedKey)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(body)
                    )
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 결제 승인 받으면 성공 처리해
            //response(json형식)를 java객체로 변환해 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            // toss에서 에러를 출력할 시 에러 반환
            if (rootNode.has("message")) {
                throw new CustomTossResponseException(rootNode.get("message").asText());
            }

            String status = rootNode.get("status").asText();
            String paymentKey = rootNode.get("paymentKey").asText();
            String orderId = rootNode.get("orderId").asText();
            long amount = rootNode.get("amount").asLong();
            OffsetDateTime requestedAt = OffsetDateTime.parse(rootNode.get("requestedAt").asText());
            OffsetDateTime approvedAt = OffsetDateTime.parse(rootNode.get("approvedAt").asText());

            // toss 반환 값에 맞추어 결제 정보 업데이트
            payment.updateByResponse(PaymentStatus.valueOf(status), paymentKey, orderId, amount, requestedAt, approvedAt);

            // 결제 끝난 주문서는 사용 종료 처리
            payment.getOrder().updateStatus(false);

            return PaymentConfirmResponse.from(payment);
        } catch (IOException | InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    // 결제 조회 (paymentKey)
    @Transactional(readOnly = true)
    public PaymentGetResponse getPaymentByPaymentKeyService(String paymentKey) {

        // 토스로 결제 요청 보내
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOSS_GET_BY_PAYMENTKEY_URL + paymentKey))
                    .header("Authorization", "Basic " + secretKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //response(json형식)를 java객체로 변환해 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            // toss에서 에러를 출력할 시 에러 반환
            if (rootNode.get("message").asText() != null) {
                throw new CustomTossResponseException(rootNode.get("message").asText());
            }

            // toss에서 정상 값을 반환할 시 값 추출
            String status = rootNode.get("status").asText();
            String orderId = rootNode.get("orderId").asText();
            long amount = rootNode.get("amount").asLong();
            OffsetDateTime requestedAt = OffsetDateTime.parse(rootNode.get("requestedAt").asText());
            OffsetDateTime approvedAt = OffsetDateTime.parse(rootNode.get("approvedAt").asText());

            // toss 반환 값에 맞추어 response 생성, 반환
            return new PaymentGetResponse(PaymentStatus.valueOf(status), orderId, paymentKey, amount, requestedAt, approvedAt);
        } catch (IOException | InterruptedException ie) {   // 결제 조회 실패 시
            throw new RuntimeException(ie);
        }
    }

    // 결제 조회 (orderIdString)
    @Transactional(readOnly = true)
    public PaymentGetResponse getPaymentByOrderIdService(String orderIdString) {

        // 토스로 결제 요청 보내
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOSS_GET_BY_ORDERID_URL + orderIdString))
                    .header("Authorization", "Basic " + secretKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //response(json형식)를 java객체로 변환해 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            // toss에서 에러를 출력할 시 에러 반환
            if (rootNode.get("message").asText() != null) {
                throw new CustomTossResponseException(rootNode.get("message").asText());
            }

            // toss에서 정상 값을 반환할 시 값 추출
            String status = rootNode.get("status").asText();
            String orderId = rootNode.get("orderId").asText();
            String paymentKey = rootNode.get("paymentKey").asText();
            long amount = rootNode.get("amount").asLong();
            OffsetDateTime requestedAt = OffsetDateTime.parse(rootNode.get("requestedAt").asText());
            OffsetDateTime approvedAt = OffsetDateTime.parse(rootNode.get("approvedAt").asText());

            // toss 반환 값에 맞추어 response 생성, 반환
            return new PaymentGetResponse(PaymentStatus.valueOf(status), orderId, paymentKey, amount, requestedAt, approvedAt);
        } catch (IOException | InterruptedException ie) {   // 결제 조회 실패 시
            throw new RuntimeException(ie);
        }
    }

    // 결제 취소
    @Transactional
    public CancelPaymentResponse cancelPaymentService(PaymentCancelRequest request, String paymentKey, long userId) {

        Payment payment = paymentRepository.findPaymentByOrderId(paymentKey).orElseThrow(
                () -> new CustomException(ErrorMessage.NOT_FOUND_PAYMENT)
        );

        // payment 삭제 권한 검사해
        Long paymentUserId = payment.getOrder().getUser().getId();
        if (paymentUserId.equals(userId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        // 아직 승인되지 않은 결제일 경우
        if (payment.getStatus() == PaymentStatus.READY) {
            throw new CustomException(ErrorMessage.NOT_YET_CONFIRM);
        }

        // 이미 취소, 환불된 결제일 경우
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new CustomException(ErrorMessage.ALREADY_CANCELED_PAYMENT);
        }

        try {

            // 결제 취소 body 생성
            String tossCancelURL = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
            String body = String.format("""
                        {
                            "cancelReason": "%s"
                        }
                        """,
                    request.getCancelReason()
            );

            // 토스로 결제 취소 요청 보내
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(tossCancelURL))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 반환값 받으면 response(json형식)를 java객체로 변환해 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            // toss에서 에러를 출력할 시 에러 반환
            if (rootNode.get("message").asText() != null) {
                throw new CustomTossResponseException(rootNode.get("message").asText());
            }

            String status = rootNode.get("status").asText();
            String orderId = rootNode.get("orderId").asText();
            long amount = rootNode.get("amount").asLong();
            OffsetDateTime requestedAt = OffsetDateTime.parse(rootNode.get("requestedAt").asText());
            OffsetDateTime approvedAt = OffsetDateTime.parse(rootNode.get("approvedAt").asText());

            // toss 반환 값에 맞추어 결제 정보 업데이트
            payment.updateByResponse(PaymentStatus.valueOf(status), paymentKey, orderId, amount, requestedAt, approvedAt);
            return CancelPaymentResponse.from(payment);
        } catch(IOException | InterruptedException ie){
            throw new RuntimeException(ie);
        }
    }
}