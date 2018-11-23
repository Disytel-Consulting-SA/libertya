package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.util.Util;

public class DeclaracionValoresCuponDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCuponDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresCuponDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}
	
	public DeclaracionValoresCuponDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String additionalWhereClause, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, additionalWhereClause, trxName);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer(getStdSelect(true));
		sql.append(getStdWhereClause(true, null, true, true, false));
		if(!Util.isEmpty(getGroupBy())){
			sql.append(" GROUP BY "+getGroupBy());
		}
		if(!Util.isEmpty(getOrderBy(), true)){
			sql.append(" ORDER BY "+getOrderBy());
		}
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	@Override
	protected String getTenderType() {
		return "'C'";
	}

	@Override
	protected String getDSDataTable(){
		return getDSFunView("c_pos_declaracionvalores_payments_filtered");
	}
}
