package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CalloutChange extends CalloutEngine {

	protected static CLogger s_log = CLogger.getCLogger( CalloutChange.class );
	
    /** Descripción de Campos */

    private boolean steps = false;
    
    private void setCost(Properties ctx, MTab mTab, String productColumnName, String costColumnName){
		Integer productID = (Integer)mTab.getValue(productColumnName);
		BigDecimal cost = BigDecimal.ZERO;
		BigDecimal oldCost = (BigDecimal)mTab.getValue(costColumnName);
		if(!Util.isEmpty(productID, true)){
			cost = MProductPricing.getCostPrice(ctx, Env.getAD_Org_ID(ctx),
					productID,
					MProductPO.getFirstVendorID(productID, null),
					Env.getContextAsInt(ctx, "$C_Currency_ID"), Env.getDate(),
					false, false, null, false, null);
		}
		// Se actualiza si es distinto
		if(oldCost == null || cost.compareTo(oldCost) != 0){
			mTab.setValue(costColumnName, cost);
		}
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

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	Integer M_Product_ID = null;
    	if(mField.getColumnName().equals("M_Product_ID")){
    		M_Product_ID  =(Integer)mTab.getValue("M_Product_ID") ;
    		setCost(ctx, mTab, "M_Product_ID", "Cost");
    	}
    	if(mField.getColumnName().equals("M_Product_To_ID")){
    		M_Product_ID  =(Integer)mTab.getValue("M_Product_To_ID") ;
    		setCost(ctx, mTab, "M_Product_To_ID", "CostTo");
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
