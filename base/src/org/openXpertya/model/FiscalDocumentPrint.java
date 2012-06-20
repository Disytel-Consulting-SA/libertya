package org.openXpertya.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.print.OXPFiscalMsgSource;
import org.openXpertya.print.fiscal.FiscalPrinter;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.Customer;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.DiscountLine;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.DocumentLine;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.print.fiscal.document.NonFiscalDocument;
import org.openXpertya.print.fiscal.document.Payment;
import org.openXpertya.print.fiscal.exception.DocumentException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.print.fiscal.msg.FiscalMessages;
import org.openXpertya.print.fiscal.msg.MsgRepository;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * Impresión fiscal de documentos. Esta clase se encarga de mapear documentos
 * de openXpertya a documentos aceptados por las impresoras fiscales. 
 * @author Franco Bonafine
 * @date 12/02/2008
 */
public class FiscalDocumentPrint {
	
	// Acciones
	
	public enum Actions { 
		ACTION_PRINT_DOCUMENT, 
		ACTION_FISCAL_CLOSE,
		ACTION_PRINT_DELIVERY_DOCUMENT
	};

	static {
		// Se inicializa la fuente de mensajes para las impresiones fiscales.
		// Se asigna la fuente de openXpertya que utiliza la funcionalidad
		// de la clase Msg.translate(...).
		MsgRepository.setMsgSource(new OXPFiscalMsgSource());
		getMaxAmountCF();
	}
	
	/** Cantidad maxima de esperas cuando la impresora se encuentra en estado ocupado */
	private static int MAX_BSY_SLEEP_COUNT = 12;
	/** Período en milisegundos mediante el cual se chequea el estado de la impresora fiscal */
	private static long BSY_SLEEP_TIME = 5000;
	
	/** Logger del sistema */
	private static CLogger log = CLogger.getCLogger(FiscalDocumentPrint.class);
	/** Monto máximo de validación para consumidores finales */
	private static BigDecimal maxAmountCF = null;
	/**
	 * Clave de la preferencia para el monto máximo permitido a consumidores
	 * finales
	 */
	private static final String MAX_AMOUNT_CF_PREFERENCE_VALUE = "L_AR_CFMontoMaximo";
	/** Manejador de eventos de la impresora fiscal */
	private FiscalPrinterEventListener printerEventListener;
	/** Manejador de eventos del estado del Controlador Fiscal de OXP */
	private List<FiscalDocumentPrintListener> documentPrintListeners;
	/** Impresora fiscal con la que se imprime el documento */ 
	private FiscalPrinter fiscalPrinter;
	/** Mapeo entre la categorías de IVA de openXpertya y las de
	 *  las clases de documentos para impresoras fiscales */
	private Map<Integer, Integer> ivaResponsabilities;
	/** Contexto de la aplicación */
	protected Properties ctx = Env.getCtx();
	/** Indica si se debe ignorar el estado de error de la impresora
	 * e intentar imprimir un documento estando en este estado */
	private boolean ignoreErrorStatus = false;
	/** Indica si se debe cancelar la espera de la impresora fiscal 
	 * cuando se encuentra en estado BUSY */
	private boolean cancelWaiting = false;
	/** Tipo de documento a emitir por la impresora fiscal */
	private String printerDocType = null;
	/** Documento de OXP que se va a emitir por la impresora fiscal. */
	private PO oxpDocument;
	/** Transacción utilizada para la impresión de un documento */
	private Trx trx;
	/** Mensaje de error del impresor de documentos */
	private String errorMsg;
	/** Indica si se debe crear o no la transacción en caso de que 
	 * no se asigne ninguna externamente */
	private boolean createTrx = false;
		
	public FiscalDocumentPrint() {
		super();
		this.documentPrintListeners = new ArrayList<FiscalDocumentPrintListener>();
	}

	/**
	 * @param printerEventListener
	 */
	public FiscalDocumentPrint(FiscalPrinterEventListener printerEventListener) {
		this();
		this.printerEventListener = printerEventListener;
	}

	public FiscalDocumentPrint(FiscalPrinterEventListener printerEventListener, FiscalDocumentPrintListener documentPrintListener) {
		this();
		this.printerEventListener = printerEventListener;
		addDocumentPrintListener(documentPrintListener);
	}

	
	/**
	 * Ejecuta la acción (comando) parámetro con los argumentos específicos 
	 * en la impresora parámetro.  
	 * @param action acción a ejecutar
	 * @param controladoraFiscalID controlador fiscal
	 * @param args argumentos de la acción
	 * @return true si se ejecutó correctamente, false caso contrario.
	 */
	MControladorFiscal cFiscal = null;
	private boolean execute(Actions action, Integer controladoraFiscalID, Object[] args) {
		boolean error = false;
		String newPrinterStatus = MControladorFiscal.STATUS_IDLE;
		String errorTitle = "";
		String errorDesc = "";
		
		// Se crea una transacción en caso de que no se haya asignado externamente
		// una. manageTrx indica si se deben administrar internamente la transacción
		// (commit y roolback). Si la transacción es asignada externamente, no se realizan
		// operaciones de commit y rollback aquí.
		boolean manageTrx = false;
		if (getTrx() == null && isCreateTrx()) {
			setTrx(Trx.get(Trx.createTrxName("FiscalDocumentPrint"), true));
			getTrx().start();
			manageTrx = true;
		}
		
		// Se inicializa el indicador de fin de espera para el casos en que
		// la impresora se encuentre en estado BUSY.
		setCancelWaiting(false);
		
		// Get the printer 
		cFiscal = new MControladorFiscal(ctx, controladoraFiscalID, getTrxName());
	
		try {
			// Se informa al manejador que se esta chequeando el status de
			// la impresora.
			fireActionStarted(FiscalDocumentPrintListener.AC_CHECK_STATUS);
			// Chequeo el estado de la impresora
			if(!checkPrinterStatus(cFiscal))
				return false;
			
			// Se informa al manejador que se esta intentando conectar con
			// la impresora fiscal.
			fireActionStarted(FiscalDocumentPrintListener.AC_CONNECT_PRINTER);
			// Se obtiene la impresora fiscal con la que se debe imprimir
			// el documento segun su tipo de documento.
			FiscalPrinter fiscalPrinter = cFiscal.getFiscalPrinter();
			fiscalPrinter.setEventListener(getPrinterEventListener());
			setFiscalPrinter(fiscalPrinter);
			// Se intenta conectar la impresora.
			getFiscalPrinter().connect();
			
			// Ejecutar la acción correspondiente
			doAction(action, args);
			
			// Se libera la impresora fiscal.
			setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);			
		} catch (FiscalPrinterStatusError e) {
			// Si la impresora retornó un estado de error se marca el controlador
			// fiscal con estado de ERROR.
			newPrinterStatus = MControladorFiscal.STATUS_ERROR;
			// Se asigna el mensaje de error.
			errorTitle = "FiscalPrinterStatusError";
			errorDesc = e.getDeviceErrorMsg();
			error = true;
			log.severe(e.getMessage() + ". " + errorDesc);

		} catch (FiscalPrinterIOException e) {
			// Se asigna el mensaje de error.
			errorTitle = "FiscalPrinterIOError";
			errorDesc = e.getMessage();
			error = true;
			log.severe(e.getFullMessage());

		} catch (DocumentException e) {
			// Se asigna el mensaje de error.
			errorTitle = "DocumentValidationError";
			errorDesc = e.getMessage();
			error = true;
			log.severe(e.getMessage());
			
		} catch (IOException e) {
			// Se asigna el mensaje de error.
			errorTitle = "UnexpectedIOError";
			errorDesc = e.getMessage();
			error = true;
			log.severe(e.getMessage());

		} catch (Exception e) {
			// Se asigna el mensaje de error.
			errorTitle = "PrintFiscalDocumentError";
			errorDesc = e.getMessage();
			error = true;
			log.severe(e.getMessage());
			e.printStackTrace();

		} finally {
			try {
				// Si hubo error...
				if(error) {
					// Se asigna el nuevo estado de la impresora.
					setFiscalPrinterStatus(cFiscal, newPrinterStatus);
					// Se dispara el evento que informa del error ocurrido.
					fireErrorOcurred(errorTitle, errorDesc);
					// Se cancela la trasancción.
					if (manageTrx)
						getTrx().rollback();
					// Se guarda el mensaje de error.
					setErrorMsg("@" + errorTitle + "@ - @" + errorDesc + "@");
				} else {
					// Se efectiviza la transacción solo si no ocurrió un error.
					if (manageTrx) {
						getTrx().commit();
						getTrx().close();
					}
				}
					
				// Se desconecta la impresora en caso de que este conectada aún.
				if(getFiscalPrinter() != null && getFiscalPrinter().isConnected())
					getFiscalPrinter().close();
				
			} catch (IOException e) {
				log.severe(e.getMessage());
			}
		}
		return !error;
	}

	
	/**
	 * Ejecuto la acción correspondiente a partir de la acción parámetro
	 * @param action
	 * @param controladoraFiscalID
	 * @param args
	 * @throws Exception
	 */
	private void doAction(Actions action, Object[] args) throws Exception{
		switch(action){
			case ACTION_PRINT_DOCUMENT:		     doPrintDocument(args); break;
			case ACTION_FISCAL_CLOSE:		     doFiscalClose(args); break;
			case ACTION_PRINT_DELIVERY_DOCUMENT: doPrintDeliveryDocument(args); break;
			default:						throw new Exception(Msg.getMsg(ctx, "InvalidAction"));
		}
	}
	
	// **************************************************
	//   			ACCIONES (COMANDOS)
	// **************************************************
	
	
	// *************************
	//   IMPRESIÓN DE FACTURA
	// *************************
	
	/**
	 * Manda a imprimir un documento de openXpertya en una impresora fiscal. 
	 * @param document <code>PO</code> que representa el documento a imprimir.
	 * @return <code>true</code> en caso de que el documento se haya emitido
	 * correctamente, <code>false</false> en caso contrario.
	 */
	public boolean printDocument(PO document) {
		// Se valida que el documento tenga asignado el tipo de documento.
		Integer docType_ID = (Integer)document.get_Value("C_DocTypeTarget_ID");
		if(docType_ID == null || docType_ID == 0)
			throw new IllegalArgumentException("Error: the document has no type");
		
		// Se obtiene el tipo de documento a emitir por la impresora.
		MDocType docType = new MDocType(ctx, docType_ID, null);
		setPrinterDocType(docType.getFiscalDocument());
		
		// Se asigna el documento OXP.
		setOxpDocument(document);
		
		// Se obtiene el controlador fiscal para chequear el status
		MControladorFiscal cFiscal = MControladorFiscal.getOfDocType(docType_ID);
		
		// Ejecutar la acción
		boolean ok = execute(Actions.ACTION_PRINT_DOCUMENT, cFiscal.getID(), new Object[]{document});

		// Se actualizan los datos del documento oxp.
		updateOxpDocument((MInvoice)document, !ok);
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		return ok;
	}
	
	/**
	 * 
	 * @param document
	 * @param documentPrintable
	 * @param docType
	 * @param originalInvoice
	 * @return
	 */
	public boolean printDocument(PO document, Document documentPrintable, MDocType docType, MInvoice originalInvoice) {
		// Se valida que el tipo de documento exista
		if(docType == null)
			throw new IllegalArgumentException("Error: No document type");
		
		// Se obtiene el tipo de documento a emitir por la impresora.
		setPrinterDocType(docType.getFiscalDocument());
		
		// Se asigna el documento OXP.
		setOxpDocument(document);
		
		// Se obtiene el controlador fiscal para chequear el status
		MControladorFiscal cFiscal = MControladorFiscal.getOfDocType(docType.getID());
		
		// Ejecutar la acción
		boolean ok = execute(Actions.ACTION_PRINT_DOCUMENT, cFiscal.getID(),
				new Object[] { document, documentPrintable, originalInvoice });

		// Se actualizan los datos del documento oxp.
		updateOxpDocument((MInvoice)document, !ok);
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		return ok;
	}
	
	/**
	 * Realiza la impresión de la factura con los parámetros correspondientes.
	 * @param args
	 * @throws Exception
	 */
	private void doPrintDocument(Object[] args) throws Exception{
		// Argumentos
		MInvoice document = (MInvoice)args[0];
		Document documentPrintable = null;
		MInvoice originalInvoice = null;
		// Factura imprimible por la impresora creada a partir del documento oxp
		if(args.length > 1){
			documentPrintable = (Document)args[1];
		}
		// Documento oxp original configurada en el documento oxp, posiblemente
		// necesario para notas de crédito 
		if(args.length > 2){
			originalInvoice = (MInvoice)args[2];
		}
		// Se manda a imprimir el documento según el tipo de documento
		// de las impresoras fiscales asignado al tipo de documento de oxp
		fireActionStarted(FiscalDocumentPrintListener.AC_PRINT_DOCUMENT);
		
		// Emisión de una factura.
		if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_Invoice)) {
			printInvoice(documentPrintable);
		
		// Emisión de una nota de crédito.
		} else if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_CreditNote)) {
			printCreditNote(documentPrintable, originalInvoice);
		
		// Emisión de una nota de débito.
		} else if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_DebitNote)) {
			printDebitNote(documentPrintable);
		}
		
		// Se dispara el evento de impresión finalizada.
		fireDocumentPrintEndedOk();
		
		// Se actualiza la secuencia del tipo de documento emitido.
		updateDocTypeSequence(document);
	}
	
	// *************************
	// 		CIERRE FISCAL
	// *************************
	
	/**
	 * Envia el comando de cierre de jornada fiscal a la impresora.
	 * @param cFiscalID Impresora a la cual enviar el comando
	 * @param closeType Tipo de cierre 
	 * @return verdadero en caso de exito, falso si hubo algún problema
	 */
	public boolean fiscalClose(Integer cFiscalID, String closeType) {
		// Ejecutar la acción
		return execute(Actions.ACTION_FISCAL_CLOSE, cFiscalID, new Object[]{closeType});
	}

	/**
	 * Realiza el cierre con los parámetros dados
	 * @param args arreglo de parámetros del procedimiento
	 * @throws Exception
	 */
	private void doFiscalClose(Object[] args) throws Exception{
		// Argumentos
		String closeType = (String)args[0];
		
		fireActionStarted(FiscalDocumentPrintListener.AC_EXECUTING_ACTION);
		
		// Cerrar la impresora fiscal
		getFiscalPrinter().fiscalClose(closeType);
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_FISCAL_CLOSE);
	}

	// **************************************************************
	//   IMPRESION DE DOCUMENTO CON ARTICULOS PENDIENTES DE ENTREGA		
	// **************************************************************
	
	/**
	 * Manda a imprimir un documento NO fiscal que en sus líneas contiene
	 * el detalle de cada artículo que tiene alguna cantidad pendiente de
	 * entrega dentro de un pedido del sistema.
	 * @param cFiscalID Impresora que emite el documento
	 * @param order Pedido del cual se obtienen los artículos pendientes de
	 * entrega
	 * @return <code>true</code> en caso de que el documento se haya emitido
	 * correctamente, <code>false</false> en caso contrario.
	 */
	public boolean printDeliveryDocument(Integer cFiscalID, MOrder order) {
		return execute(Actions.ACTION_PRINT_DELIVERY_DOCUMENT, cFiscalID, new Object[] {order});
	}
	
	/**
	 * Realiza la impresión del documento no fiscal con los artículos a entregar.
	 * @param args Arreglo con los argumentos requeridos por esta funcionalidad
	 * @throws Exception
	 */
	private void doPrintDeliveryDocument(Object[] args) throws Exception {
		MOrder order = (MOrder) args[0];
		// Informa el inicio de la impresión
		fireActionStarted(FiscalDocumentPrintListener.AC_PRINT_DOCUMENT);
		// Crea el documento no fiscal y luego obtiene todas las líneas del pedido
		NonFiscalDocument nonFiscalDocument = new NonFiscalDocument();
		MOrderLine[] orderLines = order.getLines(true);
		String line = null;
		for (MOrderLine orderLine : orderLines) {
			// Por cada línea, si tiene artículos pendientes de entrega
			if (orderLine.hasNotDeliveredProducts()) {
				// Obtiene la cantidad que falta entregar
				BigDecimal qtyToDeliver = orderLine.getQtyOrdered().subtract(orderLine.getQtyDelivered());
				MProduct product = orderLine.getProduct();
				// Crea la descripción que se mostrará en la línea del documento
				line = 
					"[x" + qtyToDeliver + "] " + product.getValue() + " " + product.getName();
				// Agrega la línea al documento no fiscal
				nonFiscalDocument.addLine(line);
			}
		}
		// Manda a imprimir el documento en la impresora fiscal
		getFiscalPrinter().printDocument(nonFiscalDocument);
		
		// Se dispara el evento de impresión finalizada.
		fireDocumentPrintEndedOk();
	}
	
	// **************************************************
	
	/**
	 * @return Returns the fiscalPrinter.
	 */
	public FiscalPrinter getFiscalPrinter() {
		return fiscalPrinter;
	}

	/**
	 * @param fiscalPrinter The fiscalPrinter to set.
	 */
	public void setFiscalPrinter(FiscalPrinter fiscalPrinter) {
		this.fiscalPrinter = fiscalPrinter;
	}

	/**
	 * Crea un documento imprimible mediante un controlador fiscal a partir de
	 * la factura parámetro y del tipo de documento fiscal configurado.
	 * 
	 * @param mInvoice
	 *            factura oxp
	 * @param originalInvoice
	 *            factura original oxp para notas de crédito
	 * @return documento imprimible fiscalmente creado a partir de los
	 *         parámetros y del tipo de documento fiscal configurado
	 */
	public Document createDocument(MInvoice mInvoice, MInvoice originalInvoice){
		Document document = null;
		// Creación de una factura.
		if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_Invoice)) {
			document = createInvoice(mInvoice);
		
		// Creación de una nota de crédito.
		} else if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_CreditNote)) {
			document = createCreditNote(mInvoice, originalInvoice);
		
		// Creación de una nota de débito.
		} else if(getPrinterDocType().equals(MDocType.FISCALDOCUMENT_DebitNote)) {
			document = createDebitNote(mInvoice);
		}
		return document;
	}
	
	
	/**
	 * Crea una factura imprimible por el controlador fiscal a partir de la
	 * factura oxp parámetro
	 * 
	 * @param mInvoice
	 *            factura
	 * @return la factura imprimible creada
	 */
	public Invoice createInvoice(MInvoice mInvoice){
		Invoice invoice = new Invoice();
		// Se asigna el cliente.
		invoice.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la factura.
		invoice.setLetter(mInvoice.getLetra());

		// TODO: Se asigna el número de remito en caso de existir.
		
		// Se agregan las líneas de la factura al documento.
		loadDocumentLines(mInvoice, invoice);
		// Agrega los pagos correspondientes de la factura partir de las imputaciones
		loadInvoicePayments(invoice, mInvoice);
		
		// Se asignan los descuentos de la factura
		loadDocumentDiscounts(invoice, mInvoice.getDiscounts());
		return invoice;
	}

	/**
	 * Crea una nota de débito imprimible por un controlador fiscal a partir de
	 * una factura oxp parámetro
	 * 
	 * @param mInvoice
	 *            factura oxp
	 * @return nota de débito creada
	 */
	public DebitNote createDebitNote(MInvoice mInvoice){
		DebitNote debitNote = new DebitNote();
		// Se asigna el cliente.
		debitNote.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la nota de débito.
		debitNote.setLetter(mInvoice.getLetra());
		
		// TODO: Se asigna el número de remito en caso de existir.
		
		// Se agregan las líneas de la nota de débito al documento.
		loadDocumentLines(mInvoice, debitNote);
		return debitNote;
	}

	/**
	 * Crea una nota de crédito imprimible por un controlador fiscal a partir de
	 * una factura oxp parámetro. La factura original parámetro permite obtener
	 * el nro de factura original.
	 * 
	 * @param mInvoice
	 *            factura oxp
	 * @param originalInvoice
	 *            factura original, si es null y la factura oxp parámetro
	 *            contiene seteado una factura original dentro del campo
	 *            C_Invoice_Orig_ID se busca desde la BD.
	 * @return nota de crédito imprimible por un controlador fiscal
	 */
	public CreditNote createCreditNote(MInvoice mInvoice, MInvoice originalInvoice){
		CreditNote creditNote = new CreditNote();
		// Se asigna el cliente.
		creditNote.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la nota de crédito.
		creditNote.setLetter(mInvoice.getLetra());

		// Se asigna el número de factura original.
		String origInvoiceNumber = null;
		MInvoice mOriginalInvoice = originalInvoice;
		// Si la factura parámetro es null y la factura oxp parámetro contiene
		// una factura original seteada entonces la busco
		if (mOriginalInvoice == null && mInvoice.getC_Invoice_Orig_ID() != 0) {
			mOriginalInvoice = new MInvoice(ctx, mInvoice
					.getC_Invoice_Orig_ID(), getTrxName());
		}
		// Si existe una factura original entonces obtengo el nro de factura
		// original
		if(mOriginalInvoice != null) {			
			origInvoiceNumber = mOriginalInvoice.getDocumentNo();
			// Si no cumple con el formato de comprobantes fiscales se envia
			// el documentNo como número de factura original.
			if(origInvoiceNumber.length() == 13) {
				// El formato es: PPPP-NNNNNNNN, Ej: 0001-00000023
				origInvoiceNumber = origInvoiceNumber.substring(1,5) + "-" + origInvoiceNumber.substring(5,13);
			}
		}
		creditNote.setOriginalDocumentNo(origInvoiceNumber);
		
		// Se agregan las líneas de la nota de crédito al documento.
		loadDocumentLines(mInvoice, creditNote);
		return creditNote;
	}

	/**
	 * Impresión de una factura.
	 * 
	 * @param document
	 *            factura imprimible creada a partir del documento oxp, si es
	 *            null se crea una nueva a partir del documento oxp que está
	 *            configurado. Dentro de este método se realiza un casting del
	 *            documento parámetro hacia {@link Invoice} por lo que debe ser
	 *            una instancia de esa clase sino se producirá un error en
	 *            tiempo de ejecución.
	 */
	private void printInvoice(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException, Exception {
		MInvoice mInvoice = (MInvoice)getOxpDocument();
		// Se valida el documento OXP.
		validateOxpDocument(mInvoice);
		// Se crea la factura imprimible en caso que no exista como parámetro
		Invoice invoice = document != null ? (Invoice) document
				: createInvoice(mInvoice);
		// Se manda a imprimir la factura a la impresora fiscal.
		getFiscalPrinter().printDocument(invoice);
		// Se actualizan los datos de la factura de oxp.
		saveDocumentData(mInvoice, invoice);
	}

	/**
	 * Impresión de una nota de débito.
	 * 
	 * @param document
	 *            nota de débito imprimible por el controlador fiscal creada a
	 *            partir del documento oxp configurado. Dentro de este método se
	 *            realiza un casting del documento parámetro hacia
	 *            {@link DebitNote}, por lo tanto debe ser una instancia de esa
	 *            clase, sino se producirá un error.
	 */
	private void printDebitNote(Document document) throws Exception {
		MInvoice mInvoice = (MInvoice)getOxpDocument();
		// Se valida el documento OXP.
		validateOxpDocument(mInvoice);
		// Se crea la nota de débito imprimible
		DebitNote debitNote = document != null ? (DebitNote) document
				: createDebitNote(mInvoice);
		// Se manda a imprimir la nota de débito a la impresora fiscal.
		getFiscalPrinter().printDocument(debitNote);
		// Se actualizan los datos de la nota de debito de oxp.
		saveDocumentData(mInvoice, debitNote);
	}

	/**
	 * Impresión de una nota de crédito.
	 * 
	 * @param document
	 *            nota de crédito imprimible por el controlador fiscal creada a
	 *            partir del documento oxp configurado. Dentro de este método se
	 *            realiza un casting del documento parámetro hacia
	 *            {@link CreditNote}, por lo tanto debe ser una instancia de esa
	 *            clase, sino se producirá un error.
	 * @param originalInvoice
	 *            factura original del documento oxp configurado, si es null y
	 *            el documento parámetro también, se verifica si el documento
	 *            oxp configurado contiene una factura original, en ese caso la
	 *            obtiene de la BD.
	 */
	private void printCreditNote(Document document, MInvoice originalInvoice) throws FiscalPrinterStatusError, FiscalPrinterIOException, Exception {
		MInvoice mInvoice = (MInvoice)getOxpDocument();
		// Se valida el documento OXP.
		validateOxpDocument(mInvoice);
		CreditNote creditNote = document != null ? (CreditNote) document
				: createCreditNote(mInvoice, originalInvoice); 
		// Se manda a imprimir la nota de crédito a la impresora fiscal.
		getFiscalPrinter().printDocument(creditNote);
		// Se actualizan los datos de la nota de crédito de oxp.
		saveDocumentData(mInvoice, creditNote);
	}

	/**
	 * Crea un <code>Customer</code> a partir de una entidad comercial.
	 * @param bPartnerID ID de la entidad comercial.
	 * @return el cliente correspondiente.
	 */
	public Customer getCustomer(int bPartnerID) {
		MBPartner bPartner = new MBPartner(Env.getCtx(), bPartnerID, getTrxName());
		Customer customer = new Customer();

		if(bPartner != null) {

			// Se asigna la categoría de iva del cliente.
			MCategoriaIva categoriaIva = new MCategoriaIva(Env.getCtx(),bPartner.getC_Categoria_Iva_ID(), getTrxName());
			customer.setIvaResponsibility(traduceIvaResponsibility(categoriaIva.getCodigo()));
			MInvoice mInvoice = (MInvoice)getOxpDocument();
			
			// Si es una factura a consumidor final, los datos del cliente se
			// obtienen a partir de la factura OXP.
			if(customer.getIvaResponsibility() == Customer.CONSUMIDOR_FINAL &&
					getPrinterDocType().equals(MDocType.FISCALDOCUMENT_Invoice)) {
				
				// Nombre, nro de identificación, domicilio
				customer.setIdentificationType(Customer.DNI);
				customer.setName(mInvoice.getNombreCli());
				customer.setIdentificationNumber(mInvoice.getNroIdentificCliente());
				customer.setLocation(mInvoice.getInvoice_Adress());
			} else {
				// Si no es factura a consumidor final 
				
				// Se asigna el nombre del cliente a partir del BPartner.
				customer.setName(bPartner.getName());

				// Se asigna el domicilio. 
				MLocation location = MLocation.get(ctx, mInvoice.getBPartnerLocation().getC_Location_ID(), getTrxName());
				customer.setLocation(location.toStringShort());
				
				// Se identifica al cliente con el C.U.I.T. configurado en el Bpartner.
				if (bPartner.getTaxID() != null && !bPartner.getTaxID().trim().equals("")) {
					customer.setIdentificationType(Customer.CUIT);
					customer.setIdentificationNumber(bPartner.getTaxID());
				}
			}
		}
		
		return customer;
	}
	
	/**
	 * Carga las líneas que se encuentran en el documento de OXP hacia
	 * el documento de impresoras fiscales.
	 * @param oxpDocument Documento de OXP.
	 * @param document Documento de impresoras fiscales.
	 */
	private void loadDocumentLines(MInvoice oxpDocument, Document document) {
		// Se obtiene el indicador de si los precios contienen los impuestos incluido
		boolean taxIncluded = MPriceList.get(ctx, oxpDocument.getM_PriceList_ID(), getTrxName()).isTaxIncluded();
		// Se obtiene el redondeo para precios de la moneda de la factura
		int scale = MCurrency.get(oxpDocument.getCtx(),
				oxpDocument.getC_Currency_ID()).getCostingPrecision();
		
		MInvoiceLine[] lines = oxpDocument.getLines();
		BigDecimal unitPrice = null;
		for (int i = 0; i < lines.length; i++) {
			MInvoiceLine mLine = lines[i];
			DocumentLine docLine = new DocumentLine();
			docLine.setLineNumber(mLine.getLine());
			docLine.setDescription(manageLineDescription(docLine, mLine));
			unitPrice = getUnitPrice(mLine, scale);
			docLine.setUnitPrice(unitPrice);
			docLine.setQuantity(mLine.getQtyEntered());
			docLine.setPriceIncludeIva(taxIncluded);
			// Se obtiene la tasa del IVA de la línea
			// Se asume que el impuesto es siempre IVA, a futuro se verá 
			// que hacer si el producto tiene otro impuesto que no sea IVA.
			MTax mTax = MTax.get(Env.getCtx(),mLine.getC_Tax_ID(),null);
			docLine.setIvaRate(mTax.getRate());
			// Se agrega la línea al documento.
			document.addLine(docLine);
		} 
		
	}
	
	/**
	 * Carga los descuentos para un documento a imprimir.
	 * @param document Documento a imprimir
	 * @param discounts Lista de descuentos que se deben cargar.
	 */
	private void loadDocumentDiscounts(Document document, List<MDocumentDiscount> discounts) {
		BigDecimal generalDiscountAmt = BigDecimal.ZERO;
		DiscountLine discountLine = null;
		for (MDocumentDiscount mDocumentDiscount : discounts) {
			// Solo se tienen en cuenta descuentos que sean Bonificación o a nivel de 
			// Documento. Aquellos que son "Al Precio" ya se encuentran reflejados en 
			// los precios de las líneas del documento, y no deben se impresos en el 
			// ticket.
			if (MDocumentDiscount.CUMULATIVELEVEL_Line.equals(mDocumentDiscount
					.getCumulativeLevel())
					&& !mDocumentDiscount.isBonusApplication()) {
				continue;
			}
			
			// Si es descuento general manual se suma el importe al total de descuento
			// de ese tipo. Este tipo de descuento es proporcional a todas las tasas de
			// impuestos con lo cual no es necesario discriminar los importes según la
			// tasa.
			if (mDocumentDiscount.isManualGeneralDiscountKind()) {
				generalDiscountAmt = generalDiscountAmt.add(mDocumentDiscount
						.getDiscountAmt().negate());
			} else {
				// Para el resto de bonificaciones, se crea una línea de descuento por cada
				// tasa de impuesto debido a que el controlador fiscal requiere saber para
				// cada importe descontado, a que tasa de impuesto afecta para registrar la 
				// reducción o incremento del importe computado para la misma.
				for (MDocumentDiscount mDiscountByTax : mDocumentDiscount
						.getDiscountsByTax()) {
					
					// Crea la línea de descuento para la tasa
					discountLine = new DiscountLine(
							mDiscountByTax.getDescription(),
							mDiscountByTax.getDiscountAmt().negate(), 
							true, // Los importes en DocumentDiscount incluyen siempre el impuesto
							mDiscountByTax.getTaxRate());
					// Agrega el descuento al documento.
					document.addDocumentDiscount(discountLine);
				}
			}
		}
		
		// Si hay descuentos manuales generales se asigna un descuento general
		// al documento.
		if (generalDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
			document.setGeneralDiscount(
				new DiscountLine(
					Msg.translate(Env.getCtx(), "FiscalTicketGeneralDiscount"), 
					generalDiscountAmt, 
					true // Incluye impuestos
				)
			);
		}
		
	}

	/**
	 * Carga los pagos en la factura a emitir a partir de las imputaciones que
	 * tenga la factura en la BD.
	 */
	private void loadInvoicePayments(Invoice invoice, MInvoice mInvoice) {
		BigDecimal totalPaidAmt = BigDecimal.ZERO;
		final String OTHERS_DESC = Msg.translate(ctx, "FiscalTicketOthersPayment");
		final String CASH_DESC = Msg.translate(ctx, "FiscalTicketCashPayment");
		// Lista temporal de pagos creados a partir de los allocations
		List<Payment> payments = new ArrayList<Payment>();
		
		// Obtiene todas las imputaciones de la factura agrupadas por pago.
		String sql = 
			"SELECT C_Payment_ID, " +
			       "C_CashLine_ID, " +
			       "C_Invoice_Credit_ID, " +
			       "SUM(Amount + DiscountAmt + WriteoffAmt) AS PaidAmount " +
			"FROM C_AllocationLine " +
			"WHERE C_Invoice_ID = ? " +
			"GROUP BY C_Payment_ID, C_CashLine_ID, C_Invoice_Credit_ID " +
			"ORDER BY PaidAmount ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// Crea los pagos de Efectivo y Otros para acumular montos de sendos tipos.
		Payment othersPayment = new Payment(BigDecimal.ZERO, OTHERS_DESC);
		Payment cashPayment =new Payment(BigDecimal.ZERO, CASH_DESC);
		
		try {
			pstmt = DB.prepareStatement(sql, getTrxName());
			pstmt.setInt(1, mInvoice.getC_Invoice_ID());
			rs = pstmt.executeQuery();
			
			int paymentID;
			int cashLineID;
			int invoiceCreditID;
			BigDecimal paidAmt = null;
			String description = null;
			// Pago que se crea en caso de que la imputación no entre en la clase
			// Efectivo u Otros Pagos.
			Payment payment = null;
			while (rs.next()) {
				// Obtiene los IDs de los documentos para determinar cual es el que
				// se utilizó para el pago
				paymentID = rs.getInt("C_Payment_ID");
				cashLineID = rs.getInt("C_CashLine_ID");
				invoiceCreditID = rs.getInt("C_Invoice_Credit_ID");
				paidAmt = rs.getBigDecimal("PaidAmount");
				description = null;
				payment = null;
				
				// 1. Imputación con un C_Payment.
				if (paymentID > 0) {
					// Obtiene la descripción.
					description = getInvoicePaymentDescription(new MPayment(
							mInvoice.getCtx(), paymentID, getTrxName()));
				// 2. Imputación con Línea de Caja
				} else if (cashLineID > 0) {
					// Todas las imputaciones con líneas de caja se suman al pago
					// global en Efectivo para imprimir una única línea que diga
					// "Efectivo".
					cashPayment.setAmount(cashPayment.getAmount().add(paidAmt));
				// 3. Imputación con Factura de Crédito (NC)
				} else if (invoiceCreditID > 0) {
					// Obtiene la descripción.
					description = getInvoicePaymentDescription(new MInvoice(
							ctx, invoiceCreditID, getTrxName()));
				}
				
				// Si es un tipo que entra dentro de "Otros Pagos", se suma el importe
				// al payment de "Otros Pagos".
				if (OTHERS_DESC.equals(description)) {
					othersPayment.setAmount(othersPayment.getAmount().add(paidAmt));
				// Caso Contrario (Tarjeta, Cheque, Transferencia, NC, etc), se crea el pago
				// con la descripción.
				} else if (description != null) {
					payment = new Payment(paidAmt, description);
				}
				
				// Si se creó un nuevo pago se agrega a la lista ordenada de pagos
				// según el mayor importe.
				if (payment != null) {
					payments.add(payment);
				}
				
				totalPaidAmt = totalPaidAmt.add(paidAmt);
			} // while
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error getting invoice allocations", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		// Si el total pagado es cero implica que la factura no contiene imputaciones
		// de pagos realizados. En ese caso, se agrega un único pago por el total de
		// la factura y en la descripción se pone el PaymentRule de la factura.
		if (totalPaidAmt.compareTo(BigDecimal.ZERO) == 0) {
			// Se crea un pago con el total de la factura y el medio de pago en
			// la descripción.
			String paymentRule = mInvoice.getPaymentRule();
			String paymentMedium = MRefList.getListName(ctx,
					MInvoice.PAYMENTRULE_AD_Reference_ID, paymentRule);
			invoice.addPayment(new Payment(mInvoice.getGrandTotal(),
					paymentMedium));
		// Si hay pagos, se cargan a los pagos de la factura a emitir.
		} else {
			int paymentQty = 0;
			// Primero el efectivo
			if (cashPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				invoice.addPayment(cashPayment);
				paymentQty++;
			}
			
			// Si hay "otros pagos" se suma el contador pero se agrega al final
			// de la lista de modo que la impresión quede en esa ubicación.
			if (othersPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				paymentQty++;
			}
			
			int allowedCount = getFiscalPrinter().getAllowedPaymentQty();
			
			for (Iterator<Payment> paymentsIter = payments.iterator(); paymentsIter.hasNext();) {
				Payment payment = paymentsIter.next();
				if (paymentQty == allowedCount - 1) {
					if (!paymentsIter.hasNext()
							|| othersPayment.getAmount().compareTo(
									BigDecimal.ZERO) > 0) {
						invoice.addPayment(payment);
					} else {
						othersPayment.setAmount(othersPayment.getAmount().add(
								payment.getAmount()));
					}
				} else if (paymentQty < allowedCount) {
					invoice.addPayment(payment);
				} else {
					othersPayment.setAmount(othersPayment.getAmount().add(
							payment.getAmount()));
				}
				paymentQty++;
			}
			
			if (othersPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				invoice.addPayment(othersPayment);
			}
		}
		
	}
	
	/**
	 * Devuelve la descripción a imprimir para un pago según un MPayment.
	 */
	private String getInvoicePaymentDescription(MPayment mPayment) {
		Properties ctx = mPayment.getCtx();
		String description = null;
		// - Tarjeta de Crédito: NombreTarjeta NroCupon.
		//   Ej: VISA 1248
		if (MPayment.TENDERTYPE_CreditCard.equals(mPayment.getTenderType())) {
			description = MRefList.getListName(ctx,
					MPayment.CREDITCARDTYPE_AD_Reference_ID,
					mPayment.getCreditCardType()) + " " + mPayment.getCouponNumber();
		// - Cheque: Cheque NumeroCheque
		//   Ej: Cheque 00032456	
		} else if (MPayment.TENDERTYPE_Check.equals(mPayment.getTenderType())) {
			description = Msg.translate(ctx, "FiscalTicketCheckPayment") + " "
					+ mPayment.getCheckNo();
		// - Transferencia: Transf NroDeTransferencia
		//   Ej: Transf 893276662	
		} else if (MPayment.TENDERTYPE_DirectDeposit.equals(mPayment.getTenderType())) {
			description = Msg.translate(ctx, "FiscalTicketTransferPayment")
					+ " " + mPayment.getCheckNo(); // En CheckNo se guarda el nro de transferencia actualmente.
		// Otros tipos: Otros pagos
		} else {
			description = Msg.translate(ctx, "FiscalTicketOthersPayment");
		}
		
		return description;
	}
	
	/**
	 * Devuelve la descripción a imprimir para un pago según un MInvoice.
	 */
	private String getInvoicePaymentDescription(MInvoice mInvoice) {
		String description = null;
		// - Nota de Crédito: NC NroNotaCrédito
		//   Ej: NC A000100004567
		if (MDocType.DOCBASETYPE_ARCreditMemo.equals(MDocType.get(ctx,
				mInvoice.getC_DocType_ID()).getDocBaseType())) {
			description = Msg.translate(ctx, "FiscalTicketCreditNotePayment")
					+ " " + mInvoice.getDocumentNo();
		// - Resto de facturas (FP, Retenciones, etc): se toman como "Otros Pagos"
		} else {
			description = Msg.translate(ctx, "FiscalTicketOthersPayment");
		}
	
		return description;
	}
	
	
	
	private void saveDocumentData(MInvoice oxpDocument, Document document) {
		String dtType = document.getDocumentType();
		
		/////////////////////////////////////////////////////////////////
		// -- Numero del comprobante emitido -- 
		// Solo para facturas, notas de crédito y débito.
		if (dtType.equals(Document.DT_INVOICE) ||
		    dtType.equals(Document.DT_CREDIT_NOTE) ||
		    dtType.equals(Document.DT_DEBIT_NOTE)) {
		
			int receiptNo = Integer.parseInt(document.getDocumentNo());
			// Solo se actualiza el documento de oxp en caso de que el 
			// número de comprobante emitido por la impresora fiscal
			// sead distinto al que le había asignado oxp.
			if(receiptNo != oxpDocument.getNumeroComprobante()) {
				oxpDocument.setNumeroComprobante(receiptNo);
				// Se modifica el número de documento de OXP acorde al número de 
				// comprobante fiscal.
				String documentNo = CalloutInvoiceExt.GenerarNumeroDeDocumento(
						oxpDocument.getPuntoDeVenta(), receiptNo, oxpDocument.getLetra(), oxpDocument.isSOTrx());
				oxpDocument.setDocumentNo(documentNo);
			}

		}
		
		/////////////////////////////////////////////////////////////////
		// -- Numero del CAI -- 
		// Solo para facturas y notas de débito, siempre y cuando
		// la impresora haya seteado alguno.
		if (dtType.equals(Document.DT_INVOICE) ||
		    dtType.equals(Document.DT_DEBIT_NOTE)) { 
			
			Invoice invoiceOrDN = (Invoice)document; 
			if(invoiceOrDN.hasCAINumber()) {
				// Se asigna el número del CAI.
				oxpDocument.setCAI(invoiceOrDN.getCAINumber());
				// Se asigna la fecha del CAI como la fecha actual.
				oxpDocument.setDateCAI(new Timestamp(System.currentTimeMillis()));
			}
		}

		/////////////////////////////////////////////////////////////////
		// -- Documento Impreso por Impresora fiscal -- 
		// Se marca el documento como impreso fiscalmente para que no pueda
		// volver a imprimirse.
		oxpDocument.setFiscalAlreadyPrinted(true);
		
		// Se guardan los cambios realizados.
		if(canSaveOxpDocument()){
			oxpDocument.save();
		}
	}

	/**
	 * @return Returns the ivaResponsabilities.
	 */
	protected Map<Integer, Integer> getIvaResponsabilities() {
		if(ivaResponsabilities == null) {
			ivaResponsabilities = new HashMap<Integer,Integer>();
			ivaResponsabilities.put(1, Customer.CONSUMIDOR_FINAL);
			ivaResponsabilities.put(2, Customer.RESPONSABLE_INSCRIPTO);
			ivaResponsabilities.put(3, Customer.RESPONSABLE_NO_INSCRIPTO);
			ivaResponsabilities.put(4, Customer.EXENTO);
			ivaResponsabilities.put(5, Customer.RESPONSABLE_MONOTRIBUTO);
			ivaResponsabilities.put(6, Customer.NO_RESPONSABLE);
			ivaResponsabilities.put(7, Customer.NO_CATEGORIZADO);
			ivaResponsabilities.put(8, Customer.RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO);
			ivaResponsabilities.put(9, Customer.RESPONSABLE_INSCRIPTO);
			ivaResponsabilities.put(10, Customer.RESPONSABLE_INSCRIPTO);
			//ivaResponsabilities.put(Customer.MONOTRIBUTISTA_SOCIAL);
			//ivaResponsabilities.put(Customer.PEQUENO_CONTRIBUYENTE_EVENTUAL);
			//ivaResponsabilities.put(Customer.PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL);
		}
		return ivaResponsabilities;
	}
	
	/**
	 * Realiza la conversión entre el entero que representa a la categoría
	 * de IVA de openXpertya y el entero en las clases de documentos de 
	 * las impresoras fiscales. 
	 * @param ivaResponsibility Valor de la responsabilidad frente a IVA.
	 * @return El entero que representa la responsabilidad frente al IVA.
	 */
	protected int traduceIvaResponsibility(Integer ivaResponsibility) {
		Integer result = getIvaResponsabilities().get(ivaResponsibility);
		if(result == null)
			result = Customer.NO_CATEGORIZADO;
		return result;
	}

	/**
	 * @return Returns the printerEventListener.
	 */
	public FiscalPrinterEventListener getPrinterEventListener() {
		return printerEventListener;
	}

	/**
	 * @param printerEventListener The printerEventListener to set.
	 */
	public void setPrinterEventListener(FiscalPrinterEventListener printerEventListener) {
		this.printerEventListener = printerEventListener;
		if(getFiscalPrinter() != null)
			getFiscalPrinter().setEventListener(printerEventListener);
	}
	
	private void fireStatusReported(MControladorFiscal cFiscal, String status) {
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.statusReported(this, cFiscal, status);
		}
	}
	
	private void fireStatusReported(MControladorFiscal cFiscal) {
		fireStatusReported(cFiscal, cFiscal.getstatus());
	}

	private void fireActionStarted(Integer action) {
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.actionStarted(this, action);
		}
	}
	
	protected void fireErrorOcurred(String errorTitle, String errorDesc) {
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.errorOcurred(
				 	this, 
				 	Msg.parseTranslation(ctx,errorTitle), 
					Msg.parseTranslation(ctx,errorDesc));
		}
	}

	protected void fireDocumentPrintEndedOk() {
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.documentPrintEndedOk(this);
		}
	}

	
	private void fireActionEndedOk(Actions action){
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.actionEnded(true, action);
		}
	}
	
	/**
	 * @return Returns the documentPrintListeners.
	 */
	public List<FiscalDocumentPrintListener> getDocumentPrintListeners() {
		return documentPrintListeners;
	}
	
	private boolean checkPrinterStatus(MControladorFiscal cFiscal) throws Exception {
		int bsyCount = 0;
		// Si la impresora se encuentra en estado de error se dispara el evento
		// que informa dicha situación.
		if(cFiscal.getstatus().equals(MControladorFiscal.STATUS_ERROR)) {
			fireStatusReported(cFiscal);
			// Dependiendo de si hay que ignorar o no el estado de error
			// se continua con la impresión. (Esto se utiliza para evitar
			// los casos en que la impresora quede marcada como error en 
			// la BD pero el dispositivo ya no contenga mas este error.
			if(isIgnoreErrorStatus()) 
				// Por ello se setea la impresora como Lista y se intenta
				// continuar con la impresión.
				setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);
			else 
				// Si no se pueden ignorar estados de error, entonces 
				// no es posible continuar con la impresión.
				return false;
		}			

		// Mientras el status sea BUSY, espera 5 segundos y vuelve a chequear.
		while(cFiscal.getstatus().equals(MControladorFiscal.STATUS_BUSY) && !isCancelWaiting()) {
			fireStatusReported(cFiscal);
			Thread.sleep(BSY_SLEEP_TIME);
			bsyCount++;
			cFiscal.load((String)null);
			if(bsyCount == MAX_BSY_SLEEP_COUNT)
				throw new IOException(Msg.translate(ctx,"FiscalPrinterBusyTimeoutError"));
		}
		// Si fue cancelada la operacion de espera entonces se retorna, indicando
		// que el estado no es correcto.
		if(isCancelWaiting()) { 
			log.fine("Fiscal printer wait canceled");
			return false;
		}
			
		fireStatusReported(cFiscal, MControladorFiscal.STATUS_IDLE);
		// Se asigna el status de la impresora, el usuario que realiza la operación
		// y la fecha de operación.
		cFiscal.setstatus(MControladorFiscal.STATUS_BUSY);
		cFiscal.setUsedBy_ID(Env.getAD_User_ID(ctx));
		cFiscal.setoperation_date(new Timestamp(System.currentTimeMillis()));
		// No se usa trx dado que los cambios deben ser visibles 
		// inmediatamente por otros usuarios.
		cFiscal.save(); 
		return true;
	}
	
	private void setFiscalPrinterStatus(MControladorFiscal cFiscal, String status) {
		if(cFiscal != null) {
			cFiscal.setstatus(status);
			cFiscal.save();
		}
	}

	/**
	 * @return Returns the ignoreErrorStatus.
	 */
	public boolean isIgnoreErrorStatus() {
		return ignoreErrorStatus;
	}

	/**
	 * @param ignoreErrorStatus The ignoreErrorStatus to set.
	 */
	public void setIgnoreErrorStatus(boolean ignoreErrorStatus) {
		this.ignoreErrorStatus = ignoreErrorStatus;
	}

	/**
	 * @return Returns the cancelWaiting.
	 */
	public boolean isCancelWaiting() {
		return cancelWaiting;
	}

	/**
	 * @param cancelWaiting The cancelWaiting to set.
	 */
	public void setCancelWaiting(boolean cancelWaiting) {
		this.cancelWaiting = cancelWaiting;
	}
	
	private void validateOxpDocument(MInvoice mInvoice) throws Exception {
		// Validar estado del documento y FiscalAlreadyPrinted
		/*
		if(!mInvoice.getDocStatus().equals("CO") && !mInvoice.getDocStatus().equals("CL")) {
			log.severe("The invoice to print must be completed.");
			throw new Exception(Msg.translate(ctx,"RequireCompletedFiscalDocument"));
		}
		*/
		
		// Validar si la factura ya fue impresa.
		if(mInvoice.isFiscalAlreadyPrinted()) {
			log.severe("The invoice was already printed with a fiscal printer.");
			throw new Exception(Msg.translate(ctx,"FiscalAlreadyPrintedError"));
		}
	}
	
	private void updateOxpDocument(MInvoice mInvoice, boolean error) {
		if(error) {
			//TODO: verificar.
			//mInvoice.setDocStatus(MInvoice.DOCSTATUS_InProgress);
			//mInvoice.save();
		}
	}

	private void updateDocTypeSequence(MInvoice mInvoice) {
		// Se actualiza la secuencia del tipo de documento del documento
		// emitido recientemento por la impresora fiscal.
		Integer lastDocumentNo = new Integer(getFiscalPrinter().getLastDocumentNo());
		MDocType docType = MDocType.get(ctx, mInvoice.getC_DocTypeTarget_ID());
		// Se obtiene la secuencia del tipo de documento...
		if(docType.getDocNoSequence_ID() != 0) {
			MSequence seq = new MSequence(ctx, docType.getDocNoSequence_ID(), getTrxName());
			String currentNext = String.valueOf(seq.getCurrentNext());
			
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMinimumIntegerDigits(8);
			format.setMaximumIntegerDigits(8);
			format.setGroupingUsed(false);
			// Se obtiene el número siguiente de documento según el ultimo comprobante
			// emitido por la impresora fiscal.
			String newCurrentNext = currentNext.substring(0,1) + format.format(lastDocumentNo + 1);
			// Se actualiza la secuencia solo si el número de comprobante siguiente es distinto al
			// que ya tenía la secuencia.
			if(!currentNext.equals(newCurrentNext)) {
				seq.setCurrentNext(Integer.parseInt(newCurrentNext));
				seq.save();
			}
		}
	}
		
	/**
	 * Agrega un manejador de impresió de documentos al cual se le reportan
	 * los estados de la impresión.
	 * @param fdpl <code>FiscalDocumentPrintListener</code> manejador de eventos.
	 */
	public void addDocumentPrintListener(FiscalDocumentPrintListener fdpl) {
		if(!getDocumentPrintListeners().contains(fdpl)) {
			getDocumentPrintListeners().add(fdpl);
			fdpl.setFiscalDocumentPrint(this);
		}
	}
	
	/**
	 * Elimina un manejador de eventos de la colección.
	 * @param fdpl Manejador de eventos a eliminar.
	 */
	public void removeDocumentPrintListener(FiscalDocumentPrintListener fdpl) {
		getDocumentPrintListeners().remove(fdpl);
	}

	/**
	 * @return Returns the printerDocType.
	 */
	public String getPrinterDocType() {
		return printerDocType;
	}

	/**
	 * @param printerDocType The printerDocType to set.
	 */
	protected void setPrinterDocType(String printerDocType) {
		this.printerDocType = printerDocType;
	}

	/**
	 * @return Returns the oxpDocument.
	 */
	public PO getOxpDocument() {
		return oxpDocument;
	}

	/**
	 * @param oxpDocument The oxpDocument to set.
	 */
	protected void setOxpDocument(PO oxpDocument) {
		this.oxpDocument = oxpDocument;
	}

	/**
	 * @return Returns the trx.
	 */
	public Trx getTrx() {
		return trx;
	}

	/**
	 * @param trx The trx to set.
	 */
	public void setTrx(Trx trx) {
		this.trx = trx;
	}
	
	/**
	 * @return El nombre de la transacción actual.
	 */
	protected String getTrxName() {
		if(getTrx() == null)
			return null;
		return getTrx().getTrxName();
	}

	/**
	 * @return Returns the errorMsg.
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg The errorMsg to set.
	 */
	protected void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * @return Devuelve los mensajes de la impresora fiscal actualmente instanciada.
	 * Si no se ha instanciado una impresora fiscal devuelve <code>null</code>.
	 */
	public FiscalMessages getFiscalMessages() {
		FiscalMessages fiscalMessages = null;
		if (getFiscalPrinter() != null) {
			fiscalMessages = getFiscalPrinter().getMessages();
		}
		return fiscalMessages;
	}

	/**
	 * @return Intenta reimprimir el documento asociado con este impresor, el
	 *         cual se intentó imprimir previamente y se produjo algún error.
	 */
	public boolean reprintDocument() {
		if (getOxpDocument() == null) {
			throw new IllegalStateException("Document must be not null");
		}
		cancelWaiting = false;
		errorMsg = null;
		printerDocType = null;
		fiscalPrinter = null;
		
		return printDocument(getOxpDocument());
	}

	/**
	 * @return Indica si se debe crear o no la transacción en caso de que no se
	 *         asigne ninguna externamente
	 */
	public boolean isCreateTrx() {
		return createTrx;
	}

	/**
	 * Setea el valor indica si se debe crear o no la transacción en caso de que
	 * no se asigne ninguna externamente
	 * 
	 * @param useTrx
	 *            el valor de useTrx a asignar
	 */
	public void setCreateTrx(boolean createTrx) {
		this.createTrx = createTrx;
	}

	/**
	 * @return true si se debe guardar el documento oxp, false caso contrario
	 */
	protected boolean canSaveOxpDocument(){
		return true;
	}
	
	/**
	 * @return el String a imprimir en cada linea del ticket
	 */
	protected String manageLineDescription(DocumentLine docLine, MInvoiceLine mLine)
	{
		MProduct aProduct = null;
		String value = "";
		String name = "";
		
		// Cargar la descripcion con la información de la línea
		String description = 
				(mLine.getM_Product_ID() != 0 || mLine.getC_Charge_ID() != 0?
					mLine.getName():mLine.getDescription());
		
		// Si es un artículo se setea según la configuración indicada en la impresora fiscal
		if (mLine.getM_Product_ID() > 0) 
		{
			// recuperar el articulo
			aProduct = MProduct.get(ctx, mLine.getM_Product_ID());
			
			// vacio la descripcion, ya que no utilizaré la de la linea, sino la info del artículo
			description = " ";

			/* Usar los campos Identificador para definir el contenido de la linea */ 
			if (cFiscal.isOnPrintUseProductReference())
			{
				return genDescriptionFromIdentifiers(aProduct, getTrxName());
			}
			/* Usar alguna de las combinaciones CLAVE NOMBRE - NOMBRE CLAVE - NOMBRE - CLAVE */
			else
			{
				// recuperar clave y nombre del articulo
				if (aProduct.getValue() != null && !aProduct.getValue().trim().isEmpty()) 
					value = aProduct.getValue().trim();
				if (aProduct.getName() != null && !aProduct.getName().trim().isEmpty())
					name = aProduct.getName().trim();
				
				// armar la descripción según la selección
				if (MControladorFiscal.ONPRINTPRODUCTFORMAT_Name.equals(cFiscal.getOnPrintProductFormat()))
					description = name;
				if (MControladorFiscal.ONPRINTPRODUCTFORMAT_Value.equals(cFiscal.getOnPrintProductFormat()))
					description = value;
				if (MControladorFiscal.ONPRINTPRODUCTFORMAT_NameValue.equals(cFiscal.getOnPrintProductFormat()))
					description = name + " " + value;
				if (MControladorFiscal.ONPRINTPRODUCTFORMAT_ValueName.equals(cFiscal.getOnPrintProductFormat()))
					description = value + " " + name;	
			}
		}	
		
		return description;
	}
	
	/**
	 * Retorna la descrpcion a imprimir en funcion de la configuración de las columnas de M_Product
	 */
	public static String genDescriptionFromIdentifiers(MProduct aProduct, String trxName)
	{
		// Recuperar las columnas identificadoras
		Vector<String> columns = M_Table.getIdentifierColumns(trxName, "M_Product");
		
		StringBuffer result = new StringBuffer();
		int count = columns.size();
		for (int i = 0; i < count; i++)
			result.append((aProduct.get_Value(columns.elementAt(i))).toString()).append(" ");

		return result.toString();
	}

	/**
	 * Esta validación debe hacerse línea por línea y ni bien se detecte que el
	 * acumulado supera el máximo se debe disparar un error dado que es así como
	 * se ejecutan los comandos en la impresora fiscal y la misma no puede
	 * predecir si luego de una linea de X monto va a venir un descuento (el
	 * cual haría que el total de la factura sea válido).
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param invoice
	 *            factura
	 * @throws Exception
	 *             en caso de que no pase la validación de límite de monto
	 *             máximo para consumidor final
	 */
	public static void validateInvoiceCFLimit(Properties ctx, MBPartner bpartner, MInvoice invoice, String trxName) throws Exception {
		MDocType mDocType = new MDocType(ctx, invoice.getC_DocTypeTarget_ID(),
				trxName);
		// Debe ser transacción de ventas, tipo de documento fiscal, y factura
		// de cliente
		if (invoice.isSOTrx() 
				&& MDocType.isFiscalDocType(invoice.getC_DocTypeTarget_ID())
				&& mDocType.isDocType(MDocType.DOCTYPE_CustomerInvoice)) {
			
			boolean validName = !Util.isEmpty(invoice.getNombreCli(), true);
			boolean validLocation = !Util.isEmpty(invoice.getInvoice_Adress(), true);
			boolean validIdNumber = !Util.isEmpty(invoice.getNroIdentificCliente(), true);
			boolean customerDataValid = validName && validLocation && validIdNumber;
		
			// Obtengo el codigo de la categoría de iva del cliente
			int codigoCategoriaIVA = MCategoriaIva.getCodigo(
					bpartner.getC_Categoria_Iva_ID(), trxName);
			
			if (codigoCategoriaIVA == MCategoriaIva.CONSUMIDOR_FINAL
					&& !customerDataValid) {
				BigDecimal total = BigDecimal.ZERO;
				boolean amountValid = true;
				// Por cada línea...
				for (MInvoiceLine invoiceLine : invoice.getLines()) {
					// Se suma al total (sin descuentos / recargos).
					total = total.add(invoiceLine.getTotalPriceListWithTax());
					// Se pregunta si sigue siendo válido el monto.
					if(total.compareTo(getMaxAmountCF()) > 0)
						amountValid = false;
					// Si sigue siendo válido
					else {
						// Se obtiene el monto del descuento / recargo.
						total = total
								.subtract(invoiceLine.getTotalLineDiscountUnityAmtWithTax())
								.subtract(invoiceLine.getTotalBonusUnityAmtWithTax());
						// Y se válida nuevamente el total.
						if(total.compareTo(getMaxAmountCF()) > 0)
							amountValid = false;
					}
					// Si no fue válido en alguno de los casos, se dispara la excepcion
					if(!amountValid) {
						// Se arma el mensaje de respuesta.
						StringBuffer msg = new StringBuffer();
						msg.append(MsgRepository.get("InvalidCFInvoiceAmount"));
						msg.append(" ($" + getMaxAmountCF() + "). ");
						msg.append(MsgRepository.get("CompleteCustomerFields") + ": ");
						if(!validName)
							msg.append(MsgRepository.get("Name") + (!validLocation?",":""));
						if(!validLocation)
							msg.append(MsgRepository.get("Location") + (!validIdNumber?",":""));
						if(!validIdNumber)
							msg.append(MsgRepository.get("IdentificationNumber"));
						throw new Exception(msg.toString());
					}
				}
			}
		}
	}
	
	
	public static BigDecimal getUnitPrice(MInvoiceLine mLine, int scale){
		BigDecimal unitPrice = BigDecimal.ZERO;
		// Aquí tenemos dos casos de línea: Con Bonificaciones o Sin Bonificaciones
		// 1. Sin Bonificaciones
		// El precio unitario es entonces simplemente el precio actual de la
		// línea, es decir el PriceActual.
		if (!mLine.hasBonus()) {
			unitPrice = mLine.getPriceActual();
		} else {
		// 2. Con Bonificaciones
		// Aquí NO se puede utilizar el mLine.getPriceActual() ya que el
		// mismo tiene contemplado las bonificaciones mientras que en la
		// impresión del ticket, las bonificaciones se restan al final
		// del mismo. De esta forma, el precio unitario para el ticket
		// va a ser mayor que el PriceActual de la línea en caso de que
		// la misma contenga bonificaciones.
		// El cálculo a realizar es:
		//    (PriceList * Qty - LineDiscountAmt) / Qty
		//
			unitPrice = (mLine.getPriceList().multiply(mLine.getQtyEntered())
					.subtract(mLine.getLineDiscountAmt())).divide(
					mLine.getQtyEntered(), scale, RoundingMode.HALF_UP);
		}
		return unitPrice;
	}
	
	/**
	 * @return Returns the maxAmountCF.
	 */
	public static BigDecimal getMaxAmountCF() {
		// Si no existe buscarlo desde las preferencias, sino el default es
		// 1000.
		if(maxAmountCF == null){
			// Obtenerlo desde las preferencias
			String maxAmountCFValue = MPreference.GetCustomPreferenceValue(
					MAX_AMOUNT_CF_PREFERENCE_VALUE,
					Env.getAD_Client_ID(Env.getCtx()));
			setMaxAmountCF(Util.isEmpty(maxAmountCFValue, true) ? new BigDecimal(
					1000) : new BigDecimal(maxAmountCFValue));
		}
		return maxAmountCF;
	}

	/**
	 * @param maxAmountCF The maxAmountCF to set.
	 */
	public static void setMaxAmountCF(BigDecimal maxAmountCF) {
		FiscalDocumentPrint.maxAmountCF = maxAmountCF;
	}
}
