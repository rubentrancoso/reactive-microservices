package com.company.ms.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.Transaction;

import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {

	@Query("SELECT * FROM transaction WHERE accountid = ?0")
	Flux<Transaction> findByAccountId(String accountId);

	@Query("SELECT * FROM payment WHERE accountid = ?0")
	Flux<Transaction> findAllPayments(String account_id);

	@Query("SELECT * FROM transactionbychargeorder WHERE accountid = ?0")
	Flux<Transaction> findAByChargeOrder(String account_id);

	
}
