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
import lombok.NoArgsConstructor;

@Data
@Table
@NoArgsConstructor
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class Account {

	@Id
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
	private String accountId;

	private Double availableCreditLimit;

	private Double availableWithdrawalLimit;

	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date created;

	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date updated;

	public Account(String accountId, Double availableCreditLimit, Double availableWithdrawalLimit) {
		this.accountId = accountId;
		this.availableCreditLimit = availableCreditLimit;
		this.availableWithdrawalLimit = availableWithdrawalLimit;
	}

	public static Account newAccount(Double availableCreditLimit, Double availableWithdrawalLimit) {
		return newAccount(UUIDGen.getUUID(), availableCreditLimit, availableWithdrawalLimit);
	}

	protected static Account newAccount(String accountId, Double availableCreditLimit,
			Double availableWithdrawalLimit) {
		return new Account(accountId, availableCreditLimit, availableWithdrawalLimit);
	}

}
