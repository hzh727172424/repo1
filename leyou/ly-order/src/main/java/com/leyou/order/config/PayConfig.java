package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.*;

@Data
public class PayConfig implements WXPayConfig{
    private  String appID;//公众账号ID
    private  String mchID;//商户号
    private String key;//生成签名密钥
    private int httpConnectTimeoutMs;//连接超时
    private int httpReadTimeoutMs;//读取超时
    private  String notifyUrl;//通知url

    public InputStream getCertStream() {
      return null;
    }


}