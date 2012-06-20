package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_M_Replenish;

public class MReplenish extends X_M_Replenish {

	private static final long serialVersionUID = 1L;

	public MReplenish(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MReplenish(Properties ctx, int M_Replenish_ID, String trxName) {
		super(ctx, M_Replenish_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		// Al seleccionar tipo de reposición automática debe indicarse el sistema de reposición
		if ((getReplenishType()!=null) && (getReplenishType().equalsIgnoreCase("5")) && (getM_ReplenishSystem_ID() == 0))
		{
			log.saveError("ReplenishSystemMissing", "");
			return false;
		}
		return true;
	}
	
	
}
