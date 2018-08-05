package com.company.ms.entities;

import java.util.Date;

import com.company.ms.helper.UUIDGen;
import com.company.ms.types.OperationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class Transaction {

	private String transactionId;
	
	private String accountId;

	private int operationTypeId;
	
	private int chargeOrder;

	private Double amount;

	private Double balance;
	
	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date eventDate;
	
	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date dueDate;

	public Transaction(String transactionId, String accountId, int operationTypeId, Double amount, Double balance) {
		this.transactionId = transactionId;
		this.accountId = accountId;
		this.operationTypeId = operationTypeId;
		this.chargeOrder = OperationType.getOperationType(operationTypeId).chargeOrder();
		this.amount = amount;
		this.balance = balance;
	}
	
	public static Transaction newTransaction( String accountId, int operationTypeId, Double amount, Double balance ) {
		return newTransaction(UUIDGen.getUUID(), accountId, operationTypeId, amount, balance);
	}

	protected static Transaction newTransaction(String transactionId, String accountId, int operationTypeId, Double amount, Double balance) {
		return new Transaction(transactionId, accountId, operationTypeId, amount, balance);
	}

}
