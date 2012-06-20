package org.openXpertya.reflection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.openXpertya.cc.CurrentAccountConnection;

public class JMSCall extends Call {

	// Variables de instancia
	
	/** Conexión remota */
	private Connection connection; 
	
	/** Sesión remota */
	private Session session;
	
	/** Destino (cola o tópico) */
	private Destination destination;
	
	/** Destino Temporal (cola o tópico), si es que hay que crear uno */
	private Destination tempDestination;
	
	@Override
	public void run(){
		// Creo un objeto para registrar el resultado de la llamada al método
		CallResult callResult = new CallResult();
		Object result = null;
		JMSCallConfig jmsConfig = (JMSCallConfig)getClient().getConfig();
		Context context = jmsConfig.getJndiContext();
		MessageProducer producer = null;
		try{
			// Obtener el factory
			ConnectionFactory factory = (ConnectionFactory) context
					.lookup(CurrentAccountConnection.CONN_FACTORY_JNDI_NAME);
			// Obtener el destino (queue o tópico)
			setDestination((Destination)context.lookup(jmsConfig.getDestinationJNDIName()));
			// Crear la conexión
			setConnection(factory.createConnection());
			// Crear la session
			setSession(getConnection().createSession(jmsConfig.isSessionTransacted(),
					jmsConfig.getAckType()));
			// Crear un productor para el destino del mensaje
			producer = getSession().createProducer(getDestination());
			// Creación del destino temporal para la respuesta del server, si es
			// que se debe dependiendo configuración
			if(jmsConfig.isCreateReplyDestination()){
				setTempDestination(createTemporaryDestination(jmsConfig.getDestinationType()));
			}
			// Obtener los mensajes a enviar
			// Creo los mensajes para enviar
			Set<String> msgTypes = jmsConfig.getMessages().keySet();
			List<Message> messagesToSend = new ArrayList<Message>();
			List<Object> objectsToSend;
			Message msg = null;
			// Itero por todos los tipos de mensajes creo los mensajes a enviar
			for (String type : msgTypes) {
				// Obtengo la lista de objetos de este tipo para enviar
				objectsToSend = jmsConfig.getMessages().get(type);
				// Itero por todos ellos y creo el mensaje de ese tipo y con el
				// objeto correspondiente 
				for (Object ots : objectsToSend) {
					msg = createMessage(type, ots);
					if(jmsConfig.isCreateReplyDestination()){
						msg.setJMSReplyTo(getTempDestination());
					}
					messagesToSend.add(msg);
				}				
			}
			// Itero por los mensajes a enviar y envío cada uno, luego realizo
			// commit si fuese necesario
			for (Message message : messagesToSend) {
				producer.send(message, jmsConfig.getDeliveryMode(),
						jmsConfig.getPriority(), jmsConfig.getTimeout());
			}
			// Si es una sesión transaccionada entonces commiteo todos los sends
			if(jmsConfig.isSessionTransacted()){
				getSession().commit();
			}
			// Esperar la respuesta del server si es lo que se debe realizar
			if(jmsConfig.isCreateReplyDestination()){
				// Bind el destino temporal de respuesta con el contexto actual
				context.bind(getTempDestination().toString(), getTempDestination());
				// Creo el consumidor para el destino de respuesta temporal
				MessageConsumer consumer = getSession().createConsumer(getTempDestination());
				// Comienzo la conexión sino no se puede ser consumidor de nada
				getConnection().start();
				// Recibo la respuesta del destino temporal
				result = consumer.receive(jmsConfig.getTimeout());
				// Unbind el destino temporal
				context.unbind(getTempDestination().toString());
				// Me guardo el resultado
				callResult.setResult(result);
			}
			// Me guardo el resultado
//			if(callResult.isError()){
//				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionResultError",true);
//			}
		} catch(JMSException jmse){
			try{
				if(jmsConfig.isSessionTransacted()){
					getSession().rollback();
				}
				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
				callResult.setMsg(callResult.getMsg()+" . "+getMsgFrom(jmse));
			} catch(JMSException jmse2){
				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
				callResult.setMsg(callResult.getMsg()+" . "+getMsgFrom(jmse2));
			}
		} catch(Exception e){
			try{
				if(jmsConfig.isSessionTransacted()){
					getSession().rollback();
				}
				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
				callResult.setMsg(callResult.getMsg()+" . "+getMsgFrom(e));
			} catch(JMSException jmse){
				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
				callResult.setMsg(callResult.getMsg()+" . "+getMsgFrom(jmse));
			}
		} finally{
			try{
				getConnection().close();
			} catch(JMSException jmse){
				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
				callResult.setMsg(callResult.getMsg()+" . "+getMsgFrom(jmse));
			}
		}
		// Aviso de finalización de método y desbloqueo de llamador
		getClient().callReturn(callResult);
	}
	
	/**
	 * Creo un mensaje dependiendo el tipo parámetro y colocando el objeto
	 * parámetro en él.
	 * 
	 * @param messageType
	 *            tipo de mensaje, ver constantes de {@link JMSCallConfig}, por
	 *            ejemplo {@link JMSCallConfig#MSG_TYPE_OBJECT}
	 * @param objectToSend
	 *            objeto a colocar dentro del mensaje, dependiendo su tipo se
	 *            realiza el casting correspondiente.
	 * @return mensaje creado
	 */
	protected Message createMessage(String messageType, Object objectToSend) throws JMSException{
		Message message = null;
		if(messageType.equals(JMSCallConfig.MSG_TYPE_BYTES)){
			message = createBytesMessage(objectToSend);	
		}
		else if(messageType.equals(JMSCallConfig.MSG_TYPE_MAP)){
			message = createMapMessage(objectToSend);
		}
		else if(messageType.equals(JMSCallConfig.MSG_TYPE_OBJECT)){
			message = createObjectMessage(objectToSend);
		}
		else if(messageType.equals(JMSCallConfig.MSG_TYPE_STREAM)){
			message = createStreamMessage(objectToSend);
		}
		else if(messageType.equals(JMSCallConfig.MSG_TYPE_TEXT)){
			message = createTextMessage(objectToSend);
		}
		return message;
	}

	/**
	 * Crea y retorna un mensaje de bytes para la sesión actual
	 * 
	 * @param object
	 *            objeto
	 * @return mensaje de bytes creado
	 * @throws JMSException
	 *             en caso de error en la creación del mensaje
	 */
	protected BytesMessage createBytesMessage(Object object) throws JMSException{
		BytesMessage msg = getSession().createBytesMessage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		}catch(IOException ioe){
			ioe.getMessage();
		}
		msg.writeBytes(baos.toByteArray());
		return msg;
	}

	/**
	 * Crea un mensaje de tipo map. El objeto parámetro se castea a Map<String,
	 * Object> donde las claves son los nombres de las claves del mensaje map y
	 * los valores son los objetos propios en sí para esas claves. <br>
	 * ACLARACIÓN: los objetos relacionados con las claves deben ser los tipos
	 * primitivos, Integer, Long, String, etc. <br>
	 * CUIDADO: Dentro de este método se realiza un casting del object parámetro
	 * a Map<String, Object> por lo que el parámetro debería ser de ese tipo
	 * 
	 * @param object
	 *            objeto parámetro (map con clave y valor)
	 * @return mensaje de tipo map con los elementos de la map parámetro
	 * @throws JMSException
	 *             en caso de error en la creación del mensaje
	 */
	protected MapMessage createMapMessage(Object object) throws JMSException{
		MapMessage msg = getSession().createMapMessage();
		Map<String, Object> map = (Map<String, Object>)object;
		Set<String> keys = map.keySet();
		for (String key : keys) {
			msg.setObject(key, map.get(key));
		}
		return msg;
	}

	/**
	 * Crea un mensaje de tipo Object. Esto significa que se pasa dentro del
	 * menjaje un objeto serializable. <br>
	 * CUIDADO: Dentro de este mensaje se castea el objeto parámetro a la
	 * interface {@link Serializable}, por lo que el objeto parámetro DEBE
	 * implementar esa interface
	 * 
	 * @param object
	 *            objecto parámetro (DEBE implementar {@link Serializable})
	 * @return mensaje de tipo objeto
	 * @throws JMSException
	 *             en caso de error en la creación del mensaje
	 */
	protected ObjectMessage createObjectMessage(Object object) throws JMSException{
		return getSession().createObjectMessage((Serializable)object);
	}

	/**
	 * Crea un mensaje de tipo stream para la sesión actual
	 * 
	 * @param object
	 *            object a enviar dentro del mensaje
	 * @return un mensaje de tipo stream
	 * @throws JMSException
	 *             en caso de error en la creación del mensaje
	 */
	protected StreamMessage createStreamMessage(Object object) throws JMSException{
		StreamMessage msg = getSession().createStreamMessage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		}catch(IOException ioe){
			ioe.getMessage();
		}
		msg.writeBytes(baos.toByteArray());
		return msg;
	}

	/**
	 * Crea y retorna un mensaje de tipo texto. El objecto parámetro se castea
	 * como un {@link String} por lo que DEBE ser un objecto de ese tipo.
	 * 
	 * @param object
	 *            objecto parámetro (DEBE ser de tipo {@link String})
	 * @return un mensaje de tipo texto
	 * @throws JMSException
	 *             en caso de error en la creación del mensaje
	 */
	protected TextMessage createTextMessage(Object object) throws JMSException{
		return getSession().createTextMessage((String)object);
	}
	
	/**
	 * Crea y retorna un destino temporal dependiendo del tipo de destino
	 * parámetro
	 * 
	 * @return destino temporal
	 */
	protected Destination createTemporaryDestination(String destinationtype) throws JMSException{
		Destination tempDestination = null;
		if(destinationtype.equals(JMSCallConfig.DESTINATION_TYPE_QUEUE)){
			tempDestination = getSession().createTemporaryQueue();
		}
		else{
			tempDestination = getSession().createTemporaryTopic();
		}
		return tempDestination;
	}
	
	// Métodos abstractos

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected Connection getConnection() {
		return connection;
	}

	protected void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}

	protected void setDestination(Destination destination) {
		this.destination = destination;
	}

	protected Destination getDestination() {
		return destination;
	}

	protected void setTempDestination(Destination tempDestination) {
		this.tempDestination = tempDestination;
	}

	protected Destination getTempDestination() {
		return tempDestination;
	}	
}
