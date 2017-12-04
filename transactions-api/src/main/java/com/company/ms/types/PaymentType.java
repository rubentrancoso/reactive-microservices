package com.company.ms.types;

import java.util.HashMap;
import java.util.Map;

public enum PaymentType {
	
	COMPRA_A_VISTA(1,"COMPRA A VISTA",2),
	COMPTA_PARCELADA(2,"COMPRA PARCELADA",1),
	SAQUE(3,"SAQUE",0),
	PAGAMENTO(4,"PAGAMENTO",0);
	
	private static Map<Integer, PaymentType> map = new HashMap<Integer, PaymentType>();
	
	private int operationType;
	private String description;
	private int chargeOrder;
	
	static {
        for (PaymentType paymentType : PaymentType.values()) {
            map.put(paymentType.operationType, paymentType);
        }
    }	
	
	PaymentType(int operationType, String description, int chargeOrder) {
		this.operationType = operationType;
		this.description = description;
		this.chargeOrder = chargeOrder;
	}
	
	public static PaymentType getPaymentType(int operation_type_id) {
		return map.get(operation_type_id);
	}	
	public int operationType() {
		return this.operationType;
	}
	public String description() {
		return this.description;
	}
	public int chargeOrder() {
		return this.chargeOrder;
	}

}
