package com.company.ms.userapi.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(content = Include.NON_NULL, value = Include.NON_NULL)
public class TransactionData {

	private String account_id;
	private int operation_type_id;
	private Amount amount;
	
}
