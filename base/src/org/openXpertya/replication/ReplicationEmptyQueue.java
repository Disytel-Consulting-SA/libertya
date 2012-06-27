package org.openXpertya.replication;

/**
 * Vacia las colas de eventos o acknowledge locales segun se indique en los parametros
 * Para la cola de eventos, vacia los mensajes con eventos de replicacion hacia un host destino o todos los mensajes encolados
 * Para la cola de acks, vacia los mensajes con confirmaciones desde un host destino hacia el host donde se vacian los acks, o los acks recibidos de todos los hosts 
 */

import javax.jms.JMSException;
import javax.jms.Message;

import org.openXpertya.model.MReplicationHost;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.util.DB;

public class ReplicationEmptyQueue extends AbstractReplicationProcess {

	protected String emptyQueueName = "";	// ReplicationConstants.JMS_EVTQUEUE_JNDI_NAME o bien ReplicationConstants.JMS_ACKQUEUE_JNDI_NAME
	protected int emptyHost = 0;			// Limpiar eventos para un destino dado (0 = todos los mensajes, sin importar el host indicado)
	
	protected void processParameters()
	{
		ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
	        String name = para[ i ].getParameterName();
	        if( para[ i ].getParameter() == null ) ;
	        
	        // Vaciar de la cola EVENT o ACK
	        if( name.equalsIgnoreCase( "Queue"))
	        	emptyQueueName = ((String)para[i].getParameter()).toLowerCase().indexOf("event")>=0 ? 
	        						ReplicationConstants.JMS_EVTQUEUE_JNDI_NAME : ReplicationConstants.JMS_ACKQUEUE_JNDI_NAME;
	        // Eliminar de la eventQueue solo para cierto host destino // Eliminar de la ackQueue solo mensajes originados desde cierto host destino
	        else if( name.equalsIgnoreCase( "AD_Org_ID" ))
	        	emptyHost = MReplicationHost.getReplicationPositionForOrg(para[i].getParameterAsInt(), get_TrxName());	        

        }
	}
	
	@Override
	protected String getProcessName() {
		return "[EMPTY] ";
	}

	@Override
	protected String doIt() throws Exception {
		
		/* Definir criterio de filtrado para vaciar por cola de eventos o confirmaciones? */
		String criteria = ReplicationConstants.JMS_EVTQUEUE_JNDI_NAME.equals(emptyQueueName) ?
							ReplicationConstants.JMS_EVT_ORG_TARGET : ReplicationConstants.JMS_ACK_ORG_TARGET;
		/* Si hay definido un host especial, filtrar los mensajes de la cola segun corresponda, sino eliminar todo (enviando null) */
		String filter = emptyHost > 0 ? criteria + " = '"+ emptyHost +"'" : null;
				
		/* Conectar a la cola local de eventos o ack */
		startJMSConsumer(emptyQueueName, thisOrgID, filter);
		getConsumerConnection().start();
		/* Vaciarla! */
    	Message m = null;
    	int attempts = 0;
    	int messageCount = 0;
		boolean finished = false;    	
		while (!finished)
		{
			try 
			{
				m = getConsumer().receive(1);				
			}	            	
			catch (JMSException e) {
        		Thread.sleep(500);
        		// si ya se reintentó varias veces, no continuar con el intento de recepcion
        		// En realidad esto se ejecutará solo localmente, con lo cual no debería ocurrir
        		if (attempts++ == 5) {
        			String errMsg = "Imposible eliminar los mensajes.  Se desistió luego de varios intentos. " + e.getMessage();
        			throw new Exception(errMsg);
        		}
        	}
			
			if (m != null)
			{
				messageCount++;
				m.acknowledge();
			}
            else 
            	finished = true;
		}
		
		return "OK. Mensajes vaciados: " + messageCount;
		
	}

}
