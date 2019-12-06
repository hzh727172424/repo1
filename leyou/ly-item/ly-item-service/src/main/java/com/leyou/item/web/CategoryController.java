package com.leyou.item.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //查询品牌分类PID是parentID。父类ID
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(name = "pid") Long pid){
        return  ResponseEntity.ok(categoryService.queryCategoryByPid(pid));
    }
    //查询品牌分类
    @GetMapping("/bid/{bid}")
    public  ResponseEntity<List<Category>> queryCategoryByBid(@PathVariable("bid") Long bid){
        return  ResponseEntity.ok(categoryService.queryCategoryByBid(bid));
    }

    @GetMapping("/list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(categoryService.queryCategoryByIds(ids));
    }
}
