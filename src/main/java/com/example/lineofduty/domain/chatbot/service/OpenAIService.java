package com.example.lineofduty.domain.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OpenAIService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String AI_MODEL = "gpt-3.5-turbo";

    // User-Agent 추가 (Cloudflare 우회)
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            당신은 대한민국 병역 관련 전문 상담 AI입니다.
            입영, 연기, 면제, 보충역 등 병역과 관련된 질문에 정확하고 친절하게 답변해주세요.
            
            주요 답변 영역:
            1. 입영 신청 절차 및 일정
            2. 입영 연기 신청 방법 (질병, 학업, 가족 사유 등)
            3. 입영 준비사항 및 준비물
            4. 병역판정검사 관련 사항
            5. 사회복무요원 관련 사항
            
            답변 시 다음을 지켜주세요:
            - 정확한 법적 정보 제공
            - 친절하고 이해하기 쉬운 설명
            - 필요시 관련 API 엔드포인트 안내
            - 불확실한 경우 관련 기관 문의 권장
            """;

    public OpenAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Async
    public CompletableFuture<String> generateResponseAsync(String userMessage) {
        return CompletableFuture.supplyAsync(() -> generateResponse(userMessage));
    }

    // 일반 채팅 응답 생성
    public String generateResponse(String userMessage) {
        try {
            // API 키 확인
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${OPENAI_API_KEY}")) {
                return getFallbackResponse(userMessage);
            }

            String requestBody = createRequestBody(userMessage);

            // User-Agent 포함한 요청 생성 (Cloudflare 우회)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("User-Agent", USER_AGENT)  // ← Cloudflare 우회
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                return getFallbackResponse(userMessage);
            }
        } catch (Exception e) {
            return getFallbackResponse(userMessage);
        }
    }

    private String createRequestBody(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", AI_MODEL);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", userMessage)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request body", e);
        }
    }

    private String parseResponse(String responseBody) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            return json.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Error parsing response", e);
            return "응답을 처리하는 중 오류가 발생했습니다.";
        }
    }

    private String getFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("입영 일정") || lowerMessage.contains("입영일정")
                || lowerMessage.contains("입영 신청") || lowerMessage.contains("입영날짜")
                || lowerMessage.contains("입영 날짜")) {
            return """
                    📅 **입영 일정 안내**
                    
                    상단 메뉴의 **[입영 일정]** 을 클릭하시면 달력 화면으로 이동합니다.
                    
                    **이용 방법:**
                    1. 달력에서 원하는 날짜를 클릭합니다.
                    2. 우측 패널에서 해당 날짜의 잔여 슬롯을 확인합니다.
                    3. **[이 날짜로 입영 신청]** 버튼을 눌러 신청을 완료합니다.
                    
                    ※ 잔여 인원이 있는 날짜만 신청 가능하며, 신청 전 로그인이 필요합니다.
                    ※ 신청 후 마이페이지에서 신청 내역을 확인하실 수 있습니다.
                    """;

        } else if (lowerMessage.contains("연기") || lowerMessage.contains("입영 연기")
                || lowerMessage.contains("연기 신청")) {
            return """
                    📋 **입영 연기 신청 안내**
                    
                    상단 메뉴의 **[연기 신청]** 을 클릭하시면 연기 신청 페이지로 이동합니다.
                    
                    **연기 가능 사유:**
                    - 질병·부상 (진단서 등 의료 서류 필요)
                    - 학업 (재학증명서 필요)
                    - 가족 사유 (가족관계증명서 등 필요)
                    - 기타 개인 사유
                    
                    **신청 절차:**
                    1. [연기 신청] 메뉴 접속
                    2. 연기 사유 선택 및 증빙 서류 업로드
                    3. 신청 제출 → 관리자 승인 대기
                    4. 승인 결과는 마이페이지 또는 이메일로 통보됩니다.
                    
                    ※ 연기는 정당한 사유가 있을 때만 가능하며, 허위 신청 시 불이익이 발생할 수 있습니다.
                    """;

        } else if (lowerMessage.contains("상품") || lowerMessage.contains("군장")
                || lowerMessage.contains("구매") || lowerMessage.contains("준비물")
                || lowerMessage.contains("장비") || lowerMessage.contains("용품")) {
            return """
                    🛒 **군장용품 상품 안내**
                    
                    상단 메뉴의 **[상품]** 을 클릭하시면 입영 전 필요한 물품을 구매하실 수 있습니다.
                    
                    **주요 상품 카테고리:**
                    - 세면도구 (칫솔, 치약, 비누 등)
                    - 의류·속옷
                    - 기타 개인 물품
                    
                    **구매 방법:**
                    1. [상품] 메뉴에서 원하는 상품 선택
                    2. **[장바구니에 담기]** 또는 **[바로 구매]**
                    3. 결제 후 [주문내역]에서 배송 현황 확인
                    
                    ※ 구매 전 반드시 입영통지서에 안내된 준비물 목록을 확인하세요.
                    ※ 장바구니는 상단 메뉴 [장바구니]에서 확인 가능합니다.
                    """;


        } else if (lowerMessage.contains("장바구니")) {
            return """
                    🛍️ **장바구니 안내**
                    
                    상단 메뉴의 **[장바구니]** 를 클릭하시면 담아둔 상품 목록을 확인할 수 있습니다.
                    
                    **이용 방법:**
                    1. [상품] 메뉴에서 원하는 상품을 장바구니에 담습니다.
                    2. [장바구니] 메뉴에서 수량 변경 및 상품 삭제가 가능합니다.
                    3. 최종 확인 후 결제를 진행합니다.
                    
                    ※ 장바구니는 로그인 후 이용 가능합니다.
                    """;

        } else if (lowerMessage.contains("주문") || lowerMessage.contains("주문내역")
                || lowerMessage.contains("배송") || lowerMessage.contains("구매내역")) {
            return """
                    📦 **주문내역 안내**
                    
                    상단 메뉴의 **[주문내역]** 을 클릭하시면 과거 주문 목록과 배송 현황을 확인할 수 있습니다.
                    
                    **확인 가능한 정보:**
                    - 주문 일자 및 주문 번호
                    - 구매 상품 목록 및 금액
                    - 배송 상태 (결제완료 / 배송중 / 배송완료)
                    
                    ※ 주문내역은 로그인 후 이용 가능합니다.
                    ※ 배송 관련 문의는 QnA 메뉴를 이용해주세요.
                    """;

        } else if (lowerMessage.contains("공지") || lowerMessage.contains("공지사항")
                || lowerMessage.contains("안내")) {
            return """
                    📢 **공지사항 안내**
                    
                    상단 메뉴의 **[공지사항]** 을 클릭하시면 병무청의 최신 공지 및 안내사항을 확인할 수 있습니다.
                    
                    **주요 공지 유형:**
                    - 시스템 점검 안내
                    - 병역 의무자 개인정보 변경 방법 안내
                    - 입영 전 준비사항 안내
                    - 제도 변경 및 정책 업데이트
                    
                    최신 공지를 정기적으로 확인하시어 중요한 정보를 놓치지 마세요.
                    """;

        } else if (lowerMessage.contains("qna") || lowerMessage.contains("문의")
                || lowerMessage.contains("질문") || lowerMessage.contains("궁금")) {
            return """
                    ❓ **QnA 안내**
                    
                    상단 메뉴의 **[QnA]** 를 클릭하시면 자주 묻는 질문을 확인하거나 1:1 문의를 남기실 수 있습니다.
                    
                    **QnA 이용 방법:**
                    1. [QnA] 메뉴 접속
                    2. 자주 묻는 질문에서 원하는 답변 검색
                    3. 해당 내용이 없으면 **1:1 문의 작성**
                    4. 담당자 답변은 마이페이지 또는 이메일로 전달됩니다.
                    
                    ※ 긴급한 사항은 병무청 콜센터(☎ 1588-9090)로 문의하시기 바랍니다.
                    """;

        } else if (lowerMessage.contains("마이페이지") || lowerMessage.contains("내 정보")
                || lowerMessage.contains("개인정보") || lowerMessage.contains("내정보")
                || lowerMessage.contains("신청내역") || lowerMessage.contains("로그인")) {
            return """
                    👤 **마이페이지 안내**
                    
                    우측 상단의 **[마이페이지]** 를 클릭하시면 개인 정보 및 신청 내역을 관리할 수 있습니다.
                    
                    **마이페이지에서 가능한 작업:**
                    - 내 정보 조회 및 수정 (주소, 연락처 등)
                    - 입영 신청 내역 확인
                    - 연기 신청 내역 및 승인 결과 확인
                    - 주문내역 바로가기
                    
                    ※ 마이페이지는 로그인 후 이용 가능합니다.
                    ※ 개인정보 변경 시 반드시 최신 정보로 업데이트해 주세요.
                    """;

        } else {
            return """
                    안녕하세요! 병무청 상담 AI입니다. 😊
                    
                    아래 메뉴에 대해 도움을 드릴 수 있습니다:
                    
                    - 🛒 **상품** - 군장용품 구매
                    - 📅 **입영 일정** - 입영 날짜 조회 및 신청
                    - 📋 **연기 신청** - 입영 연기 신청 절차
                    - 📢 **공지사항** - 병무청 공지 확인
                    - ❓ **QnA** - 질문 및 1:1 문의
                    - 🛍️ **장바구니** - 담아둔 상품 관리
                    - 📦 **주문내역** - 구매 내역 및 배송 확인
                    - 👤 **마이페이지** - 내 정보 및 신청 내역
                    
                    궁금하신 내용을 구체적으로 말씀해 주세요!
                    """;
        }
    }

    public Map<String, Object> createMetadata(int tokens, long responseTime) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tokens", tokens);
        metadata.put("responseTime", responseTime);
        metadata.put("model", AI_MODEL);
        return metadata;
    }
}