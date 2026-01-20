package com.example.lineofduty.domain.notice.dto.response;

import com.example.lineofduty.domain.notice.Notice;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "content", "createdAt", "modifiedAt"})
public class NoticeUpdateResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static NoticeUpdateResponse from(Notice notice) {
        return new NoticeUpdateResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getModifiedAt()
        );
    }
}
