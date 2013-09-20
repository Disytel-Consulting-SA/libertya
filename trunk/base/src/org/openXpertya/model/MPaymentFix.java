package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MPaymentFix extends X_C_PaymentFix {

	private List<MPaymentFixLine> lines = null;
	private MAllocationHdr allocationHdr = null;
	
	/**
	 * Constructor de PO
	 * @param ctx
	 * @param C_PaymentFix_ID
	 * @param trxName
	 */
	public MPaymentFix(Properties ctx, int C_PaymentFix_ID, String trxName) {
		super(ctx, C_PaymentFix_ID, trxName);
		// Nuevo registro
		if (C_PaymentFix_ID == 0) {
			// Defaults
			setVoidedAmt(BigDecimal.ZERO);
			setAllocatedAmt(BigDecimal.ZERO);
			setBalance(BigDecimal.ZERO);
			setProcessed(false);
		}
	}

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPaymentFix(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Solo es posible crear correcciones para algunos de los tipos de
		// asignación existentes. El método setea el msg de error.
		if (!validateAllocationType()) {
			return false;
		}
		
		return true;
	}

	/**
	 * Valida el tipo de asignación para verificar si es posible o no crear la
	 * corrección.
	 * 
	 * @return <code>true</code> si es posible, <code>false</code> si no.
	 */
	private boolean validateAllocationType() {
		boolean valid = true;
		MAllocationHdr allocHdr = getAllocationHdr();
		if(allocHdr == null){
			log.saveError("SaveError", Msg.getMsg(getCtx(), "MissingAllocationsToPaymentFix"));
			return false;
		}
		String allocType = allocHdr.getAllocationType();
		// Solo se pueden efectuar correcciones para Asignaciones Manuales o generadas
		// por el TPV (Sales Transaction).
		if (!MAllocationHdr.ALLOCATIONTYPE_Manual.equals(allocType)
				&& !MAllocationHdr.ALLOCATIONTYPE_SalesTransaction.equals(allocType)) {
			
			String msg = null;
			if (MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt.equals(allocType)
					|| MAllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt.equals(allocType)) {
				msg = Msg.translate(getCtx(), "RCPaymentFixNotAllowedError");
			} else {
				String allocTypeName = MRefList.getListName(getCtx(),
						MAllocationHdr.ALLOCATIONTYPE_AD_Reference_ID, allocType);
				msg = Msg.getMsg(getCtx(), "AllocationPaymentFixNotAllowed", new Object[] { allocTypeName });
			}
			
			log.saveError("SaveError", msg);
			valid = false;
		}
		return valid;
	}

	/**
	 * Actualiza los importes de la corrección a partir de los importes de sus
	 * líneas.
	 */
	public void updateAmounts() {
		BigDecimal voidedAmt = BigDecimal.ZERO;
		BigDecimal allocatedAmt = BigDecimal.ZERO;
		
		for (MPaymentFixLine line : getLines(false)) {
			if (line.isVoidAction()) {
				voidedAmt = voidedAmt.add(line.getPayAmt());
			} else if (line.isAllocateAction()) {
				allocatedAmt = allocatedAmt.add(line.getPayAmt());
			}
		}
		
		setVoidedAmt(voidedAmt);
		setAllocatedAmt(allocatedAmt);
		
		// El saldo es la diferencia entre el importe anulado y el asignado.
		setBalance(getVoidedAmt().subtract(getAllocatedAmt()));
	}
	
	/**
	 * Devuelve la líneas de esta corrección. Si no tiene líneas devuelve lista vacía.
	 * @param reload <code>true</code> para recargar datos desde la BD.
	 * @return Lista
	 */
	public List<MPaymentFixLine> getLines(boolean reload) {
		if (lines == null || reload) {
			lines = new ArrayList<MPaymentFixLine>();
			String sql = "SELECT * FROM C_PaymentFixLine WHERE C_PaymentFix_ID = ?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getC_PaymentFix_ID());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					lines.add(new MPaymentFixLine(getCtx(), rs, get_TrxName()));
				}
				
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Error getting Payment Fix Lines", e);
			} finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (Exception e) {}
			}
		}
		
		return lines;
	}

	/**
	 * @return Devuelve el encabezado de asignación asociado a esta corrección o
	 *         <code>null</code> si no tiene uno asociado.
	 */
	public MAllocationHdr getAllocationHdr() {
		if ((allocationHdr == null
				|| getC_AllocationHdr_ID() != allocationHdr.getC_AllocationHdr_ID()) 
		     && getC_AllocationHdr_ID() > 0) {
			
			allocationHdr = new MAllocationHdr(getCtx(), getC_AllocationHdr_ID(), get_TrxName());
		}
		return allocationHdr; 
	}

	/**
	 * Procesamiento de esta corrección de cobros
	 * 
	 * @return <code>true</code> el proceso fue correcto, <code>false</code> si
	 *         no lo fue. En este último caso se puede obtener el mensaje de
	 *         error invocando a {@link #getProcessMsg()}
	 */
	public boolean process() {
		List<MPaymentFixLine> lines = getLines(true);
		
		// Actualiza los importes a partir de las líneas de corrección.
		updateAmounts();
		
		// Deben existir líneas de corrección
		if (lines.isEmpty()) {
			m_processMsg = "@NoLines@";
			return false;
		}
		
		// El saldo de la corrección debe ser cero. Esto implica que se anularon
		// y asignaron pagos por la misma suma de dinero.
		if (getBalance().compareTo(BigDecimal.ZERO) != 0) {
			m_processMsg = "@InvalidPaymentFixBalanceError@";
			return false;
		}
		
		// La asignación debe estar Completada o Cerrada para que se pueda
		// efectuar la corrección de cobros.
		String allocDocStatus = getAllocationHdr().getDocStatus(); 
		if (!MAllocationHdr.DOCSTATUS_Completed.equals(allocDocStatus)
				&& !MAllocationHdr.DOCSTATUS_Closed.equals(allocDocStatus)) {
			m_processMsg = "@InvalidAllocationStatusError@";
			return false;
		}
		
		// Valida que los pagos a anular y asignar estén correctos
		if (!validatePayments()) {
			return false;
		}
		
		// Realiza la corrección
		try {
			performFix();
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
		
		setProcessed(true);
		return true;
	}

	/**
	 * Valida que los pagos a anular y asignar sean correctos (importes
	 * pendientes, estado de anulación previa, etc)
	 * 
	 * @return
	 */
	private boolean validatePayments() {
		PO document = null;
		List<String> validDocumentStatus = new ArrayList<String>();
		String statusErrorMsg = null;
		
		for (MPaymentFixLine line : getLines(false)) {
			document = line.getDocument();
			
			//
			// Anulación de pago o línea de caja
			//
			if (line.isVoidAction()) {
				MAllocationLine allocationLine = new MAllocationLine(getCtx(),
						line.getC_AllocationLine_ID(), get_TrxName());
				// La línea de asignación debe pertenecer al encabezado de
				// asignación que está asociado a esta correción.
				if (allocationLine.getC_AllocationHdr_ID() != getC_AllocationHdr_ID()) {
					m_processMsg = "@PFixInvalidAllocationLineHdrError@. @Line@ #" + line.getLine();
					return false;
				}
				// La línea de asignación debe estar activa. Puede suceder que
				// por fuera se anule la asignación entonces la misma se
				// encuentre desactivada.
				if (!allocationLine.isActive()) {
					m_processMsg = "@PFixInactiveAllocationLineError@. @Line@ #" + line.getLine();
					return false;
				}
				
				// El documento debe estar si o si en estado Completado para
				// poder ser anulado. Si se encuentra en otro estado no es
				// posible anularlo.
				validDocumentStatus.add("CO");
				statusErrorMsg = "@PFixInvalidDocStatusActionVoidError@";
				
			//
			// Asignación de pago o línea de caja.
			//
			} else if (line.isAllocateAction()) {
				// Valida que el importe pendiente del pago o línea de caja sea igual o superior al
				// importe indicado en la línea de corrección.
				String openAmtSQL = null;
				if (document instanceof MPayment) {
					openAmtSQL = "SELECT paymentavailable(?)";
				} else if (document instanceof MCashLine) {
					openAmtSQL = "SELECT cashlineavailable(?)";
				} else {
					log.severe("Invalid document allocation. Document=" + document);
					m_processMsg = "@Error@. @SeeTheLog@";
					return false;
				}
				BigDecimal openAmt = DB.getSQLValueBD(get_TrxName(),
						openAmtSQL, document.getID());
				if (openAmt.compareTo(line.getPayAmt()) < 0) {
					m_processMsg = "@PFixInvalidAllocatePaymentAmtError@. @Line@ #" + line.getLine();
					return false;
				}
				
				// El documento debe estar Completado o Cerrado para poder ser
				// asignado. Otro estado no es permitido.
				validDocumentStatus.add("CO");
				validDocumentStatus.add("CL");
				statusErrorMsg = "@PFixInvalidDocStatusActionAllocateError@";
			}
			
			// Valida el estado del documento
			if (!validDocumentStatus.contains(document.get_Value("DocStatus"))) {
				m_processMsg = statusErrorMsg + " @Line@ #" + line.getLine();
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Ejecuta la corrección de cobros. Elimina las líneas de la asignación
	 * marcadas para anular, anula los cobros asociados en estas líneas y crea
	 * nuevas líneas con los cobros a asignar.
	 * 
	 * @return
	 */
	private void performFix() throws Exception {
		MAllocationHdr allocHdr = getAllocationHdr();
		
		// Aquí estamos seguros que el allocation está Completado o Cerrado.
		// Dado que se va a re-abrir el allocation lo seteamos como Completado
		// para que el engine permita la operación reActivateIt.
		allocHdr.setDocStatus(MAllocationHdr.DOCSTATUS_Completed);
		
		// Re activa el allocation. Con esto revierte todos los cambios
		// realizados al completar el mismo.
		if (!allocHdr.processIt(MAllocationHdr.DOCACTION_Re_Activate)) {
			throw new Exception("@AllocationHdrReactivateError@. " + allocHdr.getProcessMsg());
		}
		
		// Procesa las línea de la corrección
		for (MPaymentFixLine line : getLines(false)) {
			// Eliminación de línea de asignación y anulación del cobro
			if (line.isVoidAction()) {
				processLineVoidAction(line);
			// Creación de nueva línea de asignación con el nuevo cobro.
			} else if (line.isAllocateAction()) {
				processLineAllocateAction(line, allocHdr);
			}
		}
		
		// Completa la asignación nuevamente
		if (!DocumentEngine.processAndSave(allocHdr, MAllocationHdr.ACTION_Complete, false)) {
			throw new Exception("@AllocationProcessError@: " + allocHdr.getProcessMsg());
		}
	}

	/**
	 * Procesa una línea de corrección cuyo tipo es anulación.
	 * 
	 * @param fixLine
	 *            Línea de corrección a procesar
	 * @throws Exception
	 */
	private void processLineVoidAction(MPaymentFixLine fixLine) throws Exception {
		/*
		 * Borra la línea de asignación en cuestión y luego anula el cobro
		 * relacionado, ya sea una línea de caja o un payment. Primero es
		 * necesario borrar la línea de asignación a fin de que al anular el
		 * cobro, no salte la validación que chequea si el cobro ya se encuentra
		 * asignado (solo para Allocations tipo STX).
		 */

		// Obtiene el documento asociado
		DocAction document = (DocAction) fixLine.getDocument();

		// Borra la línea de asignación.
		MAllocationLine allocLine = fixLine.getAllocationLine();
		fixLine.setC_AllocationLine_ID(0);
		fixLine.setProcessing(true);
		if (!fixLine.save()) {
			throw new Exception("@PaymentFixLineSaveError@ (@Line@ #"
					+ fixLine.getLine() + "): "
					+ CLogger.retrieveErrorAsString());
		}
		if (!allocLine.delete(true)) {
			throw new Exception("@AllocationLineDeleteError@ (@Line@ #"
					+ fixLine.getLine() + "): "
					+ CLogger.retrieveErrorAsString());
		}

		if (document instanceof MCashLine) {
			((MCashLine)document).setVoiderAllocationID(getC_AllocationHdr_ID());
			((MCashLine)document).setVoidPOSJournalID(((MCashLine)document).getC_POSJournal_ID());
			((MCashLine)document).setVoidPOSJournalMustBeOpen(false);
		}
		else if(document instanceof MPayment){
			((MPayment)document).setVoidPOSJournalID(((MPayment)document).getC_POSJournal_ID());
			((MPayment)document).setVoidPOSJournalMustBeOpen(false);
		}
		
		// Anula y guarda el documento
		if (!DocumentEngine.processAndSave(document, DocAction.ACTION_Void,
				false)) {
			throw new Exception("@PFixVoidPaymentError@: "
					+ document.getProcessMsg());
		}
	}

	/**
	 * Procesa una línea de corrección cuyo tipo es asignación de nuevo cobro.
	 * 
	 * @param fixLine
	 *            Línea de corrección a procesar
	 * 
	 * @param allocHdr
	 *            Encabezado de asignación sobre el cual se agrega la línea
	 * @throws Exception
	 */
	private void processLineAllocateAction(MPaymentFixLine fixLine,
			MAllocationHdr allocHdr) throws Exception {

		/*
		 * Agrega una nueva línea de asignación con el cobro e importe indicado
		 * en la línea de corrección que se procesa.
		 */
		
		MAllocationLine newAllocLine = new MAllocationLine(allocHdr);
		newAllocLine.setC_BPartner_ID(allocHdr.getC_BPartner_ID());
		// Setea la factura según la factura asociada a esta corrección
		newAllocLine.setC_Invoice_ID(getC_Invoice_ID());
		// Setea el cobro
		if (fixLine.isCashAllocation()) {
			newAllocLine.setC_CashLine_ID(fixLine.getC_CashLine_ID());
		} else if (fixLine.isPaymentAllocation()) {
			newAllocLine.setC_Payment_ID(fixLine.getC_Payment_ID());
		} else {
			log.severe("Invalid PaymentFix Allocation Type");
			throw new Exception("@Error@. @SeeTheLog@");
		}
		// Setea los importes
		newAllocLine.setAmount(fixLine.getPayAmt());
		newAllocLine.setDiscountAmt(BigDecimal.ZERO);
		newAllocLine.setWriteOffAmt(BigDecimal.ZERO);
		newAllocLine.setOverUnderAmt(BigDecimal.ZERO);
		
		// Guarda la línea
		if (!newAllocLine.save()) {
			throw new Exception("@AllocationLineSaveError@ (@Line@ #"
					+ fixLine.getLine() + "): "
					+ CLogger.retrieveErrorAsString());
		}
	}
}
