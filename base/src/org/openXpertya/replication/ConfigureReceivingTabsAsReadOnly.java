package org.openXpertya.replication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_AD_Tab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * En función de la configuración por tabla en AD_TableReplication, pasar a read-only = 'Y' (o isInsertRecord = 'N')
 * las pestañas correspondientes a las tablas marcadas como "solo recepción".
 * 
 * =============================================================================================
 * IMPORTANTE! Utiliza la conexión a base de datos especificada en $OXP_HOME/Libertya.properties
 * =============================================================================================
 * 
 * ========================================================================================================
 * IMPORTANTE: Por defecto aplica la lógica de read-only, pero por parámetro se puede modificar esta opción
 * ========================================================================================================
 * 
 * =======================================================================================================================
 * IMPORTANTE: La nómina de tabs a omitir cambios debe almacenarse separada por comas en AD_Preference, ver TAB_EXCEPTIONS
 * =======================================================================================================================
 */

public class ConfigureReceivingTabsAsReadOnly extends AbstractReplicationProcess {

	/** Opcion: Pasar a read only = Y las pestañas */
	static final String FIELD_READONLY		= "IsReadOnly";
	/** Opcion: Pasar a insert recrod = N las pestañas */
	static final String FIELD_INSERTRECORD	= "IsInsertRecord";
	
	/** === Lógica a utilizar: FIELD_READONLY o FIELD_INSERTRECORD === */
	static String changeField = FIELD_READONLY;
	
	/** Entrada en AD_Preference para especificar la nómina pestañas que NO deben modificarse (AD_Tab_IDs SEPARADOS POR ,) */
	protected static final String TAB_EXCEPTIONS 	= "InmutableReceivingTabsList";
	
	@Override
	protected String getProcessName() {
		return "";
	}
	
	public static void main(String[] args) 
	{
		if (args.length > 0) {
			changeField = args[0];
			if (!FIELD_READONLY.equals(changeField) && !FIELD_INSERTRECORD.equals(changeField))
				showHelp("El parámetro de tipo de acción debe ser " + FIELD_READONLY + " o " + FIELD_INSERTRECORD);
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");
	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar la configuracion de replicacion ");

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null)
	  		showHelp("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");

	  	// Informar a usuario e Iniciar la transacción
		String message = "[Client] Iniciando proceso. ";
	  	System.out.println(message + "(" + DB.getDatabaseInfo() + ")");

	  	ConfigureReceivingTabsAsReadOnly crtaro = new ConfigureReceivingTabsAsReadOnly();
	  	try {
	  		crtaro.prepare();
	  		System.out.println(crtaro.doIt());
	  	}
	  	catch (Exception e) {
	  		e.printStackTrace();
	  	}

		
	}
	
	protected static void showHelp(String message) {
		String help = " [[ " + message + " ]] ";
  		System.out.println(help);
  		System.exit(1);		
	}
	
	@Override
	protected String doIt() throws Exception {
		// Iterar por cada tabla configurada
		PreparedStatement pstmt = DB.prepareStatement("SELECT tr.AD_Table_ID, tr.ReplicationArray, t.TableName FROM AD_TableReplication tr INNER JOIN AD_Table t ON tr.AD_Table_ID = t.AD_Table_ID");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			// Analizar el replicationArray de la tabla
			int tableID = rs.getInt("AD_Table_ID");
			String tableName = rs.getString("TableName");
			String repArray = rs.getString("ReplicationArray");
			// Si tiene entradas de envio o envio/recepción, entonces no hacer más nada.
			if (repArray.indexOf(ReplicationConstants.REPLICATION_CONFIGURATION_SEND)>=0 ||
					repArray.indexOf(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE)>=0)
				continue;
			// Si se llegó a este punto es porque para la tabla dada, solo se recibe. 
			// Deben marcarse como solo lectura las pestañas en cuestión (que todavia no esten marcadas como solo lectura)
			PreparedStatement pstmtTab = DB.prepareStatement("SELECT AD_Tab_ID FROM AD_Tab WHERE " + getWhereClause() + " AND AD_Table_ID = " + tableID + getTabExceptions());
			ResultSet rsTab = pstmtTab.executeQuery();
			// Actualizar cada tab que referencie a la tabla en cuestion
			while (rsTab.next()) {
				X_AD_Tab aTab = new X_AD_Tab(Env.getCtx(), rsTab.getInt("AD_Tab_ID"), null);
				aTab.set_Value(changeField, getNewValue());
				if (!aTab.save())
					System.err.println("Error al actualizar tabID: " + aTab.getAD_Tab_ID() + ". " +  CLogger.retrieveErrorAsString());
				else
					System.out.println("Modificado valor " + changeField + " a " + getNewValue() + " para tab: " + aTab.getName() + " (" + aTab.getAD_Tab_ID() + "), tabla: " + tableName + " (" + tableID + ").");
			}
		}
		return "FINALIZADO";		
	}
	
	/** Retorna la parte del query correspondiente */
	public String getWhereClause() {
		if (FIELD_READONLY.equals(changeField))
			return FIELD_READONLY + " = 'N'";
		return FIELD_INSERTRECORD + " = 'Y'";
	}
	
	/** Retorna el nuevo valor en funcion de la columna a modificar */
	public String getNewValue() {
		if (FIELD_READONLY.equals(changeField))
			return "Y";
		return "N";
	}
	
	/** Retorna la nómina de pestañas a no modificar (a partir de la AD_Preference TAB_EXCEPTIONS), en formato SQL: tabID, tabID, tabID */
	public String getTabExceptions() {
		String values = MPreference.searchCustomPreferenceValue(TAB_EXCEPTIONS, 0, 0, 0, false);
		if (values==null || values.length()==0)
			return "";
		return " AND AD_Tab_ID NOT IN (" + values + ")";
	}
	
}
