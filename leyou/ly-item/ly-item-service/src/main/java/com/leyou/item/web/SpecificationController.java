package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("groups/{cid}")
    //查询商品规格组
        public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") Long cid){
        return  ResponseEntity.ok(specificationService.querySpecGroupByCid(cid));
    }
    //查询商品规格参数
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamByGid(@RequestParam(name = "gid",required = false) Long gid,
                                                               @RequestParam(name = "cid",required = false) Long cid,
                                                               @RequestParam(name = "searching",required = false) Boolean searching){
        return  ResponseEntity.ok(specificationService.querySpecParamByGid(gid,cid,searching));
    }
    //查询商品规格组以及参数
    @GetMapping("groups")
    public   ResponseEntity<List<SpecGroup>> querySpecListByCid(@RequestParam("cid") Long cid){
        return  ResponseEntity.ok(specificationService.querySpecListByCid(cid));
    }
}
