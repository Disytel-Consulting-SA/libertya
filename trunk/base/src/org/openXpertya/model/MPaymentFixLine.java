package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Msg;

public class MPaymentFixLine extends X_C_PaymentFixLine {

	/** Referencia al encabezado de la corrección de pagos */
	private MPaymentFix paymentFix = null;
	
	/** Línea de asignación cuyo pago será aulado */
	private MAllocationLine allocationLine = null;
	
	/**
	 * Indicador de que se está procesando la línea. Permite saltear ciertas
	 * validaciones que no se deben realizar en ese momento
	 */
	private boolean processing = false;
	
	/**
	 * Obtiene el importe de pago que corresponde para una línea de corrección.
	 * 
	 * @param action
	 *            Acción de la línea. ACTION_Void o ACTION_Allocate.
	 * @param docType
	 *            Tipo de documento para Asignación. DOCUMENTYPE_Cash o
	 *            DOCUMENTTYPE_Payment.
	 * @param cashLineID
	 *            ID de línea de caja (solo Asignación)
	 * @param paymentID
	 *            ID de pago (solo Asignación)
	 * @param allocationLineID
	 *            ID de asignación (solo Anulación)
	 * @param trxName
	 *            Transacción BD
	 * @return el importe o <code>BigDecimal.ZERO</code>
	 */
	public static BigDecimal getPaymentAmount(String action, String docType,
			int cashLineID, int paymentID, int allocationLineID, String trxName) {
		
		String sql = null;
		int documentID = 0;
		BigDecimal amount = BigDecimal.ZERO;
		// Si es anulación obtiene el importe de la línea de asignación.
		if (MPaymentFixLine.ACTION_Void.equals(action)) {
			documentID = allocationLineID;
			sql = 
				"SELECT Amount " +
				"FROM C_AllocationLine " +
				"WHERE C_AllocationLine_ID = ?";
		// Si es asignación, obtiene el pendiente de la línea de caja o pago
		// según corresponda.
		} else if (MPaymentFixLine.ACTION_Allocate.equals(action)){
			if (DOCUMENTTYPE_Cash.equals(docType)) {
				documentID = cashLineID;
				sql = "SELECT cashlineavailable(?)";
			} else if (DOCUMENTTYPE_Payment.equals(docType)) {
				documentID = paymentID;
				sql = "SELECT paymentavailable(?)";
			}
		}
		
		if (sql != null) {
			amount = (BigDecimal)DB.getSQLObject(trxName, sql, new Object[] {documentID});
		}
		return amount;
	}
	
	/**
	 * Obtiene el importe de pago que corresponde para una línea de corrección.
	 * @param paymentFixLine
	 * @return
	 */
	public static BigDecimal getPaymentAmount(MPaymentFixLine paymentFixLine) {
		return getPaymentAmount(paymentFixLine.getAction(),
				paymentFixLine.getDocumentType(),
				paymentFixLine.getC_CashLine_ID(),
				paymentFixLine.getC_Payment_ID(),
				paymentFixLine.getC_AllocationLine_ID(),
				paymentFixLine.get_TrxName());
	}
	
	/**
	 * Constructor de PO
	 * @param ctx
	 * @param C_PaymentFixLine_ID
	 * @param trxName
	 */
	public MPaymentFixLine(Properties ctx, int C_PaymentFixLine_ID,
			String trxName) {
		super(ctx, C_PaymentFixLine_ID, trxName);
		// Nuevo registro
		if (C_PaymentFixLine_ID == 0) {
			// Defaults
			setAction(ACTION_Void);
			setDocumentType(null);
			setPayAmt(BigDecimal.ZERO);
		}
	}

	/**
	 * Constructor de
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPaymentFixLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Constructor a partir de encabezado de corrección.
	 * @param paymentFix
	 */
	public MPaymentFixLine(MPaymentFix paymentFix) {
		this(paymentFix.getCtx(), 0, paymentFix.get_TrxName());
		setClientOrg(paymentFix);
		setC_PaymentFix_ID(paymentFix.getC_PaymentFix_ID());
	}

	@Override
	public void setDocumentType(String DocumentType) {
		if (DocumentType == null || DocumentType.trim().isEmpty()) {
			set_ValueNoCheck("DocumentType", null);
		} else {
			super.setDocumentType(DocumentType);
		}
	}
	
	@Override
	public void setC_AllocationLine_ID(int C_AllocationLine_ID) {
		if (C_AllocationLine_ID == 0) {
			set_ValueNoCheck("C_AllocationLine_ID", null);
		} else {
			super.setC_AllocationLine_ID(C_AllocationLine_ID);
		}
	}

	@Override
	public void setC_CashLine_ID(int C_CashLine_ID) {
		if (C_CashLine_ID == 0) {
			set_ValueNoCheck("C_CashLine_ID", null);
		} else {
			super.setC_CashLine_ID(C_CashLine_ID);
		}
	}

	@Override
	public void setC_Payment_ID(int C_Payment_ID) {
		if (C_Payment_ID == 0) {
			set_ValueNoCheck("C_Payment_ID", null);
		} else {
			super.setC_Payment_ID(C_Payment_ID);
		}

	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Anulación de un Pago (AllocationLine)
		if (isVoidAction()) {
			
			// Se requiere el AllocationLine para obtener el pago a anular.
			if (getC_AllocationLine_ID() == 0 && !isProcessing()) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "CustomerPayment"));
				return false;
			}
			
			// No se puede configurar un pago a anular (AllocationLine) si ya fue
			// configurado en otra línea de cualquier corrección.
			if (!validateUniqueAllocationLine()) {
				log.saveError("SaveError", Msg.translate(getCtx(), "PaymentConfiguredToVoidError"));
				return false;
			}
			
			// Borra referencias de la acción Asignar
			setDocumentType(null);
			setC_Payment_ID(0);
			setC_CashLine_ID(0);

		// Asignación de nuevo pago (Payment o CashLine)
		} else if (isAllocateAction()) {
			// Asignación de Línea de Caja
			if (isCashAllocation()) {
				// No se puede configurar una línea de caja a asignar si ya fue
				// configurada en otra línea de la corrección
				if (!validateUniqueCashLine()) {
					log.saveError("SaveError", Msg.translate(getCtx(),
							"PaymentConfiguredToAllocateError"));
					return false;
				}
				// Borra referencias
				setC_Payment_ID(0);
			
			// Asignación de Pago
			} else if (isPaymentAllocation()) {
				// No se puede configurar un pago a asignar si ya fue
				// configurado en otra línea de la corrección
				if (!validateUniquePayment()) {
					log.saveError("SaveError", Msg.translate(getCtx(),
							"PaymentConfiguredToAllocateError"));
					return false;
				}
				// Borra referencias
				setC_CashLine_ID(0);
			}
			
			// Si no hay importe lo toma del documento
			if (getPayAmt() == null || getPayAmt().compareTo(BigDecimal.ZERO) == 0) {
				setPayAmt(getPaymentAmount(this));
			}
			
			// Si el importe continúa siendo cero entonces no se permite
			// guardar. No es posible asignar documentos cuyo pendiente sea
			// cero.
			if (getPayAmt().compareTo(BigDecimal.ZERO) == 0) {
				log.saveError("SaveError", Msg.translate(getCtx(),
								"AllocationInvalidPayAmtError"));
				return false;
			}
			
			// Borra referencias de otras acciones
			setC_AllocationLine_ID(0);
		}
		
		// Asigna la descripción del cobro a partir de los datos de la línea.
		setPaymentDescription();
		
		return true;
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		if (success) {
			getPaymentFix().updateAmounts();
			success = getPaymentFix().save();
		}
		return success;
	}
	
	@Override
	protected boolean afterDelete(boolean success) {
		if (success) {
			getPaymentFix().updateAmounts();
			success = getPaymentFix().save();
		}
		return success;
	}

	/**
	 * @return Indica si esta línea de corrección implica la anulación de un
	 *         pago.
	 */
	public boolean isVoidAction() {
		return ACTION_Void.equals(getAction());
	}

	/**
	 * @return Indica si esta línea de corrección implica un asignación de un
	 *         nuevo pago.
	 */
	public boolean isAllocateAction() {
		return ACTION_Allocate.equals(getAction());
	}
	
	/**
	 * @return Indica si esta línea de corrección implica una asignación de
	 *         línea de caja.
	 */
	public boolean isCashAllocation() {
		return isAllocateAction() && DOCUMENTTYPE_Cash.equals(getDocumentType());
	}

	/**
	 * @return Indica si esta línea de corrección implica una asignación de
	 *         un pago (C_Payment).
	 */
	public boolean isPaymentAllocation() {
		return isAllocateAction() && DOCUMENTTYPE_Payment.equals(getDocumentType());
	}
	
	/**
	 * @return Encabezado de corrección al cual pertenece esta línea.
	 */
	public MPaymentFix getPaymentFix() {
		if (paymentFix == null) {
			paymentFix = new MPaymentFix(getCtx(), getC_PaymentFix_ID(), get_TrxName());
		}
		return paymentFix;
	}

	/**
	 * Verifica si el AllocationLine de está línea ya se encuentra referenciado
	 * en otra línea de corrección.
	 * 
	 * @return <code>true</code> si no está referenciado, <code>false</code> si
	 *         lo está
	 */
	private boolean validateUniqueAllocationLine() {
		return validateUniqueDocument("C_AllocationLine", getC_AllocationLine_ID(), false);
	}
	
	/**
	 * Verifica si el CashLine de está línea ya se encuentra referenciado
	 * en otra línea de corrección.
	 * 
	 * @return <code>true</code> si no está referenciado, <code>false</code> si
	 *         lo está
	 */
	private boolean validateUniqueCashLine() {
		return validateUniqueDocument("C_CashLine", getC_CashLine_ID(), true);
	}
	
	/**
	 * Verifica si el Payment de está línea ya se encuentra referenciado
	 * en otra línea de corrección.
	 * 
	 * @return <code>true</code> si no está referenciado, <code>false</code> si
	 *         lo está
	 */
	private boolean validateUniquePayment() {
		return validateUniqueDocument("C_Payment", getC_Payment_ID(), true);
	}

	/**
	 * Verifica si un documento (CashLine, Payment, AllocationLine) ya se
	 * encuentra referenciado en otra línea de corrección que no sea esta
	 * 
	 * @param tableName
	 *            Nombre de la tabla del documento.
	 * @param documentID
	 *            ID del documento
	 * @return <code>true</code> si no está referenciado, <code>false</code> si
	 *         lo está
	 */
	private boolean validateUniqueDocument(String tableName, int documentID, boolean onlyInParent) {
		String tableIDColumn = tableName + "_ID";
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) ")
		   .append("FROM C_PaymentFixLine ")
		   .append("WHERE ").append(tableIDColumn).append("  = ? ")
		   .append(  "AND C_PaymentFixLine_ID <> ? ");
		if (onlyInParent) {
			sql.append("AND C_PaymentFix_ID = ").append(getC_PaymentFix_ID());
		}
		long count = (Long) DB.getSQLObject(get_TrxName(), sql.toString(), new Object[] {
				documentID, getC_PaymentFixLine_ID() });
		return count == 0;
	}
	
	/**
	 * @return La línea de asignación cuyo cobro será anulado
	 */
	public MAllocationLine getAllocationLine() {
		if (allocationLine == null && getC_AllocationLine_ID() > 0) {
			allocationLine = new MAllocationLine(getCtx(), getC_AllocationLine_ID(), get_TrxName());
		}
		return allocationLine;
	}

	/**
	 * @return Devuelve el documento a anular o asignar configurado para esta
	 *         línea de corrección. Si es un pago devuelve una instancia de
	 *         MPayment, si es línea de caja una de MCashLine.
	 */
	public PO getDocument() {
		PO document = null;
		int paymentID = 0;
		int cashLineID = 0;
		
		if (isPaymentAllocation()) {
			paymentID = getC_Payment_ID();
		} else if (isCashAllocation()) {
			cashLineID = getC_CashLine_ID();
		} else if (isVoidAction()) {
			paymentID = getAllocationLine().getC_Payment_ID();
			cashLineID = getAllocationLine().getC_CashLine_ID();
		}
		
		if (paymentID > 0){
			document = new MPayment(getCtx(), paymentID, get_TrxName());
		} else if (cashLineID > 0) {
			document = new MCashLine(getCtx(), cashLineID, get_TrxName());
		}
		
		return document;
	}

	/**
	 * @return el valor de processing
	 */
	public boolean isProcessing() {
		return processing;
	}

	/**
	 * @param processing el valor de processing a asignar
	 */
	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	/**
	 * Asigna la descripción del pago a partir del pago que se anula o asigna.
	 */
	private void setPaymentDescription() {
		String description = null;
		// Descripción del cobro asociado a la línea de imputación
		if (isVoidAction()) {
			String sql = "SELECT Line_Description FROM C_AllocationLine WHERE C_AllocationLine_ID = ?";
			description = DB.getSQLValueString(get_TrxName(), sql, getC_AllocationLine_ID());
		// Descripción del Payment o Línea de Caja asociada a la línea de corrección. 
		} else if (isAllocateAction()) {
			PO document = getDocument();
			if (document instanceof MPayment) {
				MPayment payment = (MPayment)document;
				description = payment.getDocumentNo()
				+ "_"
				+ DisplayType.getDateFormat(DisplayType.Date).format(
						payment.getDateTrx());

			} else if (document instanceof MCashLine) {
				MCashLine cashLine = (MCashLine)document;
				
				description = cashLine.getCash().getName()
						+ "_"
						+ DisplayType.getNumberFormat(DisplayType.Amount)
								.format(cashLine.getAmount()) + "_"
						+ cashLine.getLine();
			}
		}
		setDescription(description);
	}
	
}
