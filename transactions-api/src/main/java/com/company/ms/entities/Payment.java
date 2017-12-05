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

@Data
@Table
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class Payment {

	@Id
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
	private String paymentId;
	
	private String accountId;

	private Double amount;
	
	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date eventDate;

	public Payment(String paymentId, String accountId, Double amount) {
		this.paymentId = paymentId;
		this.accountId = accountId;
		this.amount = amount;
	}
	
	public static Payment newPayment(String accountId, Double amount ) {
		return newPayment(UUIDGen.getUUID(), accountId, amount);
	}

	protected static Payment newPayment(String paymentId, String accountId, Double amount ) {
		return new Payment(paymentId, accountId, amount);
	}

}
