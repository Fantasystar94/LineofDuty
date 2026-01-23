package com.example.lineofduty.domain.payment;

public enum PaymentStatus {
    READY("READY"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    CANCELED("CANCELED"),
    ABORTED("ABORTED");

    private String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
