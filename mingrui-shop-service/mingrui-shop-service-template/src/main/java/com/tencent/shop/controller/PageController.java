package com.tencent.shop.controller;

import com.tencent.shop.service.PageService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/8
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item")
public class PageController {

    //@Autowired
    private PageService pageService;

    //@GetMapping(value = "{spuId}.html")
    public String test(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap){

        Map<String,Object> map = pageService.getGoodsInfo(spuId);

        modelMap.putAll(map);
        return "item";
    }

}
