package com.tencent.shop.service.impl;

import com.google.gson.JsonObject;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.entity.CategoryBrandEntity;
import com.tencent.shop.entity.CategoryEntity;
import com.tencent.shop.mapper.CategoryBrandMapper;
import com.tencent.shop.mapper.CategoryMapper;
import com.tencent.shop.service.CategoryService;
import com.tencent.shop.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/22
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<List<CategoryEntity>> getCateByIds(String ids) {

        List<Integer> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Integer.valueOf(idStr)).collect(Collectors.toList());
        List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(idList);
        return this.setResultSuccess(categoryEntities);
    }

    @Override
    @Transactional
    public Result<JsonObject> save(CategoryEntity categoryEntity) {

        if(categoryEntity.getIsParent() != 1){

            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setId(categoryEntity.getParentId());
            categoryEntity1.setIsParent(1);
            categoryMapper.updateByPrimaryKeySelective(categoryEntity1);

        }


        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId) {

        List<CategoryEntity> list = categoryMapper.getCategoryByBrandId(brandId);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> deleteCategoryById(Integer id) {

        if (ObjectUtil.isNotNull(id) || id > 0){

            //通过id查询当前节点信息
            CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

            //判断当前节点是否是父节点
            if (categoryEntity.getIsParent() == 1) return this.setResultError("当前节点为父节点");

            Example example = new Example(CategoryEntity.class);
            example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());

            Example example1 = new Example(CategoryBrandEntity.class);
            example1.createCriteria().andEqualTo("categoryId",id);
            List<CategoryBrandEntity> categoryBrandEntities = categoryBrandMapper.selectByExample(example1);
            if(categoryBrandEntities.size() >= 1) return this.setResultError("已被绑定，不能被删除");

            //通过当前节点的父节点的id 查询当前节点的父节点下是否还有其他子节点
            List<CategoryEntity> categoryList = categoryMapper.selectByExample(example);

            if(categoryList.size() <= 1){
                CategoryEntity categoryEntity1 = new CategoryEntity();
                categoryEntity1.setIsParent(0);
                categoryEntity1.setId(categoryEntity.getParentId());


                categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
            }

            categoryMapper.deleteByPrimaryKey(categoryEntity);
            return this.setResultSuccess();
        }

        return this.setResultError("id不合法");
    }

    @Override
    @Transactional
    public Result<JsonObject> updateCategoryById(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess();
    }
}
