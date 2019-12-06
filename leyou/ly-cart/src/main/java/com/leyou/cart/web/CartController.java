package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;

import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCarts(){
        return  ResponseEntity.ok(cartService.queryCarts());
    }
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestParam("id") Long skuId,@RequestParam("num") Integer num ){
          cartService.updateCart(skuId, num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable("id") Long skuId){
            cartService.deleteCart(skuId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("batch")
    public ResponseEntity<Void>  batchCart(@RequestParam("carts") List<Cart> carts){
        cartService.batchCart(carts);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
