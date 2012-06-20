package org.openXpertya.JasperReport.DataSource;

public class DeclaracionValoresVentasDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresVentasDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DeclaracionValoresVentasDataSource(DeclaracionValoresDTO valoresDTO,
			String trxName) {
		super(valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getQuery() {
		// TODO DOCSTATUS?
		return getStdQuery(true);
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	@Override
	protected String getTenderType() {
		return "ARI";
	}

}
