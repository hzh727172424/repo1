package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult<T> extends PageResult {
    private List<Category> categories;
    private List<Brand> brands;
    private List<Map<String,Object>> specs;
    public SearchResult() {
    }

    public SearchResult(Long total, Integer totalpage, List<T> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalpage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
