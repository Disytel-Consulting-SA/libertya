package org.openXpertya.print.fiscal.exception;

import java.util.List;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.msg.FiscalMessage;
import org.openXpertya.print.fiscal.msg.FiscalMessages;

/**
 * Existe un error en el estado reportado por la impresora fiscal.
 * @author Franco Bonafine
 * @date 06/02/2008
 */
public class FiscalPrinterStatusError extends FiscalPrinterIOException {

	private FiscalMessages statusMsgs = new FiscalMessages();
	
	public FiscalPrinterStatusError() {
		super();
	}

	/**
	 * @param request Petición ejecutada.
	 * @param response Respuesta recibida.
	 * @param statusMsgs Mensajes de estado devueltos por la impresora.
	 */
	public FiscalPrinterStatusError(FiscalPacket request, FiscalPacket response, FiscalMessages statusMsgs ) {
		super(buildErrorMsg(statusMsgs),request, response);
		this.statusMsgs = statusMsgs;
	}

	/**
	 * @return Returns the statusMsgs.
	 */
	public FiscalMessages getStatusMsgs() {
		return statusMsgs;
	}

	/**
	 * @return Retorna los mensajes de error contenidos en los mensajes de estado.
	 */
	public List<FiscalMessage> getErrorMsgs() {
		return getStatusMsgs().getErrorMsgs();
	}
	
	/**
	 * @return Retorna el mensaje de error que proviene del dispositivo.
	 * En caso de que el dispositivo no haya enviado mensaje alguno, retorna
	 * un <code>String</code> vacio <code>""</code>
	 */
	public String getDeviceErrorMsg() {
		String msg = "";
		try {
			if(getResponsePacket().getSize() >= 3)
				msg = getResponsePacket().getString(3) + ".";
		} catch (Exception e) {
			msg = "";
		}
		return msg;
	}
	
	private static String buildErrorMsg(FiscalMessages msgs) {
		String errorMsg = "";
		for (FiscalMessage msg : msgs.getErrorMsgs()) {
			errorMsg += msg; 
		}
		return errorMsg;
	}
}
