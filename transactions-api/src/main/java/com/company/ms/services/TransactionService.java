package com.company.ms.services;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Transaction;
import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.types.OperationType;
import com.company.ms.userapi.endpoints.Helper;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	ReactiveCassandraTemplate template;
	PaymentTrackingRepository paymentTrackingRepository;
	TransactionRepository transactionRepository;
	
	TransactionService(ReactiveCassandraTemplate _template, PaymentTrackingRepository _paymentTrackingRepository, TransactionRepository _transactionRepository ) {
		template = _template;
		paymentTrackingRepository = _paymentTrackingRepository;
		transactionRepository = _transactionRepository;
	}

	public Mono<Transaction> addSingleTransaction(TransactionData transactionData) throws Exception {
		logger.info(String.format("performing transaction: %s", transactionData.toString()));

//		if (transactionData == null || transactionData.getAmount().equals(null)) {
//			throw new Exception("Error invalid transaction");
//		}
//		if(transactionData.getOperation_type_id()==OperationType.PAGAMENTO.operationType()) {
//			throw new Exception(String.format("Error [%s](%d) invalid ENDPOINT for PAYMENT",OperationType.PAGAMENTO.description(), OperationType.PAGAMENTO.operationType(), transactionData.getAmount(), transactionData.getAccount_id()));
//		}

		validateTransactionValueByType(transactionData);
		Transaction transaction = Helper.genTransactionObj(transactionData);
		return transactionRepository.save(transaction);
	}
	
	private void validateTransactionValueByType(TransactionData transactionData) throws Exception {
		OperationType transaction_operationType = OperationType.getOperationType(transactionData.getOperation_type_id());
		if(transactionData.getAmount()<=0) {
			throw new Exception(String.format("Error [%s](%d) invalid ZERO/NEGATIVE amount=[% ,.2f] account_id=[%s]",transaction_operationType.description(), transaction_operationType.operationType(), transactionData.getAmount(), transactionData.getAccount_id()));
		}
		if(transaction_operationType==OperationType.PAGAMENTO) {
			throw new Exception(String.format("Error [%s](%d) invalid ENDPOINT for PAYMENT",transaction_operationType.description(), transaction_operationType.operationType(), transactionData.getAmount(), transactionData.getAccount_id()));
		}
	}
	
	private void validatePaymentValue(PaymentData paymentData) throws Exception {
		OperationType payment_operationType = OperationType.PAGAMENTO;
		if(paymentData.getAmount()<=0) {
			throw new Exception(String.format("Error [%s](%d) invalid ZERO/NEGATIVE amount=[% ,.2f] account_id=[%s]",payment_operationType.description(), payment_operationType.operationType(), paymentData.getAmount(),paymentData.getAccount_id()));
		}
	}	

	public void addPayments(PaymentData[] paymentsData) throws Exception {
		
		Set<String> accounts = new HashSet<String>(); 
		for (PaymentData paymentData : paymentsData) {
			validatePaymentValue(paymentData);
			accounts.add(paymentData.getAccount_id());
		}		
		// Add payment transactions
		insertPayments(paymentsData);
		// TODO: Update Accounts Limit
		
		// Consolidate accounts
		consolidateAccounts(accounts);
	}
	
	private void consolidateAccounts(Set<String> accounts) {
		Flux<String> accountToConsolidate = Flux.fromIterable(accounts);
		accountToConsolidate.map(accountId->consolidate(accountId));
	}

	private String consolidate(String accountId) {
		// TODO: pegar o credito acumulado de todos os pagamentos não negativos por ordem
		// TODO: pagar os debitos
		// TODO: atualizar o balanco dos pagamentos utilizados
		return accountId;
	}
	
	private void insertPayments(PaymentData[] paymentsData) {
		Flux<PaymentData> paymentFlux = Flux.fromArray(paymentsData);
		// Add payment transactions
		paymentFlux
			.flatMap(paymentData->{
				Transaction payment = Helper.genTransactionObj(paymentData);
				return transactionRepository.save(payment);
		}).subscribe();
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

	private void updateBalanceAgainstPayment(Transaction transaction) {
		Double payment_reminder = transaction.getAmount();
		Flux<Transaction> transactions = transactionRepository.findByAccountId(transaction.getAccountId());
		UpdateBalanceAgainstPaymentAccumulator accumulate = new UpdateBalanceAgainstPaymentAccumulator();
		transactions
			.filter(transaction_to_filter->transaction_to_filter.getOperationTypeId()!=4 && transaction_to_filter.getBalance() > 0)
			.scan(payment_reminder, accumulate)
			//.doOnComplete(onComplete)
			.subscribe();
	}
	
	private class UpdateBalanceAgainstPaymentAccumulator implements BiFunction<Double, Transaction, Double> {

		@Override
		public Double apply(Double payment_reminder, Transaction transaction) {
			Double transaction_balance = transaction.getBalance();
			// theres money and theres debit to reedem
			if (transaction_balance > 0 && payment_reminder > 0.0) {
				// debit is greater than available payment
				if(transaction_balance >= payment_reminder) {
					transaction_balance -= payment_reminder;
					payment_reminder = 0.0;
				// debit is lower than available payment
				} else {
					payment_reminder -= transaction_balance;
					transaction_balance = 0.0;
				}
				transaction.setBalance(transaction_balance);
				transactionRepository.save(transaction).subscribe();
			}
			logger.info("transaction = " + transaction.toString());
			return payment_reminder;
		}
	}

	public Flux<Transaction> listTransactionsFromAccount(String account_id) {
		Flux<Transaction> result = transactionRepository.findByAccountId(account_id);
		return result;
	}

	public void addTransactionGroup(TransactionData[] transactionsData) throws Exception {
		logger.info("addTransactionGroup");
		
		for (TransactionData transactionData : transactionsData) {
			validateTransactionValueByType(transactionData);
		}
		// save transaction group
		insetTransactions(transactionsData);
	}
	
	private void insetTransactions(TransactionData[] transactionsData) {
		Flux<TransactionData> transactionGroup = Flux.fromArray(transactionsData);
		transactionGroup
		.flatMap(transactionData -> {
			Transaction transaction = Helper.genTransactionObj(transactionData);
			return transactionRepository.save(transaction);
		}).subscribe();
		// TODO: update Limits
	}

	public Object listPaymentTransactionsFromAccount(String account_id) {
		Flux<Transaction> result = transactionRepository
				.findByAccountId(account_id)
				.filter(t->t.getOperationTypeId() == OperationType.PAGAMENTO.operationType());
		return result;
	}

}
