package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CreateCashLineProcess extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		BigDecimal amount = (BigDecimal)getParametersValues().get("AMOUNT");
		if(Util.isEmpty(amount, true)){
			throw new Exception(Msg.getMsg(getCtx(), "ValueMustBeDifferentThanZero",
					new Object[] { Msg.translate(getCtx(), "Amt") }));
		}
		// Obtengo la caja parámetro 
		MCash cash = new MCash(getCtx(), (Integer) getParametersValues().get("C_CASH_ID"), get_TrxName());
		MCashLine cashLine = new MCashLine(cash);
		cashLine.setC_Currency_ID(cash.getC_Currency_ID());
		cashLine.setAmount(amount);
		Integer chargeID = (Integer) getParametersValues().get("C_CHARGE_ID");
		if(!Util.isEmpty(chargeID, true)){
			cashLine.setCashType(MCashLine.CASHTYPE_Charge);
			cashLine.setC_Charge_ID(chargeID);
		}
		cashLine.addDescription(Msg.getMsg(getCtx(), "AutomaticGeneratedCashLine"));
		cashLine.setC_POSJournal_ID(cash.getC_POSJournal_ID());
		cashLine.setIgnorePOSJournal(true);
		cashLine.setAutomaticGenerated(true);
		if(!cashLine.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		if(!DocumentEngine.processAndSave(cashLine, MCashLine.DOCACTION_Complete, false)){
			throw new Exception(cashLine.getProcessMsg());
		}
		return Msg.getMsg(getCtx(), "AutomaticGeneratedCashLine") + " : " + Msg.translate(getCtx(), "Line") + " "
				+ cashLine.getLine() + " " + Msg.translate(getCtx(), "Amt") + "= "+ cashLine.getAmount() + " - "
				+ Msg.translate(getCtx(), "C_Cash_ID") + " " + cash.getName();
	}

}
