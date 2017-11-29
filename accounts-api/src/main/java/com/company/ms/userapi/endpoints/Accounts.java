package com.company.ms.userapi.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.company.ms.services.SrvAccount;
import com.company.ms.userapi.message.AccountData;
import com.company.ms.userapi.message.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class Accounts {

	private static final Logger logger = LoggerFactory.getLogger(Accounts.class);

	@Autowired
	SrvAccount accountService;

	@RequestMapping(path = "/accounts/{account_id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Mono<Account> patch(@PathVariable("account_id") String account_id, @RequestBody AccountData accountData) {
		logger.info(String.format("post /register: limits[withdrawal=%1$,.1f,credit=%1$,.1f]", accountData.getAvailable_withdrawal_limit().getAmount(),accountData.getAvailable_credit_limit().getAmount()));
		accountData.setAccount_id(account_id);
		return accountService.update(accountData);
	}
	
	
	
//	@RequestMapping(path = "/accounts/{account_id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody Object patch(@PathVariable("account_id") Long account_id, @RequestBody AccountData accountData) {
//		HttpStatus responseCode = HttpStatus.OK;
//		logger.info(String.format("post /register: limits[withdrawal=%1$,.1f,credit=%1$,.1f]", accountData.getAvailable_withdrawal_limit().getAmount(),accountData.getAvailable_credit_limit().getAmount()));
//		accountData.setAccount_id(account_id);
//		Object message = accountService.update(accountData);
//		if(message == null) {
//			responseCode = HttpStatus.NOT_FOUND;
//			return new Message("not found"); 
//		}
//		return new ResponseEntity<Object>(message, responseCode);
//	}
//	

	
	@RequestMapping(path = "/accounts/limits", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody Flux<Account> list() {
		logger.info(String.format("get /accounts/limits"));
		return accountService.limits();
	}	
	
	
//	@RequestMapping(path = "/accounts/limits", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody Object list() {
//		HttpStatus responseCode = HttpStatus.OK;
//		logger.info(String.format("get /accounts/limits"));
//		Object message = accountService.limits();
//		return new ResponseEntity<Object>(message, responseCode);
//	}

	
	@RequestMapping(path = "/accounts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<Mono<Account>> create(@RequestBody AccountData accountData) {
		HttpStatus responseCode = HttpStatus.OK;
		logger.info(String.format("post /register: limits[withdrawal=%1$,.1f,credit=%1$,.1f]", accountData.getAvailable_withdrawal_limit().getAmount(),accountData.getAvailable_credit_limit().getAmount()));
		Mono<Account> message = accountService.create(accountData);
		return new ResponseEntity<Mono<Account>>(message, responseCode);
	}

	@RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object hello() {
		HttpStatus responseCode = HttpStatus.OK;
		return new ResponseEntity<Object>(new Message("hello"), responseCode);
	}
	
	
	
//	@RequestMapping(path = "/accounts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public @ResponseBody Object create(@RequestBody AccountData accountData) {
//		HttpStatus responseCode = HttpStatus.OK;
//		logger.info(String.format("post /register: limits[withdrawal=%1$,.1f,credit=%1$,.1f]", accountData.getAvailable_withdrawal_limit().getAmount(),accountData.getAvailable_credit_limit().getAmount()));
//		Object message = accountService.create(accountData);
//		return new ResponseEntity<Object>(message, responseCode);
//	}

}
