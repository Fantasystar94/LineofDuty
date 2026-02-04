package com.example.lineofduty.domain.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendRequest {

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 2000, message = "메시지는 1자 이상 2000자 이하여야 합니다.")
    private String content;
}