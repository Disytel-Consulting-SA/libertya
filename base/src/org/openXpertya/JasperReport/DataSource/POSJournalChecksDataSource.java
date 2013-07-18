package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class POSJournalChecksDataSource extends
		DeclaracionValoresCheckDataSource {

	public POSJournalChecksDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public POSJournalChecksDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT p.documentno, p.checkno, p.a_bank, ba.description as bank_account_description, p.a_name, bp.name as bpartner_name, p.duedate, p.datetrx, p.payamt as total ");
		sql.append("FROM (");
		sql.append(getStdQuery(true));
		sql.append(" ) as dv ");
		sql.append(" INNER JOIN c_payment as p ON p.c_payment_id = dv.doc_id ");
		sql.append(" INNER JOIN c_bankaccount as ba ON ba.c_bankaccount_id = p.c_bankaccount_id ");
		sql.append(" INNER JOIN c_bpartner as bp ON bp.c_bpartner_id = p.c_bpartner_id ");
		return sql.toString();
	}
}
