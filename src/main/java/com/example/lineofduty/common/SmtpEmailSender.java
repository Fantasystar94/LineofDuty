package com.example.lineofduty.common;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.NotificationEvent;
import com.example.lineofduty.domain.enlistmentSchedule.NotificationType;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    @PostConstruct
    public void testMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("wonminyung94@gmail.com");
        message.setSubject("LineOfDuty 테스트 메일");
        message.setText("메일 전송 테스트입니다.");

        javaMailSender.send(message);
    }

    @Override
    public void send(NotificationEvent event) {
        User user = userRepository.findById(event.userId()).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));

        String to = user.getEmail();
        String subject = buildSubject(event.type());
        String body = buildBody(event);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        javaMailSender.send(msg);
    }

    private String buildSubject(NotificationType type) {
        return switch (type) {
            case ENLISTMENT_REQUESTED -> "[LineOfDuty] 입영 신청 접수 완료";
            case ENLISTMENT_CONFIRMED -> "[LineOfDuty] 입영 신청 확정";
            case DEFERMENT_APPROVED -> "[LineOfDuty] 연기 신청 승인";
            case DEFERMENT_REJECTED -> "[LineOfDuty] 연기 신청 반려";
        };
    }

    private String buildBody(NotificationEvent event) {
        return switch (event.type()) {
            case ENLISTMENT_REQUESTED ->
                    "입영 신청이 정상 접수되었습니다.\n신청번호: " + event.applicationId();
            case ENLISTMENT_CONFIRMED ->
                    "입영 신청이 승인되어 확정되었습니다.\n신청번호: " + event.applicationId();
            case DEFERMENT_APPROVED ->
                    "연기 신청이 승인되었습니다.\n연기번호: " + event.defermentId();
            case DEFERMENT_REJECTED ->
                    "연기 신청이 반려되었습니다.\n연기번호: " + event.defermentId();
        };
    }
}
