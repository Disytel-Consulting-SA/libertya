package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MIssueResolution extends X_C_Issue_Resolution {

	
    public MIssueResolution( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MIssueResolution
    
    public MIssueResolution( Properties ctx,int C_Issue_Resolution_ID,String trxName ) {
        super( ctx,C_Issue_Resolution_ID,trxName );
    } // MIssueResolution
    
    protected boolean beforeSave( boolean newRecord ) {
    	
    	if (get_Value("Value") == null || get_Value("Value").equals("")) {
    		log.saveError("Rellene Campos Obligatorios: ", "Valor");
    		return false;
    	}

    	return true;
    } // beforeSave
}
