package org.openXpertya.model;

import org.openXpertya.model.FiscalDocumentPrint.Actions;


/**
 * Información del estado del Impresor de Documentos 
 * fiscales (FiscalDocumentPrint).
 * @author Franco Bonafine
 * @date 22/02/2008
 */
public interface FiscalDocumentPrintListener {

	public static int AC_CHECK_STATUS = 1;
	public static int AC_CONNECT_PRINTER = 2;
	public static int AC_PRINT_DOCUMENT = 3;
	public static int AC_PROCESS_DOCUMENT = 4;
	public static int AC_EXECUTING_ACTION = 5;
	
	/**
	 * Informa el estado actual del controlador fiscal.
	 * @param source Impresor de documentos que generó la acción.
	 * @param cFiscal Controlador fiscal al que pertenece el estado.
	 * @param status Estado actual del controlador fiscal.
	 */
	public void statusReported(FiscalDocumentPrint source, MControladorFiscal cFiscal, String status);
	
	/**
	 * Informa que se ha comenzado a ejecutar una acción en el 
	 * impresor de documentos.
	 * @param source Impresor de documentos que generó la acción.
	 * @param action Acción que comenzo a ejecutarse.<br>
	 * <code>FiscalDocumentPrintListener.AC_CHECK_STATUS</code><br>
	 * <code>FiscalDocumentPrintListener.AC_CONNECT_PRINTER</code><br>
	 * <code>FiscalDocumentPrintListener.AC_PRINT_DOCUMENT</code><br>
	 * <code>FiscalDocumentPrintListener.AC_EXECUTING_ACTION</code><br>
	 */
	public void actionStarted(FiscalDocumentPrint source, int action);
	
	/**
	 * Informa que la impresión terminó a causa de un error. Se comunica
	 * el título y la descripción del error.
	 * @param source Impresor de documentos que terminó con error. 
	 * @param errorTitle Título del error.
	 * @param errorDesc Descripción del error.
	 */
	public void errorOcurred(FiscalDocumentPrint source, String errorTitle, String errorDesc);
	
	/**
	 * Informa que la impresión del documento terminó correctamente sin errores.
	 * @param source Impresor de documentos.
	 */
	public void documentPrintEndedOk(FiscalDocumentPrint source);
	
	/**
	 * Informa que la acción del documento terminó.
	 * @param ok true si no hubo error, false caso contrario
	 * @param action acción realizada
	 */
	public void actionEnded(boolean ok, Actions action);
	
	/**
	 * Asigna la referencia al impresor de documentos fiscales
	 * @param fdp
	 */
	public void setFiscalDocumentPrint(FiscalDocumentPrint fdp);
}
