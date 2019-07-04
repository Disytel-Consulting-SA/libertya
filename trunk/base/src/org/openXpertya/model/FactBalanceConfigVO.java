package org.openXpertya.model;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class FactBalanceConfigVO {

	public Properties ctx = null;
	public int clientID = 0;
	public int acctSchemaID = 0;
	public int orgID = 0;
	public int elementValueID = 0;
	public Timestamp dateFrom = null;
	public Timestamp dateTo = null;
	public String trxName = null;
	public boolean deleteFirst = false;
	
	public FactBalanceConfigVO() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return condición para el WHERE de Fact_Acct en base a los datos del VO,
	 *         con WHERE inicial
	 */
	public String getWhereClause(){
		String sql = " WHERE C_AcctSchema_ID = "+acctSchemaID;
		if(dateFrom != null){
			sql += " AND dateacct::Date >= '" + Env.getDateFormatted(dateFrom) + "'::date ";
		}
		if(dateTo != null){
			sql += " AND dateacct::Date <= '"+Env.getDateFormatted(dateTo)+"'::date ";
		}
		if(!Util.isEmpty(orgID, true)){
			sql += " AND ad_org_id = "+orgID;
		}
		if(!Util.isEmpty(elementValueID, true)){
			sql += " AND account_id = "+elementValueID;
		}
		return sql;
	}
}
