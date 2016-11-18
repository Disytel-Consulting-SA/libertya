package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class AuditoriaDataSource extends DeclaracionValoresDataSource {
	
	/** Moneda legal */
	private Integer currencyID;	
	
	/** Moneda de referencia */
	private Integer currencyReferenceID;
	
	public AuditoriaDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public AuditoriaDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}
	
	public AuditoriaDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, Integer currencyID,
			Integer referenceCurrencyID, String trxName) {
		this(ctx, valoresDTO, "", "", "", trxName);
		setCurrencyID(currencyID);
		setCurrencyReferenceID(referenceCurrencyID);
	}

	@Override
	protected String getQuery() {
		String trxColumn = getTrxColumn();
		StringBuffer sql = new StringBuffer("SELECT i.c_invoice_id, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, pjp.amount, currencyconvert(pjp.amount, dv.c_currency_id, ?, dv.datetrx, 0, dv.ad_client_id, dv.ad_org_id) as amountanothercurrency, pjp.description as observacion FROM ("); 
		sql.append(getStdQuery(true));
		sql.append(" ) as dv ");
		sql.append(" INNER JOIN ");
		sql.append(getDSFunView(getFunViewName()));
		sql.append(" as pjp ON pjp." + trxColumn + " = dv.doc_id ");
		sql.append(" INNER JOIN c_invoice as i ON i.c_invoice_id = pjp.c_invoice_id ");
		sql.append(" ORDER BY i.documentno ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getCurrencyReferenceID());
		for (Object param : getStdWhereClauseParams()) {
			params.add(param);
		}
		return params.toArray();
	}

	/**
	 * @return nombre de la columna de transacción por el cual se realiza el
	 *         join entre las vistas c_pos_declaracionvalores_v y
	 *         c_posjournalpayments_v
	 */
	protected abstract String getTrxColumn();
	
	/**
	 * @return el nombre de la funview para cada caso
	 */
	protected abstract String getFunViewName();

	private void setCurrencyID(Integer currencyID) {
		this.currencyID = currencyID;
	}

	private Integer getCurrencyID() {
		return currencyID;
	}

	private void setCurrencyReferenceID(Integer currencyReferenceID) {
		this.currencyReferenceID = currencyReferenceID;
	}

	private Integer getCurrencyReferenceID() {
		return currencyReferenceID;
	}
}
