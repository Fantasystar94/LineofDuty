package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.Qna;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "userId", "title", "questionContent", "askContent", "createdAt", "modifiedAt"})
public class QnaAdminAnswerUpdateResponse {


    private final Long id;
    private final Long userId;
    private final String title;
    private final String questionContent;
    private final String askContent;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static QnaAdminAnswerUpdateResponse from(Qna from) {
        return new QnaAdminAnswerUpdateResponse(
                from.getId(),
                from.getUser().getId(),
                from.getTitle(),
                from.getQuestionContent(),
                from.getAskContent(),
                from.getCreatedAt(),
                from.getModifiedAt()
        );
    }
}
