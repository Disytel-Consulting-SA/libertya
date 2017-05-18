package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.MPayment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Proceso para cambiar el estado de un cupon de rechazado a verificar.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class UnRejectCoupon extends SvrProcess {
	private int m_C_Payment_ID;

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
		m_C_Payment_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + MPayment.Table_Name + " ");
		sql.append("SET ");
		sql.append("	auditstatus = '" + MPayment.AUDITSTATUS_ToVerify + "' ");
		sql.append("WHERE ");
		sql.append("	C_Payment_ID = " + m_C_Payment_ID);

		int no = DB.executeUpdate(sql.toString());
		if (no == -1) {
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "UnRejectCouponError"));
		} else {
			return Msg.getMsg(Env.getAD_Language(getCtx()), "UnRejectCouponSuccess");
		}
	}

}
