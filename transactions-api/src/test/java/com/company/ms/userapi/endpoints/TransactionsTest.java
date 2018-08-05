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
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.company.ms.app.Application;
import com.company.ms.entities.Transaction;
import com.company.ms.helper.UUIDGen;
import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.types.OperationType;
import com.company.ms.userapi.message.PaymentData;
import com.company.ms.userapi.message.TransactionData;
import com.company.ms.util.Helper;


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
	private PaymentTrackingRepository paymentrackingRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;	
	
	
	@Before
	public void setup() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
		paymentrackingRepository.deleteAll().block();
		transactionRepository.deleteAll().block();
		//Helper.insertRandomAccounts(accountRepository, 10);
		//expectedAccounts = accountRepository.findAll().collectList().block();
	}
		
	//@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:" + this.port + "/hello", Map.class);
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().get("message")).isEqualTo("transactions-api hello");
	}
	
	@Test
	public void shouldCreateAnTransaction() throws Exception {
		// Create account Data
		TransactionData transactionData = Helper.generateRandomTransactionData(true);
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
		assertTrue(result.size() == transactionsData.length);
		for (Iterator<Transaction> iterator = result.iterator(); iterator.hasNext();) {
			Transaction result_transaction = (Transaction) iterator.next();
			TransactionData expected_item;
			assertNotNull(expected_item = expected_array.get(result_transaction.getAccountId()));
			assertTrue(expected_item.getAccount_id().equals(result_transaction.getAccountId()));
			assertTrue(expected_item.getOperation_type_id() == result_transaction.getOperationTypeId());
			assertTrue(expected_item.getAmount().equals(result_transaction.getAmount()));
		}
	}	
	
	@Test
	public void shouldAddAnPaymentArray() throws Exception {
		logger.info("shouldAddAnPaymentArray");
		// Create account Data
		PaymentData[] paymentsData = Helper.generateRandomPaymentDataArray(10);
		Map<String,PaymentData> expected_array = new HashMap<String, PaymentData>();
		for(PaymentData paymentData: paymentsData) {
			expected_array.put(paymentData.getAccount_id(), paymentData);
		}

		// Call endpoint
		webTestClient.post()
        	.uri("/payments")
        	.accept(MediaType.APPLICATION_STREAM_JSON)
        	.body(BodyInserters.fromObject(paymentsData))
        	.exchange();
		
//		List<Payment> result = paymentRepository.findAll().collectList().block();
//		assertTrue(result.size() == paymentsData.length);
//		for (Iterator<Payment> iterator = result.iterator(); iterator.hasNext();) {
//			Payment result_payment = (Payment) iterator.next();
//			PaymentData expected_item;
//			assertNotNull(expected_item = expected_array.get(result_payment.getAccountId()));
//			assertTrue(expected_item.getAccount_id().equals(result_payment.getAccountId()));
//			assertTrue(expected_item.getAmount().equals(result_payment.getAmount()));
//		}
	}	
	
	@Test
	public void shouldReturnAllTransationsForAGivenAccount() throws Exception {
		logger.info("shouldReturnAllTransationsForAGivenAccount");
		// Create transactions Data
		String accountId = UUIDGen.getUUID();
		TransactionData[] transactionsData = Helper.generateRandomTransactionDataArray(accountId, 10);
		Map<String,TransactionData> expected_array = new HashMap<String, TransactionData>();
		for(TransactionData transactionData: transactionsData) {
			if(transactionData.getOperation_type_id() != OperationType.PAGAMENTO.operationType()) {
				transactionData.setAmount(-transactionData.getAmount());
			}
			expected_array.put(transactionData.getAccount_id(), transactionData);
		}

		// Insert Transactions
		webTestClient.post()
        	.uri("/transactionsgroup")
        	.accept(MediaType.APPLICATION_STREAM_JSON)
        	.body(BodyInserters.fromObject(transactionsData))
        	.exchange();
				
		// Get Transactions for account
		ListBodySpec<Transaction> get_response = webTestClient.get()
        	.uri(String.format("/transactions/%s",accountId))
        	.accept(MediaType.APPLICATION_STREAM_JSON)
        	.exchange()
        	.expectBodyList(Transaction.class);
		
		List<Transaction> response_list = get_response.returnResult().getResponseBody();
		List<Transaction> db_list = transactionRepository.findByAccountId(accountId).collectList().block();
		assertTrue(response_list.equals(db_list));
	}


}