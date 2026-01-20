package com.example.lineofduty.domain.qna;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class QnaDto {

    private Long id;
    private Long userId;
    private String title;
    private String questionContent;
    private String askContent;
    private String createdAt;
    private String modifiedAt;

    public static QnaDto from(Qna qna) {
        return new QnaDto(
                qna.getId(),
                qna.getUser().getId(),
                qna.getTitle(),
                qna.getQuestionContent(),
                qna.getAskContent(),
                qna.getCreatedAt().toString(),
                qna.getModifiedAt().toString());
    }
}
