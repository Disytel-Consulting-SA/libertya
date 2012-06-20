/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 * Algunas partes son Copyright  1999-2001 Jorg Janke, Copyright  ComPiere, Inc.
 *  
 */
package org.openXpertya.report.rfunc;

import java.math.BigDecimal;

import org.openXpertya.util.Env;
import org.openXpertya.util.CLogger;
import org.openXpertya.report.*;
/**
 * Pretendo crear una clase base para las funciones que se permitan en 
 * los balances.
 * @TODO: Ver como podemos tratar de manera elegante los parametros.
 *
 */
public abstract class RFunc {
	/**	Argumentos pasados a la funcion			*/
	protected String ArgsStr;
	
	/** Logger									*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	
	/** Fin Report que lanza la funcion		*/
	protected FinReport p_FinReport;
	
	
	public abstract boolean prepare();
	
	public abstract BigDecimal doIt() throws RFuncException ;
	
	public void setArgsStr(String args)	throws RFuncException {
		ArgsStr = args;
	}
	
	public String getArgsStr()	{
		return ArgsStr;
	}
	
	public void setFinReport(FinReport fr)	{
		p_FinReport = fr;
	}
	
	protected int getAD_Client_ID()	{
		return Env.getAD_Client_ID(p_FinReport.getCtx());
	}
	
	protected int getAD_Org_ID()	{
		return Env.getAD_Org_ID(p_FinReport.getCtx());
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
