package org.openXpertya.cc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCreditException;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class BalanceLocalStrategy extends CurrentAccountBalanceStrategy {

	// Constructores

	public BalanceLocalStrategy() {

	}
	
	@Override
	public CallResult checkInvoiceWithinCreditLimit(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			BigDecimal invAmt, String trxName) {
		CallResult result = new CallResult();
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		MOrg org = getOrg(ctx, orgColumnNameUID, orgColumnValueUID, trxName);
		// Obtener el saldo de la entidad comercial
		BigDecimal newBalance = bp.getTotalOpenBalance();

		// Verificar que no sobrepase el límite, si lo hace no se debe poder
		// completar
		newBalance = newBalance.add(invAmt);
		// Obtengo el límite de la entidad comercial configurado + excepción si
		// es que hay alguna
		BigDecimal limit = getBPLimit(ctx, org, bp, trxName);
		if (newBalance.compareTo(limit) == 1) {
			result.parseMsg(ctx, "@DocumentSurpassLimit@ - @TotalOpenBalance@="
					+ bp.getTotalOpenBalance() + ", @SO_CreditLimit@="
					+ bp.getSO_CreditLimit(), true);
		}
		return result;
	}

	@Override
	public CallResult updateBPBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		MOrg org = getOrg(ctx, orgColumnNameUID, orgColumnValueUID, trxName);
		// Obtener el saldo
		CurrentAccountBalanceData onCreditData = getBalanceData(org, bp,
				MInvoice.PAYMENTRULE_OnCredit);
		bp.setTotalOpenBalance(onCreditData.getBalance());
		bp.setSO_CreditUsed(onCreditData.getCreditUsed());
		bp.setActualLifeTimeValue(onCreditData.getActualLifeTimeValue());
		// Guardo la entidad comercial para que se reflejen los cambios al saldo
		if(!bp.save()){
			result.setMsg(CLogger.retrieveErrorAsString(), true);
		}
		result.setResult(bp.getTotalOpenBalance());
		return result;
	}

	@Override
	public CallResult checkInvoicePaymentRulesBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			Map<String, BigDecimal> paymentRules, String trxName) {
		CallResult result = new CallResult();
		// Itero por los tipos de medio de pago y verifico las validaciones
		// correspondientes con límite y saldo
		Set<String> paymentRulesKeys = paymentRules.keySet();
		String key;
		BigDecimal balance = BigDecimal.ZERO;
		for (Iterator<String> iterator = paymentRulesKeys.iterator(); iterator
				.hasNext();) {
			key = iterator.next();
			// Obtengo el monto de la factura que corresponde con el tipo de
			// medio de pago A Crédito o Crédito si es tipo de pago
			balance = balance
					.add(key.equals(MInvoice.PAYMENTRULE_OnCredit)? paymentRules
							.get(key)
							: BigDecimal.ZERO);
		}
		// Si el balance es mayor a 0 significa que se debe chequear
		if(balance.compareTo(BigDecimal.ZERO) > 0){
			// Llamo al método que realiza la validación de crédito
			result = checkInvoiceWithinCreditLimit(ctx, bPartnerColumnNameUID,
					bPartnerColumnValueUID, orgColumnNameUID,
					orgColumnValueUID, balance, trxName);
		}
		return result;
	}

	@Override
	public CallResult setCurrentAccountStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		// Obtengo la organización
		MOrg org = getOrg(ctx, orgColumnNameUID, orgColumnValueUID, trxName);
		// Obtengo una excepción del estado de crédito si es que existe
		MCreditException stateException = getCreditException(ctx, org, bp,
				new Date(Env.getDate().getTime()),
				MCreditException.EXCEPTIONTYPE_CreditStatus, trxName);
		// Si no hay excepciones por estado de crédito, entonces verifico el
		// camino normal del estado de la entidad comercial
		String status = null;
		if(stateException == null){
			// Camino normal, estado de la entidad comercial
			try {
				status = updateCreditStatus(ctx, bp, trxName);
			} catch (Exception e) {
				String msg = e.getMessage() != null ? e.getMessage() : e
						.getCause().getMessage();
				if(Util.isEmpty(msg)){
					msg = "Error Setting credit status";
				}
				result.setMsg(msg, true);
			}
		}
		else{
			// Obtengo el estado de la excepción
			status = stateException.getCreditStatusException();
		}
		if(!result.isError()){
			result.setResult(status);
		}
		return result;
	}

	/**
	 * Actualiza el estado de la entidad comercial y retorna el nuevo estado
	 * 
	 * @param ctx
	 *            contexto
	 * @param bp
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return el estado resultante de la entidad comercial
	 * @throws Exception
	 *             si hubo algún error
	 */
	protected String updateCreditStatus(Properties ctx, MBPartner bp, String trxName) throws Exception{
		String creditStatus = bp.getSOCreditStatus();
		// Si el monto mínimo de bloqueo es menor al saldo de la entidad
		// comercial entonces le seteo el estado de crédito primario y
		// secundario Normal, siempre que el estado actual no esté deshabilitado o no tenga crédito
		if (!creditStatus.equals(MBPartner.SOCREDITSTATUS_NoCreditCheck)
				&& !creditStatus.equals(MBPartner.SOCREDITSTATUS_CreditDisabled)
				&& bp.getTotalOpenBalance().compareTo(bp.getCreditMinimumAmt()) <= 0) {
			// Cambiar el estado a normal
			String old_primaryCreditStatus = bp.getSOCreditStatus();
			String old_secondaryCreditStatus = bp.getSecondaryCreditStatus();
			bp.setSOCreditStatus(MBPartner.SOCREDITSTATUS_CreditOK);
			bp.setSecondaryCreditStatus(MBPartner.SECONDARYCREDITSTATUS_OK);			
			if (!bp.getSOCreditStatus().equals(old_primaryCreditStatus)
					|| !bp.getSecondaryCreditStatus().equals(
							old_secondaryCreditStatus)) {
				if(!bp.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
			return bp.getSOCreditStatus();
		}
		// Setear el estado actual
		// Si está en estado NORMAL entonces verifico la morosidad de las
		// facturas para pasarlo a BLOQUEADO 
		if (creditStatus.equals(MBPartner.SOCREDITSTATUS_CreditOK)
				|| creditStatus.equals(MBPartner.SOCREDITSTATUS_CreditStop)) {
			// Obtengo las facturas impagas de la entidad comercial
			String sql = "SELECT i.c_invoice_id " +
						 "FROM c_invoice as i " +
						 "INNER JOIN c_doctype AS dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
						 "WHERE i.c_bpartner_id = ? AND i.issotrx = 'Y' AND i.docstatus IN ('CO','CL') AND dt.signo_issotrx = 1 AND i.ispaid = 'N' " +
						 "ORDER BY i.dateinvoiced";
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean pastDue = false;
			int diffDueDays = 0;
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, bp.getID());
			rs = ps.executeQuery();
			// Verifico su vencimiento
			// Si hay alguna vencida e impaga entonces
			while (rs.next() && !pastDue){
				// Verifico si está vencida
				diffDueDays = MInvoice.isPastDue(ctx, new MInvoice(ctx, rs
						.getInt("c_invoice_id"), trxName), Env
						.getDate(), true, trxName);
				pastDue = diffDueDays > 0;
			}
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
			// Me guardo los estados de crédito actuales
			String old_primaryCreditStatus = bp.getSOCreditStatus();
			String old_secondaryCreditStatus = bp.getSecondaryCreditStatus();
			// Actualizar el estado primario y secundario
			updatePrimaryAndSecondaryCreditStatus(bp, diffDueDays);
			// Si cambio cualquiera de los dos entonces guardo la entidad
			// comercial
			if (!bp.getSOCreditStatus().equals(old_primaryCreditStatus)
					|| !bp.getSecondaryCreditStatus().equals(
							old_secondaryCreditStatus)) {
				if(!bp.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		}
		return bp.getSOCreditStatus();
	}

	/**
	 * Actualizar el estado de crédito primario y secundario de la entidad
	 * comercial
	 * 
	 * @param bp
	 *            entidad comercial
	 * @param dueDays
	 *            días de mora
	 */
	public void updatePrimaryAndSecondaryCreditStatus(MBPartner bp, Integer dueDays){
		// Actualizar el crédito secundario
		String secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_OK;
		String primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditOK;
		if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.BLOQUEO_AUTOMATICO, CurrentAccountConstants.PRIMERA_NOTA)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_AutomaticStop;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.PRIMERA_NOTA, CurrentAccountConstants.A_LLAMAR)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_FirstNote;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.A_LLAMAR, CurrentAccountConstants.SEGUNDA_NOTA)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_ToCall;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.SEGUNDA_NOTA, CurrentAccountConstants.A_COBRADOR)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_SecondNote;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.A_COBRADOR, CurrentAccountConstants.A_INHABILITAR)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_ToCollector;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(validateSecondaryCreditStatusDiffDays(dueDays, CurrentAccountConstants.A_INHABILITAR, CurrentAccountConstants.INHABILITACION_AUTOMATICA)){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_ToDisable;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditStop;
		}
		else if(dueDays >= CurrentAccountConstants.INHABILITACION_AUTOMATICA){
			secondaryCreditStatus = MBPartner.SECONDARYCREDITSTATUS_AutomaticDisabling;
			primaryCreditStatus = MBPartner.SOCREDITSTATUS_CreditDisabled;
		}
		// Actualizo el estado del crédito primario y secundario
		bp.setSecondaryCreditStatus(secondaryCreditStatus);
		bp.setSOCreditStatus(primaryCreditStatus);
	}

	/**
	 * Verifica si los días de mora parámetro es mayor o igual a la primer
	 * cantidad de días parámetro y menor estricto a la segunda cantidad de días
	 * parámetro.
	 * 
	 * @param dueDays
	 *            días de mora
	 * @param secondaryFirstCountDays
	 *            primera cantidad de días
	 * @param secondarySecondCountDays
	 *            segunda cantidad de días
	 * @return si se cumplen los criterios descritos
	 */
	public boolean validateSecondaryCreditStatusDiffDays(Integer dueDays, Integer secondaryFirstCountDays, Integer secondarySecondCountDays){
		return dueDays >= secondaryFirstCountDays && dueDays < secondarySecondCountDays;
	}

	/**
	 * Obtengo el límite global de crédito de la entidad comercial. El límite
	 * corresponde al límite global configurado en la pestaña Clientes de la
	 * ventana Entidades Comerciales + monto de excepción de límite de crédito
	 * si es que existe para la fecha parámetro.
	 * 
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bp
	 *            entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return límite de crédito de la entidad comercial
	 */
	protected BigDecimal getBPLimit(Properties ctx, MOrg org, MBPartner bp, String trxName) {
		BigDecimal bpLimit = bp.getSO_CreditLimit(); 
		// Obtengo la excepción de límite de crédito si es que existe
		MCreditException creditLimitException = getCreditException(ctx, org,
				bp, new Date(System.currentTimeMillis()),
				MCreditException.EXCEPTIONTYPE_CreditLimit, trxName);
		// Si existe una excepción de límite de crédito, al límite standard
		// global le sumo el monto de excepción configurado 
		if (creditLimitException != null)
			bpLimit = bpLimit.add(creditLimitException
					.getCreditLimitException());
		return bpLimit;
	}
	

	@Override
	public CallResult afterProcessDocument(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String docColumnNameUID, Object docColumnValueUID, PO doc,
			Object aditionalWorkResult, String trxName) {
		CallResult result1 = new CallResult();
		CallResult result2 = new CallResult();
		// Actualizo el crédito
		result1 = updateBPBalance(ctx, bPartnerColumnNameUID,
				bPartnerColumnValueUID, orgColumnNameUID, orgColumnValueUID,
				trxName);
		// Seteo el estado actual del cliente y lo obtengo
		result2 = setCurrentAccountStatus(ctx, bPartnerColumnNameUID,
				bPartnerColumnValueUID, orgColumnNameUID, orgColumnValueUID,
				trxName);
		// Retorno el que tiene error, si es que hay uno
		return result1.isError()?result1:result2;
	}
	
	@Override
	public CallResult performAditionalWork(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String docColumnNameUID, Object docColumnValueUID, PO doc,
			boolean processed, String trxName) {
		return new CallResult();
	}

	@Override
	public CallResult afterProcessDocument(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			Map<PO, Object> aditionalWorkResults, String trxName) {
		return afterProcessDocument(ctx, bPartnerColumnNameUID,
				bPartnerColumnValueUID, orgColumnNameUID, orgColumnValueUID,
				null, null, null, null, trxName);
	}

	@Override
	public CallResult getTenderTypesToControlStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String trxName) {
		CallResult result = new CallResult();
		Set<String> tenderTypes = new HashSet<String>();
		tenderTypes.add(MPOSPaymentMedium.TENDERTYPE_Credit);
		result.setResult(tenderTypes);
		return result;
	}

	@Override
	public CallResult hasZeroBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, boolean underMinimumCreditAmt, String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		// Determinar el monto de comparación con el saldo, si se debe verificar
		// bajo el monto de crédito mínimo entonces verifico bajo ese monto,
		// sino bajo cero
		BigDecimal amtToCompare = underMinimumCreditAmt?bp.getCreditMinimumAmt():BigDecimal.ZERO;
		Boolean under = bp.getTotalOpenBalance().compareTo(amtToCompare) <= 0;
		result.setResult(under);
		return result;
	}

	@Override
	public CallResult getCreditLimit(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		// FIXME Por ahora se obtiene el límite global, en el caso que existan
		// límites por paymentRule, se debe determinar cada uno
		// independientemente
		result.setResult(bp.getSO_CreditLimit());
		return result;
	}

	@Override
	public CallResult getTotalOpenBalance(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		// FIXME Por ahora se obtiene el saldo global, en el caso que existan
		// saldos por paymentRule, se debe determinar cada uno
		// independientemente
		result.setResult(bp.getTotalOpenBalance());
		return result;
	}

	@Override
	public CallResult getCreditStatus(Properties ctx,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID,
			String paymentRule, String trxName) {
		CallResult result = new CallResult();
		// Obtengo la entidad comercial
		MBPartner bp = getBPartner(ctx, bPartnerColumnNameUID, bPartnerColumnValueUID, trxName);
		// FIXME Por ahora se obtiene el crédito, en el caso que existan
		// estado de crédito por paymentRule, se debe determinar cada uno
		// independientemente
		result.setResult(bp.getSOCreditStatus());
		return result;
	}

	@Override
	public CallResult validateCurrentAccountStatus(Properties ctx,
			String orgColumnNameUID, Object orgColumnValueUID,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String creditStatus, String trxName) {
		return new CallResult();
	}
}
