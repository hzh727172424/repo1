package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetail {
    @Id
    private Long spuId;//spuId
    private  String description;//商品描述
    private  String genericSpec;//规格
    private  String specialSpec;//规格模板
    private  String packingList;//包装清单
    private  String afterService;//收货服务
}
