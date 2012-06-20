package org.openXpertya.util;

import java.util.HashMap;
import java.util.Map;

public class UserAuthConstants {

	/** Momento de agregar un pago en el TPV */
	public static final String POS_ADD_PAYMENT_MOMENT = "POS_ADD_PAYMENT_MOMENT";
	/** Momento de cancelar el pedido en el TPV */
	public static final String POS_CANCEL_ORDER_MOMENT = "POS_CANCEL_ORDER_MOMENT";
	
	/*
	 * Claves de búsqueda de los procesos que hacen las veces de operaciones a
	 * autorizar
	 */
	
	/** Modificación de precio de líneas de TPV */
	public static final String POS_MODIFY_PRICE_ORDER_PRODUCT_UID = "CORE-AD_Process-1010242";
	/** Autorización de devolución de efectivo de Notas de Crédito en el TPV */
	public static final String POS_CN_MAX_CASH_RETURN_UID = "CORE-AD_Process-1010252";
	/** Autorización de cancelación de pedido */
	public static final String POS_CANCEL_ORDER_UID = "CORE-AD_Process-1010253";
	
	/** Asociación de uids de procesos con sus values */
	public static Map<String, String> processValues = new HashMap<String, String>(); 
	
	static{
		processValues.put(POS_MODIFY_PRICE_ORDER_PRODUCT_UID, "POSModifyOrderProductPrice");
		processValues.put(POS_CN_MAX_CASH_RETURN_UID, "POSSurpassMaxReturnCashInCN");
		processValues.put(POS_CANCEL_ORDER_UID, "POSCancelOrder");
	}
	
	public static String getProcessValue(String key){
		return processValues.get(key);
	}
	
}
