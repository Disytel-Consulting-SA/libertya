package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MPayment;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

public class BatchClosingCreditCard extends SvrProcess {

	private String numerodelote;
	private int entidadfinanciera;
	private int organizacion;
	private MPayment payment;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("CouponBatchNumber")) {
				setNumeroDeLote((String) para[i].getParameter());
			} else if (name.equals("AD_Org_ID")) {
				setOrganizacion(para[i].getParameterAsInt());
			} else if (name.equals("M_EntidadFinanciera_ID")) {
				setEntidadFinanciera(para[i].getParameterAsInt());
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		String sql = "SELECT p.* FROM c_payment p INNER JOIN m_entidadfinancieraplan efp ON (efp.m_entidadfinancieraplan_id = p.m_entidadfinancieraplan_id) WHERE p.ad_org_id=? AND efp.m_entidadfinanciera_id=? AND couponbatchnumber IS NULL";
		PreparedStatement pstmt = null;
		pstmt = DB.prepareStatement(sql);
		pstmt.setInt(1, getOrganizacion());
		pstmt.setInt(2, getEntidadFinanciera());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			payment = new MPayment(getCtx(), rs, null);
			payment.setCouponBatchNumber(getCouponBatchNumber());
			payment.save();
		}
		rs.close();
		pstmt.close();
		pstmt = null;
		return "";
	}

	private String getCouponBatchNumber() {
		return numerodelote;
	}

	private int getEntidadFinanciera() {
		return entidadfinanciera;
	}

	private int getOrganizacion() {
		return organizacion;
	}

	private void setNumeroDeLote(String numerodelote) {
		this.numerodelote = numerodelote;
	}

	private void setEntidadFinanciera(int entidadfinanciera_id) {
		entidadfinanciera = entidadfinanciera_id;
	}

	private void setOrganizacion(int organizacion_id) {
		organizacion = organizacion_id;
	}

}
