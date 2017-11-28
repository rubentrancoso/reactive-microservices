package com.company.ms.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	
	public static String prettyPrint(Object o) {
		String jsonMessage = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			jsonMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonMessage;
	}

}
