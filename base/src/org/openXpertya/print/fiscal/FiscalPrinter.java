package org.openXpertya.print.fiscal;

import java.io.IOException;
import java.math.BigDecimal;

import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.print.fiscal.document.NonFiscalDocument;
import org.openXpertya.print.fiscal.exception.DocumentException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.print.fiscal.msg.FiscalMessages;

/**
 * Impresora fiscal. Interfaz publica para la comunicación con impresoras
 * fiscales. Cada clase que represente una impresora fiscal debe impementar
 * esta interfaz.
 * @author Franco Bonafine
 * @date 22/01/2008
 */
public interface FiscalPrinter {

	/**
	 * Da formato de cantidad a un número.
	 * @param quantity: número a formatear.
	 * @return <code>String</code> que contiene la cantidad formateada.
	 */
	public String formatQuantity(BigDecimal quantity);
	
	/**
	 * Da formato de monto a un número.
	 * @param amount: número a formatear
	 * @return <code>String</code> que contiene el monto formateado.
	 */
	public String formatAmount(BigDecimal amount);
	
	/**
	 * Formatea un texto.
	 * @param text: texto a formatear.
	 * @param maxLength: longitud máxima para el texto.
	 * @return <code>String</code> que contiene el texto formateado.
	 */
	public String formatText(String text, int maxLength);
	
	/**
	 * Formatea un número.
	 * @param number: número a formatear.
	 * @param integerPart: cantidad de dígitos para la parte entera.
	 * @param decimalPart: cantidad de dígitos para la parte decimal.
	 * @return <code>String</code> con el número formateado.
	 */
	public String formatNumber(BigDecimal number, int integerPart, int decimalPart);
	
	
	/**
	 * @return La interfaz de comunicación con el dispositivo fiscal.
	 */
	public FiscalComm getFiscalComm();
	
	
	/**
	 * Asigna la interfaz de comunicación con el dispositivo fiscal.
	 * @param fiscalComm interfaz de comunicación.
	 */
	public void setFiscalComm(FiscalComm fiscalComm);
	
	/**
	 * @return Retorna el conjunto de mensajes recibido de la impresora fiscal
	 * luego de cada ejecución de algún comando a la misma. En caso de que no
	 * existan mensajes retorna un <code>FiscalMessages</code> donde 
	 * <code>isEmpty()</code> es verdadero.
	 */
	public FiscalMessages getMessages();

	/**
	 * Impresión de una factura. 
	 * @param invoice Factura a imprimir.
	 * @throws FiscalPrinterStatusError cuando la impresora fiscal 
	 * retorna un error en su estado a partir de la ejecución de algún comando.
	 * @throws FiscalPrinterIOException cuando se produce algún error 
	 * inesperado en la comunicación con el dispositivo fiscal. 
	 * @throws DocumentException cuando la factura contiene errores y no puede ser
	 * enviado a imprimir.
	 */
	public void printDocument(Invoice invoice) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException;
	
	/**
	 * Impresión de una nota de crédito.
	 * @param creditNote Nota de crédito a imprimir.
	 * @throws FiscalPrinterStatusError cuando la impresora fiscal 
	 * retorna un error en su estado a partir de la ejecución de algún comando.
	 * @throws FiscalPrinterIOException cuando se produce algún error 
	 * inesperado en la comunicación con el dispositivo fiscal. 
	 * @throws DocumentException cuando la factura contiene errores y no puede ser
	 * enviado a imprimir.
	 */
	public void printDocument(CreditNote creditNote) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException;
	
	/**
	 * Impresión de una nota de débito.
	 * @param debitNote Nota de débito a imprimir.
	 * @throws FiscalPrinterStatusError cuando la impresora fiscal 
	 * retorna un error en su estado a partir de la ejecución de algún comando.
	 * @throws FiscalPrinterIOException cuando se produce algún error 
	 * inesperado en la comunicación con el dispositivo fiscal.
	 * @throws DocumentException cuando la factura contiene errores y no puede ser
	 * enviado a imprimir.
	 */
	public void printDocument(DebitNote debitNote) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException;

	/**
	 * Impresión de un documento no fiscal
	 * @param nonFiscalDocument Documento no fiscal a imprimir
	 * @throws FiscalPrinterStatusError cuando la impresora fiscal 
	 * retorna un error en su estado a partir de la ejecución de algún comando.
	 * @throws FiscalPrinterIOException cuando se produce algún error 
	 * inesperado en la comunicación con el dispositivo fiscal.
	 * @throws DocumentException cuando el documento no fiscal contiene errores y no 
	 * puede ser enviado a imprimir.
	 */
	public void printDocument(NonFiscalDocument nonFiscalDocument) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException;
	
	/**
	 * Realiza el cierre fiscal.
	 * @param type tipo de cierre
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 */
	public void fiscalClose(String type) throws FiscalPrinterStatusError, FiscalPrinterIOException;
	
	/**
	 * @return Retorna el número del comprobante creado ultimamente por la
	 * impresora.
	 */
	public String getLastDocumentNo();
	
	/**
	 * @param documentType
	 *            tipo del documento a consultar
	 * @param letra
	 *            letra del comprobante a consultar
	 * @return Retorna el último número impreso por la fiscal del tipo de
	 *         comprobante y letras parámetro
	 */
	public String getLastDocumentNoPrinted(String documentType, String letra)
			throws FiscalPrinterStatusError, FiscalPrinterIOException;
	
	/**
	 * Conecta la impresora con el dispositivo fiscal. Aquí se debe efectivizar
	 * la conexión de la interfaz de comunicación con el dispositivo.
	 * Para efectuar cualquier operación con la impresora fiscal es necesario
	 * que primero se invoque a este método.
	 * @throws IOException Cuando se preduce algún error en el intento de conexión
	 * con el dispositivo.
	 */
	public void connect() throws IOException;
	
	/**
	 * Desconecta la impresora con el dispositivo fiscal. Luego de invocar este
	 * método no es posible realizar alguna operación con la impresora hasta que
	 * sea invocado nuevamente el método <code>FiscalPrinter.connect()</code>
	 * @throws IOException Cuando se produce algún error en el intento de 
	 * desconexión con el dispositivo.
	 */
	public void close() throws IOException;
	
	/**
	 * Indica si en la impresora fiscal se encuentra un documento abierto
	 * actualmente. 
	 * @return Verdadero en caso de que exista un documento abierto.
	 */
	public boolean hasDocumentOpened();
	
	/**
	 * @return Retorna el manejador de eventos de la impresora fiscal.
	 */
	public FiscalPrinterEventListener getEventListener();
		
	/**
	 * Asigna el manejador de eventos que dispara la impresora fiscal.
	 * @param eventListener Manejador de eventos a asignar.
	 */
	public void setEventListener(FiscalPrinterEventListener eventListener);
	
	public boolean isConnected();
	
	/**
	 * @return La cantidad de pagos permitidos para la impresión. Ciertas marcas
	 *         y modelos tienen una limitación en la cantidad de pagos que se
	 *         pueden imprimir con su descripción y su importe en el ticket o
	 *         factura.
	 */
	public int getAllowedPaymentQty();
	
	/**
	 * Abrir el cajón de dinero
	 */
	public void openDrawer() throws FiscalPrinterIOException;
	
	/**
	 * Setea el flag de cancelación antes de imprimir un documento
	 * 
	 * @param cancelBeforePrint
	 *            true si se debe enviar el comando de cancelación antes de
	 *            imprimir un documento, false caso contrario
	 */
	public void setCancelBeforePrint(boolean cancelBeforePrint);
	
	/**
	 * @return true si se debe enviar el comando de cancelación antes de
	 *         imprimir un documento, false caso contrario
	 */
	public boolean isCancelBeforePrint();
	
	/**
	 * @param askWhenError
	 *            true si se debe preguntar en caso de error
	 */
	public void setAskWhenError(boolean askWhenError);
	
	/**
	 * @return true si se debe preguntar en caso de error, false en caso
	 *         contrario
	 */
	public boolean isDocumentPrintAsk();
	
	/**
	 * Cancelación del documento
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	public void cancelCurrentDocument() throws FiscalPrinterIOException, FiscalPrinterStatusError;
	
	/**
	 * Ejecuta un comando fiscal en la impresora y analiza la existencia
	 * de errores en la respuesta. En caso de que se produzca algún error
	 * se propagan mediante excepciones.
	 * @param command Comando a ejecutar.
	 * @return Retorna un <code>FiscalPacket</code> que contiene la respuesta
	 * de la impresora.
	 * @throws FiscalPrinterIOException cuando se producce algún error de
	 * comunicación con el dispositivo.
	 * @throws FiscalPrinterStatusError cuando la impresora responde con un
	 * código de estado de error.
	 */
	public FiscalPacket execute(FiscalPacket command) throws FiscalPrinterIOException, FiscalPrinterStatusError;
	
	public void setFiscalPrinterLogger(AbstractFiscalPrinterLogger fiscalPrinterLogger);
	
	public AbstractFiscalPrinterLogger getFiscalPrinterLogger();
}
