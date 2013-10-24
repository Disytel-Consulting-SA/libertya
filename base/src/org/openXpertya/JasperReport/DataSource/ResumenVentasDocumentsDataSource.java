package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ResumenVentasDocumentsDataSource extends
		ResumenVentasDocTypeDataSource {

	public ResumenVentasDocumentsDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	public ResumenVentasDocumentsDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN,	boolean onlyDN) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID, onlyCN, onlyDN);
	}

	@Override
	protected String getGroupFields() {
		return "c_invoice_id, documentno";
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().toUpperCase().equals("LINE_DESCRIPTION")){
			value = getLineDescription();
		}
		return value;
	}
	
	@Override
	protected String getLineDescription() {
		return (String)getCurrentRecord().get("DOCUMENTNO");
	}
}
