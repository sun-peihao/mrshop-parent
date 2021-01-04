package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpecGroupDTO;
import com.tencent.shop.entity.SpecGroupEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/4
 * @Version V1.0
 **/
@Api(value = "规格参数")
public interface SpecificationService {

    @ApiOperation(value = "规格查询")
    @GetMapping(value = "specification/list")
    Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格新增")
    @PostMapping(value = "specification/save")
    Result<JSONObject> save(@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格修改")
    @PutMapping(value = "specification/save")
    Result<JSONObject> edit(@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格删除")
    @DeleteMapping(value = "specification/delete/{id}")
    Result<JSONObject> delete(@PathVariable Integer id);
}
