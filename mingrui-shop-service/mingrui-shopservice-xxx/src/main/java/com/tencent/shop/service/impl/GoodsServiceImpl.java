package com.tencent.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.SpuEntity;
import com.tencent.shop.mapper.SpuMapper;
import com.tencent.shop.service.GoodsService;
import com.tencent.shop.utils.ObjectUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/5
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {

        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        Example example = new Example(SpuEntity.class);

        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2){
            example.createCriteria().andEqualTo("saleable",spuDTO.getSaleable());
        }

        if (!StringUtils.isEmpty(spuDTO.getTitle())){
            example.createCriteria().andLike("title","%"+spuDTO.getTitle()+"%");
        }

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);
        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);

        return this.setResultSuccess(spuEntityPageInfo);
    }
}
