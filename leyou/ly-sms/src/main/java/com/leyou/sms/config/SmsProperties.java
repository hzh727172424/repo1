package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ly.sms")
public class SmsProperties {
    private String accessKeyId;
    private String accessKeysecret;
    private String SignName;
    private String TemplateCode;
}
