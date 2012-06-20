package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;


public class CalloutIProductPrice extends CalloutEngine {
	

	/**
	 * Actualiza los valores   
	 */
	public String priceChange ( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value )
	{
		if (value == null)
			return "";
		
		if ("PriceLimit".equals(mField.getColumnName()) || "PriceList".equals(mField.getColumnName()) || "PriceStd".equals(mField.getColumnName()))
			setNewVariation(mTab, "Variation"+mField.getColumnName(), (BigDecimal)mTab.getValue("Previous"+mField.getColumnName()), ((BigDecimal)value) ) ;
	
		return "";
	}
	
	/**
	 * 	Setea el campo de variacion porcentual en funcion de los parametros de entrada 
	 */
	public void setNewVariation(MTab aTab, String variationField, BigDecimal previousPrice, BigDecimal currentPrice)
	{
		if (previousPrice == null || currentPrice == null) 
			return;
		
		if (BigDecimal.ZERO.compareTo(previousPrice) == 0)
			return;
		
		BigDecimal variation = currentPrice.divide(previousPrice, 2, BigDecimal.ROUND_HALF_EVEN);
		aTab.setValue(variationField, variation);
	}

}
