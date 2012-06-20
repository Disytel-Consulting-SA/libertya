package org.openXpertya.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openXpertya.cc.CurrentAccountConnection;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.reflection.CallConfig;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.reflection.ClientCall;
import org.openXpertya.reflection.JMSCallConfig;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ReplicationSourceProcess extends SvrProcess {

	private static final String EVTQUEUE_JNDI_NAME = "queue/EventQueue";
	
	@Override
	protected String doIt() throws Exception 
	{

		final int NUM_MSGS = 5;
			
        Context jndiContext = null;



        ConnectionFactory connectionFactory = null;
        Destination dest = null;
        
        CurrentAccountConnection aconn = new CurrentAccountConnection(getCtx(), get_TrxName());
        ConnectionFactory aconnFact = aconn.getConnectionFactory(MReplicationHost.getHostForOrg(1010053, get_TrxName()), MReplicationHost.getPortForOrg(1010053, get_TrxName()));

        jndiContext = aconn.getContext(MReplicationHost.getHostForOrg(1010053, get_TrxName()), MReplicationHost.getPortForOrg(1010053, get_TrxName()));
        
        connectionFactory = aconnFact; 
		dest = (Destination) jndiContext.lookup(EVTQUEUE_JNDI_NAME);
		
		Connection connection = null;
		MessageProducer producer = null;
		
		connection = connectionFactory.createConnection();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(dest);
		
		TextMessage message = session.createTextMessage();
		
		for (int i = 0; i < NUM_MSGS; i++) {
			message.setText("This is message " + (i + 1));
		    producer.send(message);
		}
		
		producer.send(session.createMessage());		
		
		
        
		
//		============================================================
        
//        final int NUM_MSGS = 5;
//
//        Context jndiContext = null;
//
//        jndiContext = new InitialContext();
//
//        ConnectionFactory connectionFactory = null;
//        Destination dest = null;
//
//        connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
//        dest = (Destination) jndiContext.lookup(EVTQUEUE_JNDI_NAME);
//        
//        Connection connection = null;
//        MessageProducer producer = null;
//
//        connection = connectionFactory.createConnection();
//
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        producer = session.createProducer(dest);
//
//        TextMessage message = session.createTextMessage();
//
//        for (int i = 0; i < NUM_MSGS; i++) {
//        	message.setText("This is message " + (i + 1));
//            producer.send(message);
//        }
//
//        producer.send(session.createMessage());

            
        return "OK";
    }
		
		
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}
	
	
	
	protected Context getJNDIContext(Properties ctx, String trxName, boolean throwIfNull, int orgID) throws Exception
	{
		ReplicationConnection conn = new ReplicationConnection(orgID, trxName);
		Context context = conn.getContext();
		if(context == null && throwIfNull){
			throw new Exception(Msg.getMsg(ctx, "No Connection To " + orgID ));
		}
		return context;
	}
	
//	
//	/**
//	 * Armo la configuración de la llamada remota y realizo dicha llamada,
//	 * retornando el resultado de ella.
//	 * 
//	 * @param ctx
//	 *            contexto
//	 * @param methodName
//	 *            nombre del método remoto
//	 * @param parameterTypes
//	 *            tipo de los parámetros
//	 * @param parameterValues
//	 *            valores de los parámetros
//	 * @param timerValue
//	 *            clave de búsqueda del timer
//	 * @param timeout
//	 *            valor del timeout, generalmente null cuando poseemos una clave
//	 *            de timer
//	 * @param trxName
//	 *            nombre de la transacción en curso
//	 * @return resultado de la llamada
//	 */
//	protected CallResult makeCall(Properties ctx, String destinationType,
//			String msgType, int ackType, String destinationJNDIName,
//			Map<String, List<Object>> messages, boolean createReplyDestination,
//			boolean sessionTransacted, String timerValue, Long timeout,
//			String trxName) {
//		// Obtener el servidor y mandar los datos para la carga remota
//		CallConfig config = null;
//		CallResult result = null;		
//		try {
//			// Armar la configuración
//			config = makeCallConfig(ctx, destinationType, msgType, ackType,
//					destinationJNDIName, messages, createReplyDestination,
//					sessionTransacted, timerValue, timeout, trxName);
//			// Realizar la llamada remota
//			// Creo y realizo la llamada remota
//			result = call(config);
//		} catch (Exception e) {
//			String msg = Util.isEmpty(e.getMessage()) ? e.getCause()
//					.getMessage() : e.getMessage();
//			if(msg == null){
//				msg = Msg.getMsg(ctx, "NoConnectionToCentral");
//			}
//			result.setMsg(msg,true);
//		}
//		return result;		
//	}
//
//	
//	/**
//	 * Crear la configuración para los llamados remotos
//	 * 
//	 * @param ctx
//	 *            contexto
//	 * @param methodName
//	 *            nombre del método remoto
//	 * @param parameterTypes
//	 *            tipos de datos de los parámetros del método remoto
//	 * @param parameterValues
//	 *            valores de los parámetros del método remoto
//	 * @param timerValue
//	 *            value del timer
//	 * @param timeout
//	 *            timeout en milisegundos
//	 * @param trxName
//	 *            nombre de la transacción
//	 * @return configuración de la llamada remota
//	 * @throws Exception
//	 */
//	protected CallConfig makeCallConfig(Properties ctx, String destinationType,
//			String msgType, Integer ackType, String destinationJNDIName,
//			Map<String, List<Object>> messages, boolean createReplyDestination,
//			boolean sessionTransacted, String timerValue, Long timeout,
//			String trxName) throws Exception {
//		// Obtengo la conexión
//		Context jndiContext = getJNDIContext(ctx, trxName, true, 1);
//		JMSCallConfig callConfig = new JMSCallConfig();
//		callConfig.setJndiContext(jndiContext);
//		callConfig.setCtx(ctx);
//		callConfig.setTrxName(trxName);
//		// Si el value del timer está vacío, verifico el timeout parámetro
//		if(Util.isEmpty(timerValue)){
//			// Si el timeout parámetro es null, entonces dejo como está ya que
//			// se inicializa con un timeout de 0 segundos
//			if(timeout != null){
//				callConfig.setTimeout(timeout);
//			}
//		}
//		else{
//			callConfig.setTimerValue(timerValue);
//		}
//		callConfig.setDestinationType(null); 	// no quiero respuesta
//		callConfig.setMsgType(msgType);
//		if(!Util.isEmpty(ackType, false)){
//			callConfig.setAckType(ackType);
//		}
//		callConfig.setDestinationJNDIName(destinationJNDIName);
//		callConfig.setMessages(messages);
//		callConfig.setCreateReplyDestination(createReplyDestination);
//		callConfig.setSessionTransacted(sessionTransacted);
//		return callConfig;
//	}
//    
//    
//	/**
//	 * Realizar la llamada remota con una configuración parámetro
//	 * 
//	 * @param callConfig
//	 *            configuración de llamada
//	 * @return resultado de la llamada remota
//	 * @throws Exception 
//	 */
//	protected CallResult call(CallConfig callConfig) throws Exception{
//		// Obtener el servidor y mandar los datos para la carga remota
//		ClientCall clientCall = null;
//		CallResult result = null;
//		try {
//			// Realizar la llamada remota
//			// Creo y realizo la llamada remota
//			clientCall = new ClientCall(callConfig);
//			result = clientCall.call();
//		} catch (Exception e) {
//			String msg = Util.isEmpty(e.getMessage()) ? e.getCause()
//					.getMessage() : e.getMessage();
//			if(msg == null){
//				msg = Msg.getMsg(callConfig.getCtx(), "NoConnectionToCentral");
//			}
//			result.setMsg(msg,true);
//		}
//		return result;
//	}
//	
//	/**
//	 * Obtengo el contexto jndi para la conexión a la central
//	 * 
//	 * @param ctx
//	 *            contexto de la aplicación
//	 * @param trxName
//	 *            nombre de la transacción
//	 * @return contexto jndi para la conexión a la central, null en caso de error
//	 */
//	protected Context getJNDIContext(Properties ctx, String trxName, boolean throwIfNull, int orgID) throws Exception{
//		ReplicationConnection conn = new ReplicationConnection(orgID, trxName);
//		Context context = conn.getContext();
//		if(context == null && throwIfNull){
//			throw new Exception(Msg.getMsg(ctx, "NoConnectionToCentral"));
//		}
//		return context;
//	}
}
