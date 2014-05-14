package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MExpFormat extends X_AD_ExpFormat {

	public MExpFormat(Properties ctx, int AD_ExpFormat_ID, String trxName) {
		super(ctx, AD_ExpFormat_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MExpFormat(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return Las columnas activas del formato de exportación ordenados por nro
	 *         de secuencia
	 */
	public List<MExpFormatRow> getRows() {
		List<PO> pos = PO.find(getCtx(), X_AD_ExpFormat_Row.Table_Name,
				"isactive = 'Y' and ad_expformat_id = ?", new Object[]{getID()}, 
				new String[]{"seqno"}, get_TrxName());
		List<MExpFormatRow> rows = new ArrayList<MExpFormatRow>();
		for (PO po : pos) {
			rows.add((MExpFormatRow)po);
		}
		return rows;
	}
	
	/**
	 * @return Las columnas activas del formato de exportación identificados
	 *         como criterio de ordenamiento, ordenados por nro de secuencia de
	 *         orden
	 */
	public List<MExpFormatRow> getOrderRows() {
		List<PO> pos = PO.find(getCtx(), X_AD_ExpFormat_Row.Table_Name,
				"isactive = 'Y' and isorderfield = 'Y' and ad_expformat_id = ?", 
				new Object[]{getID()}, new String[]{"orderseqno"}, get_TrxName());
		List<MExpFormatRow> rows = new ArrayList<MExpFormatRow>();
		for (PO po : pos) {
			rows.add((MExpFormatRow)po);
		}
		return rows;
	}
}
