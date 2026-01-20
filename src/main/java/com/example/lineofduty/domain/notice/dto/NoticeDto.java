package com.example.lineofduty.domain.notice.dto;

import com.example.lineofduty.domain.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String modifiedAt;

    public static NoticeDto from(Notice notice) {
        return new NoticeDto(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt().toString(),
                notice.getModifiedAt().toString());
    }
}
