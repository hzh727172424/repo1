package com.leyou.item.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     *分页查询spu
     * @param key
     * @param saleable
     * @param rows
     * @param page
     * @return
     */
    @GetMapping("/spu/page")
 PageResult<Spu> queryGoodsByPage(
            @RequestParam(name = "key",required = false) String key,
            @RequestParam(name = "saleable",required = false)  Boolean saleable,
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,
            @RequestParam(name = "page",defaultValue = "1")  Integer page
    );
    //查询spu详情
    @GetMapping("spu/detail/{id}")
    SpuDetail  querySpuDetailBySpuId(@PathVariable(name = "id")Long spuId);
    //查询sku
    @GetMapping("/sku/list")
     List<Sku>  querySkuBySpuId(@RequestParam(name = "id")Long spuId);
    //查询spu
    @GetMapping("/spu/{id}")
    Spu  querySpuById(@PathVariable("id")Long spuId);
    //根据ids查询skus
    @GetMapping("/sku/list/ids")
    List<Sku> querySkuByIds(@RequestParam(name = "ids")List<Long> ids);
    @PostMapping("stock/decrese")
    void decreseStock(@RequestBody List<CartDTO> cartDTOs);
}
