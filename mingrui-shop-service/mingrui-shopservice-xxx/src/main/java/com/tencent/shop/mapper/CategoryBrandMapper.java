package com.tencent.shop.mapper;

import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.entity.CategoryBrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @ClassName CategoryBrand
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/28
 * @Version V1.0
 **/
public interface CategoryBrandMapper extends Mapper<CategoryBrandEntity>, InsertListMapper<CategoryBrandEntity> {

    @Select(value = "select * from tb_brand t where t.id in (select cb.brand_id from tb_category_brand cb where cb.category_id = #{cid})")
    List<BrandEntity> getBrandInfoByCategoryId(Integer cid);
}
