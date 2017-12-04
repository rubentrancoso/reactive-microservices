package com.company.ms.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.Transaction;

import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
	
	@Query("SELECT * FROM transaction WHERE accountid = ?0 ORDER BY chargeorder asc")
	Flux<Transaction> findByAccountOrderByEvent(String acoountId);
	
}
