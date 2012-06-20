package org.openXpertya.JasperReport.DataSource;

public class DeclaracionValoresCheckDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCheckDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DeclaracionValoresCheckDataSource(DeclaracionValoresDTO valoresDTO,
			String trxName) {
		super(valoresDTO, trxName);
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
		return "K";
	}

}
