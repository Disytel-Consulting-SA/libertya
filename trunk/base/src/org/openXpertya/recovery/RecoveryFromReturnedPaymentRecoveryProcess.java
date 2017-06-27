package org.openXpertya.recovery;

import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;

public class RecoveryFromReturnedPaymentRecoveryProcess extends ReturnedPaymentRecoveryProcess {

	public RecoveryFromReturnedPaymentRecoveryProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public RecoveryFromReturnedPaymentRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}

	@Override
	protected void createDocument() throws Exception {
		setDocument(createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Credit_Recovery_ID(),
				getPaymentRecoveryConfig().getM_Product_Recovery_ID(),getRecoveryType().getRecoveryAmt()));
	}
	
	@Override
	protected void appendSuccesfullyFinalMsg(HTMLMsg msg) {
		HTMLMsg.HTMLList list = msg.createList("summarycustom", "ul", Msg.getMsg(getCtx(), "Resolutions"));
		msg.createAndAddListElement("invoicecredit", Msg.translate(getCtx(), "@InvoiceCreditGenerated@: "
				+ getDocument().getDocumentNo() + ". @Amt@ " + getDocument().getGrandTotal()), list);
		msg.createAndAddListElement("allocation", Msg.translate(getCtx(),
				"@AllocationsGenerated@: " + getAllocGenerator().getAllocationHdr().getDocumentNo()), list);
		msg.addList(list);
	}
}
