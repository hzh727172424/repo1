package com.leyou.gateway.filter;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})

public class AuthFilter extends ZuulFilter {
    @Autowired
    private JwtProperties prop;
    @Autowired
    private  FilterProperties fprop;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        //获取request
        RequestContext ctx= RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String path = request.getRequestURI();
        System.out.println(path);
        //判断是否放行

        return   !isAllowPath(path);
    }

    private Boolean isAllowPath(String path) {
        List<String> allowPaths = fprop.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (path.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取request
        RequestContext ctx= RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        //解析token
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
        } catch (Exception e) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}
