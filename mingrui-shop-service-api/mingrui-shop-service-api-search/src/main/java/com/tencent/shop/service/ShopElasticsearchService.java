package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.Result;
import com.tencent.shop.document.GoodsDoc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName ShopElasticsearchService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/4
 * @Version V1.0
 **/
@Api(tags = "es接口")
public interface ShopElasticsearchService {

    /*@ApiOperation(value = "获取商品信息测试")
    @GetMapping(value = "es/goodsInfo")
    Result<JSONObject> esGoodsInfo();*/

    @ApiOperation(value = "新增数据到es")
    @PostMapping(value = "es/saveData")

    Result<JSONObject> saveData(Integer spuId);
    @ApiOperation(value = "通过id删除es数据")
    @DeleteMapping(value = "es/saveData")
    Result<JSONObject> delData(Integer spuId);


    @ApiOperation(value = "搜索")
    @GetMapping(value = "es/search")
    Result<List<GoodsDoc>> search(@RequestParam String search,@RequestParam Integer page,@RequestParam String filter);

    //ES数据初始化-->索引创建,映射创建,mysql数据同步
    @ApiOperation(value = "ES商品数据初始化-->索引创建,映射创建,mysql数据同步")
    @GetMapping(value = "es/initGoodsEsData")
    Result<JSONObject> initGoodsEsData();

    @ApiOperation(value = "清空ES中的商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JSONObject> clearGoodsEsData();

}
