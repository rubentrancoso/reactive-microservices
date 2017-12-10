package com.company.ms.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.TransactionOrder;

public interface TransactionOrderRepository extends ReactiveCrudRepository<TransactionOrder, String> {

}
