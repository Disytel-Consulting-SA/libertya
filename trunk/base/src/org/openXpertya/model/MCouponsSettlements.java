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
public class MCouponsSettlements extends X_C_CouponsSettlements {
	private static final long serialVersionUID = 1L;

	public MCouponsSettlements(Properties ctx, int C_CouponsSettlements_ID, String trxName) {
		super(ctx, C_CouponsSettlements_ID, trxName);
	}

	public MCouponsSettlements(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	public boolean doAfterSave(boolean newRecord, boolean success) {
		calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}

	public void calculateSettlementCouponsTotalAmount(String trxName) {
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
			ps = DB.prepareStatement(sql.toString(), trxName);
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();

			BigDecimal amt = BigDecimal.ZERO;

			while (rs.next()) {
				amt = amt.add(rs.getBigDecimal("amount"));
			}

			X_C_CreditCardSettlement settlement = new X_C_CreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), trxName);
			settlement.setCouponsTotalAmount(amt);
			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "doAfterSave", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
	}

}
