package com.example.lineofduty.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String email;
    private String username;
    private String password;
}