package com.company.ms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.services.interfaces.ISrvAccount;
import com.company.ms.userapi.message.AccountData;
import com.company.ms.userapi.message.Amount;
import com.company.ms.userapi.message.Message;

@Service
public class SrvAccount implements ISrvAccount {

	private static final Logger logger = LoggerFactory.getLogger(SrvAccount.class);

	private AccountRepository accountRepository;

	SrvAccount(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public Object create(AccountData accountData) {
		if (accountData == null || accountData.getAvailable_credit_limit().equals(null)
				|| accountData.getAvailable_withdrawal_limit().equals(null)) {
			return null;
		}
		Account account = new Account();
		account.setAvailableCreditLimit(accountData.getAvailable_credit_limit().getAmount());
		account.setAvailableWithdrawalLimit(accountData.getAvailable_withdrawal_limit().getAmount());
		try {
			account = accountRepository.save(account);
			AccountData accountDataOut = new AccountData();
			accountDataOut.setAccount_id(account.getAccountId());
			accountDataOut.setAvailable_credit_limit(new Amount(account.getAvailableCreditLimit()));
			accountDataOut.setAvailable_withdrawal_limit(new Amount(account.getAvailableWithdrawalLimit()));
			return accountDataOut;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Message(e.getMessage());
		}
	}

	@Override
	public Object limits() {
		return accountRepository.findAll();
	}

	@Override
	public Object update(AccountData accountData) {
		logger.info("updating account...");
		Account account = accountRepository.findByAccountId(accountData.getAccount_id());
		account.setAvailableCreditLimit(Double.sum(account.getAvailableCreditLimit(),accountData.getAvailable_credit_limit().getAmount()));
		account.setAvailableWithdrawalLimit(Double.sum(account.getAvailableWithdrawalLimit(),accountData.getAvailable_withdrawal_limit().getAmount()));
		account = accountRepository.save(account);

		AccountData accountDataOut = new AccountData();
		accountDataOut.setAccount_id(account.getAccountId());
		accountDataOut.setAvailable_credit_limit(new Amount(account.getAvailableCreditLimit()));
		accountDataOut.setAvailable_withdrawal_limit(new Amount(account.getAvailableWithdrawalLimit()));
		return accountDataOut;
	}

}
