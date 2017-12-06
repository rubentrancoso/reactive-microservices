package com.company.ms.userapi.endpoints;

import java.util.Random;

import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;

public class Helper {
	
	public static Account generateRandomAccount() {
		Double availableCreditLimit = generateRancomDouble(0.0,1000.0);
		Double availableWithdrawalLimit = generateRancomDouble(0.0,1000.0);
		
		return Account.newAccount(availableCreditLimit, availableWithdrawalLimit);
	}

	public static Double generateRancomDouble(Double rangeMin, Double rangeMax) {
		Random r = new Random();
		Double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		return randomValue;
	}

	public static void insertRandomAccounts(AccountRepository accountRepository, int count) {
		for(int i=0;i<count;i++) {
			Account a = Helper.generateRandomAccount();
			accountRepository.save(a);
		}
	}	
	
}
