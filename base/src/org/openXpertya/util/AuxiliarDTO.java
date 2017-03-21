package org.openXpertya.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.cc.CurrentAccountDocument;
import org.openXpertya.model.MCentralAux;
import org.openXpertya.model.MDocType;

/**
 * Clase definida y encargada de transportar la información local a la tabla
 * auxiliar remota
 * 
 * @author Equipo de Desarrollo Libertya.
 * 
 */
public class AuxiliarDTO implements Serializable, CurrentAccountDocument {

	// Constantes
	
	private static final long serialVersionUID = 1L;
	
	// Variables de instancia

	/** UID de la organización */
	
	private String orgUID;
	
	/** Nombre de la columna UID de la organización */
	
	private String orgUIDColumnName;
	
	/** UID de la Entidad Comercial */
	
	private String bpartnerUID;
	
	/** Nombre de la columna UID de la entidad comercial */
	
	private String bpartnerUIDColumnName;
	
	/** UID del documento */
	
	private String docUID;
	
	/** Nombre de la columna UID del documento */
	
	private String docUIDColumnName;
	
	/** ID del documento */
	
	private Integer docID;
	
	/** Tipo de Registro, se supone que tiene que ser si o si Online, pero luego analizo bien */
	
	private String registerType;
	
	/** Código de autorización */
	
	private String authCode;
	
	/** Fecha de la transacción */
	
	private Timestamp dateTrx;
	
	/** Fecha de Vencimiento */
	
	private Timestamp dueDate;
	
	/** Monto */
	
	private BigDecimal amt;
	
	/** Signo de la transacción */
	
	private Integer sign;
	
	/** Estado del documento */
	
	private String docStatus;
	
	/** Forma de Pago de la transacción (Para Facturas) */
	
	private String paymentRule;
	
	/** Tipo de Pago (para Payments) */
	
	private String tenderType;
	
	/** Flag de confirmación del registro */
	
	private boolean confirmed;
	
	/** Flag que determina si es un pago anticipado (para Payments) */
	
	private boolean prepayment;
	
	/** Flag de conciliado (para Cheques) */
	
	private boolean reconciled;
	
	/** Contexto */
	
	private Properties ctx;
	
	/** Nombre de la transacción */
	
	private String trxName;
	
	/** Tipo de transacción (Cliente o Proveedor) */
	
	private String transactionType;
	
	/**
	 * Tipo de Documento para identificar luego a qué tabla debo buscar la
	 * información replicada
	 */
	
	private String docType;
	
	/** Número de Documento */
	
	private String documentNo;
	
	/** Clave del tipo de documento */

	private String docTypeKey; 
	
	/** ID del tipo de documento */

	private Integer docTypeID;
	
	// Constructores
	
	public AuxiliarDTO(){}
	
	// Getters y Setters
	
	public void setOrgUID(String orgUID) {
		this.orgUID = orgUID;
	}

	public String getOrgUID() {
		return orgUID;
	}

	public void setBpartnerUID(String bpartnerUID) {
		this.bpartnerUID = bpartnerUID;
	}

	public String getBpartnerUID() {
		return bpartnerUID;
	}

	public void setDocUID(String docUID) {
		this.docUID = docUID;
	}

	public String getDocUID() {
		return docUID;
	}

	public void setDocID(Integer docID) {
		this.docID = docID;
	}

	public Integer getDocID() {
		return docID;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setDateTrx(Timestamp dateTrx) {
		this.dateTrx = dateTrx;
	}

	public Timestamp getDateTrx() {
		return dateTrx;
	}

	public void setDueDate(Timestamp dueDate) {
		this.dueDate = dueDate;
	}

	public Timestamp getDueDate() {
		return dueDate;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setSign(Integer sign) {
		this.sign = sign;
	}

	public Integer getSign() {
		return sign;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}

	public String getPaymentRule() {
		return paymentRule;
	}

	public void setTenderType(String tenderType) {
		this.tenderType = tenderType;
	}

	public String getTenderType() {
		return tenderType;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
	}

	public boolean isPrepayment() {
		return prepayment;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setOrgUIDColumnName(String orgUIDColumnName) {
		this.orgUIDColumnName = orgUIDColumnName;
	}

	public String getOrgUIDColumnName() {
		return orgUIDColumnName;
	}

	public void setBpartnerUIDColumnName(String bpartnerUIDColumnName) {
		this.bpartnerUIDColumnName = bpartnerUIDColumnName;
	}

	public String getBpartnerUIDColumnName() {
		return bpartnerUIDColumnName;
	}

	public void setDocUIDColumnName(String docUIDColumnName) {
		this.docUIDColumnName = docUIDColumnName;
	}

	public String getDocUIDColumnName() {
		return docUIDColumnName;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setReconciled(boolean reconciled) {
		this.reconciled = reconciled;
	}

	public boolean isReconciled() {
		return reconciled;
	}

	public void setDocTypeKey(String docTypeKey) {
		this.docTypeKey = docTypeKey;
	}

	public String getDocTypeKey() {
		return docTypeKey;
	}

	public void setDocTypeID(Integer docTypeID) {
		this.docTypeID = docTypeID;
	}

	public Integer getDocTypeID() {
		return docTypeID;
	}

	@Override
	public boolean isSOTrx() {
		return MCentralAux.TRANSACTIONTYPE_Customer.equals(getTransactionType());
	}

	@Override
	public boolean isSkipCurrentAccount() {
		MDocType dt = MDocType.get(getCtx(), getDocTypeID());
		return dt != null && dt.isSkipCurrentAccounts();
	}
}
