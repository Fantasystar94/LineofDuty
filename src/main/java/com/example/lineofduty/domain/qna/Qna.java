package com.example.lineofduty.domain.qna;

import com.example.lineofduty.common.exception.ValidationMessage;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qnas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "userId", "title", "questionContent", "askContent", "viewCount", "createdAt", "modifiedAt"})
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

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    public Qna(@NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String title, @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String questionContent, User user) {
        this.title = title;
        this.questionContent = questionContent;
        this.user = user;
    }

    public Qna(String askContent) {
        this.askContent = askContent;
    }

    public void update(@NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String title, @NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String questionContent) {
        this.title = title;
        this.questionContent = questionContent;
    }

    public void createAnswer(@NotBlank(message = ValidationMessage.TITLE_CONTENT_NOT_BLANK) String askContent) {
        this.askContent = askContent;
    }

    public void updateAnswer(@NotBlank(message = ValidationMessage.ASK_CONTENT_NOT_BLANK) String askContent) {
        this.askContent = askContent;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
