package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Util;

public class MVendorImportInvoice extends X_I_Vendor_Invoice_Import {

	public MVendorImportInvoice(Properties ctx, int I_Vendor_Invoice_Import_ID, String trxName) {
		super(ctx, I_Vendor_Invoice_Import_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MVendorImportInvoice(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeDelete() {
		// Validar que no esté en borrador la factura relacionada
		if(!Util.isEmpty(getC_Invoice_ID(), true)) {
			MInvoice i = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
			if(MInvoice.DOCSTATUS_Drafted.equals(i.getDocStatus()) 
					|| MInvoice.DOCSTATUS_InProgress.equals(i.getDocStatus())) {
				log.saveError("DeleteError", "La factura relacionada al registro de importacion se encuentra en estado "
						+ i.getDocStatusName());
				return false;
			}
		}
			
		return true;
	}

}
