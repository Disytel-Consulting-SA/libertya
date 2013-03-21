package org.openXpertya.print.fiscal.action;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class OpenDrawerAction extends FiscalPrinterAction {

	/** Impresora fiscal */
	
	private Integer controladorFiscalID;
	
	private String MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
	private String MSG_OPEN_DRAWER_ERROR_MANDATORY_DATA = Msg.getMsg(Env.getCtx(), "MandatoryDataFiscalCloseError");
	private String MSG_OPEN_DRAWER_ERROR = Msg.getMsg(Env.getCtx(), "OpenDrawerError");
	
	public OpenDrawerAction(FiscalDocumentPrint fdp, String trxName) {
		super(fdp, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public OpenDrawerAction(FiscalDocumentPrint fdp, String trxName, Integer controladorFiscalID){
		this(fdp, trxName);
		setControladorFiscalID(controladorFiscalID);
	}
	

	@Override
	public boolean execute() {
		StringBuffer mandatoryMsg = new StringBuffer();
		if(getControladorFiscalID() == null || getControladorFiscalID() == 0){
			mandatoryMsg.append(MSG_FISCAL_CONTROLLER);
			mandatoryMsg.append(" , ");
		}
		// Si mensaje de error tiene algo, entonces muestro error
		if(mandatoryMsg.length() > 0){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(MSG_OPEN_DRAWER_ERROR_MANDATORY_DATA).append("\n");
			errorMsg.append(mandatoryMsg.substring(0, mandatoryMsg.lastIndexOf(",")-1));
			setErrorMsg(MSG_OPEN_DRAWER_ERROR);
			setErrorDesc(errorMsg.toString());
			return false;
		}
		
		if(!getFdp().openDrawer(getControladorFiscalID())) {
			setErrorMsg(getFdp().getErrorMsg());
			return false;
		}
		return true;
	}

	protected Integer getControladorFiscalID() {
		return controladorFiscalID;
	}

	protected void setControladorFiscalID(Integer controladorFiscalID) {
		this.controladorFiscalID = controladorFiscalID;
	}

}
