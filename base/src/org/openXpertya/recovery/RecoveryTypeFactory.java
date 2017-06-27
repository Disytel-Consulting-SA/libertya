package org.openXpertya.recovery;

public class RecoveryTypeFactory {

	public static final String RECOVERY_CASHLINE = "C";
	public static final String RECOVERY_PAYMENT = "P";
	public static final String RECOVERY_INVOICE = "I";

	protected static RecoveryType getInstance(IRecoverySource rs, String recoveryOption){
		RecoveryType rt = null;
		if(recoveryOption.equals(RECOVERY_PAYMENT)){
			rt = new RecoveryPayment(rs);
		}
		else if(recoveryOption.equals(RECOVERY_CASHLINE)){
			rt = new RecoveryCashLine(rs);
		}
		else if(recoveryOption.equals(RECOVERY_INVOICE)){
			rt = new RecoveryInvoice(rs);
		}
		return rt;
	}
	
}
