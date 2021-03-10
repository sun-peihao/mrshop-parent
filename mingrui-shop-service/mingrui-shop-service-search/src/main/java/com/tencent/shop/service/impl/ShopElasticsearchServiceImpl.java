package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.response.GoodsResponse;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.document.GoodsDoc;
import com.tencent.shop.dto.SkuDTO;
import com.tencent.shop.dto.SpecParamDTO;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.entity.CategoryEntity;
import com.tencent.shop.entity.SpecParamEntity;
import com.tencent.shop.entity.SpuDetailEntity;
import com.tencent.shop.feign.BrandFeign;
import com.tencent.shop.feign.CategoryFeign;
import com.tencent.shop.feign.GoodsFeign;
import com.tencent.shop.feign.SpecificationFeign;
import com.tencent.shop.service.ShopElasticsearchService;
import com.tencent.shop.status.HTTPStatus;
import com.tencent.shop.utils.HighlightUtil;
import com.tencent.shop.utils.JSONUtil;
import com.tencent.shop.utils.ObjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/4
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
//        GoodsDoc goodsDoc = new GoodsDoc();
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    @Override
    public GoodsResponse search(String search, Integer page, String filter) {

        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getNativeSearchQueryBuilder(search, page,filter).build(), GoodsDoc.class);
        Aggregations aggregations = searchHits.getAggregations();

        List<GoodsDoc> goodsDoc = HighlightUtil.getHighlight(searchHits.getSearchHits());

        long total = searchHits.getTotalHits();
        long totalPage = Double.valueOf(Math.ceil(Double.valueOf(total) / 10)).longValue();

        Map<Integer, List<CategoryEntity>> categoryMap = this.getCategoryList(searchHits.getAggregations());
        Integer hotCid = 0;
        List<CategoryEntity> categoryList = null;
        for (Map.Entry<Integer,List<CategoryEntity>> entry : categoryMap.entrySet()){
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }

        return new GoodsResponse(total,totalPage,this.getBrandList(aggregations),categoryList,goodsDoc,this.getSpecMap(hotCid,search));
    }

    private Map<String, List<String>> getSpecMap(Integer hotCid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specParamList = specificationFeign.list(specParamDTO);
        Map<String, List<String>> specMap = new HashMap<>();
        if (specParamList.isSuccess()){
            List<SpecParamEntity> specParam = specParamList.getData();

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            nativeSearchQueryBuilder.withPageable(PageRequest.of(0,1));

            specParam.stream().forEach(specParam1 -> {
                nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam1.getName()).field("specs."+ specParam1.getName() +".keyword"));
            });
            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), GoodsDoc.class);
            Aggregations aggregations = searchHits.getAggregations();
            specParam.stream().forEach(specParam1 -> {
                Terms aggregation = aggregations.get(specParam1.getName());
                List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
                List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                specMap.put(specParam1.getName(),valueList);
            });
        }
        return specMap;
    }

    private NativeSearchQueryBuilder getNativeSearchQueryBuilder(String search,Integer page,String filter){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

        if (!ObjectUtil.isNull(filter) && filter.length() > 2){

            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            filterMap.forEach((key,value) -> {
                MatchQueryBuilder matchQueryBuilder = null;
                if (key.equals("brandId") || key.equals("cid3")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + key + ".keyword", value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

//            nativeSearchQueryBuilder.withFilter(
//                    QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("spec.分辨率.keyword","1280*1080"))
//                            .must(QueryBuilders.matchQuery("brandId","8557"))
//            );
        }
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page -1,10));
        //高亮
        nativeSearchQueryBuilder.withHighlightBuilder(HighlightUtil.getHighlightBuilder("title"));
        //只返回有用字段数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title","skus"},null));
        //聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agg_category").field("cid3"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agg_brand").field("brandId"));

        return nativeSearchQueryBuilder;
    }

    //获取分类list
    private Map<Integer ,List<CategoryEntity>> getCategoryList(Aggregations aggregations){
        Terms agg_category = aggregations.get("agg_category");
        List<? extends Terms.Bucket> categoryBuckets = agg_category.getBuckets();

        List<Long> doCount = Arrays.asList(0L);
        List<Integer> hotCid = Arrays.asList(0);

        List<String> categoryIdList = categoryBuckets.stream().map(categoryBucket -> {

            if(categoryBucket.getDocCount() > doCount.get(0)){

                doCount.set(0,categoryBucket.getDocCount());
                hotCid.set(0,categoryBucket.getKeyAsNumber().intValue());
            }

            return categoryBucket.getKeyAsNumber().longValue() + "";

        }).collect(Collectors.toList());
        
        Result<List<CategoryEntity>> cateResult = categoryFeign.getCateByIds(String.join(",", categoryIdList));
        List<CategoryEntity> categoryList = null;
        if(cateResult.isSuccess()){
            categoryList = cateResult.getData();
        }

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();
        map.put(hotCid.get(0),categoryList);

        return map;
    }

    //获取品牌list
    private List<BrandEntity> getBrandList(Aggregations aggregations){
        Terms agg_brand = aggregations.get("agg_brand");
        List<? extends Terms.Bucket> brandBuckets = agg_brand.getBuckets();
        List<String> brandIdList = brandBuckets.stream().map(
                brandBucket -> brandBucket.getKeyAsNumber().longValue() + "").collect(Collectors.toList());
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIds(String.join(",", brandIdList));
        List<BrandEntity> brandList = null;
        if(brandResult.isSuccess()){
            brandList = brandResult.getData();
        }
        return brandList;
    }

    @Override
    public Result<JSONObject> initGoodsEsData() {
        IndexOperations operations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!operations.exists()){
            operations.create();
            operations.createMapping();
        }
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()){
            indexOperations.delete();
        }
        return this.setResultSuccess();
    }

    //@Override
    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {
//        spuDTO.setPage(1);
//        spuDTO.setRows(150);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        if (spuInfo.isSuccess()){
            List<SpuDTO> spuInfoData = spuInfo.getData();
            List<GoodsDoc> docList = spuInfoData.stream().map(spu -> {
                GoodsDoc goodsDoc = new GoodsDoc();
                //spu信息填充
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                //可搜索数据
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                //sku数据，通过spuid查询skus
                Map<List<Long>, List<Map<String, Object>>> skusAndPriceMap = this.getSkusAndPriceList(spu.getId());
                skusAndPriceMap.forEach((key,value) -> {
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });
                //获取规格参数信息
                Map<String, Object> spuMap = this.getSpecMap(spu);
                goodsDoc.setSpecs(spuMap);
                return goodsDoc;
            }).collect(Collectors.toList());
            return docList;
        }
        return null;
    }

    //获取规格参数
    private Map<String,Object> getSpecMap(SpuDTO spu){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spu.getCid3());
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.list(specParamDTO);
        if(specParamInfo.isSuccess()){
            List<SpecParamEntity> specParamList = specParamInfo.getData();
            Result<SpuDetailEntity> spuDetailInfo = goodsFeign.getSpuDetailBySpuId(spu.getId());
            if(spuDetailInfo.isSuccess()){
                SpuDetailEntity spuDetailEntity = spuDetailInfo.getData();
                //将json字符串转为map集合
                Map<String, Object> specMap = this.getSpecMap(specParamList,spuDetailEntity);
                return specMap;
            }
        }
        return null;
    }

    private Map<String, Object> getSpecMap(List<SpecParamEntity> specParamList,SpuDetailEntity spuDetailEntity){
        Map<String, Object> specMap = new HashMap<>();
        Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
        Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());
        specParamList.stream().forEach(specParam -> {
            if (specParam.getGeneric()){
                if (specParam.getNumeric() && !StringUtils.isEmpty(specParam.getSegments())){
                    specMap.put(specParam.getName(), chooseSegment(genericSpec.get(specParam.getId() + ""),
                                    specParam.getSegments(), specParam.getUnit()));
                }else {
                    specMap.put(specParam.getName(),genericSpec.get(specParam.getId() + ""));
                }
            }else {
                specMap.put(specParam.getName(),specialSpec.get(specParam.getId() + ""));
            }
        });
        return specMap;
    }

    //sku数据，通过spuid查询skus
    private Map<List<Long>,List<Map<String,Object>>> getSkusAndPriceList(Integer spuId){
        Map<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
        if (skuResult.isSuccess()) {
            List<SkuDTO> skuList = skuResult.getData();
            List<Long> priceList = new ArrayList<>();
            List<Map<String, Object>> skuMapList = skuList.stream().map(sku -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("image", sku.getImages());
                map.put("price", sku.getPrice());
                priceList.add(sku.getPrice().longValue());
                return map;
            }).collect(Collectors.toList());

            hashMap.put(priceList,skuMapList);
        }
        return hashMap;
    }

    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
