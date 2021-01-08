package com.tencent.shop.mapper;

import com.tencent.shop.entity.StockEntity;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName StockMapper
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/7
 * @Version V1.0
 **/
public interface StockMapper extends Mapper<StockEntity>, DeleteByIdListMapper<StockEntity,Long> {
}
