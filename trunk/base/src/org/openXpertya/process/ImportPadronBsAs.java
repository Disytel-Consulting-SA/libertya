/******************************************************************************
 *     El contenido de este fichero está sujeto a la Licencia Pública openXpertya versión 1.0 (LPO) en
 * tanto cuanto forme parte íntegra del total del producto denominado:     openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *     Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *     Partes del código son CopyRight © 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son CopyRight  © 2003-2005 de Consultoría y Soporte en Redes y  Tecnologías de 
 * la  Información S.L., otras partes son adaptadas, ampliadas o mejoradas a partir de código original
 * de terceros, recogidos en el ADDENDUM A, sección 3 (A.3) de dicha licencia LPO, y si dicho código
 * es extraido como parte del total del producto, estará sujeto a sus respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/licencia.html
 ******************************************************************************/

package org.openXpertya.process;

import java.math.*;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

/**
 *	Import Order from I_Order
 *
 * 	@author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright © 	Jorg Janke
 * 	@version 	$Id: ImportOrder.java,v 0.9 $
 */
public class ImportPadronBsAs extends SvrProcess
{
	/**
	 * 	Import BPartner Constructor
	 */
	public ImportPadronBsAs()
	{
		super();
	}	//	ImportPadronBsAs

	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Organization to be imported to		*/
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				m_deleteOldImported = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE,"prepare - Unknown Parameter: " + name);
		}
		
		m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		StringBuffer sql = null;
		int no = 0;
		int partnerFound = 0;
		int partnerNotFound = 0;
		int regInserted = 0;
		int regUpdated = 0;
		String clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

		//	****	Prepare	****

		//	Delete Old Imported
		if (m_deleteOldImported)
		{
			sql = new StringBuffer ("DELETE I_Padron_Sujeto "
				  + "WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate (sql.toString ());
			log.log (Level.SEVERE,"doIt - Delete Old Imported =" + no);
		}
		
		try{
			sql = new StringBuffer();
			sql.append("UPDATE i_padron_sujeto i \n");
			sql.append("SET     c_bpartner_id = \n");
			sql.append("       (SELECT b.c_bpartner_id \n");
			sql.append("       FROM    c_bpartner b \n");
			sql.append("       WHERE   REPLACE(b.taxid,'-','') = i.cuit \n");
			sql.append("       ) \n");
			sql.append("WHERE  cuit IN \n");
			sql.append("                ( SELECT REPLACE(taxid,'-','') \n");
			sql.append("                FROM    c_bpartner \n");
			sql.append("                )");
			partnerFound = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Proveedores Encontrados =" + partnerFound);
			
			sql = new StringBuffer();
			sql.append("UPDATE i_padron_sujeto \n");
			sql.append("SET    i_errormsg = 'Proveedor NO encontrado en el sistema' \n");
			sql.append("WHERE  c_bpartner_id IS NULL");
			partnerNotFound = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Proveedores NO Encontrados =" + partnerNotFound);
			
			sql = new StringBuffer();
			sql.append("UPDATE c_bpartner_padron_bsas b \n");
			sql.append("SET    FECHA_PUBLICACION = \n");
			sql.append("       (SELECT FECHA_PUBLICACION \n");
			sql.append("        FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       FECHA_DESDE = \n");
			sql.append("       (SELECT FECHA_DESDE \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       FECHA_HASTA = \n");
			sql.append("       (SELECT FECHA_HASTA \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       TIPO_CONTR_INSC = \n");
			sql.append("       (SELECT TIPO_CONTR_INSC \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       ALTA_BAJA = \n");
			sql.append("       (SELECT ALTA_BAJA \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       CBIO_ALICUOTA = \n");
			sql.append("       (SELECT CBIO_ALICUOTA \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       PERCEPCION = \n");
			sql.append("       (SELECT PERCEPCION \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       RETENCION = \n");
			sql.append("       (SELECT RETENCION \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       NRO_GRUPO_RET = \n");
			sql.append("       (SELECT NRO_GRUPO_RET \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit), \n");
			sql.append("       NRO_GRUPO_PER = \n");
			sql.append("       (SELECT NRO_GRUPO_PER \n");
			sql.append("       FROM    i_padron_sujeto WHERE   cuit = b.cuit) \n");
			sql.append("WHERE  cuit IN \n");
			sql.append("                ( SELECT cuit \n");
			sql.append("                FROM    i_padron_sujeto \n");
			sql.append("                )");		
			regUpdated = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Registros actualizados en el sistema =" + regUpdated);
			
			sql = new StringBuffer();
			sql.append("INSERT \n");
			sql.append("INTO   c_bpartner_padron_bsas \n");
			sql.append("       ( \n");
			sql.append("              c_bpartner_padron_bsas_ID, \n");
			sql.append("              FECHA_PUBLICACION        , \n");
			sql.append("              FECHA_DESDE              , \n");
			sql.append("              FECHA_HASTA              , \n");
			sql.append("              CUIT                     , \n");
			sql.append("              TIPO_CONTR_INSC          , \n");
			sql.append("              ALTA_BAJA                , \n");
			sql.append("              CBIO_ALICUOTA            , \n");
			sql.append("              PERCEPCION               , \n");
			sql.append("              RETENCION                , \n");
			sql.append("              NRO_GRUPO_RET            , \n");
			sql.append("              NRO_GRUPO_PER            , \n");
			sql.append("              AD_CLIENT_ID             , \n");
			sql.append("              AD_ORG_ID                , \n");
			sql.append("              ISACTIVE                 , \n");
			sql.append("              CREATED                  , \n");
			sql.append("              UPDATED                  , \n");
			sql.append("              CREATEDBY                , \n");
			sql.append("              UPDATEDBY                , \n");
			sql.append("              C_BPARTNER_ID \n");
			sql.append("       ) \n");
			sql.append("SELECT nextval('seq_c_bpartner_padron_bsas'), \n");
			sql.append("       FECHA_PUBLICACION                 , \n");
			sql.append("       FECHA_DESDE                       , \n");
			sql.append("       FECHA_HASTA                       , \n");
			sql.append("       CUIT                              , \n");
			sql.append("       TIPO_CONTR_INSC                   , \n");
			sql.append("       ALTA_BAJA                         , \n");
			sql.append("       CBIO_ALICUOTA                     , \n");
			sql.append("       PERCEPCION                        , \n");
			sql.append("       RETENCION                         , \n");
			sql.append("       NRO_GRUPO_RET                     , \n");
			sql.append("       NRO_GRUPO_PER                     , \n");
			sql.append("       AD_CLIENT_ID                      , \n");
			sql.append("       AD_ORG_ID                         , \n");
			sql.append("       ISACTIVE                          , \n");
			sql.append("       CURRENT_DATE                      , \n");
			sql.append("       CURRENT_DATE                      , \n");
			sql.append("       CREATEDBY                         , \n");
			sql.append("       UPDATEDBY                         , \n");
			sql.append("       C_BPARTNER_ID \n");
			sql.append("FROM   i_padron_sujeto \n");
			sql.append("WHERE cuit NOT IN \n");
			sql.append("(SELECT CUIT FROM c_bpartner_padron_bsas) ");
			
			regInserted = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Registros Pasados al sistema =" + regInserted);
			
			if(regInserted != -1 && regUpdated != -1)
			{
				sql = new StringBuffer();
				sql.append("UPDATE i_padron_sujeto \n");
				sql.append("SET    i_isimported = 'Y' , processed = 'Y' \n");
				DB.executeUpdate(sql.toString());				
			}
			
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE,"Ocurrio el siguiente error: "+ex.toString());
		}
		
//		addLog (0, null, new BigDecimal (no), "Eliminados");
		addLog (0, null, new BigDecimal (partnerFound), "Proveedodres OPX Encontrados");
		addLog (0, null, new BigDecimal (partnerNotFound), "Proveedores OPX No Encontrados");
		addLog (0, null, new BigDecimal (regUpdated), "Proveedores del padron actualizados");
		addLog (0, null, new BigDecimal (regInserted), "Proveedores nuevos en el padron");

		return "Proceso finalizado satisfactoriamente";
	}	//	doIt

}	//	ImportPadronBsAs
