package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class MCintoloExchangeDifSettings extends X_C_Cintolo_Exchange_Dif_Settings{

	public MCintoloExchangeDifSettings(Properties ctx, int C_Cintolo_Exchange_Dif_Settings_ID, String trxName) {
		super(ctx, C_Cintolo_Exchange_Dif_Settings_ID, trxName);
	}

	public MCintoloExchangeDifSettings (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}
	
	private static final long serialVersionUID = 1L;

	// dREHER sep 24, no permitir mas de una configuracion de diferencia de cambio activa
	protected boolean beforeSave(boolean newRecord) {
	
		if(!isActive())
			return true;
		
		int tmp = DB.getSQLValue(get_TrxName(), "SELECT COUNT(*) FROM C_Cintolo_Exchange_Dif_Settings WHERE IsActive='Y' AND C_Cintolo_Exchange_Dif_Settings_ID<>?",
				getC_Cintolo_Exchange_Dif_Settings_ID());
		if((newRecord && tmp > 0) ||
				!newRecord && tmp >= 1) {
			log.saveError("Error", "Solo puede existir una UNICA configuracion de diferencias de cambio activa.");
			return false;
		}
		
		if(!Util.isEmpty(getUmbral_Ajuste_Aut(),true)) {
			if(Util.isEmpty(getC_BankAccount_Ajuste_ID(), true)){
				log.saveError("Error", "Si se configura un umbral de ajuste en cobros/pagos, se debera indicar la cuenta bancaria requerida.");
				return false;
			}
		}
		
		return true;
	}
	
}
