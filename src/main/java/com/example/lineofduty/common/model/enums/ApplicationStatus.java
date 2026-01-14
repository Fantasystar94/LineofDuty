package com.example.lineofduty.common.model.enums;

public enum ApplicationStatus {
    CONFIRMED,  //완료
    CANCELED,   //취소
    PENDING,  //신청완료
    DEFERRED;   //연기

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
