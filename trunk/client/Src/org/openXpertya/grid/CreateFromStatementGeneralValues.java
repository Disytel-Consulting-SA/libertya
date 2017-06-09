package org.openXpertya.grid;

import java.util.List;

import org.openXpertya.model.X_C_Payment;

public class CreateFromStatementGeneralValues extends CreateFromStatementData {

	public CreateFromStatementGeneralValues() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getWhereClauseSQL() {
		// Sólo cheques y transferencias
		return " AND ccs.c_creditcardsettlement_id is null "
				+ " AND bdb.m_boletadeposito_id is null "
				+ " AND bdec.m_boletadeposito_id is null "
				+ " AND p.tendertype IN ('" + X_C_Payment.TENDERTYPE_Check + "','" + X_C_Payment.TENDERTYPE_DirectDeposit
				+ "')";
	}

	@Override
	public String getMsg() {
		return "GeneralValues";
	}

	@Override
	public List<Object> addSQLParams(List<Object> params) {
		// TODO Auto-generated method stub
		return params;
	}

	@Override
	public boolean isAllowGrouped() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAllowCouponBatchNoFilter() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
