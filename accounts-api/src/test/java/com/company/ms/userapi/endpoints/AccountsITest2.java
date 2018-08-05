package com.company.ms.userapi.endpoints;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.company.ms.app.Application;
import com.company.ms.entities.Account;
import com.company.ms.repositories.AccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsITest2 {

	private static final Logger logger = LoggerFactory.getLogger(AccountsITest2.class);
	
	@LocalServerPort
	private int port;
	
	WebTestClient webTestClient;
    List<Account> expectedAccounts;
	
    @Autowired
	private AccountRepository accountRepository;
    
	@Before
	public void setup() {
		accountRepository.deleteAll().block();
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
		Helper.insertRandomAccounts(accountRepository, 10);
		expectedAccounts = accountRepository.findAll().collectList().block();
	}
	
	@Test
    public void shouldReturnAllAccounts() {
		logger.info("@@@ shouldReturnAllAccounts");
        webTestClient.get()
        	.uri("/accounts/limits")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
            .expectBodyList(Account.class).isEqualTo(expectedAccounts);
    }	

	@Test
	public void shouldReadAnAccountFluxFromLimits() throws Exception {
		logger.info("@@@ shouldReadAnAccountFluxFromLimits");
		webTestClient.get()
	        .uri("/accounts/limits")
	        .accept(MediaType.TEXT_EVENT_STREAM)
	        .exchange()
	        .expectStatus().isOk()
	        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
	        .expectBodyList(Account.class).isEqualTo(expectedAccounts);
	}

}