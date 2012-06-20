package org.openXpertya.mdb;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.openXpertya.model.MAsyncReplication;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_AD_AsyncReplication;
import org.openXpertya.replication.ReplicationUtils;
import org.openXpertya.replication.ReplicationXMLUpdater;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

/**
 * 	Bean de Replicacion
 *
 *  @ejb:bean name="openXpertya/Replication"
 *           display-name="Libertya Replication Bean"
 *           type="Stateless"
 *           transaction-type="Container"
 *           jndi-name="openXpertya/Replication"
 *           destination-type="javax.jms.Queue"
 *           destination-link="queue/ReplicationQueue"
 *           destination-jndi-name = "queue/ReplicationQueue"
 *
 *  @ejb:ejb-ref ejb-name="openXpertya/Replication"
 *              ref-name="openXpertya/Replication"   
 *                            
 * @author     Equipo de Desarrollo de Libertya  
 * 
 */







/**
 * 
 * 		EN REALIDAD ESTA CLASE NO VA.
 * 
 */





public class ReplicationBean/* implements MessageDrivenBean, MessageListener*/ {
//
//	/** Serial Default */
//	private static final long serialVersionUID = 1L;
//	/**	Context	*/
//	private MessageDrivenContext context;
//	/** Factory */
//	private ConnectionFactory connectionFactory = null;
//	
//	private String m_trxName = "";
//	
//	@Override
//	public void ejbRemove() throws EJBException {
//		System.out.println("ReplicationBean Removed");
//	}
//
//	@Override
//	public void setMessageDrivenContext(MessageDrivenContext arg0)
//			throws EJBException {
//		context = arg0;
//	}
//	
//	/**************************************************************************
//	 * 	Create the Session Bean - Obligatorio implementar
//	 * 	@throws CreateException
//	 *  @ejb:create-method view-type="remote"
//	 */
//	public void ejbCreate(){
//		System.out.println("ReplicationBean Created");
//		connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
//	}
//	
//	/**************************************************************************/
//	
//	
//	@Override
//	public void onMessage(Message arg0) {
////		String trxName = null;
////		try {
////
////			// Si es un mensaje re-entregado error
////			if(arg0.getJMSRedelivered()){
////				throw new Exception("Error: Message Redelivered");
////			}
////
////			ObjectMessage objMessage = (ObjectMessage)arg0;
////			Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
////
////			Properties ctx = (Properties)params.get("ctx"); 
////			Vector<byte[]> completeCompressedXML = (Vector<byte[]>)params.get("completeCompressedXML"); 
////			boolean sync = (Boolean)params.get("sync");
////			int arrayPos = (Integer)params.get("arrayPos");
////			int initialChangelogID = (Integer)params.get("initialChangelogID");
////			int finalChangelogID = (Integer)params.get("finalChangelogID");
////			boolean force = (Boolean)params.get("force");
////			
////			/* Hay contenido a replicar? */
////			if (completeCompressedXML == null || completeCompressedXML.size() == 0)
////				return;
////
////			// Crear la transacción
////			m_trxName = Trx.createTrxName(arg0.getJMSType()
////					+ arg0.getJMSMessageID());
////			Trx.getTrx(m_trxName).start();
////			
////			/* Validar pedido de replicación */
////			validateReplicationRequest(ctx, arrayPos);
////			
////			/* Determinar el OrgId de origen en funcion de la posicion en el array origen */
////			int AD_Org_ID = MReplicationHost.getReplicationOrgForPosition(arrayPos, m_trxName);
////			if (AD_Org_ID == -1)
////				throw new RemoteException("No es posible determinar la organización a partir del replicationArrayPos");
////			
////			/** Iterar por el vector, obteniendo las páginas de replicacion correspondientes */
////			for (int i=0; i<completeCompressedXML.size(); i++)
////			{
////				/* Obtener el primer pack comprimido */
////				byte[] compressedXML = completeCompressedXML.get(i);
////				
////				/* Contenido correcto para su replicación? */
////				if (compressedXML == null || compressedXML.length == 0)
////					continue;
////				
////				/* Descomprimir el zip */
////				String contentXML = ReplicationUtils.decompressString(compressedXML);
////				if (contentXML == null || contentXML.length() == 0)
////					continue;
////
////				/* Es una replicación tardía? */
////				boolean isDelayedInsert = "Y".equals(Env.getContext(ctx, ReplicationClientProcess.DELAYEDINSERT_PROPERTY));
////				
////				/* Replicación sincrónica o asincrónica? */
////				if (sync)
////				{
////					// Replicación sincrónica: Debo procesar el XML en este preciso momento 
////					// 		Si quedan pendientes replicaciones OFFLINE, en ese momento 
////					//		deberá procesar todo el changelog existente hasta el momento
////					//		(en caso de que force es true, saltear los pendientes en tabla asincronica)
////					boolean allProcessed = force ? true : MAsyncReplication.processPendingContentInAsyncReplication(ctx, AD_Org_ID, m_trxName);
////					if (allProcessed)
////						ReplicationXMLUpdater.processChangelog(contentXML, m_trxName, AD_Org_ID, initialChangelogID, finalChangelogID, isDelayedInsert);
////					else
////						addToAsyncTable(ctx, contentXML, sync, arrayPos, initialChangelogID, finalChangelogID, AD_Org_ID, isDelayedInsert);
////				}
////				else
////					addToAsyncTable(ctx, contentXML, sync, arrayPos, initialChangelogID, finalChangelogID, AD_Org_ID, isDelayedInsert);
////			}
////			
////			
////			Trx.getTrx(trxName).commit();
////		} catch (Exception e) {
////			Trx.getTrx(trxName).rollback();			
////		} finally{
////			Trx.getTrx(trxName).close();
////		}
//	}
//
//
//	/**
//	 * Incorpora la entrada a la tabla de replicación offline
//	 */
//	private void addToAsyncTable(Properties ctx, String contentXML, boolean sync, int arrayPos, int initialChangelogID, int finalChangelogID, int AD_Org_ID, boolean delayedInsert) throws Exception
//	{
//		/* Replicación asincrónica: Solo guardar el XML, el cual será procesado en otro momento */
//		X_AD_AsyncReplication asyncReplication = new X_AD_AsyncReplication(ctx, 0, m_trxName);
//		asyncReplication.setasync_action(delayedInsert ? MAsyncReplication.ASYNC_ACTION_DelayedReplicate : X_AD_AsyncReplication.ASYNC_ACTION_Replicate);
//		asyncReplication.setasync_content(contentXML);
//		asyncReplication.setORG_Source_ID(AD_Org_ID);
//		asyncReplication.setInitialChangelog_ID(initialChangelogID);
//		asyncReplication.setFinalChangelog_ID(finalChangelogID);
//		if (!asyncReplication.save())
//			throw new Exception ("Error al persistir en tabla de replicacion asincronica");
//	}
//	
//	
//	/**
//	 * Verifica que la petición efectivamente sea de la sucursal origen.
//	 * @param ctx el contexto de Libertya
//	 * @throws Exception en caso de que la validación no sea correcta
//	 */
//	protected void validateReplicationRequest(Properties ctx, int arrayPos) throws Exception
//	{
//		/* Obtener Key origen y destino */
//		String sourceKey = Env.getContext(ctx, ReplicationClientProcess.VALIDATION_PROPERTY);
//		String targetKey = DB.getSQLValueString(m_trxName, ReplicationClientProcess.getAccessKeySQLQuery(false), arrayPos);
//		
//		/* En caso de diferir, levantar la excepcion correspondiente */
//		if (!sourceKey.equals(targetKey))
//			throw new Exception(" Error en AccessKey desde sucursal " + arrayPos);
//	}
//
//	
}
