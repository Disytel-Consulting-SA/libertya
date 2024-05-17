package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;
import java.util.regex.Pattern;

import org.openXpertya.util.Util;

public class MShipperVehicle extends X_M_Shipper_Vehicle{
	
	public MShipperVehicle(Properties ctx, int M_Shipper_Vehicle_ID, String trxName) {
		super(ctx, M_Shipper_Vehicle_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MShipperVehicle(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		if(!Util.isEmpty(getLicense())) {
			String regex1 = "^[A-Za-z]{3}\\d{3}$";
	        String regex2 = "^[A-Za-z]{2}\\d{2}[A-Za-z]{2}$";
	        
	        if(!(Pattern.matches(regex1, getLicense()) || Pattern.matches(regex2, getLicense()))) {
	        	log.saveError(null, "Formato de Patente inválido");
                return false;
	        }
	        
		}
		return true;
	} // beforeSave

}
