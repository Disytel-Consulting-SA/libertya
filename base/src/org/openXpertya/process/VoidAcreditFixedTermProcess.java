package org.openXpertya.process;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MFixedTerm;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.Msg;

public class VoidAcreditFixedTermProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		//Recupero el plazo fijo 
		MFixedTerm fixedTerm = new MFixedTerm(getCtx(), getRecord_ID(), get_TrxName());
		
		//Anulo medio de cobro por el interés en la cuenta bancaria del plazo fijo
		MPayment interestPayment = new MPayment(getCtx(), fixedTerm.getC_PaymentInterest_ID(), get_TrxName());
		if (!DocumentEngine.processAndSave((DocAction) interestPayment, DocAction.ACTION_Void, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidAcreditationError") + " " + interestPayment.getProcessMsg());
		}
		
		//Anulo retenciones (recibo)
		MAllocationHdr hdr = new MAllocationHdr(getCtx(), fixedTerm.getC_AllocationRetentionHdr_ID(), get_TrxName());
		if (!DocumentEngine.processAndSave((DocAction) hdr, DocAction.ACTION_Void, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidAcreditationError") + " " + hdr.getProcessMsg());
		}
		
		//Anulo transferencia por el neto
		MBankTransfer transfer = new MBankTransfer(getCtx(), fixedTerm.getC_BankTransferAccreditation_ID(), get_TrxName());
		if (!DocumentEngine.processAndSave((DocAction) transfer, DocAction.ACTION_Void, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidAcreditationError") + " " + transfer.getProcessMsg());
		}
		
		//Setear referencia a las operaciones y marco el plazo fijo como acreditado
		fixedTerm.setC_PaymentInterest_ID(0);
		fixedTerm.setC_AllocationRetentionHdr_ID(0);
		fixedTerm.setC_BankTransferAccreditation_ID(0);
		fixedTerm.setAccredited(false);
		if (!fixedTerm.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidAcreditationError") + " " + fixedTerm.getProcessMsg());
		}		
				
		return Msg.getMsg(getCtx(), "FixedTermVoidAcreditationSuccess;");
	}

}
