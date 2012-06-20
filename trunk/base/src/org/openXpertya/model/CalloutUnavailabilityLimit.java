package org.openXpertya.model;


import java.util.Properties;

/**
 * Descripción de Método:
 * JRBV - Dataware - 
 * Manejo de los datos en UnavaibilityLimit y UnavaibilityType
 */
public class CalloutUnavailabilityLimit extends CalloutEngine {
	

	/**
	 * Descripción de Método:
	 * JRBV - Dataware - 
     *
     * Insertamos AD_Table_ID en el contexto
     * @return
     */
	
	public String setAD_Table_ID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {

		//ctx.setProperty("Table_ID", Integer.toString(AD_Table_ID));		
        
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
	
    public String ConfigADTableID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {



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
