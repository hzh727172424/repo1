package com.leyou.order.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    UN_PAY(1,"未付款"),
    PAYED(2,"已付款，未发货"),
    UN_CONFIRM(3,"未确认收货"),
    SUCCESS(4,"已成功"),
    CLOSED(5,"已关闭"),
    RATED(6,"已评价")
    ;
    int code;
    String msg;
}
