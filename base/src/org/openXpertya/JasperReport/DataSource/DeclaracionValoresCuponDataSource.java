package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresCuponDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCuponDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresCuponDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
		// TODO Auto-generated constructor stub
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
		return "'C'";
	}

}
