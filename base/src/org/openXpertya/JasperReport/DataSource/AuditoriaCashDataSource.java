package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class AuditoriaCashDataSource extends AuditoriaDataSource {

	public AuditoriaCashDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public AuditoriaCashDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public AuditoriaCashDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, Integer currencyID,
			Integer referenceCurrencyID, String trxName) {
		super(ctx, valoresDTO, currencyID, referenceCurrencyID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getTenderType() {
		return "'CA'";
	}

	@Override
	protected String getTrxColumn() {
		return "c_cashline_id";
	}
}
