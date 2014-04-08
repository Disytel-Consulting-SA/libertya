package org.openXpertya.print.fiscal;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFiscalPrinterLogger {
	
	/** Log actual */
	private FiscalPrinterLogRecord actualControladorFiscalLog = null;
	
	/** Lote de comandos ejecutados */
	private List<FiscalPrinterLogRecord> fiscalLogRecords = null;
	
	public AbstractFiscalPrinterLogger() {
		setFiscalLogRecords(new ArrayList<FiscalPrinterLogRecord>());
	}

	public FiscalPrinterLogRecord createLog(FiscalPacket command){
		if(getActualControladorFiscalLog() == null){
			FiscalPrinterLogRecord log = new FiscalPrinterLogRecord(command.toString());
			setActualControladorFiscalLog(log);
		}
		
		return getActualControladorFiscalLog();
	}
	
	public void setLogResponse(String response, boolean addToBatch, boolean clearActualLogRecord){
		if(getActualControladorFiscalLog() != null){
			getActualControladorFiscalLog().setResponse(response);
			if(addToBatch)getFiscalLogRecords().add(getActualControladorFiscalLog());
			if(clearActualLogRecord)clear();
		}
	}
	
	public void clear(){
		setActualControladorFiscalLog(null);
	}
	
	public void clearBatchLog(){
		setFiscalLogRecords(null);
		setFiscalLogRecords(new ArrayList<FiscalPrinterLogRecord>());
	}
	
	public boolean canSaveRecord(boolean isError){
		return true;
	}

	protected FiscalPrinterLogRecord getActualControladorFiscalLog() {
		return actualControladorFiscalLog;
	}

	protected void setActualControladorFiscalLog(
			FiscalPrinterLogRecord actualControladorFiscalLog) {
		this.actualControladorFiscalLog = actualControladorFiscalLog;
	}

	public List<FiscalPrinterLogRecord> getFiscalLogRecords() {
		return fiscalLogRecords;
	}

	protected void setFiscalLogRecords(List<FiscalPrinterLogRecord> fiscalLogRecords) {
		this.fiscalLogRecords = fiscalLogRecords;
	}
	
}
