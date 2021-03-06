package com.company.ms.userapi.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.ms.entities.Account;
import com.company.ms.services.AccountService;
import com.company.ms.userapi.message.AccountData;
import com.company.ms.userapi.message.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	AccountService accountService;
	
	AccountController(AccountService _accountService) {
		accountService = _accountService;
	}

	// get limits (list accounts)
	@RequestMapping(path = "/accounts/limits", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public @ResponseBody Flux<Account> limits() {
		logger.info(String.format("get /accounts/limits"));
		return accountService.limits();
	}	
	
	// update account
	@RequestMapping(path = "/accounts/{account_id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Mono<Account> update(@PathVariable("account_id") String account_id, @RequestBody Mono<AccountData> accountData) {
		return accountService.update(account_id, accountData);
	}

	// create account
	@RequestMapping(path = "/accounts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Mono<Account> create(@RequestBody AccountData accountData) {
		logger.info(String.format("post /register: limits[withdrawal=%1$,.1f,credit=%1$,.1f]", accountData.getAvailable_withdrawal_limit().getAmount(),accountData.getAvailable_credit_limit().getAmount()));
		return accountService.create(accountData);
	}

	@RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object hello() {
		HttpStatus responseCode = HttpStatus.OK;
		return new ResponseEntity<Object>(new Message("accounts-api hello"), responseCode);
	}
	
	@RequestMapping(path = "/cleartables", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> cleartables() {
		logger.info(String.format("get /cleartables"));
		HttpStatus responseCode = HttpStatus.OK;
		Object response = new Message("Success");
		try {
			accountService.cleartables();
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);		
	}		

}
