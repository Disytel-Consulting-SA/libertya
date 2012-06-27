package org.openXpertya.replication;

/**
 * Proceso origen de replicación.
 * 
 * COLA DE NOTIFICACIONES
 * """"""""""""""""""""""
 * Lee de la cola de notificaciones a fin de saber si los registros fueron
 * correctamente replicados por parte de las distintas sucursales.  Cada
 * mensaje contendrá la información necesaria: de qué sucursal se está
 * recibiendo la información, que registros fueron correctamente replicados
 * y cuales han tenido problemas en su replicación 
 * 
 * 
 * COLA DE EVENTOS
 * """""""""""""""
 * A la cola de eventos simplemente se envia un 1 XML por modificacion en bbdd,
 * discriminando además por cada una de las sucursales, con lo cual enviará 
 * a la cola un mismo mensaje varias veces a fin de que luego cada cliente 
 * se encargue de recuperar el que le corresponde (mediante los filtros JMS)
 *
 */

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.X_AD_ReplicationError;
import org.openXpertya.util.DB;
import org.openXpertya.util.Trx;

public class ReplicationSourceProcess extends AbstractReplicationProcess {
	
	@Override
	protected String doIt() throws Exception 
	{
		try
		{
			/* Recuperar los Acks pendientes, cambiar replicationArray y commitear
			 * 
			 * Iterar por todos los registros de todas las tablas que tengan pendiente de replicacion
			 *	Mandar los registros a la EventQueue
			 */
			if (thisOrgID == -1)
				throw new Exception (" Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación ");
		
			/* Recibir de la cola de notificaciones */
			processAckQueue();
			
			/* Enviar a cola de eventos */
			processEventQueue();
		}
		catch (Exception e)
		{
			saveLog(Level.SEVERE, true, "Error en replicación: " + e.getMessage(), null);
			throw new Exception(e);
		}
		
		saveLog(Level.INFO, false, "Proceso finalizado", null);
		return "OK";
    }
		

	/**
	 * Procesa la cola de notificaciones, recibiendo los 
	 * OK o Error, a fin de actualizar el replicationArray
	 */
	@SuppressWarnings("unchecked")
	protected void processAckQueue() throws Exception
	{
		/* Configuración para la recepcion de mensajes con destino en este host */
		startJMSConsumer(ReplicationConstants.JMS_ACKQUEUE_JNDI_NAME, thisOrgID, null);
        ObjectMessage message;
        
        // Recorrer todos los mensajes y actualizar los repArray de manera acorde
        try
        {
	        boolean ok = true;
	        getConsumerConnection().start();
	        while (ok) {
	        	
	        	// intentar más de una vez en caso de problemas personales 
	        	Message m = null;
	        	int attempts = 0;
	        	try {
	        		m = getConsumer().receive(1);
	        	}
	        	catch (JMSException e) {
	        		Thread.sleep(500);
	        		// si ya se reintentó varias veces, no continuar
	        		if (attempts++ == 5) {
	        			String errMsg = "Imposible recibir mensajes. Se desistió luego de varios intentos. " + e.getMessage();
	        			throw new Exception(errMsg);
	        		}
	        	}
	
	            if (m != null) {
	            	HashMap<String, Object> map = null;
	            	try {
		            	message = (ObjectMessage) m;
		            	map = (HashMap<String, Object>)(message.getObject());
		            			
		            	// Iniciar la transacción
		        		rep_trxName = Trx.createTrxName();
		        		Trx.getTrx(rep_trxName).start();
		        		
		        		// Actualizar los repArrays dentro de la transaccion actual
		            	updateRepArray( (Integer)map.get(ReplicationConstants.JMS_ACK_ORG_SOURCE), 
		            					(Vector<String[]>) map.get(ReplicationConstants.JMS_ACK_ORG_VALUES),
		            					rep_trxName);
		            	
		                // Commitear la transacción y confirmar el procesamiento de los mensajes            	
		            	Trx.getTrx(rep_trxName).commit();
		            	message.acknowledge();
		            	Trx.getTrx(rep_trxName).close();
	            	}
	            	catch (Exception e) {
	            		// En caso de un error al procesar un mensaje, catchear la exception y continuar con el procesamiento del siguiente mensaje
	            		// IMPORTANTE: Este mecanismo evita demorar al resto de mensajes encoladsos, pero por otro lado, al realizar algún
	            		//			   acknowledge en los siguientes mensajes, el procesamiento de este mensaje se perderá para siempre.
	            		//			   Si se quiere detener el procesamiento de mensajes hasta que el actual sea corregido, deberá propagarse la excepción
	            		String error = "WARNING.  Error inesperado al procesar el mensaje: " + m.toString() + ". Error: " + e.getMessage();
	            		saveLog(Level.SEVERE, true, error, map==null?null:(Integer)map.get(ReplicationConstants.JMS_ACK_ORG_SOURCE));
	            	}
	            }
	            else ok = false;
	        }
        }
        catch (Exception e)
        {
        	// Propagar la excepcion
        	throw new Exception("Error al procesar la cola de acks: " + e.getMessage());
        }
        finally
        {
        	// Cerrar la conexion en caso de error
        	closeConsumerConnection();
        }
	}	
	
	
	/**
	 * Carga en la cola de eventos los registros insertados o modificados
	 * dentro de las tablas marcadas para su replicación
	 */
	protected void processEventQueue() throws Exception
	{
		try
		{
			/* Configuración para el envio de mensajes con origen en este host */
			startJMSProducer(ReplicationConstants.JMS_EVTQUEUE_JNDI_NAME, thisOrgID);
			
			// Instanciar el builder encargado de generar los XMLs a enviar a las colas
			ReplicationBuilder builder = new ReplicationBuilder(get_TrxName(), this);
			
			// El fillDocument interactua con el appendToEventQueue a fin de 
			// ir cargando la cola con los nuevos eventos de replicación
			builder.fillDocument();
		}
		catch (Exception e)
		{
        	// Propagar la excepcion			
			throw new Exception("Error al cargar la cola de eventos: " + e.getMessage());
		}
		finally
		{
			// Cerrar la conexión con el server
			closeProducerConnection();
		}
	}
	
	
	/**
	 * Gestiona un mensaje JMS con la nomina de Ack y Errores,
	 * a fin de actualizar el replicationArray de cada registro
	 * values es un vector con la nómina de notificaciones, con formato:
	 * tablename;uid;opType[OK|ERROR:...]
	 * 	opType en caso de error se recibe XA, XB, XC, etc...
	 * @throws Exception
	 */
	protected void updateRepArray(int repArrayPos, Vector<String[]> notifications, String trxName) throws Exception
	{
		String tempCurrStat = null;
		Character currentStatus;
		for (String[] parts : notifications)
		{
			// Cual es el estado actual del registro para la posicion dada?
			tempCurrStat = DB.getSQLValueString(trxName, " SELECT substring(repArray, " + (repArrayPos) + ", 1) " +
													   		" FROM " + (MChangeLog.OPERATIONTYPE_Deletion.equals(parts[2]) ? ReplicationConstants.DELETIONS_TABLE : parts[0]) + 
													   		" WHERE retrieveUID = ? ", parts[1], true);
 
			// de no existir el registro en el origen, omitir
			if (tempCurrStat == null || tempCurrStat.length() == 0)
				continue;
			
			currentStatus = tempCurrStat.charAt(0);
			
			// si se recibe OK...
			if (ReplicationConstants.JMS_ACK_OK.equals(parts[3]))
			{
				// Omitir trancisiones no esperadas (por ejemplo por reprocesamiento del mensaje)
				if (ReplicationConstants.nextStatusWhenOK.get(currentStatus) == null)
					continue;
				
				repArrayUpdateQuery(repArrayPos, parts[0], parts[1], currentStatus, ReplicationConstants.nextStatusWhenOK.get(currentStatus), trxName, parts[2]);
			}
			// si se recibe ERROR...
			else
			{
				// omitir mensajes mal formados 
				if ((parts[2].startsWith(""+ReplicationConstants.REPARRAY_RETRY_PREFIX) && parts[2].length() <= 1) || parts[2].length() == 0)
					continue;
				
				// recuperar la accion (I o M) o el reintento (A, B, C, etc.) en caso que exista un segundo caracter
				Character currentRetryStatus = null;
				String action = null; 
				if (parts[2].length() > 1) {
					currentRetryStatus = parts[2].charAt(1);
					action = " - Reintento: ";
				}
				else {
					currentRetryStatus = parts[2].charAt(0);
					action = " - Accion: ";
				}
				
				
				// Detalles del error
				String error = "Recepción de error desde host:" + repArrayPos + " - " + " Tabla: " + parts[0] + " - retrieveUID: " + parts[1] + action + currentRetryStatus + ". ";
								
				// Quedan reintentos disponibles?  Si el siguiente estado es X, significa que ya no quedan
				Character nextStatus = ReplicationConstants.nextStatusWhenERR.get( parts[2].length() > 1 ? currentRetryStatus : currentStatus);
				
				// Omitir trancisiones no esperadas (por ejemplo por reprocesamiento del mensaje)
				if (nextStatus == null)
					continue;
				
				if (nextStatus == ReplicationConstants.REPARRAY_NO_RETRY || 
					nextStatus == ReplicationConstants.REPARRAY_REPLICATE_NO_RETRY)
				{
					// Si no quedan reintentos disponibles, setear el ERROR a nivel SEVERE.
					error = "FATAL. REITERADOS REINTENTOS DE REPLICACION FALLIDOS. " + error;
					saveLog(Level.SEVERE, true, error + parts[3], repArrayPos);
				}
				else
				{
					error = "ERROR AL REPLICAR. " + error;
					saveLog(Level.WARNING, true, error + parts[3], repArrayPos);
				}

				// Actualizar el repArray en cualquier caso
				repArrayUpdateQuery(repArrayPos, parts[0], parts[1], currentStatus, nextStatus, trxName, parts[2]);
			}
		}
	}
	
	
	/**
	 * Ejecuta el query de actualización
	 * @param pos es el host que envió el acknowledge
	 * @param tableName tabla a actualizar
	 * @param uid registro a actualizar dentro de la tabla
	 * @param fromState estado previo a la notificacion
	 * @param toState nuevo estado
	 */
	protected void repArrayUpdateQuery(int pos, String tableName, String uid, Character fromState, Character toState, String trxName, String opType)
	{
		boolean isDeletion = MChangeLog.OPERATIONTYPE_Deletion.equals(opType);
		// El uso de prefijo SET para el repArray solo es para tablas con triggerEvent.  La tabla AD_Changelog_Replication obviamente no lo tiene seteado
		String set = isDeletion ? "" : "SET";
		String tableNameQuery = MChangeLog.OPERATIONTYPE_Deletion.equals(opType)?ReplicationConstants.DELETIONS_TABLE:tableName;
		DB.executeUpdate(" UPDATE " + (tableNameQuery) +
				 			" SET repArray = '"+set+"' || OVERLAY(repArray placing '" + toState + "' FROM "+(pos) + " for 1) " +
				 			" WHERE retrieveUID = '" + uid + "'" +
				 			" AND AD_Client_ID = " + getAD_Client_ID(), false, trxName, true);

		// Si es una eliminacion quedan tareas adicionales pendientes
		if (MChangeLog.OPERATIONTYPE_Deletion.equals(opType))
		{
			// Verificar si el registro tiene hosts pendientes de replicacion 
			String repArray = DB.getSQLValueString(trxName, " SELECT repArray FROM " + ReplicationConstants.DELETIONS_TABLE + " WHERE retrieveUID = '" + uid + "' AND AD_Client_ID = ? ",  getAD_Client_ID(), true);
			boolean ok = true;
			for (int i=0; i < repArray.length() && ok; i++)
				if (ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION != repArray.charAt(i) && ReplicationConstants.REPARRAY_REPLICATED != repArray.charAt(i))
					ok = false;
				
			// Si no quedan pendientes, entonces eliminar la entrada
			if (ok)
				DB.executeUpdate(" DELETE FROM " + ReplicationConstants.DELETIONS_TABLE + " WHERE retrieveUID = '" + uid + "' AND AD_Client_ID = " + getAD_Client_ID(), false, trxName, true);
				
		}
	}
	
	/**
	 * Incorpora un nuevo envio a la cola de eventos.  Segun el repArrayPos destino,
	 * el opType puede llegar a variar (una modificacion o una inserciòn), con lo cual
	 * es necesario reemplazar independientemente para cada mensaje el opType según corresponda.
	 * @param repArrayPos destino 
	 * @param xmlContent contenido del XML con el conjunto de columnas a insertar/modificar, tabla,etc.
	 * @param opType insercion o modificacion
	 */
	public void appendToEventQueue(int repArrayPos, String xmlContent, String opType) throws Exception
	{
		TextMessage message = getProducerSession().createTextMessage();
		message.setText(xmlContent.replaceFirst("operation=\"\"", "operation=\""+opType+"\""));
		message.setStringProperty(ReplicationConstants.JMS_EVT_ORG_TARGET, ""+repArrayPos);
		getProducer().send(message);
	}
	
	/**
	 * @return nombre de identificacion del proceso
	 */
	protected String getProcessName()
	{
		return SOURCE_PROCESS;
	}
}
