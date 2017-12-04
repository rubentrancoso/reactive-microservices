package com.company.ms.userapi.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.ms.entities.Transaction;
import com.company.ms.helper.Json;
import com.company.ms.services.SrvTransaction;
import com.company.ms.userapi.message.Message;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;

import reactor.core.publisher.Mono;

@Controller
public class Transactions {

	private static final Logger logger = LoggerFactory.getLogger(Transactions.class);

	@Autowired
	SrvTransaction transactionService;	
	
	@RequestMapping(path = "/transactions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Mono<Transaction> transactions(@RequestBody TransactionData transactionData) {
		logger.info(String.format("post /transactions: %s", transactionData.toString()));
		return transactionService.doTransaction(transactionData);
	}
	
	@RequestMapping(path = "/payments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public @ResponseBody void payments(@RequestBody PaymentData[] paymentData) {
		logger.info(String.format("post /payments: %s", Json.prettyPrint(paymentData)));
		transactionService.doPayment(paymentData);
	}	

	@RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object hello() {
		HttpStatus responseCode = HttpStatus.OK;
		return new ResponseEntity<Object>(new Message("transactions-api hello"), responseCode);
	}

}
