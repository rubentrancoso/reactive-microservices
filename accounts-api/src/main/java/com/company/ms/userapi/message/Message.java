package com.company.ms.userapi.message;

public class Message {

	private String message;
	
	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return String.format("[message=%s]", this.message);
	}
	
}
