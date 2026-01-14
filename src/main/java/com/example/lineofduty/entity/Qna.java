package com.example.lineofduty.entity;

import com.example.lineofduty.common.exception.ValidationMessage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Qna extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "question_content", nullable = false)
    private String questionContent;

    @Column(name = "ask_content")
    private String askContent;

    public Qna(@NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String title, @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String questionContent, User user) {
        this.title = title;
        this.questionContent = questionContent;
        this.user = user;
    }

    public Qna(@NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String title, @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String questionContent) {
        super();
    }
}
