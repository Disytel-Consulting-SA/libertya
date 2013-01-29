package org.openXpertya.print.fiscal;

import org.openXpertya.print.fiscal.msg.FiscalMessages;

/**
 * Manejador de eventos que pueden ser lanzados por una impresora fiscal
 * @author Franco Bonafine
 * @date 06/02/2008
 */
public interface FiscalPrinterEventListener {

	/**
	 * Es invocado cuando se ha ejecutado satisfactoriamente un comando
	 * en la impresora fiscal.
	 * @param source Impresora fiscal en la que se ejecutó el comando.
	 * @param command Comando ejecutado.
	 * @param response Respuesta recibida desde la impresora.
	 */
	public void commandExecuted(FiscalPrinter source, FiscalPacket command, FiscalPacket response);
	
	/**
	 * Es invocado cuando se produce un cambio de estado en la 
	 * impresora fiscal. 
	 * @param source Impresora fiscal en la que se ejecutó el comando.
	 * @param command Comando ejecutado.
	 * @param response Respuesta recibida desde la impresora.
	 * @param msgs Mensajes de estado de la impresora.
	 */
	public void statusChanged(FiscalPrinter source, FiscalPacket command, FiscalPacket response, FiscalMessages msgs);
	
	/**
	 * Es invoicado cuando se finaliza correctamente la impresión 
	 * del documento.
	 * @param source Impresora fiscal que realizó la impresión.
	 * @param msgs Mensajes de estado de la impresora fiscal.
	 */
	public void printEnded(FiscalPrinter source, FiscalMessages msgs);

	/**
	 * Es invoicado cuando se finaliza correctamente el cierre fiscal
	 * 
	 * @param source
	 *            Impresora fiscal que realizó el cierre.
	 * @param msgs
	 *            Mensajes de estado de la impresora fiscal.
	 */
	public void fiscalCloseEnded(FiscalPrinter source, FiscalMessages msgs);
}
