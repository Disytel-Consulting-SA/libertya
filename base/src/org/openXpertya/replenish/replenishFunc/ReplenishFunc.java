package org.openXpertya.replenish.replenishFunc;

import java.math.BigDecimal;

import org.openXpertya.replenish.Replenish;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

public abstract class ReplenishFunc {
	/**	Argumentos pasados a la funcion			*/
	protected String ArgsStr;
	
	/** Logger									*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	
	/** Replenish que lanza la funcion		*/
	protected Replenish p_Replenish;
	
	
	public abstract boolean prepare();
	
	public abstract BigDecimal doIt() throws ReplenishFuncException ;
	
	public void setArgsStr(String args)	throws ReplenishFuncException {
		ArgsStr = args;
	}
	
	public String getArgsStr()	{
		return ArgsStr;
	}
	
	public void setReplenish(Replenish r)	{
		p_Replenish = r;
	}
	
	protected int getAD_Client_ID()	{
		return Env.getAD_Client_ID(p_Replenish.getCtx());
	}
	
	protected int getAD_Org_ID()	{
		return Env.getAD_Org_ID(p_Replenish.getCtx());
	}
	
	/**
	 * Devuelve la cadena "ad_org_id=@org@ and ad_client_id=@client@ 
	 * @return
	 */
	protected String getSecTrxClause()	{
		StringBuffer sql = new StringBuffer("ad_client_id='").append(getAD_Client_ID()).append("' ");
		sql.append(" and ad_org_id='").append(getAD_Org_ID()).append("' ");
		return sql.toString();
	}
}
