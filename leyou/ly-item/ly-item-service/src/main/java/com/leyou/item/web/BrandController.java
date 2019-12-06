package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
        @Autowired
    private BrandService brandService;
        //分页查询品牌
        @GetMapping("page")
        public ResponseEntity<PageResult<Brand>> queryBrandByPage(
                @RequestParam(name = "page",defaultValue = "1") Integer page,
                @RequestParam(name = "rows",defaultValue = "5") Integer rows,
                @RequestParam(name = "sortBy",required = false) String sortBy,
                @RequestParam(name = "desc",defaultValue = "false") Boolean desc,
                @RequestParam(name = "key",required = false) String key
        ){
            return  ResponseEntity.ok(brandService.queryBrandByPage(page,rows,sortBy,desc,key));
        }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
        public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
            brandService.saveBrand(brand,cids);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

    /**
     * 根据Cid查询品牌
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandByid(@PathVariable("id")Long bid){
        return ResponseEntity.ok(brandService.queryBrandByBid(bid));
    }
    @GetMapping("list")
     public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids")List<Long> ids){
        return  ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }

}
