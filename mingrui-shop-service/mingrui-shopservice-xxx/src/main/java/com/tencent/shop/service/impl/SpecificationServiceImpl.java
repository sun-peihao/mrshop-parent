package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpecGroupDTO;
import com.tencent.shop.dto.SpecParamDTO;
import com.tencent.shop.entity.SpecGroupEntity;
import com.tencent.shop.entity.SpecParamEntity;
import com.tencent.shop.mapper.SpecGroupMapper;
import com.tencent.shop.mapper.SpecParamMapper;
import com.tencent.shop.service.SpecificationService;
import com.tencent.shop.utils.TencentBeanUtil;
import org.springframework.transaction.annotation.Transactional;
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
    private SpecGroupMapper specificationMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    @Transactional
    public Result<JSONObject> deleteSpecParamById(Integer id) {

        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> editSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(TencentBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> saveSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(TencentBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecParamEntity>> list(SpecParamDTO specParamDTO) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",specParamDTO.getGroupId());
        List<SpecParamEntity> specParamEntities = specParamMapper.selectByExample(example);

        return this.setResultSuccess(specParamEntities);
    }

    @Override
    public Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO) {

        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid",
                TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
        List<SpecGroupEntity> list = specificationMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {

        specificationMapper.insertSelective(TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> edit(SpecGroupDTO specGroupDTO) {

        specificationMapper.updateByPrimaryKeySelective(TencentBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> delete(Integer id) {

        //删除规格组之前需要先判断一下当前规格组下是否有规格参数
        //true : 不能被删除
        //false ：删除

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);

        List<SpecParamEntity> specParamEntities = specParamMapper.selectByExample(example);
        if (specParamEntities.size() >= 1) return this.setResultError("该规格组下有规格参数,无法删除");

        specificationMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }
}
