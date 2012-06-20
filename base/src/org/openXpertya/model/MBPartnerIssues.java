package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MBPartnerIssues extends X_C_BPartner_Issues {


    public MBPartnerIssues( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MBPartnerIssues
    
    public MBPartnerIssues( Properties ctx,int C_BPartner_Issues_ID,String trxName ) {
    	super( ctx,C_BPartner_Issues_ID,trxName );
    } // MBPartnerIssues
    
    protected boolean beforeSave( boolean newRecord ) {
    	
    	if ((get_Value("C_Order_ID") == null || get_Value("C_Order_ID").equals("")) && (get_Value("C_Order_Supplier") == null || get_Value("C_Order_Supplier").equals(""))) {
    		log.saveError("Rellene Campos Obligatorios: ", "Debe especificar un Pedido o la Referencia del Proveedor");
    		return false;
    	}

    	return true;
    } // beforeSave
}