package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.document.GoodsDoc;
import com.tencent.shop.dto.SkuDTO;
import com.tencent.shop.dto.SpecParamDTO;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.SpecParamEntity;
import com.tencent.shop.entity.SpuDetailEntity;
import com.tencent.shop.entity.SpuEntity;
import com.tencent.shop.feign.GoodsFeign;
import com.tencent.shop.feign.SpecificationFeign;
import com.tencent.shop.service.ShopElasticsearchService;
import com.tencent.shop.utils.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Result<JSONObject> esGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setPage(1);
        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        System.out.println(spuInfo);
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

                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spu.getId());
                if (spuInfo.isSuccess()) {
                    List<SkuDTO> skuList = skuResult.getData();
                    List<Long> priceList = new ArrayList<>();

                    List<Map<String, Object>> skuMapList = skuList.stream().map(sku -> {

                        Map<String, Object> map = new HashMap<>();
                        map.put("id", sku.getSkuId());
                        map.put("title", sku.getTitle());
                        map.put("image", sku.getImages());
                        map.put("price", sku.getPrice());

                        priceList.add(sku.getPrice().longValue());
                        return map;
                    }).collect(Collectors.toList());

                    goodsDoc.setPrice(priceList);
                    //将list转为json字符串
                    goodsDoc.setSkus(JSONUtil.toJsonString(skuMapList));
                }

                //规格参数信息
                //获取规格参数信息
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
                        Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
                        Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());

                        Map<String, Object> specMap = new HashMap<>();
                        specParamList.stream().forEach(specParam -> {
                            if (specParam.getGeneric()){
                                if (specParam.getNumeric() && !StringUtils.isEmpty(specParam.getSegments())){
                                    specMap.put(specParam.getName(),
                                            chooseSegment(genericSpec.get(specParam.getId() + ""),
                                                    specParam.getSegments(),
                                                    specParam.getUnit()));
                                }else {
                                    specMap.put(specParam.getName(),genericSpec.get(specParam.getId() + ""));
                                }
                            }else {
                                specMap.put(specParam.getName(),genericSpec.get(specParam.getId() + ""));
                            }
                        });
                        goodsDoc.setSpecs(specMap);
                    }
                }
                return goodsDoc;

            }).collect(Collectors.toList());
            System.out.println(docList);
        }
        return null;
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
