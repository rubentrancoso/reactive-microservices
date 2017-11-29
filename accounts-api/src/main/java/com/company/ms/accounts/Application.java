package com.company.ms.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Component;

import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.userapi.message.AccountData;

import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableAutoConfiguration
@EnableReactiveMongoRepositories(basePackageClasses = AccountRepository.class)
@EntityScan(basePackages = { "com.company.ms.entities" })
@ComponentScan(basePackages = { "com.company.ms" })
public class Application {

	@Autowired
	AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Component
	public class ApplicationRunnerBean implements ApplicationRunner {
		private final Logger logger = LoggerFactory.getLogger(ApplicationRunnerBean.class);

		@Override
		public void run(ApplicationArguments strArgs) throws Exception {

			logger.info("Application started...");
			accountRepository.deleteAll().subscribe();
			accountRepository.saveAll(Flux.just(new Account(0.0,0.0),new Account(0.0,0.0),new Account(0.0,0.0),new Account(0.0,0.0),new Account(0.0,0.0))).subscribe();		
			accountRepository.findAll().subscribe(a->System.out.println(((Account)a).toString()));
		}
	}

}
