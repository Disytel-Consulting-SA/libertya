package org.openXpertya.plugin.install;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.X_AD_Column;
import org.openXpertya.model.X_AD_Ref_Table;
import org.openXpertya.model.X_AD_Table;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PluginXMLUpdater {
	
	/** Trx */
	protected static String m_trxName = null;
	
	/** Este documento contiene el listado de changegroups */
	private XMLUpdateDocument updateDocument = null;
	
	/** Para uso en subclases sin necesidad de ampliar parametros */
	protected ChangeGroup currentChangeGroup = null;
	
	/** Compatibilidad de Upgrade entre releases 09.10 y 10.03 */
	private static String AD_CLIENT_REL0910 = "1000010";
	private static String AD_CLIENT_REL1003 = "1010016";
	private static String AD_ORG_REL0910 = "1000047";
	private static String AD_ORG_REL1003 = "1010053";
	private static Boolean isLibertyaRelease0910 = null;
	
	/** Warnings */
	protected static final String WARNING_UID_ALREADY_EXISTS 		= "WARNING: - Sin inserción - referencia universal ya existente.";
	protected static final String WARNING_UID_NOT_EXISTS_UPDATE		= "WARNING: - Imposible actualizar - referencia inexistente en tabla.";
	protected static final String WARNING_UID_NOT_EXISTS_DELETE 	= "WARNING: - Imposible eliminar - referencia inexistente en tabla.";
	protected static final String WARNING_CHANGEGROUP_HAS_NO_UID 	= "WARNING: - changeGroup sin refencia Universal.";
	protected static final String WARNING_STRUCTURE_MISSING		 	= "WARNING: - Error general. Sin  estructura adecuada para este procesamiento.";
	protected static final String WARNING_CANNOT_RESOLVE_UID	 	= "WARNING: - imposible determinar referencia";
	protected static final String WARNING_DISPLAY_TYPE_UNKNOWN	 	= "WARNING: - displayType no reconocido en sentencia de insercion";
	
	/** Almacena los errores que va presentando la instalación */
	protected static int errorCount = 0;
	
	/** En caso de error no elevará una excepcion sin continuar la ejecución */
	protected boolean stopOnError = false;

	/** Save point por errores */
	protected final static String savePointName = "installSavePoint";
	
	/** Indicador de actividad */
	protected String[] animation = {":.........", ".:........", "..:.......", "...:......", "....:.....", ".....:....", "......:...", ".......:..", "........:.", ".........:", "........:.", ".......:..", "......:...", ".....:....", "....:.....", "...:......", "..:.......", ".:........"}; // {"-", "\\", "|", "/"};
	
	/** Si esta activado, la instalacion de un componente implicará no solo impactar en las tablas, sino que tambien copiará 
	 *  la instalación en el changelog, quedando la base de datos destino como lista a exportar dicho componente */
	protected boolean copyToChangelog = false;
	
	/** Si esta activado, mapeara la instalación a un componente ya existente, en lugar de utilizar el especificado en las 
	 *  propiedades del componente a instalar.  Esto permite "emular" el desarrollo de un componente en un host diferente al destino */
	protected boolean mapToComponent = false;
	
	/** Valor a propagar hacia persistInChangelog */
	int keyColumnValue = -1;
	
	/** Getter: devuelve el documento entero a procesar */
	public XMLUpdateDocument getUpdateDocument() {
		return updateDocument;
	}
	
	/**
	 * Crear un parser de update y rellena el contenido estructural según el XML
	 * @param url acceso al archivo correspondiente
	 */
	public PluginXMLUpdater(String xml, String trxName, boolean stopOnError) throws Exception
	{
		// Guardar transaccion
		m_trxName = trxName;
		this.stopOnError = stopOnError;

		// Parsear el documento
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		
		// Guardar el documento parseado en estructura auxiliar
		this.new XMLUpdateDocument(doc);
		updateDocument = new XMLUpdateDocument(doc);	
	}
	
	/**
	 * Constructor simple para que el updater procese un solo changeGroup
	 * En este caso no es necesario realizar parseo de XML alguno (se supone que
	 * el mismo ya fue realizado previamente y ya se cuenta con el resultante)
	 */
	public PluginXMLUpdater(ChangeGroup changeGroup, String trxName, boolean stopOnError) throws Exception
	{
		m_trxName = trxName;
		this.stopOnError = stopOnError;
		updateDocument = new XMLUpdateDocument(changeGroup);
	}
	
	/**
	 * Itera todo el documento impactando donde corresponde
	 */
	public void processChangeLog() throws Exception
	{
		/* Sentencia a ejecutar */
		String sentence = "";
		
		/* Recargar la caché si asi corresponde */
		ReplicationCache.reloadCacheData();
		
		int iter = 0;
		/* Verificar la preferencia.  */
		copyToChangelog = shouldCopyToChangelog();
		mapToComponent = shouldMapToComponent();
		
		/* Iterar el documento completo y generar las sentencias sql correspondientes */  
		for (ChangeGroup changeGroup : getUpdateDocument().getChageGroupList())
		{
			try
			{
				// Verificar si por alguna condicion hay que omitir el procesamiento del changelogActual
				if (shouldSkipCurrentChangeGroup(changeGroup))
					continue;
				
				sentence = "";
				currentChangeGroup = changeGroup;
				PluginUtils.appendStatus("[" + iter + "]" + animation[iter++%animation.length], true, false, false, false);
				if (MChangeLog.OPERATIONTYPE_Insertion.equals(changeGroup.getOperation()))
					sentence = processInsert(changeGroup).toString();
				else if (MChangeLog.OPERATIONTYPE_Modification.equals(changeGroup.getOperation()))
					sentence = processModify(changeGroup).toString();
				else if (MChangeLog.OPERATIONTYPE_Deletion.equals(changeGroup.getOperation()))
					sentence = processDelete(changeGroup).toString();
				else if (MChangeLog.OPERATIONTYPE_InsertionModification.equals(""+changeGroup.getOperation().charAt(0)))
					sentence = processInsertModify(changeGroup).toString();				
				
				/* Si no hay sentencia de ejecucion, omitirs */
				if (sentence != null && sentence.length() > 0)
				{
					/* Impactar la bitácora */
					appendStatus("[" + iter + "]: " + sentence);
					executeUpdate(sentence, m_trxName);
					
					/* Impactar en el changelog */ 
					if (copyToChangelog)		
						persistInChangelog(changeGroup);
				}
				
				/* Si hubo exito (ejecutando sentencia u omitiendo por ser vacia) realizar acciones adicionales en caso de ser necesario */
				handleSuccess(sentence, changeGroup);
			}
			catch (Exception e)
			{
				handleException(e, changeGroup);
			}
		}
	}
	
	/* En esta clase actualmente se deben procesar todos los registros.
	 * Se deja la posibilidad de redefinicion para las subclases */
	protected boolean shouldSkipCurrentChangeGroup(ChangeGroup changeGroup)
	{
		return false;
	}
	
	
	/**
	 * Envia al log correspondiente la sentencia a ejecutar
	 * @param sentence sentencia a ejecutar
	 */
	protected void appendStatus(String sentence)
	{
		PluginUtils.appendStatus(" SQL: " + sentence, false, false, true, true);
	}
	
	/**
	 * En caso de ser necesario, ejecutar acciones adicionales luego del
	 * exito en la ejecucion de la sentencia SQL generada e impactada en BBDD
	 * Este metodo puede ser redefinido por subclases según sea necesario
	 */
	protected void handleSuccess(String sentence, ChangeGroup changeGroup) throws Exception
	{
		// Es necesario invalidar las caches para ciertas tablas que pueden ser utilizadas en el resto de la instalacion
		if (X_AD_Table.Table_Name.equalsIgnoreCase(changeGroup.getTableName()) || 
			X_AD_Column.Table_Name.equalsIgnoreCase(changeGroup.getTableName()) ||
			X_AD_Ref_Table.Table_Name.equalsIgnoreCase(changeGroup.getTableName()) )
				ReplicationCache.reloadCacheData();
	}
	
	/**
	 * Ignora la excepción o no según las condiciones del caso
	 * Este metodo puede ser redefinido por subclases según sea necesario
	 * @param e excepcion generada al ejecutar la actualización
	 * @param changeGroup datos que originalmente generaron la excepcion
	 */
	protected void handleException(Exception e, ChangeGroup changeGroup) throws Exception
	{
		// elevar excepción solo en caso de tener que detenerse ante un error
		// (si stopOnError es True, o bien se esta generando entradas en AD_Changelog)
		if (stopOnError || copyToChangelog)
			throw new Exception(e);
	}
	
	/**
	 * Inserta un nuevo registro
	 * @param changeGroup es el columnSet a impactar
	 */
	private StringBuffer processInsert(ChangeGroup changeGroup)  throws Exception
	{
		/* Creación del sql para el insert */
		StringBuffer sql = new StringBuffer("");

		/* Si el registro ya existe, no insertarlo nuevamente */
		if ( recordExists(changeGroup) ) {
			handleRecordExistsOnInsert(changeGroup);
			return sql;
		}
		
		/* Obtener la columna clave de la tabla */
		String keyColumnName = getKeyColumnName(changeGroup.getTableName());
		
		/* Rellenar stringbuffers nombres-datos de las columnas */
		StringBuffer columnNames = new StringBuffer();
		StringBuffer columnValues = new StringBuffer();

		for (Column column : changeGroup.getColumns())
			insertColumnSQLSentence(columnNames, columnValues, column, keyColumnName, changeGroup.getTableName(), changeGroup);
		
		/* Quitar ultima coma */
		columnNames.setCharAt(columnNames.length()-1, ' ');
		columnValues.setCharAt(columnValues.length()-1, ' ');
		
		/* Finalización de sentencia sql */
		sql.append(" INSERT INTO ").append(changeGroup.getTableName()).append(" (").append(columnNames).append(") VALUES (").append(columnValues).append(");");
		
		/* Customizacion adicional? */
		customizeInsertionQuery(sql, changeGroup);
		
		return sql;
	}
	
	/**
	 * Modifica un registro existente
	 * @param changeGroup es el columnSet a modificar
	 */
	private StringBuffer processModify(ChangeGroup changeGroup) throws Exception
	{
		/* Creación del sql para el update */
		StringBuffer sql = new StringBuffer("");
		
		/* Si el regitro no existe, presentar el error correspondiente  */
		if ( !recordExists(changeGroup) ) {
			handleRecordNotExistsOnModify(changeGroup);
			return sql;
		}
		
		/* Obtener la columna clave de la tabla */
		String keyColumnName = getKeyColumnName(changeGroup.getTableName());
		
		/* Inicio de sentencia sql */
		sql.append(" UPDATE " + changeGroup.getTableName() + " SET ");
		
		/* Columnas a modificar de sentencia sql (el ID de la columna debe ser omitido en cualquier modificación dado que varia en cada instancia */
		for (Column column : changeGroup.getColumns())
			if (!column.getName().equalsIgnoreCase(keyColumnName))
				updateColumnSQLSentence(sql, column, changeGroup.getTableName(), changeGroup);
			
		/* Quitar ultima coma */
		sql.setCharAt(sql.length()-1, ' ');
		
		/* Fin de sentencia sql y retorno de la misma */
		sql.append(" WHERE ").append(appendUniversalRefenceWhereClause(getUniversalReference(changeGroup))).append(";");
		
		/* Customizacion adicional? */
		customizeModificationQuery(sql, changeGroup);
		
		return sql;
	}
	
	/**
	 * Elimina un registro existente
	 * @param changeGroup es el columnSet a modificar
	 */
	private StringBuffer processDelete(ChangeGroup changeGroup)  throws Exception
	{
		/* Creación del sql para el delete */
		StringBuffer sql = new StringBuffer("");		
		
		/* Si el registro no existe, no intentar eliminarlo */
		if ( !recordExists(changeGroup) ) {
			handleRecordNotExistsOnDelete(changeGroup);
			return sql;
		}

		/* Definir sentencia y devolver la misma */
		sql.append(" DELETE FROM " + changeGroup.getTableName() + " WHERE ").append(appendUniversalRefenceWhereClause(getUniversalReference(changeGroup))).append(";");
		
		/* Customizacion adicional? */
		customizeDeletionQuery(sql, changeGroup);
		
		return sql;
	}

	/**
	 * Funcionalidad especial.  Verifica si el registro existe en la tabla a traves de su UID.
	 * 	En caso de existir, ejecutará una sentencia de actualización.  
	 * 	En caso de no existir, ejecutará una sentencia de inserción.
	 */
	private StringBuffer processInsertModify(ChangeGroup changeGroup)  throws Exception
	{
		if (recordExists(changeGroup))
			return processModify(changeGroup);
		return processInsert(changeGroup);
	}
	
	/** Eleva una excepcion a fin de notificar que el intento de inserción no fue llevado a cabo dado que el registro en cuestion ya existía */
	protected void handleRecordExistsOnInsert(ChangeGroup changeGroup) throws Exception	{
		raiseException(WARNING_UID_ALREADY_EXISTS + " Referencia: (" + getUniversalReference(changeGroup) + ") en tabla: " + changeGroup.getTableName() );
	}

	/** Eleva una excepcion a fin de notificar que el intento de modificación no fue llevado a cabo dado que el registro en cuestion no existía */
	protected void handleRecordNotExistsOnModify(ChangeGroup changeGroup) throws Exception	{
		raiseException(WARNING_UID_NOT_EXISTS_UPDATE + " Referencia: " + getUniversalReference(changeGroup) + ". Tabla:" + changeGroup.getTableName());
	}
	
	/** Eleva una excepcion a fin de notificar que el intento de eliminaciòn no fue llevado a cabo dado que el registro en cuestion no existía */
	protected void handleRecordNotExistsOnDelete(ChangeGroup changeGroup) throws Exception	{
		raiseException(WARNING_UID_NOT_EXISTS_DELETE + " Referencia: (" + getUniversalReference(changeGroup) + ") .Tabla: " + changeGroup.getTableName() );
	}
	
	/**
	 * Verifica la existencia del registro en la tabla dada
	 * @param tableName tabla donde buscar
	 * @param refUID registro a ubicar
	 * @param action insercion, modificacion, eliminación
	 * @return true si el registro existe o false en caso contrario
	 * @throws Exception en caso que la estructura en la tabla no sea la adecuada, 
	 * 					 o que el changeGroup no contenga la referencia correspondiente 
	 */
	protected boolean recordExists(ChangeGroup changeGroup) throws Exception
	{
		/* Definicion inicial de variables: nombre de tabla, referencia universal y clausula where correspondiente */
		String tableName = changeGroup.getTableName();
		String universalReference = getUniversalReference(changeGroup);
		String refUIDwhereClause = appendUniversalRefenceWhereClause(universalReference);
		
		/* Si no existe la referenciaUniversal es imposible eliminar/modificar algo */
		if (!validateUniversalReference(changeGroup)) { 
			raiseException(WARNING_CHANGEGROUP_HAS_NO_UID + " Operacion: " + changeGroup.getOperation() + "  (valor en null) - tabla: " + tableName );
			return false;
		}
		
		/* Buscar el registro por su UID */
		int recordExists = DB.getSQLValue(m_trxName, " SELECT COUNT(1) FROM " + tableName  + " WHERE " + refUIDwhereClause, true);
		
		/* Si recordExists devuelve -1 entonces la tabla no contiene la estructura adecuada para el procesamiento */
		if ( recordExists == -1 )
			raiseException(WARNING_STRUCTURE_MISSING + " Operacion: " + changeGroup.getOperation() + " - tabla: " + tableName );
		
		/* Retornar true si al menos hay un registro con dicho UID */
		return recordExists > 0;
	}
	
	
	/**
	 * Eleva una excepcion, previa incorporación del error en la bitacora
	 * @param error mensaje de error a bitacorear / elevar
	 * @throws Exception siempre
	 */
	protected static void raiseException(String errorMsg) throws Exception
	{
		errorCount++;
		PluginUtils.appendError(errorMsg);
		PluginUtils.appendStatus(errorMsg, true, true, true, true);
		throw new Exception(errorMsg);
	}
	
	
	/**
	 *	Outline de método para redefinición en subclases
	 */
	protected void customizeInsertionQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Implemented by subclass
	}
	
	/**
	 *	Outline de método para redefinición en subclases
	 */
	protected void customizeModificationQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Implemented by subclass
	}
	
	/**
	 *	Outline de método para redefinición en subclases
	 */
	protected void customizeDeletionQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Implemented by subclass
	}
	
	
	/**
	 * Valida si existe la referencia universal en el changeGroup actual
	 * Outline de método para redefinición en subclases
	 */
	protected boolean validateUniversalReference(ChangeGroup changeGroup)
	{
		return (changeGroup.getUid() != null);
	}
	
	/**
	 * Retorna el string a concatenar para la clausula WHERE (qué registro modificar)
	 * Outline de método para redefinición en subclases
	 */
	protected String appendUniversalRefenceWhereClause(String reference)
	{
		return getUIDWhereClause(reference);
	}
	
	/**
	 * Recupera la referencia universal
	 * Outline de método para redefinición en subclases
	 */
	protected String getUniversalReference(ChangeGroup changeGroup)
	{
		return ReplicationCache.mappedUIDs.get(changeGroup.getUid()) != null ? ReplicationCache.mappedUIDs.get(changeGroup.getUid()) : changeGroup.getUid();
	}
	
	
	/**
	 * Rellena los stringbuffers columnNames y columnValues con los datos correspondientes
	 * @param columnNames
	 * @param columnValues
	 * @param columnName
	 * @param columnType
	 * @param newValue
	 */
	private void insertColumnSQLSentence(StringBuffer columnNames, StringBuffer columnValues, Column column, String keyColumnName, String tableName, ChangeGroup changeGroup) throws Exception
	{
		try
		{
			/* Si no puedo determinar si requiere comillas, será necesario ignorar esta columna */
			Boolean requiresQuotes = requiresQuotes(column);
			
			/* Incorporar nueva columna a insertar */
			columnNames.append( column.getName() + "," );
			
			/* ¿Columnas especiales? */
			if (appendSpecialValues(columnValues, column, tableName, true, changeGroup))
				return;
			
			/* Incorporar comillas a la sentencia SQL o no según sea necesario */
			if (requiresQuotes)
				appendQuotedValue(columnValues, column.getNewValue());
			else
			{
				/* Si es la columna clave, buscar el siguiente ID de la tabla, ya que no utiliza el ID del XML original */
				if (column.getName().toLowerCase().equals(keyColumnName))
					appendKeyColumnValue(columnNames, columnValues, tableName, column.getNewValue());
				else 
					appendNotQuotedValue(columnValues, column);
			}
		}
		catch (Exception e)
		{
			raiseException("ERROR: Error generando sentencia de inserción. " + e.getMessage() + " - tableName:" + tableName + " - column:" + column.getName() + " - columnValues: " + columnValues.toString() + " - columnNames:" + columnNames.toString());
		}
	}
	
	/**
	 * Inserta el campo de columna clave.  En este caso el newValue no es utilizado.
	 * Directamente lee desde la información de las secuencias el proximo ID a usar.
	 * (Outline del método para redefiniciones en subclases)  
	 * @param columnValues
	 * @param tableName
	 */
	protected void appendKeyColumnValue(StringBuffer columnNames, StringBuffer columnValues, String tableName, String valueID)
	{
		keyColumnValue = MSequence.getNextID(Env.getAD_Client_ID(Env.getCtx()), tableName, m_trxName);
		columnValues.append( keyColumnValue + "," );	
	}
	
	
	/**
	 * Rellena los string buffers correspondientes
	 * @param columnChanges
	 * @param columnName
	 * @param columnType
	 * @param newValue
	 */
	private void updateColumnSQLSentence(StringBuffer sql, Column column, String tableName, ChangeGroup changeGroup) throws Exception
	{
		try
		{
			/* Si no puedo determinar si requiere comillas, será necesario ignorar esta columna */
			Boolean requiresQuotes = requiresQuotes(column);
					
			/* Incorporar nueva columna a modificar */
			sql.append( column.getName() + "=" );
			
			/* ¿Columnas especiales? */
			if (appendSpecialValues(sql, column, tableName, true, changeGroup))
				return;
			
			/* Incorporar comillas a la sentencia SQL o no según sea necesario */	
			if (requiresQuotes)
				appendQuotedValue(sql, column.getNewValue());
			else 
				appendNotQuotedValue(sql, column);
		}
		catch (Exception e)
		{
			raiseException("ERROR: Error generando sentencia de actualización. " + e.getMessage() + " - tableName:" + tableName + " - column:" + column.getName() + " - sql: " + sql.toString());
		}
		
	}
	
	/**
	 * Incorpora al query, la insercion del nuevo valor alfanumerico, tanto para inserts como para appends
	 * Escapea comillas simples y dobles en caso de presentarse estos caracteres
	 * @param sql
	 * @param value
	 * @throws Exception
	 */
	protected void appendQuotedValue(StringBuffer sql, String value) throws Exception
	{
		value = value.replaceAll("'", "\\\\'");
		value = value.replaceAll("\\\"", "\\\\\"");
		sql.append( "'" + value + "'," );
	}
	
	
	/**
	 * Incorpora al query, la inserción del nuevo valor, tanto para inserts como para appends
	 * Busca, en caso de ser necesario, la existencia de referencias para mapear entre los Ids de desarrollo y los de producción 
	 * @param query
	 * @param column
	 * @throws Exception
	 */
	protected void appendNotQuotedValue(StringBuffer query, Column column) throws Exception
	{
		/* Es un referencia? En ese caso debe mapearse la misma utilizando el ComponentObjectUID de referencia */
		if (isReferenceColumn(column))
		{
			/* Determinar columna ID y registro al cual referenciar */
			String refKeyColumnName = getKeyColumnName(column.getRefTable());
			int refRecordID = getReferenceRecordID(refKeyColumnName, column);
			
			/* Insertar la referencia basandose en los valores de identificadores de la nueva base de datos */
			query.append( (refRecordID==-1?"null":refRecordID) ).append( "," );
		}	
		else
			/* En caso contrario, realizar inserción general */
			query.append( column.getNewValue() + "," );
	}
	
	/**
	 * Outline de validación a metodo para redefinición en subclases
	 * @return true si la columna es una referencia a otra tabla 
	 */
	protected boolean isReferenceColumn(Column column)
	{
		return (!"".equals(column.getRefUID()) && !"".equals(column.getRefTable()));
	}
	
	/**
	 * Recupera el ID referencial a partir del ObjectComponentUID
	 * (Outline a método de recuperación para redefinición en subclases) 
	 * @param refKeyColumnName columna ID de la tabla donde hay que buscar la referencia
	 * @param column columna con la información relacionada a la busqueda
	 * @return el ID a utilizar para la foreign-key
	 * @throws Exception en caso de error
	 */
	protected int getReferenceRecordID(String refKeyColumnName, Column column) throws Exception
	{
		/* Verificar si el refUID apunta a un registro mapeado insertado en esta ejecución (de ser así, mapear al nuevo UID generado) */
		String uid = ReplicationCache.mappedUIDs.get(column.getRefUID())!=null ? ReplicationCache.mappedUIDs.get(column.getRefUID()) : column.getRefUID();
		/* Valor a retornar */
		String refRecordIDSQL = " SELECT " + refKeyColumnName + " FROM " + column.getRefTable() + " WHERE " + getUIDWhereClause(uid);
		int retValue = DB.getSQLValue(m_trxName, refRecordIDSQL, true );
		
		/* Elevar una excepción si no pudieron mapearse correctamente las referencias se dispara la excepción correspondiente */
		if (refKeyColumnName == null || retValue == -1)
			throw new Exception(WARNING_CANNOT_RESOLVE_UID + ": (" + refRecordIDSQL + ")");
	
		return retValue;
	}
	
	/**
	 * @return Sentencia sql para el retorno del UID
	 */
	protected String getUIDWhereClause(String uid)
	{
		return " AD_ComponentObjectUID = '" + uid + "'";
	}
	
	/**
	 * Dado el nombre de una tabla, retornar la columna clave de la misma 
	 */
	protected String getKeyColumnName(String tableName)
	{
		/* Caso general */
		return ReplicationCache.keyColumns.get(tableName.toLowerCase()); 
	}
	
	/**
	 * Columnas especiales que será necesario cargar con valores específicos, ignorando el valor del XML
	 */
	protected boolean appendSpecialValues(StringBuffer query, Column column, String tableName, boolean useQuotes, ChangeGroup changeGroup) throws Exception
	{
		boolean retValue = false;
		String quotes = (useQuotes?"'":"");
		
		/* Valores en null deben ser pasados tal como null */
		if ("null".equalsIgnoreCase(column.getNewValue()))
		{
				query.append("null");
				retValue = true;
		}
		/* Columna de fecha de creación */
		else if ("Created".equalsIgnoreCase(column.getName()))
		{
				query.append( "NOW()" );
				retValue = true;
		}
		/* Columna de fecha de actualizacion */
		else if ("Updated".equalsIgnoreCase(column.getName()))
		{
				query.append( "NOW()" );
				retValue = true;
		}		
		/* Columna de usuario que crea el registro */
		else if ("CreatedBy".equalsIgnoreCase(column.getName()))
		{
				query.append( Env.getAD_User_ID(Env.getCtx()) );
				retValue = true;
		}
		/* Columna de usuario que actualiza el registro */
		else if ("UpdatedBy".equalsIgnoreCase(column.getName()))
		{
				query.append( Env.getAD_User_ID(Env.getCtx()) );
				retValue = true;
		}
		/* Columna de tablas de traduccion que referencian a AD_Language */
		else if ((tableName.toLowerCase().endsWith("_trl") || tableName.equalsIgnoreCase("C_Bpartner") )&& "AD_Language".equalsIgnoreCase(column.getName()) && column.getRefUID() != null && column.getRefUID().length()>0)
		{
				String adLanguage = DB.getSQLValueString(m_trxName, " SELECT AD_Language FROM AD_Language WHERE " + getUIDWhereClause(column.getRefUID()) + " AND 0 = ?", 0, true);
				query.append( quotes + adLanguage + quotes);
				retValue = true;
		}
		/* Para los tipos boton, convertir true o false a Y o N */
		else if (DisplayType.Button == Integer.parseInt(column.getType()) && (column.getNewValue().equalsIgnoreCase("true") ||  column.getNewValue().equalsIgnoreCase("false")))
		{
				query.append( quotes + (column.getNewValue().equalsIgnoreCase("true")?"Y":"N") + quotes );
				retValue = true;
		}
		/* Si estamos insertando en un nodo del arbol, inicialmente se guarda con cero, ignorar la referencia y guardar 0 */
		else if (tableName.toLowerCase().startsWith("ad_treenode") && column.getName().equalsIgnoreCase("Parent_ID") && column.getNewValue().equals("0"))
		{
				query.append("0");
				retValue = true;
		}
		else if (column.getName().equalsIgnoreCase("AD_Client_ID") && column.getNewValue().equalsIgnoreCase(AD_CLIENT_REL1003) && isLibertyaRelease0910())
		{
			query.append(AD_CLIENT_REL0910);
			retValue = true;
		}
		else if (column.getName().equalsIgnoreCase("AD_Org_ID") && column.getNewValue().equalsIgnoreCase(AD_ORG_REL1003) && isLibertyaRelease0910())
		{
			query.append(AD_ORG_REL0910);
			retValue = true;
		}
		/* Si el tipo de dato es de tipo fecha o fechahora, y recibimos un string sin caracteres, deberemos convertirlo a null para que postgre lo acepte */
		else if ( (DisplayType.Time == Integer.parseInt(column.getType()) || DisplayType.Date == Integer.parseInt(column.getType()) || DisplayType.DateTime == Integer.parseInt(column.getType()) ) && "".equals(column.getNewValue()) )
		{
			query.append("null");
			retValue = true;
		}	
		/* Overrides especiales por mapeo del componente */
		else if (mapToComponent)
		{
			/* Si estoy insertando un nuevo registro... */
			if (MChangeLog.OPERATIONTYPE_Insertion.equals(changeGroup.getOperation()))
			{
				/* Debo mapear y actualizar el UID del changeGroup */
				if (!changeGroup.isMapped())
				{
					changeGroup.setMapped(true);
					newMapUID(changeGroup);
					changeGroup.setUid(ReplicationCache.mappedUIDs.get(changeGroup.getUid()));
				}
				
				/* ...el UID debe ser mapeado */
				if (column.getName().equalsIgnoreCase("AD_ComponentObjectUID"))
				{
					query.append(quotes + changeGroup.getUid() + quotes);
					retValue = true;
				}
				/* ...el ComponentVersion debe ser reasignado */
				if (column.getName().equalsIgnoreCase("AD_ComponentVersion_ID"))
				{
					query.append(Env.getContext(Env.getCtx(), PluginConstants.INSTALLED_COMPONENTVERSION_ID));
					retValue = true;			
				}
			}
			/* En caso de modificacion debo determinar si hay que mapear o no (si es un registro insertado en este proceso o uno ya existente) */
			else
			{
				/* Todavia no se mapeo? */
				if (!changeGroup.isMapped())
				{
					/* Indicar como mapeado (se mapee o no, simplemente es para no volver a ejecutar en todas las columnas */
					changeGroup.setMapped(true);
					// Actualizar el UID del changeGroup si corresponde
					if (ReplicationCache.mappedUIDs.get(changeGroup.getUid()) != null)
						changeGroup.setUid(ReplicationCache.mappedUIDs.get(changeGroup.getUid()));
				}
			}
		}
		/* Si es una columna especial, concatenar la coma final */
		if (retValue)
			query.append(",");
		
		return retValue;
				
	}
	
	/**
	 * Incorpora una nueva asociación a la map
	 * Ej: 
	 * 		Original: ASET-AD_Message-2348733
	 * 		Nuevo:    ASET2CORE-AD_Message-2348733-20120319113805
	 */
	protected void newMapUID(ChangeGroup changeGroup)
	{
		// Guardar viejo prefix. Ej. ASET
		String oldPrefix = Env.getContext(Env.getCtx(), PluginConstants.COMPONENT_SOURCE_PREFIX);
		
		// Generar nuevo prefix. Ej. ASET2CORE
		String newPrefix = oldPrefix + "2" + Env.getContext(Env.getCtx(), PluginConstants.PROP_PREFIX); 
		
		// Guardar viejo y nuevo UID 
		String oldUID = changeGroup.getUid();
		String newUID = changeGroup.getUid().replace(oldPrefix, newPrefix) + "-" + Env.getDateTime("yyyyMMddHHmmss");
		
		// Incluir el nuevo UID en la nomina de UIDs mapeados
		ReplicationCache.mappedUIDs.put(oldUID, newUID);
	}
	
	protected Boolean requiresQuotes(Column column) throws Exception
	{
		Boolean requiresQuotes = DisplayType.requiresQuotes(Integer.parseInt(column.getType()));
		if (requiresQuotes == null)
			raiseException(WARNING_DISPLAY_TYPE_UNKNOWN + ": " + column.getType());
		return requiresQuotes;
	}
	
	/** -------------------------------------------------------------------------------- */
	/** ------------------------------- Clases internas -------------------------------- */
	/** -------------------------------------------------------------------------------- */
	
	/**
	 * Nodo Column y sus componentes
	 */
	public class Column
	{
		/* Componentes de la clase */
		private String name = null;
		private String type = null;
		private String oldValue = null;
		private String newValue = null;
		private String algorithm = null;
		private String refTable = null;
		private String refUID = null;
		
		
		/* Constructor */
		public Column(String name, String type, String algorithm, String oldValue, String newValue, String refTable, String refUID)
		{
			this.name = name;
			this.type = type;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.refTable = refTable;
			this.algorithm = algorithm;
			this.refUID = refUID;
		}
		
		/* Getters & Setters */
		public String getName() 					{			return name;					}
		public void setName(String name) 			{			this.name = name;				}
		public String getType() 					{			return type;					}
		public void setType(String type) 			{			this.type = type;				}
		public String getOldValue() 				{			return oldValue;				}
		public void setOldValue(String oldValue) 	{			this.oldValue = oldValue;		}
		public String getNewValue() 				{			return newValue;				}
		public void setNewValue(String newValue) 	{			this.newValue = newValue;		}
		public String getAlgorithm() 				{			return algorithm;				}
		public void setAlgorithm(String algorithm) 	{			this.algorithm = algorithm;		}
		public String getRefTable() 				{			return refTable;				}
		public void setRefTable(String refTable)	{			this.refTable = refTable;		}
		public String getRefUID()					{			return refUID;					}
		public void setRefUID(String refUID) 		{			this.refUID = refUID;			}
	}


	/**
	 * Nodo ChangeGroup y sus componentes
	 */
	public class ChangeGroup
	{
		/* Componentes de la clase */
		private Vector<Column> columns = null; // new Vector<Column>();
		private String tableName = null;
		private String operation = null;
		private String uid = null;
		private boolean mapped = false;
		private boolean processed = false;
		
		/* Constructor */
		public ChangeGroup(String tableName, String operation, String uid, Vector<Column> columns)
		{
			this.uid = uid;
			this.columns = columns;
			this.tableName = tableName;
			this.operation = operation;
			this.processed = false;
		}
		
		/* Retorna una columna a partir del nombre la misma */
		public Column findByColumnName(String columnName)
		{
			boolean found = false;
			int i = 0;
			for (i = 0; i < getColumns().size() && !found; i++)
				if ((getColumns().get(i)).getName().equalsIgnoreCase(columnName))
					found = true;
			return getColumns().get(i);
		}
		
		/* Getters & Setters */
		public Vector<Column> getColumns() 				{			return columns;					}
		public void setColumns(Vector<Column> columns) 	{			this.columns = columns;			}
		public String getTableName() 					{			return tableName;				}
		public void setTableName(String tableName) 		{			this.tableName = tableName;		}
		public String getOperation() 					{			return operation;				}
		public void setOperation(String operation) 		{			this.operation = operation;		}
		public String getUid() 							{			return uid;						}
		public void setUid(String uid) 					{			this.uid = uid;					}
		public boolean isMapped() 						{			return mapped;					}
		public void setMapped(boolean mapped) 			{			this.mapped = mapped;			}
		public boolean isProcessed()					{			return processed;				}
		public void setProcessed(boolean processed)		{			this.processed = processed;		}
		
	}
	
	
	/**
	 * Documento principal 
	 */
	public class XMLUpdateDocument
	{
		/* Componentes de la clase */
		private Vector<ChangeGroup> chageGroupList = null;

		/* Constructor por defecto, que rellena la estructura a partir del doc */
		public XMLUpdateDocument(Document doc)
		{
			chageGroupList = fillDocument(doc);
		}

		/* Constructor adicional, que utiliza unicamenteun changeGroup */
		public XMLUpdateDocument(ChangeGroup aGroup)
		{
			chageGroupList = new Vector<ChangeGroup>();
			chageGroupList.add(aGroup);
		}
		
		/* Getters & Setters */
		public Vector<ChangeGroup> getChageGroupList() 	{
			return chageGroupList;			
		}
		
		/** Devuelve un documento con las modificaciones a realizar */
		private Vector<ChangeGroup> fillDocument(Document doc)
		{
			Vector<ChangeGroup> aDoc = new Vector<ChangeGroup>();
			
			// Obtener el listado de changeGroups
			NodeList nodes = doc.getElementsByTagName("changegroup");
			
			// Iterar por cada changeGroup e incorporarlos
			for (int i = 0; i < nodes.getLength(); i++)
				aDoc.add(fillChangeGroup((Element)nodes.item(i)));
			
			return aDoc;
		}


		/** Devuelve un changeGroup con todas sus propiedades */
		private ChangeGroup fillChangeGroup(Element elem)
		{	
			Vector<Column> columns = new Vector<Column>();
			
			// Obtener el listado de columnas
			NodeList nodes = elem.getElementsByTagName("column");
			
			// Iterar por cada column del registro
			for (int j = 0; j < nodes.getLength(); j++)
				columns.add(fillColumns((Element)nodes.item(j)));
		
			// retornar el changeGroup completo
			ChangeGroup changeGroup = new ChangeGroup(elem.getAttribute("tableName"), elem.getAttribute("operation"), elem.getAttribute("uid"), columns);
			return changeGroup;
		}
		
		
		/** Devuelve un column con todas sus propiedades */
		private Column fillColumns(Element elem)
		{	
			String oldValue = null;
			String newValue = null;
			String refUID = null;
			String refTable = elem.getAttribute("refTable");
			
			// Obtener old & new value
			NodeList list = elem.getChildNodes();
			for (int k=0; k < list.getLength(); k++)
			{
				if (list.item(k)==null)
					continue;
				
				String key = list.item(k).getNodeName();
				String value = list.item(k).getTextContent();

				/* Guardar old y new value, en caso de ser una referencia, se almacena la misma para utilizar posteriormente */
				if ("oldValue".equals(key))
					oldValue = value;
				if ("newValue".equals(key))
				{
					newValue = value;
					refUID = ((Element)list.item(k)).getAttribute("refUID"); 
				}
			}

			// retornar el changeGroup completo
			Column aColumn = new Column(elem.getAttribute("name"), elem.getAttribute("type"), elem.getAttribute("algorithm"), oldValue, newValue, refTable, refUID);
			return aColumn;

		}
		
	}

	
	/**
	 * Procesa el contenido completo del SQL que recibe (simple o multiples sentencias)
	 * @param sql queries a ejecutar
	 * @param trxName
	 * @throws Exception
	 */
	public static void executeUpdate(String sql, String trxName) throws Exception
	{
		// guardar savepoint en caso de error
		Connection conn = (trxName != null ? Trx.getTrx(trxName).getConnection() : null);
		Savepoint savePoint = (conn != null ? conn.setSavepoint(savePointName) : null);
		try 
		{
			   
			if (sql != null && sql.length() > 0)
			{
				CPreparedStatement cs = new CPreparedStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, trxName, true);
				cs.executeUpdate();
				cs.close();
			}
		}
		catch (Exception e)
		{
			// rollbackear a ultimo savepoint
			if (trxName != null)
				conn.rollback(savePoint);
			raiseException("ERROR: Error en ejecución de sentencia SQL. " + e.getMessage() + " - SQL: " + sql);
		}
		finally
		{
			if (trxName != null)
				conn.releaseSavepoint(savePoint);
		}
	}
	
	/**
	 * Verifica si la version de Libertya que utiliza es la 09.10
	 * Esto lo hace chequeando el ID de la compañía (el cual en la version 9.10 es 1000010)
	 * @return
	 */
	private static boolean isLibertyaRelease0910()
	{
		if (isLibertyaRelease0910 != null)
			return isLibertyaRelease0910;
			
		isLibertyaRelease0910 = (1 == DB.getSQLValue(m_trxName, " SELECT COUNT(1) FROM AD_Client WHERE AD_Client_ID = 1000010 " ));
		return isLibertyaRelease0910;
		
	}
	
	/**
	 * Impacta la inserción/modificación/cambio en el changelog 
	 * @param column
	 * @param changeGroup
	 */
	protected void persistInChangelog(ChangeGroup changeGroup) throws Exception
	{
		int changelogGroupID = -1;
		int componentVersion = -1;
		int recordID = -1;
		String newValue = "";
		try
		{
			// Crear un nuevo changegroup
			changelogGroupID = MSequence.getNextSequenceID(Env.getAD_Client_ID(Env.getCtx()), "seq_ad_changelog_group", m_trxName);
	
			// Obtener el componentversion
			componentVersion = Integer.parseInt(Env.getContext(Env.getCtx(), PluginConstants.INSTALLED_COMPONENTVERSION_ID));
			if (componentVersion <= 0)
				throw new Exception("Version de componente incorrecta: " + componentVersion);

			// Si estoy insertando, utilizar el nextValue correspondiente a la tabla, en caso contrario usar el UID
			if (MChangeLog.OPERATIONTYPE_Insertion.equals(changeGroup.getOperation()))
				recordID = keyColumnValue;
			else
				recordID = resolveKeyFromUID(m_trxName, changeGroup.getTableName(), getUniversalReference(changeGroup));
		
			// Iterar por las columnas y persistir en el changelog
			for (Column column : changeGroup.getColumns())
			{
				// Si es una columna especial, procesarla acordemente (ignorando XML)
				StringBuffer query = new StringBuffer();
				if (appendSpecialValues(query, column, changeGroup.getTableName(), false, changeGroup))
					newValue = query.toString().substring(0, query.length()-1);
				else
				{
					// Inicialmente el valor por defecto
					newValue = column.getNewValue();

					// Si la columna referencia a otra columna, usar su UID para resolver el ID						
					if (isReferenceColumn(column))
					{
						String refKeyColumnName = getKeyColumnName(column.getRefTable());
						int refRecordID = getReferenceRecordID(refKeyColumnName, column);
						newValue = (refRecordID == -1 ? "null" : Integer.toString(refRecordID));
					}
				}
				// Instanciar y persistir en el changelog
				MChangeLog aChangeLog = null;
				/* DisplayType: usamos siempre String dado que asi esta en el XML. */
				aChangeLog = new MChangeLog(Env.getCtx(), 0, m_trxName, 666, M_Table.getID(changeGroup.getTableName()), M_Column.getColumnID(m_trxName, column.getName(), changeGroup.getTableName()), recordID, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()), column.getOldValue(), newValue, changeGroup.getUid(), componentVersion, changeGroup.getOperation(), DisplayType.String, changelogGroupID);
				if (!aChangeLog.insertDirect())
					throw new Exception(" Error al guardar el changelog: UID:" + changeGroup.getTableName() + " - OP:" + changeGroup.getOperation() + " - NewValue:" + column.getNewValue());
			}
		}
		catch (Exception e)	{
			throw new Exception(" Imposible replicar instalacion en Changelog: " + e.getMessage());
		}
	}
	
	/**
	 * A partir de una referencia universal, devuelve el ID local
	 */
	protected int resolveKeyFromUID(String trxName, String tableName, String reference)
	{
		return DB.getSQLValue(trxName, 
								" SELECT " + getKeyColumnName(tableName) + 
								" FROM " + tableName + 
								" WHERE " + appendUniversalRefenceWhereClause(reference), true);
	
	}

	/**
	 * Verifica si debe copiar a la tabla AD_Changelog, el changelog recibido a ser procesado
	 */
	protected boolean shouldCopyToChangelog()
	{
		return "Y".equals(Env.getContext(Env.getCtx(), PluginConstants.PROP_COPY_TO_CHANGELOG));	
	}
	
	/**
	 * Verifica si debe mapear el component
	 */
	protected boolean shouldMapToComponent()
	{
		return "Y".equals(Env.getContext(Env.getCtx(), PluginConstants.MAP_TO_COMPONENT));	
	}

}
