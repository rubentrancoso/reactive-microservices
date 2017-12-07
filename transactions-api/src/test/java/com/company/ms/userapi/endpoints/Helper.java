package com.company.ms.userapi.endpoints;

import java.sql.Timestamp;
import java.util.Random;

import com.company.ms.entities.Transaction;
import com.company.ms.helper.UUIDGen;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.types.OperationType;
import com.company.ms.userapi.message.Amount;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

public class Helper {

	public static Transaction generateRandomTransaction() {
		String accountId = UUIDGen.getUUID();
		OperationType operationType = generateRandomPaymentType();
		Double amount = generateRandomDouble(0.0, 1000.0);
		Double balance = generateRandomDouble(0.0, 1000.0);
		return Transaction.newTransaction(accountId, operationType.operationType(), amount, balance);
	}

	public static TransactionData[] generateRandomTransactionDataArray(int size) {
		TransactionData[] transactionData = new TransactionData[size];
		for(int i=0;i<size;i++) {
			transactionData[i] = generateRandomTransactionData();
		}
		return transactionData;
	}
	
	public static TransactionData generateRandomTransactionData() {
		TransactionData transactionData = new TransactionData();
		transactionData.setAccount_id(UUIDGen.getUUID());
		transactionData.setOperation_type_id(generateRandomPaymentType().operationType());
		transactionData.setAmount(new Amount(generateRandomDouble(0.0, 1000.0)));
		return transactionData;
	}

	public static Double generateRandomDouble(Double rangeMin, Double rangeMax) {
		Random r = new Random();
		Double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		return randomValue;
	}

	public static OperationType generateRandomPaymentType() {
		Random r = new Random();
		int randomValue = r.nextInt(4 - 1 + 1) + 1;
		return OperationType.getOperationType(randomValue);
	}

	public static void insertRandomTransaction(TransactionRepository transactionRepository, int count) {
		for (int i = 0; i < count; i++) {
			Transaction a = Helper.generateRandomTransaction();
			transactionRepository.save(a);
		}
	}

	public static boolean compareTransaction2TransactionData(Transaction transaction, TransactionData transactionData) {
		return (transaction.getAccountId().equals(transactionData.getAccount_id())
				&& transaction.getAmount().equals(transactionData.getAmount().getAmount())
				&& transaction.getBalance().equals(transactionData.getAmount().getAmount()))
				&& (transaction.getChargeOrder() == OperationType
						.getOperationType(transactionData.getOperation_type_id()).chargeOrder()
						&& transaction.getOperationTypeId() == transactionData.getOperation_type_id());
	}
	
	public static Transaction genTransactionObj(TransactionData transactionData) {
		Transaction transaction = Transaction.newTransaction(transactionData.getAccount_id(),
				transactionData.getOperation_type_id(), transactionData.getAmount().getAmount(),
				transactionData.getAmount().getAmount());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		transaction.setEventDate(timestamp);
		return transaction;
	}

	public static PaymentData[] generateRandomPaymentDataArray(int size) {
		PaymentData[] paymentData = new PaymentData[size];
		for(int i=0;i<size;i++) {
			paymentData[i] = generateRandomPaymentData();
		}
		return paymentData;
	}
	
	public static PaymentData generateRandomPaymentData() {
		PaymentData paymentData = new PaymentData();
		paymentData.setAccount_id(UUIDGen.getUUID());
		paymentData.setAmount(new Amount(generateRandomDouble(1.0, 1000.0)));
		return paymentData;
	}	

}
