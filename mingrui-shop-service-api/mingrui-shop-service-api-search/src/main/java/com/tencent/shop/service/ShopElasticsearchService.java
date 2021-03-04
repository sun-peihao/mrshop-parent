package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName ShopElasticsearchService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/4
 * @Version V1.0
 **/
@Api(tags = "es接口")
public interface ShopElasticsearchService {

    @ApiOperation(value = "获取商品信息测试")
    @GetMapping(value = "es/goodsInfo")
    Result<JSONObject> esGoodsInfo();
}
