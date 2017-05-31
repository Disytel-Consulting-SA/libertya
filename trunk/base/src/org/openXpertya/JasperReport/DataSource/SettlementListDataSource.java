package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MRefList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * Data Source para reporte de listado de liquidaciones.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class SettlementListDataSource extends QueryDataSource {

	private Properties ctx;
	private int c_bpartner_id;
	private String docstatus;
	private String dateFrom;
	private String dateTo;
	private int ad_org_id;

	/**
	 * Constructor.
	 * @param trxName Transacción.
	 */
	public SettlementListDataSource(Properties ctx, String trxName) {
		super(trxName);
		setCtx(ctx);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	s.settlementno, "); // Nro liquidación
		sql.append("	s.docstatus, "); 
		sql.append("	bp.value || '-' || bp.name AS creditcard, "); // Tarjeta
		sql.append("	COALESCE(amount, 0) AS amount, "); // Importe bruto
		sql.append("	COALESCE(netamount, 0) AS netamount, "); // Importe acreditado
		sql.append("	COALESCE(couponstotalamount, 0) AS done, "); // Importe neto
		sql.append("	COALESCE(ivaamount, 0) AS iva, "); // Total iva
		sql.append("	COALESCE(withholding, 0) AS retention, "); // Total retenciones
		sql.append("	COALESCE(expenses, 0) AS expenses, "); // Total otros gastos
		sql.append("	COALESCE(commissionamount, 0) AS commission, "); // Total comisiones
		sql.append("	COALESCE(perception, 0) AS perception, "); // Total percepciones
		sql.append("	paymentdate AS date "); // Fecha
		sql.append("FROM ");
		sql.append("	" + MCreditCardSettlement.Table_Name + " s ");
		sql.append("	INNER JOIN " + MBPartner.Table_Name + " bp ON s.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("WHERE ");
		sql.append("	COALESCE(s.settlementno, '') <> '' ");

		if (ad_org_id > 0) {
			sql.append("	AND s.ad_org_id = " + ad_org_id + " ");
		}
		if (c_bpartner_id > 0) {
			sql.append("	AND s.c_bpartner_id = " + c_bpartner_id + " ");
		}
		if (docstatus != null && !docstatus.trim().isEmpty()) {
			sql.append("	AND s.docstatus = '" + docstatus + "' ");
		}
		if (dateFrom != null && !dateFrom.trim().isEmpty()) {
			sql.append("	AND s.paymentdate >= '" + dateFrom + "' ");
		}
		if (dateTo != null && !dateTo.trim().isEmpty()) {
			sql.append("	AND s.paymentdate <= '" + dateTo + "' ");
		}

		sql.append("ORDER BY ");
		sql.append("	s.settlementno ");

		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

	@Override
	protected boolean isQueryNoConvert() {
		return true;
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().equalsIgnoreCase("docstatus")){
			value = MRefList.getListName(getCtx(), MCreditCardSettlement.DOCSTATUS_AD_Reference_ID,
					(String) getCurrentRecord().get(field.getName().toUpperCase()));
		}
		return value;
	}

	// SETTERS

	public void setC_bpartner_id(int c_bpartner_id) {
		this.c_bpartner_id = c_bpartner_id;
	}

	public void setDocstatus(String docstatus) {
		this.docstatus = docstatus;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setAd_org_id(int ad_org_id) {
		this.ad_org_id = ad_org_id;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
