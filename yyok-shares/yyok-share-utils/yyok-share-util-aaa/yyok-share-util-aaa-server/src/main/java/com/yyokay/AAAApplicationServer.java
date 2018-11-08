package com.yyokay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/* @author: linqinghong
 * @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@SpringBootApplication
@EnableEurekaServer
public class AAAApplicationServer {

    public static void main(String[] args) {

        SpringApplication.run(AAAApplicationServer.class, args);
    }
}

