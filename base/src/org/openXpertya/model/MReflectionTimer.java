package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MReflectionTimer extends X_AD_ReflectionTimer {

	public MReflectionTimer(Properties ctx, int AD_ReflectionTimer_ID,
			String trxName) {
		super(ctx, AD_ReflectionTimer_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MReflectionTimer(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beforeSave(boolean newRecord){
		// Si no hay value, error
		if(Util.isEmpty(getValue())){
			log.saveError("FillMandatory", Msg.getMsg(getCtx(), "Value"));
			return false;
		}
		// No permitir claves duplicadas para la compañía
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM ad_reflectiontimer WHERE (ad_client_id = ?) AND (upper(trim(value)) = upper(trim(?))) AND (isactive = 'Y')");
		if(!newRecord){
			sql.append(" AND (ad_reflectiontimer_id <> "+getAD_ReflectionTimer_ID()+") ");
		}
		// Verifico la cantidad de registros con igual value, si hay al menos
		// una con el mismo value error
		int noSameValue = DB.getSQLValue(get_TrxName(), sql.toString(),
				getAD_Client_ID(), getValue());
		if(noSameValue > 0){
			log.saveError("",Msg.getMsg(getCtx(), "DuplicatedFieldValue", new Object[]{Table_Name,getValue(),Msg.getMsg(getCtx(), "Value")}));
			return false;
		}
		return true;
	}
}
