package com.company.ms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.userapi.message.AccountData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SrvAccount {

	private static final Logger logger = LoggerFactory.getLogger(SrvAccount.class);

	@Autowired
	private AccountRepository accountRepository;

	public Mono<Account> create(AccountData accountData) {
		if (accountData == null || accountData.getAvailable_credit_limit().equals(null)
				|| accountData.getAvailable_withdrawal_limit().equals(null)) {
			return null;
		}
		Account account = new Account();
		account.setAvailableCreditLimit(accountData.getAvailable_credit_limit().getAmount());
		account.setAvailableWithdrawalLimit(accountData.getAvailable_withdrawal_limit().getAmount());
		return accountRepository.save(account);
	}

	public Flux<Account> limits() {
		return accountRepository.findAll();
	}

	public Mono<Account> update(AccountData accountData) {
		logger.info("updating account...");
		return accountRepository.findById(accountData.getAccount_id()).flatMap(receivedAccount -> {
			receivedAccount.setAvailableCreditLimit(Double.sum(receivedAccount.getAvailableCreditLimit(),
					accountData.getAvailable_credit_limit().getAmount()));
			receivedAccount.setAvailableWithdrawalLimit(Double.sum(receivedAccount.getAvailableWithdrawalLimit(),
					accountData.getAvailable_withdrawal_limit().getAmount()));
			return accountRepository.save(receivedAccount);
		});
	}

}
