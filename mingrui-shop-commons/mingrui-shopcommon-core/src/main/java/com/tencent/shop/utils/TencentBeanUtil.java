package com.tencent.shop.utils;

import org.springframework.beans.BeanUtils;

/**
 * @ClassName TencentBeanUtil
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/25
 * @Version V1.0
 **/
public class TencentBeanUtil<T> {

        public static <T> T copyProperties(Object source,Class<T> classes){
            try {
                T t = classes.newInstance();
                BeanUtils.copyProperties(source,t);
                return t;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

}
