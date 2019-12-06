package com.leyou.order.web;

import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    private OrderService orderService;
    //回调
    @PostMapping(value = "pay",produces = "application/xml")
    public String handleNotify(@RequestBody Map<String,String> result){
        orderService.handleNotify(result);
        log.info("[微信回调]  接收微信支付回调");
        return "<xml>\n" +
                "\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }
//    @GetMapping("{id}")
//    public String hello(@PathVariable("id") Long id){
//        return "id"+id;
//    }
}
