package org.openXpertya.model;

import java.util.Properties;

public class CalloutSurveyQuestion extends CalloutEngine {

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
	
    public String SetIsTitle( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el valor de IsTitle es Y se desmarcan las opciones de campo
    	if (mTab.getValue("IsTitle").toString().equals("true")){
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsListRef","N");
    		mTab.setValue("IsRadioButton","N");
    		mTab.setValue("IsText","N");
    		mTab.setValue("AD_Reference_ID","-1");
    	}
    	else{
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsListRef","N");
    		mTab.setValue("IsRadioButton","N");
    		mTab.setValue("IsText","Y");
    	}
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
	
    public String SetIsText( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {

    	// Si el valor de IsText es Y se desmarcan las otras opciones
    	if (mTab.getValue("IsText").toString().equals("true")){
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsListRef","N");
    		mTab.setValue("IsRadioButton","N");
    		mTab.setValue("AD_Reference_ID","-1");
    	}
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
	
    public String SetIsBoolean( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el valor de IsBoolean es Y se desmarcan las otras opciones
    	if (mTab.getValue("IsBoolean").toString().equals("true")){
    		mTab.setValue("IsText","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsListRef","N");
    		mTab.setValue("IsRadioButton","N");
    		mTab.setValue("AD_Reference_ID","-1");
    	}
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
	
    public String SetIsNumber( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el valor de IsText es Y se desmarcan las otras opciones
    	if (mTab.getValue("IsNumber").toString().equals("true")){
    		mTab.setValue("IsText","N");
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsListRef","N");
    		mTab.setValue("IsRadioButton","N");
    		mTab.setValue("AD_Reference_ID","-1");
    	}
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
	
    public String SetIsListRef( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el valor de IsText es Y se desmarcan las otras opciones
    	if (mTab.getValue("IsListRef").toString().equals("true")){
    		mTab.setValue("IsText","N");
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsRadioButton","N");
    	}
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
	
    public String SetIsRadioButton( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el valor de IsText es Y se desmarcan las otras opciones
    	if (mTab.getValue("IsRadioButton").toString().equals("true")){
    		mTab.setValue("IsText","N");
    		mTab.setValue("IsBoolean","N");
    		mTab.setValue("IsNumber","N");
    		mTab.setValue("IsListRef","N");
    	}
    	return "";
    }
}
