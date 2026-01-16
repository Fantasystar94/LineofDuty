package com.example.lineofduty.domain.qna.dto.request;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QnaResisterRequest {

    @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK)
    private String title;
    @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK)
    private String questionContent;

}
