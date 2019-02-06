package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MPOSCashStatement;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;


public class ValoresDataSource extends DeclaracionValoresSubreportDataSource {

	public ValoresDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ValoresDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select cashvalue, cs.c_currency_id, iso_code, description, sum(qty)::integer as qty, sum(amount)::numeric(22,2) as amount, sum(currencybase(amount, cs.c_currency_id, pj.datetrx, cs.ad_client_id, cs.ad_org_id))::numeric(22,2) as amount_converted " +
					 "from C_POSCashStatement as cs " +
					 "inner join c_posjournal as pj on pj.c_posjournal_id = cs.c_posjournal_id " +
					 "inner join c_currency as c on c.c_currency_id = cs.c_currency_id");
		String where = " WHERE pj.docstatus NOT IN ('DR') AND ";
		String whereClause = getStdWhereClause(false, "pj", false, false, false, false);
		if(!Util.isEmpty(whereClause, true)){
			sql.append(where).append(whereClause);
		}
		sql.append(" GROUP BY cs.c_currency_id, cashvalue, iso_code, description ");
		sql.append(" ORDER BY cs.c_currency_id, cashvalue ");
		return sql.toString();
	}
	
	public String getTheQuery(){
		return getQuery();
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	public BigDecimal getDeclaracionValoresTotalAmt() throws Exception{
		StringBuffer sql = new StringBuffer("SELECT sum(amount_converted) FROM ( ");
		sql.append(getQuery());
		sql.append(" ) as todo ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		ps = DB.prepareStatement(sql.toString(), getTrxName());
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for (Object param : getParameters()) {
			ps.setObject(i++, param);
		}
		rs = ps.executeQuery();
		if(rs.next()){
			total = rs.getBigDecimal(1);
		}
		
		rs.close();
		ps.close();
		
		return total;
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
	
	protected boolean isFunView(){
		return false;
	}
}
