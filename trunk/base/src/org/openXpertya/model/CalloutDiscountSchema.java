package org.openXpertya.model;

import java.util.Properties;

public class CalloutDiscountSchema extends CalloutEngine {

	public String scope(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		
		boolean isGeneralScope = (Boolean)mTab.getValue("IsGeneralScope");
		boolean isBPartnerScope = (Boolean)mTab.getValue("IsBPartnerScope");
		
		// Actualmente o es General o es EC. Los checkbox funcionan como un radio button.
		// Si se agregan nuevas opciones se deberá revisar esta lógica y ver que checkbox
		// tiene precedencia. Normalmente si un Esquema es General no debería tener otro ámbito,
		// pero tal vez otros ámbitos si sean configurables mutuamente.
		if (mField.getColumnName().equals("IsGeneralScope")) {
			isBPartnerScope = !isGeneralScope;
		} else if (mField.getColumnName().equals("IsBPartnerScope")) {
			isGeneralScope = !isBPartnerScope;
		}
		
		mTab.setValue("IsGeneralScope", isGeneralScope);
		mTab.setValue("IsBPartnerScope", isBPartnerScope);
		
		// Si es de tipo Financiero se debe colocar como nivel de acumulativo a
		// Documento 
		if (((String) mTab.getValue("DiscountContextType")) != null
				&& ((String) mTab.getValue("DiscountContextType"))
						.equals(MDiscountSchema.DISCOUNTCONTEXTTYPE_Financial)) {
			mTab.setValue("CumulativeLevel",
					MDiscountSchema.CUMULATIVELEVEL_Document);
		}
		
		setCalloutActive(false);
		return "";
	}
}
