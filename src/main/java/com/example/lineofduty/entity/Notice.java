package com.example.lineofduty.entity;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;


    public Notice(@NotBlank(message = ValidationMessage.NOTICE_TITLE_CONTENT_NOT_BLANK) String title, @NotBlank(message = ValidationMessage.NOTICE_TITLE_CONTENT_NOT_BLANK) String content, User user) {
        this.title = title;
        this.content = content;
        this.author = user;

    }
}
