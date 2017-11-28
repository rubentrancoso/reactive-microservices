package com.company.ms.userapi.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(content=Include.NON_NULL, value=Include.NON_NULL)
public class AccountData {
	
	private Long account_id;
	private Amount available_credit_limit;
	private Amount available_withdrawal_limit;
	
	public Long getAccount_id() {
		return account_id;
	}
	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}
	public Amount getAvailable_credit_limit() {
		return available_credit_limit;
	}
	public void setAvailable_credit_limit(Amount available_credit_limit) {
		this.available_credit_limit = available_credit_limit;
	}
	public Amount getAvailable_withdrawal_limit() {
		return available_withdrawal_limit;
	}
	public void setAvailable_withdrawal_limit(Amount available_withdrawal_limit) {
		this.available_withdrawal_limit = available_withdrawal_limit;
	}
	

	
}
