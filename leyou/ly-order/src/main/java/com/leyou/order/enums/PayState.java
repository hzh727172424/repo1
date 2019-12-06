package com.leyou.order.enums;

public enum PayState {
    NOTPAY(0),SUCCESS(1),FAIL(2)
    ;
    PayState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

     int value;
}
