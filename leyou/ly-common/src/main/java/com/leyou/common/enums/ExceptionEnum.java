package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnum {
    PRICE_COUNNT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    BRAND_NOT_FOUND(404, "品牌没找到"),
    INSERT_BRAND_ERROR(500,"新增商品失败" ),
    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(400, "无效的文件类型"),
    CATEGORY_ID_NOT_FOUND(404,"商品分类ID查不到"), SPECGROUP_NOT_FOUND(404, "商品规格分组找不到"),
    SPECPARAM_NOT_FOUND(404, "商品规格参数找不到"), GOODS_NOT_FOUND(404, "商品找不到"),
    GOODS_SAVE_ERROR(500,"商品新增失败"),
    GOODS_SPUDETAIL_NOT_FOUND(404, "商品详情找不到"),
    GOODS_SKU_NOT_FOUND(404, "商品Sku不存在"),
    UPDATE_GOODS_SPU_ERROR(500, "商品Spu信息更新失败"),
    UPDATE_GOODS_SPU_DETAIL_ERROR(500, "商品详情信息更新失败"),
    GOODS_SPU_ID_CANNOT_BE_NULL(400, "商品ID不能为空"),
    INVALID_USER_DATA_TYPE(400,"用户数据类型有误"),
    INVALID_VERIFY_CODE(400,"无效的验证码"),
    INSERT_USER_ERROR(500,"新增用户错误"),
    INVALID_USERNAME_OR_PASSWORD(400,"用户名或密码错误"),
    CREATE_TOKEN_ERROR(500,"用户凭证生成失败"),
    UNAUTHORIZED(403,"未授权"),
    CART_NOT_FOUND(404,"购物车商品查询不到"),
    INSERT_ORDER_ERROR(500,"订单新增失败"),
    INSERT_ORDER_DETAIL_ERROR(500,"订单详情新增失败"),
    UPDATE_DECRESE_STOCK_ERROR(500,"减少库存失败"),
    ORDER_NOT_FOUND(500,"订单查不到"),
    ORDER_DETAIL_NOT_FOUND(500,"订单详情查不到"),
    ORDER_STATUS_NOT_FOUND(500,"订单状态查不到"),
    WX_PAY_ORDER_ERROR(500,"微信支付订单失败"),
    ORDER_STATUS_ERROR(500,"微信支付订单失败"),
    INVALID_SIGN_ERROR(500,"无效签名异常"),
    INVALID_TOTALPAY_ERROR(500,"无效的金额参数异常"),
    UPDATA_ORDER_STATUS_ERROR(500,"修改订单状态失败")
    ;
    private int code;
    private String msg;
}
