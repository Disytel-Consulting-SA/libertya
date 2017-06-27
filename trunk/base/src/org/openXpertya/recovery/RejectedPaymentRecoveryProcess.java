package org.openXpertya.recovery;

import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;

/**
 * Este caso se da cuando la entidad financiera rechaza el cobro/pago por algún
 * motivo.
 * <ol>
 * Esta situación se resuelve realizando las siguientes operaciones:
 * <li>Se crea el payment de devolución del payment rechazado parámetro.</li>
 * <li>Se crea la NC en base a los campos de Rechazo de Tipo de Documento y
 * Artículo en la configuración. El importe de la misma es el mismo que el del
 * payment parámetro.</li>
 * <li>Se imputan entre ellos.</li>
 * </ol>
 * 
 * @author Matías Cap - Disytel
 *
 */
public class RejectedPaymentRecoveryProcess extends ReturnedPaymentRecoveryProcess {

	public RejectedPaymentRecoveryProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public RejectedPaymentRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}
	
	/** Payment de devolución */
	private MPayment recoveryPayment;
	
	public Integer getPaymentID() {
		return (Integer) getParametersValues().get("C_PAYMENT_ID");
	}
	
	@Override
	protected void createRecoveryType() throws Exception {
		// Generar el payment de devolución del payment parámetro
		setRecoveryPayment(createReturnedPayment(getPaymentID()));
		// El tipo de recupero en este caso es una devolución de un payment,
		// previa devolución de mercadería en una transacción
		setRecoveryType(RecoveryTypeFactory.getInstance(this, RecoveryTypeFactory.RECOVERY_PAYMENT));
	}
	
	@Override
	public Integer getPaymentRecoveryID() {
		return getRecoveryPayment().getID();
	}

	@Override
	protected void createDocument() throws Exception {
		setDocument(createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Credit_Rejected_ID(),
				getPaymentRecoveryConfig().getM_Product_Rejected_ID(),getRecoveryPayment().getPayAmt()));
	}
	
	@Override
	protected void appendSuccesfullyFinalMsg(HTMLMsg msg) {
		HTMLMsg.HTMLList list = msg.createList("summarycustom", "ul", Msg.getMsg(getCtx(), "Resolutions"));
		msg.createAndAddListElement("invoicecredit", Msg.translate(getCtx(), "@InvoiceCreditGenerated@: "
				+ getDocument().getDocumentNo() + ". @Amt@ " + getDocument().getGrandTotal()), list);
		MDocType paymentDocType = MDocType.get(getCtx(), getRecoveryPayment().getC_DocType_ID(), getTrxName());
		msg.createAndAddListElement("payment", paymentDocType.getName() + ": " + getRecoveryPayment().getDocumentNo(),
				list);
		msg.createAndAddListElement("allocation", Msg.translate(getCtx(),
				"@AllocationsGenerated@: " + getAllocGenerator().getAllocationHdr().getDocumentNo()), list);
		msg.addList(list);
	}

	protected MPayment getRecoveryPayment() {
		return recoveryPayment;
	}

	protected void setRecoveryPayment(MPayment recoveryPayment) {
		this.recoveryPayment = recoveryPayment;
	}

}
