package org.openXpertya.print.fiscal;

public class FiscalPrinterLogRecord {

	/** Comando */
	private String command;
	
	/** Respuesta */
	private String response;
	
	
	public FiscalPrinterLogRecord() {
		
	}

	public FiscalPrinterLogRecord(String command) {
		setCommand(command);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
}
