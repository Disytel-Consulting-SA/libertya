package org.openXpertya.recovery;

import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;

/**
 * Corrección de cobros/pagos por Devolución/Recupero.
 * 
 * Este caso de corrección corresponde a una devolución de cobro/pago. El
 * procedimiento recibe una Nota de Crédito que hace las veces de devolución
 * sobre una transacción. A su vez, recibe el cobro/pago realizado por
 * devolución y se imputan entre ellas.
 * 
 * @author Matías Cap - Disytel
 *
 */
public class ReturnedPaymentRecoveryProcess extends PaymentRecoveryStrategyProcess {
	
	public ReturnedPaymentRecoveryProcess(){
		
	}
	
	public ReturnedPaymentRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}
	
	@Override
	protected void createRecoveryType() throws Exception {
		// El tipo de recupero en este caso es una devolución de un payment,
		// previa devolución de mercadería en una transacción
		setRecoveryType(RecoveryTypeFactory.getInstance(this, RecoveryTypeFactory.RECOVERY_PAYMENT));
	}
	
	@Override
	protected void createDocument() throws Exception {
		setDocument(new MInvoice(getCtx(), getCreditRecoveryID(), getTrxName()));
	}

	@Override
	protected void appendSuccesfullyFinalMsg(HTMLMsg msg) {
		HTMLMsg.HTMLList list = msg.createList("summarycustom", "ul", Msg.getMsg(getCtx(), "Resolutions"));
		msg.createAndAddListElement("allocation", Msg.translate(getCtx(),
				"@AllocationsGenerated@: " + getAllocGenerator().getAllocationHdr().getDocumentNo()), list);
		msg.addList(list);
	}

}
