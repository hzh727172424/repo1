package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand values(#{cid},#{bid})")
     int insertCategoryBrand(@Param("cid") Long cid,@Param("bid")Long bid);
    @Select("select*from tb_brand b join tb_category_brand cb on b.id=cb.brand_id where category_id=#{cid}")
    List<Brand> queryBrandByCid(@Param("cid")Long cid);

}
