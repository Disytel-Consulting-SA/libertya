package org.openXpertya.process;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MProcess;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class PrintWarehouseDeliveryDocument extends SvrProcess {

	/** Pedido */
	private MOrder order;
	
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
		if(getOrder() == null){
			throw new Exception(Msg.getMsg(getCtx(), "OrderIsMandatory"));
		}
		
		// Si existe factura como parámetro, entonces se debe imprimir la salida
		// de depósito en base al tipo de documento, si es fiscal se imprime
		// ticket fiscal, sino se imprime jasper
		Integer warehouseDeliveryCount = getOrder().getWarehouseDeliveryProductsCount();
		if(warehouseDeliveryCount > 0){
			if (getInvoice() != null && getInvoice().requireFiscalPrint()) {
				MDocType docType = MDocType.get(getCtx(),
						getInvoice().getC_DocTypeTarget_ID(), get_TrxName());
				if (docType.getC_Controlador_Fiscal_ID() > 0) {
					// Impresor de comprobantes.
					FiscalDocumentPrint fdp = new FiscalDocumentPrint();
//					fdp.addDocumentPrintListener(getFiscalDocumentPrintListener());
//					fdp.setPrinterEventListener(getFiscalPrinterEventListener());
					if (!fdp.printDeliveryDocument(
							docType.getC_Controlador_Fiscal_ID(), getOrder(),
							getInvoice())) {
						throw new Exception(fdp.getErrorMsg());
					}
				}
			}
			else{
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("C_Order_ID", getOrder().getID());
				params.put("C_Invoice_ID", getInvoiceID());
				ProcessInfo info = MProcess.execute(getCtx(), MInvoice
						.getWarehouseDeliverDocumentProcessID(get_TrxName()),
						params, get_TrxName());
				if(info.isError()){
					throw new Exception(info.getSummary());
				}
			}
		}
		else{
			throw new Exception(Msg.getMsg(getCtx(),
					"NoWarehouseDeliveryProducts"));
		}
		
		return "";
	}

	protected void setOrder(MOrder order) {
		this.order = order;
	}

	protected MOrder getOrder() {
		if(order == null){
			MOrder the_order = null;
			// Buscar el pedido por la factura
			if (the_order == null && getInvoice() != null
					&& !Util.isEmpty(getInvoice().getC_Order_ID(), true)) {
				the_order = new MOrder(getCtx(), getInvoice().getC_Order_ID(),
						get_TrxName());
			}
			setOrder(the_order);
		}
		return order;
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
