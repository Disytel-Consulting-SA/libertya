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
import org.openXpertya.model.MCity;
import org.openXpertya.model.X_I_C_City;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ImportCity extends SvrProcess
{
	/**	Client to be imported to		*/
	private int 			p_AD_Client_ID = 0;
	/**	Delete old Imported			*/
	private boolean			p_DeleteOldImported = false;
	/**	Import						*/
	private int				p_I_C_City_ID = 0;

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
		p_I_C_City_ID = getRecord_ID();
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
			sql = new StringBuffer ("DELETE I_C_City "
				  + "WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate (sql.toString ());
			log.fine ("doIt - Delete Old Imported =" + no);
		}
		//	Set IsActive, Created/Updated
		sql = new StringBuffer ("UPDATE I_C_City "
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
		sql = new StringBuffer ("UPDATE I_C_City i "
			+ "SET AD_Client_ID=COALESCE (AD_Client_ID,").append (p_AD_Client_ID).append (") "
			+ "WHERE (AD_Client_ID IS NULL OR AD_Client_ID=0)"
			+ " AND I_IsImported<>'Y'");
		no = DB.executeUpdate (sql.toString ());
		log.fine("doIt - Set Client from Value=" + no);

		//	Error Confirmation Line
		sql = new StringBuffer ("UPDATE I_C_City i "
			+ "SET I_IsImported='E' "
			+ "WHERE (I_C_City_ID IS NULL OR I_C_City_ID=0 "
			//Modificado por Conserti
			+ " OR EXISTS (SELECT * FROM C_City c WHERE i.Name =c.Name))"
			+ " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate (sql.toString ());
		//if (no != 0)
			log.fine ("doIt - Invalid City=" + no);

		//	Error Confirmation No
		sql = new StringBuffer ("UPDATE I_C_City i "
			+ "SET I_IsImported='E'"
			+ "WHERE (Name IS NULL OR Name='')"
			+ " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate (sql.toString ());
		//if (no != 0)
			log.fine ("doIt - Invalid ConfirmationNo=" + no);

		PreparedStatement pstmt = null;
		sql = new StringBuffer ("SELECT I_C_City_ID,Name FROM I_C_City "
			+ "WHERE I_IsImported='N'")
			.append(" ORDER BY I_C_City_ID");
		no = 0;
		try
		{
			pstmt = DB.prepareStatement (sql.toString());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				log.fine("Llega al paso1");
				//X_I_C_City importCity = new X_I_C_City(getCtx(), rs.getInt("I_C_City_ID"),rs.getString("Name"));
				X_I_C_City importCity = new X_I_C_City(getCtx(), rs.getInt("I_C_City_ID"),null);
				log.fine("paso2");
				MCity city = new MCity (getCtx(),0, null);
					//importCategory.getI_Category_ID());
				log.fine("paso3");
				city.setName(importCity.getName());
				log.fine("paso4");
				city.setPostal(importCity.getPostal());
				log.fine("paso5");
				city.setC_Country_ID(importCity.getC_Country_ID());
				city.setC_Region_ID(importCity.getC_Region_ID());
				log.fine("paso6");
				if (city.save())
				{
					log.fine("llego hasta aqui");
					importCity.setI_IsImported(true);
					if (importCity.save())
						no++;

				}
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log( Level.SEVERE,"En doIt, ERRORR",e );
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
 *  @(#)ImportCity.java   02.07.07
 *
 *  Fin del fichero ImportConversionRate.java
 *
 *  Versión 2.2
 *
 */
