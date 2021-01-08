package com.tencent.shop.dto;

import com.tencent.shop.base.BaseDTO;
import com.tencent.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SpuDTO
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/5
 * @Version V1.0
 **/
@ApiModel(value = "spu数据传输DTO")
@Data
public class SpuDTO extends BaseDTO {

    @ApiModelProperty(value = "主键")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "标题")
    @NotEmpty(message = "标题不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String title;

    @ApiModelProperty(value = "子标题")
    private String subTitle;

    @ApiModelProperty(value = "1级类目id")
    @NotNull(message = "1级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid1;

    @ApiModelProperty(value = "2级类目id")
    @NotNull(message = "2级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid2;

    @ApiModelProperty(value = "3级类目id")
    @NotNull(message = "3级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid3;

    @ApiModelProperty(value = "商品所属品牌id")
    @NotNull(message = "商品所属品牌id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer brandId;

    @ApiModelProperty(value = "是否上架，0下架，1上架")
    @NotNull(message = "是否上架不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer saleable;

    @ApiModelProperty(value = "是否有效，0已删除，1有效")
    @NotNull(message = "是否有效不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer valid;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    private String categoryName;

    private String brandName;

    @ApiModelProperty(value = "大字段数据")
    private SpuDetailDTO spuDetail;

    @ApiModelProperty(value = "sku属性数据集合")
    private List<SkuDTO> skus;

}
