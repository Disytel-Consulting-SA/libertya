package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MPOSCashStatement;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.Util;


public class ValoresDataSource extends DeclaracionValoresDataSource {

	public ValoresDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ValoresDataSource(DeclaracionValoresDTO valoresDTO, String trxName) {
		super(valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select cs.c_posjournal_id,qty,cashvalue,cs.c_currency_id,amount,iso_code,description,currencybase(amount, cs.c_currency_id, pj.datetrx, cs.ad_client_id, cs.ad_org_id)::numeric(22,2) as amount_converted " +
					 "from C_POSCashStatement as cs " +
					 "inner join c_posjournal as pj on pj.c_posjournal_id = cs.c_posjournal_id " +
					 "inner join c_currency as c on c.c_currency_id = cs.c_currency_id");
		sql.append(" WHERE ");
		sql.append(getStdWhereClause(false, "pj"));
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	@Override
	protected String getTenderType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		// Obtiene el valor del campo del registro actual, a partir del nombre del
		// campo del reporte jasper.
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		// Si es el nombre de la moneda, obtengo el nombre del unitario
		if(field.getName().equalsIgnoreCase("CASHNAME")){
			BigDecimal n = MPOSCashStatement
					.getCashValue((String) getCurrentRecord().get("CASHVALUE"));
			int decValue = n.subtract(new BigDecimal(n.intValue()))
					.multiply(new BigDecimal(100)).intValue();
			int number = n.intValue() > 0?n.intValue():decValue;
			String aditionalDescr = n.intValue() > 0?"":" cvos.";
			String nroDescr = Util.initCap(NumeroCastellano.numeroACastellano(number)).trim();
			value = nroDescr + aditionalDescr;
		}
		else if(field.getName().equalsIgnoreCase("CASH_REAL_VALUE")){
			value = MPOSCashStatement.getCashValue((String) getCurrentRecord().get("CASHVALUE"));
		}
		return value;
	}
}
