package org.openXpertya.process;

import java.util.Properties;
import org.openXpertya.model.MInvoice;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.HTMLMsg.HTMLListHeader;
import org.openXpertya.util.Msg;

public class InvoiceFiscalPrint extends SvrProcess {

	/** ID de Factura a anular junto con todas sus relaciones creadas */
	private Integer invoiceID = 0;
	
	/** Factura a imprimir */
	private MInvoice invoice;
	
	/** Mensaje final de proceso */
	private HTMLMsg msg = new HTMLMsg();
	
	/** Transaccción para uso externo a SvrProcess (instanciación por constructor) */
	private String localTrxName = null;
	
	/** Contexto para uso externo a SvrProcess (instanciación por constructor) */
	private Properties localCtx = null;
	
	public InvoiceFiscalPrint() {
		super();
	}

	/**
	 * Constructor del proceso para imprimir factura por controlador fiscal
	 * 
	 * @param invoiceID
	 *            ID de factura a anular
	 * @param ctx
	 *            Contexto de la aplicación
	 * @param trxName
	 *            Nombre de transacción a utilizar.
	 */
	public InvoiceFiscalPrint(int invoiceID, Properties ctx, String trxName) {
		super();
		this.invoiceID = invoiceID;
		localTrxName = trxName;
		if (ctx == null) {
			ctx = Env.getCtx();
		}
		localCtx = ctx;
	}

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();
            if( name.equalsIgnoreCase( "C_Invoice_ID" )) {
            	invoiceID = para[ i ].getParameterAsInt();
            }
        }
        
        if(invoiceID <= 0)
        	invoiceID = getRecord_ID();
        
	}
	
	@Override
	protected String doIt() throws Exception {
		
		if(invoice==null)
			initialize();
		
		checkBeforeInvoice();

		CallResult rNo = getLastNoPrinted(invoice, true);
		String slastNo = rNo.getMsg();
		Integer lastNo = -1;
		if(!rNo.isError() && rNo.getMsg()!=null && !slastNo.isEmpty()) {
			try {
				lastNo = Integer.valueOf(slastNo.replace("#LastNoPrinted=", ""));
			}catch(Exception ex) {
				log.warning("No pudo leer el ultimo numero fiscal impreso! Error=" + ex.toString());
			}
		}else {
			if(rNo.isError())
				return "Se produjo un error al validar el ultimo numero impreso!";
		}
		
		// Si ya imprimio el mismo numero de comprobante o uno superior, NO volver a enviar impresion 
		// de este comprobante, solo asegurar que se registre la marca de impreso fiscal...
		if(lastNo >= invoice.getNumeroComprobante() ) {
			invoice.setFiscalAlreadyPrinted(true);
			invoice.save();
			log.info("El fiscal ya imprimio numeros iguales o posteriores al comprobante actual, solo marca el comprobante!");
		}else {		
			log.info("Ultimo numero impreso menor al comprobante actual, envia impresion fiscal...");
			printFiscalInvoice(invoice, true);
		}
		
		return getFinalMsg();
	}

	/**
	 * Inicializa la factura y sus relaciones, pedido, remito y allocations.
	 * 
	 * @throws Exception
	 *             en caso de error al inicializar
	 */
	protected void initialize() throws Exception{
		initInvoice();
		
		if(!invoice.requireFiscalPrint()) {
			throw new Exception("Este comprobante NO se emite a traves de controladores fiscales!");
		}
		
		if(invoice.isFiscalAlreadyPrinted()) {
			throw new Exception("Este comprobante YA fue impreso en un controlador fiscal!");
		}
	}
	
	private void checkBeforeInvoice() throws Exception {
		
		MInvoice before = invoice.getBeforeInvoice();
		if(before!=null)
			if(!before.isFiscalAlreadyPrinted())
				throw new Exception("Debe gestionar el comprobante anterior!");
		
	}

	/**
	 * Imprimir la factura parámetro
	 * 
	 * @param invoice
	 *            factura a imprimir
	 * @param makeMsg
	 *            true si se debe armar el mensaje con esta factura, false si no
	 *            hay que crear nada de mensaje por esta factura
	 * @throws Exception
	 *             en caso de error
	 */
	protected void printFiscalInvoice(MInvoice invoice, boolean makeMsg) throws Exception{

		CallResult printResult = new CallResult();
		
		// dREHER en este proceso NO intenta generar el CAE ni la impresion fiscal del comprobante Revertido...
		invoice.skipFiscalProcess = true;
		 		
		try {
			printResult = invoice.doFiscalPrint(makeMsg);
			if(printResult.isError()) {
				getMsg().setHeaderMsg(Msg.parseTranslation(getCtx(),
						printResult.getMsg()));
				log.warning("Dio error al imprimir en controlador fiscal..." + Msg.parseTranslation(getCtx(),
						printResult.getMsg()));
				return;
			}
			
		}catch(Exception ex) {
			log.warning("Error al imprimir en controlador fiscal: " + ex);
		}
		
		// Si debo armar el mensaje, lo armo para esta factura en particular
		if(makeMsg){
			// Armo el mensaje para esta factura
			HTMLList list = createHTMLList(null, Msg.translate(getCtx(),
					"C_Invoice_ID"));
			getMsg().createAndAddListElement(null, invoice.getDocumentNo(), list);
			getMsg().addList(list);
		}
	}	
	
	/**
	 * Devuelve el ultimo numero impreso en la fiscal para el tipo de factura parámetro
	 * 
	 * @param invoice
	 *            factura a chequear impresion
	 * @param makeMsg
	 *            true si se debe armar el mensaje con esta factura, false si no
	 *            hay que crear nada de mensaje por esta factura
	 * @throws Exception
	 *             en caso de error
	 */
	protected CallResult getLastNoPrinted(MInvoice invoice, boolean makeMsg) throws Exception{

		CallResult printResult = new CallResult();
		 		
		try {
			
			// dREHER en este proceso NO intenta generar el CAE ni la impresion fiscal del comprobante Revertido...
			invoice.skipFiscalProcess = true;
			
			printResult = invoice.getLastNoPrinted(makeMsg);
			if(printResult.isError()) {
				getMsg().setHeaderMsg(Msg.parseTranslation(getCtx(),
						printResult.getMsg()));
			}
			
		}catch(Exception ex) {
			log.warning("Error al leer en controlador fiscal: " + ex);
			printResult.setError(true);
		}
		
		return printResult;
	}	
	
	
	/**
	 * Inicializa la factura con la de la BD
	 */
	protected void initInvoice(){
		setInvoice(new MInvoice(getCtx(), invoiceID, get_TrxName()));
	}

	/**
	 * Crea una lista html para el mensaje final con un id parámetro
	 * 
	 * @param id
	 *            id de la lista
	 * @return nueva lista html
	 */
	protected HTMLList createHTMLList(String id, String msg){
		return getMsg().createList(id, HTMLListHeader.UL_LIST_TYPE, msg);
	}

	/**
	 * Recupero un mensaje de la BD a partir del ad_message
	 * 
	 * @param adMessage
	 *            clave de mensaje
	 * @return descripción del mensaje
	 */
	protected String getMsg(String adMessage){
		return Msg.getMsg(getCtx(), adMessage);
	}
	
	/**
	 * @return mensaje final del proceso
	 */
	protected String getFinalMsg(){

		if(getMsg().getHeaderMsg()!=null)
			return getMsg().getHeaderMsg();
			
		getMsg().setHeaderMsg(Msg.parseTranslation(getCtx(),
				"@ProcessOK@. @PrintedInvoice@"));
		
		return getMsg().toString();
	}
	
	protected void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	protected MInvoice getInvoice() {
		return invoice;
	}

	protected void setMsg(HTMLMsg msg) {
		this.msg = msg;
	}

	protected HTMLMsg getMsg(){
		return this.msg;
	}

	@Override
	protected String get_TrxName() {
		if (localTrxName != null) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	/**
	 * Comienza la ejecución del proceso.
	 * 
	 * @return Mesaje HTML con los documentos anulados
	 * @throws Exception
	 *             cuando se produce un error en la anulación de alguno de los
	 *             documentos.
	 */
	public String start() throws Exception {
		return doIt();
	}

}
