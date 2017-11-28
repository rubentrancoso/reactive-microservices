package com.company.ms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@JsonInclude(content=Include.NON_NULL, value=Include.NON_NULL)
public class Account {

	@Id
	@Column(unique=true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long accountId;
	
	private Double availableCreditLimit;
	
	private Double availableWithdrawalLimit;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Double getAvailableCreditLimit() {
		return availableCreditLimit;
	}

	public void setAvailableCreditLimit(Double availableCreditLimit) {
		this.availableCreditLimit = availableCreditLimit;
	}

	public Double getAvailableWithdrawalLimit() {
		return availableWithdrawalLimit;
	}

	public void setAvailableWithdrawalLimit(Double availableWithdrawalLimit) {
		this.availableWithdrawalLimit = availableWithdrawalLimit;
	}

	public String toString() {
		return String.format("[Account_ID=%s] [AvailableCreditLimit=%s] [AvailableWithdrawalLimit=%s]", this.accountId, this.availableCreditLimit, this.availableWithdrawalLimit);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || !(obj instanceof Account))
			return false;
		Account account = (Account) obj;
		return account.getAccountId().equals(this.accountId);
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + accountId.hashCode();
        return result;
    }	

}
