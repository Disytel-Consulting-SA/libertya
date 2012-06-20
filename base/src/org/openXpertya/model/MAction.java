package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MAction extends X_C_Action {
	
    public MAction( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlert
    
    public MAction( Properties ctx,int AD_Action_ID,String trxName ) {
        super( ctx,AD_Action_ID,trxName );
    }
    
    protected boolean beforeSave( boolean newRecord ) {
    	
    	//creamos un Business partner con el ID del mismo
    	MBPartner bp = new MBPartner(getCtx(),getC_BPartner_ID(),get_TrxName());
    	
    	//obtenemos el id del priceList
    	int M_PriceList_ID = bp.getM_PriceList_ID(); 
    	
    	if (M_PriceList_ID != 0)	{
    		setM_PriceList_ID(M_PriceList_ID);
    		return true;
    	}
   		log.info("No se ha podido grabar la tarifa: M_PriceList_ID=" + M_PriceList_ID);
   		return false;    	
    }

}
