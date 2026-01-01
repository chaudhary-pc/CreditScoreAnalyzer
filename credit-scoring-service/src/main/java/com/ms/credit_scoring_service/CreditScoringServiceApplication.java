package com.ms.credit_scoring_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
// @EnableCaching // Disabled for now
public class CreditScoringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditScoringServiceApplication.class, args);
	}

}
