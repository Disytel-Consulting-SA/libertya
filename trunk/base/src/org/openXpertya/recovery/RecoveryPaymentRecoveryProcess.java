package org.openXpertya.recovery;

import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;

/**
 * Este caso se da cuando se realiza un payment que compensa un faltante de otra
 * transacción. Por ejemplo, luego de una corrección de cobro en donde fue mal
 * ingresado el importe (menor al real), el cliente debe volver a pagar. Se crea
 * una ND mediante la configuración correspondiente y se asocia el payment
 * parámetro.
 * 
 * @author Matías Cap - Disytel
 *
 */
public class RecoveryPaymentRecoveryProcess extends ReturnedPaymentRecoveryProcess {
	
	public RecoveryPaymentRecoveryProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public RecoveryPaymentRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}
	
	protected String getRecoveredOption(){
		return (String) getParametersValues().get("RECOVEREDOPTION");
	}
	
	@Override
	protected void createRecoveryType() throws Exception {
		setRecoveryType(RecoveryTypeFactory.getInstance(this, getRecoveredOption()));
	}
	
	@Override
	protected void createDocument() throws Exception {
		setDocument(createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Recovery_ID(),
				getPaymentRecoveryConfig().getM_Product_Recovery_ID(),getRecoveryType().getRecoveryAmt()));
	}
	
	@Override
	protected void appendSuccesfullyFinalMsg(HTMLMsg msg) {
		HTMLMsg.HTMLList list = msg.createList("summarycustom", "ul", Msg.getMsg(getCtx(), "Resolutions"));
		msg.createAndAddListElement(
				"invoicedebit", Msg.translate(getCtx(), "@InvoiceDebitGenerated@: "
						+ getDocument().getDocumentNo() + ". @Amt@ " + getDocument().getGrandTotal()),
				list);
		msg.createAndAddListElement("allocation", Msg.translate(getCtx(),
				"@AllocationsGenerated@: " + getAllocGenerator().getAllocationHdr().getDocumentNo()), list);
		msg.addList(list);
	}

}
