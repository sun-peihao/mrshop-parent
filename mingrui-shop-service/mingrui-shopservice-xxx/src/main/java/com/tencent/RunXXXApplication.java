package com.tencent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

import javax.swing.*;

/**
 * @ClassName RunXXXApplication
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/22
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.tencent.shop.mapper")
public class RunXXXApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunXXXApplication.class);
    }

}
