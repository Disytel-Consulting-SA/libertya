package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresTransferDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresTransferDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresTransferDataSource(Properties ctx,
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
		return "'A'";
	}

	@Override
	protected String getDSDataTable(){
		return getDSFunView("c_pos_declaracionvalores_payments_filtered");
	}
}
