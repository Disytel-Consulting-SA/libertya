package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MOrgPercepcion extends X_AD_Org_Percepcion {

	public MOrgPercepcion(Properties ctx, int AD_Org_Percepcion_ID,
			String trxName) {
		super(ctx, AD_Org_Percepcion_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MOrgPercepcion(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// No puede existir un registro repetido por organización e impuesto
		StringBuffer sql = new StringBuffer("SELECT count(*) as cant FROM "
				+ get_TableName() + " WHERE ad_org_id = " + getAD_Org_ID()
				+ " AND c_tax_id = " + getC_Tax_ID());
		if(!Util.isEmpty(getID(), true)){
			sql.append(" AND ("+get_TableName()+"_ID <> "+getID()+")");
		}
		if(DB.getSQLValue(get_TrxName(), sql.toString()) > 0){
			log.saveError("OrgPercepcionRepeated", "");
			return false;
		}
		return true;
	}	
}
