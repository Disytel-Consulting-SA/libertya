package org.openXpertya.process;

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
		sql.append(" UPDATE ").append(X_C_Payment.Table_Name);
		sql.append(" SET auditstatus = '" + X_C_Payment.AUDITSTATUS_Paid + "' ");
		sql.append(" WHERE c_payment_id IN (");
		sql.append("	SELECT p.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_CreditCardCouponFilter.Table_Name + " f ");
		sql.append("	INNER JOIN " + X_C_CouponsSettlements.Table_Name + " c ");
		sql.append("		ON c.c_creditcardcouponfilter_id = f.c_creditcardcouponfilter_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = c.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	f.c_creditcardsettlement_id = ").append(creditCardSettlement.getC_CreditCardSettlement_ID());
		sql.append("	AND p.auditstatus = '").append(X_C_Payment.AUDITSTATUS_ToVerify).append("'");
		sql.append("	AND c.include = 'Y' ) ");
		
		int updated = DB.executeUpdate(sql.toString(), get_TrxName());

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

		int deleted = DB.executeUpdate(sql.toString(), get_TrxName());

		// Finalmente marca los restantes como conciliados.
		sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + X_C_CouponsSettlements.Table_Name + " ");
		sql.append("SET ");
		sql.append("	isReconciled = 'Y', ");
		sql.append("	processed = 'Y' ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + creditCardSettlement.getC_CreditCardSettlement_ID() + " ");

		DB.executeUpdate(sql.toString(), get_TrxName());

		Object[] params = new Object[] { updated, deleted };
		return Msg.getMsg(Env.getAD_Language(getCtx()), "ReconcileCouponsResult", params);
	}

}
