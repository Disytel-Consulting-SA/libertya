package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;

public class CalloutProduct extends CalloutEngine {

	public String productCategory( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(isCalloutActive())
			return "";
		setCalloutActive(true);
		
		if(value != null){
			Integer productCategoryID = (Integer)value;
			MProductCategory productCategory = MProductCategory.get(
					Env.getCtx(), productCategoryID, null);
			if(productCategory != null){
				mTab.setValue("AmortizationPerc", productCategory.getAmortizationPerc());
				mTab.setValue("YearLife", productCategory.getYearLife());
			}
		}
		
		setCalloutActive(false);
		
		return "";
	}	
	
	public String productType( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(isCalloutActive())
			return "";
		setCalloutActive(true);
		
		if(value != null){
			String strValue = (String)value;
			mTab.setValue("isSold", !strValue.equals(MProduct.PRODUCTTYPE_Assets));
		}
		
		setCalloutActive(false);
		
		return "";
	}
}
