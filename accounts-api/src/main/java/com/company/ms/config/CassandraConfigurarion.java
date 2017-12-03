package com.company.ms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;

import com.company.ms.repositories.AccountRepository;

@Configuration
public class CassandraConfigurarion extends AbstractReactiveCassandraConfiguration {
	
	@Value("${cassandra.keyspace}")
	String keySpace;

	@Value("${cassandra.contactpoints}")
	String contactPoints;
	
	@Autowired
	AccountRepository accountRepository;

	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	protected String getContactPoints() {
		return contactPoints;
	}
}
