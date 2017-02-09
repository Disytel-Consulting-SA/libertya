package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.X_C_CouponsSettlements;
import org.openXpertya.util.DB;

/**
 * Proceso que permite remover la seleccion (Desmarcar "Incluir")
 * todos los cupones correspondientes a una liquidacion
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class UnselectAllCoupons extends SvrProcess {

	private int m_C_CreditCardSettlement_ID;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}

		m_C_CreditCardSettlement_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		if (m_C_CreditCardSettlement_ID == 0) {
			throw new IllegalArgumentException("C_CreditCardSettlement_ID = 0");
		}

		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + X_C_CouponsSettlements.Table_Name + " ");
		sql.append("SET ");
		sql.append("	include = 'N' ");
		sql.append("WHERE ");
		sql.append("	 c_creditcardsettlement_id = " + m_C_CreditCardSettlement_ID);
		sql.append(" AND isreconciled = 'N'");

		DB.executeUpdate(sql.toString(), get_TrxName());

		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), m_C_CreditCardSettlement_ID, get_TrxName());
		settlement.calculateSettlementCouponsTotalAmount(get_TrxName());

		return "";
	}

}
