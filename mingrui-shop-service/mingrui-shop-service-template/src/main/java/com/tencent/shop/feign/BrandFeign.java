package com.tencent.shop.feign;

import com.tencent.shop.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName BrandFeign
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/6
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "BrandService")
public interface BrandFeign extends BrandService {
}
