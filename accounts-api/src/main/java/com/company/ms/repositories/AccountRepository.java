package com.company.ms.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.company.ms.entities.Account;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {}
