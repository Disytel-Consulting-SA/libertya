package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Callout para Cupones en liquidación.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CalloutCouponsSettlement extends CalloutEngine {

	/**
	 * Al cargar un pago, se deben autocompletar los datos del cupon.
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String payment(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		if (value != null) {
			Integer C_Payment_ID = (Integer) value;

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	ah.DocumentNo, ");
			sql.append("	p.Payamt, ");
			sql.append("	p.C_Currency_ID, ");
			sql.append("	p.CouponNumber, ");
			sql.append("	p.CreditCardNumber, ");
			sql.append("	efp.M_EntidadFinanciera_ID, ");
			sql.append("	p.M_EntidadFinancieraPlan_ID, ");
			sql.append("	p.CouponBatchNumber, ");
			sql.append("	p.DateTrx ");
			sql.append("FROM ");
			sql.append("	" + X_C_Payment.Table_Name + " p ");
			sql.append("	LEFT JOIN " + X_M_EntidadFinancieraPlan.Table_Name + " efp ");
			sql.append("		ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id ");
			sql.append("	LEFT JOIN " + X_C_AllocationLine.Table_Name + " al ");
			sql.append("		ON p.c_payment_id = al.c_payment_id ");
			sql.append("	LEFT JOIN " + X_C_AllocationHdr.Table_Name + " ah ");
			sql.append("		ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
			sql.append("WHERE ");
			sql.append("	p.C_Payment_ID = ? ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				pstmt = DB.prepareStatement(sql.toString());
				pstmt.setInt(1, C_Payment_ID);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					mTab.setValue("AllocationNumber", rs.getString(1));
					mTab.setValue("Amount", rs.getBigDecimal(2));
					mTab.setValue("C_Currency_ID", rs.getInt(3));
					mTab.setValue("CouponNo", rs.getString(4));
					mTab.setValue("CreditCardNo", rs.getString(5));
					mTab.setValue("M_EntidadFinanciera_ID", rs.getInt(6));
					mTab.setValue("M_EntidadFinancieraPlan_ID", rs.getInt(7));
					mTab.setValue("PaymentBatch", rs.getString(8));
					mTab.setValue("TrxDate", rs.getTimestamp(9));
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, sql.toString(), e);
			} finally {
				try {
					pstmt.close();
					rs.close();
				} catch (Exception e) {
					log.log(Level.SEVERE, "Cannot close statement or resultset");
				}
			}
		} else {
			Object couponID = mTab.getValue("C_CouponsSettlements_ID");
			if (couponID != null && (Integer)couponID > 0) {
				mTab.setValue("AllocationNumber", null);
				mTab.setValue("Amount", null);
				mTab.setValue("C_Currency_ID", null);
				mTab.setValue("CouponNo", null);
				mTab.setValue("CreditCardNo", null);
				mTab.setValue("M_EntidadFinanciera_ID", null);
				mTab.setValue("M_EntidadFinancieraPlan_ID", null);
				mTab.setValue("PaymentBatch", null);
				mTab.setValue("TrxDate", null);
			}
		}
		setCalloutActive(false);
		return "";
	}

}
