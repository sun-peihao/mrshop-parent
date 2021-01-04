package com.tencent.shop.service;

import com.google.gson.JsonObject;
import com.tencent.shop.base.Result;
import com.tencent.shop.entity.CategoryEntity;
import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/22
 * @Version V1.0
 **/
@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "通过品牌id查询分类信息")
    @GetMapping(value = "category/brand")
    Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId);

    @ApiOperation(value = "通过Id删除商品分类")
    @DeleteMapping(value = "category/delete")
    Result<JsonObject> deleteCategoryById(Integer id);

    @ApiOperation(value = "通过Id修改商品分类")
    @PutMapping(value = "category/update")
    Result<JsonObject> updateCategoryById(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "增加商品分类")
    @PostMapping(value = "category/save")
    Result<JsonObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);

}
