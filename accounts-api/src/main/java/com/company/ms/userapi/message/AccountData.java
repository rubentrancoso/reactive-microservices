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
@JsonInclude(content=Include.NON_NULL, value=Include.NON_NULL)
public class AccountData {
	
	private String account_id;
	private Amount available_credit_limit;
	private Amount available_withdrawal_limit;
	
	public AccountData(Double  availableCreditLimit, Double availableWithdrawalLimit) {
		this.available_credit_limit = new Amount(availableCreditLimit);
		this.available_withdrawal_limit = new Amount(availableWithdrawalLimit);
	}	
	
}
