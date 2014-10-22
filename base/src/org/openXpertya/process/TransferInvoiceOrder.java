package org.openXpertya.process;

import java.util.HashMap;
import java.util.Map;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class TransferInvoiceOrder extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		Integer invoiceID = (Integer)getParametersValues().get("C_INVOICE_ID");
		Integer orgID = (Integer)getParametersValues().get("AD_ORG_ID");
		Integer warehouseID = (Integer)getParametersValues().get("M_WAREHOUSE_ID");
		MInvoice invoice = new MInvoice(getCtx(), invoiceID, get_TrxName());
		MOrder transferableOrder;
		ProcessInfo info = null;
		// Pedido de la factura
		Integer orderID = invoice.getC_Order_ID();
		if(!Util.isEmpty(orderID, true)){
			MOrder order = new MOrder(getCtx(), orderID, get_TrxName());
			transferableOrder = new MOrder(getCtx(), 0, get_TrxName());
			MDocType transferableDocType = MDocType.getDocType(getCtx(),
					MDocType.DOCTYPE_Pedido_Transferible, get_TrxName());
			MOrder.copyValues(order, transferableOrder);
			transferableOrder.setAD_Org_Transfer_ID(orgID);
			transferableOrder.setM_Warehouse_Transfer_ID(warehouseID);
			transferableOrder.setC_DocTypeTarget_ID(transferableDocType.getID());
			transferableOrder.setC_DocType_ID(transferableDocType.getID());
			transferableOrder.setDateOrdered(Env.getDate(getCtx()));
			transferableOrder.setDocStatus(MOrder.DOCSTATUS_Drafted);
			transferableOrder.setDocAction(MOrder.DOCACTION_Complete);
			transferableOrder.setProcessed(false);
			if(!transferableOrder.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("C_Invoice_ID", invoiceID);
			parameters.put("CopyHeader", "Y");
			info = MProcess.execute(getCtx(),
					getCopyFromOrderProcessID(), X_C_Order.Table_ID, parameters,
					get_TrxName(), transferableOrder.getID());
			// Recargar el pedido transferible
			transferableOrder = new MOrder(getCtx(), transferableOrder.getID(), get_TrxName());
			// Completarlo
			if(!DocumentEngine.processAndSave(transferableOrder, MOrder.DOCACTION_Complete, false)){
				throw new Exception(transferableOrder.getProcessMsg());
			}
		} 
		else{
			throw new Exception(Msg.getMsg(getCtx(), "NonExistsOrderRelated"));
		}
		
		// Armar el mensaje
		StringBuffer msg = new StringBuffer();
		msg.append(Msg.getMsg(getCtx(), "TransferableDocumentCreated"));
		msg.append(" : ");
		msg.append(transferableOrder.getDocumentNo());
		return msg.toString();
	}
	
	protected Integer getCopyFromOrderProcessID(){
		return DB
				.getSQLValue(
						get_TrxName(),
						"SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = 'CORE-AD_Process-211'");
	}

}
