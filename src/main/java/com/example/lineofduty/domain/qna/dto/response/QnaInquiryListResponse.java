package com.example.lineofduty.domain.qna.dto.response;

import com.example.lineofduty.domain.qna.Qna;
import com.example.lineofduty.domain.qna.QnaDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"content", "totalElements", "totalPages", "size", "number"})
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
