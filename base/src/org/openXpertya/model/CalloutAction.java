package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class CalloutAction  extends CalloutEngine {

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

    public String sales( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	if( value == null ) {
            return "";
        }
    	
    	Integer C_Action_Type_ID = ( Integer ) mTab.getValue("C_Action_Type_ID");
    	
    	X_C_Action_Type at = new X_C_Action_Type(ctx, C_Action_Type_ID.intValue(), null);
    	
    	if (at.isSales())	{
    		mTab.setValue("IsSales","Y");
    	}
    	else {
    		mTab.setValue("IsSales","N");
    	}
    	return "";
    }
    

public String bPartner( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		
		Integer C_BPartner_ID = ( Integer )value;
		
	  	if( (C_BPartner_ID == null) || (C_BPartner_ID.intValue() == 0) ) {
            return "";
        }
	  	
	  	//setCalloutActive( true );
	  	
		String SQL = "SELECT c.AD_User_ID " + "FROM C_BPartner p" + " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) " + "WHERE p.C_BPartner_ID=?";    // #1
		//boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));
		
		try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            pstmt.setInt( 1,C_BPartner_ID.intValue());
            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
            	//Contact

                int contID = rs.getInt( "AD_User_ID" );

                if( C_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) 
                {
                    
                	String cont = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"AD_User_ID" );

                    if( cont.length() > 0 ) {
                        contID = Integer.parseInt( cont );
                    }
                }

                if( contID == 0 ) {
                    mTab.setValue( "AD_User_ID",null );
                } else {
                    mTab.setValue( "AD_User_ID",new Integer( contID ));
                }
            	
            }
    
            rs.close();
            pstmt.close();
            
		} catch( SQLException e ) {
            log.log( Level.SEVERE,"Error en CalloutAction l:60",e );
            return e.getLocalizedMessage();
        }
		
	  	return "";
	}
 
}
