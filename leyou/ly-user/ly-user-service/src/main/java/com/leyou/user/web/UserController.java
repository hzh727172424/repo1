package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    /**
     * 校验用户数据
     */
    @Autowired
    private UserService userService;
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
            return  ResponseEntity.ok(userService.checkUser(data,type));
    }
    /**
     * 发送短信验证码
     */
    @PostMapping("code")
    public  ResponseEntity<Void> sendMsg(@RequestParam("phone") String phone){
        userService.sendMsg(phone);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //注册用户
    @PostMapping("register")
    public  ResponseEntity<Void> registerUser(User user,@RequestParam("code") String code){
        userService.register(user,code);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //查询用户
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,@RequestParam("password")String password){
        return   ResponseEntity.ok( userService.queryUser(username,password)) ;

    }
}
