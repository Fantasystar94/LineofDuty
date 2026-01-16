package com.example.lineofduty.domain.notice.dto.response;

import com.example.lineofduty.domain.notice.dto.NoticeDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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


    public NoticeUpdateResponse(NoticeDto from) {
                this.id = from.getId();
                this.title = from.getTitle();
                this.content = from.getContent();
                this.createdAt = from.getCreatedAt();
                this.modifiedAt = from.getModifiedAt();
    }
}
