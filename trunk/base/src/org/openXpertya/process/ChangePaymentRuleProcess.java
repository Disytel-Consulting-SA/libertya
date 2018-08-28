package org.openXpertya.process;

import java.util.List;
import java.util.Map;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPOSLetter;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ChangePaymentRuleProcess extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		HTMLMsg msg = new HTMLMsg();
		HTMLList list = msg.createList("Final", "ul");
		// Si las formas de pago del comprobante parámetro y destino del proceso son iguales, error
		MInvoice invoice = new MInvoice(getCtx(), (Integer) getParametersValues().get("C_INVOICE_ID"), get_TrxName());
		if(invoice.getPaymentRule().equals((String)getParametersValues().get("PAYMENTRULE"))){
			throw new Exception(Msg.getMsg(getCtx(), "SamePaymentRule"));
		}
		
		// Copiar los valores del comprobante parámetro sobre uno nuevo y anular
		// el que viene por parámetro
		// Copiar los valores
		boolean localeARActive = CalloutInvoiceExt.ComprobantesFiscalesActivos();
		MDocType invoiceDoctype = new MDocType(getCtx(), invoice.getC_DocTypeTarget_ID(), get_TrxName());
		MDocType copyDocType = invoiceDoctype;
		invoice.setSkipExtraValidations(true);
		invoice.setSkipModelValidations(true);
		if (localeARActive & invoice.isSOTrx()) {

			// Se obtiene el tipo de documento a generar en base al comprobante copia.
			// Obtener el punto de venta:
			// 1) Desde la caja diaria, priorizando la personalización de
			// punto de venta por letra de la config del tpv asociada a ella
			// 2) Desde la config de TPV, si es que posee una sola,
			// priorizando la personalización de punto de venta por letra
			// 3) Desde la factura a anular
			Integer ptoVenta = null;
			String letra = invoice.getLetra();
			// 1)
			if (MPOSJournal.isActivated()) {
				ptoVenta = MPOSJournal.getCurrentPOSNumber(letra);
			}
			// 2)
			if (Util.isEmpty(ptoVenta, true)) {
				List<MPOS> pos = MPOS.get(getCtx(),
						Env.getAD_Org_ID(getCtx()),
						Env.getAD_User_ID(getCtx()), get_TrxName());
				if (pos.size() == 1) {
					Map<String, Integer> letters = MPOSLetter
							.getPOSLetters(pos.get(0).getID(),
									get_TrxName());
					ptoVenta = letters.get(letra) != null ? letters
							.get(letra) : pos.get(0).getPOSNumber();
				}
			}
			// 3)
			if (Util.isEmpty(ptoVenta, true)) {
				ptoVenta = invoice.getPuntoDeVenta();
			}
			// Se obtiene el tipo de documento del contramovimiento.
			copyDocType = MDocType.getDocType(getCtx(), invoice.getAD_Org_ID(),
					invoiceDoctype.getBaseKey(), invoice.getLetra(), ptoVenta,
					get_TrxName());
			// Error si no se encuentra el tipo de documento
			if(copyDocType == null){
				throw new Exception(
						Msg.getMsg(getCtx(), "LARDocTypeNotFounded", new Object[] { ptoVenta, invoice.getLetra() }));
			}
		}
		// Copiar los valores del comprobante parámetro
		MInvoice invoiceCopy = MInvoice.copyFrom(invoice, Env.getDate(), copyDocType.getID(), invoice.isSOTrx(), false,
				get_TrxName(), true, true, true, !invoice.isSOTrx(), false, false);
		invoiceCopy.setPaymentRule((String)getParametersValues().get("PAYMENTRULE"));		
		if(!invoiceCopy.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		
		// Anular el comprobante parámetro
		if(!DocumentEngine.processAndSave(invoice, MInvoice.DOCACTION_Void, false)){
			throw new Exception(invoice.getProcessMsg());
		}
		
		// Completar el comprobante parámetro
		invoiceCopy.setFiscalAlreadyPrinted(false);
		if(!DocumentEngine.processAndSave(invoiceCopy, MInvoice.DOCACTION_Complete, false)){
			throw new Exception(invoiceCopy.getProcessMsg());
		}
		
		msg.createAndAddListElement(String.valueOf(invoice.getID()),
				Msg.getMsg(getCtx(), "VoidedDocument") + " : " + invoice.getDocumentNo(), list);
		msg.createAndAddListElement(String.valueOf(invoiceCopy.getID()),
				Msg.getMsg(getCtx(), "DocumentCreated") + " : " + invoiceCopy.getDocumentNo(), list);
		msg.addList(list);
		
		return msg.toString();
	}

}
