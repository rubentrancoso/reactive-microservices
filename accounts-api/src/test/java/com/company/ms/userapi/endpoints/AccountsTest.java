package com.company.ms.userapi.endpoints;


import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.company.ms.accounts.Application;
import com.company.ms.entities.Account;
import com.company.ms.helper.Json;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.services.AccountService;
import com.company.ms.userapi.message.AccountData;
import com.company.ms.userapi.message.Amount;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.server.port=0"})
public class AccountsTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountsTest.class);
	
	@LocalServerPort
	private int port;

	@Value("${management.server.port}")
	private int mgt;
	
	WebTestClient webTestClient;
    List<Account> expectedAccounts;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Before
	public void setup() {
		accountRepository.deleteAll();
	}
	
	@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:" + this.port + "/hello", Map.class);
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().get("message")).isEqualTo("accounts-api hello");
	}

//	@Test
//	public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
//		@SuppressWarnings("rawtypes")
//		ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:" + this.mgt + "/info", Map.class);
//		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//	}	

	@Test
	public void testGetLimits() throws Exception {
        webTestClient = WebTestClient.bindToController(new AccountController(accountService)).build();
        expectedAccounts = accountRepository.findAll().collectList().block();

		this.webTestClient.get()
	        .uri("/limits")
	        .accept(MediaType.TEXT_EVENT_STREAM)
	        .exchange()
	        .expectStatus().isOk()
	        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
	        .returnResult(Account.class);
	}
	
	@Test
	public void shouldCreateAnAccount() throws Exception {
		// Create account Data
		AccountData accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(5000.0));
		accountData.setAvailable_withdrawal_limit(new Amount(5000.0));
		
		// Call endpoint
		ResponseEntity<Object> response = restTemplate.postForEntity("/accounts", accountData, Object.class);
		@SuppressWarnings("unchecked")
		JSONObject responseBody = new JSONObject((Map<String, ?>) response.getBody());
		logger.info("@@@ testCreateAccount - Accounts response: "+ Json.prettyPrint(response.getBody()));	

		// Verify status and get values
		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		Double availableCreditLimit = responseBody.getDouble("availableCreditLimit");
		Double availableWithdrawalLimit = responseBody.getDouble("availableWithdrawalLimit");
		
		// Verify values
		then(availableCreditLimit.equals(5000.0));
		then(availableWithdrawalLimit.equals(5000.0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldUpdateAnAccount() throws Exception {
		// Create account Data
		AccountData accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(10.0));
		accountData.setAvailable_withdrawal_limit(new Amount(10.0));
		
		// Call endpoint to create account
		ResponseEntity<Object> response = restTemplate.postForEntity("/accounts", accountData, Object.class);
		JSONObject responseBody = new JSONObject((Map<String, ?>) response.getBody());
		Json.prettyPrint(response.getBody());
		String account_id = responseBody.getString("accountId");
		
		// Update account data
		accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(10.0));
		accountData.setAvailable_withdrawal_limit(new Amount(-10.0));

		// Call endpoint to update account
		String url = String.format("/accounts/%s", account_id);
		Account accountDataOut = (Account) restTemplate.patchForObject(url, accountData, Account.class);
		logger.info("@@@ testUpdateteAccount - Accounts response: "+ Json.prettyPrint(accountDataOut));	

		// Verify values
		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(accountDataOut.getAvailableCreditLimit().equals(20.0));
		then(accountDataOut.getAvailableWithdrawalLimit().equals(0.0));
	}


}