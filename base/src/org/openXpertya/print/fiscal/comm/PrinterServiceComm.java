package org.openXpertya.print.fiscal.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.SimpleDoc;

import org.openXpertya.print.CPrinter;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.msg.MsgRepository;

public class PrinterServiceComm extends AbstractFiscalComm {

	/** Nombre de la impresora dentro de las impresoras habilitadas */
	private String printerName = null;
	
	/** Job que luego será impreso */
	private DocPrintJob pj = null;
	
	public PrinterServiceComm(String printerName) {
		setPrinterName(printerName);
	}

	@Override
	public void connect() throws IOException {
		// La impresora existe y se puede conectar
		if(!CPrinter.existsPrinterName(getPrinterName())) {
			throw new IOException("Impresora "+getPrinterName()+" no encontrada ");
		}
		// Trabajo de Impresión
		pj = CPrinter.getPrinterJob(getPrinterName()).getPrintService().createPrintJob();
		setOutputStream(new ByteArrayOutputStream());
		setConnected(true);
	}
	
	public synchronized void execute(FiscalPacket request, FiscalPacket response) throws IOException {
		if(request == response) throw new IllegalArgumentException();
		if(request == null) throw new NullPointerException(MsgRepository.get("NullRequestError"));
		if(response == null) throw new NullPointerException(MsgRepository.get("NullResponseError"));
		// Se valida el estado de la conexión con el spooler.
		validateConnection();
		
		// Se obtiene la representación en bytes del comando y se escribe
		// sobre stream de salida.
		byte[] cmdBytes = request.encodeBytes();
		getOutputStream().write(cmdBytes);
	}
	
	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	@Override
	public void finishPrint() throws Exception {
		// Se imprime y luego cierra todo
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(((ByteArrayOutputStream)getOutputStream()).toByteArray(), flavor, null);
        pj.print(doc, null);
	}

}
