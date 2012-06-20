package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.X_C_Unavailability;

import org.openXpertya.util.DB;

public class CalloutUnavailability extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
    public String ConfigADTableID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	mTab.setValue("Record_ID", null);
    	
	  	return "";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab     * @param mField
     * @param value
     *
     * @return
     */
    public String ConfigCalendar( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Al cambiar el calendario se vacian los campos año y periodo
    	

    	mTab.setValue("C_Year_ID",null);
    	mTab.setValue("C_Period_ID",null);
    	
	  	return "";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
    public String ConfigYear( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Al cambiar el calendario se vacian el campo periodo
    	mTab.setValue("C_Period_ID",null);
    	
	  	return "";
    }
}
