package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MPerceptionsSettlement extends X_C_PerceptionsSettlement {
	private static final long serialVersionUID = 1L;

	/**
	 * Obtiene la percepción para el impuesto y liquidación parámetro.
	 * 
	 * @param ctx
	 * @param creditCardSettlementID
	 * @param taxID
	 * @param trxName
	 * @return la percepción para el impuesto y liquidación parámetro, null si
	 *         no existe
	 * @throws Exception
	 */
	public static MPerceptionsSettlement get(Properties ctx, Integer creditCardSettlementID, Integer taxID, String trxName) throws Exception{
		MPerceptionsSettlement perse = null;
		String sql = "SELECT * FROM "+Table_Name+" WHERE "+MCreditCardSettlement.Table_Name+"_ID = ? AND c_tax_id = ? AND isactive = 'Y'";
		PreparedStatement ps = DB.prepareStatement(sql, trxName);
		ps.setInt(1, creditCardSettlementID);
		ps.setInt(2, taxID);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			perse = new MPerceptionsSettlement(ctx, rs, trxName);
		}
		rs.close();
		ps.close();
		return perse;
	}
	
	public MPerceptionsSettlement(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MPerceptionsSettlement(Properties ctx, int C_PerceptionsSettlement_ID, String trxName) {
		super(ctx, C_PerceptionsSettlement_ID, trxName);
	}

	@Override
	public boolean doAfterSave(boolean newRecord, boolean success) {
		return recalculate();
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return recalculate();
	}

	private boolean recalculate() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	amount ");
		sql.append("FROM ");
		sql.append("	" + Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();

			BigDecimal amt = BigDecimal.ZERO;

			while (rs.next()) {
				amt = amt.add(rs.getBigDecimal("amount"));
			}

			MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
			settlement.setPerception(amt);
			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "recalculate", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return true;
	}

}
