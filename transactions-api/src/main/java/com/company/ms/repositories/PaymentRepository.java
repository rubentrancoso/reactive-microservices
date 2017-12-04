package com.company.ms.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.Payment;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {}
