package com.tencent.response;

import com.tencent.shop.base.Result;
import com.tencent.shop.document.GoodsDoc;
import com.tencent.shop.entity.BrandEntity;
import com.tencent.shop.entity.CategoryEntity;
import com.tencent.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsResponse
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/6
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    private Map<String, List<String>> specMap;

    public GoodsResponse(Long total,Long totalPage,List<BrandEntity> brandList,List<CategoryEntity> categoryList,List<GoodsDoc> goodsDoc,Map<String, List<String>> specMap){
        super(HTTPStatus.OK, "",goodsDoc);
        this.total = total;
        this.totalPage = totalPage;
        this.brandList = brandList;
        this.categoryList = categoryList;
        this.specMap = specMap;
    }

}
