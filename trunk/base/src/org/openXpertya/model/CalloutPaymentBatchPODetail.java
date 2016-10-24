package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Msg;

public class CalloutPaymentBatchPODetail extends CalloutEngine {

	public String bPartner(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if(value == null) {
            return "";
        }
		
		//Recupero datos 
		Integer bPartnerId = (Integer) value;
		MBPartner bPartner = new MBPartner(ctx, bPartnerId, null);
		if (bPartner.getBatch_Payment_Rule() == null) {
			mField.setError(true);
			return Msg.getMsg(ctx, "BatchPaymentRuleNotSet");
		}
		mTab.setValue("Batch_Payment_Rule", bPartner.getBatch_Payment_Rule());
		
		if (bPartner.getC_BankAccount_ID() != 0) {
			MBankAccount bankAccount = new MBankAccount(ctx, bPartner.getC_BankAccount_ID(), null);
			mTab.setValue("C_BankAccount_ID", bPartner.getC_BankAccount_ID());
			mTab.setValue("C_Bank_ID", bankAccount.getC_Bank_ID());
		}
		
		return "";
	}
}
