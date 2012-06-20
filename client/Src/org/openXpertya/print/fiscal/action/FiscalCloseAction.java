package org.openXpertya.print.fiscal.action;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class FiscalCloseAction extends FiscalPrinterAction {

	// Variables de instancia
	
	/** Impresora fiscal */
	
	private Integer controladorFiscalID;
	
	/** Tipo de cierre */
	
	private String closetype;
	
	// Mensajes

	private String MSG_FISCAL_CLOSE_ERROR = Msg.getMsg(Env.getCtx(), "FiscalCloseError");
	private String MSG_FISCAL_CLOSE_TYPE = Msg.getMsg(Env.getCtx(), "FiscalCloseType");
	private String MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
	private String MSG_FISCAL_CLOSE_ERROR_MANDATORY_DATA = Msg.getMsg(Env.getCtx(), "MandatoryDataFiscalCloseError");
	
	// Constructores
	
	public FiscalCloseAction(FiscalDocumentPrint fdp, String trxName){
		super(fdp, trxName);
	}
	
	public FiscalCloseAction(FiscalDocumentPrint fdp, String trxName, String closeType, Integer controladorFiscalID){
		this(fdp, trxName);
		setClosetype(closeType);
		setControladorFiscalID(controladorFiscalID);
	}
	
	@Override
	public boolean execute() {
		// Validaciones de datos obligatorios
		StringBuffer mandatoryMsg = new StringBuffer();
		// Tipo de cierre fiscal
		if(getControladorFiscalID() == null || getControladorFiscalID() == 0){
			mandatoryMsg.append(MSG_FISCAL_CLOSE_TYPE);
			mandatoryMsg.append(" , ");
		}
		// Controlador fiscal
		if(getClosetype() == null || getClosetype().length() == 0){
			mandatoryMsg.append(MSG_FISCAL_CONTROLLER);
			mandatoryMsg.append(" , ");
		}
		// Si mensaje de error tiene algo, entonces muestro error
		if(mandatoryMsg.length() > 0){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(MSG_FISCAL_CLOSE_ERROR_MANDATORY_DATA).append("\n");
			errorMsg.append(mandatoryMsg.substring(0, mandatoryMsg.lastIndexOf(",")-1));
			setErrorMsg(MSG_FISCAL_CLOSE_ERROR);
			setErrorDesc(errorMsg.toString());
			return false;
		}

		if(!getFdp().fiscalClose(getControladorFiscalID(), getClosetype())) {
			setErrorMsg(getFdp().getErrorMsg());
			return false;
		}
		return false;
	}

	
	// Getters y Setters
	
	public void setControladorFiscalID(Integer controladorFiscalID) {
		this.controladorFiscalID = controladorFiscalID;
	}

	public Integer getControladorFiscalID() {
		return controladorFiscalID;
	}

	public void setClosetype(String closetype) {
		this.closetype = closetype;
	}

	public String getClosetype() {
		return closetype;
	}

}
