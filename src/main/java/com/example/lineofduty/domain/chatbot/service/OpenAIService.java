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

        if (lowerMessage.contains("입영 신청")) {
            return """
                    입영 신청은 다음과 같은 절차로 진행됩니다:
                    
                    1. **입영 가능 일정 조회**: GET /api/enlistment API를 통해 가능한 입영일을 확인합니다.
                    2. **일정 선택**: 원하는 입영일을 선택합니다.
                    3. **신청**: POST /api/enlistment-applications API로 입영 신청을 합니다.
                    
                    더 자세한 정보가 필요하시면 관리자에게 문의해주세요.
                    """;
        } else if (lowerMessage.contains("입영 연기")) {
            return """
                    입영 연기 신청은 다음과 같이 진행됩니다:
                    
                    1. **연기 사유 선택**: 질병, 학업, 가족 사유, 개인 사유 등
                    2. **필요 서류 준비**: 사유에 따른 증빙 서류
                    3. **시스템 신청**: POST /api/deferments API를 통해 연기 신청
                    4. **관리자 승인 대기**
                    
                    연기는 정당한 사유가 있을 때만 가능하며, 관리자의 승인이 필요합니다.
                    """;
        } else if (lowerMessage.contains("입영 일정") || lowerMessage.contains("입영 날짜")) {
            return """
                    입영 일정 조회는 다음과 같이 진행됩니다:
                    
                    1. **전체 일정 조회**: GET /api/enlistment API로 입영 가능한 전체 일정을 확인할 수 있습니다.
                    2. **기간별 조회**: GET /api/enlistment/search?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD로 특정 기간의 일정을 조회할 수 있습니다.
                    3. **단건 조회**: GET /api/enlistment/{scheduleId}로 특정 일정의 상세 정보를 확인할 수 있습니다.
                    
                    각 일정의 남은 슬롯 수도 함께 확인하실 수 있습니다.
                    """;
        } else if (lowerMessage.contains("준비물") || lowerMessage.contains("준비")) {
            return """
                    입영 시 준비물은 다음과 같습니다:
                    
                    **필수 지참물**:
                    - 주민등록증 또는 운전면허증
                    - 입영통지서
                    - 도장 (인감도장 권장)
                    - 신분증 사진 3매
                    
                    **개인 물품** (선택):
                    - 세면도구 (칫솔, 치약, 비누 등)
                    - 속옷 2~3벌
                    - 현금 (소액)
                    
                    상세한 준비물 목록은 입영통지서를 참고해주세요.
                    """;
        } else {
            return """
                    안녕하세요! 병역 관련 상담 AI입니다.
                    
                    다음과 같은 내용에 대해 도움을 드릴 수 있습니다:
                    - 입영 신청 방법
                    - 입영 연기 절차
                    - 입영 일정 조회
                    - 준비물 안내
                    
                    궁금하신 내용을 구체적으로 말씀해주세요!
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