package org.openXpertya.cc;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCreditException;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;

public abstract class CurrentAccountBalanceStrategy extends
		CurrentAccountStrategy {
	
	// Variables de clase

	/**
	 * Variable que asocia los tender types de payments con payment rules de
	 * invoices
	 */
	protected static Map<String, String> tenderTypesOnPaymentsRules;

	/**
	 * Variable que asocia los payment rules de invoices con tender type de
	 * pagos tender types de payments
	 */
	
	protected static Map<String, String> paymentsRulesOnTenderTypes;
	
	// Inicialización estática

	static {
		// Payment Rules -> Tender types
		tenderTypesOnPaymentsRules = new HashMap<String, String>();
		tenderTypesOnPaymentsRules.put(MPayment.TENDERTYPE_Check,
				MInvoice.PAYMENTRULE_Check);
		tenderTypesOnPaymentsRules.put(MPOSPaymentMedium.TENDERTYPE_Cash,
				MInvoice.PAYMENTRULE_Cash);
		tenderTypesOnPaymentsRules.put(MPayment.TENDERTYPE_CreditCard,
				MInvoice.PAYMENTRULE_CreditCard);
		tenderTypesOnPaymentsRules.put(MPayment.TENDERTYPE_DirectDebit,
				MInvoice.PAYMENTRULE_DirectDebit);
		tenderTypesOnPaymentsRules.put(MPayment.TENDERTYPE_DirectDeposit,
				MInvoice.PAYMENTRULE_DirectDeposit);
		tenderTypesOnPaymentsRules.put(MPOSPaymentMedium.TENDERTYPE_Credit,
				MInvoice.PAYMENTRULE_OnCredit);
		// Tender types -> Payment Rules
		paymentsRulesOnTenderTypes = new HashMap<String, String>();
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_Check,
				MPayment.TENDERTYPE_Check);
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_Cash,
				MPOSPaymentMedium.TENDERTYPE_Cash);
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_CreditCard,
				MPayment.TENDERTYPE_CreditCard);
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_DirectDebit,
				MPayment.TENDERTYPE_DirectDebit);
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_DirectDeposit,
				MPayment.TENDERTYPE_DirectDeposit);
		paymentsRulesOnTenderTypes.put(MInvoice.PAYMENTRULE_OnCredit,
				MPOSPaymentMedium.TENDERTYPE_Credit);
	}

	/**
	 * Obtengo el equivalente payment rule para el tender type parámetro.
	 * 
	 * @param tenderType
	 *            tipo de pago (en payments)
	 * @return el equivalente payment rule del tender type parámetro, null si no
	 *         existe ninguno
	 */
	public static String getPaymentRuleEquivalent(String tenderType) {
		return tenderTypesOnPaymentsRules.get(tenderType);
	}
	
	/**
	 * Obtengo el equivalente tender type para el payment rule parámetro.
	 * 
	 * @param payment rule 
	 *            forma de pago (en invoices)
	 * @return el equivalente tender type del payment rule parámetro, null si no
	 *         existe ninguno
	 */
	public static String getTenderTypeEquivalent(String paymentRule) {
		return paymentsRulesOnTenderTypes.get(paymentRule);
	}
	
	/**
	 * Obtengo los datos de crédito de la entidad comercial en ese tipo de medio
	 * de pago.
	 * 
	 * @param org
	 *            organización
	 * @param bp
	 *            entidad comercial
	 * @param paymentRule
	 *            tipo de medio de pago
	 * @return datos de crédito o null
	 */
	protected CurrentAccountBalanceData getBalanceData(MOrg org, MBPartner bp,
			String paymentRule) {
		CurrentAccountBalanceData balanceData = CurrentAccountBalanceData
				.getInstance(org, bp, paymentRule);
		if (balanceData != null) {
			balanceData.loadBalanceData();
		}
		return balanceData;
	}	
	
	/**
	 * Obtengo una excepción de crédito a partir de los parámetros. 
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bp
	 *            entidad comercial
	 * @param date
	 *            fecha de excepción
	 * @param exceptionType
	 *            tipo de excepción
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return excepción si es que existe para esos parámetros, null caso
	 *         contrario
	 */
	protected MCreditException getCreditException(Properties ctx, MOrg org,
			MBPartner bp, Date date, String exceptionType, String trxName) {
		// Obtengo una excepción del estado de crédito si es que existe
		return MCreditException.get(ctx, org, bp, date, exceptionType, trxName);
	}

	/*
	 * ****************************************************************
	 * 						MÉTODOS ABSTRACTOS
	 * ****************************************************************
	 */

	/**
	 * Chequeo del saldo de la entidad comercial + monto de la factura respecto
	 * al límite de crédito total.
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param invAmt
	 *            monto convertido de la factura
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult checkInvoiceWithinCreditLimit(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			BigDecimal invAmt, String trxName);

	/**
	 * Actualizar el saldo de la entidad comercial parámetro.
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult updateBPBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String trxName);

	/**
	 * Realizo validaciones de límites y saldo de entidad comercial y los tipos
	 * de medio de pago parámetro.
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param paymentRules
	 *            map que asocia tipo de medio de pago con monto convertido de
	 *            la factura
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult checkInvoicePaymentRulesBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			Map<String, BigDecimal> paymentRules, String trxName);

	/**
	 * Seteo el estado actual de la entidad comercial
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult setCurrentAccountStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String trxName);

	/**
	 * Realizo acciones luego de procesar un documento o transacción
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param docColumnNameUID
	 *            nombre de la columna que identifica al documento unívocamente
	 * @param docColumnValueUID
	 *            valor de la columna que identifica al documento unívocamente
	 * @param doc
	 *            documento o transacción actual
	 * @param aditionalWorkResult
	 *            resultado de la llamada adicional de trabajo al procesar un
	 *            documento, si es que existe
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult afterProcessDocument(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String docColumnNameUID, Object docColumnValueUID, PO doc, 
			Object aditionalWorkResult, String trxName);

	/**
	 * Realizo acciones luego de procesar un documento o transacción
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param doc
	 *            documento o transacción actual
	 * @param aditionalWorkResults
	 *            resultado de la llamadas adicionales de trabajo los documentos
	 *            o transacciones que vienen con ella en al map, si es que
	 *            existe
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult afterProcessDocument(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			Map<PO, Object> aditionalWorkResults, String trxName) throws Exception;
	
	/**
	 * Realizo acciones procesando un documento o transacción
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param docColumnNameUID
	 *            nombre de la columna que identifica al documento unívocamente
	 * @param docColumnValueUID
	 *            valor de la columna que identifica al documento unívocamente
	 * @param doc
	 *            el documento o transacción actual
	 * @param processed
	 *            true si el documento actual ya fue procesado, false caso
	 *            contrario
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult performAditionalWork(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String docColumnNameUID, Object docColumnValueUID, PO doc,
			boolean processed, String trxName);

	/**
	 * Obtener los tipos de pago para el control del estado de crédito
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult getTenderTypesToControlStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String trxName);

	/**
	 * Determinar si el saldo de la entidad comercial parámetro es menor o igual
	 * a 0.
	 * 
	 * @param ctx
	 *            contexto
	 * @param bPartnerColumnNameUID
	 *            nombre de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param bPartnerColumnValueUID
	 *            valor de la columna que identifica a la entidad comercial
	 *            unívocamente
	 * @param orgColumnNameUID
	 *            nombre de la columna que identifica a la organización
	 *            unívocamente
	 * @param orgColumnValueUID
	 *            valor de la columna que identifica a la organización
	 *            unívocamente
	 * @param underMinimumCreditAmt
	 *            true si se debe verificar que se encuentra bajo el monto de
	 *            crédito mínimo, false si se debe verificar en 0
	 * @param trxName
	 *            nombre de la transacción
	 * @return resultado de la llamada
	 */
	public abstract CallResult hasZeroBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, boolean underMinimumCreditAmt, String trxName);

	/**
	 * Obtiene el límite de crédito para el paymentRule pasado como parámetro.
	 * Si el paymentRule es null, se devuelve el límite global
	 * 
	 * @param ctx
	 * @param bPartnerColumnNameUID
	 * @param bPartnerColumnValueUID
	 * @param orgColumnNameUID
	 * @param orgColumnValueUID
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public abstract CallResult getCreditLimit(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName);

	/**
	 * Obtengo el saldo para el payment rule parámetro. Si paymentRule es null,
	 * entonces se devuelve el saldo global
	 * 
	 * @param ctx
	 * @param bPartnerColumnNameUID
	 * @param bPartnerColumnValueUID
	 * @param orgColumnNameUID
	 * @param orgColumnValueUID
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public abstract CallResult getTotalOpenBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName);

	/**
	 * Obtengo el estado de crédito para el payment rule parámetro. Si
	 * paymentRule es null, entonces se devuelve el estado de crédito de la
	 * entidad comercial
	 * 
	 * @param ctx
	 * @param bPartnerColumnNameUID
	 * @param bPartnerColumnValueUID
	 * @param orgColumnNameUID
	 * @param orgColumnValueUID
	 * @param paymentRule
	 * @param trxName
	 * @return
	 */
	public abstract CallResult getCreditStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName);
	
	/**
	 * Validar el estado de crédito dependiendo la estrategia
	 * 
	 * @param ctx
	 * @param bPartnerColumnNameUID
	 * @param bPartnerColumnValueUID
	 * @param orgColumnNameUID
	 * @param orgColumnValueUID
	 * @param creditStatus
	 * @param trxName
	 * @return
	 */
	public abstract CallResult validateCurrentAccountStatus(Properties ctx,
			String orgColumnNameUID, Object orgColumnValueUID,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String creditStatus, String trxName);
	
	// ****************************************************************
}
