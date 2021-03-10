package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.feign.BrandFeign;
import com.tencent.shop.feign.CategoryFeign;
import com.tencent.shop.feign.GoodsFeign;
import com.tencent.shop.feign.SpecificationFeign;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.*;
import com.tencent.shop.entity.*;
import com.tencent.shop.service.TemplateService;
import com.tencent.shop.utils.ObjectUtil;
import com.tencent.shop.utils.TencentBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/9
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${mrshop.static.html.path}")
    private String htmlPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {
        Map<String, Object> goodsInfo = this.getGoodsInfo(spuId);

        Context context = new Context();
        context.setVariables(goodsInfo);

        File file = new File(htmlPath,spuId + ".html");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (ObjectUtil.isNotNull(printWriter)){
                printWriter.close();
            }
        }


        return this.setResultSuccess();
    }

    private Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        //spu
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        SpuDTO spuInfo = null;
        if (spuResult.isSuccess()){
            spuInfo = spuResult.getData().get(0);
            map.put("spuInfo",spuInfo);

            //spuDetail
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
            if (spuDetailResult.isSuccess()){
                SpuDetailEntity spuDetailInfo = spuDetailResult.getData();
                map.put("spuDetailInfo",spuDetailInfo);

            }
        }

        //category
        Result<List<CategoryEntity>> cateResult = categoryFeign.getCateByIds(
                String.join(","
                        , Arrays.asList(spuInfo.getCid1()+"",spuInfo.getCid2()+"",spuInfo.getCid3()+"")
                )
        );
        if (cateResult.isSuccess()){
            List<CategoryEntity> cateInfo = cateResult.getData();
            map.put("cateInfo",cateInfo);
        }

        //brand
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuInfo.getBrandId());
        Result<PageInfo<BrandEntity>> brandResult = brandFeign.getBrandInfo(brandDTO);
        if (brandResult.isSuccess()) {
            BrandEntity brandInfo = brandResult.getData().getList().get(0);
            map.put("brandInfo",brandInfo);
        }

        //sku
        Result<List<SkuDTO>> skusResult = goodsFeign.getSkuBySpuId(spuId);
        if (skusResult.isSuccess()) {
            List<SkuDTO> skusInfo = skusResult.getData();
            map.put("skusInfo",skusInfo);
        }

        //specGroup,specParam
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(spuInfo.getCid3());
        Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.list(specGroupDTO);
        if (specGroupResult.isSuccess()){
            List<SpecGroupEntity> specGroupInfo = specGroupResult.getData();
            List<SpecGroupDTO> specGroupAndSpecParam = specGroupInfo.stream().map(specGroup -> {
                SpecGroupDTO specGroupDTO1 = TencentBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(specGroupDTO1.getId());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.list(specParamDTO);
                if (specParamResult.isSuccess()) {
                    specGroupDTO1.setSpecList(specParamResult.getData());
                }
                return specGroupDTO1;
            }).collect(Collectors.toList());
            map.put("specGroupAndSpecParam",specGroupAndSpecParam);
        }

        //特殊规格
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuInfo.getCid3());
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.list(specParamDTO);
        if (specParamResult.isSuccess()){
            List<SpecParamEntity> specParamInfo = specParamResult.getData();
            Map<Integer, String> specParamMap = new HashMap<>();
            specParamInfo.stream().forEach(specParam -> specParamMap.put(specParam.getId(),specParam.getName()));
            map.put("specParamMap",specParamMap);
        }

        return map;

    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {

        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
        if(spuInfo.isSuccess()){
            List<SpuDTO> spuList = spuInfo.getData();
            spuList.stream().forEach(spu -> {
                this.createStaticHTMLTemplate(spu.getId());
            });
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearStaticHTMLTemplate() {
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
        if(spuInfo.isSuccess()){
            List<SpuDTO> spuList = spuInfo.getData();
            spuList.stream().forEach(spu -> {
                this.deleteStaticHTMLTemplate(spu.getId());
            });
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId) {

        File file = new File(htmlPath,spuId + ".html");

        if (file.exists()){
            file.delete();
        }

        return this.setResultSuccess();
    }
}
