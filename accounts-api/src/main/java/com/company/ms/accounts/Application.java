package com.company.ms.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.company.ms.repositories.AccountRepository;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = { AccountRepository.class })
@EntityScan(basePackages = { "com.company.ms.entities" })
@ComponentScan(basePackages = {
		"com.company.ms.config", 
		"com.company.ms.audit",
		"com.company.ms.services", 
		"com.company.ms.userapi.endpoints"
	}
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
