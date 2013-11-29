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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_BPartner_Padron_BsAs;
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
	 * Preference para el mantenimiento de la tabla de Padrón, para que no
	 * crezca demasiado, se define ésta que valoriza en meses los registros
	 * permanentes. Esto significa que si posee valor 1, entonces se guardarán
	 * los del mes anterior al actual, si posee valor 2, de dos meses hacia
	 * atrás, y así sucesivamente. El resto de registros anteriores se
	 * eliminarán.
	 */
	private static final String MANTENIMIENTO_PADRON = "MantenimientoPadron";
	
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
	
	private String clientCheck = null;
	
	private boolean updateRecords = false;
	
	private String padronType = X_C_BPartner_Padron_BsAs.PADRONTYPE_PadrónBsAs;

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
			if (name.equals("PadronType"))
				padronType = ( String )para[ i ].getParameter();
			else
				log.log(Level.SEVERE,"prepare - Unknown Parameter: " + name);
		}
		
		m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		setClientCheck(" AND AD_Client_ID=" + m_AD_Client_ID);
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
		int regDeleted = 0;
		int noPadronType = 0;
		//	****	Prepare	****

		//	Delete Old Imported
		if (m_deleteOldImported)
		{
			sql = new StringBuffer ("DELETE I_Padron_Sujeto "
				  + "WHERE I_IsImported='Y'").append (getClientCheck());
			no = DB.executeUpdate (sql.toString ());
			log.log (Level.SEVERE,"doIt - Delete Old Imported =" + no);
		}
		
		// Mantenimiento de tabla Padron
		regDeleted = maintainPadronTable();
		
		try{
			 
			sql = new StringBuffer();
			sql.append("UPDATE i_padron_sujeto i \n");
			sql.append("SET     c_bpartner_id = \n");
			sql.append("       (SELECT b.c_bpartner_id \n");
			sql.append("       FROM    c_bpartner b \n");
			sql.append("       WHERE   REPLACE(b.taxid,'-','') = i.cuit \n");
			sql.append("       LIMIT 1) \n");
			sql.append("WHERE  cuit IN \n");
			sql.append("                ( SELECT REPLACE(taxid,'-','') \n");
			sql.append("                FROM    c_bpartner \n");
			sql.append("                )");
			partnerFound = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Entidades Comerciales Encontradas =" + partnerFound);
			
			sql = new StringBuffer();
			sql.append("UPDATE i_padron_sujeto \n");
			sql.append("SET    i_errormsg = 'Entidad Comercial NO encontrada en el sistema' \n");
			sql.append("WHERE  c_bpartner_id IS NULL AND i_isimported = 'N' ").append(getClientCheck());
			partnerNotFound = DB.executeUpdate(sql.toString());
			log.log (Level.SEVERE,"doIt - Entidades Comerciales NO Encontradas =" + partnerNotFound);
			
			sql = new StringBuffer();
			sql.append("UPDATE i_padron_sujeto \n");
			sql.append("SET    padrontype = '"+getPadronType()+"' \n");
			sql.append("WHERE  padrontype IS NULL AND i_isimported = 'N' ").append(getClientCheck());
			noPadronType = DB.executeUpdate(sql.toString());

	        if( noPadronType != 0 ) {
	            log.warning( "Invalid Padron Type=" + no );
	        }
			
			
			if(isUpdateRecords()){
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
				log.log(Level.SEVERE,"doIt - Registros actualizados en el sistema =" + regUpdated);
			}
			
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
			sql.append("              C_BPARTNER_ID 		   , \n");
			sql.append("              padrontype \n");
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
			sql.append("       C_BPARTNER_ID 					 , \n");
			sql.append("       PADRONTYPE                            ");
			sql.append(" FROM   i_padron_sujeto \n ");
			sql.append(" WHERE i_isimported = 'N' \n ");
			sql.append(getClientCheck()+" \n ");
			/* 
			 * Se debe permitir insertar registros con CUITs iguales
			sql.append("WHERE cuit NOT IN \n");
			sql.append("(SELECT CUIT FROM c_bpartner_padron_bsas) ");
			*/
			
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
			throw ex;
		}
		
		addLog (0, null, new BigDecimal (regDeleted), "Registros eliminados por Mantenimiento de Padron");
		addLog (0, null, new BigDecimal (partnerFound), "Entidades Comerciales Encontrados");
		addLog (0, null, new BigDecimal (partnerNotFound), "Entidades Comerciales No Encontrados");
		addLog (0, null, new BigDecimal (regUpdated), "Entidades Comerciales actualizados");
		addLog (0, null, new BigDecimal (regInserted), "Entidades Comerciales insertados");

		return "Proceso finalizado satisfactoriamente";
	}	//	doIt
	
	/**
	 * Mantenimiento de la tabla de padron para que no crezca en cada
	 * importación ya que no debe actualizar por importaciones que pisen por
	 * cuit los datos actuales
	 * 
	 * @throws Exception
	 */
	protected int maintainPadronTable() throws Exception{
		String updatePadron = MPreference.searchCustomPreferenceValue(
				MANTENIMIENTO_PADRON, getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),
				Env.getAD_User_ID(getCtx()), true);
		Integer padron = 0;
		try {
			padron = Integer.parseInt(updatePadron);
		} catch (Exception e) {
			throw new Exception("La preferencia o valor predeterminado "
					+ MANTENIMIENTO_PADRON
					+ " no esta existo o contiene un valor no numerico");
		}
		padron = padron * -1;
		// Eliminación de registros anteriores a los meses de tolerancia hacia atrás
		String sql = "DELETE FROM " + X_C_BPartner_Padron_BsAs.Table_Name
				+ " WHERE date_trunc('month',?::date) >= date_trunc('month',fecha_desde) AND padrontype = '"
				+ X_C_BPartner_Padron_BsAs.PADRONTYPE_PadrónBsAs + "' "+getClientCheck();
		Calendar toleranceDate = Calendar.getInstance();
		toleranceDate.setTimeInMillis(Env.getDate().getTime());
		toleranceDate.add(Calendar.MONTH, padron);
		PreparedStatement ps = new CPreparedStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql,
				get_TrxName(), true);
		ps.setTimestamp(1, new Timestamp(toleranceDate.getTimeInMillis()));
		return ps.executeUpdate();
	}


	protected boolean isUpdateRecords() {
		return updateRecords;
	}


	protected void setUpdateRecords(boolean updateRecords) {
		this.updateRecords = updateRecords;
	}


	protected String getPadronType() {
		return padronType;
	}


	protected void setPadronType(String padronType) {
		this.padronType = padronType;
	}


	protected String getClientCheck() {
		return clientCheck;
	}


	protected void setClientCheck(String clientCheck) {
		this.clientCheck = clientCheck;
	}

}	//	ImportPadronBsAs
