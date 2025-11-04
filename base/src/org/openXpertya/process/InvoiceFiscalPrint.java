package org.openXpertya.process;

import java.util.Properties;

import org.openXpertya.model.FiscalDocumentPrintListener;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.HTMLMsg.HTMLListHeader;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Proceso que envia el comprobante a imprimir en impresora fiscal 
 * (se llama desde gestion de comprobantes de ventas)
 * 
 * @author dREHER
 */
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
	
	// dREHER Feb '25
	private final boolean FORZAR_RETIROPORALMACEN_JASPER = false;
	private FiscalDocumentPrintListener fiscalDocumentPrintListener;
	private FiscalPrinterEventListener fiscalPrinterEventListener;
	
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
				return "Error al validar el ultimo numero impreso. Verifique si realizo el cierre Z correspondiente.";
		}
		
		// Si ya imprimio el mismo numero de comprobante o uno superior, NO volver a enviar impresion 
		// de este comprobante, solo asegurar que se registre la marca de impreso fiscal...
		if(lastNo >= invoice.getNumeroComprobante() ) {
			invoice.setFiscalAlreadyPrinted(true);
			invoice.save();
			log.info("El fiscal ya imprimio numeros iguales o posteriores al comprobante actual, solo marca el comprobante! #: " + lastNo);
		}else {		
			log.info("Ultimo numero impreso menor al comprobante actual, envia impresion fiscal...");
			printFiscalInvoice(invoice, true);
			
			
			// dREHER Abr 25
			if(RequiereImpresionSalidaPorDeposito(invoice)) {
				log.info("Envia impresion de salida por deposito...");
				printWareHouseDeliveryTicket(invoice, true);
			}
		}
		
		return getFinalMsg();
	}

	/**
	 * 
	 * @param invoice2
	 * @return
	 * @author dREHER verifica si hay algun articulo del pedido que requiera impresion de salida por deposito
	 */
	private boolean RequiereImpresionSalidaPorDeposito(MInvoice mInvoice) {
		boolean isSalidDepo = false;
		
		if(!Util.isEmpty(mInvoice.getC_Order_ID(), true)){
			MOrder order = new MOrder(mInvoice.getCtx(),
					mInvoice.getC_Order_ID(), mInvoice.get_TrxName());
			int total_lines = order.getLines().length;
			
			boolean found = false;
			// Itero por todas las líneas del pedido y verifico si existe alguna
			// imprimible por retiro por depósito
			for (int i = 0; i < total_lines
					&& !(found = order.getLines()[i]
							.isDeliverDocumentPrintable()); i++);
			if(found){
				isSalidDepo = true;
				debug("Agregue foo de retiro por deposito...");
			}
			else{
				debug("Agregue foo - NO tiene retiro por deposito");
			}	
		}
		
		return isSalidDepo;
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
		
		// dREHER Mayo 25
		if(invoice.get_Value("ManageFiscalCancelInvoice")!=null && invoice.get_Value("ManageFiscalCancelInvoice").equals("Y"))
			throw new Exception("Ticket fiscal CANCELADO, debe gestionar CANCELACION de Ticket Fiscal!");
		
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
				
				if(printResult.getMsg().contains("CANCELO TICKET FISCAL")) {
					
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
				return;
			}
			
					debug("Detecto que se cancelo ticket fiscal, marcar gestion de cancelacion fiscal!");
					if(lastNo >= invoice.getNumeroComprobante())
						DB.executeUpdate("UPDATE C_Invoice SET ManageFiscalCancelInvoice='Y' WHERE C_Invoice_ID=" + invoice.getC_Invoice_ID(), null);
				}
				
				return;
			}
			
			// dREHER si no dio error al imprimir fiscal, debe guardar la marca de impresion fiscal
			if(!invoice.save())
				DB.executeUpdate("UPDATE C_Invoice SET FiscalAlreadyPrinted='Y', ManageFiscalCancelInvoice='N' WHERE C_Invoice_ID=" + invoice.getC_Invoice_ID(), null);
			
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
	 * Imprime el comprobante para retiro de artículos por almacén en caso de estar
	 * indicada esta opción en la configuración del TPV.
	 * @author dREHER 'Feb 25
	 */
	protected void printWareHouseDeliveryTicket(MInvoice invoice, boolean makeMsg)  throws Exception{

		CallResult printResult = new CallResult();

		// dREHER en este proceso NO intenta generar el CAE ni la impresion fiscal del comprobante Revertido...
		invoice.skipFiscalProcess = true;

		try {
			printResult = invoice.doFiscalWarehouseDeliveryPrint(makeMsg);
			debug("Volvio de impresion de salida por deposito:" + printResult);
			if(printResult.isError()) {
				getMsg().setHeaderMsg(Msg.parseTranslation(getCtx(),
						printResult.getMsg()));
				log.warning("Dio error al imprimir en controlador fiscal (salida por deposito)..." + Msg.parseTranslation(getCtx(),
						printResult.getMsg()));
				return;
			}
			
		}catch(Exception ex) {
			log.warning("Error al imprimir en controlador fiscal (salida por deposito):  " + ex);
		}

		// Si debo armar el mensaje, lo armo para esta factura en particular
		if(makeMsg){
			// Armo el mensaje para esta factura
			HTMLList list = createHTMLList(null, "Salida por deposito");
			getMsg().createAndAddListElement(null, invoice.getDocumentNo(), list);
			getMsg().addList(list);
		}
	}	


	// dREHER Feb 25 verifica si hay productos para retirar por deposito
	private int getWarehouseCheckoutProductsCount(MOrder order) {
		int tmp = 0;
		MOrderLine[] lines = order.getLines();
		for(MOrderLine line: lines){
			MProduct prod = line.getProduct();
			if(prod.getCheckoutPlace().equals(MProduct.CHECKOUTPLACE_Warehouse)) {
				tmp++;
			}
		}
		return tmp;
	}

	// dREHER Feb'25
	private void debug(String string) {
		System.out.println("-->InvoiceFiscalPrint." + string);
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
	
// dREHER 'Feb 25	-------------------------------------------------------------------------------
	/**
	 * @return the fiscalDocumentPrintListener
	 */
	public FiscalDocumentPrintListener getFiscalDocumentPrintListener() {
		return fiscalDocumentPrintListener;
	}

	/**
	 * @param fiscalDocumentPrintListener the fiscalDocumentPrintListener to set
	 */
	public void setFiscalDocumentPrintListener(
			FiscalDocumentPrintListener fiscalDocumentPrintListener) {
		this.fiscalDocumentPrintListener = fiscalDocumentPrintListener;
	}

	/**
	 * @return the fiscalPrinterEventListener
	 */
	public FiscalPrinterEventListener getFiscalPrinterEventListener() {
		return fiscalPrinterEventListener;
	}

	/**
	 * @param fiscalPrinterEventListener the fiscalPrinterEventListener to set
	 */
	public void setFiscalPrinterEventListener(
			FiscalPrinterEventListener fiscalPrinterEventListener) {
		this.fiscalPrinterEventListener = fiscalPrinterEventListener;
	}
// --------------------------------------------------------------------------------------------------

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
