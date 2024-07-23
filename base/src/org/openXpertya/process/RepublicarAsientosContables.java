package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.model.X_C_BankStatement;
import org.openXpertya.model.X_M_Inventory;
import org.openXpertya.model.X_M_Movement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class RepublicarAsientosContables extends SvrProcess {
	
	private Date fechaD;
	private Date fechaH;
	private Integer AD_Table_ID;
	private String notPosted;
	private Integer acct_acount;
	private String custom_join;
	private String custom_where;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] parameters = getParameter();
		
		for (int i = 0; i < parameters.length; i++) {
			
			String name = parameters[i].getParameterName();
			Object value = parameters[i].getParameter();
			
			if ("fechaD".equalsIgnoreCase(name))
				fechaD = (Date)value;
			
			if ("fechaH".equalsIgnoreCase(name))
				fechaH = (Date)value;
			
			if ("notPosted".equalsIgnoreCase(name))
				notPosted = value.toString();
			
			if ("AD_Table_ID".equalsIgnoreCase(name))
				AD_Table_ID = ((BigDecimal)value).intValue();
			
			if ("acct_acount".equalsIgnoreCase(name))
				acct_acount = ((BigDecimal)value).intValue();
			
			if ("custom_join".equalsIgnoreCase(name))
				custom_join = value.toString();
			
			if ("custom_where".equalsIgnoreCase(name))
				custom_where = value.toString();
			
		}
	}
	
	@Override
	protected String doIt() throws Exception {
		Integer countOk = 0;
		Integer countFail = 0;
		String tablename = DB.getSQLValueString(get_TrxName(), "SELECT tablename FROM ad_table WHERE ad_table_id = ?", AD_Table_ID);
		
		Server server = CConnection.get().getServer();
		if( server == null ) {
        	throw new Exception("No se encontro servidor de aplicaciones");
        }
		
		for (Integer Record_ID : getRecords(tablename)) {
			Boolean result = new Boolean( server.postImmediate( Env.getCtx(),Env.getAD_Client_ID(getCtx()),AD_Table_ID,Record_ID,true));
            if (result) 
            	countOk++; 
            else 
            	countFail++;
		}
		
		return "Se republicaron " + countOk + " registros exitosamente y " + countFail + " registros fallaron.";
	}
	
	private List<Integer> getRecords(String tablename) {
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat dateFormatTo = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		
		String sql = "SELECT " + tablename + "_id FROM " + tablename + " t ";
		
		if (acct_acount != null) {
			int table_id = DB.getSQLValue(get_TrxName(), "SELECT ad_table_id FROM ad_table WHERE tablename = ?", tablename);
			sql += " JOIN fact_acct f ON f.ad_table_id = " + table_id + " AND f.record_id = t." + tablename + "_id AND f.ad_client_id = " + Env.getAD_Client_ID(getCtx());
		}
		
		if (custom_join != null && !custom_join.isEmpty())
			sql += " " + custom_join + " ";
		
		sql += " WHERE t.processed = 'Y' AND t.ad_client_id = " + Env.getAD_Client_ID(getCtx());
		
		if (fechaD != null || fechaH != null) {
			if (fechaD != null) sql += " AND t." + getDateField() + " >= '" + dateFormatFrom.format(fechaD) + "' ";
			if (fechaH != null) sql += " AND t." + getDateField() + " <= '" + dateFormatTo.format(fechaH) + "' ";
		}
		
		if (notPosted != null && notPosted.equals("Y")) {
			sql += " AND t.posted != 'Y' ";
		}
		
		if (acct_acount != null) {
			sql += " AND f.account_id = " + acct_acount;
		}
		
		if (custom_where != null && !custom_where.isEmpty())
			sql += " " + custom_where + " ";

		List<Integer> results = new ArrayList<Integer>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// Preparo la Query
			pstmt = DB.prepareStatement(sql);

			// Ejecuto la consulta
			rs = pstmt.executeQuery();

			// Recorro los resultados.
			while (rs.next()) {
				results.add(rs.getInt(1));
			}
			// Libera el ResultSet inmediatamente en lugar de esperar a que lo cierre automaticamente.
			rs.close();
			// Libera el PreparedStatement inmediatamente en lugar de esperar a que lo cierre automaticamente.
			pstmt.close();
			pstmt = null;

		} catch (SQLException e) {
			log.log(Level.SEVERE, sql, e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch(Exception e) { /* :O */ }
			pstmt = null;
		}
		// Retorno el resultado.
		return results;
	}
	
	private String getDateField() {
		if (AD_Table_ID == X_C_BankStatement.Table_ID) return "statementdate";
		if (AD_Table_ID == X_M_Inventory.Table_ID || AD_Table_ID == X_M_Movement.Table_ID) return "movementdate";
		return "dateacct";
	}

}
