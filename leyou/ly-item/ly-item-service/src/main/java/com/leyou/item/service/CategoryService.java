package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    public List<Category> queryCategoryByPid(Long pid){
        Category category =new Category();
        category.setParent_id(pid);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Long> selectCid(Long bid) {
        List<Long> cids = categoryMapper.selectCid(bid);
        if (CollectionUtils.isEmpty(cids)){
            throw  new LyException(ExceptionEnum.CATEGORY_ID_NOT_FOUND);
        }
        return cids;
    }
    //多个CID查询分类
    public List<Category>  queryCategoryByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(categories)){
            throw  new LyException(ExceptionEnum.CATEGORY_ID_NOT_FOUND);
        }
        return categories;
    }
    //查询品牌的分类
    public List<Category> queryCategoryByBid(Long bid) {
        List<Category> categories = categoryMapper.queryCategoryByBid(bid);
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }
}
