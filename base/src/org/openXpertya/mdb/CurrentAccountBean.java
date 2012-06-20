package org.openXpertya.mdb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.openXpertya.cc.BalanceLocalStrategy;
import org.openXpertya.cc.CurrentAccountBalanceStrategy;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCentralAux;
import org.openXpertya.model.MOrg;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * 	Bean de Cuentas Corrientes
 *
 *  @ejb:bean name="openXpertya/CurrentAccount"
 *           display-name="Libertya Current Account Bean"
 *           type="Stateless"
 *           transaction-type="Container"
 *           jndi-name="openXpertya/CurrentAccount"
 *           destination-type="javax.jms.Queue"
 *           destination-link="queue/CurrentAccountQueue"
 *           destination-jndi-name = "queue/CurrentAccountQueue"
 *
 *  @ejb:ejb-ref ejb-name="openXpertya/CurrentAccount"
 *              ref-name="openXpertya/CurrentAccount"   
 *                            
 * @author     Equipo de Desarrollo de Libertya  
 * 
 */

public class CurrentAccountBean implements MessageDrivenBean, MessageListener {

	/** Serial Default */
	private static final long serialVersionUID = 1L;
	/**	Context	*/
	private MessageDrivenContext context;
	/** Factory */
	private ConnectionFactory connectionFactory = null;
	
	@Override
	public void ejbRemove() throws EJBException {
		System.out.println("CurrentAccountBean Removed");
	}

	@Override
	public void setMessageDrivenContext(MessageDrivenContext arg0)
			throws EJBException {
		context = arg0;
	}
	
	/**************************************************************************
	 * 	Create the Session Bean - Obligatorio implementar
	 * 	@throws CreateException
	 *  @ejb:create-method view-type="remote"
	 */
	public void ejbCreate(){
		System.out.println("CurrentAccountBean Created");
		connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
	}
	
	/**************************************************************************/
	
	
	@Override
	public void onMessage(Message arg0) {
		String trxName = null;
		Connection conn = null;
		Session session = null;
		Message replyMessage = null; 
		try {
			// Crear la transacción
			trxName = Trx.createTrxName(arg0.getJMSType()
					+ arg0.getJMSMessageID());
			Trx.getTrx(trxName).start();
			// Si es un mensaje re-entregado error
			if(arg0.getJMSRedelivered()){
				throw new Exception("Error: Message Redelivered");
			}
			Object result = doWork(arg0,trxName);
			if(arg0.getJMSReplyTo() != null){
				// Obtengo el destino para contestar
				Destination q = (Destination)context.lookup(arg0.getJMSReplyTo().toString());
				// Creo una conexión
	            conn = connectionFactory.createConnection();
	            // Creo una sesión
	            session = conn.createSession(true, 0);
	            // Creo un productor para ese destino
	            MessageProducer producer = session.createProducer(q);
				// Creo el mensaje de respuesta con el objeto devuelto por el
				// método correspondiente dependiendo el tipo del mensaje
				// ingresado
	            replyMessage = session.createObjectMessage((Serializable)result);
	            // Envío el mensaje de respuesta
				producer.send(replyMessage, DeliveryMode.NON_PERSISTENT, 4,
						arg0.getJMSExpiration());
			}
			Trx.getTrx(trxName).commit();
		} catch (Exception e) {
			Trx.getTrx(trxName).rollback();			
		} finally{
			Trx.getTrx(trxName).close();
		}
	}

	/**
	 * Determino qué método debo ejecutar
	 * 
	 * @param msg
	 *            mensaje entrante
	 * @return objeto resultante del método ejecutado
	 * @throws Exception
	 *             en caso de error
	 */
	private Object doWork(Message msg, String trxName) throws Exception{
		Object result = null;
		if(msg.getJMSType().equals("checkInvoicePaymentRulesBalance")){
			result = checkInvoicePaymentRulesBalance(msg,trxName);
		}
		else if(msg.getJMSType().equals("checkInvoiceWithinCreditLimit")){
			result = checkInvoiceWithinCreditLimit(msg,trxName);
		}
		else if(msg.getJMSType().equals("setCurrentAccountStatus")){
			result = setCurrentAccountStatus(msg,trxName);
		}
		else if(msg.getJMSType().equals("updateBPBalance")){
			result = updateBPBalance(msg,trxName);
		}
		else if(msg.getJMSType().equals("getTenderTypesToControl")){
			result = getTenderTypesToControl(msg,trxName);
		}
		else if(msg.getJMSType().equals("saveAuxiliarInfo")){
			result = saveAuxiliarInfo(msg,trxName);
		}
		else if(msg.getJMSType().equals("saveAuxiliarInfoList")){
			result = saveAuxiliarInfoList(msg,trxName);
		}
		else if(msg.getJMSType().equals("hasZeroBalance")){
			result = hasZeroBalance(msg,trxName);
		}
		return result;
	}
	
	/**
	 * Obtengo el auxiliar de la BD en base a la info del DTO.
	 * 
	 * @param auxiliarDTO
	 *            Data Transfer Object Auxiliar
	 * @return el auxiliar de la base que cumple con la info del DTO, null caso
	 *         contrario
	 */
	public MCentralAux getAuxiliar(Properties ctx, AuxiliarDTO auxiliarDTO, String trxName) throws Exception{
		MCentralAux centralAuxiliar = null;
		Integer centralID = getAuxiliarSQL(ctx, auxiliarDTO, trxName);
		if(!Util.isEmpty(centralID, true)){
			centralAuxiliar = new MCentralAux(ctx, centralID, trxName);
		}
		return centralAuxiliar;
	}


	/**
	 * Obtengo el auxiliar de la BD en base a la info del DTO.
	 * 
	 * @param auxiliarDTO
	 *            Data Transfer Object Auxiliar
	 * @return el auxiliar de la base que cumple con la info del DTO, null caso
	 *         contrario
	 */
	public Integer getAuxiliarSQL(Properties ctx, AuxiliarDTO auxiliarDTO, String trxName) throws Exception{
		// Armo el where a partir de la información que tengo en el DTO
		Integer centralAuxID = 0;
		StringBuffer sql = new StringBuffer("SELECT c_centralaux_id FROM c_centralaux ");
		StringBuffer whereClause = new StringBuffer();
		List<Object> whereParams = new ArrayList<Object>();
		// 1) DocumentUID
		if(!Util.isEmpty(auxiliarDTO.getDocUID())){
			whereClause.append(" (documentuid = ?) ");
			whereParams.add(auxiliarDTO.getDocUID());
		}
		// 2) AuthCode
		if(!Util.isEmpty(auxiliarDTO.getAuthCode())){
			whereClause.append(" AND (authcode = ?) ");
			whereParams.add(auxiliarDTO.getAuthCode());
		}
		// 3) DocStatus
//		if(!Util.isEmpty(auxiliarDTO.getDocStatus())){
//			whereClause.append(" AND (docstatus = ?) ");
//			whereParams.add(auxiliarDTO.getDocStatus());
//		}
		// 4) TenderType
		if(!Util.isEmpty(auxiliarDTO.getTenderType())){
			whereClause.append(" AND (tendertype = ?) ");
			whereParams.add(auxiliarDTO.getTenderType());
		}
		// 5) DocType
		if(!Util.isEmpty(auxiliarDTO.getDocType())){
			whereClause.append(" AND (doctype = ?) ");
			whereParams.add(auxiliarDTO.getDocType());
		}
		// Ejecuto la consulta y devuelvo el resultado
		// Si hay cláusula where, entonces coloco el where parámetro
		if ((whereClause != null) && (whereClause.toString().trim().length() > 0)) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}
		PreparedStatement ps = DB.prepareStatement(sql.toString(), trxName);
		int p = 1;
		for (int i = 0; i < whereParams.size(); i++) {
			ps.setObject(p++, whereParams.get(i));
		}
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			centralAuxID = rs.getInt("c_centralaux_id");
		}
		return centralAuxID;
	}
	
	public AuxiliarDTO saveAuxiliarInfo(AuxiliarDTO auxiliarDTO, String trxName) throws Exception{
		System.out.println("Start saveAuxiliarInfo " + new Timestamp(System.currentTimeMillis()));
		try {
			// Verificamos que este registro no se encuentre en la tabla auxiliar
			// actualmente, si es así lo modificamos
			MCentralAux auxiliar = getAuxiliar(auxiliarDTO.getCtx(), auxiliarDTO, trxName);
			// Si no existe ninguno que cumple con las condiciones, creo uno nuevo
			if(auxiliar == null){
				// Creo uno nuevo
				auxiliar = MCentralAux.createAuxiliar(auxiliarDTO.getCtx(),auxiliarDTO,trxName);
			}
			// Realizo las modificaciones si debo actualizar el registro
			auxiliar.setConfirmed(auxiliarDTO.isConfirmed());
			auxiliar.setReconciled(auxiliarDTO.isReconciled());
			auxiliar.setPrepayment(auxiliarDTO.isPrepayment());
			auxiliar.setDocStatus(auxiliarDTO.getDocStatus());
			// Guardo
			if(!auxiliar.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			// Actualizo el estado y saldo de la entidad comercial
			CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
			CallResult result = manager.updateBalanceAndStatus(auxiliarDTO
					.getCtx(), new MOrg(auxiliarDTO.getCtx(), auxiliar
					.getAD_Org_ID(), trxName), new MBPartner(
					auxiliarDTO.getCtx(), auxiliar.getC_BPartner_ID(),
					trxName), trxName);
			// Si hubo error entonces 
			if(result.isError()){
				throw new Exception(result.getMsg());
			}			
		} catch(Exception e){
			String msg = null;
			if(e.getMessage() != null){
				msg = e.getMessage();
			}
			else if(e.getCause() != null){
				msg = e.getCause().getMessage();
			}
			if(Util.isEmpty(msg)){
				msg = "Error auxiliar table update. "+auxiliarDTO;
			}
			// Mensaje
			throw new Exception(Msg.parseTranslation(auxiliarDTO.getCtx(),
					"@Error@ " + msg));
		}
		System.out.println("End saveAuxiliarInfo " + new Timestamp(System.currentTimeMillis()));
		return auxiliarDTO;
	}

	public AuxiliarDTO saveAuxiliarInfo(Message message, String trxName) throws JMSException, Exception{
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		AuxiliarDTO auxiliarDTO = (AuxiliarDTO)params.get("auxiliar");
		try {
			// Verificamos que este registro no se encuentre en la tabla auxiliar
			// actualmente, si es así lo modificamos
			MCentralAux auxiliar = getAuxiliar(auxiliarDTO.getCtx(), auxiliarDTO, trxName);
			// Si no existe ninguno que cumple con las condiciones, creo uno nuevo
			if(auxiliar == null){
				// Creo uno nuevo
				auxiliar = MCentralAux.createAuxiliar(auxiliarDTO.getCtx(),auxiliarDTO,trxName);
			}
			// Realizo las modificaciones si debo actualizar el registro
			auxiliar.setConfirmed(auxiliarDTO.isConfirmed());
			auxiliar.setReconciled(auxiliarDTO.isReconciled());
			auxiliar.setPrepayment(auxiliarDTO.isPrepayment());
			auxiliar.setDocStatus(auxiliarDTO.getDocStatus());
			// Guardo
			if(!auxiliar.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			// Actualizo el estado y saldo de la entidad comercial
			CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
			CallResult result = manager.updateBalanceAndStatus(auxiliarDTO
					.getCtx(), new MOrg(auxiliarDTO.getCtx(), auxiliar
					.getAD_Org_ID(), trxName), new MBPartner(
					auxiliarDTO.getCtx(), auxiliar.getC_BPartner_ID(),
					trxName), trxName);
			// Si hubo error entonces 
			if(result.isError()){
				throw new Exception(result.getMsg());
			}
		} catch(Exception e){
			String msg = null;
			if(e.getMessage() != null){
				msg = e.getMessage();
			}
			else if(e.getCause() != null){
				msg = e.getCause().getMessage();
			}
			if(Util.isEmpty(msg)){
				msg = "Error auxiliar table update. "+auxiliarDTO;
			}			
			// Mensaje
			throw new Exception(Msg.parseTranslation(auxiliarDTO.getCtx(),
					"@Error@ " + msg));
		}
		System.out.println("End saveAuxiliarInfo " + new Timestamp(System.currentTimeMillis()));
		return auxiliarDTO;
	}

	
	public List<AuxiliarDTO> saveAuxiliarInfoList(Message message, String trxName) throws JMSException, Exception{
		System.out.println("Start saveAuxiliarInfo list " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		List<AuxiliarDTO> auxiliarsDTO = (List<AuxiliarDTO>)params.get("auxiliarsDTO");
		AuxiliarDTO auxDTO = null;
		try {
			// Iterar por todos los registros auxiliares parámetro
			for (Object aux : auxiliarsDTO) {
				auxDTO = (AuxiliarDTO)aux;
				// Actualizar la tabla auxiliar
				saveAuxiliarInfo(auxDTO, trxName);
			}
		} catch(Exception e){
			String msg = null;
			if(e.getMessage() != null){
				msg = e.getMessage();
			}
			else if(e.getCause() != null){
				msg = e.getCause().getMessage();
			}
			if(Util.isEmpty(msg)){
				msg = "Error auxiliar table update.";
			}			
			// Mensaje
			throw new Exception(Msg.parseTranslation(auxDTO.getCtx(),
					"@Error@ " + msg));
		}
		System.out.println("End saveAuxiliarInfo list " + new Timestamp(System.currentTimeMillis()));
		return auxiliarsDTO;
	}
	
	public Set<String> getTenderTypesToControl(Message message, String trxName) throws JMSException, Exception{
		System.out.println("Start getTenderTypesToControl " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		Set<String> tenderTypes = new HashSet<String>();
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Obtener los tipos de medios de pago para controlar de la entidad
		// comercial
		CallResult result = balanceStrategy.getTenderTypesToControlStatus(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		tenderTypes = (Set<String>)result.getResult();
		System.out.println("End getTenderTypesToControl " + new Timestamp(System.currentTimeMillis()));
		return tenderTypes;
	}

	public Object checkInvoicePaymentRulesBalance(Message message, String trxName) throws JMSException, Exception {
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		Map<String, BigDecimal> paymentRules = (Map<String, BigDecimal>)params.get("paymentRules");
		
		System.out.println("Start checkInvoicePaymentRulesBalance " + new Timestamp(System.currentTimeMillis()));
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.checkInvoicePaymentRulesBalance(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), paymentRules, trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End checkInvoicePaymentRulesBalance " + new Timestamp(System.currentTimeMillis()));
		return result.getResult();
	}

	public Object checkInvoiceWithinCreditLimit(Message message, String trxName) throws JMSException, Exception {
		System.out.println("Start checkInvoiceWithinCreditLimit " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		BigDecimal invAmt = (BigDecimal)params.get("invAmt");
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.checkInvoiceWithinCreditLimit(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), invAmt, trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End checkInvoiceWithinCreditLimit " + new Timestamp(System.currentTimeMillis()));
		return result.getResult();
	}

	public String setCurrentAccountStatus(Message message, String trxName) throws JMSException, Exception {
		System.out.println("Start setCurrentAccountStatus " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.setCurrentAccountStatus(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End setCurrentAccountStatus " + new Timestamp(System.currentTimeMillis()));
		return (String)result.getResult();
	}

	public BigDecimal updateBPBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String trxName) throws Exception{
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		System.out.println("Start updateBPBalance " + new Timestamp(System.currentTimeMillis()));
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.updateBPBalance(ctx,
				bPartnerColumnNameUID, bPartnerColumnValueUID,
				orgColumnNameUID, orgColumnValueUID, trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End updateBPBalance " + new Timestamp(System.currentTimeMillis()));
		return (BigDecimal)result.getResult();
	}
	
	public BigDecimal updateBPBalance(Message message, String trxName) throws JMSException, Exception{
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		System.out.println("Start updateBPBalance " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.updateBPBalance(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End updateBPBalance " + new Timestamp(System.currentTimeMillis()));
		return (BigDecimal)result.getResult();
	}

	public Boolean hasZeroBalance(Message message, String trxName) throws JMSException, Exception{
		// Creo la estrategia de saldo de entidad comercial local ya que en este
		// momento se que estoy en la central
		System.out.println("Start hasZeroBalance " + new Timestamp(System.currentTimeMillis()));
		ObjectMessage objMessage = (ObjectMessage)message;
		Map<String, Object> params = (Map<String, Object>)objMessage.getObject();
		Properties ctx = (Properties)params.get("ctx");
		String bPartnerColumnNameUID = (String)params.get("bPartnerColumnNameUID");
		String orgColumnNameUID = (String)params.get("orgColumnNameUID");
		Boolean underCreditMinimumAmt = (Boolean)params.get("underCreditMinimumAmt");
		CurrentAccountBalanceStrategy balanceStrategy = new BalanceLocalStrategy();
		// Realizo la llamada local ya que estoy en la central
		CallResult result = balanceStrategy.hasZeroBalance(ctx,
				bPartnerColumnNameUID, params.get("bPartnerColumnValueUID"),
				orgColumnNameUID, params.get("orgColumnValueUID"), underCreditMinimumAmt, trxName);
		// Si ocurrió un error, mando una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		System.out.println("End hasZeroBalance " + new Timestamp(System.currentTimeMillis()));
		return (Boolean)result.getResult();
	}
}
