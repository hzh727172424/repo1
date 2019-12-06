package com.leyou.sms.utils;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {
    @Autowired
    private SmsProperties prop;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private  static final String KEY_PREFIX="sms.phone";
        public void sendSms(String phone,String signName,String templateCode,String templateParam){
            String key=KEY_PREFIX+phone;
            String lastTime = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(lastTime)){
                log.info("[短信服务] 短信发送频率过高");
                return;
            }
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", prop.getAccessKeyId(), prop.getAccessKeysecret());
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", templateParam);
            try {
                CommonResponse response = client.getCommonResponse(request);

                Map<String,String> map = JSONObject.parseObject(response.getData(), Map.class);
                String message = map.get("Message");
                if (!"Ok".equalsIgnoreCase(message)){
                    log.info("[短信服务] 短信发送失败");
                }else {
                    log.info("[短信服务] 短信发送成功");
                    redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),1 , TimeUnit.MINUTES);
                }
                System.out.println(response.getData());
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

}
