package com.leyou.order.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserIterceptor implements HandlerInterceptor {
    private JwtProperties prop;
    private  static  final ThreadLocal<UserInfo> tl=new ThreadLocal();
    public UserIterceptor(JwtProperties prop) {
        this.prop=prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            tl.set(userInfo);
            return true;
        } catch (Exception e) {
            log.error("[订单服务]  解析用户信息失败"+e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        tl.remove();
    }
    public static UserInfo getUser(){
      return   tl.get();
    }
}
