package com.tencent.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.entity.CategoryEntity;
import com.tencent.shop.entity.SpuEntity;
import com.tencent.shop.mapper.BrandMapper;
import com.tencent.shop.mapper.CategoryMapper;
import com.tencent.shop.mapper.SpuMapper;
import com.tencent.shop.service.GoodsService;
import com.tencent.shop.status.HTTPStatus;
import com.tencent.shop.utils.ObjectUtil;
import com.tencent.shop.utils.TencentBeanUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

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

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = TencentBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //1.
            /*CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(spuEntity.getCid1());
            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(spuEntity.getCid2());
            CategoryEntity categoryEntity3 = categoryMapper.selectByPrimaryKey(spuEntity.getCid3());
            spuDTO1.setCategoryName(categoryEntity.getName() + "/" + categoryEntity2.getName() + "/" + categoryEntity3.getName());*/

            //2.
            /*List<Integer> cidList = new ArrayList<>();
            cidList.add(spuEntity.getCid1());
            cidList.add(spuEntity.getCid2());
            cidList.add(spuEntity.getCid3());

            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(cidList);
            String categoryName = "";
            List<String> strings = new ArrayList<>();
            strings.set(0,"");
            categoryEntities.stream().forEach(categoryEntity -> {
                strings.set(0,strings.get(0) + categoryEntity.getName() + "/");
            });
            categoryName = strings.get(0).substring(0,strings.get(0).length());*/

            //3.
            List<Integer> cidList = Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3());
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(cidList);
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);

        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal() + "",spuDTOList);
    }
}
