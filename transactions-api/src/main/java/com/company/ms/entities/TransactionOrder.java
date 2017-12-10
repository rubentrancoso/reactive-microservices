package com.company.ms.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

import com.company.ms.helper.UUIDGen;
import com.company.ms.types.OperationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@NoArgsConstructor
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class TransactionOrder {

	@Id
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

	public TransactionOrder(String transactionId, String accountId, int operationTypeId, Double amount, Double balance) {
		this.transactionId = transactionId;
		this.accountId = accountId;
		this.operationTypeId = operationTypeId;
		this.chargeOrder = OperationType.getOperationType(operationTypeId).chargeOrder();
		this.amount = amount;
		this.balance = balance;
	}
	
	public static TransactionOrder newTransaction( String accountId, int operationTypeId, Double amount, Double balance ) {
		return newTransaction(UUIDGen.getUUID(), accountId, operationTypeId, amount, balance);
	}

	protected static TransactionOrder newTransaction(String transactionId, String accountId, int operationTypeId, Double amount, Double balance) {
		return new TransactionOrder(transactionId, accountId, operationTypeId, amount, balance);
	}

}
