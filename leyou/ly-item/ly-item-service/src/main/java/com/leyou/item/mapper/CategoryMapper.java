package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper  extends Mapper<Category>,IdListMapper<Category,Long>{
    //通过中间表查询CID
    @Select("select*from tb_category_brand where brand_id=#{bid}")
    List<Long> selectCid(@Param("bid") Long bid);
    @Select("select*from tb_category where id in(select category_id from tb_category_brand where brand_id=#{bid})")
    List<Category>  queryCategoryByBid(@Param("bid") Long bid);
}
