package com.tencent.shop.mapper;

import com.tencent.shop.dto.SkuDTO;
import com.tencent.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName SkuMapper
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/7
 * @Version V1.0
 **/
public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {

    @Select(value = "select k.*,t.stock from tb_sku k,tb_stock t where k.id = t.sku_id AND k.spu_id = #{spuId}")
    List<SkuDTO> getSkuBySpuId(Integer spuId);

}
