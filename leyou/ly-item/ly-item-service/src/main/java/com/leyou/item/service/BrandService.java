package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.apache.el.parser.BooleanNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.print.DocFlavor;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc,String key){
        PageHelper.startPage(page, rows);
        Example example=new Example(Brand.class);
        if (StringUtils.isNotBlank(key)){
            example.createCriteria().orLike("name", "%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy+(desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(),brands);
    }
@Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
            brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count!=1){
            throw new LyException(ExceptionEnum.INSERT_BRAND_ERROR);
        }
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count!=1){
                throw new LyException(ExceptionEnum.INSERT_BRAND_ERROR);
            }
        }
    }
    public Brand queryBrandByBid(Long bid){
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand==null){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

}
