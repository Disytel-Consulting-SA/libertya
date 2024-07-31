package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class UpdateUnpaidInvoices extends SvrProcess{

	@Override
	protected void prepare() {
		
	}

	@Override
	protected String doIt() throws Exception {
		
		// dREHER, solo leo las FC que tienen distinto termino de pago actual...
		StringBuffer sql = new StringBuffer("SELECT * FROM c_invoice ci WHERE c_bpartner_id = ?" +
				" AND C_PaymentTerm_ID <> ? " +
				" AND docstatus IN ('CO', 'CL') " +
				" AND invoiceopen(ci.c_invoice_id, null) > 0 ");
		
		StringBuffer errorMsg = new StringBuffer();
		
		MBPartner bpartner = new MBPartner(getCtx(), getRecord_ID(), get_TrxName());
		int paymentTermID = bpartner.getPO_PaymentTerm_ID();
		
		// dREHER Controlo que exista esquema de vencimiento seteado en el socio de negocios
		if(paymentTermID == 0)
			return "Se debe seleccionar un nuevo esquema de vencimiento!";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int ok = 0;
		int ko = 0;
		int items = 0;
		
		try {
			pstmt = DB.prepareStatement(sql.toString());
			pstmt.setInt(1, getRecord_ID());
			pstmt.setInt(2, paymentTermID);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				items++;
				
				MInvoice invoice = new MInvoice(getCtx(), rs, get_TrxName());
				invoice.setC_PaymentTerm_ID(paymentTermID);
				
				// dREHER Valido que se pueda crear el esquema de vencimientos y guardo, sino -> error
				if(invoice.createPaySchedule()) {
					if (!invoice.save()) {
						ko ++;
						errorMsg.append("La Factura " + invoice.getDocumentNo() + " no se pudo actualizar. " + CLogger.retrieveErrorAsString() + " \n");
					}else {
						ok++;
					}
				}else {
					ko++;
					errorMsg.append("La Factura " + invoice.getDocumentNo() + " no se pudo generar nuevo esquema de vencimientos. " + CLogger.retrieveErrorAsString() + " \n");
				}
			}
						
		} catch (Exception e) {
			System.err.println("Error al realizacion la operacion. " + e.getMessage());
			
		} finally {
			rs.close();
			pstmt.close();
		}
		
		return "Proceso finalizado. Comprobantes: " + items + " [OK]:" + ok + " [Error]:" + ko + " - " + errorMsg.toString();
	}

}
