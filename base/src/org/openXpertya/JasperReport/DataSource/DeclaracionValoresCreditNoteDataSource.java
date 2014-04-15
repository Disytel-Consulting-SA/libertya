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
		return "c_pos_declaracionvalores_credit";
	}
}
