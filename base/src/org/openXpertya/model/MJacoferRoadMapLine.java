package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.CLogger;

public class MJacoferRoadMapLine extends LP_M_Jacofer_RoadMapLine  {

	/** Flag para actualizar la cabecera */
	private boolean updateHeader = true;
	
	public MJacoferRoadMapLine(Properties ctx, int M_Jacofer_RoadMapLine_ID, String trxName) {
		super(ctx, M_Jacofer_RoadMapLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MJacoferRoadMapLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		success = success && doHeaderUpdate();
		return success;
	}
	
	@Override
	protected boolean afterDelete(boolean success) {
		success = success && doHeaderUpdate();
		return success;
	}

	/**
	 * Realiza la actualización de la cabecera
	 * 
	 * @return true si el proceso finalizó correctamente, false caso contrario
	 */
	protected boolean doHeaderUpdate() {
		boolean fine = true;
		if(isUpdateHeader()) {
			MJacoferRoadMap rm = new MJacoferRoadMap(getCtx(), getM_Jacofer_RoadMap_ID(), get_TrxName());
			rm.updateTotals();
			if(!rm.save()) {
				log.saveError("SaveError", CLogger.retrieveErrorAsString());
				fine = false;
			}
		}
		return fine;
	}
	
	public boolean isUpdateHeader() {
		return updateHeader;
	}

	public void setUpdateHeader(boolean updateHeader) {
		this.updateHeader = updateHeader;
	}

	
	
}
