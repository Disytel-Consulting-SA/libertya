package org.openXpertya.print.fiscal.action;

import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MControladorFiscal;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Setea el estado del controlador fiscal
 * dREHER
 */
public class FiscalStatusAction extends FiscalPrinterAction {

	// Variables de instancia
	
	/** Impresora fiscal */
	
	private Integer controladorFiscalID;
	private MControladorFiscal fiscal;
	


	/** Estado del controlador */
	
	private String status;
	
	// Mensajes

	private String MSG_FISCAL_STATUS_ERROR = Msg.getMsg(Env.getCtx(), "FiscalStatusError");
	private String MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
	private String MSG_FISCAL_STATUS_ERROR_MANDATORY_DATA = Msg.getMsg(Env.getCtx(), "MandatoryDataFiscalStatusError");
	
	// Constructores
	
	public FiscalStatusAction(FiscalDocumentPrint fdp, String trxName){
		super(fdp, trxName);
	}
	
	public FiscalStatusAction(FiscalDocumentPrint fdp, String trxName, Integer controladorFiscalID){
		this(fdp, trxName);
		setControladorFiscalID(controladorFiscalID);
		setFiscal(new MControladorFiscal(Env.getCtx(), controladorFiscalID, null));
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

		// Si mensaje de error tiene algo, entonces muestro error
		if(mandatoryMsg.length() > 0){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(MSG_FISCAL_STATUS_ERROR_MANDATORY_DATA).append("\n");
			errorMsg.append(mandatoryMsg.substring(0, mandatoryMsg.lastIndexOf(",")-1));
			setErrorMsg(MSG_FISCAL_STATUS_ERROR);
			setErrorDesc(errorMsg.toString());
			return false;
		}

		// Obtener la info de inicialización de la impresora
		// Se comenta ya que devuelve error en todas las impresoras
		// getFdp().getInitData(getControladorFiscalID());
		
		// Status fiscal
		String status = MControladorFiscal.STATUS_ERROR;
		try {
			getFdp().setIgnoreErrorStatus(true);
			status = getFdp().getAndSaveStatus(getFiscal());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setStatus(status);
		
		if(status.equals(MControladorFiscal.STATUS_ERROR) || status.equals(MControladorFiscal.STATUS_BUSY)) {
			setErrorMsg("Controlador Fiscal con Error u Ocupado!");
			return false;
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

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public MControladorFiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(MControladorFiscal fiscal) {
		this.fiscal = fiscal;
	}

}
