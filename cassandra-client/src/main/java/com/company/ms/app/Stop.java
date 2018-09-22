package com.company.ms.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.company.ms.cassandra.CassandraClient;

@EnableAutoConfiguration(exclude={CassandraDataAutoConfiguration.class})
@ComponentScan(basePackages = { "com.company.ms" })
public class Stop {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Stop.class);

	@Value("${cassandra.keyspaces}")
	String keySpaces;

	@Value("${cassandra.contactpoints}")
	String contactPoints;

	@Autowired
	CassandraClient cassandraClient;

	public static void main(String[] args) {
		SpringApplication.run(Stop.class, args);
	}
	
	@Component
	public class ApplicationRunnerBean implements ApplicationRunner {

		@Override
		public void run(ApplicationArguments strArgs) throws Exception {
			LOGGER.info("stoping cassandra-client");
			cassandraClient.stoptCassandra();;
			LOGGER.info("All done.");
			System.exit(0);
		}
		
	}

}
