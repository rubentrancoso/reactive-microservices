package com.company.ms.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;

@SpringBootApplication
@EnableAutoConfiguration
@EnableReactiveCassandraRepositories(basePackageClasses = { TransactionRepository.class, PaymentTrackingRepository.class  })
@EntityScan(basePackages = { "com.company.ms.entities" })
@ComponentScan(basePackages = { "com.company.ms" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
