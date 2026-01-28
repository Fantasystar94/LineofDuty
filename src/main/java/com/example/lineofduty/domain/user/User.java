package com.example.lineofduty.domain.user;

import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ColumnDefault("0")
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDeleted;

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isDeleted = false;
    }

    public void updateProfile(String email, String username, String password) {

        if (email != null && !email.isEmpty()) {
            this.email = email;
        }

        if (username != null && !username.isEmpty()) {
            this.username = username;
        }

        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    public void updateIsDeleted() {
        this.isDeleted = true;
    }

}
