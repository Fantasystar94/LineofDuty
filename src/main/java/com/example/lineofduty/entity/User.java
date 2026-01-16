package com.example.lineofduty.entity;

import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.common.util.AesUtil;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "resident_number", nullable = false)
    @Convert(converter = AesUtil.ResidentNumberConverter.class)
    private String residentNumber;

    public User(String username, String email, String password, Role role, String residentNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.residentNumber = residentNumber;
    }

}
