package com.company.ms.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Document(collection = "accounts")
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(content=Include.NON_NULL, value=Include.NON_NULL)
public class Account {

	@Id
	private String accountId;

	private Double availableCreditLimit;
	
	private Double availableWithdrawalLimit;

	public Account(Double availableCreditLimit, Double availableWithdrawalLimit) {
		this.availableCreditLimit = availableCreditLimit;;
		this.availableWithdrawalLimit = availableWithdrawalLimit;
	}

}
