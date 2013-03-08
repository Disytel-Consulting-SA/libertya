package org.openXpertya.replication;

/**
 * Redefinición de ChangelogXMLBuilder para la generación 
 * de los archivos XML definitivos para su replicación.
 * 
 * Interactua con el manejador de colas a fin de reducir el consumo de memoria
 * 
 * @author fcristina
 */

import java.util.logging.Level;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.plugin.install.ChangeLogXMLBuilder;
import org.openXpertya.process.CreateReplicationTriggerProcess;
import org.openXpertya.util.DB;


public class ReplicationBuilder extends ChangeLogXMLBuilder {

	/** Prefijo de referencia por retrieveUID */
	public static final String RUID_REFERENCE_PREFIX = "UID=";
	/** Prefijo de referencia por componentObjectUID */
	public static final String CUID_REFERENCE_PREFIX = "CID=";	
	
	/** XML a enviar al destinatario */
	protected StringBuffer m_replicationXMLData = null; 
	
	/** Encargado de recuperar las entradas a replicar */
	protected ChangeLogGroupListReplication groupList = null;
	
	/** Manejador de colas JMS */
	protected ReplicationSourceProcess replicationHndler = null;	
	

	
	/**
	 * Constructor especifico para el Builder de Replicacion
	 */
	public ReplicationBuilder(String trxName, ReplicationSourceProcess handler) {
		super(trxName);
		replicationHndler = handler;
	}

	/** Overrides varios */
	protected Integer getTableSchemaID() {		return null;	}
	protected void initIgnoreColumns(){}
	protected void initComponentVersion(Integer componentVersionID){}
	

	/** En este caso no se guarda el documento en archivo,
	 *  sino que se almacena el String resultante a fin de 
	 *  enviarlo al host correspondiente para su replicación
	 */
	protected void saveDocument() throws Exception{
		// YA SE CUENTA CON EL STRINGBUFFER RESULTANTE
//        StringWriter stw = new StringWriter();
//        Transformer serializer = TransformerFactory.newInstance().newTransformer();
//        serializer.transform(new DOMSource(getDoc()), new StreamResult(stw));
//        m_replicationXMLData = stw.toString(); 
	}
	
	
	/**
	 * Redefinicion específica para replicación
	 *
	 * Por cada group, el mismo se envia a replicacion,
	 * contemplando los hosts destinos a abarcar
	 * 
	 */
	@Override
	protected void fillDocument() throws Exception {
						
		/* Variables para la generacion del groupList */
		groupList = new ChangeLogGroupListReplication();
		String newValue;
		boolean isTableReference;
		boolean useRetrieveUID = false;
		String retrieveUIDValue = "";
		String tableName = "";
		boolean tableHasRetrieveUID;
		int tableID = -1;
		
		/* Cargar el listado de grupos (cada tupla de AD_Changelog_Replication es un groupList) */
		groupList.fillList(trxName);

		/* Por cada grupo... */
		int i = 0;
		for (ChangeLogGroupReplication group : groupList.getGroupsReplication()) {
			
			// Creo el nodo elemento del grupo
			m_replicationXMLData = new StringBuffer("");
			m_replicationXMLData.append("<changegroup");
			
			// En caso de error al crear el xml para un changegroup, se omite el mismo, pero se continua con los siguientes
			try
			{
				// Creo, seteo el valor de los nodos atributos y asocio con el nodo padre
				m_replicationXMLData.append(" tableName=\"").append(group.getTableName()).append("\"");
				m_replicationXMLData.append(" uid=\"").append(group.getAd_componentObjectUID()).append("\"");  // <- el uid es usado para el recordUID (retrieveUID)
				m_replicationXMLData.append(" operation=\"").append(group.getOperation()).append("\"");
				m_replicationXMLData.append(">");
				
				// Itero por los elementos del grupo
				for (ChangeLogElement element : group.getElements()) {
					// Creo el nodo elemento para la columna
					m_replicationXMLData.append("<column");
					m_replicationXMLData.append(" name=\"").append(element.getColumnName()).append("\"");
					m_replicationXMLData.append(" type=\"").append(element.getAD_Reference_ID()).append("\"");
					
					// Si es de tipo referencia a una tabla o de tipo Entero con _ID, 
					// coloco los atributos específicos
					isTableReference = isTableReference(element);
					useRetrieveUID = false;
					retrieveUIDValue = "";
					if(isTableReference && element.getNewValue() != null && element.getNewValue().toString().length() > 0 
							&& !getIgnoresReferenceColumns().contains(element.getColumnName())){
						// Determinar el nombre de la tabla de referencia
						tableName = element.getColumnName();
						if(element.getColumnName().toUpperCase().endsWith("_ID")){
							tableName = element.getColumnName().substring(0,element.getColumnName().lastIndexOf("_"));
						}
						// Si tiene una referencia seteada en la columna, entonces busco ahí la tabla
						if(element.getAD_Reference_Value_ID() != 0){
							tableID = ReplicationCache.referencesData.get(element.getAD_Reference_Value_ID()); 
							tableName = ReplicationCache.tablesData.get(tableID);  
						}
						
						/**
						 *  Si la referencia posee un retrieveUID, entonces se deberá utilizar éste en lugar del ID local
						 */
						// La tabla referenciada tiene campo retrieveUID?
						tableHasRetrieveUID = ReplicationCache.tablesWithRetrieveUID.contains(tableName.toLowerCase()); 
						if (tableHasRetrieveUID)
						{
							// El registro tiene seteado el retrieveUID? Si no lo tiene, es una refTable tradicional
							retrieveUIDValue = DB.getSQLValueString(trxName, " SELECT retrieveUID FROM " + tableName + " WHERE " + tableName + "_ID = " + element.getNewValue() + " AND 1 = ?", 1 );
							if (retrieveUIDValue != null && retrieveUIDValue.length() > 0)
								useRetrieveUID = true;
						}
						// incorporar la referencia a la tabla correspondiente
						m_replicationXMLData.append(" refTable=\"").append(tableName).append("\"");
					}
					
					// cierre de tag inicial de la columna
					m_replicationXMLData.append(">");
					
					// Textos del nodo, old y new values
				    newValue = useRetrieveUID ? (RUID_REFERENCE_PREFIX+retrieveUIDValue) : String.valueOf(element.getNewValue());
	//				if(element.getBinaryValue() != null){
	//					/** TODO: VER QUE HACER ACA CON LOS BINARIOS EN REPLICACIÓN! */
	//				}
					// En el AD_Org_ID en realidad no se envia el AD_Org_ID sino el host asociado (replicationArrayPos) cargado, 
					// dado que este es el único valor en común.  Para AD_Org_ID = 0, pasamos directamente ese valor sin mapear 
					if ("AD_Org_ID".equalsIgnoreCase(element.getColumnName()) && !"UID=AD_Org-0".equals(newValue))
					{
						// Si useRetrieveUID es false, entonces UID no estará seteado.  Sin embargo, AD_Org_ID podrìa ser 0 en este caso
						// el cual tambien es necesario omitir, dejando también 0 como valor.
						if (!"0".equals(newValue)) 
						{
							Integer orgPos = ReplicationCache.map_RepArrayPos_OrgID.get(newValue.replace("UID=AD_Org-", ""));
							if (orgPos == null)
								throw new Exception ("No hay mapeo posible para la organización " + newValue + " en la tabla de hosts de replicación ");
							newValue = orgPos.toString();
						}
					}
					// Agrego los nodos texto al nodo newValue
					m_replicationXMLData.append("<newValue>").append(newValue).append("</newValue>");
					// Cierre de columna
					m_replicationXMLData.append("</column>");
				}
	
				// Cierre del changegroup
				m_replicationXMLData.append("</changegroup>");
				
				// Enviar a replicacion, una vez por cada destino a replicar
				for (int arrayPos = 0; arrayPos < group.getRepArray().length(); arrayPos++)
					// En las posiciones que corresponde, se envia a replicacion (cola de eventos).  
					// Si hubo timeOut, tambièn se reenvia donde corresponda
					// En caso de estar reenviando todos los registros, se envia a todo el repArray (menos donde se indique sin accion)
					if (ReplicationConstants.replicateStates.contains(group.getRepArray().charAt(arrayPos)) || 
						  (group.isTimeOut() && ReplicationConstants.timeOutStates.contains(group.getRepArray().charAt(arrayPos))) ||
						  (ReplicationConstants.RESEND_ALL_RECORDS && ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION!=group.getRepArray().charAt(arrayPos)) )
							sendToEventQueue(group, arrayPos);
			}
			catch (Exception e) 	{
				replicationHndler.saveLog(Level.SEVERE, true, "Error en ReplicationBuilder: " + e.getMessage(), null);
			}
		
			// Limpiar memoria cada cierto intervalo de iteraciones
			if (i++ % 1000 == 0)
				System.gc();
		}
		
		// Dado que los registros se impactan por lotes de a ReplicationConstants.REPLICATION_SOURCE_QUERIES_PER_GROUP,
		// si queda un remanente, también hay que ejecutarlo para completar el ultimo lote de registros de manera completa
		setRepArraysToWaitingAckLastRecords();

	}

	/**
	 * Retorna el tipo de operación a replicar (inserción o actualización
	 */
	protected void sendToEventQueue(ChangeLogGroupReplication group, int arrayPos) throws Exception
	{
		// estado del registro en la posicion arrayPos
		char state = group.getRepArray().charAt(arrayPos);

		/* === CASOS DE ELIMINACION === */
		if (MChangeLog.OPERATIONTYPE_Deletion.equals(group.getOperation()))
		{
			// Eliminaciones (en definitiva una orden de INSERTAR una replicación de eliminación se transforma en una operación de ELIMINACION)
			// Contemplando tambien casos por time out
			if (ReplicationConstants.REPARRAY_REPLICATE_INSERT == state || ReplicationConstants.timeOutStates.contains(state) )
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_Deletion);
			// Reintento de eliminacion. Tambien valido para reenvio completo.
			else if (ReplicationConstants.errorStates.contains(state) || ReplicationConstants.RESEND_ALL_RECORDS)
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_Deletion + state);
			// En caso contrario, elevar exception
			else
				throw new Exception("ReplicationBuilder. Sin opType para: " + group);
		}
		else
		{
			/* === CASOS DE INSERCION O ACTUALIZACION === */
			// Si en la posición tiene marca de Error... INSERT+UPDATE
			// (dado que el estado de error no almacena si fue en intento de inserción o actualización, directamente
			// se utiliza el caso especial de intento de inserción o actualización según la existencia del registro )
			// se incluye ademas la letra reintento (A,B,C...) a fin de realizar el seguimiento correspondiente.
			// Los reintentos de replicación por timeout del acknowledge tambien se interpretan aquí como estdos de error
			if (ReplicationConstants.errorStates.contains(state))
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_InsertionModification + state);
			// Si en la posición tiene marca de inserción... INSERT
			else if (ReplicationConstants.REPARRAY_REPLICATE_INSERT == state)
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_Insertion);
			// Si en la posición tiene marca de modificación... UPDATE
			else if (ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION == state)
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_Modification);
			// Si está esperando ack, y se llega a este punto, es por timeOut de la espera. Manejarlo como caso de error. Tambien valido para reenvio completo
			else if (ReplicationConstants.timeOutStates.contains(state) || ReplicationConstants.RESEND_ALL_RECORDS)
				replicationHndler.appendToEventQueue(arrayPos+1, m_replicationXMLData.toString(), MChangeLog.OPERATIONTYPE_InsertionModification + ReplicationConstants.REPARRAY_RETRY1);
			// En caso contrario, informar error para este changeGroup
			else
				throw new Exception("ReplicationBuilder. Sin opType para: " + group);
		}
		// Actualizar el repArray para este registro, en la posición en cuestión
		setRepArraysToWaitingAck(group, arrayPos+1);
	}
	
	/**
	 * Actualiza los repArrays de los registros involucrados en esta replicación,
	 * setean la espera de confirmacion en cada caso
	 */
	int count = 0;
	StringBuffer toWaitingAckQuery = null;
	public void setRepArraysToWaitingAck(ChangeLogGroupReplication group, int arrayPos) throws Exception
	{
		// Cambiar de Replicar (1ra vez, nuevamente o por error) -> Espera de Ack
		StringBuilder sb = new StringBuilder(group.getRepArray());
		sb.setCharAt(arrayPos-1, ReplicationConstants.REPARRAY_ACK_WAITING);
		group.setRepArray(sb.toString());

		// Ampliar el query 
		if (toWaitingAckQuery==null)
			toWaitingAckQuery = new StringBuffer();
		boolean isDeletion = MChangeLog.OPERATIONTYPE_Deletion.equals(group.getOperation());
		// El uso de prefijo SET para el repArray solo es para tablas con triggerEvent.  La tabla AD_Changelog_Replication obviamente no lo tiene seteado
		String set = isDeletion ? "" : "SET";
		// Tabla a actualizar
		String table = isDeletion ? ReplicationConstants.DELETIONS_TABLE : group.getTableName();
		// Armar el query de actualizacion del repArray
		toWaitingAckQuery.append(" UPDATE ").append(table)
						.append(" SET ")
						.append(  ReplicationConstants.COLUMN_REPARRAY     ).append(" = '"+set).append(group.getRepArray()).append("', ")
						.append(  ReplicationConstants.COLUMN_DATELASTSENT ).append(" = NOW() ")
						.append(  updateIncludeInReplication(group) )
						.append(" WHERE retrieveUID = '").append(group.getAd_componentObjectUID()).append("'; ");
		
		count++;
		// Ejecutar la actualización, si corresponde
		if (count == ReplicationConstants.REPLICATION_SOURCE_QUERIES_PER_GROUP) {
			DB.executeUpdate(toWaitingAckQuery.toString(), trxName);
			toWaitingAckQuery = null;
			count = 0;
		}
	}
	
	/**
	 * Ejecuta cualquier pendiente en el lote de sentencias sql a ejecutar sobre los updates a WaitingAck
	 */
	public void setRepArraysToWaitingAckLastRecords() throws Exception
	{
		if (toWaitingAckQuery!=null && toWaitingAckQuery.length()>0)
			DB.executeUpdate(toWaitingAckQuery.toString(), trxName);
		toWaitingAckQuery = null;
		count = 0;
	}
	
	/**
	 * Actualiza el includeInReplication a 'N' en el caso en que ninguna
	 * de las posiciones actuales este marcada para replicación 
	 * @param group grupo recibido
	 * @return un string con el query aumentado en caso de cambiar a N, o un string vacio en cc
	 */
	protected String updateIncludeInReplication(ChangeLogGroupReplication group) {
		for (Character pos : group.getRepArray().toCharArray()) {
			if (ReplicationConstants.replicateStates.contains(pos))
				return "";
		}
		return ", includeInReplication = 'N'";
	}
	
	
	public String getM_replicationXMLData() {
		return m_replicationXMLData.toString();
	}
	
	public int getM_replicationXMLDataLength() {
		return m_replicationXMLData.length();
	}
	
	public void emptyM_replicationXMLData() {
		m_replicationXMLData.delete(0, m_replicationXMLData.length());
		m_replicationXMLData = null;
		groupList.getGroups().clear();
		
	}
	
	public boolean hasReplicationData() {
		return m_replicationXMLData != null && m_replicationXMLData.length()>0;
	}

	public ChangeLogGroupListReplication getGroupList() {
		return groupList;
	}
	
	public byte[] getCompressedXML() throws Exception
	{
		return ReplicationUtils.compressString(m_replicationXMLData.toString());
	}



}
