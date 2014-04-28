package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_BPartner_Padron_BsAs;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;


public class ImportPadronBsAsFromCopy extends SvrProcess {

	/**
	 * Preference para el mantenimiento de la tabla de Padrón, para que no
	 * crezca demasiado, se define ésta que valoriza en meses los registros
	 * permanentes. Esto significa que si posee valor 1, entonces se guardarán
	 * los del mes anterior al actual, si posee valor 2, de dos meses hacia
	 * atrás, y así sucesivamente. El resto de registros anteriores se
	 * eliminarán.
	 */
	private static final String MANTENIMIENTO_PADRON = "MantenimientoPadron";
	
	private int p_AD_Org_ID = 0;
	private String p_NameCsvFile = null;
	private String p_PadronType = null;
	private int ad_Client_ID = 0;
	private int ad_User_ID = 0;
	
	/**	Client to be imported to		*/
	private String clientCheck = null;
	
	
	private int partnerFound = 0;
	private int regInserted = 0;
	private int regDeleted; 
	
	private StringBuffer sql;

	
	 
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "NameCsvFile" )) {
                p_NameCsvFile = ( String )para[ i ].getParameter();
            } else if( name.equals( "PadronType" )) {
                p_PadronType = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
		}
		ad_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		ad_User_ID = Env.getAD_User_ID(getCtx());
		setClientCheck(" AND AD_Client_ID=" + ad_Client_ID);
	}
	
	@Override
	protected String doIt() throws Exception {
		/** Se elimina el contenido de la tabla i_padron_sujeto_aux */
		sql = new StringBuffer();
		sql.append("DELETE FROM i_padron_sujeto_aux");
		DB.executeUpdate(sql.toString());
		
		/** Se copia el contenido del padrón a la tabla i_padron_sujeto_aux */
		sql = new StringBuffer();
		sql.append("COPY i_padron_sujeto_aux FROM '"+ getPath() + p_NameCsvFile + "' WITH DELIMITER '" + getSeparatorCharacterCSV() + "'");
		DB.executeUpdate(sql.toString());
		
		/** Se ejecuta el proceso de mantenimiento de padrón eliminando aquellos padrones que ya no se usan */
		regDeleted = maintainPadronTable();
		
		/** Se insertan los registros a la tabla c_bpartner_padron_bsas */
		insert();
		
		/** Se actualiza el campo c_bpartner_id de la tabla c_bpartner_padron_bsas*/
		updateCBPartner();

		log.log (Level.SEVERE,"doIt - Entidades Comerciales NO Encontradas =" + (regInserted - partnerFound));
		
		
		addLog (0, null, new BigDecimal (regDeleted), "Registros eliminados por Mantenimiento de Padron");
		addLog (0, null, new BigDecimal (partnerFound), "Entidades Comerciales Encontrados");
		addLog (0, null, new BigDecimal (regInserted - partnerFound), "Entidades Comerciales No Encontrados");
		addLog (0, null, new BigDecimal (regInserted), "Entidades Comerciales insertados");

		return "Proceso finalizado satisfactoriamente";
	}

	private void updateCBPartner() {
		sql = new StringBuffer();
		sql.append("UPDATE c_bpartner_padron_bsas i \n");
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
	}

	private String getPath(){
		/**
		 * El nombre de AD_Preference es PathPadronCSV
		 */
		String preference = MPreference.searchCustomPreferenceValue("PathPadronCSV", getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),Env.getAD_User_ID(getCtx()), true);
		if(Util.isEmpty(preference, true)){
			throw new IllegalArgumentException( "@PathPadronCSVNotFound@" ); 
			//Error al cargar la preferencia "PathPadronCSV"
		}
		return preference;
	}
	
	private String getSeparatorCharacterCSV(){
		/**
		 * El nombre de AD_Preference es SeparadorDeCampoEnCSVPadron
		 */
		String preference = MPreference.searchCustomPreferenceValue("SeparadorDeCampoEnCSVPadron", getAD_Client_ID(), Env.getAD_Org_ID(getCtx()),Env.getAD_User_ID(getCtx()), true);
		if(Util.isEmpty(preference, true)){
			throw new IllegalArgumentException( "@SeparadorDeCampoEnCSVPadronNotFound@" ); 
			//Error al cargar la preferencia "SeparadorDeCampoEnCSVPadron"
		}
		return preference;
	}
	
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
			throw e;
		}
		padron = padron * -1;
		// Eliminación de registros anteriores a los meses de tolerancia hacia atrás
		String sql = "DELETE FROM " + X_C_BPartner_Padron_BsAs.Table_Name
				+ " WHERE date_trunc('month',?::date) >= date_trunc('month',fecha_desde) AND padrontype = '" + p_PadronType + "' "+getClientCheck();
		Calendar toleranceDate = Calendar.getInstance();
		toleranceDate.setTimeInMillis(Env.getDate().getTime());
		toleranceDate.add(Calendar.MONTH, padron);
		PreparedStatement ps = new CPreparedStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql,
				null, true);
		ps.setTimestamp(1, new Timestamp(toleranceDate.getTimeInMillis()));
		return ps.executeUpdate();
	}
	
	protected String getClientCheck() {
		return clientCheck;
	}

	protected void setClientCheck(String clientCheck) {
		this.clientCheck = clientCheck;
	}
	
	/**
	 * Insertar registros desde la tabla i_padron_sujeto_aux a la
	 * tabla c_bpartner_padron_bsas
	 * 
	 * @throws Exception
	 */
	protected void insert(){
		sql = new StringBuffer();
		sql.append("INSERT \n");
		sql.append("INTO   c_bpartner_padron_bsas \n");
		sql.append("       ( \n");
		sql.append(" c_bpartner_padron_bsas_ID, \n");
		sql.append(" FECHA_PUBLICACION        , \n");
		sql.append(" FECHA_DESDE              , \n");
		sql.append(" FECHA_HASTA              , \n");
		sql.append(" CUIT                     , \n");
		sql.append(" TIPO_CONTR_INSC          , \n");
		sql.append(" ALTA_BAJA                , \n");
		sql.append(" CBIO_ALICUOTA            , \n");
		sql.append(" PERCEPCION               , \n");
		sql.append(" RETENCION                , \n");
		sql.append(" NRO_GRUPO_RET            , \n");
		sql.append(" NRO_GRUPO_PER            , \n");
		sql.append(" AD_CLIENT_ID             , \n");
		sql.append(" AD_ORG_ID                , \n");
		sql.append(" ISACTIVE                 , \n");
		sql.append(" CREATED                  , \n");
		sql.append(" UPDATED                  , \n");
		sql.append(" CREATEDBY                , \n");
		sql.append(" UPDATEDBY                , \n");
		sql.append(" padrontype                 \n");
		sql.append("                          ) \n");
		sql.append(" SELECT nextval('seq_c_bpartner_padron_bsas'),     \n");
		sql.append(" to_timestamp(FECHA_PUBLICACION, 'DDMMYYYY')     , \n");
		sql.append(" to_timestamp(FECHA_DESDE, 'DDMMYYYY')           , \n");
		sql.append(" to_timestamp(FECHA_HASTA, 'DDMMYYYY')           , \n");
		sql.append(" CUIT                                            , \n");
		sql.append(" TIPO_CONTR_INSC                                 , \n");
		sql.append(" ALTA_BAJA                                       , \n");
		sql.append(" CBIO_ALICUOTA                                   , \n");
		sql.append(" to_number(PERCEPCION, '9999999D99')             , \n");
		sql.append(" to_number(RETENCION, '9999999D99')              , \n");
		sql.append(" NRO_GRUPO_RET                                   , \n");
		sql.append(" NRO_GRUPO_PER                                   , \n");
		sql.append(" " + ad_Client_ID + "                            , \n");
		sql.append(" " + p_AD_Org_ID + "                             , \n");
		sql.append(" 'Y'                                             , \n");
		sql.append(" CURRENT_DATE                                    , \n");
		sql.append(" CURRENT_DATE                                    , \n");
		sql.append(" " + ad_User_ID + "                              , \n");
		sql.append(" " + ad_User_ID + "                              , \n");
		sql.append(" '" + p_PadronType + "'                                  ");
		sql.append(" FROM   i_padron_sujeto_aux                       \n ");
		/* 
		 * Se debe permitir insertar registros con CUITs iguales
		sql.append("WHERE cuit NOT IN \n");
		sql.append("(SELECT CUIT FROM c_bpartner_padron_bsas) ");
		*/		
		regInserted = DB.executeUpdate(sql.toString());
		log.log (Level.SEVERE,"doIt - Registros Pasados al sistema =" + regInserted);
	}

}
