package com.tencent.shop.dto;

import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpuDetailDTO
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/7
 * @Version V1.0
 **/
@ApiModel(value = "spu大字段数据传输类")
@Data
public class SpuDetailDTO {

    @ApiModelProperty(value = "spu主键",example = "1")
    @NotNull(message = "spu主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer spuId;

    @ApiModelProperty(value = "商品描述信息")
    private String description;

    @ApiModelProperty(value = "通用规格参数数据")
    @NotEmpty(message = "通用规格参数数据不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String genericSpec;

    @ApiModelProperty(value = "特有规格参数及可选值信息，json格式")
    @NotEmpty(message = "特有规格参数及可选值信息不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String specialSpec;

    @ApiModelProperty(value = "包装清单")
    private String packingList;

    @ApiModelProperty(value = "售后服务")
    private String afterService;

}
