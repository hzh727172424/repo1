package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
@Autowired
private SpuDetailMapper spuDetailMapper;
@Autowired
private SkuMapper skuMapper;
@Autowired
private StockMapper stockMapper;
@Autowired
private AmqpTemplate amqpTemplate;
    /**
     * 分页查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> queryGoodsByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //分页
        PageHelper.startPage(page,rows );
        //过滤
        Example example =new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%" );
        }
        //是否上架过滤
        if (saleable!=null){
            criteria.andEqualTo("saleable", saleable);
        }
        //赋予默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> list =spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        loadCategoryAndBrandName(list);
        PageInfo<Spu> pageInfo =new PageInfo(list);
        return  new PageResult(pageInfo.getTotal(),list);
    }
//获取Cname和Bname到查询出的Spu集合中
    private void loadCategoryAndBrandName(List<Spu> list) {
        for (Spu spu : list) {
            List<String> names = categoryService.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            System.out.println(spu.getBrandId());
            spu.setBname(brandService.queryBrandByBid(spu.getBrandId()).getName());
        }
    }

    /**
     * 新增商品
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insert(spuDetail);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        saveSkuAndStock(spu);
        amqpTemplate.convertAndSend( "ly.item.exchange","item.insert", spu.getId());
    }

    /**
     * 新增商品sku和商品库存
     * @param spu
     */

    private void saveSkuAndStock(Spu spu) {
        int count;
        List<Sku> skus = spu.getSkus();
        List<Stock> stocks=new ArrayList<>();
        for (Sku sku : skus) {
            sku.setId(null);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            count = skuMapper.insert(sku);
            if (count!=1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            Stock stock =new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        count = stockMapper.insertList(stocks);
        if (count!=stocks.size()){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    /**
     * 查询商品详情来获取修改商品回显
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail==null){
            throw new LyException(ExceptionEnum.GOODS_SPUDETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 查询商品Sku来获取商品回显
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
            Sku s =new Sku();
            s.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(s);
        if (CollectionUtils.isEmpty(skus)){
            throw  new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        getSkusWithStock(skus);
        return skus;
    }

    /**
     * 修改商品
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId()==null){
            throw new LyException(ExceptionEnum.GOODS_SPU_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skus = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skus)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> skuIds = skus.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(skuIds);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1){
            throw  new LyException(ExceptionEnum.UPDATE_GOODS_SPU_ERROR);
        }
        //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count!=1){
            throw  new LyException(ExceptionEnum.UPDATE_GOODS_SPU_DETAIL_ERROR);
        }
        //新增sku和stock
         saveSkuAndStock(spu);

        amqpTemplate.convertAndSend( "ly.item.exchange","item.update", spu.getId());
    }
//查询spu和其他数据
    public Spu querySpuById(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<Sku> skuList = querySkuBySpuId(spuId);
        SpuDetail spuDetail = querySpuDetailBySpuId(spuId);
        spu.setSkus(skuList);
        spu.setSpuDetail(spuDetail);
        return spu;
    }
//根据sku表中的Id查询skus
    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)){
            throw  new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        getSkusWithStock(skus);
        return skus;
    }
//获取sku和库存
    private void getSkusWithStock(List<Sku> skus) {
        List<Long> skuIds = skus.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(skuIds);
        Map<Long, Integer> map = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        for (Sku sku : skus) {
            sku.setStock(map.get(sku.getId()));
        }
    }
//减少库存
    @Transactional
    public void decreseStock(List<CartDTO> cartDTOs) {
        for (CartDTO cartDTO : cartDTOs) {
            int count = stockMapper.decreseStock(cartDTO.getSkuId(), cartDTO.getNum());
            if (count!=1){
                throw new LyException(ExceptionEnum.UPDATE_DECRESE_STOCK_ERROR);
            }
        }
    }
}
