package org.openXpertya.cc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public abstract class CurrentAccountManager {

	// Constantes

	/** Prefijo de los mensajes por estado de crédito */

	public static final String CREDIT_STATUS_MSG_PREFIX = "Credit_Status_";

	/**
	 * Clave de mensaje default para mensajes de estados de crédito inexistentes
	 */

	public static final String CREDIT_STATUS_MSG_DEFAULT = CREDIT_STATUS_MSG_PREFIX
			+ "Default";

	// Variables de instancia

	/**
	 * Estrategia para las validaciones de saldo y crédito de la cuenta
	 * corriente
	 */

	private CurrentAccountBalanceStrategy balanceStrategy;

	/** Estrategia para la obtención de comprobantes */

	private CurrentAccountObtainStrategy obtainStrategy;

	/**
	 * Obtengo el valor de la columna unívoca del registro parámetro. <br>
	 * CUIDADO: No funciona para claves múltiples.
	 * 
	 * @param po
	 *            registro o entidad
	 * @return Valor de la columna que identifica unívocamente del regsitro
	 *         parámetro
	 */
	public Object getUIDColumnValue(PO po) throws Exception{
		// Obtengo la columna que identifica univocamente al PO parámetro
		String columnName = getUIDColumnName(po);
		// Obtengo el valor de esa columna para este PO
		Object value = DB.getSQLObject(po.get_TrxName(), "SELECT " + columnName
				+ " FROM " + po.get_TableName() + " WHERE "
				+ po.get_TableName() + "_id = ?", new Object[] { po.getID() });
		return value;
	}
	
	/**
	 * Realiza validaciones básicas
	 * @param bpartner entidad comercial
	 * @return true si pasa las validaciones básicas, false caso contrario
	 */
	public boolean basicValidation(MBPartner bpartner){
		return !Util.isEmpty(bpartner.getSOCreditStatus(), true)
				&& !bpartner.getSOCreditStatus().equals(
						MBPartner.SOCREDITSTATUS_NoCreditCheck);
	}
	
	/**
	 * Realiza validaciones de estado de crédito en base a los montos de la
	 * factura.
	 * 
	 * @param ctx
	 *            contexto
	 * @param bpartner
	 *            entidad comercial
	 * @param org
	 *            organización
	 * @param invAmt
	 *            monto convertido de la factura
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult invoiceWithinCreditLimit(Properties ctx, MOrg org,
			MBPartner bpartner, BigDecimal invAmt, String trxName) throws Exception{
		CallResult result = new CallResult();
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().checkInvoiceWithinCreditLimit(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), invAmt, trxName); 
		}
		return result;
	}

	/**
	 * Actualiza el saldo de la entidad comercial parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult updateBalance(Properties ctx, MOrg org,
			MBPartner bpartner, String trxName) throws Exception{
		CallResult result = new CallResult();
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().updateBPBalance(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), trxName);
		}
		return result;
	}

	/**
	 * Chequeo el saldo de los tipos de medio pago + los montos parámetro para
	 * la entidad comercial indicada
	 * 
	 * @param ctx
	 *            contexto
	 * @param bpartner
	 *            entidad comercial
	 * @param org
	 *            organización
	 * @param paymentRules
	 *            monto convertido discriminado por tipo de medio de pago
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult checkInvoicePaymentRulesBalance(Properties ctx,
			MBPartner bpartner, MOrg org, Map<String, BigDecimal> paymentRules,
			String trxName) throws Exception{
		CallResult result = new CallResult();
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().checkInvoicePaymentRulesBalance(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org),
					paymentRules, trxName);
		}
		return result;
	}

	/**
	 * Seteo el estado de la cuenta corriente de la entidad comercial
	 * 
	 * @param ctx
	 *            contexto
	 * @param bpartner
	 *            entidad comercial
	 * @param org
	 *            organización
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult setCurrentAccountStatus(Properties ctx,
			MBPartner bpartner, MOrg org, String trxName) throws Exception{
		CallResult result = new CallResult();
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().setCurrentAccountStatus(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), trxName); 
		}
		else{
			result.setResult(bpartner.getSOCreditStatus());
		}
		return result;
	}

	/**
	 * Realizo acciones luego de procesar un documento o transacción
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param document
	 *            documento o transacción
	 * @param aditionalWorkResult
	 *            resultado de la llamada adicional de trabajo al procesar un
	 *            documento, si es que existe
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult afterProcessDocument(Properties ctx, MOrg org,
			MBPartner bpartner, PO document, Object aditionalWorkResult,
			String trxName) throws Exception{
		CallResult result = new CallResult();
		if (basicValidation(bpartner)) {
			result = getBalanceStrategy().afterProcessDocument(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org),
					getUIDColumnName(document), getUIDColumnValue(document),
					document, aditionalWorkResult, trxName);
		}
		return result;
	}

	/**
	 * Realizo acciones luego de procesar un documento o transacción
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param aditionalWorkResults
	 *            resultados de la llamada adicional de trabajo al procesar un
	 *            documento de la entidad comercial. Una entidad comercial puede
	 *            tener varios trabajos adicionales, por ejemplo pueden existir
	 *            varias líneas de caja en un libro de caja diario, entonces
	 *            puede llegar a tener varios resultados a trabajos adicionales
	 *            para una misma entidad comercial
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult afterProcessDocument(Properties ctx, MOrg org,
			MBPartner bpartner, Map<PO, Object> aditionalWorkResults,
			String trxName) throws Exception{
		// Realizar la llamada de 
		CallResult result = new CallResult();
		if (basicValidation(bpartner)) {
			result = getBalanceStrategy().afterProcessDocument(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org),
					aditionalWorkResults, trxName);
		}
		return result;
	}
	
	/**
	 * Realizar acciones sobre o a partir del documento o transación
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param document
	 *            documento o transacción
	 * @param processed
	 *            true si el documento parámetro está procesado ya o false si
	 *            está en curso
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return resultado de la llamada
	 */
	public CallResult performAditionalWork(Properties ctx, MOrg org,
			MBPartner bpartner, PO document, boolean processed, String trxName) throws Exception{
		CallResult result = new CallResult();
		if (basicValidation(bpartner)) {
			result = getBalanceStrategy().performAditionalWork(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org),
					getUIDColumnName(document), getUIDColumnValue(document),
					document, processed, trxName);
		}
		return result;
	}

	
	/**
	 * Actualiza el saldo y el estado de crédito de la entidad comercial parámetro 
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult updateBalanceAndStatus(Properties ctx, MOrg org,
			MBPartner bpartner, String trxName) throws Exception{
		CallResult result1 = new CallResult();
		CallResult result2 = new CallResult();
		if(basicValidation(bpartner)){
			// Actualizo el crédito
			result1 = getBalanceStrategy().updateBPBalance(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), trxName);
			// Seteo el estado actual del cliente y lo obtengo
			result2 = getBalanceStrategy().setCurrentAccountStatus(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org),
					trxName);
		}
		// Retorno el que tiene error, si es que hay uno
		return result1.isError()?result1:result2;
	}

	/**
	 * Obtener los tipos de medios de pago que se deben verificar para la
	 * gestión de crédito
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bpartner
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return obtener los tipos de pagos para el control del estado de crédito
	 */
	public CallResult getTenderTypesToControlStatus(Properties ctx, MOrg org,
			MBPartner bpartner, String trxName) throws Exception{
		CallResult result = new CallResult();
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().getTenderTypesToControlStatus(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), trxName);
		}
		else{
			Set<String> tenderTypes = new HashSet<String>();
			tenderTypes.add(MPOSPaymentMedium.TENDERTYPE_Credit);
			result.setResult(tenderTypes);
		}
		return result;
	}
	
	/**
	 * Valida el estado de crédito parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param creditStatus
	 *            estado de crédito
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public CallResult validateCurrentAccountStatus(Properties ctx, MOrg org,
			MBPartner bpartner, String creditStatus, String trxName)
			throws Exception {
		CallResult result = new CallResult();
		// Validaciones principales del estado del crédito
		// Error es cuando el estado no es NORMAL
		boolean error = !creditStatus.equals(MBPartner.SOCREDITSTATUS_CreditOK);
		String msg = null;
		// Error directamente en la organización actual
		if(error){
			// Existe un mensaje por cada valor de la lista de estado de crédito. Si
			// el valor de crédito parámetro (que corresponde con el value) es D,
			// entonces el AD_Message es el prefijo predefinido + D, en el caso que
			// no exista entonces se toma el default
			String AD_Message = CREDIT_STATUS_MSG_PREFIX + creditStatus.trim();
			msg = Msg.getMsg(ctx, AD_Message);
			// Si el mensaje obtenido es igual al pasado como parámetro es porque no
			// encontró ninguno con ese nombre, por lo tanto colocar un default
			if (msg.equals(AD_Message)) {
				// Obtener el nombre de la validación de lista para esa clave
				String name = MRefList.getListName(ctx,
						MBPartner.SOCREDITSTATUS_AD_Reference_ID, creditStatus);
				// Obtengo el mensaje default como parámetro ese nombre de estado de
				// crédito
				msg = Msg.getMsg(ctx, CREDIT_STATUS_MSG_DEFAULT,
						new Object[] { name });
			}
		}
		else{
			// Verificar si la estrategia tiene otras formas de controlar por el estado
			CallResult resultStrategy = getBalanceStrategy()
					.validateCurrentAccountStatus(ctx,
							getUIDColumnName(org),
							getUIDColumnValue(org),
							getUIDColumnName(bpartner),
							getUIDColumnValue(bpartner), creditStatus, trxName);
			msg = resultStrategy.getMsg();
			error = resultStrategy.isError();
		}
		// Seteo el resultado
		result.setMsg(msg, error);
		return result;
	}

	/**
	 * Devuelve el resultado de la llamada con un booleano como resultado
	 * integrado que determina si el saldo de la entidad comercial es menor o
	 * igual a 0, false si es mayor a 0 o existió algún error.
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización actual
	 * @param bpartner
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return resultado de la llamada
	 */
	public CallResult hasZeroBalance(Properties ctx, MOrg org,
			MBPartner bpartner, boolean underMinimumCreditAmt, String trxName) throws Exception{
		CallResult result = new CallResult();
		result = getBalanceStrategy().hasZeroBalance(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), underMinimumCreditAmt, trxName);
		return result;
	}

	/**
	 * Obtengo el límite de crédito para el paymentRule pasado como parámetro.
	 * Si el paymentRule es null, se obtiene el límite global
	 * 
	 * @param ctx
	 * @param org
	 * @param bpartner
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public CallResult getCreditLimit(Properties ctx, MOrg org, MBPartner bpartner, String paymentRule, String trxName) throws Exception{
		CallResult result = new CallResult();
		result.setResult(BigDecimal.ZERO);
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().getCreditLimit(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), paymentRule,
					trxName);
		}
		return result;
	}

	/**
	 * Obtengo el saldo para el payment rule parámetro. Si paymentRule es null,
	 * entonces se devuelve el saldo global
	 * 
	 * @param ctx
	 * @param org
	 * @param bpartner
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public CallResult getTotalOpenBalance(Properties ctx, MOrg org, MBPartner bpartner, String paymentRule, String trxName) throws Exception{
		CallResult result = new CallResult();
		result.setResult(BigDecimal.ZERO);
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().getTotalOpenBalance(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), paymentRule,
					trxName);
		}
		return result;
	}

	/**
	 * Obtengo el estado de crédito para el payment rule parámetro. Si
	 * paymentRule es null, entonces se devuelve el estado de crédito de la
	 * entidad comercial
	 * 
	 * @param ctx
	 * @param org
	 * @param bpartner
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public CallResult getCreditStatus(Properties ctx, MOrg org, MBPartner bpartner, String paymentRule, String trxName) throws Exception{
		CallResult result = new CallResult();
		result.setResult(BigDecimal.ZERO);
		if(basicValidation(bpartner)){
			result = getBalanceStrategy().getCreditStatus(ctx,
					getUIDColumnName(bpartner), getUIDColumnValue(bpartner),
					getUIDColumnName(org), getUIDColumnValue(org), paymentRule,
					trxName);
		}
		return result;
	}
	
	/**
	 * Setea el matching de autorización dependiendo la estrategia de gestión de
	 * cuentas corrientes
	 * 
	 * @param po doc po
	 */
	public void setAuthMatch(PO po){
		// TODO Verificar las premisas a setear false esta columna
//		getBalanceStrategy().setAuthMatch(po);
	}

	// Getters y Setters

	public void setBalanceStrategy(CurrentAccountBalanceStrategy balanceStrategy) {
		this.balanceStrategy = balanceStrategy;
	}

	public CurrentAccountBalanceStrategy getBalanceStrategy() {
		return balanceStrategy;
	}

	public void setObtainStrategy(CurrentAccountObtainStrategy obtainStrategy) {
		this.obtainStrategy = obtainStrategy;
	}

	public CurrentAccountObtainStrategy getObtainStrategy() {
		return obtainStrategy;
	}

	/*
	 * ******************************************************************
	 * MÉTODOS ABSTRACTOS
	 * ******************************************************************
	 */

	/**
	 * Obtengo el nombre de la columna que identifica unívocamente al registro
	 * pasado como parámetro.
	 * 
	 * @param po
	 *            registro o entidad
	 * @return Nombre de la columna que identifica unívocamente al registro
	 */
	public abstract String getUIDColumnName(PO po) throws Exception;

	// ******************************************************************

}
