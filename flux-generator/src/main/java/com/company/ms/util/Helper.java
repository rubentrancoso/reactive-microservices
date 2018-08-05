package com.company.ms.util;

import java.sql.Timestamp;
import java.util.Random;

import com.company.ms.entities.Transaction;
import com.company.ms.helper.UUIDGen;
import com.company.ms.types.OperationType;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

public class Helper {

	public static Transaction generateRandomTransaction() {
		String accountId = UUIDGen.getUUID();
		OperationType operationType = generateRandomOperationType();
		Double amount = generateRandomDouble(0.0, 1000.0);
		Double balance = generateRandomDouble(0.0, 1000.0);
		return Transaction.newTransaction(accountId, operationType.operationType(), amount, balance);
	}

	public static TransactionData[] generateRandomTransactionDataArray(int size) {
		TransactionData[] transactionData = new TransactionData[size];
		for(int i=0;i<size;i++) {
			transactionData[i] = generateRandomTransactionData(true);
		}
		return transactionData;
	}
	
	public static TransactionData generateRandomTransactionData(boolean excludePayment) {
		TransactionData transactionData = new TransactionData();
		transactionData.setAccount_id(UUIDGen.getUUID());
		int operationType_id;
		if(excludePayment) {
			operationType_id = generateRandomOperationTypeNoPayment().operationType();
		} else {
			operationType_id = generateRandomOperationType().operationType();
		}
		transactionData.setOperation_type_id(operationType_id);
		transactionData.setAmount(generateRandomDouble(0.1, 1000.0));
		return transactionData;
	}

	public static Double generateRandomDouble(Double rangeMin, Double rangeMax) {
		Random r = new Random();
		Double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		return randomValue;
	}

	public static OperationType generateRandomOperationType() {
		Random r = new Random();
		int randomValue = r.nextInt(4 - 1 + 1) + 1;
		return OperationType.getOperationType(randomValue);
	}

	public static OperationType generateRandomOperationTypeNoPayment() {
		Random r = new Random();
		int randomValue = r.nextInt(3 - 1 + 1) + 1;
		return OperationType.getOperationType(randomValue);
	}

	public static boolean compareTransaction2TransactionData(Transaction transaction, TransactionData transactionData) {
		return (transaction.getAccountId().equals(transactionData.getAccount_id())
				&& transaction.getAmount().equals(transactionData.getAmount())
				&& transaction.getBalance().equals(transactionData.getAmount()))
				&& (transaction.getChargeOrder() == OperationType
						.getOperationType(transactionData.getOperation_type_id()).chargeOrder()
						&& transaction.getOperationTypeId() == transactionData.getOperation_type_id());
	}
	
	public static Transaction genTransactionObj(PaymentData paymentData) {
		Transaction transaction = Transaction.newTransaction(paymentData.getAccount_id(),
				OperationType.PAGAMENTO.operationType(), paymentData.getAmount(),
				paymentData.getAmount());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		transaction.setEventDate(timestamp);
		return transaction;
	}	
	
	public static Transaction genTransactionObj(TransactionData transactionData) {
		Transaction transaction = Transaction.newTransaction(transactionData.getAccount_id(),
				transactionData.getOperation_type_id(), transactionData.getAmount(),
				transactionData.getAmount());
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
		paymentData.setAmount(generateRandomDouble(1.0, 1000.0));
		return paymentData;
	}

	public static TransactionData[] generateRandomTransactionDataArray(String accountId, int size) {
		TransactionData[] transactionData = new TransactionData[size];
		for(int i=0;i<size;i++) {
			transactionData[i] = generateRandomTransactionData(false);
			transactionData[i].setAccount_id(accountId);
		}
		return transactionData;
	}

}
