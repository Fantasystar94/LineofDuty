package com.example.lineofduty.domain.notice.dto.request;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResisterRequest {

    @NotBlank(message = ValidationMessage.NOTICE_TITLE_CONTENT_NOT_BLANK)
    private String title;
    @NotBlank(message = ValidationMessage.NOTICE_TITLE_CONTENT_NOT_BLANK)
    private String content;

}
