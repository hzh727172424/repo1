package com.leyou.order.utils;


import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.config.WxPayConfiguration;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.wxpay.sdk.WXPayConstants.FAIL;
import static com.github.wxpay.sdk.WXPayConstants.SUCCESS;

@Component
@Slf4j
public class PayHelper {
    @Autowired
    private PayConfig config;
    @Autowired
    private WXPay wxpay;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    public String createOrder(Long orderId, Long totalPay,String desc) {
        try {
        Map<String, String> data = new HashMap<String, String>();
        //商品描述
        data.put("body",desc);
        //订单id
        data.put("out_trade_no", orderId.toString());
      //标价金额
        data.put("total_fee", totalPay.toString());
        data.put("spbill_create_ip", "127.0.0.1");
        data.put("notify_url", config.getNotifyUrl());
        data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
            //判断返回码
            isSuccess(resp);
            return resp.get("code_url");
        } catch (Exception e) {
            log.error("[微信支付] 创建订单交易异常，失败原因{}"+e);
            return null;
        }
    }

    public  void isSuccess(Map<String, String> resp) {
        if (FAIL.equalsIgnoreCase(resp.get("return_code"))){
            log.error("[微信支付] 微信下单通信失败，失败原因{}"+resp.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_ERROR);
        }
        if (FAIL.equalsIgnoreCase(resp.get("result_code"))){
            log.error("[微信支付] 微信下单结果失败，失败原因{}"+resp.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_ERROR);
        }
    }
//验证签名
    public void isValidSign(Map<String, String> result) {
        try {
            String sign1 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.MD5);
            String sign2 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign=result.get("sign");
            if (!StringUtils.equals(sign,sign1 )&&!StringUtils.equals(sign, sign2)){
                throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
            }
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
        }
    }

    public PayState queryOrderPayState(Long orderId) {
        try {
            //查询订单
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", orderId.toString());
            Map<String, String> result = wxpay.orderQuery(data);
            isSuccess(result);
            isValidSign(result);
            //校验金额
            String total_fee = result.get("total_fee");
            if (StringUtils.isBlank(total_fee) ){
                throw new LyException(ExceptionEnum.INVALID_TOTALPAY_ERROR);
            }
            Order order = orderService.queryOrder(orderId);
            Long actualPay = /*order.getActualPay()*/1L;
            if (actualPay!=Long.valueOf(total_fee)){
                throw new LyException(ExceptionEnum.INVALID_TOTALPAY_ERROR);
            }
            String state = result.get("trade_state");
            System.out.println("交易状态"+state);
            if (SUCCESS.equals(state)){
                //修改状态
                OrderStatus orderStatus=new OrderStatus();
                orderStatus.setStatus(OrderStatusEnum.PAYED.getCode());
                orderStatus.setOrderId(orderId);
                orderStatus.setPaymentTime(new Date());
                int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
                if (count!=1){
                    throw new LyException(ExceptionEnum.UPDATA_ORDER_STATUS_ERROR);
                }
                return  PayState.SUCCESS;
                }

            if ("NOTPAY".equals(state)||"USERPAYING".equals(state)){
                    return PayState.NOTPAY;
                }
                return  PayState.FAIL;
        } catch (Exception e) {
                    log.error("[支付状态] 订单支付状态查询功能异常{}"+e);
                   return PayState.NOTPAY;
        }
    }
}