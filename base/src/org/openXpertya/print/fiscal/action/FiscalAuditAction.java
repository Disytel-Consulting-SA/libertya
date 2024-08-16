package org.openXpertya.print.fiscal.action;

import java.sql.Timestamp;
import java.util.Calendar;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class FiscalAuditAction extends FiscalPrinterAction {

	// Variables de instancia
	
	/** Impresora fiscal */
	
	private Integer controladorFiscalID;
	
	/** Fecha Desde */
	private Timestamp fechaDesde = null;
	
	/** Fecha Hasta */
	private Timestamp fechaHasta = null;
	
	// Mensajes

	private String MSG_FISCAL_AUDIT_ERROR = Msg.getMsg(Env.getCtx(), "FiscalAuditError");
	private String MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
	private String MSG_FISCAL_AUDIT_ERROR_MANDATORY_DATA = Msg.getMsg(Env.getCtx(), "MandatoryDataFiscalAuditError");
	
	// Constructores
	
	public FiscalAuditAction(FiscalDocumentPrint fdp, String trxName){
		super(fdp, trxName);
	}
	
	public FiscalAuditAction(FiscalDocumentPrint fdp, String trxName, Timestamp fechaDesde, Timestamp fechaHasta, Integer controladorFiscalID){
		this(fdp, trxName);
		setFechaDesde(fechaDesde);
		setFechaHasta(fechaHasta);
		setControladorFiscalID(controladorFiscalID);
	}
	
	@Override
	public boolean execute() {
		// Validaciones de datos obligatorios
		StringBuffer mandatoryMsg = new StringBuffer();
		
		/**
		 * Hardcoded para probar rapido
		 
		xmlParser parser = new xmlParser("/home/jorge/Auditoria_Hasar_556_230629_230629.xml");
		String x = parser.parsear();
		return true;
		*/
		
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
			res = getFdp().fiscalAudit(getControladorFiscalID(), fechaDesde, fechaHasta);
		} catch (Exception e) {
			System.out.println("Dio error al obtener auditoria: " + e.toString());
		}
		// Guardo el mensaje que indica el nombre del archivo generado y la ruta correspondiente
		setErrorMsg(getFdp().getErrorMsg());
		
		if(!res) {
			return false;
		}else {
			
			// TODO: ver de que manera mostrar el resultado...
			System.out.println("Resultado de fiscalAudit= " + getFdp().getErrorMsg());
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

}
