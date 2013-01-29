package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.PO;

public class MCentralConfiguration extends X_C_CentralConfiguration {

	// Métodos de clase

	/**
	 * Obtengo la configuración para la compañía parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param AD_Client_ID
	 *            id de compañía
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return configuración para la compañía actual, null si no existe ninguna
	 */
	public static MCentralConfiguration get(Properties ctx, Integer AD_Client_ID, String trxName){
		return (MCentralConfiguration) PO.findFirst(ctx, Table_Name,
				"ad_client_id = ?", new Object[] { AD_Client_ID }, null,
				trxName);
	}	
	
	// Métodos de instancia
	
	public MCentralConfiguration(Properties ctx,
			int C_CentralConfiguration_ID, String trxName) {
		super(ctx, C_CentralConfiguration_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCentralConfiguration(Properties ctx, ResultSet rs,
			String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Validación para no permitir dos configuraciones diferentes para la
		// misma compañía y sea distinto del registro actual en el caso que no
		// sea un registro nuevo
		if (existRecordFor(
				getCtx(),
				get_TableName(),
				newRecord ? "ad_client_id = ?"
						: "ad_client_id = ? AND C_CentralConfiguration_ID <> ?",
				newRecord ? new Object[] { getAD_Client_ID() } : new Object[] {
						getAD_Client_ID(), getID() }, get_TrxName())) {
			log.saveError("ExistCCACConfForClient", "");
			return false;
		}
		return true;
	}
}
