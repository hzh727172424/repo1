package com.leyou.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ly.sms")
public class SmsProperties {
    private String exchange;
    private String routingKey;
    private Integer saveTime;
}
