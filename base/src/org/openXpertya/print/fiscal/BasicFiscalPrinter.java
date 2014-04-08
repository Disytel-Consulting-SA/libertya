package org.openXpertya.print.fiscal;

import java.io.IOException;
import java.math.BigDecimal;

import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.print.fiscal.msg.FiscalMessages;

/**
 * Contiene funcionalidad básica que facilitan la implementación de
 * controladores fiscales concretos. 
 * @author Franco Bonafine
 * @date 22/01/2008
 */
public abstract class BasicFiscalPrinter implements FiscalPrinter { 

	/** Interfaz de comunicación con el dispositivo */
	private FiscalComm fiscalComm;
	/** Mensajes reportados por el dispositivo a partir de la ejecución
	 * de un comando */
	private FiscalMessages messages;
	/** Manejador de eventos generados por la impresora fiscal */
	private FiscalPrinterEventListener eventListener;
	/** Número de comprobante ultimamente creado por la impresora */
	private String lastDocumentNo;
	/** Indica si se encuentra un documento abierto actualmente */
	private boolean documentOpened = false;
	/** Indica si se encuentra conectada la impresora */
	private boolean connected = false;
	/** Indica si es posible cancelar el documento actualmente en impresión */
	private boolean cancelAllowed = false;
	/** Indica si es posible preguntar en caso de error */
	private boolean askAllowed = false;
	/** Indica si es posible preguntar en este momento en caso de error */
	private boolean askMoment = false;
	/** Indica si la impresora se encuentra sin papel */
	private boolean withoutPaper = false;
	/** Ultimo comando enviado a la impresora fiscal */
	private FiscalPacket lastRequest;
	/** Ultima respuesta recibida desde la impresora fiscal */
	private FiscalPacket lastResponse;
	/** Enviar comando de cancelación antes de imprimir un documento */
	private boolean cancelBeforePrint = false;
	/** Preguntar cuando hay un error */
	private boolean askWhenError = false;
	/** Tipo de log a guardar */
	private AbstractFiscalPrinterLogger fiscalPrinterLogger;
	
	public BasicFiscalPrinter() {
		super();
	}
	
	/**
	 * @param fiscalComm Interfaz de comunicación con el dispositivo.
	 */
	public BasicFiscalPrinter(FiscalComm fiscalComm) {
		super();
		this.fiscalComm = fiscalComm;
		this.messages = new FiscalMessages();
	}

	/**
	 * Crea un paquete fiscal que representa un comando de petición a la
	 * impresora fiscal.
	 * @param commandCode: código del comando.
	 * @return <code>FiscalPacket</code> que representa el comando.
	 */
	protected FiscalPacket createFiscalPacket(int commandCode) {
		FiscalPacket packet = createFiscalPacket();
		packet.setCommandCode(commandCode);
		return packet;
	}

	/**
	 * Crea un paquete fiscal para ser interpretado por esta impresora.
	 * @return <code>FiscalPacket</code> para la impresora.
	 */
	protected abstract FiscalPacket createFiscalPacket();
	
	public String formatText(String text, int maxLength) {
		if(text != null && text.length() > maxLength)
			text = text.substring(0, maxLength);
		return text;
	}
	
	public String formatNumber(BigDecimal number, int integerPart, int decimalPart) {
		String num = number.toString();
		int pointIndex = num.indexOf('.');
		if(pointIndex == -1) {
			num = number + ".";
			for(int i = 1; i <= decimalPart; num += "0", i++);
		} else if (num.substring(pointIndex + 1).length() > decimalPart)
			num = num.substring(0, num.indexOf('.') + decimalPart);
		return num;
	}

	/**
	 * @return Returns the fiscalComm.
	 */
	public FiscalComm getFiscalComm() {
		return fiscalComm;
	}

	/**
	 * @param fiscalComm The fiscalComm to set.
	 */
	public void setFiscalComm(FiscalComm fiscalComm) {
		this.fiscalComm = fiscalComm;
	}

	public FiscalMessages getMessages() {
		return messages;
	}
	
	protected void setMessages(FiscalMessages messages) {
		this.messages = messages;
	}

	/**
	 * @return Returns the eventListener.
	 */
	public FiscalPrinterEventListener getEventListener() {
		return eventListener;
	}

	/**
	 * @param eventListener The eventListener to set.
	 */
	public void setEventListener(FiscalPrinterEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	/**
	 * Se ha ejecutado un comando. Se informa al EventListener (en caso de que
	 * exista) del comando ejecutado.
	 * @param command Comando ejecutado.
	 * @param response Respuesta recibida.
	 */
	protected void fireCommandExecuted(FiscalPacket command, FiscalPacket response) {
		if(getEventListener() != null)
			getEventListener().commandExecuted(this, command, response);
	}

	/**
	 * Se ha producido un cambio en el estado de la impresora. Se informa al 
	 * EventListener (en caso de que exista) de los cambios producidos .
	 * @param command Comando ejecutado.
	 * @param response Respuesta recibida.
	 * @param msgs Mensajes de la impresora (contiene los errores en caso de 
	 * que se haya producido alguno).
	 */
	protected void fireStatusChanged(FiscalPacket command, FiscalPacket response, FiscalMessages msgs) {
		if(getEventListener() != null)
			getEventListener().statusChanged(this, command, response, msgs);
	}
	
	/**
	 * Se ha ejecutado un comando. Se informa al EventListener (en caso de que
	 * exista) del comando ejecutado.
	 * @param command Comando ejecutado.
	 * @param response Respuesta recibida.
	 */
	protected void fireStatusChanged(FiscalPacket command, FiscalPacket response) {
		fireStatusChanged(command, response, getMessages());
	}
	
	/**
	 * Se ha finalizado correctamente la impresión del documento. Se informa
	 * al EventListener (en caso de que exista).
	 * @param msgs Mensajes de estado de la impresora.
	 */
	protected void firePrintEnded(FiscalMessages msgs) {
		if(getEventListener() != null)
			getEventListener().printEnded(this, msgs);
	}

	/**
	 * Se ha finalizado correctamente la impresión del documento. Se informa
	 * al EventListener (en caso de que exista).
	 * @param msgs Mensajes de estado de la impresora.
	 */
	protected void firePrintEnded() {
		firePrintEnded(getMessages());
	}

	/**
	 * Se ha finalizado correctamente el cierre fiscal. Se informa al
	 * EventListener (en caso de que exista).
	 * 
	 * @param msgs
	 *            Mensajes de estado de la impresora.
	 */
	protected void fireFiscalCloseEnded(FiscalMessages msgs) {
		if(getEventListener() != null)
			getEventListener().fiscalCloseEnded(this, msgs);
	}
	
	protected void fireOpenDrawerEnded(FiscalMessages msgs){
		if(getEventListener() != null)
			getEventListener().openDrawerEnded(this, msgs);
	}
	
	/**
	 * Se ha finalizado correctamente el cierre fiscal. Se informa al
	 * EventListener (en caso de que exista).
	 */
	protected void fireFiscalCloseEnded() {
		fireFiscalCloseEnded(getMessages());
	}
	
	/**
	 * Se ha finalizado correctamente el apertura del cajón de dinero. Se
	 * informa al EventListener (en caso de que exista).
	 */
	protected void fireOpenDrawerEnded(){
		fireOpenDrawerEnded(getMessages());
	}
	
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
	public abstract FiscalPacket executeCmd(FiscalPacket command)
			throws FiscalPrinterIOException, FiscalPrinterStatusError;
	
	@Override
	public FiscalPacket execute(FiscalPacket command) throws FiscalPrinterIOException, FiscalPrinterStatusError {
		FiscalPacket response = null;
		String responseStr = null;
		
		// Log 
		createLog(command);
		
		try{
			// Ejecutar comando
			response = executeCmd(command);
			
			// Respuesta en string
			responseStr = response.toString();
			
		} catch (FiscalPrinterStatusError fpse){
			responseStr = fpse.getFullMessage();
			throw fpse;
		} catch (FiscalPrinterIOException fpioe){
			responseStr = fpioe.getFullMessage();
			throw fpioe;
		} finally{
			setLogResponse(responseStr, true, true);
		}
		
		return response;
	}
	
	protected boolean manageLog(){
		return getFiscalPrinterLogger() != null;
	}
	
	protected void createLog(FiscalPacket command){
		if(manageLog()){
			getFiscalPrinterLogger().createLog(command);
		}
	}
	
	protected void setLogResponse(String response, boolean addToBatch, boolean clearActualLogRecord){
		if(manageLog()){
			getFiscalPrinterLogger().setLogResponse(response, addToBatch,
					clearActualLogRecord);
		}
	}
	
	protected void clearLog(){
		if(manageLog()){
			getFiscalPrinterLogger().clear();
		}
	}
	
	public boolean canSaveRecord(boolean isError){
		return manageLog() && getFiscalPrinterLogger().canSaveRecord(isError);
	}
	
	public String getLastDocumentNo() {
		return lastDocumentNo;
	}

	/**
	 * @param lastDocumentNo The lastDocumentNo to set.
	 */
	protected void setLastDocumentNo(String lastDocumentNo) {
		this.lastDocumentNo = lastDocumentNo;
	}

	public void connect() throws IOException {
		getFiscalComm().connect();
		setConnected(getFiscalComm().isConnected());
	}

	public void close() throws IOException {
		getFiscalComm().close();
		setConnected(false);
	}

	/**
	 * @return Returns the documentOpened.
	 */
	public boolean hasDocumentOpened() {
		return documentOpened;
	}

	/**
	 * @param documentOpened The documentOpened to set.
	 */
	protected void setDocumentOpened(boolean documentOpened) {
		this.documentOpened = documentOpened;
	}

	/**
	 * @return Returns the connected.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected The connected to set.
	 */
	protected void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * @return Returns the cancelAllowed.
	 */
	protected boolean isCancelAllowed() {
		return cancelAllowed;
	}

	/**
	 * @param cancelAllowed The cancelAllowed to set.
	 */
	protected void setCancelAllowed(boolean cancelAllowed) {
		this.cancelAllowed = cancelAllowed;
	}

	/**
	 * @return Returns the withoutPaper.
	 */
	public boolean isWithoutPaper() {
		return withoutPaper;
	}

	/**
	 * @param withoutPaper The withoutPaper to set.
	 */
	protected void setWithoutPaper(boolean withoutPaper) {
		this.withoutPaper = withoutPaper;
	}

	/**
	 * @return Returns the lastRequest.
	 */
	protected FiscalPacket getLastRequest() {
		return lastRequest;
	}

	/**
	 * @param lastRequest The lastRequest to set.
	 */
	protected void setLastRequest(FiscalPacket lastRequest) {
		this.lastRequest = lastRequest;
	}

	/**
	 * @return Returns the lastResponse.
	 */
	protected FiscalPacket getLastResponse() {
		return lastResponse;
	}

	/**
	 * @param lastResponse The lastResponse to set.
	 */
	protected void setLastResponse(FiscalPacket lastResponse) {
		this.lastResponse = lastResponse;
	}
	
	@Override
	public void setCancelBeforePrint(boolean cancelBeforePrint){
		this.cancelBeforePrint = cancelBeforePrint;
	}
	
	@Override
	public boolean isCancelBeforePrint(){
		return cancelBeforePrint;
	}
	
	@Override
	public boolean isDocumentPrintAsk(){
		return isAskAllowed() && isAskWhenError() && isAskMoment();
	}

	public boolean isAskAllowed() {
		return askAllowed;
	}

	public void setAskAllowed(boolean askAllowed) {
		this.askAllowed = askAllowed;
	}

	public boolean isAskMoment() {
		return askMoment;
	}

	public void setAskMoment(boolean askMoment) {
		this.askMoment = askMoment;
	}

	public boolean isAskWhenError() {
		return askWhenError;
	}

	@Override
	public void setAskWhenError(boolean askWhenError) {
		this.askWhenError = askWhenError;
	}

	@Override
	public AbstractFiscalPrinterLogger getFiscalPrinterLogger() {
		return fiscalPrinterLogger;
	}

	@Override
	public void setFiscalPrinterLogger(AbstractFiscalPrinterLogger fiscalPrinterLogger) {
		this.fiscalPrinterLogger = fiscalPrinterLogger;
	}
}
