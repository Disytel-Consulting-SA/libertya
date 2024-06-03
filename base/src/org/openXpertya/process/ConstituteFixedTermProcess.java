package org.openXpertya.process;

import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MFixedTerm;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class ConstituteFixedTermProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		//Recupero el plazo fijo 
		MFixedTerm fixedTerm = new MFixedTerm(getCtx(), getRecord_ID(), get_TrxName());  
		
		//Genero la transferencia del capital desde la cuenta de banco a la de plazo fijo  
		MBankTransfer transfer = new MBankTransfer(getCtx(), 0, get_TrxName());
		transfer.setAD_Org_ID(fixedTerm.getAD_Org_ID());
		transfer.setDocStatus(MBankTransfer.DOCSTATUS_Drafted);
		transfer.setDocAction(MBankTransfer.DOCACTION_Complete);
		transfer.setDateTrx(fixedTerm.getTrxDate());	
		int c_bpartner_id = DB.getSQLValue(get_TrxName(), "SELECT c_bpartner_id FROM c_bank WHERE c_bank_id = " + fixedTerm.getC_Bank_ID());
		if(c_bpartner_id > 0) {
			transfer.setC_BPartner_ID(c_bpartner_id);			
		} else {
			throw new Exception("El banco utilizado debe tener una Entidad Comercial asociada.");
		}
		transfer.setDescription(Msg.getMsg(getCtx(), "FixedTermConstitutionDescription") + fixedTerm.getID());
		transfer.setC_bankaccount_from_ID(fixedTerm.getC_BankAccount_ID());
		transfer.setC_bankaccount_to_ID(fixedTerm.getC_BankAccountFixedTerm_ID());
		transfer.setC_currency_from_ID(fixedTerm.getC_Currency_ID());
		transfer.setC_currency_to_ID(fixedTerm.getC_Currency_ID());
		transfer.setammount_from(fixedTerm.getInitialAmount());
		
		//Guardo la transferencia
		if (!transfer.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermConstitutionError") + " " + transfer.getProcessMsg());
		}
		
		//Completo la operación de transferencia
		if (!DocumentEngine.processAndSave((DocAction) transfer, DocAction.ACTION_Complete, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermConstitutionError") + " " + transfer.getProcessMsg());
		}
		
		//Setear referencia a la operación y marco el plazo fijo como constituido
		fixedTerm.setC_BankTransferConstitution_ID(transfer.getID());
		fixedTerm.setConstituted(true);
		if (!fixedTerm.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermConstitutionError") + " " + fixedTerm.getProcessMsg());
		}
		
		return Msg.getMsg(getCtx(), "FixedTermConstitutionSuccess;");
	}

}
