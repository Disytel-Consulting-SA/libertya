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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MTableReplication;
import org.openXpertya.model.X_AD_Client;
import org.openXpertya.model.X_AD_Sequence;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_Cash;
import org.openXpertya.model.X_C_CashLine;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_M_Transfer;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.plugin.install.PluginXMLUpdater.ChangeGroup;
import org.openXpertya.process.CreateReplicationTriggerProcess;
import org.openXpertya.util.DB;


public class ReplicationXMLUpdater extends PluginXMLUpdater {

	/** Sucursal origen de la cual se está replicando */
	protected int m_AD_Org_ID = -1;

	/** Sucursal origen de la cual se está replicando */
	protected int thisHostPos = -1;
	
	/** Realizar una sola pasada o doble pasada sobre el changeGroup? */
	protected boolean doublePass = false;
	
	/** Pasada actual */
	protected int currentPass = -1;
		
	/** Log de ACKS/Errores a enviar al host origen */
	protected Vector<String[]> eventLog = null; 

	
	/**
	 * Constructor con ID de organizacion origen, y posicion de este hosts
	 */
	public ReplicationXMLUpdater(String xml, String trxName, int AD_Org_ID, int thisHostPos) throws Exception
	{
		super(xml, trxName, true);
		m_AD_Org_ID = AD_Org_ID;
		this.thisHostPos = thisHostPos;
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
		boolean useRetrieveUID = column.getNewValue().startsWith(ReplicationBuilder.RUID_REFERENCE_PREFIX);	
		boolean useComponentObjectUID = column.getNewValue().startsWith(ReplicationBuilder.CUID_REFERENCE_PREFIX);	
		
		/* Si es referencia mediante retrieveUID (o componentObjectUID), utilizar este, sino hacer bypass del dato (si es dato vacio pasar null) */
		if (!useRetrieveUID && !useComponentObjectUID)
			return (column.getNewValue()==null||column.getNewValue().equals(""))?-1:Integer.parseInt(column.getNewValue());
		
		int retValue = -1;
		String retrieveUIDSQL = null;
		/* valor a retornar via retrieveUID (despreciar los 4 caracteres de UID) */
		if (useRetrieveUID) {
			retrieveUIDSQL = " SELECT " + refKeyColumnName + " FROM " + column.getRefTable() + 
		  					 " WHERE " + appendUniversalRefenceWhereClause(column.getNewValue().substring(ReplicationBuilder.RUID_REFERENCE_PREFIX.length()));
			retValue = DB.getSQLValue(m_trxName, retrieveUIDSQL, true);
		}
		
		/* valor a retornar via componentObjectUID (despreciar los 4 caracteres de CID) */
		if (useComponentObjectUID) {
			retrieveUIDSQL = " SELECT " + refKeyColumnName + " FROM " + column.getRefTable() + 
		  					 " WHERE AD_ComponentObjectUID = '" + column.getNewValue().substring(ReplicationBuilder.CUID_REFERENCE_PREFIX.length()) + "'";
			retValue = DB.getSQLValue(m_trxName, retrieveUIDSQL, true);
		}
		
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
		
		// Recuperar configuracion de repArray para la tabla dada
		int tableID = ReplicationCache.tablesIDs.get(changeGroup.getTableName().toLowerCase());
		String newRepArray = MTableReplication.getReplicationArray(tableID, m_trxName);
		// Es bidireccional? (algún 3 en alguna posicion).  Marcar el registro como "replicado", pero esto permite reenviarlo al origen si es modificado
		if (newRepArray.indexOf(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE) >= 0)
			newRepArray = "'SET" + newRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED)
											  .replace(ReplicationConstants.REPLICATION_CONFIGURATION_SEND, ReplicationConstants.REPARRAY_REPLICATED) + "'";
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
			newRepArray = newRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED)
									 .replace(ReplicationConstants.REPLICATION_CONFIGURATION_SEND, ReplicationConstants.REPARRAY_REPLICATED);
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
			query.append( "null".equalsIgnoreCase(column.getNewValue()) ? "null" : quotes + column.getNewValue() + quotes);
			retValue = true;
		}
		/* Tablas AD_Client y tablas de traducciones, para el campo AD_Language no hay que resolver valores */
		else if ((X_AD_Client.Table_Name.equalsIgnoreCase(tableName) || tableName.toLowerCase().endsWith("_trl")) && "AD_Language".equalsIgnoreCase(column.getName()))
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
		/* El created y updated también deberían copiarse como cualquier otro dato (no utilizar NOW() como lo hace la superclase) */
		else if (("Created".equalsIgnoreCase(column.getName()) || "Updated".equalsIgnoreCase(column.getName())) && column.getNewValue()!=null && column.getNewValue().length() > 0 )
		{
			query.append( quotes + column.getNewValue() + quotes );
			retValue = true;
		}
		/* Dado que una misma sucursal puede tener un AD_Org_ID distinto en cada host, se debe realiza el mapeo correspondiente 
		 * (siempre y cuando sea una organizacion con valor distinto de cero, en este caso no es necesario realizar mapeo alguno
		 * 	Para el caso en que la tabla AD_Org se encuentra marcada para replicación, el registro "0"
		 * 	en realidad llegará como UID=AD_Org-0, con lo cual hay que tener en cuenta este caso adicional */
		else if ("AD_Org_ID".equals(column.getName()) && (!"0".equals(column.getNewValue())) && (!(ReplicationBuilder.RUID_REFERENCE_PREFIX+"AD_Org-0").equals(column.getNewValue())))
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
		 * No modificar el valor actual de los campos currentnext y currentnextsys de las secuencias a fin de evitar pisar los valores actuales 
		 */
		else if (MChangeLog.OPERATIONTYPE_Modification.equals(changeGroup.getOperation()) && X_AD_Sequence.Table_Name.equalsIgnoreCase(tableName))
		{
			if ("currentnext".equalsIgnoreCase(column.getName())) {
				query.append("currentnext");
				retValue = true;
			}
			else if ("currentnextsys".equalsIgnoreCase(column.getName())) {
				query.append("currentnextsys");
				retValue = true;
			}
			else 
				retValue = false;
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
	
	/** Redefinicion, no elevar excepcion si el registro en cuestion ya existía.  Generar sentencia de actualizacion en su lugar */
	protected StringBuffer handleRecordExistsOnInsert(ChangeGroup changeGroup) throws Exception	{
		return processInsertModify(changeGroup);
	}
	
	/** Redefinicion, no elevar excepcion si el registro en cuestion no existía */
	protected StringBuffer handleRecordNotExistsOnDelete(ChangeGroup changeGroup) throws Exception	{
		return null;
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
	
	
	/**
	 * Quitar ciertas columnas del changeGroup si así se considera necesario segun las siguientes reglas
	 */
	protected void checkForColumnsToSkip(ChangeGroup changeGroup)
	{
		// Nombre de tabla
		String tableName = changeGroup.getTableName();
		// Numero original de columnas
		int initialColumnSize = changeGroup.getColumns().size();
		for (int i = initialColumnSize-1; i>=0; i--) {
			Column column = changeGroup.getColumns().get(i); 
			/* 
			 * Si alguna columna tiene especificado destinos en particular, y esos destinos 
			 * no incluyen a este host, entonces directamente quitar la columna del query 
			 */
			if (column.getTargetOnly() != null && column.getTargetOnly().size() > 0 && !column.getTargetOnly().contains(thisHostPos)) {
				changeGroup.getColumns().remove(i);
			}	
			/*
			 * Omision de referencias ciclicas en inserción.  Solo se omite en la inserción inicial para 
			 * solucionar la doble referencia, a fin de que un posterior update setee el valor correctamente
			 */
			else if (ReplicationConstantsWS.cyclicReferences.get(tableName.toLowerCase()) != null && ReplicationConstantsWS.cyclicReferences.get(tableName.toLowerCase()).contains(column.getName().toLowerCase()) && (MChangeLog.OPERATIONTYPE_Insertion.equals(changeGroup.getOperation()) || changeGroup.getOperation().startsWith(MChangeLog.OPERATIONTYPE_InsertionModification))) {
				changeGroup.getColumns().remove(i);
			}
			/*
			 * Los representantes de ventas son inherentes a las organizaciones, con lo cual probablemente genere
			 * un error intentar recuperar el AD_User, dado que el mismo podría no existir en otros hosts.
			 * Por otra parte las listas de precio en general pertenecen a una unica organizacion, con lo cual replicar
			 * la lista de precio de las entidades comerciales entre sucursales va a presentar un error. 
			 */
			else if (X_C_BPartner.Table_Name.equalsIgnoreCase(tableName) && ("salesrep_id".equalsIgnoreCase(column.getName()) || "m_pricelist_id".equalsIgnoreCase(column.getName()))) {
				changeGroup.getColumns().remove(i);
			}
			/*
			 * No replicar el campo M_Inventory_ID para la tabla M_Transfer
			 */
			else if (X_M_Transfer.Table_Name.equalsIgnoreCase(tableName) && "m_inventory_id".equalsIgnoreCase(column.getName())) {
				changeGroup.getColumns().remove(i);
			}
			/*
			 * No replicar el campo Ref_OrderLine_ID para la tabla C_OrderLine_ID
			 */
			else if (X_C_OrderLine.Table_Name.equalsIgnoreCase(tableName) && "ref_orderline_id".equalsIgnoreCase(column.getName())) {
				changeGroup.getColumns().remove(i);
			}
		}
	}
	
}
