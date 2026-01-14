package com.example.lineofduty.model.enums;

public enum ApplicationStatus {
    COMPLETED,
    CANCELED,
    CONFIRMED,
    DEFERRED;

    public enum OrderStatus {
        ORDERED,
        CANCELED,
        DELIVERED
    }

    public enum PaymentStatus {
        READY,
        COMPLETED,
        FAILED
    }

    public enum ProductStatus {
        ON_SALE,
        SOLD_OUT
    }
}
