package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class CalloutBankTransfer extends CalloutEngine {

	
	public String currency(Properties ctx,int WindowNo,MTab mTab,MField mField,Object value){
		if(value == null){
			return "";
		}
		
		if(isCalloutActive()){
			return "";
		}
		
		setCalloutActive(true);
		
        // Determinar la moneda de la cuenta 
        String sql = "SELECT c_currency_id from c_bankaccount where c_bankaccount_id = ?";
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        int currency = 0;
        
        try{
        	ps = DB.prepareStatement(sql);
        	ps.setInt(1, (Integer)value);
        	        	
        	rs = ps.executeQuery();
        	if(rs.next()){
        		currency = rs.getInt("c_currency_id");
        	}
        	
        }catch(Exception e){
        	return "No se pudo determinar la moneda de la cuenta bancaria";
        }finally{
        	try{
        		ps.close();
        		rs.close();
        	}catch(Exception e){
        		return "No se pudo determinar la moneda de la cuenta bancaria";
        	}
        }
        
        
        String field;
        // Si es la cuenta origen seteo la moneda origen, sino la moneda destino
        if(mField.getColumnName().equalsIgnoreCase("C_bankaccount_from_ID")){
        	field = "C_currency_from_ID";
        }
        else{
        	field = "C_currency_to_ID";
        }
        
        
        mTab.setValue(field, currency);        
                       
        setCalloutActive(false);       
        
        return "";
	}
}
