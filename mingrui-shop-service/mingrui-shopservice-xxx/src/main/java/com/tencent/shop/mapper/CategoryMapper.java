package com.tencent.shop.mapper;

import com.tencent.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName CategoryMapper
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/22
 * @Version V1.0
 **/
public interface CategoryMapper extends Mapper<CategoryEntity>, SelectByIdListMapper<CategoryEntity,Integer> {
    //通过品牌id查询分类信息
    @Select(value = "select id,name from tb_category where id in (select category_id from tb_category_brand where brand_id = #{brandId})")
    List<CategoryEntity> getCategoryByBrandId(Integer brandId);

}
