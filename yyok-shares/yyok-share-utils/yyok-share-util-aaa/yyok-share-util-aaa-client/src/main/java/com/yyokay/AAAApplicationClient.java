package com.yyokay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description:
 * @author: linqinghong
 * @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.yyokay.admin.mapper")
public class AAAApplicationClient {

    public static void main(String[] args) {

        SpringApplication.run(AAAApplicationClient.class, args);
    }
}

