package com.company.ms.services;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Payment;
import com.company.ms.entities.Transaction;
import com.company.ms.repositories.PaymentRepository;
import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	ReactiveCassandraTemplate template;
	PaymentRepository paymentRepository;
	PaymentTrackingRepository paymentTrackingRepository;
	TransactionRepository transactionRepository;
	
	TransactionService(ReactiveCassandraTemplate _template, PaymentRepository _paymentRepository, PaymentTrackingRepository _paymentTrackingRepository, TransactionRepository _transactionRepository ) {
		template = _template;
		paymentRepository = _paymentRepository;
		paymentTrackingRepository = _paymentTrackingRepository;
		transactionRepository = _transactionRepository;
	}

	public Mono<Transaction> addSingleTransaction(TransactionData transactionData) {
		logger.info(String.format("performing transaction: %s", transactionData.toString()));

		if (transactionData == null || transactionData.getAmount().equals(null)) {
			return null;
		}
		Transaction transaction = Transaction.newTransaction(transactionData.getAccount_id(),
				transactionData.getOperation_type_id(), transactionData.getAmount().getAmount(),
				transactionData.getAmount().getAmount());

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		transaction.setEventDate(timestamp);
		transaction.setDueDate(timestamp);
		return transactionRepository.save(transaction);
	}

	public void addPayment(PaymentData[] paymentsData) {
		Flux.fromArray(paymentsData)
				// save payment
				.map(paymentData -> {
					Payment payment = genPaymentObj(paymentData);
					paymentRepository.save(payment);
					return payment;
				})
				// update balance
				.map(payment -> {
					updateBalance(payment);
					return payment;
				}).subscribe();
	}

	private Payment genPaymentObj(PaymentData paymentData) {
		Payment payment = Payment.newPayment(paymentData.getAccount_id(), paymentData.getAmount().getAmount());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		payment.setEventDate(timestamp);
		return payment;
	}

	// private PaymentTracking paymentTrackingObjFromData(PaymentData
	// paymentTrackingData) {
	// PaymentTracking paymentTracking =
	// PaymentTracking.newPaymentTracking(creditTransactionId, debitTransactionId,
	// amount);
	// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	// paymentTracking.setEventDate(timestamp);
	// return paymentTracking;
	// }

	private void updateBalance(Payment payment) {
		Double amount = payment.getAmount();
		transactionRepository.findByAccountOrderByEvent(payment.getAccountId()).map(transaction -> {
			Double balance = transaction.getBalance();
			if (amount > 0.0 && balance < 0) {
				//Double reminder = balance % amount;
				transaction.setBalance(balance);
			}
			logger.info("transaction = " + transaction.toString());
			return transaction;
		}).subscribe();
	}

	public Flux<Transaction> listTransactionsFromAccount(String account_id) {
		Flux<Transaction> result = transactionRepository.findByAccountOrderByEvent(account_id);
		return result;
	}

	public void addTransactionGroup(TransactionData[] transactionsData) {
		logger.info("addTransactionGroup");
		
		// save transaction group
		Flux.fromArray(transactionsData)
		.flatMap(transactionData -> {
			Transaction transaction = genTransactionObj(transactionData);
			return transactionRepository.save(transaction);
		}).subscribe();
	}

	private Transaction genTransactionObj(TransactionData transactionData) {
		Transaction transaction = Transaction.newTransaction(transactionData.getAccount_id(),
				transactionData.getOperation_type_id(), transactionData.getAmount().getAmount(),
				transactionData.getAmount().getAmount());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		transaction.setEventDate(timestamp);
		return transaction;
	}

}
