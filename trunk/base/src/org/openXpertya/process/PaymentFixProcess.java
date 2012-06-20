package org.openXpertya.process;

import org.openXpertya.model.MPaymentFix;

public class PaymentFixProcess extends SvrProcess {

	/** ID de la corrección de pago a procesar */
	private int p_PaymentFixID = 0; 
	
	@Override
	protected void prepare() {
		p_PaymentFixID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		MPaymentFix paymentFix = new MPaymentFix(getCtx(), p_PaymentFixID, get_TrxName());
		if (!paymentFix.process()) {
			throw new Exception(paymentFix.getProcessMsg());
		}
		
		if (!paymentFix.save()) {
			throw new Exception(paymentFix.getProcessMsg());
		}
		
		return "@Processed@";
	}

}
