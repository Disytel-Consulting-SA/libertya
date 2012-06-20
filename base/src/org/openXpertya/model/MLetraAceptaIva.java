package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MLetraAceptaIva extends X_C_Letra_Acepta_Iva {

	public MLetraAceptaIva(Properties ctx, int C_Letra_Acepta_Iva_ID, String trxName)
	{
		super (ctx, C_Letra_Acepta_Iva_ID, trxName);
	}
	
	public MLetraAceptaIva(Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}
	
	protected boolean beforeSave(boolean newRecord) {
		
		int x = DB.getSQLValue(this.get_TrxName(), " SELECT COUNT(*) FROM C_Letra_Acepta_Iva WHERE AD_Client_ID = " + this.getAD_Client_ID() + 
				" AND CATEGORIA_VENDOR = " + this.getCategoria_Vendor() + 
				" AND CATEGORIA_CUSTOMER = " + this.getCategoria_Customer() + 
				" AND C_LETRA_COMPROBANTE_ID = ? ", this.getC_Letra_Comprobante_ID());
		if (x != 0) {
			log.saveError("SQLErrorNotUnique", "");
			return false;
		}
		
		return true;
	}
}
