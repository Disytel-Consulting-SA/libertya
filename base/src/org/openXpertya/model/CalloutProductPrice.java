package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CalloutProductPrice extends CalloutEngine {

	public static final String PORCENTAJE_AUMENTO_PRECIO_PREFERENCE_NAME = "PorcentajeDeAumentoDePrecio";
	
	public String price( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(value != null){
			if(isCalloutActive())
				return "";
			setCalloutActive(true);
			boolean clear = true;
			// Verificar que el aumento del precio de costo respecto al anterior
			// no supere el porcentaje configurado en la preference
			MPriceListVersion priceListVersion = new MPriceListVersion(ctx,
					(Integer) mTab.getValue("M_PriceList_Version_ID"), null);
			MPriceList priceList = MPriceList.get(ctx,
					priceListVersion.getM_PriceList_ID(), null);
			if(!priceList.isSOPriceList()){
				MProductPrice productPrice = MProductPrice.get(ctx, priceListVersion.getID(),
						(Integer) mTab.getValue("M_Product_ID"), null);
				if(productPrice != null){
					BigDecimal oldPrice = (BigDecimal)productPrice.get_Value(mField.getColumnName());
					BigDecimal newPrice = (BigDecimal)value;
					String preferenceValueStr = MPreference
							.searchCustomPreferenceValue(
									PORCENTAJE_AUMENTO_PRECIO_PREFERENCE_NAME,
									Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(ctx),
									Env.getAD_User_ID(ctx), true);
					if(!Util.isEmpty(preferenceValueStr, true)){
						BigDecimal preferencePercentVariation = new BigDecimal(preferenceValueStr);
						BigDecimal diff = newPrice.subtract(oldPrice);
						// La diferencia debe ser positiva ya que se debe validar el
						// aumento del precio, no la rebaja
						if(diff.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal realPercentVariation = oldPrice
									.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
									: diff.divide(oldPrice, 2,
											BigDecimal.ROUND_HALF_EVEN).multiply(
											Env.ONEHUNDRED); 
							// Si la variación supera a la de la preference, entonces error
							if(realPercentVariation.compareTo(preferencePercentVariation) > 0){
								mTab.setCurrentRecordWarning(Msg.getMsg(ctx,
										"ProductPriceRaiseExceedsPercentLimit"));
								clear = false;
							}
						}
					}
				}
			}
			if(clear){
				mTab.clearCurrentRecordWarning();
			}
			setCalloutActive(false);
		}
		
		return "";
	}

}
