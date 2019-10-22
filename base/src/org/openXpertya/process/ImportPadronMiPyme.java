package org.openXpertya.process;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ImportPadronMiPyme extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		// Desmarcar las EC de emisión mi pyme que se encuentran en la tabla del padrón
		markBPartners(false);
		// Eliminar los registros de la tabla del padrón
		deletePadron();
		// Importar el padrón
		int r = importPadron();
		// Marcar las EC de emisión mi pyme que se encuentran en la tabla del padrón
		int bp = markBPartners(true);
		// Mensaje final
		return "@ImportedRecords@: "+r+". @BPartnersFound@: "+bp;
	}

	/**
	 * Actualizar la marca Emitir Mi Pyme de las entidades comerciales que se
	 * encuentran en la tabla del padrón
	 * 
	 * @param setMiPyme true si se debe marcar, false caso contrario
	 * @return cantidad de registros actualizados
	 */
	private int markBPartners(boolean setMiPyme) {
		String sql = "UPDATE c_bpartner SET emitir_mi_pyme = '" + (setMiPyme ? "Y" : "N") + "' WHERE iscustomer = 'Y' AND ad_client_id = "
				+ Env.getAD_Client_ID(getCtx())
				+ " and taxid IN (select cuit from i_padron_mipyme where i_isimported = 'Y' and ad_client_id = "
				+ Env.getAD_Client_ID(getCtx()) + ") ";
		return DB.executeUpdate(sql, get_TrxName());
	}
	
	/**
	 * @return cantidad de registros eliminados del padrón
	 */
	private int deletePadron(){
		return DB.executeUpdate("DELETE FROM i_padron_mipyme WHERE i_isimported = 'Y' and ad_client_id = "
				+ Env.getAD_Client_ID(getCtx()), get_TrxName());
	}
	
	/**
	 * @return cantidad de registros importados del padrón
	 */
	private int importPadron() {
		return DB.executeUpdate(
				"UPDATE i_padron_mipyme SET i_isimported = 'Y', processed = 'Y' WHERE ad_client_id = " + Env.getAD_Client_ID(getCtx()),
				get_TrxName());
	}
}
