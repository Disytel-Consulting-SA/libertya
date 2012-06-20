package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
/**
 * Descripción de Clase:
 * Before safe: check if Unavailability Limit exist for the specified 
 * Unavailability Type Row. If Limit exist do not save, else save.
 *
 * @version 2.2, 25.07.06
 * @author     JRBV - Dataware Sistemas S.L. - 
 */
public class MUnavailabilityType extends X_C_Unavailability_Type {
    public MUnavailabilityType( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MUnavailabilityType
    
    public MUnavailabilityType( Properties ctx,int C_Unavailability_ID,String trxName ) {
    	super( ctx,C_Unavailability_ID,trxName );
    } // MUnavailabilityType
    
    /**
     * beforeSave
     * 
     * 
     */
    
    protected boolean beforeSave( boolean newRecord ) {
    	
    	boolean icansave=true;
    	
    	
    	return icansave;

    }
}
