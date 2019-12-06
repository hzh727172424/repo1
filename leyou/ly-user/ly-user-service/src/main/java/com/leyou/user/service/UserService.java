package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;

import com.leyou.user.config.SmsProperties;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties(SmsProperties.class)
public class UserService {
    @Autowired
    private  SmsProperties prop;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RabbitTemplate  rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private  static final String KEY_PREFIX="user.verify.phone";
    //校验数据
    public Boolean checkUser(String data, Integer type) {
     User record =new User();
      /*  if (type==1){
            record.setUsername(data);
        }else if (type==2){
            record.setPhone(data);
        }else{
            throw new LyException(ExceptionEnum.USER_PARAM_ERROR);
        }*/
switch (type){
        case 1:
             record.setUsername(data);
             break;
        case 2:
            record.setPhone(data);
            break;
        default:
            throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
}

        return userMapper.selectCount(record)==0;
    }
//发送短信
    public void sendMsg(String phone) {
        String key=KEY_PREFIX+phone;
           Map<String,String> msg =new HashMap<>();
           msg.put("phone",phone );
        String code = NumberUtils.generateCode(6);
        msg.put("code", code);
        //发送消息
           rabbitTemplate.convertAndSend(prop.getExchange(),prop.getRoutingKey(),msg);
           //手机对应的验证码存入redis
           redisTemplate.opsForValue().set(key,code ,prop.getSaveTime(), TimeUnit.MINUTES);
    }
@Transactional
    public void register(User user, String code) {
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!code.equals(cacheCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        String salt = CodecUtils.generateSalt();
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);
        user.setCreated(new Date());
        user.setSalt(salt);
        int count = userMapper.insert(user);
        if (count!=1){
            throw  new LyException(ExceptionEnum.INSERT_USER_ERROR);
        }
    }
//查询用户
    public User queryUser(String username, String password) {
        User record=new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        if (user==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_OR_PASSWORD);
        }
        if (!StringUtils.equals(user.getPassword(),CodecUtils.md5Hex(password, user.getSalt()) )){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_OR_PASSWORD);
        }
        return user;
    }
}