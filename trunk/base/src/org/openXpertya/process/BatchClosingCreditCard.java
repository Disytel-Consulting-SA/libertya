package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openXpertya.model.MPayment;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class BatchClosingCreditCard extends SvrProcess {

	private String numerodelote;
	private int entidadfinanciera;
	private Integer organizacion;
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

	private boolean existeEntidadFinanciera(int EntidadFinanciera_ID) {
		int cantidad = DB
				.getSQLValue(
						get_TrxName(),
						"SELECT COUNT(M_EntidadFinanciera_ID) FROM M_EntidadFinanciera WHERE M_EntidadFinanciera_ID = "
								+ EntidadFinanciera_ID);
		return (cantidad == 1);
	}

	private boolean existeOrganizacion(Integer AD_Org_ID) {
		if (AD_Org_ID == null)
			return false;
		int cantidad = DB.getSQLValue(get_TrxName(),
				"SELECT COUNT(AD_Org_ID) FROM AD_Org WHERE AD_Org_ID = "
						+ AD_Org_ID);
		return (cantidad == 1);
	}

	@Override
	protected String doIt() {
		if (!existeOrganizacion(getOrganizacion())) {
			return getMsg("OrgIsMandatoryOrNoExist");
		}
		if (!existeEntidadFinanciera(getEntidadFinanciera())) {
			return getMsg("EntidadFinancieraIsMandatoryOrNoExist");
		}
		if ((getCouponBatchNumber() == null)
				|| ("".equals(getCouponBatchNumber()))) {
			return getMsg("CouponBatchNumberMandatory");
		}
		String sql = "SELECT p.* FROM c_payment p INNER JOIN m_entidadfinancieraplan efp ON (efp.m_entidadfinancieraplan_id = p.m_entidadfinancieraplan_id) WHERE p.ad_org_id=? AND efp.m_entidadfinanciera_id=? AND couponbatchnumber IS NULL";
		PreparedStatement pstmt = null;
		pstmt = DB.prepareStatement(sql);
		try {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pstmt = null;
		return getMsg("ProcessOK");
	}

	private String getCouponBatchNumber() {
		return numerodelote;
	}

	private int getEntidadFinanciera() {
		return entidadfinanciera;
	}

	private Integer getOrganizacion() {
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

	protected String getMsg(String msg) {
		return Msg.translate(getCtx(), msg);
	}

}
