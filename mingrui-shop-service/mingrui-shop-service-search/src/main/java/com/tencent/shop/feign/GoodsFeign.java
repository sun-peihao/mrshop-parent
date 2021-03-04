package com.tencent.shop.feign;

import com.tencent.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/4
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server")
public interface GoodsFeign extends GoodsService {
}
