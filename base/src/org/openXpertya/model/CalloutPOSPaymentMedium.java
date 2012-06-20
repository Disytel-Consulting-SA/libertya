package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Callouts de Medios de Pago de TPV
 */
public class CalloutPOSPaymentMedium extends CalloutEngine {

	
	/**
	 * Callout de Tipo de Pago
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String tenderType(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		String tenderType = (String)value;
		// Los tipos de pago que no sean efectivo deben tener como moneda la moneda
		// por defecto de la Compañía (la interfaz se encarga de hacer el campo de solo
		// lectura)
		if (!MPOSPaymentMedium.TENDERTYPE_Cash.equals(tenderType)) {
			int clientCurrencyID = Env.getContextAsInt(ctx, "$C_Currency_ID");
			mTab.setValue("C_Currency_ID", clientCurrencyID);
		}
		
		// Los tipos de pago Retención y Cobro Anticipado son sólo para Recibos de
		// Cliente por ahora. Modificar esta porción de código cuando esté
		// implementado en el TPV cualquiera de los tipos de pago indicados
		if (tenderType.equals(MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt)
				|| tenderType.equals(MPOSPaymentMedium.TENDERTYPE_Retencion)) {
			mTab.setValue("Context", MPOSPaymentMedium.CONTEXT_CustomerReceiptsOnly);
		}
		
		// El tipo de pago Crédito es sólo para TPV
		if (tenderType.equals(MPOSPaymentMedium.TENDERTYPE_Credit)) {
			mTab.setValue("Context", MPOSPaymentMedium.CONTEXT_POSOnly);
		}
		
		return "";
	}
}
