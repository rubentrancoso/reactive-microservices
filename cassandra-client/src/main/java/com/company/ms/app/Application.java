package com.company.ms.app;

import java.io.IOException;

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
public class Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunnerBean.class);

	@Value("${cassandra.keyspaces}")
	String keySpaces;

	@Value("${cassandra.contactpoints}")
	String contactPoints;

	@Autowired
	CassandraClient cassandraClient;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Component
	public class ApplicationRunnerBean implements ApplicationRunner {

		@Override
		public void run(ApplicationArguments strArgs) throws Exception {
			LOGGER.info("starting cassandra-client");
			cassandraClient.startCassandra();
			while(!cassandraClient.serverListening("localhost", 9042)) {
				LOGGER.info("waiting for cassandra...");
				Thread.sleep(2000);
			};
			LOGGER.info("port 9042 is open.");
			boolean wait = true;
			while(wait) {
				try {
					cassandraClient.connect(contactPoints);
				} catch (Exception e) {
					LOGGER.info("trying to connect (4s)...");;
					Thread.sleep(4000);
					continue;
				}
				wait = false;
			}
			cassandraClient.getSession();
			String keyspaces[] = keySpaces.split(",");
			for(String keyspace: keyspaces) {
				createSchema(keyspace.trim());
			}
			cassandraClient.closeSession();
			cassandraClient.close();
			LOGGER.info("All done.");
			System.exit(0);
		}
		
		private void createSchema(String keyspace) {
			cassandraClient.dropKeySpace(keyspace);
			cassandraClient.createKeySpace(keyspace);
			try {
				cassandraClient.createSchema(keyspace + ".cql");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
