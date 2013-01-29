package org.openXpertya.reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;

import org.apache.ecs.xhtml.link;

public class JMSCallConfig extends CallConfig {

	/** Constantes con los tipos de mensajes a enviar */
	public static final String MSG_TYPE_TEXT = TextMessage.class.getName();
	public static final String MSG_TYPE_OBJECT = ObjectMessage.class.getName();
	public static final String MSG_TYPE_BYTES = BytesMessage.class.getName();
	public static final String MSG_TYPE_MAP = MapMessage.class.getName();
	public static final String MSG_TYPE_STREAM = StreamMessage.class.getName();
	
	/** Constantes para la comunicación, si es por colas o por tópicos */
	public static final String DESTINATION_TYPE_QUEUE = Queue.class.getName();
	public static final String DESTINATION_TYPE_TOPIC = Topic.class.getName();	
	
	/**
	 * Tipo de ACK para los sends. Por defecto es AUTO_ACKNOWLEDGE. En la clase
	 * {@link Session} se encuentran constantes con los tipos de acks
	 * existentes.
	 */
	private Integer ackType;
	
	/**
	 * Booleano que determina si la sesión debe ser con transacciones, esto es
	 * útil cuando deseamos enviar más de un mensaje (generalmente para enviar
	 * más objetos) y en el caso de errores se realiza un rollback de dicha
	 * sesión. False por defecto.
	 */
	private boolean sessionTransacted;

	/**
	 * Map con tipos de mensajes y el objeto a enviar sobre el canal. Se
	 * realizan todos los send primeramente para todos estos mensajes y luego se
	 * realiza la espera en el destino temporal, si es que se debe crear uno.
	 */
	private Map<String, List<Object>> messages;
	
	/**
	 * Boolean que determina que se debe crear un destino temporal de respuesta al
	 * mensaje enviado. Esto determina que en la llamada, luego el cliente
	 * quedará en escucha sobre esta cola y el receptor del mensaje debería
	 * responder a esta destino temporal, mas allá que si hay un timeout configurado
	 * el que envía no se quedará escuchando para siempre. False por defecto.
	 */
	private boolean createReplyDestination;
	
	/** Contexto JNDI para obtener los objetos */
	private Context jndiContext;
	
	/** Nombre JNDI para el destino, cola o tópico */
	private String destinationJNDIName;
	
	/**
	 * Tipo de comunicación, por cola o tópico. Valores en constantes {@link
	 * this#DESTINATION_TYPE_QUEUE} y {@link this#DESTINATION_TYPE_TOPIC}
	 */
	private String destinationType;
	
	/**
	 * Clasificación de mensaje, esto sirve para que el servidor filtre los
	 * mensajes para donde desee. Podemos tener en una misma cola o tópico
	 * varios tipos de mensajes que hagan diferentes operaciones.
	 */
	private String msgType;
	
	/**
	 * Prioridad. La prioridad va de 0 (muy bajo) a 9 (muy alto). Por defecto se
	 * inicializa en 4.
	 */
	private Integer priority;
	
	/**
	 * Forma de entrega y recepción. Si el mensaje pasó su timeout y el modo de
	 * entrega {@link DeliveryMode#PERSISTENT} se sigue guardando hasta que no
	 * existe más el server, si el modo es {@link DeliveryMode#NON_PERSISTENT}
	 * se desecha. Por defecto es {@link DeliveryMode#NON_PERSISTENT}.
	 */
	private Integer deliveryMode;
	
	public JMSCallConfig() {
		setSessionTransacted(false);
		setCreateReplyDestination(false);
		setAckType(Session.AUTO_ACKNOWLEDGE);
		setPriority(4);
		setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		setMessages(new HashMap<String, List<Object>>());
	}
	
	@Override
	public Call createCall() {
		return new JMSCall();
	}
	
	// Getters y Setters
	
	public void setAckType(Integer ackType) {
		this.ackType = ackType;
	}

	public Integer getAckType() {
		return ackType;
	}
	
	public void setSessionTransacted(boolean sessionTransacted) {
		this.sessionTransacted = sessionTransacted;
	}


	public boolean isSessionTransacted() {
		return sessionTransacted;
	}


	public void setCreateReplyDestination(boolean createReplyDestination) {
		this.createReplyDestination = createReplyDestination;
	}


	public boolean isCreateReplyDestination() {
		return createReplyDestination;
	}

	public void setMessages(Map<String, List<Object>> messages) {
		this.messages = messages;
	}

	public Map<String, List<Object>> getMessages() {
		return messages;
	}

	public void setJndiContext(Context jndiContext) {
		this.jndiContext = jndiContext;
	}

	public Context getJndiContext() {
		return jndiContext;
	}

	public void setDestinationJNDIName(String destinationJNDIName) {
		this.destinationJNDIName = destinationJNDIName;
	}

	public String getDestinationJNDIName() {
		return destinationJNDIName;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setDeliveryMode(Integer deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public Integer getDeliveryMode() {
		return deliveryMode;
	}	
}
