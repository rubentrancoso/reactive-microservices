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
		accountToConsolidate.map(accountId->consolidate(accountId)).subscribe();
	}

	private String consolidate(String accountId) {
		// reunir o credito acumulado de todos os pagamentos não negativos por ordem
		gatherAvailableCredit(accountId)
		.map(credit_value->{
			// pagar os debitos e obter o montante pago
			updateBalanceAgainstPayment(accountId, credit_value)
			// atualizar com o montante pago o balanco dos pagamentos utilizados distribuindo a partir do mais antigo
			.map(payedAmount->{
					updatePayments(accountId, payedAmount);
					return payedAmount;
			})
			.subscribe();
			return credit_value;
		})
		.subscribe();
		return accountId;
	}
	
	private void updatePayments(String accountId, Double payedAmount) {
		Flux<Transaction> payments = transactionRepository.findAllPayments(accountId);
		payments
			.filter(payment->{
				return payment.getBalance()>0;
			})
			.scan(payedAmount, new UpdatePayments())
			.subscribe();
	}

	private class UpdatePayments implements BiFunction<Double, Transaction, Double> {
		@Override
		public Double apply(Double payedAmount, Transaction payment) {
			Double payment_balance = payment.getBalance();
			if(payment_balance>0 && payedAmount > 0.0) {
				// debit is greater than available payment
				if(payment_balance >= payedAmount) {
					payment_balance -= payedAmount;
					payedAmount = 0.0;
				// debit is lower than available payment
				} else {
					payedAmount -= payment_balance;
					payment_balance = 0.0;
				}
				payment.setBalance(payment_balance);
				transactionRepository.save(payment).subscribe();
			}
			return payedAmount;
		}
	}
	
	private Mono<Double> gatherAvailableCredit(String accountId) {
		Flux<Transaction> payments = getPaymentsByWithPositiveBalance(accountId);
		return payments
			.filter(transaction->{
				return transaction.getBalance()>0;
			})
			.scan(0.0, new GatherCredit())
			.last();
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

	private Mono<Double> updateBalanceAgainstPayment(String accountId, Double credit) {
		Flux<Transaction> transactions = transactionRepository.findByAccountId(accountId);
		UpdateBalanceAgainstPaymentAccumulator accumulate = new UpdateBalanceAgainstPaymentAccumulator();
		return transactions
			.filter(transaction_to_filter->transaction_to_filter.getOperationTypeId()!=4 && transaction_to_filter.getBalance() > 0)
			.scan(credit, accumulate)
			.last().map(reminder->credit-reminder);
	}

	private class GatherCredit implements BiFunction<Double, Transaction, Double> {
		@Override
		public Double apply(Double credit, Transaction transaction) {
			credit += transaction.getBalance();
			return credit;
		}
	}
	
	private class UpdateBalanceAgainstPaymentAccumulator implements BiFunction<Double, Transaction, Double> {
		@Override
		public Double apply(Double credit, Transaction transaction) {
			Double transaction_balance = transaction.getBalance();
			// theres money and theres debit to reedem
			if (transaction_balance > 0 && credit > 0.0) {
				// debit is greater than available payment
				if(transaction_balance >= credit) {
					transaction_balance -= credit;
					credit = 0.0;
				// debit is lower than available payment
				} else {
					credit -= transaction_balance;
					transaction_balance = 0.0;
				}
				transaction.setBalance(transaction_balance);
				transactionRepository.save(transaction).subscribe();
			}
			logger.info("transaction = " + transaction.toString());
			return credit;
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
		return getPaymentsByAccount(account_id);
	}
	
	private Flux<Transaction> getPaymentsByWithPositiveBalance(String account_id) {
		Flux<Transaction> result = transactionRepository.findAllPayments(account_id);
		return result;
	}

	private Flux<Transaction> getPaymentsByAccount(String account_id) {
		Flux<Transaction> result = transactionRepository
				.findByAccountId(account_id)
				.filter(t->t.getOperationTypeId() == OperationType.PAGAMENTO.operationType());
		return result;
	}

}
