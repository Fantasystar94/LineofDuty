package com.example.lineofduty.domain.token;

import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token", nullable = false)
    private String token;

    public RefreshToken(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
