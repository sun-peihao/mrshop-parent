package com.tencent.shop.feign;

import com.tencent.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName SpecificationFeign
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/4
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "SpecificationService")
public interface SpecificationFeign extends SpecificationService {
}
