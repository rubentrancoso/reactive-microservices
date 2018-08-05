package com.company.ms.userapi.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.ms.services.GeneratorService;
import com.company.ms.userapi.message.Message;

@Controller
public class GeneratorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorController.class);

	GeneratorService generatorService;	
	
	GeneratorController(GeneratorService _generatorService) {
		generatorService = _generatorService;
	}	
	
	@RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> hello() {
		LOGGER.info("hello");
		HttpStatus responseCode = HttpStatus.OK;
		Object response;
		try {
			response = new Message("transactions-api hello");
		} catch (Exception e) {
			responseCode = HttpStatus.BAD_REQUEST;
			response = new Message(e.getMessage());
		}
		return new ResponseEntity<Object>(response, responseCode);			
	}

}
