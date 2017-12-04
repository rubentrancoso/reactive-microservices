// https://github.com/oscerd/cassandra-java-example/blob/master/src/main/java/com/github/oscerd/cassandra/SimpleClient.java
package com.company.ms.cassandra;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.company.ms.client.Application.ApplicationRunnerBean;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

@Component
public class CassandraClient {

	private final Logger logger = LoggerFactory.getLogger(ApplicationRunnerBean.class);

	@Value("${cassandra.contactpoints}")
	String contactPoints;

	private Cluster cluster;
	private Session session;

	public void connect(String _contactPoints) {
		String[] contactPoints = _contactPoints.split(",");
		for (String contactPoint : contactPoints) {
			cluster = Cluster.builder().addContactPoint(contactPoint).build();
		}
		Metadata metadata = cluster.getMetadata();
		logger.info("Connected to cluster:" + metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			logger.info("Datatacenter: " + host.getDatacenter() + "; Host: " + host.getAddress() + "; Rack: " + host.getRack());
		}
	}

	public void getSession() {
		session = cluster.connect();
	}

	public void closeSession() {
		session.close();
	}

	public void close() {
		cluster.close();
	}

	public void createKeySpace(String keyspace) {
		String command1 = "CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
		String command2 = "use " + keyspace + ";";
		logger.info("\n\n" + command1.trim() + "\n");
		session.execute(command1);
		logger.info("\n\n" + command2.trim() + "\n");
		session.execute(command2);
	}

	public void dropKeySpace(String keyspace) {
		String command = "DROP KEYSPACE IF EXISTS " + keyspace + ";";
		logger.info("\n\n" + command.trim() + "\n");
		session.execute(command);
	}

	public void createSchema(String schemaFile) throws IOException {
		File file = new File("schema/" + schemaFile);
		Scanner sc = new Scanner(file);
		String content = sc.useDelimiter("\\Z").next();
		String commands[] = content.split(";"); 
		for(String command: commands) {
			String cmd = command.trim();
			if(!cmd.isEmpty()) {
				session.execute(cmd);
				logger.info("\n\n" + cmd + "\n");
			}
			
		}
		sc.close();
	}

}
