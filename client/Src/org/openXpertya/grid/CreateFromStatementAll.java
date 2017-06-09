package org.openXpertya.grid;

import java.util.List;

public class CreateFromStatementAll extends CreateFromStatementData {

	@Override
	public String getWhereClauseSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMsg() {
		return "All";
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
		return true;
	}

}
