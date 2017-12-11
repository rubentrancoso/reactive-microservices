package com.company.ms.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.PaymentTracking;
import com.company.ms.entities.Transaction;

import reactor.core.publisher.Flux;

public interface PaymentTrackingRepository extends ReactiveCrudRepository<PaymentTracking, String> {
	
	@Query("SELECT * FROM paymenttracking WHERE credittransactionid = ?0")
	Flux<Transaction> findByCreditTransactionId(String accountId);
	
}
