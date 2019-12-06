package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties("ly.jwt")
@Data
public class JwtProperties {
    private  String pubKeyPath;
    private  String cookieName;
    private PublicKey publicKey;
    @PostConstruct
    public void init() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
