package org.openXpertya.util;

import java.util.HashMap;
import java.util.Map;

public class UserAuthConstants {

	/** Momento de agregar un pago en el TPV */
	public static final String POS_ADD_PAYMENT_MOMENT = "POS_ADD_PAYMENT_MOMENT";
	/** Momento de cancelar el pedido en el TPV */
	public static final String POS_CANCEL_ORDER_MOMENT = "POS_CANCEL_ORDER_MOMENT";
	/** Momento de iniciar el TPV */
	public static final String POS_INIT_MOMENT = "POS_INIT_MOMENT";
	/** Momento de finalizar la venta del TPV */
	public static final String POS_FINISH_MOMENT = "POS_FINISH_MOMENT";
	/**
	 * Momento de anulación de comprobantes en el TPV (error al imprimir
	 * el comprobante)
	 */
	public static final String POS_VOID_DOCUMENT = "POS_VOID_DOCUMENT";
	
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
	/** Autorización de inicialización de TPV */
	public static final String POS_INIT_UID = "CORE-AD_Process-1010284";
	/** Autorización de aplicación de descuento/recargo manual general */
	public static final String POS_MANUAL_GENERAL_DISCOUNT_UID = "CORE-AD_Process-1010295";
	/** Autorización de anulación de comprobantes en TPV */
	public static final String POS_VOID_DOCUMENTS_UID = "CORE-AD_Process-1010296";
	/** Autorización de Apertura del cajón de dinero */
	public static final String OPEN_DRAWER_UID = "CORE-AD_Process-1010337";
	
	/** Asociación de uids de procesos con sus values */
	public static Map<String, String> processValues = new HashMap<String, String>(); 
	
	static{
		processValues.put(POS_MODIFY_PRICE_ORDER_PRODUCT_UID, "POSModifyOrderProductPrice");
		processValues.put(POS_CN_MAX_CASH_RETURN_UID, "POSSurpassMaxReturnCashInCN");
		processValues.put(POS_CANCEL_ORDER_UID, "POSCancelOrder");
		processValues.put(POS_INIT_UID, "POSInitAuthorization");
		processValues.put(POS_MANUAL_GENERAL_DISCOUNT_UID, "POSManualGeneralDiscountAuth");
		processValues.put(POS_VOID_DOCUMENTS_UID, "POSVoidDocumentsAuth");
		processValues.put(OPEN_DRAWER_UID, "OpenDrawerAuthorization");
	}
	
	public static String getProcessValue(String key){
		return processValues.get(key);
	}
	
}
