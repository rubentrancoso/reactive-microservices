package com.company.ms.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

import com.company.ms.repositories.AccountRepository;

@EnableAutoConfiguration
@EnableReactiveCassandraRepositories(basePackageClasses = { AccountRepository.class })
@EntityScan(basePackages = { "com.company.ms.entities" })
@ComponentScan(basePackages = { "com.company.ms" })
public class Application {

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
