package com.company.ms.types;

import java.util.HashMap;
import java.util.Map;

public enum OperationType {
	
	COMPRA_A_VISTA(1,"COMPRA A VISTA",2),
	COMPTA_PARCELADA(2,"COMPRA PARCELADA",1),
	SAQUE(3,"SAQUE",0),
	PAGAMENTO(4,"PAGAMENTO",0);
	
	private static Map<Integer, OperationType> map = new HashMap<Integer, OperationType>();
	
	private int operationType;
	private String description;
	private int chargeOrder;
	
	static {
        for (OperationType operationType : OperationType.values()) {
            map.put(operationType.operationType, operationType);
        }
    }	
	
	OperationType(int operationType, String description, int chargeOrder) {
		this.operationType = operationType;
		this.description = description;
		this.chargeOrder = chargeOrder;
	}
	
	public static OperationType getOperationType(int operation_type_id) {
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
