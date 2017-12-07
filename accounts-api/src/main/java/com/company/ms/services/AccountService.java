package com.company.ms.services;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.userapi.message.AccountData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	
	private AccountRepository accountRepository;

	AccountService(AccountRepository _accountRepository) {
		accountRepository = _accountRepository;
	}
	
	public Mono<Account> create(AccountData accountData) {
		if (accountData == null || accountData.getAvailable_credit_limit().equals(null)
				|| accountData.getAvailable_withdrawal_limit().equals(null)) {
			return null;
		}
		Account account = Account.newAccount(
				accountData.getAvailable_credit_limit().getAmount(), 
				accountData.getAvailable_withdrawal_limit().getAmount()
		);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		account.setCreated(timestamp);
		account.setUpdated(timestamp);
		return accountRepository.save(account);
	}

	public Flux<Account> limits() {
		return accountRepository.findAll();
	}

	public Mono<Account> update(String account_id, Mono<AccountData> accountData) {
		logger.info("updating account...");
		return accountRepository.findById(account_id)
			.flatMap(originaAccount -> { 
				// update Account instance with new Values
				accountData.map(newValues-> { 
					originaAccount.setAvailableCreditLimit(Double.sum(originaAccount.getAvailableCreditLimit(),
							newValues.getAvailable_credit_limit().getAmount()));
					originaAccount.setAvailableWithdrawalLimit(Double.sum(originaAccount.getAvailableWithdrawalLimit(),
							newValues.getAvailable_withdrawal_limit().getAmount()));
					originaAccount.setUpdated(new Timestamp(System.currentTimeMillis()));
					return originaAccount; 
				}).subscribe();
				return accountRepository.save(originaAccount);
			});
	}

}
