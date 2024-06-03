package org.openXpertya.process;

import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MFixedTerm;
import org.openXpertya.util.Msg;

public class VoidConstituteFixedTermProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		//Recupero el plazo fijo 
		MFixedTerm fixedTerm = new MFixedTerm(getCtx(), getRecord_ID(), get_TrxName());
		
		//Anulo transferencia por el capital
		MBankTransfer transfer = new MBankTransfer(getCtx(), fixedTerm.getC_BankTransferConstitution_ID(), get_TrxName());
		if (!DocumentEngine.processAndSave((DocAction) transfer, DocAction.ACTION_Void, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidConstitutionError") + " " + transfer.getProcessMsg());
		}
		
		//Setear referencia a las operaciones y marco el plazo fijo como acreditado
		fixedTerm.setC_BankTransferConstitution_ID(0);
		fixedTerm.setConstituted(false);
		fixedTerm.setCertificate(null);
		if (!fixedTerm.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermVoidConstitutionError") + " " + fixedTerm.getProcessMsg());
		}		
				
		return Msg.getMsg(getCtx(), "FixedTermVoidConstitutionSuccess;");
	}

}
