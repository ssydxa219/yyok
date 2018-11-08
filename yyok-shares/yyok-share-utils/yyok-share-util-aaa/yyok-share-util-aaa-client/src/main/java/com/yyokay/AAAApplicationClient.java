package com.yyokay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description:
 * @author: linqinghong
 * ?* @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class AAAApplicationClient {

    public static void main(String[] args) {

        SpringApplication.run(AAAApplicationClient.class, args);
    }
}

