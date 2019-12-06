package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptor.UserIterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
     private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;
@Autowired
private  PayHelper payHelper;
@Autowired
 private StringRedisTemplate redisTemplate;
    private  static final String KEY_PREFIX="cart:uid:";
    //新增订单业务
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        //1新增订单
        //1.1生成订单Id
        long orderId = idWorker.nextId();
        //1.2订单基本信息
        Order order=new Order();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        //1.3用户信息
        UserInfo user = UserIterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerRate(false);
        order.setBuyerNick(user.getUsername());
        //1.4收货信息
        AddressDTO address = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(address.getName());
        order.setReceiverAddress(address.getAddress());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverMobile(address.getPhone());
        order.setReceiverState(address.getState());
        order.setReceiverZip(address.getZipCode());
        //1.5金额信息
        List<CartDTO> carts = orderDTO.getCarts();
        Map<Long, Integer> map = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set<Long> skuIds = map.keySet();
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(skuIds));
        Long totalPay=0L;
        //封装订单详情
        List<OrderDetail> details=new ArrayList<>();
        for (Sku sku : skus) {
            //订单总价格
            totalPay+=sku.getPrice()*map.get(sku.getId());
            //订单详情信息
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(map.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //订单实际价格是总价+邮费-优惠
        order.setActualPay(totalPay+order.getPostFee()-0);
        //将order存入数据库
        int count = orderMapper.insertSelective(order);
        if (count!=1){
            log.error("[订单服务] 创建订单失败,orderId:{}",orderId);
            throw new LyException(ExceptionEnum.INSERT_ORDER_ERROR);
        }
        // 2 订单详情
         count = orderDetailMapper.insertList(details);
        if (count!=details.size()){
            log.error("[订单服务] 创建订单详情失败,details:{}",details);
            throw new LyException(ExceptionEnum.INSERT_ORDER_DETAIL_ERROR);
        }
        //3 订单状态
        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(new Date());
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.getCode());
        orderStatusMapper.insert(orderStatus);
        if (count!=1){
            log.error("[订单服务] 创建订单状态失败,rderId:{}",orderId);
            throw new LyException(ExceptionEnum.INSERT_ORDER_ERROR);
        }

         //减少库存
        goodsClient.decreseStock(carts);
        return orderId;
    }
//根据订单ID查询订单
    public Order queryOrder(Long id) {

        Order order = orderMapper.selectByPrimaryKey(id);
        if (order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        // 查询订单详情
        OrderDetail orderDetail=new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        //查询订单状态
        OrderStatus status = orderStatusMapper.selectByPrimaryKey(id);
        if (status==null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        //订单添加详情和状态
        order.setOrderDetails(details);
        order.setOrderStatus(status);
        return order;
    }
//查询二维码地址
    public String createPayUrl(Long id) {
        Order order = queryOrder(id);
        Integer status = order.getOrderStatus().getStatus();
        if (status!=1){
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }
        Long actualPay = 1L;
        OrderDetail detail = order.getOrderDetails().get(0);
        String desc = detail.getTitle();
        String url = payHelper.createOrder(id, actualPay, desc);
        return url;
    }
//微信支付回调信息处理
    public void handleNotify(Map<String, String> result) {
        //判断下单通信是否成功
        payHelper.isSuccess(result);
        //验证签名
        payHelper.isValidSign(result);
        //校验金额
        String total_feeStr = result.get("total_fee");
        if (StringUtils.isBlank(total_feeStr)){
            throw new LyException(ExceptionEnum.INVALID_TOTALPAY_ERROR);
        }
        Long totalFee = Long.valueOf(total_feeStr);
        String trade_no = result.get("out_trade_no");
        Long orderId = Long.valueOf(trade_no);
        Order order = queryOrder(orderId);
        if (totalFee!=/*order.getActualPay()*/1L){
            throw new LyException(ExceptionEnum.INVALID_TOTALPAY_ERROR);
        }
        //修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setPaymentTime(new Date());
        orderStatus.setStatus(OrderStatusEnum.PAYED.getCode());
        int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if (count!=1){
            throw  new LyException(ExceptionEnum.UPDATA_ORDER_STATUS_ERROR);
        }
        log.info("[订单回调] 订单 支付成功订单编号：{}"+orderId);
    }
//查询支付状态
    public PayState queryPayState(Long orderId) {
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        System.out.println(orderStatus.getStatus());
        if (orderStatus.getStatus()!=OrderStatusEnum.UN_PAY.getCode()) {
            //如果订单状态不等于1则已经支付完成
            return PayState.SUCCESS;
        }
            return    payHelper.queryOrderPayState(orderId);
    }
}
