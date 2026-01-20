package com.example.lineofduty.domain.notice.dto.response;

import com.example.lineofduty.domain.notice.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeInquiryListResponse {

    private final List<NoticeDto> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;


    public static NoticeInquiryListResponse from(Page<NoticeDto> page) {
        return new NoticeInquiryListResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}
