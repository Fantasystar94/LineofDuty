package com.example.lineofduty.domain.notice.dto.response;

import com.example.lineofduty.domain.notice.dto.NoticeDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "content", "createdAt", "modifiedAt"})
public class NoticeInquiryResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String createdAt;
    private final String modifiedAt;

    public NoticeInquiryResponse(NoticeDto from) {
        this.id = from.getId();
        this.title = from.getTitle();
        this.content = from.getContent();
        this.createdAt = from.getCreatedAt().toString();
        this.modifiedAt = from.getModifiedAt().toString();
    }


}
