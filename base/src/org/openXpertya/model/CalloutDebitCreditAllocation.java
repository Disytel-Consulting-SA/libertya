package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class CalloutDebitCreditAllocation extends CalloutEngine {

	public String creditInvoice(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {
		//Si se selecciono una nueva Factura de Proveedor
		if (!Util.isEmpty((Integer) value, true)) {
			//Actualizo el monto.
			mTab.setValue("Amount", DB.getSQLValueBD(null,
					"select invoiceopen(?,0)", (Integer) value, true));
			mTab.setValue("C_Currency_ID", (MInvoice.get(ctx, (Integer) value, null).getC_Currency_ID()));
		} else
			mTab.setValue("Amount", BigDecimal.ZERO);
		return "";
	}

	public String debitInvoice(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {
		//Si se selecciono una nueva Factura de Proveedor
		if (!Util.isEmpty((Integer) value, true)) {
			//Actualizo el monto.
			mTab.setValue("PendingDebitAmount", DB.getSQLValueBD(null,
					"select invoiceopen(?,0)", (Integer) value, true));
		} else
			mTab.setValue("PendingDebitAmount", BigDecimal.ZERO);
		return "";
	}
	
	public String allocation(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {
		//Si se selecciono una nueva Factura de Proveedor
		if (!Util.isEmpty((Integer) value, true)) {
			//Actualizo el monto.
			mTab.setValue("Amount", DB.getSQLValueBD(null,
					"select POCRAvailable(?)", (Integer) value, true));
		} else
			mTab.setValue("Amount", BigDecimal.ZERO);
		return "";
	}
	
}
