package org.openXpertya.apps;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;
//import org.openXpertya.apps.*;

public class UnavailabilityGoParent extends SvrProcess {
	
	/** Descripcion de Campos */
    private int p_D_File_ID = 0;
    
	protected void prepare() {
		// D_File_ID
		p_D_File_ID = getRecord_ID();
	}

	protected String doIt() throws Exception {
		
		// Recogemos Table_ID y Record_ID 
		X_C_Unavailability file = new X_C_Unavailability( getCtx(),p_D_File_ID,get_TrxName());
		int Table_ID_Ref = file.getAD_Table_ID();
		int Record_ID_Ref = file.getRecord_ID();
		
		if (Table_ID_Ref != 0 && Record_ID_Ref != 0){
			
			// Recogemos el nombre de la tabla destino
			X_AD_Table tabla = new X_AD_Table( getCtx(),Table_ID_Ref,get_TrxName());
			String TableName = tabla.getTableName();
			
			// Recogemos el nombre del campo
			String sql = "SELECT ColumnName,isKey FROM AD_Column WHERE AD_Table_ID=?";
			String Name = "";
				
	        try {
	            PreparedStatement pstmt = DB.prepareStatement( sql );
	
	            pstmt.setInt( 1,Table_ID_Ref );
	
	            ResultSet rs = pstmt.executeQuery();
	
	            while ( rs.next()) {
	            	if (rs.getString( 2 ).equals("Y"))
	            		Name = rs.getString( 1 );
	            }
	
	            rs.close();
	            pstmt.close();
	        } catch( SQLException e ) {
	            log.log( Level.SEVERE,sql,e );
	        }
			
			// Hacemos zoom
	        MQuery m_query = new MQuery(TableName);
			m_query.addRestriction(Name,MQuery.EQUAL,Record_ID_Ref);
			
			if (!Name.equals("")){
				AEnv.zoom(m_query);
			}
		}
 
		return null;
	}
}
