package org.openXpertya.pos.model;

import org.openXpertya.util.Util;

public class CreditCardMaskManager {

	// Constantes iniciales que determinan la tarjeta a instanciar
	public static final String STD_CREDIT_CARD_INITIAL_MASK = "%b";
	public static final String TDF_CREDIT_CARD_INITIAL_MASK = "%&"; 
	
	public static CreditCard getCreditCard(String creditCardStr){
		CreditCard creditCard = null;
		if(!Util.isEmpty(creditCardStr, true)){
			// Verificar si es una tarjeta de crédito con máscara inicial
			// standard 
			if (creditCardStr.toUpperCase().startsWith(
					STD_CREDIT_CARD_INITIAL_MASK.toUpperCase())) {
				creditCard = new StdCreditCard(creditCardStr);
			}
			// Verificar si es una tarjeta de crédito TDF
			else if (creditCardStr.toUpperCase().startsWith(
					TDF_CREDIT_CARD_INITIAL_MASK.toUpperCase())) {
				creditCard = new TDFCreditcard(creditCardStr);
			}
		}
		return creditCard;
	}
	
}
