package com.company.ms.userapi.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class TransactionData {

	private String account_id;
	private int operation_type_id;
	private Amount amount;

	public TransactionData(String account_id, int operation_type_id, Double amount) {
		this.account_id = account_id;
		this.operation_type_id = operation_type_id;
		this.amount = new Amount(amount);
	}
	
}
