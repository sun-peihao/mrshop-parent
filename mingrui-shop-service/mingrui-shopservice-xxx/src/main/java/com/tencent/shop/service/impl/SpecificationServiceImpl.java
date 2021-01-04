package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpecGroupDTO;
import com.tencent.shop.entity.SpecGroupEntity;
import com.tencent.shop.mapper.SpecificationMapper;
import com.tencent.shop.service.SpecificationService;
import com.tencent.shop.utils.TencentBeanUtil;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/4
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecificationMapper specificationMapper;

    @Override
    public Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
        List<SpecGroupEntity> list = specificationMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {

        specificationMapper.insertSelective(TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> edit(SpecGroupDTO specGroupDTO) {

        specificationMapper.updateByPrimaryKeySelective(TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delete(Integer id) {

        specificationMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
