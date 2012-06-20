package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MSeverity extends X_C_Severity {

    public MSeverity( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MSeverity
    
    public MSeverity( Properties ctx,int C_Severity_ID,String trxName ) {
        super( ctx,C_Severity_ID,trxName );
    } // MSeverity
    
    protected boolean beforeSave( boolean newRecord ) {
    	
    	if (get_Value("Value") == null || get_Value("Value").equals("")) {
    		log.saveError("Rellene Campos Obligatorios: ", "Valor");
    		return false;
    	}

    	return true;
    } // beforeSave
}