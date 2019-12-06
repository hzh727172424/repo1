package com.leyou.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void sendTest(){
        Map<String,String> msg=new HashMap<>();
        msg.put("phone",  "13868595965");
        msg.put("code","1235" );
        rabbitTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
    }
}
