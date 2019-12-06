package com.leyou.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        //添加CORS配置
        CorsConfiguration config =new CorsConfiguration();
        //允许的域，不要写* 否则COOKIE无法使用
        config.addAllowedOrigin("http://manage.leyou.com");config.addAllowedOrigin("http://www.leyou.com");
        //是否发送cookie信息
        config.setAllowCredentials(true);
        //允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("PUT");
        //允许的头
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);
        //添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource  configSource=new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }
}
