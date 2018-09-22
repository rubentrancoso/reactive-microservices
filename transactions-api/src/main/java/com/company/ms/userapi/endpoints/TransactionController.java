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

import com.company.ms.helper.Json;
import com.company.ms.services.TransactionService;
import com.company.ms.userapi.message.Message;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

@Controller
public class TransactionController {

	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	TransactionService transactionService;	
	
	TransactionController(TransactionService _transactionService) {
		transactionService = _transactionService;
	}
	
	@RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object hello() {
		HttpStatus responseCode = HttpStatus.OK;
		return new ResponseEntity<Object>(new Message("transactions-api hello"), responseCode);
	}	
	
	@RequestMapping(path = "/transactions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addSingleTransaction(@RequestBody TransactionData transactionData) {
		logger.info(String.format("post /transactions: %s", transactionData.toString()));
		HttpStatus responseCode = HttpStatus.OK;
		Object response;
		try {
			response = transactionService.addSingleTransaction(transactionData);
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);
	}

	@RequestMapping(path = "/transactionsgroup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addTransactionArray(@RequestBody TransactionData[] transactionsData) {
		logger.info(String.format("post /transactionsgroup: %s", Json.prettyPrint(transactionsData)));
		HttpStatus responseCode = HttpStatus.OK;
		Object response = new Message("Success");
		try {
			transactionService.addTransactionGroup(transactionsData);
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);		
	}
	
	@RequestMapping(path = "/transactions/{account_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> listTransactionsFromAccount(@PathVariable("account_id") String account_id) {
		logger.info(String.format("get /transactions"));
		HttpStatus responseCode = HttpStatus.OK;
		Object response = new Message("Success");
		try {
			response = transactionService.listTransactionsFromAccount(account_id);
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);		
	}

	@RequestMapping(path = "/payments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addPayments(@RequestBody PaymentData[] paymentData) {
		logger.info(String.format("post /payments: %s", Json.prettyPrint(paymentData)));
		HttpStatus responseCode = HttpStatus.OK;
		Object response = new Message("Success");
		try {
			transactionService.addPayments(paymentData);
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);		
	}	
	
//	@RequestMapping(path = "/payments/{account_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//	public @ResponseBody ResponseEntity<?> listPaymentTransactionsFromAccount(@PathVariable("account_id") String account_id) {
//		logger.info(String.format("get /payments/{account_id}"));
//		HttpStatus responseCode = HttpStatus.OK;
//		Object response = new Message("Success");
//		try {
//			response = transactionService.listPaymentTransactionsFromAccount(account_id);
//		} catch (Exception e) {
//			responseCode = HttpStatus.BAD_REQUEST;
//			response = new Message(e.getMessage());
//		}
//		return new ResponseEntity<Object>(response, responseCode);		
//	}
	
	@RequestMapping(path = "/cleartables", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> cleartables() {
		logger.info(String.format("get /cleartables"));
		HttpStatus responseCode = HttpStatus.OK;
		Object response = new Message("Success");
		try {
			transactionService.cleartables();
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);		
	}	

}
