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

import java.sql.*;
import java.util.*;

import org.openXpertya.model.X_C_Remesa;
import org.openXpertya.util.*;

/**
 *
 *
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Pablo Menendez, Conserti.
 * 
 *  @version $Id: MRemesa.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */
public class MRemesa extends X_C_Remesa
{
	/**************************************************************************
	 *  Default Constructor
	 *  @param ctx context
	 *  @param  C_Order_ID    order to load, (0 create new order)
	 */
	public MRemesa(Properties ctx, int C_Order_ID,String trxName)
	{
		super (ctx, C_Order_ID, trxName);
		//  New
		if (C_Order_ID == 0)
		{
			setTotalAmt (Env.ZERO);
		}
	}	//	MRemesa

	
	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 */
	public MRemesa (Properties ctx, ResultSet rs)
	{
		super (ctx, rs,null);
	}	//	MRemesa
	
	/**************************************************************************
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MRemesa[")
			.append(getID()).append("-").append(getDescription())
			.append(",Norma=").append(getC_Norma_ID())
			.append(",TotalAmt=").append(getTotalAmt())
			.append(",ExecuteDate=").append(getExecuteDate())
			.append ("]");
		return sb.toString ();
	}	//	toString

	
	/**************************************************************************
	 * 	Lineas de Remesa
	 * 	@param whereClause where clause or null (starting with AND)
	 * 	@return lines
	 */
	public MRemesaLine[] getLines (String whereClause, String orderClause)
	{
		ArrayList list = new ArrayList ();
		StringBuffer sql = new StringBuffer("SELECT * FROM C_RemesaLine WHERE C_Remesa_ID=? ");
		if (whereClause != null)
			sql.append(whereClause);
		if (orderClause != null)
			sql.append(" ").append(orderClause);
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, getC_Remesa_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MRemesaLine(getCtx(), rs));
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.saveError("getLines - " + sql, e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}
		//
		MRemesaLine[] lines = new MRemesaLine[list.size ()];
		list.toArray (lines);
		return lines;
	}	//	getLines

	/**
	 * 	Cuenta l�neas
	 *	@return n�mero de l�neas
	 */
	public int getCuentaLineas ()
	{
		MRemesaLine[] lines = getLines(null, null);
		return lines.length;

	}	//	getCuentaLineas
	
	 /** 	Set Processed.
	 * 	Propergate to Lines/Taxes
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		if (getID() == 0)
			return;
		String set = "SET C_Remesa_ID='"
			+ getC_Remesa_ID()
			+ "' WHERE ";
		log.fine("En MRemesa la sql q ejecuta es: UPDATE C_InvoicePaySchedule"+set);
		int noLine = DB.executeUpdate("UPDATE C_InvoicePaySchedule " + set, get_TrxName());
	}	//	setProcessed
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		updateRemesa();
		return true;
	}	//	beforeSave
	
	
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success || newRecord)
			return success;
		
		return true;
	}	//	afterSave

	/**
	 *	Update Remesa
	 */
	private boolean updateRemesa()
	{
		//	Update Remesa
		String sql = "UPDATE C_Remesa "
			+ " SET TotalAmt="
			+ " (SELECT COALESCE(SUM(rl.LineNetAmt),0) FROM C_RemesaLine rl GROUP BY rl.C_Remesa_ID HAVING rl.C_Remesa_ID="+ getC_Remesa_ID()+")"
			//+ " FROM C_Remesa rm"
			+ " WHERE C_Remesa_ID=" + getC_Remesa_ID();
		int no = DB.executeUpdate(sql);

		return no == 1;
	}	//	updateHeaderTax

}	//	MOrder


