package com.tencent.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.tencent.shop.base.BaseApiService;
import com.tencent.shop.base.Result;
import com.tencent.shop.constant.MrConstant;
import com.tencent.shop.dto.UserDTO;
import com.tencent.shop.entity.UserEntity;
import com.tencent.shop.mapper.UserMapper;
import com.tencent.shop.redis.repository.RedisRepository;
import com.tencent.shop.utils.BCryptUtil;
import com.tencent.shop.utils.LuosimaoDuanxinUtil;
import com.tencent.shop.utils.ObjectUtil;
import com.tencent.shop.utils.TencentBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2021/3/10
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<JSONObject> checkCode(String phone, String code) {

        String codeM = redisRepository.get(MrConstant.REDIS_DUANXIN_CODE_PRE + phone);

        if (code.equals(codeM)){
            return this.setResultSuccess();
        }
        return this.setResultError("验证码填写错误!");
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);
        //LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);
        log.info("向手机号码:{} 发送验证码:{}",userDTO.getPhone(),code);

        redisRepository.set(MrConstant.REDIS_DUANXIN_CODE_PRE + userDTO.getPhone(),code);
        redisRepository.expire(MrConstant.REDIS_DUANXIN_CODE_PRE + userDTO.getPhone(),60);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);

        if(ObjectUtil.isNotNull(value) && ObjectUtil.isNotNull(type)){
            example.createCriteria().andEqualTo(type == 1 ? "username" : "phone",value);
        }

        List<UserEntity> userEntities = userMapper.selectByExample(example);

        return this.setResultSuccess(userEntities);
    }

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = TencentBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setCreated(new Date());
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));

        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }
}
