package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.dto.QnaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QnaResisterResponse {

    private Long id;
    private Long userId;
    private String title;
    private String questionContent;
    private String askContent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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
