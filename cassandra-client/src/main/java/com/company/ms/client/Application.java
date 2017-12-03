package com.company.ms.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.company.ms.cassandra.CassandraClient;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.company.ms" })
public class Application {

	@Value("${cassandra.keyspace}")
	String keySpace;

	@Value("${cassandra.contactpoints}")
	String contactPoints;

	@Autowired
	CassandraClient cassandraClient;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Component
	public class ApplicationRunnerBean implements ApplicationRunner {
		private final Logger logger = LoggerFactory.getLogger(ApplicationRunnerBean.class);

		@Override
		public void run(ApplicationArguments strArgs) throws Exception {
			logger.info("Application started...");
			cassandraClient.connect(contactPoints);
			cassandraClient.getSession();
			cassandraClient.dropKeySpace(keySpace);
			cassandraClient.createKeySpace(keySpace);
			try {
				cassandraClient.createSchema("cassandra.cql");
			} catch (IOException e) {
				e.printStackTrace();
			}
			cassandraClient.closeSession();
			cassandraClient.close();
			System.exit(0);
		}
	}

}
