package com.tencent.shop.dto;

import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName StockDTO
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/7
 * @Version V1.0
 **/
@ApiModel(value = "库存数据传输类")
@Data
public class StockDTO {

    @ApiModelProperty(value = "库存对应的商品sku id",example = "1")
    @NotNull(message = "库存对应的商品sku id不能为空",groups = {MingruiOperation.Update.class})
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存",example = "1")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数量",example = "1")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存数量",example = "1")
    @NotNull(message = "库存数量不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer stock;
}
