package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.Util;

public class RegisteredDocumentDTO {

	/** Contexto */
	private Properties ctx;
	
	/** Nombre de transacción */
	private String trxName;
	
	/** Fecha */
	private Date dateTrx;
	
	/** ID del POS */
	private Integer posID;
	
	/** Nombre del POS */
	private String posName;
	
	/** ID del Usuario */
	private Integer userID;
	
	/** Nombre del usuario */
	private String userName;
	
	/** Nombre del tipo de documento */
	private String docTypeName;
	
	/** Nro del documento */
	private String documentNo;
	
	/** ID del cliente */
	private Integer bpartnerID;
	
	/** Nombre del cliente */
	private String nombreCli;
	
	/** Ya impreso fiscalmente */
	private String fiscalAlreadyPrinted;
	
	/** Total del documento */
	private BigDecimal grandTotal;
	
	/** Estado del Documento */
	private String docStatus;
	
	/** Es faltante? */
	private Boolean isMissing;
	
	public RegisteredDocumentDTO(Properties ctx, Date dateTrx, Integer posID,
			String posName, Integer userID, String userName,
			String docTypeName, String documentNo, Integer bpartnerID,
			String nombreCli, String fiscalAlreadyPrinted,
			BigDecimal grandTotal, String docStatus, Boolean isMissing,
			String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		setDateTrx(dateTrx);
		setPosID(posID);
		setPosName(posName);
		setUserID(userID);
		setUserName(userName);
		setDocTypeName(docTypeName);
		setDocumentNo(documentNo);
		setBpartnerID(bpartnerID);
		setNombreCli(nombreCli);
		setFiscalAlreadyPrinted(fiscalAlreadyPrinted);
		setGrandTotal(grandTotal);
		setDocStatus(docStatus);
		setMissing(isMissing);
	}

	public Date getDateTrx() {
		return dateTrx;
	}

	public void setDateTrx(Date dateTrx) {
		this.dateTrx = dateTrx;
	}

	public Integer getPosID() {
		return posID;
	}

	public void setPosID(Integer posID) {
		this.posID = posID;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public Integer getBpartnerID() {
		return bpartnerID;
	}

	public void setBpartnerID(Integer bpartnerID) {
		this.bpartnerID = bpartnerID;
	}

	public String getNombreCli() {
		return nombreCli;
	}

	public void setNombreCli(String nombreCli) {
		this.nombreCli = nombreCli;
	}

	public String getFiscalAlreadyPrinted() {
		return fiscalAlreadyPrinted;
	}

	public void setFiscalAlreadyPrinted(String fiscalAlreadyPrinted) {
		this.fiscalAlreadyPrinted = fiscalAlreadyPrinted;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		// Dependiendo del valor del estado, buscar la traducción
		String docStatusName = JasperReportsUtil.getListName(getCtx(),
				MInvoice.DOCSTATUS_AD_Reference_ID, docStatus);
		if(Util.isEmpty(docStatusName, true)){
			docStatusName = docStatus;
		}
		this.docStatus = docStatusName;
	}

	public Boolean isMissing() {
		return isMissing;
	}

	public void setMissing(Boolean isMissing) {
		this.isMissing = isMissing;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

}
