package com.tencent.shop.feign;

import com.tencent.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName CategoryFeign
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/6
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "CategoryService")
public interface CategoryFeign extends CategoryService {
}
