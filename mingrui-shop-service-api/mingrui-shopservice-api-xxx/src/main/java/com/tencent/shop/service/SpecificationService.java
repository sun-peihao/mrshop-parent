package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpecGroupDTO;
import com.tencent.shop.dto.SpecParamDTO;
import com.tencent.shop.entity.SpecGroupEntity;
import com.tencent.shop.entity.SpecParamEntity;
import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/4
 * @Version V1.0
 **/
@Api(tags = "规格")
public interface SpecificationService {

    @ApiOperation(value = "规格查询")
    @GetMapping(value = "specgroup/list")
    Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格新增")
    @PostMapping(value = "specgroup/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格修改")
    @PutMapping(value = "specgroup/save")
    Result<JSONObject> edit(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格删除")
    @DeleteMapping(value = "specgroup/delete/{id}")
    Result<JSONObject> delete(@PathVariable Integer id);

    @ApiOperation(value = "规格参数查询")
    @GetMapping(value = "specparam/list")
    Result<List<SpecParamEntity>> list(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "规格参数新增")
    @PostMapping(value = "specparam/save")
    Result<JSONObject> saveSpecParam(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "规格参数修改")
    @PutMapping(value = "specparam/save")
    Result<JSONObject> editSpecParam(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "规格参数删除")
    @DeleteMapping(value = "specparam/delete")
    Result<JSONObject> deleteSpecParamById(Integer id);
}
