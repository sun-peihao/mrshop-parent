package com.tencent.sph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import javax.swing.*;

/**
 * @ClassName RunEurekaServerApplication
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/22
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaServer
public class RunEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunEurekaServerApplication.class);
    }

}
