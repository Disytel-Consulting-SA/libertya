package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresVentasDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresVentasDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DeclaracionValoresVentasDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}

	@Override
	protected String getQuery() {
		return getStdQuery(true);
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	@Override
	protected String getTenderType() {
		return "'ARI'";
	}

	@Override
	protected String getDSDataTable(){
		return "c_pos_declaracionvalores_ventas";
	}
}
