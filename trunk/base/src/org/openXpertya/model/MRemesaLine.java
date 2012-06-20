/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.model;

import java.math.*;
import java.sql.*;
import java.util.*;

import org.openXpertya.model.X_C_RemesaLine;
import org.openXpertya.util.*;

public class MRemesaLine extends X_C_RemesaLine
{
	/**
	 *  Default Constructor
	 *  @param ctx context
	 *  @param  C_OrderLine_ID  order line to load
	 */
	public MRemesaLine (Properties ctx, int C_RemesaLine_ID,String trxName)
	{
		super (ctx, C_RemesaLine_ID,trxName);
		if (C_RemesaLine_ID == 0)
		{
			//setLineNetAmt(0);
			//
		}
	}	//	RemesaLine

	/**
	 *  
	 */
	public MRemesaLine (MRemesa rem)
	{
		this (rem.getCtx(), 0, null);
		if (rem.getID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setC_Remesa_ID (rem.getC_Remesa_ID());	//	parent
		setRemesa(rem);
		//	Reset
		//setLineNetAmt(0);
	}	//	MRemesaLine

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 */
	public MRemesaLine (Properties ctx, ResultSet rs)
	{
		super (ctx, rs,null);
	}	//	MRemesaLine

	/**
	 * 	Set Defaults from Order.
	 * 	Does not set Parent !!
	 * 	@param order order
	 */
	public void setRemesa (MRemesa rem)
	{
		setC_Remesa_ID(rem.getC_Remesa_ID());
	}	//	setOrder

	
	/**************************************************************************
	 * 	String Representation
	 * 	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MRemesaLine[")
			.append(getID()).append(",BPartner=").append(getBPName())
			.append(",Description=").append(getDescription())
			.append ("]");
		return sb.toString ();
	}	//	toString

	/***************************
	 * 
	 * @param newRecord
	 * @return BPName
	 */
	public String getBPName()
	{
		int bp = getC_BPartner_ID();
		if (bp != 0)
		{
			MBPartner mbp = new MBPartner(Env.getCtx(), bp,null);
			return mbp.getName();			
		}
		else
			return "";
	}
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return true if it can be sabed
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		updateLineNetAmt();
		return true;
	}	//	beforeSave

	
	/**
	 * 	Before Delete
	 *	@return true if it can be deleted
	 */
	protected boolean beforeDelete ()
	{
		return true;
	}	//	beforeDelete
	
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return saved
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		return updateRemesa();
	}	//	afterSave

	/**
	 * 	After Delete
	 *	@param success success
	 *	@return deleted
	 */
	protected boolean afterDelete (boolean success)
	{
		return updateRemesa();
	}	//	afterDelete
	
	/**
	 *	Update Remesa
	 */
	private boolean updateRemesa()
	{
		//	Update Order Header
		String sql = "UPDATE C_Remesa "
			+ " SET TotalAmt="
			+ " (SELECT COALESCE(SUM(rl.LineNetAmt),0) FROM C_RemesaLine rl GROUP BY rl.C_Remesa_ID HAVING rl.C_Remesa_ID="+ getC_Remesa_ID()+")"
			//+ " FROM C_Remesa rm"
			+ " WHERE C_Remesa_ID=" + getC_Remesa_ID();
		int no = DB.executeUpdate(sql);
		log.fine("En updateRemesa, devolvio....:"+no);
		return no == 1;
	}	//	updateHeaderTax
	
	public void updateLineNetAmt()
	{
		
		MInvoicePaySchedule[] schedule = MInvoicePaySchedule.getInvoicePaySchedule(getCtx(), this);
		if (schedule.length == 0)
			log.info("Entro en updateLineNetAmt con schedule.length=0");
		//	setLineNetAmt(0);
		else
		{
			//	Add up due amounts
			BigDecimal total = Env.ZERO;
			for (int i = 0; i < schedule.length; i++)
			{
				log.fine("Entro en el for de updateLineNetAmt");
				if (!schedule[i].isProcessed())
				{
					BigDecimal due = schedule[i].getDueAmt();
					log.fine("Dentro del primer if del for, y due="+due);
					if (due != null)
						total = total.add(due);
				}
			}
			log.fine("Total en updateLineNetAmt es="+total);
			String sql = "UPDATE C_RemesaLine"
				+ " SET LineNetAmt=" + total
				+ " WHERE C_Remesa_ID=" + getC_Remesa_ID();
			int no = DB.executeUpdate(sql);
			log.fine("La sentencia SQL es:"+sql);
			log.fine("Y devolvio...="+no);
		}
		
		updateRemesa();
	}	
	
	/** Get RemesaLines from a Remesa */
	public static MRemesaLine[] getRemesaLines(Properties ctx, int m_C_Remesa_ID)
	{
		ArrayList list = new ArrayList();
		PreparedStatement pstmt = null;

		if (m_C_Remesa_ID != 0)
		{
			StringBuffer sql = new StringBuffer("SELECT * FROM C_RemesaLine rml");
				sql.append(" WHERE rml.C_Remesa_ID=").append(m_C_Remesa_ID); 
				//
			try
			{
				pstmt = DB.prepareStatement(sql.toString());
				
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					list.add (new MRemesaLine(ctx, rs));
				rs.close();
				pstmt.close();
				pstmt = null;
			}
			catch (Exception e)
			{
			}
		}	
		
		MRemesaLine[] retValue = new MRemesaLine[list.size()];
		list.toArray(retValue);
		return retValue;
	}	//	getRemesaLines
	
}	//	MRemesaLine
