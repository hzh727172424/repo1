package com.leyou.item.web;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @GetMapping("/spu/page")
    //分页查询商品
    public ResponseEntity<PageResult<Spu>> queryGoodsByPage(
            @RequestParam(name = "key",required = false) String key,
            @RequestParam(name = "saleable",required = false)  Boolean saleable,
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,
            @RequestParam(name = "page",defaultValue = "1")  Integer page
            ){
        return ResponseEntity.ok(goodsService.queryGoodsByPage(key,saleable,page,rows));
    }
    //新增商品
    @PostMapping("goods")
    public  ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //查询商品详情来获取修改商品回显
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable(name = "id")Long spuId){
        return  ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }
    //查询spu下的sku集合
    @GetMapping("/sku/list")
    public  ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam(name = "id")Long spuId){
        return  ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }
    //根据sku的id查询sku
    @GetMapping("/sku/list/ids")
    public  ResponseEntity<List<Sku>> querySkuByIds(@RequestParam(name = "ids")List<Long> ids){
        return  ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }
    //商品修改
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //查询单个spu和详情和sku
    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long spuId){
        return  ResponseEntity.ok(goodsService.querySpuById(spuId));
    }
    //减少库存
    @PostMapping("stock/decrese")
    public ResponseEntity<Void> decreseStock(@RequestBody List<CartDTO> cartDTOs){
       goodsService.decreseStock(cartDTOs);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
