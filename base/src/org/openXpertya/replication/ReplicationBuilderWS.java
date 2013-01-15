package org.openXpertya.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.util.DB;


public class ReplicationBuilderWS extends ReplicationBuilder {

	/** Cliente de replicacion WS */
	protected AbstractReplicationProcess replicationHndler = null;
	
	/** Contenido a enviar para cada host destino.  RepArrayPos -> XMLContent[]
	  * 	(agrupando conjuntos de acciones a fin de no hacer un XML demasiado extenso para una misma invocacion al WS) */
	public HashMap<Integer, ArrayList<StringBuffer>> completeReplicationXMLDataForHost = new HashMap<Integer, ArrayList<StringBuffer>>(); 
	/** Contador temporal de acciones para un host destino (para saber cuando crear un nuevo conjunto de acciones) */
	protected HashMap<Integer, Integer> completeReplicationXMLDataForHostCount = new HashMap<Integer, Integer>();
	
	public ReplicationBuilderWS(String trxName, AbstractReplicationProcess handler) {
		super(trxName, null);
		replicationHndler = handler;
	}


	/**
	 * Esta es CASI la misma implementacion que la superclase, salvo que en lugar de 
	 * enviar a los XMLs a las colas, se almacena en una estructura temporal, la cual
	 * posteriormente será utilizada para enviar su contenido a cada host destino.
	 * 
	 * TODO: REFACTOR PENDING.  
	 */
	@Override
	public void fillDocument() throws Exception {
						
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
				    newValue = useRetrieveUID ? (UID_REFERENCE_PREFIX+retrieveUIDValue) : String.valueOf(element.getNewValue());
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
					if (ReplicationConstantsWS.replicateStates.contains(group.getRepArray().charAt(arrayPos)) && (ReplicationTableManager.filterHost==null || ReplicationTableManager.filterHost == arrayPos+1))
						addActionsForHost(arrayPos+1, group);
			}
			catch (Exception e) 	{
				replicationHndler.saveLog(Level.SEVERE, true, "Error en ReplicationBuilderWS: " + e.getMessage(), null); 
			}
		
			// Limpiar memoria cada cierto intervalo de iteraciones
			if (i++ % 1000 == 0)
				System.gc();
		}

	}

	/**
	 * Almacena el XML completo correspondiente a enviar hacia cada sucursal, basado en <code>m_replicationXMLData</code>
	 * @param arrayPos posicion destino
	 * @param group informacion de la acción
	 */
	protected void addActionsForHost(int arrayPos, ChangeLogGroupReplication group) throws Exception {
		// Si para la posicion dada todavia no se encuentra inicializado el buffer, inicializarlo
		if (completeReplicationXMLDataForHost.get(arrayPos) == null) {
			completeReplicationXMLDataForHost.put(arrayPos, new ArrayList<StringBuffer>());
			completeReplicationXMLDataForHost.get(arrayPos).add(new StringBuffer());
			completeReplicationXMLDataForHostCount.put(arrayPos, 0);
		}
		
		String actionXML = null;
		// estado del registro en la posicion arrayPos
		char state = group.getRepArray().charAt(arrayPos-1);
		
		/* === CASO DE ELIMINACION === */
		if (MChangeLog.OPERATIONTYPE_Deletion.equals(group.getOperation())) {
			actionXML = m_replicationXMLData.toString().replaceFirst("operation=\"\"", "operation=\""+MChangeLog.OPERATIONTYPE_Deletion+"\"");
		}
		/* === CASO DE INSERCION O ACTUALIZACION === */
		else {
			if (ReplicationConstants.REPARRAY_REPLICATE_INSERT == state)
				actionXML = m_replicationXMLData.toString().replaceFirst("operation=\"\"", "operation=\""+MChangeLog.OPERATIONTYPE_Insertion+"\"");
			else if (ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION == state)
				actionXML = m_replicationXMLData.toString().replaceFirst("operation=\"\"", "operation=\""+MChangeLog.OPERATIONTYPE_Modification+"\"");
			else if (ReplicationConstants.errorStates.contains(state))
				actionXML = m_replicationXMLData.toString().replaceFirst("operation=\"\"", "operation=\""+MChangeLog.OPERATIONTYPE_InsertionModification+ReplicationConstants.REPARRAY_RETRY1+"\"");
			else
				throw new Exception("ReplicationBuilder. Sin opType para: " + group);
		}

		// Se llego al limite? Nuevo conjunto de acciones
		if (completeReplicationXMLDataForHostCount.get(arrayPos) == ReplicationConstantsWS.EVENTS_PER_CALL) {
			completeReplicationXMLDataForHost.get(arrayPos).add(new StringBuffer());
			completeReplicationXMLDataForHostCount.put(arrayPos, 1);
		}
		else
			completeReplicationXMLDataForHostCount.put(arrayPos, completeReplicationXMLDataForHostCount.get(arrayPos)+1);
		// Incorporar al conjunto de acciones 
		(completeReplicationXMLDataForHost.get(arrayPos).get(completeReplicationXMLDataForHost.get(arrayPos).size()-1)).append(actionXML);
	}

}
