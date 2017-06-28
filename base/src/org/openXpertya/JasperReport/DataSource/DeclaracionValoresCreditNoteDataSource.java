package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresCreditNoteDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCreditNoteDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresCreditNoteDataSource(Properties ctx,
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
		return "'CR'";
	}

	@Override
	protected String getDSDataTable(){
		return getDSFunView("c_pos_declaracionvalores_credit_filtered");
	}
	
	@Override
	protected String getInvoiceProductFiltered(String invoiceColumnName, boolean withInitialAnd){
		StringBuffer where = new StringBuffer(" AND (( ");
		where.append(super.getInvoiceProductFiltered(invoiceColumnName, false));
		where.append(" ) AND ( ");
		where.append(super.getInvoiceProductFiltered("doc_id", false));
		where.append(" )) ");
		return where.toString();
	}
}
