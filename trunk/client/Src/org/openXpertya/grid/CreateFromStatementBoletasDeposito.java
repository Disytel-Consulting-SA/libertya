package org.openXpertya.grid;

import java.util.List;

public class CreateFromStatementBoletasDeposito extends CreateFromStatementData {

	public CreateFromStatementBoletasDeposito() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getWhereClauseSQL() {
		return " AND ((bdb.m_boletadeposito_id is not null and bdb.docstatus in ('CO','CL','VO','RE')) "
				+ "		OR (bdec.m_boletadeposito_id is not null AND bdec.docstatus in ('CO','CL','VO','RE'))) ";
	}

	@Override
	public String getMsg() {
		return "M_BoletaDeposito_ID";
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
