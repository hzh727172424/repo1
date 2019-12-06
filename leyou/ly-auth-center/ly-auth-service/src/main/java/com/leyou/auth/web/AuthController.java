package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PublicKey;

@RestController
@Slf4j
public class AuthController {
    @Value("${ly.jwt.cookieName}")
    private String cookieName;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties prop;
    //用户登录授权
    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password, HttpServletResponse response, HttpServletRequest request) throws Exception {
          String token=authService.queryUser(username,password);
        CookieUtils.newBuilder(response).request(request).httpOnly().build(cookieName, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //校验用户登录状态
    @GetMapping("verify")
    public ResponseEntity<UserInfo>  verify(@CookieValue("LY_TOKEN") String token,HttpServletRequest request,HttpServletResponse response){
        try {
            //解析token获取userinfo
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //再次获取token
            String tokenNew = JwtUtils.generateToken(user, prop.getPrivateKey(), prop.getExpire());
            //重新写入来刷新时长
            CookieUtils.newBuilder(response).request(request).httpOnly().build(cookieName, tokenNew);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }

}
