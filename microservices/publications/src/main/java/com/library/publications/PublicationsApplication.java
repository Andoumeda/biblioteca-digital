package com.library.publications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackages = {"com.library.entities"})
@EnableCaching
public class PublicationsApplication {
	public static void main(String[] args) {
		SpringApplication.run(PublicationsApplication.class, args);
	}
}