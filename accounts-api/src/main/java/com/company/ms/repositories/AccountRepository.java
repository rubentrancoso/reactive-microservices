package com.company.ms.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.Account;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {}
