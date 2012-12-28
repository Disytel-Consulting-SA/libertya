package org.openXpertya.replication;

/**
 * Redefinición de PluginXMLUpdater a fin de 
 * procesar los XMLs de replicación basándose en las
 * convenciones especiales para recuperación de referencias (FK)
 * 
 * 
 * -------------------------------------------------------------------------
 * Para cada changegroup, el UID es utilizado para alamacenar el retrieveUID
 * -------------------------------------------------------------------------
 */

import java.util.Vector;

import org.openXpertya.model.MTableReplication;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.plugin.install.PluginXMLUpdater.ChangeGroup;
import org.openXpertya.process.CreateReplicationTriggerProcess;
import org.openXpertya.util.DB;


public class ReplicationXMLUpdater extends PluginXMLUpdater {

	/** Sucursal origen de la cual se está replicando */
	protected int m_AD_Org_ID = -1;
	
	/** Realizar una sola pasada o doble pasada sobre el changeGroup? */
	protected boolean doublePass = false;
	
	/** Pasada actual */
	protected int currentPass = -1;

	/** Relacion entre la posicion del una organizacion en el replicationArrayPos y el AD_Org_ID */
	
		
	/** Log de ACKS/Errores a enviar al host origen */
	protected Vector<String[]> eventLog = null; 
	
	/**
	 * Constructor con ID de organizacion origen
	 */
	public ReplicationXMLUpdater(String xml, String trxName, int AD_Org_ID) throws Exception
	{
		super(xml, trxName, true);
		m_AD_Org_ID = AD_Org_ID;
	}
	

	/** 
	 * Procesa el changelog recibido.  Intenta impactar en BBDD
	 * todas las sentencias SQL convertidas a partir del XML.
	 * Reintenta nuevamente (processTemporalErrors), debido
	 * a posibles errores temporales por dependencia de datos.
	 */
	public void processChangeLog() throws Exception
	{
		// Vaciar el eventLog a devolver al origen
		eventLog = new Vector<String[]>();
				
		// Reiniciar el changelog
		PluginUtils.resetStatus();
		
		// Existen varias operaciones a replicar? En ese caso utilizar pasada multiple
		doublePass = false;
		currentPass = 1;
		if (getUpdateDocument() != null && getUpdateDocument().getChageGroupList() != null && getUpdateDocument().getChageGroupList().size() > 1)
			doublePass = true;	
		
		// Replicar de manera tradicional
		super.processChangeLog();
		
		// Si se requiere una segunda pasada...
		if (doublePass && errorsRemainingInFirstPass())
		{
			currentPass = 2;
			// Replicar nuevamente para los casos que existan errores
			super.processChangeLog();
		}
	}
	
	/** Retorna true si algunas de las ejecuciones dieron error en la primer pasada */
	protected boolean errorsRemainingInFirstPass()
	{
		// Si el changeGroupList tiene mayor longitud que el eventLog (solo de OKs en la primer pasada),
		// entonces estamos ante la presencia de chageGroups con errores que pueden ser reintentados
		return getUpdateDocument().getChageGroupList().size() > eventLog.size();
	}
	
	/** Incorporar el ack en el eventLog */
	protected void handleSuccess(String sentence, ChangeGroup changeGroup) throws Exception
	{
		// tablename;uid;opType;OK
		String data[] = {changeGroup.getTableName(), changeGroup.getUid(), changeGroup.getOperation(), "OK"};
		eventLog.add(data);
		
		// Setear el registro como ya procesado a fin de que en la segunda pasada no vuelva a ser procesado
		if (doublePass)
			changeGroup.setProcessed(true);
	}
	
	/** Incorporar el error en el eventLog */
	protected void handleException(Exception e, ChangeGroup changeGroup) throws Exception
	{
		// Incorporar el error solo en el caso que se este en la segunda pasada (ya no se puede solucionar localmente)
		if (!doublePass || (doublePass && currentPass == 2))
		{
			// tablename;uid;opType;ERROR:...
			String data[] = {changeGroup.getTableName(), changeGroup.getUid(), changeGroup.getOperation(), "ERROR:" + e.getMessage()};
			eventLog.add(data);
		}
	}
	

	
	/**
	 * @return la nomina de eventos
	 */
	public Vector<String[]> getEventLog() {
		return eventLog;
	}
	
	/**
	 * Redefinición para determinar si es una columna de referenia
	 */
	protected boolean isReferenceColumn(Column column)
	{
		/* Si tiene una referencia a una tabla, entonces es una columna de FK*/
		return (!"".equals(column.getRefTable()));
	}
	
	/**
	 * Redefinición para obtener el registro referenciado correcto según el retrieveUID
	 * (usado para recuperación de registros en columnas de tablas foraneas)
	 */
	protected int getReferenceRecordID(String refKeyColumnName, Column column) throws Exception
	{
		/* Determinar si la referencia a buscar está alojada en retrieveUID o bien es directa (no existe el registro en la tabla) */
		boolean useRetrieveUID = column.getNewValue().startsWith(ReplicationBuilder.UID_REFERENCE_PREFIX);	// (1 == DB.getSQLValue(m_trxName, "SELECT count(1) FROM information_schema.columns WHERE table_name = '" + column.getRefTable().toLowerCase() + "' AND column_name = 'retrieveuid'"));
		
		/* Si existe el campo retrieveUID, utilizar este, sino hacer bypass del dato (si es dato vacio pasar null) */
		if (!useRetrieveUID)
			return (column.getNewValue()==null||column.getNewValue().equals(""))?-1:Integer.parseInt(column.getNewValue());
				
		/* valor a retornar via retrieveUID (despreciar los 4 caracteres de UID) */
		String retrieveUIDSQL = " SELECT " + refKeyColumnName + " FROM " + column.getRefTable() + 
								" WHERE " + appendUniversalRefenceWhereClause(column.getNewValue().substring(ReplicationBuilder.UID_REFERENCE_PREFIX.length()));
		int retValue = DB.getSQLValue(m_trxName, retrieveUIDSQL, true);
		
		/* Elevar una excepción si no pudieron mapearse correctamente las referencias se dispara la excepción correspondiente */
		if (refKeyColumnName == null || retValue == -1)
			throw new Exception(" - imposible determinar referencia (" + retrieveUIDSQL + ")");
	
		return retValue;
	}
	
	/**
	 * Redefinición de método a fin de almacenar el retrieveUID (clave original en host origen)
	 * en la tabla correspondiente.  Es por esto que la tabla destino DEBE contener el campo retrieveUID
	 */
	protected void appendKeyColumnValue(StringBuffer columnNames, StringBuffer columnValues, String tableName, String valueID)
	{
		super.appendKeyColumnValue(columnNames, columnValues, tableName, valueID);
	}
		
	/**
	 * Redefinición para recuperación de ID
	 * Se deben contemplar casos en los que la referencia 
	 * sea una tabla con multiples columnas clave
	 */
	protected String appendUniversalRefenceWhereClause(String reference)
	{
		return " retrieveUID = '" + reference + "' ";
	}
	
	/**
	 * El campo retrieveID se encuentra almacenado en el UID del changeGroup
	 */
	protected String getUniversalReference(ChangeGroup changeGroup)
	{
		return changeGroup.getUid();	
	}

	/**
	 * El camo retrieveID se encuentra almacenado en el UID del changeGroup
	 */
	protected boolean validateUniversalReference(ChangeGroup changeGroup)
	{
		return (changeGroup.getUid() != null);
	}
	
	/**
	 * Redefinicion: Es necesario incorporar una columna mas: el retrieveUID!
	 * 				 Tambien hay que poner SKIP en repArray para que el trigger replication_event() no procese la sentencia
	 * 				Para los registros de tablas bidireccionales se requiere un procesamiento adicional
	 */
	protected void customizeInsertionQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Insertar en el query el nombre de las columnas 
		int lastColumnPos =  sql.indexOf(")");
		sql.insert(lastColumnPos, ",retrieveUID,repArray");
		// Insertar en el query los valores de las columnas
		int lastValuePos =  sql.lastIndexOf(")");
		
		int tableID = ReplicationCache.tablesIDs.get(changeGroup.getTableName().toLowerCase());
		String newRepArray = MTableReplication.getReplicationArray(tableID, m_trxName);
		// Es bidireccional? (algún 3 en alguna posicion).  Marcar el registro como "replicado", pero esto permite reenviarlo al origen si es modificado
		if (newRepArray.indexOf(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE) >= 0)
			newRepArray = "'SET" + newRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED) + "'";
		else
			newRepArray = "'SKIP'";
		sql.insert(lastValuePos, ",'" + changeGroup.getUid() + "',"+newRepArray);
	}
	
	/**
	 *	Se incorpora al query el campo repArray = SKIP a fin de no bitacorear entradas por replicacion
	 *  En este caso, luego debo poner el registro como replicable nuevamente (si corresponde)
	 */
	protected void customizeModificationQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Insertar el campo repArray antes del where
		sql.insert(sql.indexOf("WHERE"), ",repArray='SKIP' ");
		// Concatenar el UPDATE para repArray=0 (o uno diferente en caso de rep. bidireccional) luego del UPDATE principal
		int tableID = ReplicationCache.tablesIDs.get(changeGroup.getTableName().toLowerCase());
		String newRepArray = MTableReplication.getReplicationArray(tableID, m_trxName);
		// Es bidireccional? (algún 3 en alguna posicion).  Marcar el registro como "replicado", pero esto permite reenviarlo al origen si es modificado
		if (newRepArray.indexOf(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE) >= 0)
			newRepArray = newRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED);
		else
		// En caso contrario (no es bidireccional), simplemente dejarlo como un registro sin nueva posibilidad de reenvio
			newRepArray = CreateReplicationTriggerProcess.DUMMY_REPARRAY;
		sql.append(" UPDATE " + changeGroup.getTableName() + " SET repArray = 'SET" + newRepArray + "' " + sql.substring(sql.indexOf("WHERE"), sql.length()-1) + ";" );
		
	}
	
	/**
	 *	Se incorpora al inicio del query el dato repArray = 'SKIP' a fin de no replicar nuevamente entradas por replicacion
	 *  En este caso, primeramente debo setear al registro como SKIP y recien despues borrarlo
	 */
	protected void customizeDeletionQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Recuperar la posicion del WHERE en adelante
		int whereClausePos = sql.indexOf("WHERE");
		// Insertar el UPDATE antes del DELETE a fin de que no se bitacoree
		sql.insert(0, " UPDATE " + changeGroup.getTableName() + " SET repArray = 'SKIP' " + sql.substring(whereClausePos, sql.length()-1) + ";"  );
	}
	
	/**
	 * Redefiniciones especiales
	 */
	protected boolean appendSpecialValues(StringBuffer query, Column column, String tableName, boolean useQuotes, ChangeGroup changeGroup) throws Exception 
	{
		boolean retValue = false;
		String quotes = (useQuotes?"'":"");
		
		/* En la tabla C_BPartner se almacena el campo AD_Language, los cuales no son para replicacion, debido a que ya existen con anterioridad */
		if (tableName.equalsIgnoreCase("C_BPartner") && column.getName().equalsIgnoreCase("AD_Language") && (column.getRefUID() == null || column.getRefUID().length() == 0))
		{
			query.append( quotes + column.getNewValue() + quotes);
			retValue = true;
		}
		/* Tablas de traducciones, para el campo AD_Language no hay que resolver valores */
		else if (tableName.toLowerCase().endsWith("_trl") && "AD_Language".equalsIgnoreCase(column.getName()))
		{
			query.append( quotes + column.getNewValue() + quotes);
			retValue = true;
		}
		/* A fin de que el procesador contable genere las entradas contables correspondientes, el Posted deben ser pasadas como false */
		else if ("Posted".equals(column.getName()))
		{
			query.append( quotes + "N" + quotes);
			retValue = true;
		}
		/* Dado que una misma sucursal puede tener un AD_Org_ID distinto en cada host, se debe realiza el mapeo correspondiente 
		 * (siempre y cuando sea una organizacion con valor distinto de cero, en este caso no es necesario realizar mapeo alguno
		 * 	Para el caso en que la tabla AD_Org se encuentra marcada para replicación, el registro "0"
		 * 	en realidad llegará como UID=AD_Org-0, con lo cual hay que tener en cuenta este caso adicional */
		else if ("AD_Org_ID".equals(column.getName()) && (!"0".equals(column.getNewValue())) && (!(ReplicationBuilder.UID_REFERENCE_PREFIX+"AD_Org-0").equals(column.getNewValue())))
		{
			// En el AD_Org_ID en realidad no me llega el AD_Org_ID sino que me llega el host (replicationArrayPos) cargado,
			// dado que este es el único valor en común que comparten todas las organizaciones
			Integer newOrgID = ReplicationCache.map_RepArrayPos_OrgID_inv.get(Integer.parseInt(column.getNewValue()));

			// Si no se puede mapear la sucursal, elevar la excepcion correspondiente
			if (newOrgID == null)
				raiseException(" - imposible determinar el AD_Org_ID para la sucursal (" + column.getNewValue() + ")");

			// Incorporar el AD_Org_ID correspondiente en este host
			query.append(newOrgID);
			retValue = true;
		}
		/*
		 * Omision de referencia ciclica entre C_Invoice -> C_CashLine y C_CashLine -> C_Invoice
		 * La referencia a C_CahsLine desde C_Invoice se encuentra actualmente deprecada
		 */
		else if (X_C_Invoice.Table_Name.equalsIgnoreCase(tableName) && "C_CashLine_ID".equalsIgnoreCase(column.getName()))
		{
			query.append("null");
			retValue = true;
		}
		/* Si es una columna especial, concatenar la coma final */
		if (retValue)
			query.append(",");
		
		/* Si ya se aplico una columna especial, entonces no invocar a super */
		return retValue || super.appendSpecialValues(query, column, tableName, true, changeGroup);
	}

	
	/**
	 * Redefinicion: para replicacion no debe impactarse en la tabla AD_Changelog
	 */
	protected boolean shouldCopyToChangelog()
	{
		return false;
	}
	
	/** Redefinicion, no elevar excepcion si el registro en cuestion ya existía */
	protected void handleRecordExistsOnInsert(ChangeGroup changeGroup) throws Exception	{
	}
	
	/** Redefinicion, no elevar excepcion si el registro en cuestion no existía */
	protected void handleRecordNotExistsOnDelete(ChangeGroup changeGroup) throws Exception	{
	}
	
	/**
	 * Redefinicion, no enviar a Consola a fin de reducir tiempo de procesamiento
	 */
	protected void appendStatus(String sentence)
	{
		PluginUtils.appendStatus(" SQL: " + sentence, false, false, false, false);
	}

	/**
	 * Si el changelog actual esta procesado, entonces no reprocesarlo
	 */
	protected boolean shouldSkipCurrentChangeGroup(ChangeGroup changeGroup)
	{
		return changeGroup.isProcessed();
	}
	
}
