package org.openXpertya.recovery;

import java.util.Map;
import java.util.Properties;

public class RecoveryFromRejectedPaymentRecoveryProcess extends RecoveryPaymentRecoveryProcess {

	public RecoveryFromRejectedPaymentRecoveryProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public RecoveryFromRejectedPaymentRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}
	
	@Override
	protected void createDocument() throws Exception {
		setDocument(createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Rejected_ID(),
				getPaymentRecoveryConfig().getM_Product_Rejected_ID(),getRecoveryType().getRecoveryAmt()));
	}
	
}
