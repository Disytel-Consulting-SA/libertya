package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.model.X_C_CashLine;
import org.openXpertya.util.Env;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;


public class DeclaracionValoresCashDataSource extends DeclaracionValoresDataSource {

	public DeclaracionValoresCashDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresCashDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
		// TODO Auto-generated constructor stub
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
		return "'CA'";
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		// Categoría del reporte = cashtype
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		Object returnVal = getCurrentRecord().get(field.getName().toUpperCase());; 
		if(field.getName().equalsIgnoreCase("CATEGORY")){
			returnVal = JasperReportsUtil.getListName(Env.getCtx(),
					X_C_CashLine.CASHTYPE_AD_Reference_ID, (String) value);
			if(((String) value).equals(X_C_CashLine.CASHTYPE_Charge)){
				returnVal = ""+returnVal+" "+getCurrentRecord().get("CHARGENAME");
			}
		}
		// Obtiene el valor del campo del registro actual, a partir del nombre del
		// campo del reporte jasper.
		return returnVal;
	}

}
