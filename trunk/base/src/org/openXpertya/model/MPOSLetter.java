package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MPOSLetter extends X_C_POSLetter {

	/**
	 * @param ctx
	 * @param posID
	 *            id de config de tpv
	 * @param trxName
	 * @return los puntos de venta personalizados por letra para la config de
	 *         tpv parámetro
	 */
	public static Map<String, Integer> getPOSLetters(Integer posID, String trxName){
		String sql = "SELECT letter, posnumber FROM " + Table_Name
				+ " WHERE c_pos_id = ? AND isactive = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Integer> letters = new HashMap<String, Integer>();
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, posID);
			rs = ps.executeQuery();
			while(rs.next()){
				letters.put(rs.getString("letter"), rs.getInt("posnumber"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return letters;
	}
	
	public MPOSLetter(Properties ctx, int C_POSLetter_ID, String trxName) {
		super(ctx, C_POSLetter_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MPOSLetter(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	protected boolean beforeSave( boolean newRecord ) {
		// No se pueden tener mismas letras para distintos puntos de venta
		if (PO.existRecordFor(
				getCtx(),
				get_TableName(),
				"letter = ? AND c_pos_id = ?"
						+ (newRecord ? "" : " AND c_posletter_id <> ?"),
				(newRecord ? new Object[] { getLetter(), getC_POS_ID() }
						: new Object[] { getLetter(), getC_POS_ID(), getID() }),
				get_TrxName())) {
			log.saveError("ExistLetterConfiguredForPOS", "");
			return false;
		}
		return true;
	}
	
}
