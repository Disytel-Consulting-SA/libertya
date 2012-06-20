package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
/**
 *  Boleta de deposito
 * 
 * Agrupa los valores (cheques) que se depositaran en una cuenta bancaria
 * @author Jorge Vidal - Disytel
 * @version 1.9
 *  
 */
public class MBoletaDeposito extends X_M_BoletaDeposito implements DocAction {
	
	
	/** Líneas de la boleta */
	private MBoletaDepositoLine[] lines = null;

	public MBoletaDeposito(Properties ctx, int M_BoletaDeposito_ID, String trxName) {
		super(ctx, M_BoletaDeposito_ID, trxName);
	}

	public MBoletaDeposito(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Devuelve todas las líneas de la Boleta de Depósito sin
	 * releer los datos de la BD.
	 * @return Arreglo con las líneas
	 */
	public MBoletaDepositoLine[] getLines() {
		return getLines(false);
	}
	
	/**
	 * Devuelve todas las líneas de la Boleta de Depósito
	 * @param reload fuerza a recargar las líneas desde la BD
	 * @return Arreglo con las líneas
	 */
	public MBoletaDepositoLine[] getLines(boolean reload) {
		if (lines == null || reload) {
			ArrayList<MBoletaDepositoLine> list = new ArrayList<MBoletaDepositoLine>();
			StringBuffer sql = new StringBuffer(
					"SELECT * FROM M_BOLETADEPOSITOLINE WHERE M_BOLETADEPOSITO_ID=? ");
	
			PreparedStatement pstmt = null;
			try {
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, getM_BoletaDeposito_ID());
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					list.add(new MBoletaDepositoLine(getCtx(), rs, get_TrxName()));
				rs.close();
				pstmt.close();
				pstmt = null;
			} catch (Exception e) {
				log.log(Level.SEVERE, "getLines - " + sql, e);
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Exception e) {
				}
				pstmt = null;
			}
			//
			lines = new MBoletaDepositoLine[list.size()];
			list.toArray(lines);
		}
		return lines;
	}

	/*----------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 05/09/2006
	 @Comentario: Actualiza el total que corresponde a las lineas
	 @Parametros:
	 -------------------------------------------*/
	public void checkLines() {
		StringBuffer sql = new StringBuffer(
				"SELECT SUM(PAYMENT_AMT) FROM M_BOLETADEPOSITOLINE WHERE M_BOLETADEPOSITO_ID=? ");

		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, getM_BoletaDeposito_ID());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				BigDecimal total = rs.getBigDecimal(1);
				if (!(total.equals(getGrandTotal()))) {
					setGrandTotal(total);
					save();
				}
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "CheckLines - " + sql, e);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
			pstmt = null;
		}
	}

	public boolean approveIt() {
		return false;
	}

	public boolean closeIt() {
		return false;
	}

	/*------------------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 05/09/2006 
	 @Comentario: Completa las lineas de la boleta y luego la boleta
     @Modificado: Franco Bonafine - Disytel
     @Fecha: 24/06/2009	
	 -------------------------------------------*/
	public String completeIt() {
		
		try {
			// Se crean los contra-movimientos por cada cheque a depositar
			createReversalChecks();
			// Acción: Crear un único documento por el total de la boleta
			if (isDocumentForBoleta()) {
				createDocumentForBoleta();
			// Acción: Crear un documento por cada cheque a depositar
			} else if (isDocumentForEachCheck()) {
				createDocumentForEachCheck();
			// Acción inválida.
			} else {
				throw new Exception("Invalid Boleta Action");
			}
			
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return STATUS_Invalid;
			
		}
		// Finaliza correctamente la acción
		setProcessed(true);
		setDocAction(DOCACTION_Void);
		return DOCSTATUS_Completed;
	}

	public BigDecimal getApprovalAmt() {
		return getGrandTotal();
	}

	public int getDoc_User_ID() {
		return 0;
	}


	public String getSummary() {
		String s = "BoletaDeposito " + getDocumentNo() + " "
				+ getGrandTotal().toString() + " "
				+ getFechaDeposito().toString();
		return s;
	}

	public boolean invalidateIt() {
		log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	public boolean postIt() {
		log.info("postIt - " + toString());
		return false;
	}

	public String prepareIt() {
		String error = null;
		MBoletaDepositoLine[] lines = getLines();
		// Se valida el estado de los cheques en las líneas de esta boleta.
		for (MBoletaDepositoLine line : lines) {
			// Se valida que el Cheque asociado a la línea no hayan sido previamente
			// depositados por otra boleta de depósito.
			if (line.getPayment().getM_BoletaDeposito_ID() > 0) {
				error = "@PaymentAlreadyDepositedError@";
			}
			
			// El cheque debe estar en estado Completo
			if (!line.getPayment().getDocStatus().equals(MPayment.DOCSTATUS_Completed)) {
				error = "@CheckMustBeCompleted@";
			}
			
			// El cheque debe pertenecer a una cuenta de cheques en cartera
			if (!checkInCartera(line.getPayment())) {
				error = "@CheckMustBeInCartera@";
			}
			
			// Si hubo error se cancela el preparado.
			if (error != null) {
				m_processMsg = error + " (@Check@: " + line.getPayment().getDocumentNo() + ")";
				return STATUS_Invalid;
			}
		}
		
		// La próxima acción es completar.
		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(action, getDocAction(), log);
	}

	public boolean reActivateIt() {
		return false;
	}

	public boolean rejectIt() {
		return false;
	}

	public boolean reverseAccrualIt() {
		return false;
	}

	public boolean reverseCorrectIt() {
		return false;
	}

	public boolean unlockIt() {
		log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	}

	public boolean voidIt() {
		String error = null;
		MBoletaDepositoLine[] lines = getLines();
		
		for (MBoletaDepositoLine line : lines) {
			// Se anulan el cheque Contra-Movimiento generado a partir
			// del Cheque original asociado a la línea.
			MPayment reversalCheck = line.getReversalPayment();
			// Se cambia el estado para que el engine permita Anular (si está en Closed no
			// permite la anulación)
			reversalCheck.setDocStatus(MPayment.STATUS_Completed); 
			// Se anula el cheque.
			if (!reversalCheck.processIt(MPayment.ACTION_Void)) {
				error = reversalCheck.getProcessMsg();
			} else if (!reversalCheck.save()) {
				error = CLogger.retrieveErrorAsString();
			}
			// Si ocurrió algún error se cancela la operación
			if (error != null) {
				m_processMsg = "@ReversalCheckVoidError@ (" + reversalCheck.getDocumentNo() + "): " + error;
				return false;
			}
			// Si al completar la boleta se generó un cheque entrante a la cuenta
			// destino a partir del cheque original, entonces se anula también
			// este cheque generado.
			if (isDocumentForEachCheck() && line.getC_Depo_Payment_ID() > 0) {
				MPayment depoCheck = line.getDepoPayment();
				// Se cambia el estado para que el engine permita Anular (si está en Closed no
				// permite la anulación)
				depoCheck.setDocStatus(MPayment.DOCSTATUS_Completed);
				if (!depoCheck.processIt(MPayment.ACTION_Void)) {
					error = depoCheck.getProcessMsg();
				} else if (!depoCheck.save()) {
					error = CLogger.retrieveErrorAsString();
				}
				// Si ocurrió algún error se cancela la operación
				if (error != null) {
					m_processMsg = "@IncomeBoletaCheckVoidError@ (" + depoCheck.getDocumentNo() + "): " + error;
					return false;
				}
			}
			
			// Se libera el cheque original para que pueda ser depositado
			// en otra boleta.
			MPayment originalCheck = line.getPayment();
			originalCheck.setM_BoletaDeposito_ID(0);
			originalCheck.setDocStatus(MPayment.DOCSTATUS_Completed);
			originalCheck.setDocAction(MPayment.DOCACTION_Close);
			clearDepositedDescription(originalCheck);
			if (!originalCheck.save()) {
				error = CLogger.retrieveErrorAsString();
				m_processMsg = "@OriginalCheckFreeError@ (" + originalCheck.getDocumentNo() + "): " + error;
				return false;
			}
		}

		// Si la acción al completar fue crear un documento por el total de la boleta
		// entonces se anula también este documento.
		if (isDocumentForBoleta() && getC_Boleta_Payment_ID() > 0) {
			MPayment boletaPayment = getBoletaPayment();
			if (!boletaPayment.processIt(MPayment.ACTION_Void)) {
				error = boletaPayment.getProcessMsg();
			} else if (!boletaPayment.save()) {
				error = CLogger.retrieveErrorAsString();
			}
			// Si ocurrió algún error se cancela la operación
			if (error != null) {
				m_processMsg = "@DocumentForBoletaVoidError@ (" + boletaPayment.getDocumentNo() + "): " + error;
				return false;
			}
		}

		setDocAction(ACTION_None);
		return true;
	}

	/*-----------------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 16/09/2006
	 @Comentario: Setea isReconciled = valor a :
	 	la boleta
	 	las lineas
	 	los pagos asociados a las lineas
	 @Parametros: 
	 valor: Valor a setear como conciliado (true/false)
	 -------------------------------------------*/
	public boolean setConciliado(boolean valor) {
		// TODO Auto-generated method stub
		MBoletaDepositoLine lineas[] = getLines();
		for (int i = 0; i < lineas.length; i++) {
			MBoletaDepositoLine line = lineas[i];
			line.setIsReconciled(valor);
			line.save();
			
			if (line.getC_Payment_ID() != 0) {
				MPayment payment = new MPayment(getCtx(), line.getC_Payment_ID(), get_TrxName());
				payment.setIsReconciled(valor);
				payment.save();
			}
		}
		setIsReconciled(valor);
		setProcessed(true);
		setDocAction(DOCACTION_None);
		save();
		return true;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// No se puede modificar la moneda si ya existen líneas de boleta.
		if (is_ValueChanged("C_Currency_ID") && hasLines()) {
			log.saveError("SaveError", getMsg("CannotChangeBoletaCurrency"));
			return false;
		}
		
		// Validaciones para acción de crear un documento por toda la boleta 
		if (isDocumentForBoleta()) {
			// El Tipo de Documento debe ser un Recibo de Cliente (o subtipo) y el sigo 
			// debe ser -1
			MDocType docType = new MDocType(getCtx(), getC_DocType_ID(), get_TrxName());
			if (getC_DocType_ID() > 0 &&
					!docType.getDocBaseType().equals(MDocType.DOCBASETYPE_ARReceipt) ||
					!docType.getsigno_issotrx().equals("-1")) {
				log.saveError("SaveError", getMsg("InvalidBoletaDocTypeError"));
				return false;
			}
		}
	
		// -------------------------------------------------------------------
		// Completado de valores por defecto. Este código debe permanecer
		// luego de las validaciones
		// -------------------------------------------------------------------

		// Se requiere el tipo de documento para el pago a generar si la acción es
		// generar un documento por el total de la boleta. Si no fue asignado por
		// el usuario se asigna por defecto Cobro a Cliente.
		if (isDocumentForBoleta() && getC_DocType_ID() == 0) {
			setC_DocType_ID(MDocType.
				getDocType(getCtx(), MDocType.DOCTYPE_CustomerReceipt, get_TrxName()).getC_DocType_ID());
		}
	
		return true;
	}
	
	/**
	 * @return Indica si la Boleta contiene al menos una línea de cheque.
	 */
	public boolean hasLines() {
		return getLines().length > 0;
	}
	
	private String getMsg(String name) {
		return Msg.translate(getCtx(), name);
	}
	
	/**
	 * Crea un contra-movimiento por cada cheque a depositar para efectuar la salida
	 * del cheque de la cuenta de cheques en cartera.
	 * @throws Exception cuando se produce algún error al crear o procesar el cheque
	 * original o el contra-movimiento.
	 */
	private void createReversalChecks() throws Exception {
		MBoletaDepositoLine[] lines = getLines(true);
		// Por cada Cheque asociado a la boleta, se crea un contramovimiento
		// con el importe invertido para realizar la salida del cheque de la cuenta
		// de cheques en cartera.
		for (MBoletaDepositoLine line : lines) {
			MPayment check = line.getPayment();
			MPayment reversalCheck = new MPayment(getCtx(), 0, get_TrxName());
			String error = null;
			PO.copyValues(check, reversalCheck);
			// Se asigna la fecha del contra-movimiento
			reversalCheck.setDateTrx(getDocumentDate());
			reversalCheck.setDateAcct(getDocumentDate());
			reversalCheck.setDueDate(getDocumentDate());
			reversalCheck.setIsReconciled(true);			// incidencia 4324 - conciliado con check
			// Se asigna la EC de la boleta
			reversalCheck.setC_BPartner_ID(getC_BPartner_ID());
			// Se invierte el signo del monto del cheque original
			reversalCheck.setPayAmt(check.getPayAmt().negate());
			// Se asigna la organizacion del cheque origen
			reversalCheck.setAD_Org_ID(check.getAD_Org_ID());
			// Se agrega una descripcion
			reversalCheck.addDescription(
					Msg.getMsg(getCtx(), "ReversalCheck", 
					new Object[] { check.getDocumentNo(), getDocumentNo() }));
			
			// FB - Corrección a partir de problema detectado en Intersys
			// Setea el estado a Borrador ya que el copyValues copia el estado
			// del cheque original (Completado) y entonces al completar el
			// reversal el engine ignora esa acción (no se puede completar un
			// documento ya completado) y entonces ejecuta el DocAction guardado
			// en el registro. Normalmente esa acción es CL y por eso no se
			// rompía, pero en Intersys el pago tenía acción RC y quería
			// revertir el reverso, lógica errónea que además tiraba un
			// NullPointerException.
			reversalCheck.setDocStatus(DOCSTATUS_Drafted);
			reversalCheck.setDocAction(DOCACTION_Complete);
			
			// Se Completa y se cierra el cheque
			if (!reversalCheck.save()) {
				error = CLogger.retrieveErrorAsString();
			} else if (!reversalCheck.processIt(ACTION_Complete)) {
				error = reversalCheck.getProcessMsg();
			} else if (!reversalCheck.processIt(ACTION_Close)) {
				error = reversalCheck.getProcessMsg();
			} else if (!reversalCheck.save()) {
				error = CLogger.retrieveErrorAsString();
			}
			// Si ocurrió un error al procesar se termina la operación de completado.
			if (error != null) {
				throw new Exception("@ReversalCheckProcessError@ (" + reversalCheck.getDocumentNo() + "): " + error);
			}
			
			// Al cheque original se le asigna la referencia a esta boleta de depósito
			// y se agrega una descripción.
			check.setM_BoletaDeposito_ID(getM_BoletaDeposito_ID());
			check.addDescription(getDepositedDescription());
			check.setIsReconciled(true);					// incidencia 4324 - conciliado con reversalCheck
			// Se cierra también el cheque original para que no pueda ser depositado nuevamente.
			if (!check.processIt(ACTION_Close)) {
				error = check.getProcessMsg();
			} else if (!check.save()) {
				error = CLogger.retrieveErrorAsString();
			}
			// Si ocurrió un error al procesar se termina la operación de completado.
			if (error != null) {
				throw new Exception("@OriginalCheckProcessError@ (" + check.getDocumentNo() + "): " + error);
			}
			
			// Se asigna a la línea de la boleta la referencia al cheque Contra-Movimiento
			line.setC_Reverse_Payment_ID(reversalCheck.getC_Payment_ID());
			if (!line.save()) {
				error = CLogger.retrieveErrorAsString();
				throw new Exception("@BoletaLineSaveError@ (@Check@ " + check.getDocumentNo() + "): " + error);
			}
		}
	}
	
	/**
	 * Crea un pago por el total de la boleta de depósito.
	 * @throws Exception si se produce un error en la creación o procesado del pago.
	 */
	private void createDocumentForBoleta() throws Exception {
		String error = null;
		// Se crea el pago y se asignan sus valores.
		MPayment payment = new MPayment(getCtx(), 0, get_TrxName());
		// El DocumentNo del pago es el mismo que el de la boleta.
		payment.setDocumentNo(getDocumentNo()); 
		payment.setC_BPartner_ID(getC_BPartner_ID());
		payment.setC_BankAccount_ID(getC_BankAccount_ID());
		payment.setC_Currency_ID(getC_Currency_ID());
		payment.setPayAmt(getGrandTotal());
		payment.setIsReceipt(true);
		payment.setTenderType(MPayment.TENDERTYPE_Check);
		payment.setDateTrx(getDocumentDate());
		payment.setDateAcct(getDocumentDate());
		payment.setAD_Org_ID(getAD_Org_ID());
		
		// Se asigna el Tipo de Documento indicado en la boleta (si existe)
		if (getC_DocType_ID() > 0)
			payment.setC_DocType_ID(getC_DocType_ID());
		else
			payment.setC_DocType_ID(true);
		// Se agrega una descripcion
		payment.setDescription(
				Msg.getMsg(getCtx(), "IncomeBoletaCheck", 
				new Object[] { getDocumentNo() }));

		// Se completa y se guarda el pago
		if (!payment.processIt(MPayment.ACTION_Complete)) {
			error = payment.getProcessMsg();
		} else if (!payment.save()) {
			error = CLogger.retrieveErrorAsString();
		}
		// Si hubo error se dispara una excepcion.
		if (error != null) {
			throw new Exception("@DocumentForBoletaCreateError@: " + error);
		}
		
		// Se asigna la referencia al documento (pago) creado para la boleta
		setC_Boleta_Payment_ID(payment.getC_Payment_ID());
	}
	
	/**
	 * Crea un cheque de entrada a la cuenta de la boleta por cada cheque a depositar.
	 * @throws Exception cuando se produce un error en la creación o procesado de alguno
	 * de los nuevos cheques.
	 */
	private void createDocumentForEachCheck() throws Exception {
		// Se recorren la líneas de la boleta y se crea un nuevo cheque por cada
		// cheque a depositar, en donde la cuenta bancaria y la EC del nuevo cheque
		// son la EC y la Cta Bcria de la Boleta.
		for (MBoletaDepositoLine line : getLines()) {
			MPayment check = line.getPayment();
			MPayment newCheck = new MPayment(getCtx(), 0, get_TrxName());
			String error = null;
			PO.copyValues(check, newCheck);
			// Se asigna la fecha del nuevo cheque
			newCheck.setDateTrx(check.getDateTrx()); // incidencia 4324 - Si se usa el tilde de "crear un payment por cheque", la fecha de emisión y Vto se deberían mantener.
			newCheck.setDateAcct(getDocumentDate());
			newCheck.setDueDate(check.getDueDate()); // incidencia 4324 - Si se usa el tilde de "crear un payment por cheque", la fecha de emisión y Vto se deberían mantener.
			newCheck.setIsReconciled(false);		 // incidencia 4324 - el nuevo cheque no se encuentra inicialmente conciliado
			// Se asigna los datos de la boleta
			newCheck.setC_BPartner_ID(getC_BPartner_ID());
			newCheck.setC_BankAccount_ID(getC_BankAccount_ID());
			newCheck.setAD_Org_ID(getAD_Org_ID());
			// Se agrega una descripcion
			newCheck.setDescription(
					Msg.getMsg(getCtx(), "IncomeBoletaCheck", 
					new Object[] { getDocumentNo() }));
			// Se quita la referencia a la Boleta
			newCheck.setM_BoletaDeposito_ID(0);
			// Se Completa y se cierra el cheque
			if (!newCheck.processIt(ACTION_Complete)) {
				error = newCheck.getProcessMsg();
			} else if (!newCheck.save()) {
				error = CLogger.retrieveErrorAsString();
			}
			// Si ocurrió un error al procesar se termina la operación de completado.
			if (error != null) {
				throw new Exception("@IncomeBoletaCheckProcessError@ (" + newCheck.getDocumentNo() + "): " + error);
			}
			
			// Se asigna a la línea de la boleta la referencia al cheque creado 
			// por el depósito del cheque original.
			line.setC_Depo_Payment_ID(newCheck.getC_Payment_ID());
			if (!line.save()) {
				error = CLogger.retrieveErrorAsString();
				throw new Exception("@BoletaLineSaveError@ (@Check@ " + check.getDocumentNo() + "): " + error);
			}
		}
	}
	
	/**
	 * Verifica si un cheque pertenece a una cuenta bancaria marcada como Cheques en Cartera.
	 * @return
	 */
	private boolean checkInCartera(MPayment payment) {
		String sql = " SELECT IsChequesEnCartera FROM C_BankAccount WHERE C_BankAccount_ID = ?";
		String result = DB.getSQLValueString(get_TrxName(), sql, payment.getC_BankAccount_ID());
		return "Y".equals(result);
	}
	
	/**
	 * @return Indica si la acción para esta boleta es crear un único documento
	 * por el total de la boleta.
	 */
	public boolean isDocumentForBoleta() {
		return BOLETAACTION_DocumentForBoleta.equals(getBoletaAction());
	}
	
	/**
	 * @return Indica si la acción para esta boleta es crear un documento
	 * por cada cheque involucrado en las líneas de la boleta.
	 */
	public boolean isDocumentForEachCheck() {
		return BOLETAACTION_DocumentForEachCheck.equals(getBoletaAction());
	}
	
	/**
	 * @return Devuelve el Pago generado por el total de la boleta
	 * @comment
	 * 	El objeto no está cacheado, siempre se lee de la BD.
	 */
	public MPayment getBoletaPayment() {
		MPayment payment = null;
		if (getC_Boleta_Payment_ID() > 0) {
			payment = new MPayment(getCtx(), getC_Boleta_Payment_ID(), get_TrxName());
		}
		return payment;
	}
	
	/**
	 * @return Devuelve el texto de descripción para un cheque depositado.
	 */
	private String getDepositedDescription() {
		return getMsg("Deposited");
	}
	
	/**
	 * Quita la descripción del pago que indica que el mismo fue depositado. Este método
	 * es utilizado en la anulación de Boleta, donde se debe liberar el cheque originalmente
	 * depositado por la boleta.
	 */
	private void clearDepositedDescription(MPayment payment) {
		String deposited = getDepositedDescription();
		String description = payment.getDescription();
		
		description = description.replaceAll(deposited, "").trim();
		if (description.startsWith("|")) {
			description = description.replaceFirst("|", "").trim();
		}
		if (description.endsWith("|")) {
			description = description.substring(0, description.length() - 1).trim();
		}
		payment.setDescription(description);
	}

	/**
	 * @return Devuelve la fecha de transacción para los documentos generados
	 * al completar la Boleta de Depósito.
	 */
	private Timestamp getDocumentDate() {
		// La fecha de trx de los documentos a generar es la fecha de depósito de
		// la boleta.
		return getFechaDeposito();
	}
	
}
