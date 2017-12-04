package com.company.ms.services;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Transaction;
import com.company.ms.repositories.PaymentRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

import reactor.core.publisher.Mono;

@Service
public class SrvTransaction {

	private static final Logger logger = LoggerFactory.getLogger(SrvTransaction.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PaymentRepository paymentRepository;
	
	public Mono<Transaction> doTransaction(TransactionData transactionData) {
		logger.info(String.format("performing transaction: %s", transactionData.toString()));

		if (transactionData == null || transactionData.getAmount().equals(null)) {
			return null;
		}
		Transaction transaction = Transaction.newTransaction(
				transactionData.getAccount_id(),
				transactionData.getOperation_type_id(), 
				transactionData.getAmount().getAmount(), 
				transactionData.getAmount().getAmount() 
		);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		transaction.setEventDate(timestamp);
		transaction.setDueDate(timestamp);
		return transactionRepository.save(transaction);
	}
	
	public void doPayment(PaymentData[] paymentData) {
//		for(PaymentData payment: paymentData) {
//			Payment payment = Payment.newPayment(creditTransactionId, debitTransactionId, amount);
//			paymentRepository.save(payment);
//		}
	}	


}
