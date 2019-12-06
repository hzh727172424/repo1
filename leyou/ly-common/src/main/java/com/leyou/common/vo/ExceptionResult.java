package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data

public class ExceptionResult {
    private int code;
    private String msg;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum exceptionEnum) {
        this.code=exceptionEnum.getCode();
        this.msg=exceptionEnum.getMsg();
        this.timestamp=System.currentTimeMillis();
    }
}
