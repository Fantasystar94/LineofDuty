package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.dto.QnaDto;
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

    public QnaResisterResponse(QnaDto from) {
        this.id = from.getId();
        this.userId = from.getUserId();
        this.title = from.getTitle();
        this.questionContent = from.getQuestionContent();
        this.askContent = from.getAskContent();
        this.createdAt = from.getCreatedAt();
        this.modifiedAt = from.getModifiedAt();
    }
}
