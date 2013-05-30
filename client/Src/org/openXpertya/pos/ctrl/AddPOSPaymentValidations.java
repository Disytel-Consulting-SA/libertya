package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;

import org.openXpertya.pos.view.PoSMainForm;
import org.openXpertya.reflection.CallResult;

public class AddPOSPaymentValidations {

	public CallResult validateCashPayment(PoSMainForm pmf){
		CallResult result = new CallResult();
		return result;
	}
	
	public CallResult validateCheckPayment(PoSMainForm pmf, BigDecimal amount){
		CallResult result = new CallResult();
		return result;
	}
	
	public CallResult validateCreditPayment(PoSMainForm pmf){
		CallResult result = new CallResult();
		return result;
	}
	
	public CallResult validateCreditCardPayment(PoSMainForm pmf, String creditCardNumber, String couponNumber, String couponBatchNumber){
		CallResult result = new CallResult();
		return result;
	}
	
	public CallResult validateCreditNotePayment(PoSMainForm pmf, BigDecimal balanceAmt, boolean returnCash){
		CallResult result = new CallResult();
		return result;
	}
	
	public CallResult validateDirectDepositPayment(PoSMainForm pmf){
		CallResult result = new CallResult();
		return result;
	}
}
