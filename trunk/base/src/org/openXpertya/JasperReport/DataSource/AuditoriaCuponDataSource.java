package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class AuditoriaCuponDataSource extends AuditoriaDataSource {

	public AuditoriaCuponDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public AuditoriaCuponDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}
	
	public AuditoriaCuponDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, Integer currencyID,
			Integer referenceCurrencyID, String trxName) {
		super(ctx, valoresDTO, currencyID, referenceCurrencyID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getTenderType() {
		return "'C'";
	}

	@Override
	protected String getTrxColumn() {
		return "c_payment_id";
	}

	@Override
	protected String getFunViewName() {
		return "c_posjournal_c_payment_v_filtered";
	}
}
