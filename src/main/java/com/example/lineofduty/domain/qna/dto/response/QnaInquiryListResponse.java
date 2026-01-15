package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.dto.QnaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class QnaInquiryListResponse {

    private final List<QnaDto> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;

    public static QnaInquiryListResponse from(Page<QnaDto> page) {
        return new QnaInquiryListResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}
