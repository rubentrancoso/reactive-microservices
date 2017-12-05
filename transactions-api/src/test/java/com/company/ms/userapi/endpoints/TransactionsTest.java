package com.company.ms.userapi.endpoints;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.company.ms.repositories.PaymentRepository;
import com.company.ms.repositories.PaymentTrackingRepository;
import com.company.ms.repositories.TransactionRepository;
import com.company.ms.transactions.Application;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class TransactionsTest {

	private static final Logger logger = LoggerFactory.getLogger(TransactionsTest.class);

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PaymentTrackingRepository paymentrackingRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;	
	
	@Before
	public void setup() {
		paymentRepository.deleteAll();
		paymentrackingRepository.deleteAll();
		transactionRepository.deleteAll();
	}
	
	@Test
	public void test() throws Exception {
	}
	



}