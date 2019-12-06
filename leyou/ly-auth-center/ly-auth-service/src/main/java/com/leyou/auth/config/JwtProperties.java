package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix =   "ly.jwt")
public class JwtProperties {
    private  String secret;
    private String  pubKeyPath;
    private String  priKeyPath;
    private int expire;
    private String cookieName;
   private PublicKey publicKey;
    private PrivateKey privateKey;
     @PostConstruct
    public void init() throws Exception {
         File pubPath=new File(pubKeyPath);
         File priPath=new File(priKeyPath);
         if (!priPath.exists()||!priPath.exists()){
             RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
         }
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
         this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
     }
}
