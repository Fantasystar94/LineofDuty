package com.example.lineofduty.common;

import com.example.lineofduty.domain.enlistmentSchedule.NotificationEvent;

public interface EmailSender {
    void send(NotificationEvent event);
}
