package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    //新增订单
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }
    //根据订单id查询订单
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrder(@PathVariable("id")Long id){
        return  ResponseEntity.ok(orderService.queryOrder(id));
    }
    //查询url路径生成二维码
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long id){
        return  ResponseEntity.ok(orderService.createPayUrl(id));
    }
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id")Long orderId){
    return  ResponseEntity.ok(orderService.queryPayState(orderId).getValue());
    }
}
