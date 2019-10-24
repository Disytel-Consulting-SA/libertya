package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MExternalService;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.process.customImport.centralPos.jobs.Import;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación de liquidaciones desde tablas temporales.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportSettlements extends SvrProcess {

	private static final String SAVED = "Guardado correctamente";
	private static final String ERROR = "Con errores";
	private static final String IGNORED = "Ignorado";

	/** Eliminar registros importados previamente. */
	private boolean m_deleteOldImported = true;
	/** Tipo de tarjeta. */
	private String p_CreditCardType;

	private Map<Integer, Integer> orgIDForBP = new HashMap<Integer, Integer>();

	@Override
	protected void prepare() {
		ProcessInfoParameter[] params = getParameter();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getParameterName();
			if (params[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("CreditCardType")) {
				p_CreditCardType = (String) params[i].getParameter();
			} else if (name.equals("DeleteOldImported")) {
				m_deleteOldImported = "Y".equals(params[i].getParameter());
			} else {
				log.log(Level.SEVERE, "ImportSettlements.prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		Import importClass = Import.get(getCtx(), p_CreditCardType, get_TrxName());
		if(importClass == null) {
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "InvalidCreditCardTypeParam"));
		}
		
		// Delete Old Imported
		if (m_deleteOldImported) {
			StringBuffer sql = new StringBuffer();
			int no = 0;

			sql.append("DELETE ");
			sql.append("	" + importClass.getTableName() + " ");
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
		sql.append("	" + importClass.getTableName() + " ");
		sql.append("WHERE i_isimported = 'N'");

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			// Un posible resultado es guardado, ignorado, o error. Luego con
			// esta información se emitirá un mensaje tras finalizar el proceso.
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put(SAVED, 0);
			result.put(IGNORED, 0);
			result.put(ERROR, 0);

			boolean someResults = false;
			boolean resultImport;
			
			int C_ExternalService_ID = getExternalServiceByName(importClass.getServiceName());
			MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
			Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();
			
			while (rs.next()) {
				someResults = true;
				try {
					// Validar los datos
					importClass.validate(getCtx(), rs, attributes, get_TrxName());
					// Realizar la creación o modificación de la liquidación
					resultImport = importClass.create(getCtx(), rs, attributes, get_TrxName());
					// Registrar éxito
					resultMsg = resultImport?SAVED:IGNORED;
					markAsImported(importClass.getTableName(), rs.getInt(importClass.getTableName() + "_ID"));
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { importClass.getServiceName(), result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				} catch (Exception e) {
					markAsError(importClass.getTableName(), rs.getInt(importClass.getTableName() + "_ID"),
							e.getMessage());
					result.put(ERROR, result.get(ERROR) + 1);
					Object[] params = new Object[] { importClass.getServiceName(), result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
			}
			if (!someResults) {
				resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationNoResults");
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
		sql.append("	i_isimported = 'Y' ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

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
		sql.append("	i_errormsg = '" + errorMsg + "' ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

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

