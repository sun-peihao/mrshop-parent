package com.tencent.shop.service;

import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.SpuEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

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
    Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO);

}
