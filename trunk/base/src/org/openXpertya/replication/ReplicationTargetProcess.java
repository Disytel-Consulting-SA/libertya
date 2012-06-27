package org.openXpertya.replication;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Trx;

public class ReplicationTargetProcess extends AbstractReplicationProcess {
		
	@Override
	protected String doIt() throws Exception {

		/* Iterar por todas las sucursales */
		int[] orgs = PO.getAllIDs("AD_Org", " isActive = 'Y' AND AD_Client_ID = " + getAD_Client_ID() + " AND AD_Org_ID != " + thisOrgID, get_TrxName());
		for (int i=0; i<orgs.length; i++)
		{
			// Recuperar la posicion dentro del repArray
			int replicationArrayPos = MReplicationHost.getReplicationPositionForOrg(orgs[i], get_TrxName());
			
			// Si no hay que replicar informacion desde el host i-esimo, omitir esta sucursal
			if (!shouldReplicateFromHost(replicationArrayPos))
				continue;
			
			try
			{
				// Obtener la cola de notificaciones y replicar
				processQueueFromHost(orgs[i], replicationArrayPos);
			}
			catch (Exception e)
			{
				saveLog(Level.SEVERE, true, "Imposible conectar a host origen (" + replicationArrayPos + "): " + e.toString(), null);
			}
		}
		
		saveLog(Level.INFO, false, "Proceso finalizado", null);
		return "OK";
	}

	/**
	 * En funcion de la configuración en ad_tablereplication,
	 * determinar si deberá conectarse al host "position" y
	 * obtener la información a replicar correspondiente.
	 * @param position es la posicion del host dentro del replicationArray
	 * @return true si hay que replicar o false en caso contrario
	 */
	protected boolean shouldReplicateFromHost(int position)
	{
		// Si en los parametros se especificó un host en especial, verificar si es el correcto
		if (ReplicationConstants.REPLICATION_TARGET_REPLICATE_FROM_HOST > 0) 
			return position == ReplicationConstants.REPLICATION_TARGET_REPLICATE_FROM_HOST;
		
		// Si no se especificó un host en especial, revisar cuales son a los que deberá conectarse 
		String sql = " SELECT count(distinct(1)) " +
					 " FROM ad_tablereplication " +
					 " WHERE ad_client_Id = " + getAD_Client_ID() + 
					 " AND (substring(replicationArray, ?, 1) = '" + ReplicationConstants.REPLICATION_CONFIGURATION_RECEIVE + "' OR " +
					     "  substring(replicationArray, ?, 1) = '" + ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE + "' )" ;
		return DB.getSQLValue(get_TrxName(), sql, position, position, true) > 0;
	}
	
	/**
	 * Conectarse al host origen, recibir la cola de notificaciones que
	 * tenga almacenada para el host destino y procesarla acordemente
	 * @param orgID id de la organizacion del host origen
	 * @param hostID posicion en el reparray del host origen
	 */
	protected void processQueueFromHost(int sourceOrgID, int sourceHostID) throws Exception
	{
		/* Conectar a la cola remota de eventos */
		startJMSConsumer(ReplicationConstants.JMS_EVTQUEUE_JNDI_NAME, sourceOrgID, ReplicationConstants.JMS_EVT_ORG_TARGET + " = '"+ thisOrgPos +"'");

		/* Configuración para el envio de mensajes Ack con destino en el host originante */
		startJMSProducer(ReplicationConstants.JMS_ACKQUEUE_JNDI_NAME, sourceOrgID);
		
		try
		{
	        // Iniciar la lectura de mensajes - Procesar la cola
	        boolean ok = true;
	        TextMessage message = null;        
	        getConsumerConnection().start();
        	int messagesPerTrx = getMessagesPerTrx();
        	int totalMsgCount = 0;
        	// Finalizar procesamiento si se llego al limite de registros indicado como parametro (si es 0 no hay limite) o si no hay nada en la cola
        	while (ok && (ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS == 0 || totalMsgCount < ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS))
        	{
    			// Procesar hasta un quantum dentro de esta transacción, por limite de registros (si es 0 no hay limite), o si no hay nada en la cola
	        	int msgCount = 0;
	        	StringBuffer completeXML = new StringBuffer();
	        	while (ok && (ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS == 0 || totalMsgCount < ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS) && msgCount < messagesPerTrx)
	        	{
	            	Message m = null;
	            	int attempts = 0;
	            	try {
	            		m = getConsumer().receive(1);
	            	}
	            	catch (JMSException e) {
	            		Thread.sleep(500);
	            		// si ya se reintentó varias veces, no continuar con el intento de recepcion
	            		if (attempts++ == 5) {
	            			String errMsg = "Imposible recibir mensajes. Se desistió luego de varios intentos. " + e.getMessage();
	            			throw new Exception(errMsg);
	            		}
	            	}
	
		            if (m != null) {
		            	message = (TextMessage) m;
		                completeXML.append(message.getText());
		                msgCount++;
		                totalMsgCount++;
		            }
		            else ok = false;
	        	}
	        	
	            // Si hay algo en la cola...
	            if (completeXML.length() > 0)
	            {
	            	try {
	            		
		            	// Iniciar la transacción
		        		rep_trxName = Trx.createTrxName();
		        		Trx.getTrx(rep_trxName).start();
		        		
		            	// ...Procesar el XML recibido y actualizar el repArray
		            	ReplicationXMLUpdater replicationXMLUpdater = new ReplicationXMLUpdater(" <root> " + completeXML.toString() + " </root> ", rep_trxName, sourceOrgID);
		                replicationXMLUpdater.processChangeLog();
		                if (replicationXMLUpdater.getEventLog().size() > 0)
		                	sendAcksToSourceHost(sourceOrgID, replicationXMLUpdater.getEventLog());
		                
		                // Commitear la transacción y confirmar el procesamiento de los mensajes
		            	Trx.getTrx(rep_trxName).commit();
		                message.acknowledge();
		                Trx.getTrx(rep_trxName).close();
	            	}
	            	catch (Exception e) {
	            		// En caso de un error al procesar un mensaje, catchear la exception y continuar con el procesamiento del siguiente
	            		// IMPORTANTE: Este mecanismo evita demorar al resto de mensajes encoladsos, pero por otro lado, al realizar algún
	            		//			   acknowledge en los siguientes mensajes, el procesamiento de este mensaje se perderá para siempre
	            		//			   Si se quiere detener el procesamiento de mensajes hasta que el actual sea corregido, deberá propagarse la excepción
	            		String error = "WARNING.  Error inesperado al procesar un mensaje. Error: " + e.getMessage();
	            		saveLog(Level.SEVERE, true, error, thisOrgID);
	            	}
	            }
	        }
		}
		catch (Exception e)
		{
			// Propagar la excepion
			throw new Exception("Error al procesar la cola de eventos: " + e.getMessage());	
		}
		finally
		{
			closeConsumerConnection();
			closeProducerConnection();
		}
	}
	
	/**
	 * Conecta al host originario de los eventos y le envia
	 * los ACKS/ERRORS correspondientes a fin de que éste
	 * pueda gestionar de manera acorde dicha información
	 */
	protected void sendAcksToSourceHost(int sourceOrgID, Vector<String[]> eventLog) throws Exception
	{
        /* Generar  el mensaje con la informacion */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(ReplicationConstants.JMS_ACK_ORG_SOURCE, thisOrgPos);
        map.put(ReplicationConstants.JMS_ACK_ORG_VALUES, eventLog);

        /* Enviar el mensaje Ack */
        ObjectMessage message = getProducerSession().createObjectMessage();
        message.setStringProperty(ReplicationConstants.JMS_ACK_ORG_TARGET, ""+thisOrgPos);
        message.setObject(map);
        getProducer().send(message);
	}
	

	/**
	 * Retorna la cantidad de mensajes que debe procesar en una misma transacción
	 */
	protected int getMessagesPerTrx()
	{
		// TODO: esto debe ser una preferencia y NO una constante
		return ReplicationConstants.REPLICATION_TARGET_MESSAGES_PER_TRX;
	}
	
	/**
	 * @return nombre de identificacion del proceso
	 */
	protected String getProcessName()
	{
		return TARGET_PROCESS;
	}
}
