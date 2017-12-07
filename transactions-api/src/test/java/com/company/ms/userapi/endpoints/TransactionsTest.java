package com.company.ms.userapi.endpoints;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.company.ms.entities.Transaction;
import com.company.ms.repositories.PaymentRepository;
import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.transactions.Application;
import com.company.ms.userapi.message.TransactionData;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.server.port=0"})
public class TransactionsTest {

	private static final Logger logger = LoggerFactory.getLogger(TransactionsTest.class);

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	WebTestClient webTestClient;
    List<Transaction> expectedTransactions;	
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PaymentTrackingRepository paymentrackingRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;	
	
	
	@Before
	public void setup() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
		paymentRepository.deleteAll().block();
		paymentrackingRepository.deleteAll().block();
		transactionRepository.deleteAll().block();
		//Helper.insertRandomAccounts(accountRepository, 10);
		//expectedAccounts = accountRepository.findAll().collectList().block();
	}
		
	@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:" + this.port + "/hello", Map.class);
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().get("message")).isEqualTo("transactions-api hello");
	}
	
	@Test
	public void shouldCreateAnTransaction() throws Exception {
		// Create account Data
		TransactionData transactionData = Helper.generateRandomTransactionData();
		// Call endpoint
		Transaction transaction = restTemplate.postForObject("/transactions", transactionData, Transaction.class);
		// Verify values
		then(Helper.compareTransaction2TransactionData(transaction, transactionData));
	}	
	
	@Test
	public void shouldAddAnTransactionArray() throws Exception {
		logger.info("shouldAddAnTransactionArray");
		// Create account Data
		TransactionData[] transactionsData = Helper.generateRandomTransactionDataArray(10);
		Map<String,TransactionData> expected_array = new HashMap<String, TransactionData>();
		for(TransactionData transactionData: transactionsData) {
			expected_array.put(transactionData.getAccount_id(), transactionData);
		}

		// Call endpoint
		webTestClient.post()
        	.uri("/transactionsgroup")
        	.accept(MediaType.APPLICATION_STREAM_JSON)
        	.body(BodyInserters.fromObject(transactionsData))
        	.exchange();
		
		List<Transaction> result = transactionRepository.findAll().collectList().block();
		for (Iterator<Transaction> iterator = result.iterator(); iterator.hasNext();) {
			Transaction result_transaction = (Transaction) iterator.next();
			TransactionData expected_item;
			assertNotNull(expected_item = expected_array.get(result_transaction.getAccountId()));
			assertTrue(expected_item.getAccount_id().equals(result_transaction.getAccountId()));
			assertTrue(expected_item.getAmount().getAmount().equals(result_transaction.getAmount()));
			assertTrue(expected_item.getOperation_type_id() == result_transaction.getOperationTypeId());
		}
	}	
	
//	@RequestMapping(path = "/transactions/{account_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//	public @ResponseBody Flux<Transaction> listTransactionsFromAccount(@PathVariable("account_id") String account_id) {
//		logger.info(String.format("get /transactions"));
//		return transactionService.listTransactionsFromAccount(account_id);
//	}
//
//	@RequestMapping(path = "/payments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//	public @ResponseBody void addPayments(@RequestBody PaymentData[] paymentData) {
//		logger.info(String.format("post /payments: %s", Json.prettyPrint(paymentData)));
//		transactionService.addPayment(paymentData);
//	}		
	



}