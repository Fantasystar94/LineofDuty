package com.example.lineofduty.common.config;

import com.example.lineofduty.common.EmailSender;
import com.example.lineofduty.domain.enlistmentSchedule.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailSender emailSender;

    @Async
    @EventListener
    public void onNotification(NotificationEvent event) {
        emailSender.send(event);
    }

}
