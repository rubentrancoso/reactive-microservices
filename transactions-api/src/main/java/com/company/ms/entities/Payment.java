package com.company.ms.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.company.ms.helper.UUIDGen;
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
public class Payment {

	@Id
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
	private String paymentTrackintId;
	
	private Double amount;
	
	private String creditTransactionId;
	
	private String debitTransactionId;
	
	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date created;

	public Payment(String paymentTrackId, String creditTransactionId, String debitTransactionId, Double amount) {
		this.paymentTrackintId = paymentTrackId;
		this.creditTransactionId = creditTransactionId;
		this.debitTransactionId = debitTransactionId;
		this.amount = amount;
	}
	
	public static Payment newPayment(String creditTransactionId, String debitTransactionId, Double amount ) {
		return newPayment(UUIDGen.getUUID(), creditTransactionId, debitTransactionId, amount);
	}

	protected static Payment newPayment(String paymentTrackId, String creditTransactionId, String debitTransactionId, Double amount ) {
		return new Payment(paymentTrackId, creditTransactionId, debitTransactionId, amount);
	}

}