package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;
    //用户登录授权
    public String queryUser(String username, String password) {
        User user = userClient.queryUser(username, password);
        if (user==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_OR_PASSWORD);
        }
        String token = null;
        try {
            token = JwtUtils.generateToken(new UserInfo(user.getId(), username), prop.getPrivateKey(), prop.getExpire());
        } catch (Exception e) {
          log.error("[用户授权中心] 用户凭证生成失败{}" +e);
          throw  new LyException(ExceptionEnum.CREATE_TOKEN_ERROR);
        }
        return token;
    }
}
