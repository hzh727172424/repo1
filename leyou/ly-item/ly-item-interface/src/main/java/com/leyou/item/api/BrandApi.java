package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {
    @GetMapping("brand/{id}")
    Brand queryBrandByid(@PathVariable("id")Long bid);
    @GetMapping("brand/list")
    List<Brand> queryBrandByIds(@RequestParam("ids")List<Long> ids);
}
