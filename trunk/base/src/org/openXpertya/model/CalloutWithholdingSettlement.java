package org.openXpertya.model;

import java.util.Properties;

public class CalloutWithholdingSettlement extends CalloutEngine {

	/**
	 * Al ingresar un esquema de retención, se autocompleta la Región o Zona, si la retención
	 * tiene una configurada
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String retencionSchema (Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		MRetencionSchema retSchema = null;
		if (value != null) {
			Integer C_RetencionSchema_ID = (Integer) value;
			retSchema = new MRetencionSchema(ctx, C_RetencionSchema_ID, null);
		}
		if (retSchema != null)
			mTab.setValue("C_Region_ID", retSchema.getC_Region_ID());
		else
			mTab.setValue("C_Region_ID", null);
		
		setCalloutActive(false);
		return "";
	}

}
