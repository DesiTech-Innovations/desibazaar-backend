package com.desitech.vyaparsathi.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH, CARD, UPI, NET_BANKING, CHEQUE, OTHER;

    @JsonCreator
    public static PaymentMethod from(String value) {
        return value == null ? null : PaymentMethod.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}