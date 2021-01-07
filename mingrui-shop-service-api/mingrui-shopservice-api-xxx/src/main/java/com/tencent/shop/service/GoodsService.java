package com.tencent.shop.service;

import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SkuDTO;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.SpuEntity;
import com.tencent.shop.utils.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/5
 * @Version V1.0
 **/
@Api(value = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation(value = "新增商品")
    @PostMapping(value = "goods/save")
    Result<JSONUtil> saveGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuid获取spuDetail信息")
    @GetMapping(value = "goods/getSpuDetailBySpuId")
    Result<List<SpuEntity>> getSpuDetailBySpuId(Integer spuId);

    @ApiOperation(value = "通过spuid获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);
}
