package com.paymilli.paymilli.domain.payment.infrastructure.entity;

public enum PaymentStatus {
    PAYMENT("payment"), REFUND("refund");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
