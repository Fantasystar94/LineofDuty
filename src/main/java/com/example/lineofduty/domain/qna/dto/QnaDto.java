package com.example.lineofduty.domain.qna.dto;

import com.example.lineofduty.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QnaDto {

    private Long id;
    private Long userId;
    private String title;
    private String questionContent;
    private String askContent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static QnaDto from(Qna qna) {
        return new QnaDto(
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
