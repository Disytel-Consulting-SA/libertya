package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class CalloutPaymentBatchPOInvoices extends CalloutEngine {
	
	public String invoice(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if(value == null) {
            return "";
        }
				
		//Recupero datos 
		Integer detailId = (Integer)mTab.getValue("C_PaymentBatchpoDetail_ID");
		MPaymentBatchPODetail detail = new MPaymentBatchPODetail(ctx, detailId, null);
		MPaymentBatchPO paymentBatch = new MPaymentBatchPO(ctx, detail.getC_PaymentBatchPO_ID(), null);
		Integer invoiceId = (Integer) value;
		MInvoice invoice = new MInvoice(ctx, invoiceId, null);
		int C_InvoicePaySchedule_ID = 0;
        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_Invoice_ID" ) == invoiceId.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" ) != 0) ) {
            C_InvoicePaySchedule_ID = Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" );
        }
        MInvoicePaySchedule paySchedule = new MInvoicePaySchedule(ctx, C_InvoicePaySchedule_ID, null);
		
		//Verifico que la factura elegida sea del proveedor seleccionado en el detalle 
	    //(por defecto la ventana info para búsqueda filtra por proveedor)
		if (invoice.getC_BPartner_ID() != detail.getC_BPartner_ID()) {
			mField.setError(true);
			return Msg.getMsg(ctx, "VendorDifferent");
		}
			
		
		//Verifico que la factura elegida no esté ya incorporada en el detalle
		if (MPaymentBatchPOInvoices.invoiceInDetail(invoiceId, detailId)) {
			mField.setError(true);
			return Msg.getMsg(ctx, "InvoiceAlreadyInDetail");
		}
		
		//Verifico que tenga importes pendientes y que esté CO o CL
		if (!MPaymentBatchPOInvoices.invoiceIsOk(invoiceId, C_InvoicePaySchedule_ID)) {
			mField.setError(true);
			return Msg.getMsg(ctx, "InvoiceWithoutOpenAmountOrNotAuthorized");
		}
		
		//Importe en pesos de la factura 
		BigDecimal convertedAmt = MCurrency.currencyConvert(
				paySchedule.getDueAmt(), invoice.getC_Currency_ID(),
				paymentBatch.getC_Currency_ID(), paymentBatch.getBatchDate(), invoice.getAD_Org_ID(),
				ctx);
		
		//Importe pendiente en pesos de la factura
		BigDecimal convertedOpenAmt = MCurrency.currencyConvert(
				paySchedule.getOpenAmount(), invoice.getC_Currency_ID(),
				paymentBatch.getC_Currency_ID(), paymentBatch.getBatchDate(), invoice.getAD_Org_ID(),
				ctx);
		if (convertedOpenAmt == null || convertedAmt == null) {
			throw new IllegalArgumentException(Msg.getMsg(ctx, "ConvertionRateInvalid"));
		}
		
		//Actualizo los valores de los campos de solo lectura
		mTab.setValue("DocumentNo", invoice.getDocumentNo());
		mTab.setValue("DateInvoiced", invoice.getDateInvoiced());        
        mTab.setValue("DueDate", paySchedule.getDueDate());
        mTab.setValue("InvoiceAmount", convertedAmt);
        mTab.setValue("OpenAmount", convertedOpenAmt);
        mTab.setValue("PaymentAmount", convertedOpenAmt);
        mTab.setValue("C_InvoicePaySchedule_ID", paySchedule.getID());
        
		return "";
	}

}
