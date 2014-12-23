package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
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
	protected String doIt() throws Exception {
		if (!existeOrganizacion(getOrganizacion())) {
			throw new Exception(getMsg("OrgIsMandatoryOrNoExist"));
		}
		if (!existeEntidadFinanciera(getEntidadFinanciera())) {
			throw new Exception(getMsg("EntidadFinancieraIsMandatoryOrNoExist"));
		}
		if ((getCouponBatchNumber() == null) || ("".equals(getCouponBatchNumber()))) {
			throw new Exception(getMsg("CouponBatchNumberMandatory"));
		}
		String sql = "SELECT p.* FROM c_payment p INNER JOIN m_entidadfinancieraplan efp ON (efp.m_entidadfinancieraplan_id = p.m_entidadfinancieraplan_id) WHERE p.ad_org_id=? AND efp.m_entidadfinanciera_id=? AND couponbatchnumber IS NULL";
		PreparedStatement pstmt = null;
		pstmt = DB.prepareStatement(sql);
		ResultSet rs = null;
		try {
			pstmt.setInt(1, getOrganizacion());
			pstmt.setInt(2, getEntidadFinanciera());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				payment = new MPayment(getCtx(), rs, get_TrxName());
				payment.setCouponBatchNumber(getCouponBatchNumber());
				if (!payment.save()) {
					throw new Exception("Error actualizando el pago: "
							+ payment.getDocumentNo() + ". Motivo: "
							+ CLogger.retrieveErrorAsString());
				}
			}
			pstmt.close();
			rs.close();
		} catch (Exception e) {
			String errorMsg = "Error actualizando el pago" + e.getMessage(); 
			log.warning(errorMsg);
			throw new Exception(errorMsg);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
				pstmt = null;
				rs = null;
			} catch (SQLException e) {
				log.severe("Cannot close statement or resultset");
			}
		}
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
