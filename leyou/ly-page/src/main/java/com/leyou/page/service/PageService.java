package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class PageService {
@Autowired
private GoodsClient goodsClient;
@Autowired
private BrandClient brandClient;
@Autowired
private SpecificationClient specificationClient;
@Autowired
private CategoryClient categoryClient;
@Autowired
private TemplateEngine templateEngine;
    public Map<String, Object> getModel(Long spuId) {
Map<String,Object> model=new HashMap<>();
        //查询Spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        List<Sku> skus = spu.getSkus();
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        Brand brand = brandClient.queryBrandByid(spu.getBrandId());
        List<SpecGroup> specs = specificationClient.querySpecsByCid(spu.getCid3());
        model.put("spu",spu );
        model.put("skus",skus );
        model.put("detail",detail );
        model.put("categories",categories );
        model.put("brand",brand );
        model.put("specs",specs );
        return model;
    }
    public void createHtml(Long spuId){
        //获取context
        Context context = new Context();
        context.setVariables(getModel(spuId));
        //输出流
        File file = new File("F:\\IdeaProjects\\yun6\\upload",spuId+".html");
        if (file.exists()){
            file.delete();
        }
        try(PrintWriter writer = new PrintWriter(file,"UTF-8")){
            //生成html
            templateEngine.process("item",context , writer);
        }catch (Exception e){
            log.error("[静态页面]生成静态页面异常");
        }
    }

    public void delteHtml(Long spuId) {
        File file = new File("F:\\IdeaProjects\\yun6\\upload",spuId+".html");
        if (file.exists()){
            file.delete();
        }
    }
}
