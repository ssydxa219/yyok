package com.yyok.compute;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */
@SpringBootApplication
//@EnableEurekaClient
//@EnableFeignClients
public class ComputeUIService {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ComputeUIService.class).web(true).run(args);
	}
}
