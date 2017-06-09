package org.openXpertya.grid;

import java.util.List;

public class CreateFromStatementSettlements extends CreateFromStatementData {

	@Override
	public String getWhereClauseSQL() {
		return " AND ccs.c_creditcardsettlement_id is not null "
				+ "AND ccs.docstatus IN ('CO','CL','VO','RE')";
	}

	@Override
	public String getMsg() {
		return "C_CreditCardSettlement_ID";
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
