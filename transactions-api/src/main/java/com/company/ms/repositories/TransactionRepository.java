package com.company.ms.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.Transaction;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {}
