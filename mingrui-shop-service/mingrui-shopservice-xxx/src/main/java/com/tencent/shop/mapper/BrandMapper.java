package com.tencent.shop.mapper;

import com.tencent.shop.entity.BrandEntity;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName BrandMapper
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/25
 * @Version V1.0
 **/
public interface BrandMapper extends Mapper<BrandEntity> , SelectByIdListMapper<BrandEntity,Integer> {
}
