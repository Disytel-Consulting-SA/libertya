package org.openXpertya.replication;

import java.util.ArrayList;
import java.util.HashMap;


public class ReplicationConstants {

	/** ============= PREFERENCIAS ============= */
	/** Cantidad de mensajes a procesar en el destino por transaccion */
	public static int REPLICATION_TARGET_MESSAGES_PER_TRX = 100;
	/** Cantidad de queries a agrupar por acceso a BBDD al setear estado de un registro en el origen al enviar */
	public static int REPLICATION_SOURCE_QUERIES_PER_GROUP = 100;
	/** Numero maximo de registros a enviar en esta ejecución (0 = todos) */
	public static int REPLICATION_SOURCE_MAX_RECORDS = 0;
	/** Numero maximo de registros a procesar en esta ejecución (0 = todos) */
	public static int REPLICATION_TARGET_MAX_RECORDS = 0;	
	/** Replicar registros solo desde el host origen dado (0 = todos los que correspondan) */
	public static int REPLICATION_TARGET_REPLICATE_FROM_HOST = 0;
	/** Tiempo de espera para considerar una espera de acknowledge perdida */
	public static String ACK_TIME_OUT = null;	
	/** Fuerza el reenvio de todos los registros (incluso los marcados como confirmados).  Solo en conjunto con ACK_TIME_OUT */
	public static boolean RESEND_ALL_RECORDS = false;
	/** Tabla utilizada para replicar eliminaciones */
	public static final String DELETIONS_TABLE = "AD_Changelog_Replication";	
	
	/** ============= POSIBLES ENTRADAS EN EL REPARRAY DE UN REGISTRO DE UNA TABLA ============= */
	/** Replicación de un registro por primera vez */
	public static Character REPARRAY_REPLICATE_INSERT 			= '1';
	/** Registro replicado */
	public static Character REPARRAY_REPLICATED 				= '2';
	/** Replicar un registro nuevamente */
	public static Character REPARRAY_REPLICATE_MODIFICATION		= '3';
	/** Esperando confirmación (Acknowledge) */
	public static Character REPARRAY_ACK_WAITING 				= '4';
	/** Espera de confirmación, replicar nuevamente luego del ack */
	public static Character REPARRAY_REPLICATE_AFTER_ACK 		= '5';
	/** El estado de reintento tiene dos caracteres, el prefijo X y una letra (A,B,C,etc.) */
	public static Character REPARRAY_RETRY_PREFIX		 		= 'X';
	/** Reintentos de replicacion luego de error */
	public static Character REPARRAY_RETRY1 					= 'A';
	public static Character REPARRAY_RETRY2 					= 'B';
	public static Character REPARRAY_RETRY3 					= 'C';
	public static Character REPARRAY_RETRY4 					= 'D';
	public static Character REPARRAY_RETRY5 					= 'E';
	public static Character REPARRAY_RETRY6 					= 'F';
	public static Character REPARRAY_RETRY7 					= 'G';
	public static Character REPARRAY_RETRY8  					= 'H';
	public static Character REPARRAY_RETRY9  					= 'I';
	public static Character REPARRAY_RETRY10 					= 'J';
	public static Character REPARRAY_NO_RETRY 					= 'X';
	/** Replicar nuevamente el registro si luego de un reintento se replico correctamente */
	public static Character REPARRAY_REPLICATE_AFTER_RETRY1		= 'a';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY2		= 'b';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY3		= 'c';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY4		= 'd';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY5		= 'e';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY6		= 'f';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY7		= 'g';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY8		= 'h';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY9		= 'i';
	public static Character REPARRAY_REPLICATE_AFTER_RETRY10	= 'j';
	public static Character REPARRAY_REPLICATE_NO_RETRY			= 'x';

	/** ============= POSIBLES ENTRADAS EN EL REPLICATIONARRAY DENTRO DE LA CONFIGURACION DE REPLICACION AD_TableReplication ============= */
	/** Replicar esta tabla hacia otros hosts */
	public static Character REPLICATION_CONFIGURATION_NO_ACTION		= '0';
	/** Replicar esta tabla hacia otros hosts */
	public static Character REPLICATION_CONFIGURATION_SEND			= '1';
	/** Replicar esta tabla desde otros hosts */
	public static Character REPLICATION_CONFIGURATION_RECEIVE		= '2';
	/** Replicar esta tabla hacia otros hosts y desde otros hosts (bidireccional) */
	public static Character REPLICATION_CONFIGURATION_SENDRECEIVE	= '3';

	/** ============= CONSTANTES RELACIONADAS CON EL USO DE JMS ============= */
	/** Organizacion destino para un mensaje de evento */
	public static String JMS_EVT_ORG_TARGET 					= "JMSEVTORGTARGET";
	/** Organizacion destino de un evento que origina un ack en el origen */
	public static String JMS_ACK_ORG_TARGET 					= "JMSACKORGTARGET";
	/** Clave del map que contiene la organizacion de origen (repArrayPos) para un mensaje en la ack queue */
	public static String JMS_ACK_ORG_SOURCE 					= "JMSACKORGSOURCE";
	/** Clave del map que contiene los OK/ERR para un mensaje en la ack queue */	
	public static String JMS_ACK_ORG_VALUES 					= "JMSACKORGVALUES";
	/** Se recibió una confirmación de replicación exitosa en la ACK Queue */	
	public static String JMS_ACK_OK 							= "OK";
	/** Se recibió una confirmación de replicación errónea en la ACK Queue */	
	public static String JMS_ACK_ERROR 							= "ERROR";
	
	/** Nombre de la cola de eventos */
	public static final String JMS_EVTQUEUE_JNDI_NAME 			= "queue/EventQueue";
	/** Nombre de la cola de notificaciones (Acknowledge) */
	public static final String JMS_ACKQUEUE_JNDI_NAME 			= "queue/AckQueue";
	
	/** ============= COLECCIONES ============= */
	/** Conjunto: Estados en los que se deberá replicar */
	public static ArrayList<Character> replicateStates = new ArrayList<Character>();
	/** Conjunto: Estados de error de replicacion */
	public static ArrayList<Character> errorStates = new ArrayList<Character>();
	/** Conjunto: Estados de retry por timeout */
	public static ArrayList<Character> timeOutStates = new ArrayList<Character>();	
	/** Dado un estado, obtener el siguiente estado si se recibio un OK */
	public static HashMap<Character, Character> nextStatusWhenOK = new HashMap<Character, Character>();
	/** Dado un estado, obtener el siguiente estado si se recibio un ERROR */
	public static HashMap<Character, Character> nextStatusWhenERR = new HashMap<Character, Character>();
	

	static 
	{
		// Estados de replicacion
		replicateStates.add(REPARRAY_REPLICATE_INSERT);
		replicateStates.add(REPARRAY_REPLICATE_MODIFICATION);
		replicateStates.add(REPARRAY_RETRY1);
		replicateStates.add(REPARRAY_RETRY2);
		replicateStates.add(REPARRAY_RETRY3);
		replicateStates.add(REPARRAY_RETRY4);
		replicateStates.add(REPARRAY_RETRY5);
		replicateStates.add(REPARRAY_RETRY6);
		replicateStates.add(REPARRAY_RETRY7);
		replicateStates.add(REPARRAY_RETRY8);
		replicateStates.add(REPARRAY_RETRY9);
		replicateStates.add(REPARRAY_RETRY10);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY1);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY2);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY3);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY4);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY5);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY6);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY7);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY8);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY9);
		replicateStates.add(REPARRAY_REPLICATE_AFTER_RETRY10);
		 
		// Casos de replicacion por TimeOut del ACK
		timeOutStates.add(REPARRAY_ACK_WAITING);
		timeOutStates.add(REPARRAY_REPLICATE_AFTER_ACK);
		
		// Estados de error
		errorStates.add(REPARRAY_RETRY1);
		errorStates.add(REPARRAY_RETRY2);
		errorStates.add(REPARRAY_RETRY3);
		errorStates.add(REPARRAY_RETRY4);
		errorStates.add(REPARRAY_RETRY5);
		errorStates.add(REPARRAY_RETRY6);
		errorStates.add(REPARRAY_RETRY7);
		errorStates.add(REPARRAY_RETRY8);
		errorStates.add(REPARRAY_RETRY9);
		errorStates.add(REPARRAY_RETRY10);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY1);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY2);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY3);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY4);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY5);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY6);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY7);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY8);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY9);
		errorStates.add(REPARRAY_REPLICATE_AFTER_RETRY10);

		
		
		/** =========== GRAFO DE SIGUIENTES ESTADOS (segun Ref. estados_replicacion.png) =========== */
		// Si recibimos un OK y dado un estado, el siguiente estado será...
		nextStatusWhenOK.put(ReplicationConstants.REPARRAY_ACK_WAITING, ReplicationConstants.REPARRAY_REPLICATED);
		nextStatusWhenOK.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_ACK, ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION);

		// Si recibimos un ERROR y dado un estado, el siguiente estado será...		
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_ACK_WAITING, ReplicationConstants.REPARRAY_RETRY1);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY1, ReplicationConstants.REPARRAY_RETRY2);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY2, ReplicationConstants.REPARRAY_RETRY3);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY3, ReplicationConstants.REPARRAY_RETRY4);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY4, ReplicationConstants.REPARRAY_RETRY5);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY5, ReplicationConstants.REPARRAY_RETRY6);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY6, ReplicationConstants.REPARRAY_RETRY7);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY7, ReplicationConstants.REPARRAY_RETRY8);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY8, ReplicationConstants.REPARRAY_RETRY9);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY9, ReplicationConstants.REPARRAY_RETRY10);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_RETRY10, ReplicationConstants.REPARRAY_NO_RETRY);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_ACK, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY1);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY1, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY2);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY2, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY3);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY3, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY4);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY4, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY5);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY5, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY6);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY6, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY7);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY7, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY8);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY8, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY9);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY9, ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY10);
		nextStatusWhenERR.put(ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY10, ReplicationConstants.REPARRAY_REPLICATE_NO_RETRY);
	}
}
