package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MPOSPaymentMedium;

public class DeclaracionValoresVentasReceiptDataSource extends
		DeclaracionValoresVentasDataSource {
	
	public DeclaracionValoresVentasReceiptDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT al.c_invoice_id, tipo, montosaldado, i.description, al.paydescription, i.datetrx, ingreso FROM ("); 
		sql.append(super.getQuery());
		sql.append(" ) as i ");
		sql.append(" INNER JOIN c_allocation_detail_v as al ON i.doc_id = al.c_invoice_id ");
		return sql.toString();
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		if(field.getName().toUpperCase().equals("TIPO_COBRO")){
			value = JasperReportsUtil.getListName(getCtx(),
					MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
					(String) getCurrentRecord().get("TIPO"));
		}
		else{
			value = super.getFieldValue(field);
		}
		return value;
	}
}
