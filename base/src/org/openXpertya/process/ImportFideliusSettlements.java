package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MExternalService;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_I_FideliusLiquidaciones;
import org.openXpertya.process.customImport.fidelius.jobs.Import;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación de liquidaciones desde tablas temporales.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFideliusSettlements extends SvrProcess {

	private static final String SAVED = "Guardado correctamente";
	private static final String ERROR = "Con errores";
	private static final String IGNORED = "Ignorado";
	private static final String TIPOIMPORTACION = "Fidelius";

	/** Eliminar registros importados previamente. */
	private boolean m_deleteOldImported = true;
	/** Tipo de tarjeta. */
	private String p_CreditCardType;
	/** Entidad Financiera */
	private int p_M_EntidadFinanciera_ID = 0;
	/** Entidad Comercial */
	private int p_C_BPartner_ID = 0;
	/** Fecha (rango) */
	private Timestamp p_FromFecha = null;
	private Timestamp p_ToFecha = null;

	private Map<Integer, Integer> orgIDForBP = new HashMap<Integer, Integer>();

	@Override
	protected void prepare() {
		ProcessInfoParameter[] params = getParameter();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getParameterName();
			if (params[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("Fecha")) {
				p_FromFecha = (Timestamp) params[i].getParameter();
				p_ToFecha = (Timestamp) params[i].getParameter_To();
			} else if (name.equalsIgnoreCase("EntidadFinanciera")) {
				p_M_EntidadFinanciera_ID = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("C_BPartner_ID")) {
				p_C_BPartner_ID = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("TipoTarjeta")) {
				p_CreditCardType = (String) params[i].getParameter();
			} else if (name.equals("DeleteOldImported")) {
				m_deleteOldImported = "Y".equals(params[i].getParameter());
			} else {
				log.log(Level.SEVERE, "Fidelius ImportSettlements.prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		
		/**
		 * Fidelius NO permite filtrar por Tarjeta, trae todas las tarjetas e identifica en cada
		 * registro a que tarjeta pertenece la liquidacion.
		 * 
		 * El servicio externo se configura como "Fidelius"
		*/
		
		// Fidelius trae todas las tarjetas juntas, por ende no se filtra por una determinada
		// tarjeta, sino que trae todas las liquidaciones...
		Import importClass = Import.get(getCtx(), TIPOIMPORTACION, get_TrxName());
		if(importClass == null) {
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "InvalidCreditCardTypeParam"));
		}
		
		// Delete Old Imported
		if (m_deleteOldImported) {
			StringBuffer sql = new StringBuffer();
			int no = 0;

			sql.append("DELETE ");
			sql.append("	" + X_I_FideliusLiquidaciones.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	I_IsImported = 'Y' ");

			no = DB.executeUpdate(sql.toString());
			log.fine("Delete Old Imported = " + no);
		}

		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		String resultMsg = null;
		ResultSet rs = null;

		// Selecciono las columnas a importar, de la tabla correspondiente.
		sql.append("SELECT ");
		for (int i = 0; i < importClass.getFilteredFields().length; i++) {
			String field = importClass.getFilteredFields()[i];
			sql.append("	" + field + ", ");
		}
		sql.append("	" + importClass.getTableName() + "_ID ");

		sql.append("FROM ");
		sql.append("	" + importClass.getTableName() + " i");
		
// sql.append(" LEFT JOIN M_EntidadFinanciera ef ON ef.EstablishmentNumber = i.num_com ");
// dREHER el numero de comercio puede estar relacionado a varias entidades financieras, buscar de otra
// manera a la hora de filtrar
		
		sql.append(" 	WHERE i_isimported = 'N'");
		
		// Si llega el parametro, filtrar la entidad financiera (comercio) correspondiente
		if(p_M_EntidadFinanciera_ID > 0) {
			sql.append(" AND i.num_com IN (SELECT ef.EstablishmentNumber FROM "
											+ " M_EntidadFinanciera ef "
											+ " WHERE ef.M_EntidadFinanciera_ID=" + p_M_EntidadFinanciera_ID  + " AND ef.IsActive='Y')");
		}
		
		// Si llega el parametro, filtrar la entidad financiera (comercio) correspondiente
		if(p_C_BPartner_ID > 0) {
			sql.append(" AND i.num_com IN (SELECT ef.EstablishmentNumber FROM "
											+ " M_EntidadFinanciera ef "
											+ " WHERE ef.C_BPartner_ID=" + p_C_BPartner_ID  + " AND ef.IsActive='Y')");
		}
		
		int C_ExternalService_ID = getExternalServiceByName(importClass.getServiceName());
		MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
		Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();
		
		// Si llega el parametro, filtrar el tipo de tarjeta correspondiente
		if(p_CreditCardType != null && !p_CreditCardType.isEmpty()) {
			String tarjeta = "";
			
			ArrayList<X_C_ExternalServiceAttributes> attr = Utilidades.getDataExtraSEFromTipo(C_ExternalService_ID, "Tipo Tarjeta", get_TrxName());

			for(X_C_ExternalServiceAttributes ex : attr) {
				if(ex.getValue().equals(p_CreditCardType))
					tarjeta += (!tarjeta.isEmpty()?",":"") + "'" + ex.getName().trim() + "'";
			}

			if(tarjeta!=null && !tarjeta.isEmpty())
				sql.append(" AND i.Tarjeta IN (" + tarjeta + ")");	
		}
		
		sql.append(" AND fpag::DATE BETWEEN ?::DATE AND ?::DATE");

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setTimestamp(1, p_FromFecha);
			ps.setTimestamp(2, p_ToFecha);
			
			rs = ps.executeQuery();

			// Un posible resultado es guardado, ignorado, o error. Luego con
			// esta información se emitirá un mensaje tras finalizar el proceso.
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put(SAVED, 0);
			result.put(IGNORED, 0);
			result.put(ERROR, 0);

			boolean someResults = false;
			boolean resultImport;
			
			StringBuilder idOk = new StringBuilder();
			StringBuilder idError = new StringBuilder();
			
			int vez = 0;
			while (rs.next()) {
				someResults = true;
				log.finest("Evalua liquidacion del comercio: " + rs.getString("num_com") + " Liquidacion:" + rs.getString("nroliq"));
				try {
					
					// Validar los datos
					importClass.validate(getCtx(), rs, attributes, get_TrxName(), vez);
					
					// Realizar la creación o modificación de la liquidación
					resultImport = importClass.create(getCtx(), rs, attributes, get_TrxName());
					
					// Registrar éxito
					resultMsg = resultImport?SAVED:IGNORED;
					/**
						markAsImported(importClass.getTableName(), rs.getInt(importClass.getTableName() + "_ID")); 
					*/
					
					if(idOk.length()>0)
						idOk.append(",");
					idOk.append(rs.getInt(importClass.getTableName() + "_ID"));
					
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { importClass.getServiceName(), result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				} catch (Exception e) {
					/**markAsError(importClass.getTableName(), rs.getInt(importClass.getTableName() + "_ID"),
							e.getMessage());*/
					
					idError.append("UPDATE " + importClass.getTableName() +
									" SET i_errormsg = '" + e.getMessage() + "', " +
									" processed = 'Y' " +
									" WHERE " + importClass.getTableName() + "_ID=" +
									rs.getInt(importClass.getTableName() + "_ID") + ";");
					
					result.put(ERROR, result.get(ERROR) + 1);
					Object[] params = new Object[] { importClass.getServiceName(), result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
				
				vez++;
				
			}
			
			
			if (!someResults) {
				resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationNoResults");
			}else {
				markAsImported(importClass.getTableName(), idOk);
				markAsError(idError);
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "ImportSettlements.doIt", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return resultMsg;
	}

	/**
	 * Marca un registro de la tabla de importación correspondiente, como importado.
	 * @param tableName Nombre de la tabla sobre la cual se realizará una actualización.
	 * @param id Clave primaria del registro a actualizar.
	 */
	private void markAsImported(String tableName, int id) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + tableName + " ");
		sql.append("SET ");
		sql.append("	i_isimported = 'Y', ");
		sql.append("	processed = 'Y', ");
		sql.append("	i_errormsg = null ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

		DB.executeUpdate(sql.toString(), get_TrxName());
	}
	
	/**
	 * Marca un registro de la tabla de importación correspondiente, como importado.
	 * @param tableName Nombre de la tabla sobre la cual se realizará una actualización.
	 * @param sb StringBuilder con todas las Claves primarias de los registros a actualizar.
	 * 
	 * dREHER hace un solo update para todos los registros importados Ok
	 */
	private void markAsImported(String tableName, StringBuilder sb) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + tableName + " ");
		sql.append("SET ");
		sql.append("	i_isimported = 'Y', ");
		sql.append("	processed = 'Y', ");
		sql.append("	i_errormsg = null ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID IN (" + sb.toString() + ") ");

		DB.executeUpdate(sql.toString(), get_TrxName());
	}
	
	/**
	 * Marca un registro de la tabla de importación correspondiente, como con error.
	 * @param tableName Nombre de la tabla sobre la cual se realizará una actualización.
	 * @param id Clave primaria del registro a actualizar.
	 */
	private void markAsError(String tableName, int id, String errorMsg) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + tableName + " ");
		sql.append("SET ");
		sql.append("	i_errormsg = '" + errorMsg + "', ");
		sql.append("	processed = 'Y' ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

		DB.executeUpdate(sql.toString(), get_TrxName());
	}
	
	/**
	 * Marca un registro de la tabla de importación correspondiente, como con error.
	 * @param sb satringBuilder con todas las sentencias UPDATEs...
	 * dREHER
	 */
	private void markAsError(StringBuilder sql) {
		DB.executeUpdate(sql.toString(), get_TrxName());
	}

	/**
	 * Obtiene un objeto de configuración de servicios externos.
	 * @param name Nombre por el cual buscar el registro.
	 * @return ID del registro.
	 */
	private int getExternalServiceByName(String name) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		return DB.getSQLValue(get_TrxName(), sql.toString(), name);
	}
	
	protected Map<Integer, Integer> getOrgIDForBP() {
		return orgIDForBP;
	}

	protected void setOrgIDForBP(Map<Integer, Integer> orgIDForBP) {
		this.orgIDForBP = orgIDForBP;
	}
}

