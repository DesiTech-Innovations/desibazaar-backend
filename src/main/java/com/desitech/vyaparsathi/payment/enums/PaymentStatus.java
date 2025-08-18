package com.desitech.vyaparsathi.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDING, PARTIALLY_PAID, PAID;

    @JsonCreator
    public static PaymentStatus from(String value) {
        return value == null ? null : PaymentStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}