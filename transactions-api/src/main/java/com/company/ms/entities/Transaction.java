package com.company.ms.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.company.ms.helper.UUIDGen;
import com.company.ms.types.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@Table
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class Transaction {

	@Id
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
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
		this.chargeOrder = PaymentType.getPaymentType(operationTypeId).chargeOrder();
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
