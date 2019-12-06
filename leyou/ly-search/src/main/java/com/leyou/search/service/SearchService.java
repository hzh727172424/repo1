package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu){
        //得到标题
        String title = spu.getTitle();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询品牌
        Brand brand = brandClient.queryBrandByid(spu.getBrandId());
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        String all=title+brand.getName()+StringUtils.join(names," ");

        //查询Sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        //sku存放集合
        List<Map<String,Object>> skus =new ArrayList<>();
        //价格存放集合
        Set<Long> price=new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId() );
            map.put("title",sku.getTitle() );
            map.put("price",sku.getPrice() );
            map.put("image",StringUtils.substringBefore(sku.getImages(), ","));
            skus.add(map);
            price.add(sku.getPrice());
        }
        //查询商品规格参数
        List<SpecParam> specParams = specificationClient.querySpecParamByGid(null, spu.getCid3(), true);
        //查询规格参数详情
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //封装的规格参数Map
        Map<String,Object> specs =new HashMap<>();
        //使用JsonUtils把数据库spuDetail的规格参数 Json转成map
        Map<Long, String> generic_spec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        Map<Long, List<String>> special_spec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        for (SpecParam specParam : specParams) {
            String key=specParam.getName();
            Object value="";
                if (specParam.getGeneric()){
                        value=generic_spec.get(specParam.getId());
                        if (specParam.getNumeric()){
                            chooseSegment(value.toString(), specParam);
                        }
                }else{
                    value=special_spec.get(specParam.getId());
                }
            specs.put(key,value);
        }
        Goods goods =new Goods();
        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setSpecs(specs);//商品规格参数
        goods.setSkus(JsonUtils.toString(skus));// 商品sku
        goods.setAll(all);//  查询所有搜索字段，标题，分类，品牌，
        goods.setPrice(price);//商品价格
        return goods;
    }

    private String chooseSegment(String value,SpecParam p){
        double val = NumberUtils.toDouble(value);
        String result="其他";
        //保存数值段
            for(String segment: p.getSegments().split(",")){
                String[] segs =segment.split("-");
                //获取数值范围
                double begin=NumberUtils.toDouble(segs[0]);
                double end =Double.MAX_VALUE;
                if (segs.length==2){
                    end=NumberUtils.toDouble(segs[1]);
                }
                //判断是否在范围内
                if (val >=begin && val<end){
                    if (segs.length==1){
                        result=segs[0]+p.getUnit()+"以上";
                    }else if(begin==0){
                        result=segs[1]+p.getUnit()+"以下";
                    }else {
                        result=segment+p.getUnit();
                    }
                    break;
                }
            }
            return result;
    }

    public PageResult<Goods> queryGoodsByPage(SearchRequest request) {
        int page = request.getPage()-1;
        int size = request.getSize();
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //分页
        queryBuilder.withPageable(PageRequest.of(page,size ));
        //过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String [] {"id","subTitle","skus"},null));
        //搜索条件
        QueryBuilder basicQuery = buildFilterQuery(request);
        queryBuilder.withQuery(basicQuery);
        //聚合
        String  categoryAggName="category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        String brandAggName="brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc? SortOrder.DESC:SortOrder.ASC));
        }
        //查询
        AggregatedPage<Goods> goodsPage = template.queryForPage(queryBuilder.build(), Goods.class);
        int totalPages = goodsPage.getTotalPages();
        long total = goodsPage.getTotalElements();
        List<Goods> content = goodsPage.getContent();
        //解析聚合
        Aggregations aggregations = goodsPage.getAggregations();
        List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));
        //完成聚合规格参数
        List<Map<String,Object>> specs=null;
        if (categories!=null && categories.size()==1){
            specs=buildSpecificationAgg(categories.get(0).getId(),basicQuery);
        }

        return new SearchResult(total,totalPages,content,categories,brands,specs);
    }

    private QueryBuilder buildFilterQuery(SearchRequest request) {
        Map<String, String> filter = request.getFilter();
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //must指定查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (!"cid3".equals(key)&&!"brandId".equals(key)){
                key="specs."+key+".keyword";
            }
            String value = entry.getValue();
            //过滤字段
            queryBuilder.filter(QueryBuilders.termQuery(key, value));
        }
        return queryBuilder;
    }

    //规格参数聚合方法
    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs=new ArrayList<>();
        //查询规格参数
        List<SpecParam> params = specificationClient.querySpecParamByGid(null, cid, true);
        //聚合查询构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //查询条件
        queryBuilder.withQuery(basicQuery);
        for (SpecParam param : params) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggregations = result.getAggregations();
        for (SpecParam param : params) {
            StringTerms terms = aggregations.get(param.getName());
            List<String> options = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            Map<String,Object> map =new HashMap<>();
            map.put("k", param.getName());
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        }catch (Exception e){
            log.error("查询聚合品牌异常"+e);
            return null;
        }

    }


    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createInsertOrUpdate(Long spuId) {
        Spu spu = goodsClient.querySpuById(spuId);
        Goods goods = buildGoods(spu);
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
