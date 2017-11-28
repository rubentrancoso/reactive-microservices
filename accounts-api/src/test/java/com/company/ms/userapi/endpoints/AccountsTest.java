package com.company.ms.userapi.endpoints;


import static org.assertj.core.api.BDDAssertions.then;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.company.ms.accounts.Application;
import com.company.ms.helper.Json;
import com.company.ms.repositories.AccountRepository;
import com.company.ms.userapi.message.AccountData;
import com.company.ms.userapi.message.Amount;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class AccountsTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountsTest.class);
	
	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Before
	public void setup() {
		accountRepository.deleteAll();
	}
	
	@Test
	public void testCreateAccount() throws Exception {
		AccountData accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(5000.0));
		accountData.setAvailable_withdrawal_limit(new Amount(5000.0));
		ResponseEntity<Object> response = this.testRestTemplate.postForEntity("/accounts", accountData, Object.class);
		@SuppressWarnings("unchecked")
		JSONObject responseBody = new JSONObject((Map<String, ?>) response.getBody());
		logger.info("@@@ testCreateAccount - Accounts response: "+ Json.prettyPrint(response.getBody()));	

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject available_credit_limit = responseBody.getJSONObject("available_credit_limit");
		Double credit_value = available_credit_limit.getDouble("amount");
		JSONObject available_withdrawal_limit = responseBody.getJSONObject("available_withdrawal_limit");
		Double withdrawal_value = available_withdrawal_limit.getDouble("amount");
		
		then(credit_value.equals(5000.0));
		then(withdrawal_value.equals(5000.0));
	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateteAccount() throws Exception {
		AccountData accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(10.0));
		accountData.setAvailable_withdrawal_limit(new Amount(10.0));
		ResponseEntity<Object> response = this.testRestTemplate.postForEntity("/accounts", accountData, Object.class);

		JSONObject responseBody = new JSONObject((Map<String, ?>) response.getBody());
		
		Long account_id = responseBody.getLong("account_id");
		
		String url = String.format("/accounts/%d", account_id);

		accountData = new AccountData();
		accountData.setAvailable_credit_limit(new Amount(10.0));
		accountData.setAvailable_withdrawal_limit(new Amount(-10.0));

		AccountData accountDataOut = (AccountData) this.testRestTemplate.patchForObject(url, accountData, AccountData.class);

		logger.info("@@@ testUpdateteAccount - Accounts response: "+ Json.prettyPrint(accountDataOut));	

		then(accountDataOut.getAvailable_credit_limit().getAmount().equals(20.0));
		then(accountDataOut.getAvailable_withdrawal_limit().getAmount().equals(0.0));
	}


}