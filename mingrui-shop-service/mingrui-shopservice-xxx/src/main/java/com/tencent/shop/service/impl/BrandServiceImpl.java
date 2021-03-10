package com.tencent.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.dto.BrandDTO;
import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.entity.CategoryBrandEntity;
import com.tencent.shop.mapper.BrandMapper;
import com.tencent.shop.mapper.CategoryBrandMapper;
import com.tencent.shop.service.BrandService;
import com.tencent.shop.utils.ObjectUtil;
import com.tencent.shop.utils.PinyinUtil;
import com.tencent.shop.utils.TencentBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/25
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public Result<List<BrandEntity>> getBrandByIds(String ids) {

        List<Integer> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Integer.valueOf(idStr)).collect(Collectors.toList());
        List<BrandEntity> brandEntities = brandMapper.selectByIdList(idList);
        return this.setResultSuccess(brandEntities);
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfoByCategoryId(Integer cid) {

        List<BrandEntity> list = categoryBrandMapper.getBrandInfoByCategoryId(cid);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //分页
        if (!ObjectUtil.isNull(brandDTO.getPage()) && !ObjectUtil.isNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
//        String isOrder = "asc";
        if (!StringUtils.isEmpty(brandDTO.getSort())){
//            if (Boolean.valueOf(brandDTO.getOrder())){
//                isOrder = "desc";
//            }
            PageHelper.orderBy(brandDTO.getOrderBy());
        }

        BrandEntity brandEntity = TencentBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        Example example = new Example(BrandEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(brandEntity.getName()))
            criteria.andLike("name","%" + brandEntity.getName() + "%");

        if(ObjectUtil.isNotNull(brandDTO.getId()))
            criteria.andEqualTo("id",brandDTO.getId());


        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandEntities);

        return this.setResultSuccess(pageInfo);
    }

    @Override
    @Transactional
    public Result<JSONObject> save(BrandDTO brandDTO) {

        BrandEntity brandEntity = TencentBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //品牌首字母
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);

        brandMapper.insertSelective(brandEntity);

        this.insertCategoryBrandData(brandDTO.getCategories(),brandEntity.getId());

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> editBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = TencentBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        this.deleteCategoryBrandByBrandId(brandEntity.getId());

        this.insertCategoryBrandData(brandDTO.getCategories(),brandEntity.getId());

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> deleteBrand(Integer brandId) {

        //删除品牌
        brandMapper.deleteByPrimaryKey(brandId);

        //通过品牌id删除分类
        this.deleteCategoryBrandByBrandId(brandId);

        return this.setResultSuccess();
    }

    private void deleteCategoryBrandByBrandId(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    private void insertCategoryBrandData(String categories,Integer id){
        if (StringUtils.isEmpty(categories)) throw new RuntimeException("分类不能为空");
//        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
        if(categories.contains(",")){
            String[] categoryArray = categories.split(",");
            /*for (String s : categoryArray) {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(brandEntity.getId());
                categoryBrandEntity.setCategoryId(Integer.valueOf(s));
                categoryBrandEntities.add(categoryBrandEntity);
            }*/
            List<CategoryBrandEntity> collect = Arrays.asList(categoryArray).stream().map(categoryIdStr -> {
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(id);
                categoryBrandEntity.setCategoryId(Integer.valueOf(categoryIdStr));
                return categoryBrandEntity;
            }).collect(Collectors.toList());
            categoryBrandMapper.insertList(collect);
            /*categoryBrandMapper.insertList(
                Arrays.asList(categoryArray)
                    .stream()
                    .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr),brandEntity.getId()))
                    .collect(Collectors.toList())
            );*/
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(id);
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }
}
