/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 *  
 */

package org.openXpertya.report.rfunc;

import java.math.BigDecimal;

import org.openXpertya.model.M_Column;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class RFunc_haber extends RFunc {

	private final String m_col = "AmtAcctDr";
	
	private String AcctValue;
	
	
	public BigDecimal doIt() throws RFuncException  {
		// Obtenemos el saldo de la cuenta
		StringBuffer sql = new StringBuffer("SELECT SUM(").append(m_col).append(") ");
		sql.append(" FROM Fact_Acct_Balance WHERE ");
		sql.append("account_id= (select C_ElementValue_ID from C_ElementValue where value='");
		sql.append(AcctValue).append("' and ").append(getSecTrxClause()).append(")");
		sql.append(" AND DateAcct ");
		sql.append(p_FinReport.getDateWhere());
		sql.append(" and ").append(getSecTrxClause());
		
		log.info("Calculando :" + sql.toString());
		
		BigDecimal res = (BigDecimal)DB.getSQLObject(null, sql.toString(), null);
		if (res == null)	{
			res = Env.ZERO;
		}
		return res;
		
	}

	public boolean prepare() throws RFuncException {
		if (ArgsStr == null)	{
			throw new RFuncException("No se han pasado parametros.");
		}
		
		AcctValue = ArgsStr;
		return true;
	}

}
