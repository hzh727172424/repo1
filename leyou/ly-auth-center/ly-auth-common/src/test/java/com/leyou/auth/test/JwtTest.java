package com.leyou.auth.test;

import com.leyou.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;


public class JwtTest {
    private  static final String priKeyPath="F:\\tmp\\rsa\\rsa.pub";
    private  static final String pubKeyPath="F:\\tmp\\rsa\\rsa.pub";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }


}
