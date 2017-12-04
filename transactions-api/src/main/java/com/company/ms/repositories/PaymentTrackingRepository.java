package com.company.ms.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.company.ms.entities.PaymentTracking;

public interface PaymentTrackingRepository extends ReactiveCrudRepository<PaymentTracking, String> {}
