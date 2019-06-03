package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MInflationIndex extends X_C_Inflation_Index {

	public MInflationIndex(Properties ctx, int C_Inflation_Index_ID, String trxName) {
		super(ctx, C_Inflation_Index_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MInflationIndex(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// No se puede tener más de un índice de inflación por período y compañía
		if (existRecordFor(getCtx(), Table_Name,
				"ad_client_id = ? and c_period_id = ?" + (newRecord ? "" : " and c_inflation_index_id <> " + getID()),
				new Object[] { getAD_Client_ID(), getC_Period_ID() }, get_TrxName())) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "SameInflationIndexPeriod"));
			return false;
		}
		return true;
	}
}
