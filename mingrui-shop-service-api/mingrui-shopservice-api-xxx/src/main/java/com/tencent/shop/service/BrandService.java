package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.BrandDTO;
import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName BrandService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/25
 * @Version V1.0
 **/
@Api(tags = "品牌接口")
public interface BrandService {


    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/list")
    Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌")
    @PostMapping(value = "brand/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌")
    @PutMapping(value = "brand/save")
    Result<JSONObject> editBrand(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "删除品牌")
    @DeleteMapping(value = "brand/delBrand")
    Result<JSONObject> deleteBrand(Integer brandId);

    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfoByCategoryId")
    Result<PageInfo<BrandEntity>> getBrandInfoByCategoryId(Integer cid);

}
