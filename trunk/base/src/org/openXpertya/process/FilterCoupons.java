package org.openXpertya.process;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MCouponsSettlements;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_CreditCardCouponFilter;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.model.X_M_EntidadFinancieraPlan;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Proceso que genera liquidaciones de cupones a partir de un filtro.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class FilterCoupons extends SvrProcess {

	private int m_C_CreditCardCouponFilter_ID;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
		m_C_CreditCardCouponFilter_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		if (m_C_CreditCardCouponFilter_ID == 0) {
			throw new IllegalArgumentException("C_CreditCardCouponFilter_ID = 0");
		}

		X_C_CreditCardCouponFilter filter = new X_C_CreditCardCouponFilter(getCtx(), m_C_CreditCardCouponFilter_ID, get_TrxName());

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	efp.m_entidadfinanciera_id, ");
		sql.append("	p.m_entidadfinancieraplan_id, ");
		sql.append("	p.datetrx, ");
		sql.append("	p.payamt, ");
		sql.append("	p.couponnumber, ");
		sql.append("	ah.documentno AS allocationnumber, ");
		sql.append("	p.creditcardnumber, ");
		sql.append("	p.couponbatchnumber, ");
		sql.append("	p.c_currency_id, ");
		sql.append("	p.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_Payment.Table_Name + " p ");
		sql.append("	JOIN " + X_M_EntidadFinancieraPlan.Table_Name + " efp ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id ");
		sql.append("	LEFT JOIN " + X_C_AllocationLine.Table_Name + " al ON p.c_payment_id = al.c_payment_id ");
		sql.append("	LEFT JOIN " + X_C_AllocationHdr.Table_Name + " ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("WHERE ");
		sql.append("	p.c_payment_id NOT IN ( ");
		sql.append("		SELECT ");
		sql.append("			c_payment_id ");
		sql.append("		FROM ");
		sql.append("			" + MCouponsSettlements.Table_Name + " ");
		sql.append("	) ");
		sql.append("	AND p.isreconciled = 'N' ");

		if (filter.getTrxDateFrom() != null) {
			sql.append("AND p.datetrx >= ? ");
		}
		if (filter.getTrxDateTo() != null) {
			sql.append("AND p.datetrx <= ? ");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int saved = 0;

		try {
			pstmt = DB.prepareStatement(sql.toString());

			int aux = 1;
			if (filter.getTrxDateFrom() != null) {
				pstmt.setDate(aux, new Date(filter.getTrxDateFrom().getTime()));
				aux++;
			}
			if (filter.getTrxDateTo() != null) {
				pstmt.setDate(aux, new Date(filter.getTrxDateTo().getTime()));
			}

			rs = pstmt.executeQuery();
			while (rs.next()) {
				MCouponsSettlements couponsSettlements = new MCouponsSettlements(getCtx(), 0, get_TrxName());
				couponsSettlements.setC_CreditCardSettlement_ID(filter.getC_CreditCardSettlement_ID());

				couponsSettlements.setC_CreditCardCouponFilter_ID(filter.getC_CreditCardCouponFilter_ID());
				
				couponsSettlements.setM_EntidadFinanciera_ID(rs.getInt(1));
				couponsSettlements.setM_EntidadFinancieraPlan_ID(rs.getInt(2));
				couponsSettlements.setTrxDate(rs.getTimestamp(3));
				couponsSettlements.setAmount(rs.getBigDecimal(4));
				couponsSettlements.setCouponNo(rs.getString(5));
				couponsSettlements.setAllocationNumber(rs.getString(6));
				couponsSettlements.setCreditCardNo(rs.getString(7));
				couponsSettlements.setPaymentBatch(rs.getString(8));
				couponsSettlements.setC_Currency_ID(rs.getInt(9));
				couponsSettlements.setC_Payment_ID(rs.getInt(10));

				couponsSettlements.setInclude(false);

				if (!couponsSettlements.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				} else {
					couponsSettlements.calculateSettlementCouponsTotalAmount(get_TrxName());
					saved++;
				}
			}
			// Marco al filtro como procesado.
			filter.setIsProcessed(true);
			if (!filter.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
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
		return "@Created@ #" + saved;
	}

}
