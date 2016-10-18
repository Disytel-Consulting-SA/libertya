package org.openXpertya.JasperReport.DataSource;

/**
 * Data source dummy para reporte de cheques de Banco Frances.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CheckPaymentFrancesDataSource extends QueryDataSource {

	public CheckPaymentFrancesDataSource(String trxName) {
		super(trxName);
	}

	@Override
	protected String getQuery() {
		return "SELECT 1";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

}
