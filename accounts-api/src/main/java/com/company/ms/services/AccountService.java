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

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
	
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
		LOGGER.info("creating account - {}", account);
		return accountRepository.save(account)
				.doOnSuccess( result -> LOGGER.info("account created - {}", result));
	}

	public Flux<Account> limits() {
		return accountRepository.findAll();
	}

	public Mono<Account> update(String account_id, Mono<AccountData> newData) {
		return accountRepository.findById(account_id)
			.flatMap(currentAccount -> {
				LOGGER.info("updating account - {}", currentAccount);
				return newData.map(newValues-> { 
					currentAccount.setAvailableCreditLimit(
							Double.sum(
									currentAccount.getAvailableCreditLimit(),
									newValues.getAvailable_credit_limit().getAmount()
							)
					);
					currentAccount.setAvailableWithdrawalLimit(
							Double.sum(
									currentAccount.getAvailableWithdrawalLimit(),
									newValues.getAvailable_withdrawal_limit().getAmount()
							)
					);
					currentAccount.setUpdated(new Timestamp(System.currentTimeMillis()));
					return currentAccount; 
				}).flatMap(accountRepository::save)
						.doOnSuccess( result -> LOGGER.info("account uupdated - {}", result));
			});

	}

}
