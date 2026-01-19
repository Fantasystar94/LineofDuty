package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.Qna;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "userId", "title", "questionContent", "askContent", "createdAt", "modifiedAt"})
public class QnaResisterResponse {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String questionContent;
    private final String askContent;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static QnaResisterResponse from(Qna qna) {
        return new QnaResisterResponse(
                qna.getId(),
                qna.getUser().getId(),
                qna.getTitle(),
                qna.getQuestionContent(),
                qna.getAskContent(),
                qna.getCreatedAt(),
                qna.getModifiedAt()
        );
    }
}
