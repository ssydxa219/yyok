package com.yyok.home;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */
@SpringBootApplication
//@EnableEurekaClient
//@EnableFeignClients
public class HomeUIService {
	public static void main(String[] args) {
		new SpringApplicationBuilder(HomeUIService.class).web(true).run(args);
	}
}
