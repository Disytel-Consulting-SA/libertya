package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

public class CalloutChange extends CalloutEngine {

	protected static CLogger s_log = CLogger.getCLogger( CalloutChange.class );
	
    /** Descripción de Campos */

    private boolean steps = false;
    
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

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	Integer M_Product_ID = null;
    	if(mField.getColumnName().equals("M_Product_ID")){
    		M_Product_ID  =(Integer)mTab.getValue("M_Product_ID") ;
    	}
    	if(mField.getColumnName().equals("M_Product_To_ID")){
    		M_Product_ID  =(Integer)mTab.getValue("M_Product_To_ID") ;
    	}
    	if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        log.fine("En CalloutChange.product con Propiertes ="+ ctx+ " MTab= "+ mTab+ " Mfield= "+ mField+ "Object= "+ value );
        

        if( steps ) {
            log.warning( "product - init" );
        }

        if (isCalloutActive())
        	return "";
        
        setCalloutActive( true );

        //
        
        if (mField.getColumnName().equals("M_Product_ID")) {
	        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
	            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
	        } else {
	            mTab.setValue( "M_AttributeSetInstance_ID",null );
	        }
        }
        
        if (mField.getColumnName().equals("M_Product_To_ID")) {
	        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
	            mTab.setValue( "M_AttributeSetInstanceTo_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
	        } else {
	            mTab.setValue( "M_AttributeSetInstanceTo_ID",null );
	        }
        }
        
        //
        
        Integer M_AttributeSetInstance_ID = (Integer)mTab.getValue("M_AttributeSetInstance_ID");
        Integer M_AttributeSetInstance_To_ID = (Integer)mTab.getValue("M_AttributeSetInstanceTo_ID");
        if (M_AttributeSetInstance_ID == null)
        	M_AttributeSetInstance_ID = new Integer(0);
        if (M_AttributeSetInstance_To_ID == null)
        	M_AttributeSetInstance_To_ID = new Integer(0);
  
        setCalloutActive( false );
        
        if( steps ) {
            log.warning( "product - fini" );
        }

        return "";

        
    }// product

}
