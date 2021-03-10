package com.tencent.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.component.MrRabbitMQ;
import com.tencent.shop.constant.MqMessageConstant;
import com.tencent.shop.dto.SkuDTO;
import com.tencent.shop.dto.SpuDTO;
import com.tencent.shop.dto.SpuDetailDTO;
import com.tencent.shop.entity.*;
import com.tencent.shop.mapper.*;
import com.tencent.shop.service.GoodsService;
import com.tencent.shop.status.HTTPStatus;
import com.tencent.shop.utils.JSONUtil;
import com.tencent.shop.utils.ObjectUtil;
import com.tencent.shop.utils.TencentBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/1/5
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private StockMapper stockMapper;

    @Autowired
    private MrRabbitMQ mrRabbitMQ;

    @Override
    @Transactional
    public Result<JSONUtil> downGoods(SpuDTO spuDTO) {

        SpuEntity spuEntity = TencentBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        if (spuEntity.getSaleable() == 1){
            spuEntity.setSaleable(0);
        }else{
            spuEntity.setSaleable(1);
        }

        spuMapper.updateByPrimaryKeySelective(spuEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONUtil> deleteGoods(Integer spuId) {

        //spu
        spuMapper.deleteByPrimaryKey(spuId);
        //spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuId = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuId);
        stockMapper.deleteByIdList(skuId);
        mrRabbitMQ.send(spuId + "",MqMessageConstant.SPU_ROUT_KEY_DELETE);
        return this.setResultSuccess();
    }

    public void deleteGoodsTransactional(){

    }

    @Override
    public Result<JSONUtil> editGoods(SpuDTO spuDTO) {

        Integer spuId = this.editGoodsTransactional(spuDTO);

        mrRabbitMQ.send(spuId+"",MqMessageConstant.SPU_ROUT_KEY_UPDATE);
        return this.setResultSuccess();
    }

    @Transactional
    public Integer editGoodsTransactional(SpuDTO spuDTO){
        final Date date = new Date();
        //spu
        SpuEntity spuEntity = TencentBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //spuDetail
        SpuDetailEntity spuDetailEntity = TencentBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //skus and stock
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuEntity.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuId = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuId);
        stockMapper.deleteByIdList(skuId);
        this.saveSkusAndStock(spuDTO,spuEntity.getId(),date);

        return spuEntity.getId();
    }

    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkuBySpuId(spuId);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())) {
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder())) {
            PageHelper.orderBy(spuDTO.getOrderBy());
        }

        Example example = new Example(SpuEntity.class);

        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2){
            example.createCriteria().andEqualTo("saleable",spuDTO.getSaleable());
        }

        if (!StringUtils.isEmpty(spuDTO.getTitle())){
            example.createCriteria().andLike("title","%"+spuDTO.getTitle()+"%");
        }

        if (ObjectUtil.isNotNull(spuDTO.getId())){
            example.createCriteria().andEqualTo("id",spuDTO.getId());
        }

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = TencentBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //1.
            /*CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(spuEntity.getCid1());
            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(spuEntity.getCid2());
            CategoryEntity categoryEntity3 = categoryMapper.selectByPrimaryKey(spuEntity.getCid3());
            spuDTO1.setCategoryName(categoryEntity.getName() + "/" + categoryEntity2.getName() + "/" + categoryEntity3.getName());*/

            //2.
            /*List<Integer> cidList = new ArrayList<>();
            cidList.add(spuEntity.getCid1());
            cidList.add(spuEntity.getCid2());
            cidList.add(spuEntity.getCid3());

            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(cidList);
            String categoryName = "";
            List<String> strings = new ArrayList<>();
            strings.set(0,"");
            categoryEntities.stream().forEach(categoryEntity -> {
                strings.set(0,strings.get(0) + categoryEntity.getName() + "/");
            });
            categoryName = strings.get(0).substring(0,strings.get(0).length());*/

            //3.
            List<Integer> cidList = Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3());
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(cidList);
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);

        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal() + "",spuDTOList);
    }

    @Override
    public Result<JSONUtil> saveGoods(SpuDTO spuDTO) {

        Integer spuId = this.saveGoodsTransactional(spuDTO);
        mrRabbitMQ.send(spuId+"", MqMessageConstant.SPU_ROUT_KEY_SAVE);
        return this.setResultSuccess();
    }

    @Transactional
    public Integer saveGoodsTransactional(SpuDTO spuDTO){
        //spu
        final Date date = new Date();
        SpuEntity spuEntity = TencentBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = TencentBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //sku
        this.saveSkusAndStock(spuDTO,spuEntity.getId(),date);

        return spuEntity.getId();
    }

    private void saveSkusAndStock(SpuDTO spuDTO,Integer spuId,Date date){
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = TencentBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
