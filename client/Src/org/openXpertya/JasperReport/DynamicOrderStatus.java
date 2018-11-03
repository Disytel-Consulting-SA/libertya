package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.Util;

public class DynamicOrderStatus extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal invoiceID = (BigDecimal) params.get("C_Invoice_ID");
		BigDecimal orderID = (BigDecimal) params.get("C_Order_ID");
		if(Util.isEmpty(orderID, true) && !Util.isEmpty(invoiceID, true)){
			MInvoice invoice = new MInvoice(ctx, invoiceID.intValue(), null);
			params.put("C_Order_ID", new BigDecimal(invoice.getC_Order_ID()));
		}
	}

}
