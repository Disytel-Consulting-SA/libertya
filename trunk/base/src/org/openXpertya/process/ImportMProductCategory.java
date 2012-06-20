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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MProductCategory;
import org.openXpertya.model.X_I_Conversion_Rate;
import org.openXpertya.model.X_I_MProduct_Category;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ImportMProductCategory extends SvrProcess
{
	/**	Client to be imported to		*/
	private int 			p_AD_Client_ID = 0;
	/**	Delete old Imported			*/
	private boolean			p_DeleteOldImported = false;
	/**	Import						*/
	private int				p_I_MProductCategory_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Client_ID"))
				p_AD_Client_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("DeleteOldImported"))
				p_DeleteOldImported = "Y".equals(para[i].getParameter());
			else
				log.fine("prepare - Unknown Parameter: " + name);
		}
		p_I_MProductCategory_ID = getRecord_ID();
	}	//	prepare

	/**
	 * 	doIt
	 *	@return
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		log.info("doIt");
		StringBuffer sql = null;
		int no = 0;
		String clientCheck = " AND AD_Client_ID=" + p_AD_Client_ID;

		//	Delete Old Imported
		if (p_DeleteOldImported)
		{
			sql = new StringBuffer ("DELETE I_MProduct_Category "
				  + "WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate (sql.toString ());
			log.fine ("doIt - Delete Old Imported =" + no);
		}
		//	Set IsActive, Created/Updated
		sql = new StringBuffer ("UPDATE I_MProduct_Category "
			+ "SET IsActive = COALESCE (IsActive, 'Y'),"
			+ " Created = COALESCE (Created, SysDate),"
			+ " CreatedBy = COALESCE (CreatedBy, 0),"
			+ " Updated = COALESCE (Updated, SysDate),"
			+ " UpdatedBy = COALESCE (UpdatedBy, 0),"
			//+ " I_ErrorMsg = NULL,"
			+ " I_IsImported = 'N' "
			+ "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
		no = DB.executeUpdate (sql.toString ());
		log.info ("doIt - Reset=" + no);

		//	Set Client from Name
		sql = new StringBuffer ("UPDATE I_MProduct_Category i "
			+ "SET AD_Client_ID=COALESCE (AD_Client_ID,").append (p_AD_Client_ID).append (") "
			+ "WHERE (AD_Client_ID IS NULL OR AD_Client_ID=0)"
			+ " AND I_IsImported<>'Y'");
		no = DB.executeUpdate (sql.toString ());
		log.fine("doIt - Set Client from Value=" + no);

		//	Error Confirmation Line
		sql = new StringBuffer ("UPDATE I_MProduct_Category i "
			+ "SET I_IsImported='E' "
			+ "WHERE (I_MProduct_Category_ID IS NULL OR I_MProduct_Category_ID=0 "
			//Modificado por ConSerTi, antes OR NOT EXISTS (SELECT * FROM M_Product_Category c WHERE i.Name =c.Name))
			+ " OR EXISTS (SELECT * FROM M_Product_Category c WHERE i.Name =c.Name))"
			+ " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate (sql.toString ());
		if (no != 0)
			log.fine ("doIt - Invalid Category=" + no);

		//	Error Confirmation No
		sql = new StringBuffer ("UPDATE I_MProduct_Category i "
			+ "SET I_IsImported='E'"
			+ "WHERE (Name IS NULL OR Name='')"
			+ " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate (sql.toString ());
		if (no != 0)
			log.fine ("doIt - Invalid ConfirmationNo=" + no);

		PreparedStatement pstmt = null;
		sql = new StringBuffer ("SELECT * FROM I_MProduct_Category "
			+ "WHERE I_IsImported='N'")
			.append(" ORDER BY I_MProduct_Category_ID");
		no = 0;
		try
		{
			pstmt = DB.prepareStatement (sql.toString());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				X_I_MProduct_Category importCategory = new X_I_MProduct_Category(getCtx(), rs.getInt("I_MProduct_Category_ID"),rs.getString("Name"));
				MProductCategory category = new MProductCategory (getCtx(),0, null);
					//importCategory.getI_Category_ID());
				category.setName(importCategory.getName());
				category.setValue(importCategory.getValue());
				category.setDescription(importCategory.getDescription());
				category.setM_Product_Gamas_ID(importCategory.getM_Product_Gamas_ID());
				//category.setConfirmationNo(importCategory.getConfirmationNo());
				//category.setConfirmedQty(importCategory.getConfirmedQty());
				//category.setDifferenceQty(importCategory.getDifferenceQty());
			//	category.setScrappedQty(importCategory.getScrappedQty());
				if (category.save())
				{
					//	Import
					importCategory.setI_IsImported(true);
					if (importCategory.save())
						no++;

				}
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			//log.fine ("doIt", e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}

		return "@Inserted@ #" + no;
	}	//	doIt

}	//	ImportInOutConfirm


/*
 *  @(#)ImportConversionRate.java   02.07.07
 *
 *  Fin del fichero ImportConversionRate.java
 *
 *  Versión 2.2
 *
 */
