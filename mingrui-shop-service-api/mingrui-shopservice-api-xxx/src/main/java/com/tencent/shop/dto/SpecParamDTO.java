package com.tencent.shop.dto;

import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecParamDTO
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/4
 * @Version V1.0
 **/
@ApiModel(value = "规格参数")
@Data
public class SpecParamDTO {

    @ApiModelProperty(name = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer id;

    @ApiModelProperty(name = "商品分类id",example = "1")
    @NotNull(message = "商品分类id不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer cid;

    @ApiModelProperty(name = "规格组id",example = "1")
    @NotNull(message = "规格组id不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer groupId;

    @ApiModelProperty(name = "参数名",example = "1")
    @NotEmpty(message = "参数名不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private String name;

    @ApiModelProperty(name = "是否是数字类型参数,1->true 0->false",example = "0")
    @NotNull(message = "是否是数字类型参数不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Boolean numeric;

    @ApiModelProperty(name = "数字类型参数的单位",example = "1")
    @NotNull(message = "数字类型参数的单位不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private String unit;

    @ApiModelProperty(name = "是否是sku通用属性，1-》true 0-》false",example = "0")
    @NotNull(message = "是否是sku通用属性不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Boolean generic;

    @ApiModelProperty(name = "是否用于搜索过滤，1-》true 0-》false",example = "0")
    @NotNull(message = "是否是sku通用属性不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Boolean searching;

    @ApiModelProperty(value = "数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0")
    private String segments;



}
