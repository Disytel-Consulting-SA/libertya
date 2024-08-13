package org.openXpertya.print.fiscal.action;

import java.sql.Timestamp;
import java.util.Calendar;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class FiscalReportAuditAction extends FiscalPrinterAction {

	// Variables de instancia
	
	/** Impresora fiscal */
	
	private Integer controladorFiscalID;
	
	/** Fecha Desde */
	private Timestamp fechaDesde = null;
	
	/** Fecha Hasta */
	private Timestamp fechaHasta = null;
	
	/** Informe completo */
	private boolean completo = false;
	
	// Mensajes
	private String MSG_FISCAL_AUDIT_ERROR = Msg.getMsg(Env.getCtx(), "FiscalAuditError");
	private String MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
	private String MSG_FISCAL_AUDIT_ERROR_MANDATORY_DATA = Msg.getMsg(Env.getCtx(), "MandatoryDataFiscalAuditError");
	
	// Constructores
	
	public FiscalReportAuditAction(FiscalDocumentPrint fdp, String trxName){
		super(fdp, trxName);
	}
	
	public FiscalReportAuditAction(FiscalDocumentPrint fdp, String trxName, Timestamp fechaDesde, Timestamp fechaHasta, Boolean completo, Integer controladorFiscalID){
		this(fdp, trxName);
		setFechaDesde(fechaDesde);
		setFechaHasta(fechaHasta);
		setControladorFiscalID(controladorFiscalID);
		setCompleto(completo);
	}
	
	@Override
	public boolean execute() {
		// Validaciones de datos obligatorios
		StringBuffer mandatoryMsg = new StringBuffer();
		// Tipo de cierre fiscal
		if(getControladorFiscalID() == null || getControladorFiscalID() == 0){
			mandatoryMsg.append(MSG_FISCAL_CONTROLLER);
			mandatoryMsg.append(" , ");
		}

		if(fechaDesde==null) {
			fechaDesde = Env.getDate(Env.getCtx());
			 Calendar calendar = Calendar.getInstance();
		     calendar.setTimeInMillis(fechaDesde.getTime());
		     calendar.add(Calendar.DAY_OF_MONTH, -7);
		     fechaDesde = new Timestamp(calendar.getTimeInMillis());
		}
		if(fechaHasta==null)
			fechaHasta = Env.getDate(Env.getCtx());
		
		// Si mensaje de error tiene algo, entonces muestro error
		if(mandatoryMsg.length() > 0){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(MSG_FISCAL_AUDIT_ERROR_MANDATORY_DATA).append("\n");
			errorMsg.append(mandatoryMsg.substring(0, mandatoryMsg.lastIndexOf(",")-1));
			setErrorMsg(MSG_FISCAL_AUDIT_ERROR);
			setErrorDesc(errorMsg.toString());
			return false;
		}

		// Obtener info de auditoria
		Boolean res = false;
		try {
			res = getFdp().fiscalReportAudit(getControladorFiscalID(), fechaDesde, fechaHasta, completo);
		} catch (Exception e) {
			System.out.println("Dio error al obtener auditoria: " + e.toString());
		}
		// Guardo el mensaje que indica el nombre del archivo generado y la ruta correspondiente
		setErrorMsg(getFdp().getErrorMsg());
		
		if(!res) {
			return false;
		}else {
			
			// TODO: ver de que manera mostrar el resultado...
			System.out.println("Resultado de fiscalReportAudit= " + getFdp().getErrorMsg());
		}
		return true;
	}

	
	// Getters y Setters
	
	public void setControladorFiscalID(Integer controladorFiscalID) {
		this.controladorFiscalID = controladorFiscalID;
	}

	public Integer getControladorFiscalID() {
		return controladorFiscalID;
	}

	public Timestamp getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Timestamp fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Timestamp getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Timestamp fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	
	public boolean isCompleto() {
		return completo;
	}

	public void setCompleto(boolean completo) {
		this.completo = completo;
	}

}
