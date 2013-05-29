package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPaymentTerm;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRefList;
import org.openXpertya.print.fiscal.document.CurrentAccountInfo;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PrintCurrenAccountDocument extends SvrProcess {

	/** Factura */
	private MInvoice invoice;
	
	/** ID de factura */
	private Integer invoiceID;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();

            if( name.equals( "C_Invoice_ID" )) {
                setInvoiceID(para[ i ].getParameterAsInt());
            } 
            else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		CurrentAccountInfo currentAccountInfo = null;
		CurrentAccountInfo infoOnCredit = null;
		List<CurrentAccountInfo> infos = new ArrayList<CurrentAccountInfo>();
		MBPartner bPartner = new MBPartner(getCtx(), getInvoice()
				.getC_BPartner_ID(), get_TrxName());
		BigDecimal inCreditAllocationAmt = getInvoice().getAllocatedAmt(true, false, false, null);
		if (getInvoice() != null
				&& (getInvoice().getInitialCurrentAccountAmt().compareTo(BigDecimal.ZERO) > 0 
					 || (bPartner.isAutomaticCreditNotes() 
							 && !Util.isEmpty(inCreditAllocationAmt, true)))) {
			if (getInvoice().getInitialCurrentAccountAmt().compareTo(
					BigDecimal.ZERO) > 0) {
				currentAccountInfo = new CurrentAccountInfo(null,
						MRefList.getListName(getCtx(),
								MInvoice.PAYMENTRULE_AD_Reference_ID,
								MInvoice.PAYMENTRULE_OnCredit),
						getInvoice().getInitialCurrentAccountAmt());
				infos.add(currentAccountInfo);
				infoOnCredit = currentAccountInfo;
			}
			// Sumar lo de NC siempre que sea automática y tildar aquella
			// condición a crédito
			if (bPartner.isAutomaticCreditNotes() 
					 && !Util.isEmpty(inCreditAllocationAmt, true)) {
				if(infoOnCredit != null){
					infoOnCredit.setAmount(infoOnCredit.getAmount().add(
							inCreditAllocationAmt));
				}
				else{
					currentAccountInfo = new CurrentAccountInfo(null,
							MRefList.getListName(getCtx(),
									MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
									MPOSPaymentMedium.TENDERTYPE_CreditNote),
									inCreditAllocationAmt);
					infos.add(currentAccountInfo);
				}
			}
			
			// Imprimo el ticket no fiscal de cuenta corriente, en caso que no
			// se imprima por la fiscal, entonces va al formato jasper
			MDocType docType = MDocType.get(getCtx(), getInvoice()
					.getC_DocTypeTarget_ID());
			if (docType.getC_Controlador_Fiscal_ID() > 0) {
				// Impresor de comprobantes.
				FiscalDocumentPrint fdp = new FiscalDocumentPrint();
//				fdp.addDocumentPrintListener(getFiscalDocumentPrintListener());
//				fdp.setPrinterEventListener(getFiscalPrinterEventListener());
				if (!fdp.printCurrentAccountDocument(
						docType.getC_Controlador_Fiscal_ID(), bPartner, getInvoice(), infos)) {
					throw new Exception(fdp.getErrorMsg());
				}
			}
			else{
				Map<String, Object> params = new HashMap<String, Object>();
				MPaymentTerm paymentTerm = new MPaymentTerm(getCtx(),
						getInvoice().getC_PaymentTerm_ID(), get_TrxName());
				// FIXME Por ahora hay un sólo medio de pago de cuenta corriente
				// (A Crédito). Si existen otros medios de pago con esas
				// condiciones, se debe dar soporte para ellos dentro de este
				// bloque, además de pasarlos como parámetro al reporte
				params.put("AD_Org_ID", Env.getAD_Org_ID(getCtx()));
				params.put("C_BPartner_ID", bPartner.getID());
				params.put("C_Invoice_ID", getInvoiceID());
				params.put(
						"PaymentRule_1",
						infoOnCredit != null ? paymentTerm.getName()
								: MRefList
										.getListName(
												getCtx(),
												MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
												MPOSPaymentMedium.TENDERTYPE_CreditNote));
				params.put("PaymentRule_Amt_1",
						infoOnCredit != null ? infoOnCredit.getAmount()
								: currentAccountInfo.getAmount());
				ProcessInfo info = MProcess.execute(getCtx(), MInvoice
						.getCurrentAccountDocumentProcessID(get_TrxName()),
						params, get_TrxName());
				if(info.isError()){
					throw new Exception(info.getSummary());
				}
			}
		}
		return "";
	}

	public void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	public MInvoice getInvoice() {
		if(invoice == null){
			if(!Util.isEmpty(getInvoiceID(), true)){
				MInvoice the_invoice = new MInvoice(getCtx(), getInvoiceID(),
						get_TrxName());
				setInvoice(the_invoice);
			}
		}
		return invoice;
	}

	protected Integer getInvoiceID() {
		return invoiceID;
	}

	protected void setInvoiceID(Integer invoiceID) {
		this.invoiceID = invoiceID;
	}
	
}
