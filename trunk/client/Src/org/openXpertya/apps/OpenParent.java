package org.openXpertya.apps;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.openXpertya.model.MTab;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.model.*;

public class OpenParent extends CalloutEngine {
	
	public String Open( Properties ctx,int WindowNo,MTab mTab ) {
		
		int DataTable_ID= Env.getContextAsInt( ctx,WindowNo,"AD_Table_ID" );
		int DataRecord_ID= Env.getContextAsInt( ctx,WindowNo,"Record_ID");
		// Recogemos el nombre de la tabla destino
		X_AD_Table tabla = new X_AD_Table( ctx,DataTable_ID,null);
		String TableName = tabla.getTableName();
		
		// Recogemos el nombre del campo
		//SELECT Name,isKey FROM AD_Column WHERE AD_Table_ID=540
		String sql = "SELECT ColumnName,isKey FROM AD_Column WHERE AD_Table_ID=?";
		String Name = "";

		
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,DataTable_ID );

            ResultSet rs = pstmt.executeQuery();

            while ( rs.next()) {
            	if (rs.getString( 2 ).equals("Y"))
            		Name = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
        	return "Error";
        }
		
		// Hacemos zoom
        MQuery m_query = new MQuery(TableName);
        if (DataRecord_ID!=0)
        	m_query.addRestriction(Name,MQuery.EQUAL,DataRecord_ID);
		
		if (!Name.equals("")){
			AEnv.zoom(m_query);
		}		

		return "Go parent";
	}
    	

}
