package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.Env;

public class RecordFinder {

	private static RecordFinder instance = new RecordFinder(null);

	private String trxName;
	
	public RecordFinder(String trxName) {
		
	}
	
	public RecordFinder() {
		this(null);
	}
	
	public static RecordFinder getInstance() {
		return instance;
	}
	
	public ResultSet find(Properties ctx, String value, String tableName) throws DBException {
		String sql = getQuery(tableName, "value");
		CPreparedStatement pstm = DB.prepareStatement(sql, getTrxName());
		try {
			pstm.setString(1, value);
			pstm.setInt(2, Env.getAD_Client_ID(ctx));
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				return rs;
			}
			return null;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public ResultSet find(Properties ctx, String value, String tableName, String columnName) throws DBException {
		String sql = getQuery(tableName, columnName);
		CPreparedStatement pstm = DB.prepareStatement(sql, getTrxName());
		try {
			pstm.setString(1, value);
			pstm.setInt(2, Env.getAD_Client_ID(ctx));
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				return rs;
			}
			return null;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	
	public ResultSet find(Properties ctx, int value, String tableName, String columnName) throws DBException {
		String sql = getQuery(tableName, columnName);
		CPreparedStatement pstm = DB.prepareStatement(sql, getTrxName());
		try {
			pstm.setInt(1, value);
			pstm.setInt(2, Env.getAD_Client_ID(ctx));
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				return rs;
			}
			return null;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	public ResultSet find(Properties ctx, Map filter, String tableName) throws DBException {
		String sql = "SELECT * FROM " + tableName + " WHERE (AD_Client_ID = ? OR AD_Client_ID = 0) "; 
		
		for (Iterator i = filter.keySet().iterator(); i.hasNext();) {
			String columnName = " AND " + (String) i.next();
			sql += columnName + " = ? ";
		}
		CPreparedStatement pstm = DB.prepareStatement(sql, getTrxName());
		try {
			int n = 1;
			pstm.setInt(n, Env.getAD_Client_ID(ctx));
			for (Iterator i = filter.keySet().iterator(); i.hasNext();) {
				Object value = filter.get(i.next());
				if (value instanceof String) {
					pstm.setString(++n, (String) value);
				} else if (value instanceof Integer) {
					pstm.setInt(++n, (Integer) value);
				} else if (value instanceof BigDecimal) {
					pstm.setBigDecimal(++n, (BigDecimal) value);
				}
			}

			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				return rs;
			}
			return null;

		} catch (Exception e) {
			throw new DBException(e);
		}
	}


	private String getQuery(String tableName, String columnName) {
		return "SELECT * FROM " + tableName + " WHERE " + columnName + " = ? AND (AD_Client_ID = ? OR AD_Client_ID = 0) ORDER BY AD_Client_ID DESC";
		
	}

	private String getTrxName() {
		return trxName;
	}

	private void setTrxName(String trxName) {
		this.trxName = trxName;
	}
	
	
}
