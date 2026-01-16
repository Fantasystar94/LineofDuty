package com.example.lineofduty.domain.qna.dto.request;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaUpdateRequest {


    @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK)
    private String title;
    @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK)
    private String questionContent;

}
