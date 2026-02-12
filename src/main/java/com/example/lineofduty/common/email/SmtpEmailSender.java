package com.example.lineofduty.common.email;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.enlistmentSchedule.NotificationEvent;
import com.example.lineofduty.domain.enlistmentSchedule.NotificationType;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class SmtpEmailSender {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(NotificationEvent event) {
        User user = userRepository.findById(event.userId()).orElseThrow(()-> new CustomException(ErrorMessage.USER_NOT_FOUND));

        String to = user.getEmail();
        String subject = buildSubject(event.type());
        String defaultFooter = """
                \n
                감사합니다.
                ※ 본 메일은 발신 전용입니다. 문의가 필요하신 경우 고객센터를 이용해 주세요.
                """;

        String body = buildBody(event);
        String fullSentence = body + defaultFooter;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(fullSentence);

        javaMailSender.send(msg);
    }

    @Async
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[LineOfDuty] 회원가입 인증 코드 안내");
        msg.setText("인증 코드: " + code + "\n\n3분 이내에 입력해 주세요.");

        javaMailSender.send(msg);
    }

    private String buildSubject(NotificationType type) {
        return switch (type) {
            case ENLISTMENT_REQUESTED -> "[LineOfDuty] 입영 신청 접수 완료";
            case ENLISTMENT_CONFIRMED -> "[LineOfDuty] 입영 신청 확정";
            case DEFERMENT_APPROVED -> "[LineOfDuty] 연기 신청 승인";
            case DEFERMENT_REJECTED -> "[LineOfDuty] 연기 신청 반려";
            case DEFERMENT_REQUEST -> "[LineOfDuty] 연기 신청 요청 완료";
        };
    }

    private String buildBody(NotificationEvent event) {
        return switch (event.type()) {
            case ENLISTMENT_REQUESTED ->
                    "입영 신청이 정상 접수되었습니다.\n신청번호 : " + event.applicationId() +"\n" + "입영 날짜 :" + event.enlistmentDate();
            case ENLISTMENT_CONFIRMED ->
                    "입영 신청이 승인되어 확정되었습니다.\n신청번호 : " + event.applicationId()+ "\n" + "입영 연기 신청번호" + event.defermentId() + "\n입영 날짜 :" + event.enlistmentDate();
            case DEFERMENT_APPROVED ->
                    "연기 신청이 승인되었습니다.\n연기번호 : " + event.defermentId() +"\n" + "입영 날짜 : " + event.enlistmentDate() +"\n입영 연기 신청번호 : " + event.defermentId() + "\n변경된 입영 날짜 : " + event.changeDate();
            case DEFERMENT_REJECTED ->
                    "연기 신청이 반려되었습니다.\n연기번호 : " + event.defermentId() +"\n" + "입영 날짜 :" + event.enlistmentDate() +"\n입영 연기 신청번호 : " + event.defermentId() + "\n변경된 입영 날짜 : " + event.changeDate();
            case DEFERMENT_REQUEST ->
                    "연기 신청 요청이 접수되었습니다.\n연기번호 : " + event.defermentId() + "\n" + "입영 날짜 :" + event.enlistmentDate() + "\n변경될 날짜 : " + event.changeDate();
        };
    }
}
