package com.company.ms.entities;

import java.util.Date;

import com.company.ms.helper.UUIDGen;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class PaymentTracking {

	private String paymentTrackingtId;
	
	private Double amount;
	
	private String creditTransactionId;
	
	private String debitTransactionId;
	
	@JsonFormat(pattern = "EEE yyyy-MM-dd HH:mm:ss.SSSZ")
	private Date eventDate;

	public PaymentTracking(String paymentTrackId, String creditTransactionId, String debitTransactionId, Double amount) {
		this.paymentTrackingtId = paymentTrackId;
		this.creditTransactionId = creditTransactionId;
		this.debitTransactionId = debitTransactionId;
		this.amount = amount;
	}
	
	public static PaymentTracking newPaymentTracking(String creditTransactionId, String debitTransactionId, Double amount ) {
		return newPaymentTracking(UUIDGen.getUUID(), creditTransactionId, debitTransactionId, amount);
	}

	protected static PaymentTracking newPaymentTracking(String paymentTrackId, String creditTransactionId, String debitTransactionId, Double amount ) {
		return new PaymentTracking(paymentTrackId, creditTransactionId, debitTransactionId, amount);
	}

}
