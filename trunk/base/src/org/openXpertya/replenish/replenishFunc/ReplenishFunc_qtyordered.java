package org.openXpertya.replenish.replenishFunc;

import java.math.BigDecimal;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class ReplenishFunc_qtyordered extends ReplenishFunc {

	private String T;	

	@Override
	public BigDecimal doIt() throws ReplenishFuncException {
		
		StringBuffer sql = new StringBuffer(
		" SELECT SUM(OL.QTYENTERED) " +  
		" FROM C_ORDER O, C_ORDERLINE OL " +
		" WHERE OL.M_PRODUCT_ID = " + p_Replenish.getM_Product_ID() +
		" AND OL.M_WAREHOUSE_ID = " + p_Replenish.getM_Warehouse_ID() +
		" AND O.ISSOTRX = 'N' " +
		" AND O.ISACTIVE = 'Y' " +  
		" AND OL.ISACTIVE = 'Y' " +  
		" AND (O.DOCSTATUS = 'CO' OR O.DOCSTATUS = 'CL') " +  
		" AND O.CREATED BETWEEN ('now'::text)::timestamp(6) + '-" + T + " days' AND ('now'::text)::timestamp(6) "); 
		
		log.info("Calculando :" + sql.toString());
		
		BigDecimal res = (BigDecimal)DB.getSQLObject(null, sql.toString(), null);
		if (res == null)	{
			res = Env.ZERO;
		}
		return res;
	}

	
	
	@Override
	public boolean prepare() throws ReplenishFuncException {
		if (ArgsStr == null)	{
			throw new ReplenishFuncException ("No se han pasado parametros.");
		}
		T = ArgsStr;
		return true;
	}

	
}
