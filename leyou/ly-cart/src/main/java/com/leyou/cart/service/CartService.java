package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private  static final String KEY_PREFIX="cart:uid:";
    //新增购物车功能
    @Transactional
    public void addCart(Cart cart) {
        //获取user
        UserInfo user = UserInterceptor.getUser();
        String key=KEY_PREFIX+user.getId().toString();
        String hasKey=cart.getSkuId().toString();
        int num=cart.getNum() ;
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断商品的数据是否存在
        if (operations.hasKey(hasKey)){
            System.out.println(hasKey);
            String  json = operations.get(hasKey).toString();
             cart = JsonUtils.toBean(json, Cart.class);
             cart.setNum(cart.getNum()+num);
            System.out.println(cart);
        }
        operations.put(hasKey,JsonUtils.toString(cart) );
    }
//查询购物车商品
    public List<Cart> queryCarts() {
        //线程获取user
        UserInfo user = UserInterceptor.getUser();
        String key=KEY_PREFIX+user.getId().toString();
        if(!redisTemplate.hasKey(key)){
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> carts = operations.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());
        return carts;
    }
//修改购物车商品
    @Transactional
    public void updateCart(Long skuId, Integer num) {
        UserInfo user = UserInterceptor.getUser();
        String key=KEY_PREFIX+user.getId().toString();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);
        operations.put(skuId.toString(), JsonUtils.toString(num));
    }
//删除购物车商品
    public void deleteCart(Long skuId) {
        UserInfo user = UserInterceptor.getUser();
        String key =KEY_PREFIX+user.getId().toString();
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }
//合并购物车商品
    @Transactional
    public void batchCart(List<Cart> carts) {
        UserInfo user = UserInterceptor.getUser();
        String key =KEY_PREFIX+user.getId().toString();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);

        for (Cart cart : carts) {
            String skuId = cart.getSkuId().toString();
            if (operations.hasKey(skuId)){
                String c = operations.get(skuId).toString();
                Cart redisCart = (Cart) JsonUtils.toBean(c, Cart.class);
                cart.setNum(cart.getNum()+redisCart.getNum());
         }
         operations.put(skuId,JsonUtils.toString(cart) );
        }
    }
}
