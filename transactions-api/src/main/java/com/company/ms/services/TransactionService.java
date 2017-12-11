package com.company.ms.services;

import java.sql.Timestamp;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.stereotype.Service;

import com.company.ms.entities.PaymentTracking;
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

		validateTransactionValueByType(transactionData);
		Transaction transaction = Helper.genTransactionObj(transactionData);
		return transactionRepository.save(transaction);
		// TODO: abater o valor  do limite
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
	
	public void addTransactionGroup(TransactionData[] transactionsData) throws Exception {
		logger.info("addTransactionGroup");
		
		for (TransactionData transactionData : transactionsData) {
			validateTransactionValueByType(transactionData);
		}
		Flux<TransactionData> transaction_data_flux = Flux.fromArray(transactionsData);
		// save transaction group
		Flux<Transaction> transactions_flux = insetTransactions(transaction_data_flux);
		// Consolidate accounts
		Flux<Transaction> consolidate_flux = consolidateAccounts(transactions_flux);
		// TODO: Update Accounts Limit
		consolidate_flux.subscribe();
	}
	
	public void addPayments(PaymentData[] paymentsData) throws Exception {
		
		for (PaymentData paymentData : paymentsData) {
			validatePaymentValue(paymentData);
		}		
		Flux<PaymentData> payments_data_flux = Flux.fromArray(paymentsData);
		// Add payment transactions
		Flux<Transaction> payments_flux = insertPayments(payments_data_flux);
		// Consolidate accounts
		Flux<Transaction> consolidate_flux = consolidateAccounts(payments_flux);
		// TODO: Update Accounts Limit
		consolidate_flux.subscribe();
		
	}

	private Flux<Transaction> insertPayments(Flux<PaymentData> paymentDataFlux) {
		// Add payment transactions
		return paymentDataFlux
			.map(paymentData->{
				Transaction payment = Helper.genTransactionObj(paymentData);
				return transactionRepository.save(payment);
			})
			.flatMap(transaction->transaction);
	}
	
	private Flux<Transaction> consolidateAccounts(Flux<Transaction> transaction_flux) {
		return transaction_flux
			.map(transaction->{ 
				consolidateAccount(transaction.getAccountId());
				return transaction;
			});
	}
	
	private void consolidateAccount(String accountId) {
		// obter os pagamento com balance positivo
		Flux<Transaction> available_payments_flux = positiveBalancePayments(accountId);
		available_payments_flux
			.map(payment->{
				return balanceDebits(payment);
			})
			.map(reduced_payment->{
				return reduced_payment.map(payment->{
					return balanceCredits(payment);
				}).subscribe();
			}).subscribe();
	}	
	
	private Double balanceCredits(Transaction payment) {
		Double payed = payment.getAmount()-payment.getBalance();
		transactionRepository.save(payment).subscribe();
		return payed;
	}	
	
	private Mono<Transaction> balanceDebits(Transaction payment) {
		Flux<Transaction> debits = transactionRepository.findAByChargeOrder(payment.getAccountId());
		UpdateDebitTransaction updateDebitTransaction = new UpdateDebitTransaction();
		return debits
			.filter(debit_transaction->{
				return debit_transaction.getBalance()>0 && debit_transaction.getOperationTypeId()!=4;
			})
			.reduce(payment, updateDebitTransaction);
	}
	
	private Flux<Transaction> positiveBalancePayments(String accountId) {
		Flux<Transaction> payments = transactionRepository.findAllPayments(accountId);
		return payments
			.filter(payment->{
				return payment.getBalance()>0;
			});
	}

	private class UpdateDebitTransaction implements BiFunction<Transaction, Transaction, Transaction> {
		@Override
		public Transaction apply(Transaction payment_transaction, Transaction debit_transaction) {
			Double amount_payed = 0.0;
			Double debit_balance = debit_transaction.getBalance();
			Double payment_balance = payment_transaction.getBalance();
			if(debit_balance>0 && payment_balance > 0.0) {
				// debit is greater than available payment
				if(debit_balance >= payment_balance) {
					debit_balance -= payment_balance;
					amount_payed = payment_balance;
					payment_balance = 0.0;
				// debit is lower than available payment
				} else {
					payment_balance -= debit_balance;
					amount_payed = debit_balance;
					debit_balance = 0.0;
				}
				payment_transaction.setBalance(payment_balance);
				
				// Generate Tracking
				if(amount_payed>0.0) {
					PaymentTracking paymentTracking = PaymentTracking.newPaymentTracking(payment_transaction.getTransactionId(), debit_transaction.getTransactionId(), amount_payed);
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					paymentTracking.setEventDate(timestamp);
					paymentTrackingRepository.save(paymentTracking).subscribe();
					// Update Debit balance with ammount payed
					debit_transaction.setBalance(debit_balance);
					transactionRepository.save(debit_transaction).subscribe();
				}
			}
			return payment_transaction;
		}
	}

	public Flux<Transaction> listTransactionsFromAccount(String account_id) {
		Flux<Transaction> result = transactionRepository.findByAccountId(account_id);
		return result;
	}
	
	private Flux<Transaction> insetTransactions(Flux<TransactionData> transaction_data_flux) {
		return transaction_data_flux
		.flatMap(transactionData -> {
			Transaction transaction = Helper.genTransactionObj(transactionData);
			return transactionRepository.save(transaction);
		});
		// TODO: update Limits
	}

	public Object listPaymentTransactionsFromAccount(String account_id) {
		return getPaymentsByAccount(account_id);
	}
	
	private Flux<Transaction> getPaymentsByAccount(String account_id) {
		Flux<Transaction> result = transactionRepository
				.findByAccountId(account_id)
				.filter(t->t.getOperationTypeId() == OperationType.PAGAMENTO.operationType());
		return result;
	}

	public void cleartables() {
		transactionRepository.deleteAll().subscribe();
		paymentTrackingRepository.deleteAll().subscribe();
	}

}
