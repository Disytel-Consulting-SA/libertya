package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.X_C_CouponsSettlements;
import org.openXpertya.model.X_C_CreditCardCouponFilter;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Conciliación de Cupones.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ReconcileCoupons extends SvrProcess {

	private MCreditCardSettlement creditCardSettlement;

	@Override
	protected void prepare() {
		creditCardSettlement = new MCreditCardSettlement(getCtx(), getRecord_ID(), get_TrxName());
	}

	@Override
	protected String doIt() throws Exception {
		if (!creditCardSettlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Completed)) {
			return Msg.getMsg(Env.getAD_Language(getCtx()), "ReconcileCouponsWrongDocStatus");
		}
		// Llegado este punto, la liquidación no está completa, y
		// el estado de auditoría de los cupones es "A verificar".
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	P.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_CreditCardCouponFilter.Table_Name + " f ");
		sql.append("	INNER JOIN " + X_C_CouponsSettlements.Table_Name + " c ");
		sql.append("		ON c.c_creditcardcouponfilter_id = f.c_creditcardcouponfilter_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = c.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	f.c_creditcardsettlement_id = ? ");
		sql.append("	AND P.auditstatus = ? ");
		sql.append("	AND include = 'Y' ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		int updated = 0;
		int deleted = 0;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, creditCardSettlement.getC_CreditCardSettlement_ID());
			ps.setString(2, X_C_Payment.AUDITSTATUS_ToVerify);
			rs = ps.executeQuery();
			if (rs.next()) {
				// Cambia el estado de auditoria de los cupones a "Pagado".
				sql = new StringBuffer();
				sql.append("UPDATE ");
				sql.append("	" + X_C_Payment.Table_Name + " ");
				sql.append("SET ");
				sql.append("	auditstatus = '" + X_C_Payment.AUDITSTATUS_Paid + "' ");
				sql.append("WHERE ");
				sql.append("	c_payment_id = " + rs.getInt(1));

				updated += DB.executeUpdate(sql.toString());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "ReconcileCoupons.doIt", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		// Elimina de los cupones, todos los que se recuperaron
		// al filtrar que no tengan el check "Incluir".
		sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_CouponsSettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_couponssettlements_id IN ( ");
		sql.append("		SELECT DISTINCT ");
		sql.append("			c.c_couponssettlements_id ");
		sql.append("		FROM ");
		sql.append("				" + X_C_CreditCardCouponFilter.Table_Name + " f ");
		sql.append("			INNER JOIN " + X_C_CouponsSettlements.Table_Name + " c ");
		sql.append("				ON c.c_creditcardcouponfilter_id = f.c_creditcardcouponfilter_id ");
		sql.append("		WHERE ");
		sql.append("			f.c_creditcardsettlement_id = " + creditCardSettlement.getC_CreditCardSettlement_ID() + " ");
		sql.append("			AND C.include = 'N' ");
		sql.append("	) ");

		deleted += DB.executeUpdate(sql.toString());

		Object[] params = new Object[] { updated, deleted };
		return Msg.getMsg(Env.getAD_Language(getCtx()), "ReconcileCouponsResult", params);
	}

}
