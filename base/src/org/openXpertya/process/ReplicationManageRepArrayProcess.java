package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.openXpertya.replication.AbstractTerminalLaunchProcess;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

/**
 * Gestión de repArrays.  Modifica o rellena el repArray de los registros según los parametros indicados.
 *
 * 	- Posición a actualizar (host). 
 * 		Obligatorio. Por seguridad se indica una unica posición por ejecución.  Inicia en posicion 1.  Si la posición es mayor al contenido del repArray, rellenará las posiciones vacias con el valor indicado.
 *  - Valor a setear en los registros. 0=sin accion / 1=replicar / 2=replicado / 3=modificado / A=reintentar(insertar o actualizar)  
 *  	Opcional. Por defecto no especifica ninguno
 *  - Setear includeInReplication a (Y) Forzar a Y, (N) Forzar a N, (O) Omitir cambios
 *  	Opcional. Valor por defecto O
 *  - Tabla a actualizar.  
 *  	Si no se indica, se actualizan todas las tablas de envio/bidireccionales.
 *  - Fecha desde de los registros (campo updated).  
 *  	Si no se indica, se omite este filtrado. 
 *  - Fecha hasta de los registros (campo updated).  
 *  	Si no se indica, se omite este filtrado.
 *  - Limitar registros.  
 *  	Cantidad de registros a actualizar por transacción.  Si no se indica, el valor por defecto es 100.
 */

public class ReplicationManageRepArrayProcess extends AbstractTerminalLaunchProcess {

	/** Posibles acciones sobre campo includeInReplication */
	public static final String 	INCLUDE_IN_REPLICATION_FORCE_VALUE_Y = "Y";
	public static final String 	INCLUDE_IN_REPLICATION_FORCE_VALUE_N = "N";
	public static final String 	INCLUDE_IN_REPLICATION_OMIT_CHANGES = "O";
	/** Cantidad de registros a procesar en una misma transaccion */
	public static final int 	RECORDS_TO_PROCESS_BATCH_LIMIT = 100;
	
	// actualizar posicion del reparray
	static int updatePos = -1;
	// valor a setear
	static String newValue = null;
	// modificar includeinreplication?
	static String incInRepValue = INCLUDE_IN_REPLICATION_OMIT_CHANGES;
	// solo tabla con nombre...
	static String onlyTableName = null;
	// solo registro con nombre...
	static String onlyRecordUID = null;
	// filtrar con fecha desde... 
	static String dateFrom = null;
	// filtrar con fecha hasta...
	static String dateTo = null;
	// cantidad de registros por transaccion
	static int limitRecords = RECORDS_TO_PROCESS_BATCH_LIMIT;
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String doIt() throws Exception {
		// tiempo por tabla
		long partialTime;
		// tiempo total
		long totalTime = System.currentTimeMillis();
		
		// Obtener las tablas por las cuales iterar
		ArrayList<String> tables = loadTablesToProcess();
		
		// Iterar por todas las tablas
		for (String aTable : tables) {
			
			// Medicion de tiempo por tabla
			partialTime = System.currentTimeMillis();
			
			// Imprimir evolucion por tabla
			int total = 0;
			System.out.print(" Tabla: " + aTable + ". Registros actualizados: ");
			
			// Procesar todos los registros, por lotes
			int length = 0;
			while (true) {
				
				// Quedan registros a procesar?
				ArrayList<String> records = getRecordList(aTable);
				if (records.size() == 0)
					break;
				
				// Borrar caracteres de total para reimprimir luego de procesar
				while (length-- > 0)
					System.out.print("\b");
				
				// Actualizar registros por lotes
				String trxName = Trx.createTrxName();
				try {
					// Actualizar registros dentro de la transacción
					Trx.getTrx(trxName).start();
					int count = DB.executeUpdate(getUpdateQuery(aTable, records), trxName);
					if (count==-1)
						throw new Exception("Error al actualizar " + aTable + ". excecuteUpdate retorno -1!");
					Trx.getTrx(trxName).commit();
					total += count;
					System.out.print(total);
					length = String.valueOf(total).length();
				}
				catch (Exception e) {
					Trx.getTrx(trxName).rollback();
					System.out.print("Omitiendo actualizaciones para esta tabla debido a errores. " + e.toString());
					break;
				}
				finally {
					Trx.getTrx(trxName).close();
				}
			}
			
			// Informar el tiempo por tabla
			System.out.print(". Tiempo transcurrido: " + ((System.currentTimeMillis() - partialTime) / 1000) + " segundos. Tiempo acumulado: " + ((System.currentTimeMillis() - totalTime) / 1000) + " segundos. ");
			
			// Se viene otra tabla 
			System.out.println();
		}
		
		return " FINALIZADO. Tiempo total: " + ((System.currentTimeMillis() - totalTime) / 1000) + " segundos. ";
		
	}
	
	/**
	 * Retorna la nomina de tablas a actualizar su repArray.  
	 * Si no se especifica una tabla por parametro, retornará todas las tablas cuya configuración sea de envio/bidireccional
	 */
	protected ArrayList<String> loadTablesToProcess() throws Exception {
		ArrayList<String> retValue = new ArrayList<String>();
		
		// Limitar a una tabla en particular?
		if (onlyTableName != null && onlyTableName.length()>0) {
			retValue.add(onlyTableName);
			return retValue;
		}
		
		// Recuperar todas las tablas de envio y/o bidireccionales
		PreparedStatement pstmt = 
			DB.prepareStatement(
				" SELECT t.tablename " +
				" FROM ad_tablereplication tr " +
				" INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id " +
				" WHERE replicationarray LIKE ('%1%') OR replicationarray LIKE ('%3%') " +
				" ORDER BY t.tablename ASC ");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) 
			retValue.add(rs.getString("tablename"));
		
		// Retornar listado
		return retValue;
	}
	
	/**
	 * Devuelve una lista de retrieveUIDs pertenecientes a registros de una tabla a modificar  
	 * @param aTable tabla a modificar sus repArrays
	 * @return una lista de retrieveUIDs (podría devolverse vacía si ya no quedan registros a actualizar) 
	 */
	protected ArrayList<String> getRecordList(String aTable) throws Exception {
		// Cambió la tabla? De ser así, recuperar la nómina de registros a actualizar
		if (!aTable.equals(currentTable)) {
			currentTable = aTable;
			PreparedStatement pstmt = DB.prepareStatement(" SELECT retrieveUID FROM " + aTable + getRecordsToUpdateWhereClause(aTable));
			currentRS = pstmt.executeQuery();
		}
		// Devolver la nomina de registros a actualizar, identificados por su retrieveUID, limitando la cantidad segun limitRecords
		int count = 0;
		ArrayList<String> retValue = new ArrayList<String>();
		while (count++ < limitRecords && currentRS.next()) {
			retValue.add(currentRS.getString("retrieveUID"));
		}
		return retValue;
	}
	protected String currentTable = null;
	protected ResultSet currentRS = null;
	
	/**
	 * Arma la clausula where para recuperar registros a actualizar (filtrado segun criterios)
	 */
	protected String getRecordsToUpdateWhereClause(String aTable) {
		StringBuffer retValue = new StringBuffer();
		// Filtro por compañía
		retValue.append(" WHERE AD_Client_ID IN (0, " + Env.getContext(Env.getCtx(), "#AD_Client_ID") + ") ");
		// Filtro por fecha desde
		if (dateFrom != null)
			retValue.append(" AND Updated > '" + dateFrom + "'");
		// Filtro por fecha hasta
		if (dateTo != null)
			retValue.append(" AND Updated < '" + dateTo + "'");
		if (onlyRecordUID != null)
			retValue.append(" AND RetrieveUID = '" + onlyRecordUID + "'");
		// Retornar valor
		return retValue.toString();
	}
	
	/**
	 * Query de actualizacion de registros para...
	 * @param aTable una tabla dada
	 * @param records y un set de registros dado
	 * @return String con el query de actualizacion
	 */
	protected String getUpdateQuery(String aTable, ArrayList<String> records) {
		StringBuffer retValue = new StringBuffer();
		
		// Cachear el query hasta que cambie la tabla
		if (!aTable.equals(lastTable)) {
			lastTable = aTable;
			updateQuery = new StringBuffer();
			
			// Sentencia de actualizacion
			updateQuery.append(" UPDATE ").append(aTable)		
					   .append(" SET repArray = ");
			
			// Si no se esta especificando un nuevo valor, simplemente marcar SET y el repArray existente
			if (newValue == null)
				updateQuery.append(setRepArrayPrefix(aTable)).append(" || repArray ");
			else
			// Si se está especificando un nuevo valor, entonces actualizar la posicion o rellenar hasta dicha posicion
				updateQuery.append("		CASE WHEN length(repArray) < ").append(updatePos)
						   .append("       THEN ").append(setRepArrayPrefix(aTable)).append(" || rpad(COALESCE(reparray,''), ").append(updatePos).append(", '").append(newValue).append("') ")
						   .append("       ELSE ").append(setRepArrayPrefix(aTable)).append(" || overlay(reparray placing '").append(newValue).append("' from ").append(updatePos).append(" for 1) ")
						   .append("       END ");
			
			// Forzar includeInReplication (Y/N)?
			if (incInRepValue.equals(INCLUDE_IN_REPLICATION_FORCE_VALUE_Y))
				updateQuery.append(" , includeInReplication = '").append(INCLUDE_IN_REPLICATION_FORCE_VALUE_Y).append("'");
			else if (incInRepValue.equals(INCLUDE_IN_REPLICATION_FORCE_VALUE_N))
				updateQuery.append(" , includeInReplication = '").append(INCLUDE_IN_REPLICATION_FORCE_VALUE_N).append("'");
		}
		
		// Concatenar el updateQuery
		retValue.append(updateQuery);
		
		// Armar el where
		retValue.append(" WHERE retrieveUID in (");
		for (int i=0; i<records.size(); i++) 
			retValue.append("'").append(records.get(i)).append(i<records.size()-1?"',":"'");
		retValue.append(" ) ");
		
		// Retornar valor
		return retValue.toString();
	}
	String lastTable = null;
	StringBuffer updateQuery = null;
	
	
	/**
	 * En todas las tablas de replicacion se requiere el prefijo SET para poder modificar el campo repArray,
	 * a excepción de la tabla de eliminaciones, dado que la cual no posee el trigger replication_event
	 * @param aTable
	 * @return
	 */
	protected String setRepArrayPrefix(String aTable) {
		if (ReplicationConstants.DELETIONS_TABLE.toLowerCase().equals(aTable.toLowerCase()))
			return "''";
		return "'SET'";
			
	}
	
	
	/* ================================================ INVOCACION DESDE TERMINAL ================================================ */

	/** Ayuda */
	static final String PARAM_HELP			 		=	"-h";
	/** Posicion a actualizar */
	static final String PARAM_POS			 		=	"-p";
	/** Valor a setear */
	static final String PARAM_VAL			 		=	"-v";
	/** IncludeInReplication a setear */
	static final String PARAM_IIR			 		=	"-i";
	/** Tabla a actualizar */
	static final String PARAM_TABLE			 		=	"-t";
	/** Registro a actualizar */
	static final String PARAM_RECORD			 	=	"-r";
	/** Filtro por fecha desde */
	static final String PARAM_DATE_FROM		 		=	"-df";
	/** Filtro por fecha hasta */
	static final String PARAM_DATE_TO				=	"-dt";
	/** Limite de registros por transaccion */
	static final String PARAM_RECORD_LIMIT	 		=	"-l";

	
	
	public static void main(String args[])
	{
		// Si no se especifica parametro alguno, la funcionalidad recorrería todos los registros sin realizar cambio alguno.
		if (args.length == 0)
			showHelp(" \n Error: Se requiere al menos una configuracion de cambios. \n\n " + getHelpMessage());
		// Procesar argumentos
		for (String arg : args) {
			// Ayuda?
			if (arg.toLowerCase().startsWith(PARAM_HELP))
				showHelp(getHelpMessage());
			// Posicion a actualizar
			if (arg.toLowerCase().startsWith(PARAM_POS))
				updatePos = Integer.parseInt(arg.substring(PARAM_POS.length()));
			// Valor a setear en reparray
			if (arg.toLowerCase().startsWith(PARAM_VAL))
				newValue = arg.substring(PARAM_POS.length());
			// Valor a setear en includeinreparray
			if (arg.toLowerCase().startsWith(PARAM_IIR))
				incInRepValue = arg.substring(PARAM_IIR.length());
			// Tabla a actualizar
			if (arg.toLowerCase().startsWith(PARAM_TABLE))
				onlyTableName = arg.substring(PARAM_TABLE.length());
			// Registro a actualizar
			if (arg.toLowerCase().startsWith(PARAM_RECORD))
				onlyRecordUID = arg.substring(PARAM_RECORD.length());
			// Filtro por fecha desde
			if (arg.toLowerCase().startsWith(PARAM_DATE_FROM))
				dateFrom = arg.substring(PARAM_DATE_FROM.length());
			// Filtro por fecha hasta
			if (arg.toLowerCase().startsWith(PARAM_DATE_TO))
				dateTo = arg.substring(PARAM_DATE_TO.length());
			// Limite de registros por transaccion
			if (arg.toLowerCase().startsWith(PARAM_RECORD_LIMIT))
				limitRecords = Integer.parseInt(arg.substring(PARAM_RECORD_LIMIT.length()));			
		}
		
		// Si no se indica un valor para reparray y tampoco un cambio para includeinreplication, no tiene sentido continuar 
		if (newValue == null && incInRepValue.equals(INCLUDE_IN_REPLICATION_OMIT_CHANGES))
			showHelp(" \n Error: Se requiere al menos una configuracion de cambios. \n\n " + getHelpMessage());

		// No es posible indicar un valor pero no indicar una posicion
		if (updatePos == -1 && newValue != null && newValue.length()>0)
			showHelp(" \n Error: Si se especifica un valor a setear se debe indicar una posicion a actualizar. \n\n " + getHelpMessage());
		
		// No es posible filtrar por registro si no se filtra por tabla
		if (onlyRecordUID != null && onlyRecordUID.length() > 0 && onlyTableName == null)
			showHelp(" \n Error: El filtrado por registro requiere especificar también la tabla. \n\n " + getHelpMessage());
		
		// Iniciar el proceso
		new ReplicationManageRepArrayProcess().execute();
	}

	
	protected static String getHelpMessage() {
		return			
			" ---------- Manager de campos RepArrays e IncludeInReplication ---------- \n" +
			" " + PARAM_HELP  			+ "    Muestra esta ayuda. \n" +
			" " + PARAM_POS     		+ "    (Obligatorio si " + PARAM_VAL + " está seteado) Posicion a actualizar. Inicia en posicion 1.  Si la posición es mayor al contenido del repArray, rellenará las posiciones vacias con el valor indicado (si se indica un valor) \n" +
			" " + PARAM_VAL      		+ "    (Opcional) Valor a setear en el campo RepArray de los registros. (ejemplos: 0=sin accion / 1=insertar / 2=replicado / 3=modificado / A=reintentar(insertar o actualizar).  Por defecto no se especifica ninguno. \n" +
			" " + PARAM_IIR    			+ "    (Opcional) Especificar valor de campo IncludeInReplication.  "+INCLUDE_IN_REPLICATION_FORCE_VALUE_Y+"=Forzar a Y, "+INCLUDE_IN_REPLICATION_FORCE_VALUE_N+"=Forzar a N, "+INCLUDE_IN_REPLICATION_OMIT_CHANGES+"=Omitir cambios. Valor por defecto: "+INCLUDE_IN_REPLICATION_OMIT_CHANGES+" \n" +
			" " + PARAM_TABLE  			+ "    (Opcional) Limita la replicación unicamente a la tabla especificada. Si no se indica, se actualizan todas las tablas de envio/bidireccionales (excepto ad_changelog_replication, la cual debe indicarse explícitamente mediante este parametro). \n" +
			" " + PARAM_RECORD 			+ "    (Opcional) Limita la replicación unicamente al registro especificado. Se requiere especificar en este caso la tabla a modificar. \n" +
			" " + PARAM_DATE_FROM 		+ "    (Opcional) Incluir unicamente registros mayores a esta fecha (campo updated). \n" +
			" " + PARAM_DATE_TO   		+ "    (Opcional) Incluir unicamente registros menores a esta fecha (campo updated). \n" +
			" " + PARAM_RECORD_LIMIT   	+ "    (Opcional) limita la cantidad de registros a procesar por transaccion. El valor por defecto es " + RECORDS_TO_PROCESS_BATCH_LIMIT + " \n" +
			" ------------ IMPORTANTE: NO DEBEN DEJARSE ESPACIOS ENTRE EL PARAMETRO Y EL VALOR DEL PARAMETRO! --------------- \n";
	}
	
}
