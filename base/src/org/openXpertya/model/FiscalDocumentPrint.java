package org.openXpertya.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.print.OXPFiscalMsgSource;
import org.openXpertya.print.fiscal.FiscalClosingResponseDTO;
import org.openXpertya.print.fiscal.FiscalInitData;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.FiscalPrinter;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.print.fiscal.FiscalPrinterLogRecord;
import org.openXpertya.print.fiscal.document.CashPayment;
import org.openXpertya.print.fiscal.document.CashRetirementPayment;
import org.openXpertya.print.fiscal.document.ClientOrgInfo;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.CurrentAccountInfo;
import org.openXpertya.print.fiscal.document.Customer;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.DiscountLine;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.DocumentLine;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.print.fiscal.document.NonFiscalDocument;
import org.openXpertya.print.fiscal.document.Payment;
import org.openXpertya.print.fiscal.document.Payment.TenderType;
import org.openXpertya.print.fiscal.document.Tax;
import org.openXpertya.print.fiscal.exception.DocumentException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.print.fiscal.hasar.HasarFiscalPrinter2G;
import org.openXpertya.print.fiscal.msg.FiscalMessages;
import org.openXpertya.print.fiscal.msg.MsgRepository;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.FacturaElectronicaQRCodeGenerator;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ReservedUtil;
import org.openXpertya.util.StringUtil;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
import org.openXpertya.xml.util.xmlParser;

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
		ACTION_PRINT_DELIVERY_DOCUMENT,
		ACTION_PRINT_CURRENT_ACCOUNT_DOCUMENT,
		ACTION_OPEN_DRAWER,
		ACTION_GET_INIT_DATA,
		ACTION_FISCAL_AUDIT, // dREHER
		ACTION_FISCAL_REPORT_AUDIT // dREHER
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
	/**
	 * Cuando el comprobante original de la NC no está seteado en la factura, es
	 * posible obtenerlo del pedido relacionado a la NC. Esta preference
	 * contiene claves de tipo de documento de pedido pasibles de obtención de
	 * nro de documento.
	 */
	private static final String COMPROBANTE_ORIGINAL_NC_TIPOS_DOCUMENTO_PEDIDOS_PREFERENCE_NAME = "NCFiscal_ComprobanteOriginal_ClaveTiposDocumentoPedido";
	
	/** Preference que permite abrir siempre el cajón de dinero */
	protected static final String ALWAYS_OPEN_DRAWER_PREFERENCE_VALUE = "FiscalPrinter_Always_Open_Drawer";
	
	/** Prefijo de la Preference de descripción fiscal de la categoría de iva */
	protected static final String CATEGORIAIVA_LEYENDAFISCAL_PREFIX_PREFERENCE_NAME = "CategoriaIVA_LeyendaFiscal_";
	
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
	/** Documento imprimible */
	private Document document;
	/** Transacción utilizada para la impresión de un documento */
	private Trx trx;
	/** Mensaje de error del impresor de documentos */
	private String errorMsg;
	/** Indica si se debe crear o no la transacción en caso de que 
	 * no se asigne ninguna externamente */
	private boolean createTrx = false;
	/**
	 * Tirar Excepción al cancelar la impresión en el momento de chequeo de
	 * estado de impresora fiscal
	 */
	private boolean throwExceptionInCancelCheckStatus = false;
	
	/** Indica si está permitido preguntar */
	private boolean askAllowed = false;
	
	/** Indica si está permitido preguntar en este momento */	
	private boolean askMoment = false;
	
	/**
	 * Indica si siempre se debe abrir el cajón de dinero al imprimir un
	 * documento
	 */
	private boolean alwaysOpenDrawer = false;
	
	/**
	 * Datos de inicialización de la impresora fiscal. Se requiere ejecutar el
	 * comando para que esta variable tenga datos.
	 */
	private FiscalInitData fiscalInitData = null;
	
	/** Comando para obtener ConsultarAcumuladosComprobante 
	 * 
	 * dREHER 
	 */
	protected static final int CMD_CONSULTAR_ACUMULADOS_COMPROBANTE = 0x8C; 

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
			
			log("Se genero una nueva transaccion.");
		}
		
		// Se inicializa el indicador de fin de espera para el casos en que
		// la impresora se encuentre en estado BUSY.
		setCancelWaiting(false);
		log("Se inicializa el indicador de fin de espera.");
		
		setAskMoment(false);
		
		// Get the printer 
		cFiscal = new MControladorFiscal(ctx, controladoraFiscalID, getTrxName());
		log("Se carga el controlador fiscal a utilizar. " + cFiscal);
		
		try {
			// Se informa al manejador que se esta chequeando el status de
			// la impresora.
			fireActionStarted(FiscalDocumentPrintListener.AC_CHECK_STATUS);
			log("Se informa al manejador que se esta chequeando el status");
			
			// Se obtiene la impresora fiscal con la que se debe imprimir
			// el documento segun su tipo de documento.
			FiscalPrinter fiscalPrinter = cFiscal.getFiscalPrinter();
			setFiscalPrinter(fiscalPrinter);
			log("Se carga la impresora a utilizar. " + fiscalPrinter);
			
			// Chequeo el estado de la impresora
			if(!checkPrinterStatus(cFiscal)) {
				log.warning("Volvio del chequeo de impresora fiscal con ERROR!");
				return false;
			}
			
			// Se informa al manejador que se esta intentando conectar con
			// la impresora fiscal.
			fireActionStarted(FiscalDocumentPrintListener.AC_CONNECT_PRINTER);
			fiscalPrinter.setEventListener(getPrinterEventListener());
			log("Se informa al manejador que se esta intentando conectar con la impresora fiscal");
			
			// Se intenta conectar la impresora.
			getFiscalPrinter().connect();
			log("Se conecto con la impresora fiscal");
			
			// Ejecutar la acción correspondiente
			log("Va a ejecutar la accion: " + action);
			doAction(action, args);
			log("Ejecuto la accion: " + action);
			
			// Se libera la impresora fiscal.
			setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);
			log("Se libera la impresora fiscal.");
			
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
			// Guardar Log
			log("Guarda log.");
			saveFiscalLog(error);
			// Si hubo error...
			if(error) {
				log("Hubo un error.");
				if (!ask()) {
					
					log("Se asigna el nuevo estado de la impresora.");
					// Se asigna el nuevo estado de la impresora.
					setFiscalPrinterStatus(cFiscal, newPrinterStatus);
					
					// Se dispara el evento que informa del error ocurrido.
					fireErrorOcurred(errorTitle, errorDesc);
					
					log("Se dispara el evento que informa del error ocurrido. " + errorTitle + "-" + errorDesc);
					
					// Se cancela la trasancción.
					if (manageTrx){
						getTrx().rollback();
						getTrx().close();
						log("Cancela transaccion.");
					}
					// Se guarda el mensaje de error.
					setErrorMsg("@" + errorTitle + "@ - @" + errorDesc + "@");
				}
				else{
					// Se dispara el evento de pregunta si se imprimió correctamente
					log("Se dispara el evento de pregunta si se imprimió correctamente.");
					fireDocumentPrintAsk(errorTitle, errorDesc, newPrinterStatus);
				}
			} else {
				// Se efectiviza la transacción solo si no ocurrió un error.
				if (manageTrx) {
					getTrx().commit();
					getTrx().close();
					
					log("Efectiviza la transaccion.");
				}
			}
			
			if (!error
					|| (error && !ask())) {
				
				log("Cierra la impresora fiscal.");
				closeFiscalPrinter();
				
			}
		}
		
		log("Retorna ejecucion de accion: " + !error);
		
		return !error;
	}

	/**
	 * Setea el estado de la impresora y devuelve el mismo
	 * @return <code>status</code> 
	 * 
	 * dREHER - 
	 * @throws Exception 
	 */
	public String getAndSaveStatus(MControladorFiscal fiscal) throws Exception {
		
		String status = MControladorFiscal.STATUS_IDLE;
		
		// Se obtiene el controlador fiscal para chequear el status
		cFiscal = fiscal;
		log("Controlador Fiscal: " + cFiscal);
		
		// Se obtiene la impresora fiscal con la que se debe imprimir
		// el documento segun su tipo de documento.
		FiscalPrinter fiscalPrinter = cFiscal.getFiscalPrinter();
		setFiscalPrinter(fiscalPrinter);
		log("Se carga la impresora a utilizar. " + fiscalPrinter);
		
		// Chequeo el estado de la impresora
		if(!checkPrinterStatus(cFiscal)) {
			status = MControladorFiscal.STATUS_ERROR;
		}
					
		// Se informa al manejador que se esta intentando conectar con
		// la impresora fiscal.
		fireActionStarted(FiscalDocumentPrintListener.AC_CONNECT_PRINTER);
		fiscalPrinter.setEventListener(getPrinterEventListener());
		log("Se informa al manejador que se esta intentando conectar con la impresora fiscal");
					
		// Se intenta conectar la impresora.
		try {
			getFiscalPrinter().connect();
			log("Se conecto con la impresora fiscal");
		}catch(Exception ex) {
			status = MControladorFiscal.STATUS_ERROR;
		}
		
		// Se libera la impresora fiscal.
		setFiscalPrinterStatus(cFiscal, status);
				
		// Se efectiviza la transacción solo si no ocurrió un error.
		if (getTrx() != null && isCreateTrx()) {
			getTrx().commit();
			getTrx().close();
		}
				
		try {
			closeFiscalPrinter();
		}catch(Exception ex) {
			status = MControladorFiscal.STATUS_ERROR;
		}
		
		log("Resultado de la accion. Status fiscal printer: " + status);
		return status;
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
			case ACTION_PRINT_DOCUMENT:		     		doPrintDocument(args); break;
			case ACTION_FISCAL_CLOSE:		     		doFiscalClose(args); break;
			case ACTION_PRINT_DELIVERY_DOCUMENT: 		doPrintDeliveryDocument(args); break;
			case ACTION_PRINT_CURRENT_ACCOUNT_DOCUMENT:	doPrintCurrentAccountDocument(args); break;
			case ACTION_OPEN_DRAWER:					doOpenDrawer(args); break;
			case ACTION_GET_INIT_DATA:					doGetInitData(args); break;
			case ACTION_FISCAL_AUDIT:		     		doFiscalAudit(args); break; // dREHER
			case ACTION_FISCAL_REPORT_AUDIT:		    doFiscalReportAudit(args); break; // dREHER
			default:						throw new Exception(Msg.getMsg(ctx, "InvalidAction"));
		}
	}
	
	/**
	 * Deja log en consola para seguimiento de ejecucion de acciones...
	 * @param msg
	 * dREHER
	 */
	private void log(String msg) {
		System.out.println("FiscalDocumentPrint. " + msg);
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
	 * 
	 * 
	 * dREHER - Desde TPV se invoca este metodo de impresion...
	 */
	public boolean printDocument(PO document) {
		
		log("printDocument (PO document)");
		
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
		log("Controlador Fiscal: " + cFiscal);
		
		// Ejecutar la acción
		log("Ejecuta la accion: " + Actions.ACTION_PRINT_DOCUMENT);
		boolean ok = execute(Actions.ACTION_PRINT_DOCUMENT, cFiscal.getID(), new Object[]{document});
		
		/**
		 * Si se trata de un controlador de segunda generacion, verificar si los importes del documento
		 * coinciden con los del controlador, caso contrario excepcionar...
		 * 
		 * dREHER
		 */
		// TODO: ver como obtener el Invoice Original como para crear un Document tipo Nota de Credito
		if(!checkAmounts(null, null)) {
			endPrintingWrong("Error",
					"No coinciden los montos impresos con los montos Libertya", MControladorFiscal.STATUS_ERROR);
			ok = false;
		}
		
		// Se actualizan los datos del documento oxp.
		log("Se actualizan los datos del documento oxp.: " + !ok);
		updateOxpDocument((MInvoice)document, !ok);
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		
		log("Resultado de la accion: " + ok);
		return ok;
	}
	
	/**
	 * devuelve el ultimo numero impreso en una impresora fiscal. 
	 * @param document <code>PO</code> que representa el documento a imprimir.
	 * @return <code>lastNo</code> en caso de que el documento se haya emitido
	 * 
	 * dREHER - 
	 * @throws Exception 
	 */
	public Integer getLastNoPrinted(PO document) throws Exception {
		
		log("FiscalDocumentPrint. getLastNoPrinted (PO document)");
		
		Integer lastNo = -1;
		
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
		cFiscal = MControladorFiscal.getOfDocType(docType_ID);
		log("Controlador Fiscal: " + cFiscal);
		
		// Se obtiene la impresora fiscal con la que se debe imprimir
		// el documento segun su tipo de documento.
		FiscalPrinter fiscalPrinter = cFiscal.getFiscalPrinter();
		setFiscalPrinter(fiscalPrinter);
		log("Se carga la impresora a utilizar. " + fiscalPrinter);
		
		// Chequeo el estado de la impresora
		if(!checkPrinterStatus(cFiscal)) {
			log.warning("Volvio del chequeo de impresora fiscal con ERROR!");
			return -3;
		}
					
		// Se informa al manejador que se esta intentando conectar con
		// la impresora fiscal.
		fireActionStarted(FiscalDocumentPrintListener.AC_CONNECT_PRINTER);
		fiscalPrinter.setEventListener(getPrinterEventListener());
		log("Se informa al manejador que se esta intentando conectar con la impresora fiscal");
					
		// Se intenta conectar la impresora.
		getFiscalPrinter().connect();
		log("Se conecto con la impresora fiscal");
		
		/**
		 *  Validar si el ultimo numero impreso de este tipo de documento es mayor a igual al imprimir, en cuyo caso NO deberia volver a imprimir para las
		 *  dREHER
		 */
		// impresoras NO termicas (fiscales)
		MInvoice mInvoice = (MInvoice)document;
		if(!mInvoice.isThermalFiscalPrint(mInvoice.getC_DocTypeTarget_ID())) {
			
			Document docFiscalImprimible = getDocument();
			
			if(docFiscalImprimible==null) {
				log.warning("No se pudo recuperar el documentPrintable, intenta crearlo");
				docFiscalImprimible = this.createDocument((MInvoice)getOxpDocument(), null);
			}
			
			if(docFiscalImprimible==null) {
				log.warning("No se pudo recuperar ni crear el documentPrintable!");
				return -2;
			}

			try {
				lastNo = getLastPrintedNumber(docFiscalImprimible);
			} catch (FiscalPrinterStatusError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FiscalPrinterIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else
			lastNo = -10;
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		
		// Se libera la impresora fiscal.
		setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);
				
				// Se efectiviza la transacción solo si no ocurrió un error.
		if (getTrx() != null && isCreateTrx()) {
			getTrx().commit();
			getTrx().close();
		}
				
		closeFiscalPrinter();
		
		log("Resultado de la accion. Ultimo comprobante impreso: " + lastNo);
		return lastNo;
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
		
		log("printDocument (PO document, Document documentPrintable, MDocType docType, MInvoice originalInvoice )");
		
		// Se valida que el tipo de documento exista
		if(docType == null)
			throw new IllegalArgumentException("Error: No document type");
		
		// Se obtiene el tipo de documento a emitir por la impresora.
		setPrinterDocType(docType.getFiscalDocument());
		
		// Se asigna el documento OXP.
		setOxpDocument(document);
		
		// Se obtiene el controlador fiscal para chequear el status
		MControladorFiscal cFiscal = MControladorFiscal.getOfDocType(docType.getID());
		log("Controlador Fiscal: " + cFiscal);
		
		// Ejecutar la acción
		log("Ejecuta la accion: " + Actions.ACTION_PRINT_DOCUMENT);
		boolean ok = execute(Actions.ACTION_PRINT_DOCUMENT, cFiscal.getID(),
				new Object[] { document, documentPrintable, originalInvoice });

		/**
		 * Si se trata de un controlador de segunda generacion, verificar si los importes del documento
		 * coinciden con los del controlador, caso contrario excepcionar...
		 * 
		 * dREHER
		 */
		if(!checkAmounts(documentPrintable, originalInvoice)) {
			endPrintingWrong("Error",
					"No coinciden los montos impresos con los montos Libertya", MControladorFiscal.STATUS_ERROR);
			ok = false;
		}
		
		// Se actualizan los datos del documento oxp.
		log("Se actualizan los datos del documento oxp.: " + !ok);
		updateOxpDocument((MInvoice)document, !ok);
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		log("Resultado de la accion: " + ok);
		return ok;
	}
	
	/**
	 * Realiza la impresión de la factura con los parámetros correspondientes.
	 * @param args
	 * @throws Exception
	 */
	private void doPrintDocument(Object[] args) throws Exception{
		
		log("printDocument (Object[] args)");
		
		setAskMoment(true);
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
		
		// Inicializar flag para que siempre se abra el cajón de dinero
		initAlwaysOpenDrawer();
		
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
		
		/**
		 * Si se trata de un controlador de segunda generacion, verificar si los importes del documento
		 * coinciden con los del controlador, caso contrario excepcionar...
		 * 
		 * dREHER
		 */
		if(!checkAmounts(documentPrintable, originalInvoice)) {
			endPrintingWrong("Error",
					"No coinciden los montos impresos con los montos Libertya", MControladorFiscal.STATUS_ERROR);
			return;
		}

		// Se dispara el evento de impresión finalizada.
		fireDocumentPrintEndedOk();
		
		// dREHER TODO: verificar si este metodo debe ser ejecutado, ya que si no coincide numero de controlador fiscal, con numero Libertya podria ocacionar problemas...
		// Se actualiza la secuencia del tipo de documento emitido.
		updateDocTypeSequence(document);
	}
	
	/**
	 * En caso de tratarse de controladores fiscales de segunda generacion, validar si el monto
	 * que se envio al documento fiscal, es el mismo que el documento Libertya (Invoice)
	 * 
	 * TODO: ver si se puede leer desde la impresora fiscal...
	 * 
	 * ConsultarAcumuladoComprobante(tipo, numero) -> esto puede devolver en respuesta.getTotal()
	 * Esta info se puede obtener luego de cerrado el documento fiscal, no deberia llamarse este metodo hasta realizar el cierre del ticket...
	 * 
	 * 
	 * @return boolean true->coinciden valores del documento impreso y controlador fiscal
	 */
	private boolean checkAmounts(Document documentPrintable, MInvoice original) {
		boolean isOk = true;
		BigDecimal fiscalAmount = null;

		// TODO: solo comparar en las de 2da generacion ?
		if(is2daGeneracion()) {

			Document docFiscalImprimible = documentPrintable;
			
			if(docFiscalImprimible==null)
				docFiscalImprimible = getDocument();
			
			if(docFiscalImprimible==null) {
				log.warning("No se pudo recuperar el documentPrintable, intenta crearlo");
				docFiscalImprimible = this.createDocument((MInvoice)getOxpDocument(), original);
			}

			if(docFiscalImprimible==null) {
				log.warning("No se pudo recuperar ni crear el documentPrintable!");
				return false;
			}
			
			fiscalAmount = docFiscalImprimible.getTotal();
			if(fiscalAmount == null)
				fiscalAmount = Env.ZERO;
			
			log.info("Total en el documento fiscal imprimible: " + fiscalAmount);
			
			if(fiscalAmount.compareTo(Env.ZERO)!=0) {
				if(getOxpDocument()!=null) {
					MInvoice invoice = (MInvoice)getOxpDocument();
					log.info("Comprobante a imprimir:" + invoice.getDocumentNo());
					if(invoice.getGrandTotal().setScale(2, RoundingMode.DOWN).compareTo(fiscalAmount.setScale(2, RoundingMode.DOWN)) != 0) {
						isOk = false;
						log.warning("Total Libertya: " + invoice.getGrandTotal());
					}else
						log.info("Los totales del comprobante Fiscal y Libertya son iguales!");
				}else
					log.warning("No se puede leer el comprobante a imprimir!");
			}else
				log.warning("No se pudo recuperar total del comprobante fiscal!");
		}
		
		return isOk;
	}
	
	/**
	 * TODO: encontrar un metodo mas efectivo para determinar si se trata de un controlador fiscal de 2da generacion
	 * 
	 * @return true -> segunda generacion
	 * dREHER
	 */
	private boolean is2daGeneracion() {
		boolean is2da = false;
		
		if(cFiscal == null)
			return false;
		
		
		// TODO: Segun soporte, YA no se utilizan mas las de 1ra generacion, todas pasarian a ser de segunda generacion
		// El regimen vigente prohibe las de 1ra generacion a partir de Junio 2022 - verificar esto!
		// 16/01/2023
		if(cFiscal.getControladorFiscalType().equals(X_C_Controlador_Fiscal.CONTROLADORFISCALTYPE_Fiscal))
			return true;
		
		X_C_Controlador_Fiscal_Type cType = new X_C_Controlador_Fiscal_Type(Env.getCtx(), cFiscal.getC_Controlador_Fiscal_Type_ID(), getTrxName());
		String className = cType.getclazz();
		
		if(cFiscal.getPrinterName()!=null)
			if(cFiscal.getPrinterName().indexOf("2G") > -1 ||
				className.indexOf("2G") > -1)
					is2da = true;
		
		return is2da;
	}
	
	/**
	 * Inicialización de flag que determina si se debe abrir siempre el cajón de
	 * dinero al imprimir un documento
	 */
	private void initAlwaysOpenDrawer(){
		String alwaysOpenDrawerPreferenceValue = MPreference
				.searchCustomPreferenceValue(
						ALWAYS_OPEN_DRAWER_PREFERENCE_VALUE,
						Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(ctx), 
						Env.getAD_User_ID(ctx), true);
		setAlwaysOpenDrawer("Y".equals(alwaysOpenDrawerPreferenceValue));
		
		if("Y".equals(alwaysOpenDrawerPreferenceValue))
			log.info("FiscalDocumentPrint. Abrir siempre cajon. AD_Preference - FiscalPrinter_Always_Open_Drawer=Y");
	}

	// *****************************************************
	// 		AUDITORIA FISCAL NO SE PRESENTA COMO DDJJ A AFIP
	// *****************************************************
		
	
	/**
	 * Envia el comando de consulta de auditoria
	 * @param cFiscalID Impresora a la cual enviar el comando
	 * @param fecha desde y hasta 
	 * @return verdadero en caso de exito, falso si hubo algún problema
	 * 
	 * dREHER
	 * @throws Exception 
	 */
	public boolean fiscalAudit(Integer cFiscalID, Timestamp fechaDesde, Timestamp fechaHasta) {
		
		/*
		// Get the printer 
		cFiscal = new MControladorFiscal(ctx, cFiscalID, getTrxName());
		FiscalPrinter fiscalPrinter = cFiscal.getFiscalPrinter();
		setFiscalPrinter(fiscalPrinter);
		
		// Ejecutar la acción
		return doFiscalAudit(new Object[]{fechaDesde, fechaHasta});
		*/
		
		return execute(Actions.ACTION_FISCAL_AUDIT, cFiscalID, new Object[]{fechaDesde, fechaHasta});
	}

	/**
	 * Consulta los bloques de auditoria con los parámetros dados
	 * @param args arreglo de parámetros del procedimiento
	 * @throws Exception
	 * 
	 * dREHER
	 */
	private boolean doFiscalAudit(Object[] args) throws Exception{
		
		if(!is2daGeneracion()) {
			System.out.println("Solo valido para Hasar 2da generacion!");
			return false;
		}
		
		if(getFiscalPrinter()==null) {
			System.out.println("No se especifico impresora fiscal!");
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		
		// Argumentos
		Timestamp fechaDesde = (Timestamp)args[0];
		Timestamp fechaHasta = (Timestamp)args[1];
		
		String sDesde = sdf.format(fechaDesde);
		String sHasta = sdf.format(fechaHasta);
		
		fireActionStarted(FiscalDocumentPrintListener.AC_EXECUTING_ACTION);
		
		// Consultar los bloques de auditoria en formato XML
		StringBuilder res = getFiscalPrinter().fiscalAudit(sDesde, sHasta);
		
		String file = "";
		if(res!=null && res.length() > 0) {
			file = org.openXpertya.util.Utils.convertToFile(res, sDesde, sHasta, cFiscal.getName());
			if(file!=null) {
				
				xmlParser parser = new xmlParser(file);
				parser.setDesde(sDesde);
				parser.setHasta(sHasta);
				parser.getNotShow().add("AtributosImpresion");
				parser.getNotShow().add("Emisor");
				file = file + "<br>" + parser.parsear();
				
			}
				
		}
		setErrorMsg("Se crearon los archivos: " + file);
		
		// Guardar la respuesta 
		// saveFiscalClosing(closeType, getFiscalPrinter().getLastResponse());
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_FISCAL_AUDIT);
		
		return true;
	}

// ---------------------------------------------------------------------------------------------------------------	
	
	// ****************************************************************
	// 		REPORTE DE AUDITORIA FISCAL NO SE PRESENTA COMO DDJJ A AFIP
	// ****************************************************************
		
	
	/**
	 * Envia el comando de consulta de reporte de auditoria
	 * @param cFiscalID Impresora a la cual enviar el comando
	 * @param fecha desde y hasta 
	 * @return verdadero en caso de exito, falso si hubo algún problema
	 * 
	 * dREHER
	 * @throws Exception 
	 */
	public boolean fiscalReportAudit(Integer cFiscalID, Timestamp fechaDesde, Timestamp fechaHasta, boolean completo) {
	
		return execute(Actions.ACTION_FISCAL_REPORT_AUDIT, cFiscalID, new Object[]{fechaDesde, fechaHasta, completo});
	}

	/**
	 * Consulta los bloques de reportes de auditoria con los parámetros dados
	 * @param args arreglo de parámetros del procedimiento
	 * @throws Exception
	 * 
	 * dREHER
	 */
	private boolean doFiscalReportAudit(Object[] args) throws Exception{
		
		if(!is2daGeneracion()) {
			System.out.println("Solo valido para Hasar 2da generacion!");
			return false;
		}
		
		if(getFiscalPrinter()==null) {
			System.out.println("No se especifico impresora fiscal!");
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		
		// Argumentos
		Timestamp fechaDesde = (Timestamp)args[0];
		Timestamp fechaHasta = (Timestamp)args[1];
		
		String sDesde = sdf.format(fechaDesde);
		String sHasta = sdf.format(fechaHasta);
		
		fireActionStarted(FiscalDocumentPrintListener.AC_EXECUTING_ACTION);
		
		// Consultar los bloques de auditoria en formato ZIP
		// Esto devuelve ASCII85 habria que convertir a binario y luego renombrar a ZIP
		StringBuilder res = getFiscalPrinter().fiscalReportAudit(sDesde, sHasta);
		
		String file = "";
		if(res!=null && res.length() > 0) {
			file = org.openXpertya.util.Utils.convertToFile(res, "ReporteAuditoria_" +
					cFiscal.getName().replace(" ", "_") + "_" +
					sDesde + "_" + sHasta + 
					".zip");
			if(file!=null) {
				

				// Decodifica el contenido ASCII85 a binario
			    // byte[] binaryData = org.openXpertya.util.Utils.decodeASCII85(zip.toString());
				
				/*
			    org.openXpertya.util.Utils.convertToFile(zip, "ReporteAuditoria_" +
						cFiscal.getName().replace(" ", "_") + "_" +
						sDesde + "_" + sHasta + 
						".zip");
				*/
			    /*
			    org.openXpertya.util.Utils.convertToFile(binaryData, "Auditoria_" +
						cFiscal.getName().replace(" ", "_") + "_" +
						sDesde + "_" + sHasta + 
						".zip");
				*/
			}
				
		}
		setErrorMsg("Se crearon los archivos: " + file);
		
		// Guardar la respuesta 
		// saveFiscalClosing(closeType, getFiscalPrinter().getLastResponse());
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_FISCAL_REPORT_AUDIT);
		
		return true;
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
		
		// Guardar la respuesta 
		saveFiscalClosing(closeType, getFiscalPrinter().getLastResponse());
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_FISCAL_CLOSE);
	}

	// **************************************************************
	//   IMPRESION DE DOCUMENTO CON ARTICULOS PENDIENTES DE ENTREGA		
	// **************************************************************

	/**
	 * Manda a imprimir un documento NO fiscal que en sus líneas contiene el
	 * detalle de cada artículo que tiene alguna cantidad pendiente de entrega
	 * dentro de un pedido del sistema.
	 * 
	 * @param cFiscalID
	 *            Impresora que emite el documento
	 * @param order
	 *            Pedido del cual se obtienen los artículos pendientes de
	 *            entrega
	 * @param invoice
	 *            factura impresa anteriormente
	 * @return <code>true</code> en caso de que el documento se haya emitido
	 *         correctamente, <code>false</false> en caso contrario.
	 */
	public boolean printDeliveryDocument(Integer cFiscalID, MOrder order, MInvoice invoice) {
		return execute(Actions.ACTION_PRINT_DELIVERY_DOCUMENT, cFiscalID,
				new Object[] { order, invoice });
	}
	
	/**
	 * Realiza la impresión del documento no fiscal con los artículos a entregar.
	 * @param args Arreglo con los argumentos requeridos por esta funcionalidad
	 * @throws Exception
	 */
	private void doPrintDeliveryDocument(Object[] args) throws Exception {
		MOrder order = (MOrder) args[0];
		MInvoice invoice = (MInvoice) args[1];
		setOxpDocument(invoice);
		// Informa el inicio de la impresión
		fireActionStarted(FiscalDocumentPrintListener.AC_PRINT_DOCUMENT);
		// Crea el documento no fiscal y luego obtiene todas las líneas del pedido
		NonFiscalDocument nonFiscalDocument = new NonFiscalDocument();
		// Información de la compañía/organización
		nonFiscalDocument.setClientOrgInfo(getClientOrgInfo(invoice));
		MOrderLine[] orderLines = order.getLines(true);
		String line = null;

		/**
		 * Texto al pie del documento de salida de deposito
		 * dREHER
		 */
		List<String> footer = new ArrayList<String>();
		footer.add("  Las mercaderias deben ser retiradas");
		footer.add("dentro de los 90 dias excepto materiales");
		footer.add("  de construccion pesada, que podran");
		footer.add(" hacerlo dentro de los 24 meses, y solo");
		footer.add("seran entregadas al titular de la compra");
		footer.add("     o a la persona autorizada.");

		/** Unica linea footer
		String textoSalida = "Salida de Deposito";		
		String tmp = MPreference.GetCustomPreferenceValue("TextoSalidaDeposito", Env.getAD_Client_ID(ctx));
		if(tmp != null) {
			if(tmp.length()>40)
				tmp = tmp.substring(0, 40);
			textoSalida = tmp;
		}
		*/
		
		/**
		 * Se modifica el mensaje "Salida de Deposito" por "Número de Salida de Depósito exclusiva para entrega"
		 * dREHER
								   ******* Numero de Salida Depósito ****** 
								   ******** Exclusiva para Entrega ********
			cada linea tiene 40 caracteres
		*/
		nonFiscalDocument.addLine("****************************************");
		nonFiscalDocument.addLine("***** "+Msg.getMsg(ctx, "Numero de Salida de Deposito")+" *****");
		nonFiscalDocument.addLine("******** "+Msg.getMsg(ctx, "Exclusiva para Entrega")+" ********");
		nonFiscalDocument.addLine("**** "+Msg.getMsg(ctx, "VoucherNo")+":["+invoice.getDocumentNo()+"] ***");
		nonFiscalDocument.addLine("****************************************");
		
		/** Agrego Nombre del cliente y DNI */
		String name = order.getNombreCli();
		String nroDoc = order.getNroIdentificCliente();
		if(name==null || name.isEmpty()) {
			MBPartner bp = new MBPartner(ctx, order.getC_BPartner_ID(), null);
			name = bp.getName();
			nroDoc = bp.getTaxID();
			if(nroDoc==null)
				nroDoc = "";
			nroDoc = nroDoc.trim();
			name = name.substring(0, 40-(nroDoc.length()+2));
		}

		nonFiscalDocument.addLine(name + "- " + nroDoc);
		nonFiscalDocument.addLine("****************************************");
		
		for (MOrderLine orderLine : orderLines) {
			// Por cada línea, si tiene artículos pendientes de entrega
			if (orderLine.isDeliverDocumentPrintable()) {
				// Obtiene la cantidad que falta entregar
				BigDecimal qtyToDeliver = ReservedUtil.getOrderLinePending(orderLine);
				MProduct product = orderLine.getProduct();
				// Primera línea a imprimir: [cantidad] value del artículo 
				line = 
					"[" + qtyToDeliver.setScale(2) + "] " + product.getValue();
				// Agrega la línea al documento no fiscal
				nonFiscalDocument.addLine(line);
				// Segunda línea a imprimir: el nombre el del artículo
				line = product.getName();
				// Agrega la línea al documento no fiscal
				nonFiscalDocument.addLine(line);
			}
		}
		nonFiscalDocument.addLine("****************************************");
		// NO VA nonFiscalDocument.addLine(textoSalida); // dREHER
		for(String foot: footer) {
			nonFiscalDocument.addLine(foot);
		}
		nonFiscalDocument.addLine(" ");
		
		// Comentarios del pie del ticket
		setStdFooterObservations(invoice, nonFiscalDocument);
		
		// Manda a imprimir el documento en la impresora fiscal
		getFiscalPrinter().printDocument(nonFiscalDocument);
		
		// Se dispara el evento de impresión finalizada.
		fireDocumentPrintEndedOk();
	}
	
	// **************************************************************
	//   IMPRESION DE DOCUMENTO CON DATOS DE CUENTA CORRIENTE		
	// **************************************************************

	/**
	 * Manda a imprimir un documento NO fiscal que posee la información del
	 * cliente en cuenta corriente
	 * 
	 * @param cFiscalID
	 *            Impresora que emite el documento
	 * @param bpartner
	 *            cliente cuenta corriente
	 * @param invoice
	 *            factura actual
	 * @param infos
	 *            datos de las condiciones de venta de cuenta corriente
	 * @return <code>true</code> en caso de que el documento se haya emitido
	 *         correctamente, <code>false</false> en caso contrario.
	 */
	public boolean printCurrentAccountDocument(Integer cFiscalID, MBPartner bpartner, MInvoice invoice, List<CurrentAccountInfo> infos) {
		setOxpDocument(invoice);
		// Creo el customer
		Customer customer = getCustomer(bpartner.getID());
		/* Por lo pronto, no es necesaria toda esta información
		MOrg org = MOrg.get(ctx, Env.getAD_Org_ID(ctx));
		CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
		// Estado de crédito
		CallResult result = manager.getCreditStatus(ctx, org, bpartner, null, getTrxName());
		customer.setCreditStatus((String) result.getResult());
		
		// Límite
		result = manager.getCreditLimit(ctx, org, bpartner, null, getTrxName());
		customer.setCreditLimit((BigDecimal)result.getResult());
		
		// Saldo
		result = manager.getTotalOpenBalance(ctx, org, bpartner, null, getTrxName());
		customer.setCreditBalance((BigDecimal)result.getResult());
		*/
		// Itero por los datos de cuenta corriente y le asigno el cliente
		for (CurrentAccountInfo currentAccountInfo : infos) {
			currentAccountInfo.setCustomer(customer);
		}
		return execute(Actions.ACTION_PRINT_CURRENT_ACCOUNT_DOCUMENT,
				cFiscalID, new Object[] { invoice, infos });
	}

	/**
	 * Realiza la impresión del documento no fiscal con los datos del cliente de
	 * cuenta corriente
	 * 
	 * @param args
	 *            Arreglo con los argumentos requeridos por esta funcionalidad
	 * @throws Exception
	 */
	private void doPrintCurrentAccountDocument(Object[] args) throws Exception{
		MInvoice invoice = (MInvoice) args[0];
		MPaymentTerm paymentTerm = new MPaymentTerm(invoice.getCtx(),
				invoice.getC_PaymentTerm_ID(), getTrxName());
		String onCreditPaymentRuleDescr = MRefList.getListName(
				invoice.getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
				MInvoice.PAYMENTRULE_OnCredit);
		List<CurrentAccountInfo> infos = (List<CurrentAccountInfo>)args[1];
		// Si no existen datos de cuenta corriente no se imprime nada
		if(infos == null || infos.size() == 0){
			return;
		}
		// Informa el inicio de la impresión
		fireActionStarted(FiscalDocumentPrintListener.AC_PRINT_DOCUMENT);
		// Crea el documento no fiscal y luego obtiene todas las líneas del pedido
		NonFiscalDocument nonFiscalDocument = new NonFiscalDocument();
		// Información de la compañía/organización
		nonFiscalDocument.setClientOrgInfo(getClientOrgInfo(invoice));
		// Cargar datos del cliente que se obtiene de por lo menos el primer
		// registro de los balances. Se sabe que para todos es el mismo cliente
		Customer customer = infos.get(0).getCustomer();
		String currentAccountMsg = Msg.getMsg(ctx, "CurrentAccount");
		nonFiscalDocument.addLine("****************************************");
		nonFiscalDocument.addLine("** "+Msg.getMsg(ctx, "CurrentAccountTicketAcceptanceLine")+" **");
		nonFiscalDocument.addLine("*********** "+currentAccountMsg+" ***********");
		nonFiscalDocument.addLine("****************************************");
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "VoucherNo")+": "+invoice.getDocumentNo());
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "Date")
				+ ": "
				+ new SimpleDateFormat("dd/MM/yyyy").format(invoice
						.getDateInvoiced()));
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "Account")+": "+customer.getValue()+" "+customer.getName());
		MCurrency currency = MCurrency.get(ctx, Env.getContextAsInt(ctx, "$C_Currency_ID"), getTrxName());
		for (CurrentAccountInfo currentAccountInfo : infos) {
			// Si la línea es a crédito, entonces va el nombre del esquema de
			// vencimientos de la factura
			if (currentAccountInfo.getPaymentRule().equals(
					onCreditPaymentRuleDescr)) {
				nonFiscalDocument.addLine(paymentTerm.getName());
			}
			else{
				nonFiscalDocument.addLine(currentAccountInfo.getPaymentRule());
			}
			nonFiscalDocument.addLine(Msg.getMsg(ctx, "Amt") + ":  "
					+ currency.getCurSymbol() + currentAccountInfo.getAmount());
		}
		/* Por ahora no es necesario imprimir esta información
		// Imprimir estado de crédito
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "Status")
				+ ": "
				+ MRefList.getListName(ctx,
						MBPartner.SOCREDITSTATUS_AD_Reference_ID,
						customer.getCreditStatus()));
		// Imprimir límite
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "Limit") + ": "
				+ currency.getCurSymbol() + customer.getCreditLimit());
		// Imprimir saldo
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "Balance") + ": "
				+ currency.getCurSymbol() + customer.getCreditBalance());
		*/
		// Imprimir línea para firma del cliente
		nonFiscalDocument.addLine(" ");
		nonFiscalDocument.addLine(" ");
		nonFiscalDocument.addLine("--------------------------");
		nonFiscalDocument.addLine(Msg.getMsg(ctx, "SignatureAndClarification"));
		nonFiscalDocument.addLine(" ");
		// Comentarios del pie del ticket
		setStdFooterObservations(invoice, nonFiscalDocument);
		
		// FIXME Excepciones de crédito?
		// Manda a imprimir el documento en la impresora fiscal
		getFiscalPrinter().printDocument(nonFiscalDocument);
		// Se dispara el evento de impresión finalizada.
		fireDocumentPrintEndedOk();
	}
	
	// **************************************************************
	//   			APERTURA DE CAJÓN DE DINERO		
	// **************************************************************
	
	/**
	 * @param cFiscalID
	 *            impresora fiscal
	 * @return true si se ejecutó correctamente, falso en caso contrario
	 */
	public boolean openDrawer(Integer cFiscalID) {
		return execute(Actions.ACTION_OPEN_DRAWER, cFiscalID,
				new Object[] { });
	}
	
	/**
	 * @param args argumentos
	 * @throws Exception en caso de error
	 */
	public void doOpenDrawer(Object[] args) throws Exception{
		fireActionStarted(FiscalDocumentPrintListener.AC_EXECUTING_ACTION);
		
		// Abrir el cajón de dinero
		getFiscalPrinter().openDrawer();
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_OPEN_DRAWER);
	}

	// **************************************************************
	//   		OBTENER INFORMACIÓN DE INICIALIZACIÓN
	// **************************************************************

	/**
	 * @param cFiscalID
	 *            impresora fiscal
	 * @return true si se ejecutó correctamente, falso en caso contrario
	 */
	public boolean getInitData(Integer cFiscalID) {
		return execute(Actions.ACTION_GET_INIT_DATA, cFiscalID,
				new Object[] { });
	}
	
	/**
	 * @param args argumentos
	 * @throws Exception en caso de error
	 */
	public void doGetInitData(Object[] args) throws Exception{
		fireActionStarted(FiscalDocumentPrintListener.AC_EXECUTING_ACTION);
		
		// Ejecutar el comando de obtención de datos de inicialización de la
		// impresora
		getFiscalPrinter().getInitData();
		
		// Decodificar la respuesta del comando para quedarnos con el DTO 
		setFiscalInitData(getFiscalPrinter().decodeInitData(getFiscalPrinter().getLastResponse()));
		
		// Se dispara el evento de acción finalizada.
		fireActionEndedOk(Actions.ACTION_GET_INIT_DATA);
	}
	
	// **************************************************************
	
	public void endPrintingOK() throws Exception{
		
		/**
		 * Si viene con excepcion, devolver final de impresion con error y continuar con mensaje al usuario...
		 * dREHER
		 */
		try {
		
			saveDocumentData((MInvoice)getOxpDocument(), getDocument());
		
		}catch(Exception ex) {
			endPrintingWrong("Error",
					ex.toString(), MControladorFiscal.STATUS_ERROR);
			return;
		}
		
		// Se actualizan los datos del documento oxp.
		updateOxpDocument((MInvoice)getOxpDocument(), false);
		
		// reset documento oxp y tipo de doc de la impresora
		//setOxpDocument(null);
		setPrinterDocType(null);
		
		fireDocumentPrintEndedOk();


		/**
		 * Termino impresion, verificar si el total del documento se condice con el documento Libertya
		 * 
		 * getOXPDocument -> MInvoice Libertya : getDocument() -> Invoice Fiscal
		 * 
		 * Ambos totales deben ser iguales, TODO: ver posibilidad de enviar comando a la impresora fiscal para validar si el total del document impreso es igual
		 * al total del comprobante Libertya Origen (getOXPDocument)
		 * 
		 * Hasar 2G, comando: ConsultarAcumuladosComprobante, devuelve total del comprobante en la posicion 6
		 * Doc: CF 2G Manual de Comandos Rev004.pdf
		 * 
		 * Comando a enviar:
		 * 
		 * 	STX
			SN
			ESC
			8C hexa/140 decimal
			FS
		1 	Código de Comprobante
			FS
		2	Número del comprobante
			ETX
			BCC
			
			Ej: [STX]1[ESC][8CH][FS]81[FS]3[ETX]00B1
			
		 *  Respuesta:
		 *  	
		 *  Ejemplo:
			[STX]1[ESC][8CH]FS]0000[FS]0000[FS]1[FS]81[FS]00000003[FS]00000003[FS]00000[FS]2035.00[FS
			]706.65[FS]381.31[FS]661.01[FS]130.35[FS]155.68[FS]10.50[FS]8.47[FS]80.66[FS]21.00[FS]121.08[
			FS]580.35[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.0
			0[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.00[FS]0.0
			0[FS]0.00[FS]10[FS]80.68[FS]7[FS]5.00[FS]8[FS]10.00[FS]5[FS]15.00[FS]6[FS]20.00[FS]2[FS]25.00[F
			S]0[FS]0.00[FS]0[FS]0.00[FS]0[FS]0.00[FS]0[FS]0.00[FS]0[FS]0.00[ETX]00D9
		 * 
		 * dREHER
		 */
		
		if(getTotal().setScale(2, RoundingMode.DOWN).compareTo(((MInvoice)getOxpDocument()).getGrandTotal().setScale(2, RoundingMode.DOWN)) != 0) {
			log.warning("Error Monto Libertya vs Monto Fiscal. Libertya=" + ((MInvoice)getOxpDocument()).getGrandTotal().setScale(2, RoundingMode.DOWN) +
					" Fiscal=" + getTotal().setScale(2, RoundingMode.DOWN));
			endPrintingWrong("Error",
					"No coinciden los montos impresos con los montos Libertya.", MControladorFiscal.STATUS_ERROR);
			return;
		}
		
		// Se actualiza la secuencia del tipo de documento emitido.
		updateDocTypeSequence((MInvoice)getOxpDocument());
		
		// Se libera la impresora fiscal.
		setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);
		
		// Se efectiviza la transacción solo si no ocurrió un error.
		if (getTrx() != null && isCreateTrx()) {
			getTrx().commit();
			getTrx().close();
		}
		
		closeFiscalPrinter();
		
	}
	
	/**
	 * Permite consultar los totales acumulados en un documento fiscal
	 *  
	 * @param docType
	 * @param nroComprobante
	 * @return paquete de info recibida desde el controlador fiscal
	 * 
	 * dREHER
	 */
	public BigDecimal getTotal() {
		
		BigDecimal total = Env.ZERO;
		
		try {
			if(cFiscal.getFiscalPrinter() instanceof HasarFiscalPrinter2G && document != null ) {
				HasarFiscalPrinter2G h2g = (HasarFiscalPrinter2G)cFiscal.getFiscalPrinter();
				
				int nroComprobante = Integer.parseInt(document.getDocumentNo());
				
				total = h2g.getTotal(document.getDocumentType(), nroComprobante);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return total;
	}
	
	public void endPrintingWrong(String errorTitle,
			String errorDesc, String printerStatus) {
		try{
			getFiscalPrinter().cancelCurrentDocument();
		} catch(Exception e){
			e.printStackTrace();
		}
		// Se asigna el nuevo estado de la impresora.
		setFiscalPrinterStatus(cFiscal, printerStatus);
		// Se dispara el evento que informa del error ocurrido.
		fireErrorOcurred(errorTitle, errorDesc);
		// Se cancela la trasancción.
		if (getTrx() != null && isCreateTrx()){
			getTrx().rollback();
			getTrx().close();
		}
		// Se guarda el mensaje de error.
		setErrorMsg("@" + errorTitle + "@ - @" + errorDesc + "@");
		
		closeFiscalPrinter();
	}
	
	protected void closeFiscalPrinter(){
		try{
			// Se desconecta la impresora en caso de que este conectada aún.
			if (getFiscalPrinter() != null
					&& getFiscalPrinter().isConnected())
				getFiscalPrinter().close();
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
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
	 * Agrega las observaciones standard a la cola del ticket para ser
	 * imprimibles
	 */
	protected void setStdFooterObservations(MInvoice invoice, Document document){
		// Agregar la descripción fiscal del comprobante
		document.addFooterObservation(Util.isEmpty(
				invoice.getFiscalDescription(), true) ? "-" : invoice
				.getFiscalDescription());
		// Si tiene relacionado una caja diaria, entonces se agrega el nombre de
		// la config del tpv y el usuario asociados a la caja		
		if(!Util.isEmpty(invoice.getC_POSJournal_ID(), true)){
			MPOSJournal posJournal = new MPOSJournal(ctx,
					invoice.getC_POSJournal_ID(), getTrxName());
			MPOS pos = MPOS.get(ctx, posJournal.getC_POS_ID());
			MUser salesRep = MUser.get(ctx, posJournal.getAD_User_ID());
			
			document.addFooterObservation("Cajero:" + salesRep.getName()
					+ "|Caja:" + pos.getName());
		}
		// Sino busca el usuario de ventas desde el documento
		else{
			// Setear el nombre del responsable de ventas
			MUser salesRep = MUser.get(ctx, invoice.getSalesRep_ID());
			document.addFooterObservation("Resp. Ventas: "+ salesRep.getName());
		}
		// Agregar las leyendas del pie del ticket del tipo de documento
		MDocType docType = new MDocType(ctx, invoice.getC_DocTypeTarget_ID(),
				getTrxName());
		if(!Util.isEmpty(docType.getFiscalPrintingFooterLegends(), true)){
			StringTokenizer tokens = new StringTokenizer(
					docType.getFiscalPrintingFooterLegends(), ";");
			String token;
			while (tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				document.addFooterObservation(Util.isEmpty(token, true)?"-":token);
			}
		}
		// Agregar la descripción para categorías de iva
		if (document.getCustomer() != null) {
			if (!Util.isEmpty(document.getCustomer().getCategoriaIVAFiscalDescription(), true)) {
				if(getFiscalPrinter().getFooterTrailerMaxLength() > 0) {
					List<String> lines = StringUtil.splitLines(
							document.getCustomer().getCategoriaIVAFiscalDescription(), " ",
							getFiscalPrinter().getFooterTrailerMaxLength());
					for (String l : lines) {
						document.addFooterObservation(l);
					}
				}
				else {
					document.addFooterObservation(document.getCustomer().getCategoriaIVAFiscalDescription());
				}
			}
		}
	}

	/**
	 * Agrega las observaciones standard a la cola del ticket no fical para ser
	 * imprimibles
	 */
	protected void setStdFooterObservations(MInvoice invoice, NonFiscalDocument nfd){
		// Si tiene relacionado una caja diaria, entonces se agrega el nombre de
		// la config del tpv y el usuario asociados a la caja		
		if(!Util.isEmpty(invoice.getC_POSJournal_ID(), true)){
			MPOSJournal posJournal = new MPOSJournal(ctx,
					invoice.getC_POSJournal_ID(), getTrxName());
			MPOS pos = MPOS.get(ctx, posJournal.getC_POS_ID());
			MUser salesRep = MUser.get(ctx, posJournal.getAD_User_ID());
			
			nfd.addFooterObservation("Cajero:" + salesRep.getName()
					+ "|Caja:" + pos.getName());
		}
		// Sino busca el usuario de ventas desde el documento
		else{
			// Setear el nombre del responsable de ventas
			MUser salesRep = MUser.get(ctx, invoice.getSalesRep_ID());
			nfd.addFooterObservation("Resp. Ventas: "+ salesRep.getName());
		}
	}
	
	/**
	 * Reordenación de leyendas al pie del ticket
	 * @param document
	 */
	protected void reorderFooterObservation(Document document){
		List<String> observationsOrdered = new ArrayList<String>();
		for (String observation : document.getFooterObservations()) {
			if(observation != null && !observation.equals("-")){
				observationsOrdered.add(observation);
			}
		}
		// Si la cantidad de observaciones no llega a 5, agrego los guiones
		// FIXME 5 se toma como límite de cantidad de leyendas al pie del
		// ticket, pueden ser menos o más dependiendo del controlador fiscal
		for (int i = observationsOrdered.size(); i < 5; i++){
			observationsOrdered.add("-");
		}
		document.setFooterObservations(observationsOrdered);
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
		// Información de la compañía/organización
		invoice.setClientOrgInfo(getClientOrgInfo(mInvoice));
		// Se asigna el cliente.
		invoice.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la factura.
		invoice.setLetter(mInvoice.getLetra());
		invoice.setDocumentNo(mInvoice.getDocumentNo());
		
		// Se setea la inclusión del impuesto en el precio
		invoice.setTaxIncluded(MPriceList.get(ctx, mInvoice.getM_PriceList_ID(), getTrxName()).isTaxIncluded());
		
		// CAE
		invoice.setCae(mInvoice.getcae());
		invoice.setCaeDueDate(mInvoice.getvtocae());
		
		// Importe del cargo
		invoice.setChargeAmt(mInvoice.getChargeAmt());
		
		// Setear los mensajes a la cola de la impresión
		setStdFooterObservations(mInvoice, invoice);
		
		// Verificar si esta factura tiene salida por depósito, en ese caso
		// imprimir leyenda al final de la factura
		// FIXME cuando la config del TPV no debe imprimir el documento de
		// retiro por depósito, hay que imprimir esta leyenda en el ticket?
		// En el caso que no haya que hacerlo se debe agregar una variable de
		// instancia boolean en esta clase con ese flag, luego en la factura
		// para setearla desde afuera y que al completar se la setee a esta
		// clase. Luego se debe modificar este if contemplando ese flag 
		
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
				invoice.addFooterObservation(Msg.getMsg(mInvoice.getCtx(),
						"InvoiceWithDeliverDocument"));
			}
			else{
				invoice.addFooterObservation("-");
			}	
		}
		
		// Reorganizar las leyendas al pie del ticket
		reorderFooterObservation(invoice);
		
		// TODO: Se asigna el número de remito en caso de existir.
		
		// Se agregan las líneas de la factura al documento.
		loadDocumentLines(mInvoice, invoice);
		// Agrega los pagos correspondientes de la factura partir de las imputaciones
		loadInvoicePayments(invoice, mInvoice);
		
		// Se asignan los descuentos de la factura
		loadDocumentDiscounts(invoice, mInvoice.getDiscounts());
		
		// Cargar impuestos automáticos
		loadAutomaticTaxes(invoice, mInvoice);
		
		// Cargar impuestos adicionales 
		loadOtherTaxes(invoice,mInvoice);
		
		// Código QR
		loadQRCode(invoice, mInvoice);
		
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
		// Información de la compañía/organización
		debitNote.setClientOrgInfo(getClientOrgInfo(mInvoice));
		// Se asigna el cliente.
		debitNote.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la nota de débito.
		debitNote.setLetter(mInvoice.getLetra());
		debitNote.setDocumentNo(mInvoice.getDocumentNo());
		
		// Se setea la inclusión del impuesto en el precio
		debitNote.setTaxIncluded(MPriceList.get(ctx, mInvoice.getM_PriceList_ID(), getTrxName()).isTaxIncluded());
		
		// CAE
		debitNote.setCae(mInvoice.getcae());
		debitNote.setCaeDueDate(mInvoice.getvtocae());
		
		// Importe del cargo
		debitNote.setChargeAmt(mInvoice.getChargeAmt());
		
		// Setear los mensajes a la cola de la impresión
		setStdFooterObservations(mInvoice, debitNote);
		
		// Reorganizar las leyendas al pie de la nd
		reorderFooterObservation(debitNote);
				
		// Se agregan las líneas de la nota de débito al documento.
		loadDocumentLines(mInvoice, debitNote);
		
		// TODO: Se asigna el número de remito en caso de existir.
		// Descuentos
		loadDocumentDiscounts(debitNote, mInvoice.getDiscounts());
		
		// Cargar impuestos automáticos
		loadAutomaticTaxes(debitNote, mInvoice);
		
		// Cargar impuestos adicionales 
		loadOtherTaxes(debitNote,mInvoice);
		
		// Código QR
		loadQRCode(debitNote, mInvoice);
		
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
		// Información de la compañía/organización
		creditNote.setClientOrgInfo(getClientOrgInfo(mInvoice));
		// Se asigna el cliente.
		creditNote.setCustomer(getCustomer(mInvoice.getC_BPartner_ID()));
		// Se asigna la letra de la nota de crédito.
		creditNote.setLetter(mInvoice.getLetra());
		creditNote.setDocumentNo(mInvoice.getDocumentNo());
		
		// Se setea la inclusión del impuesto en el precio
		creditNote.setTaxIncluded(MPriceList.get(ctx, mInvoice.getM_PriceList_ID(), getTrxName()).isTaxIncluded());
		
		// CAE
		creditNote.setCae(mInvoice.getcae());
		creditNote.setCaeDueDate(mInvoice.getvtocae());
		
		// Importe del cargo
		creditNote.setChargeAmt(mInvoice.getChargeAmt());
		
		// Setear los mensajes a la cola de la impresión
		setStdFooterObservations(mInvoice, creditNote);
		
		// Se asigna el número de factura original.
		String origInvoiceNumber = null;
		String origInvoiceLetter = null;
		String origInvoicePOS = null;
		String origInvoiceNo = null;
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
			origInvoiceLetter = mOriginalInvoice.getLetra();
			// Si no cumple con el formato de comprobantes fiscales se envia
			// el documentNo como número de factura original.
			if(origInvoiceNumber.length() == 13) {
				// El formato es: PPPP-NNNNNNNN, Ej: 0001-00000023
				origInvoiceLetter = Util.isEmpty(origInvoiceLetter, true) ? origInvoiceNumber.substring(0, 1)
						: origInvoiceLetter;
				origInvoicePOS = origInvoiceNumber.substring(1,5);
				origInvoiceNo = origInvoiceNumber.substring(5,13);
				origInvoiceNumber = origInvoicePOS + "-" + origInvoiceNo;
			}
		}
		// Si no existe también se puede obtener del nro del pedido dependiendo
		// de las claves de preference
		else{
			if(!Util.isEmpty(mInvoice.getC_Order_ID(), true)){
				String orderDocTypesKeys = MPreference
						.searchCustomPreferenceValue(
								COMPROBANTE_ORIGINAL_NC_TIPOS_DOCUMENTO_PEDIDOS_PREFERENCE_NAME,
								Env.getAD_Client_ID(ctx),
								Env.getAD_Org_ID(ctx), null, false);
				if(!Util.isEmpty(orderDocTypesKeys, true)){
					MOrder order = new MOrder(ctx, mInvoice.getC_Order_ID(),
							getTrxName());
					MDocType orderDocType = MDocType.get(ctx,
							order.getC_DocTypeTarget_ID(), getTrxName());
					StringTokenizer tokens = new StringTokenizer(orderDocTypesKeys,";");
					boolean founded = false;
					String token;
					while(tokens.hasMoreTokens() && !founded){
						token = tokens.nextToken();
						if (orderDocType.getDocTypeKey().equals(token)) {
							origInvoiceNumber = order.getDocumentNo();
							founded = true;
						}
					}
					// Si no cumple con el formato de comprobantes fiscales se envia
					// el documentNo como número de factura original.
					if (!Util.isEmpty(origInvoiceNumber, true)
							&& origInvoiceNumber.length() == 13) {
						// El formato es: PPPP-NNNNNNNN, Ej: 0001-00000023
						// El formato es: PPPP-NNNNNNNN, Ej: 0001-00000023
						origInvoiceLetter = Util.isEmpty(origInvoiceLetter, true) ? origInvoiceNumber.substring(0, 1)
								: origInvoiceLetter;
						origInvoicePOS = origInvoiceNumber.substring(1,5);
						origInvoiceNo = origInvoiceNumber.substring(5,13);
						origInvoiceNumber = origInvoicePOS + "-" + origInvoiceNo;
					}
				}
			}
		}
		// Número de comprobante original armado y desarmado
		creditNote.setOriginalDocumentNo(origInvoiceNumber);
		creditNote.setOriginalLetter(origInvoiceLetter);
		creditNote.setOriginalPOS(origInvoicePOS != null?Integer.parseInt(origInvoicePOS):null);
		creditNote.setOriginalNo(origInvoiceNo != null?Integer.parseInt(origInvoiceNo):null);
		
		// Reorganizar las leyendas al pie de la nc
		reorderFooterObservation(creditNote);
		
		// Se agregan las líneas de la nota de crédito al documento.
		loadDocumentLines(mInvoice, creditNote);
		
		// Se asignan los descuentos de la factura
		loadDocumentDiscounts(creditNote, mInvoice.getDiscounts());
		
		// Cargar impuestos automáticos
		loadAutomaticTaxes(creditNote, mInvoice);
		
		// Cargar impuestos adicionales 
		loadOtherTaxes(creditNote,mInvoice);
		
		// Código QR
		loadQRCode(creditNote, mInvoice);
		
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
		setDocument(invoice);
		invoice.setAlwaysOpenDrawer(isAlwaysOpenDrawer());
		// Se manda a imprimir la factura a la impresora fiscal.
		getFiscalPrinter().printDocument(invoice);
		// Se actualizan los datos de la factura de oxp.
		try {
			saveDocumentData(mInvoice, invoice);
		}catch(Exception ex) {
			log.warning("Error al imprimir: " + ex.toString());
		}
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
		setDocument(debitNote);
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
		setDocument(creditNote);
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
			customer.setIvaResponsibility(traduceIvaResponsibility(categoriaIva.getFiscalPrinterCodigo()));
			MInvoice mInvoice = (MInvoice)getOxpDocument();
			
			// Si es una factura a consumidor final, los datos del cliente se
			// obtienen a partir de la factura OXP.
			if(customer.getIvaResponsibility() == Customer.CONSUMIDOR_FINAL) {
				
				// Nombre, nro de identificación, domicilio
				customer.setIdentificationType(Customer.DNI);
				customer.setName(cortarCadena(quitarCaracteresEspeciales(mInvoice.getNombreCli()), 40)); // dREHER
				customer.setValue("");
				customer.setIdentificationNumber(mInvoice.getNroIdentificCliente());
				customer.setLocation(mInvoice.getInvoice_Adress());
				customer.setIdentificationName("D.N.I.");
			} else {
				// Si no es factura a consumidor final 
				
				// Se asigna el nombre del cliente a partir del BPartner.
				customer.setName(cortarCadena(quitarCaracteresEspeciales(bPartner.getName()),40)); // dREHER
				customer.setValue(bPartner.getValue());
				// Se asigna el domicilio. 
				MLocation location = MLocation.get(ctx, mInvoice.getBPartnerLocation().getC_Location_ID(), getTrxName());
				customer.setLocation(location.toStringShort());
				
				// Se identifica al cliente con el C.U.I.T. configurado en el Bpartner.
				if (bPartner.getTaxID() != null && !bPartner.getTaxID().trim().equals("")) {
					customer.setIdentificationType(Customer.CUIT);
					customer.setIdentificationNumber(bPartner.getTaxID());
					customer.setIdentificationName("C.U.I.T.");
				}
			}
			
			// Como en el TPV se puede modificar la dirección aún si no es
			// Consumidor Final, entonces se tomar el de la factura siempre
			// TODO Actualmente también se guardan el nombre del cliente y el
			// nro de identificación aún cuando no es CF, analizar si está bien
			// que se tome directamente desde la factura antes de hacer el
			// cambio
			customer.setLocation(mInvoice.getInvoice_Adress());
			customer.setIvaResponsibilityName(categoriaIva.getName());
			
			// Descripción fiscal de la categoría de IVA
			customer.setCategoriaIVAFiscalDescription(getCategoriaIVAFiscalDescription(categoriaIva.getCodigo()));
		}
		
		return customer;
	}
	
	// dREHER
	// Cortar largo maximo
	private String cortarCadena(String cadena, int largo) {
		if(cadena!=null && cadena.length() > largo)
			cadena = cadena.substring(0, largo);
		return cadena;
	}
	
	// dREHER
	// Mejorar el metodo
	private String quitarCaracteresEspeciales(String cadena) {
		
		if(cadena!=null) {
			cadena = cadena.replace("Ñ", "N").replace("ñ","n").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").
					replace("Á", "A").replace("É", "E").replace("Í","I").replace("Ó", "O").replace("Ú", "U").replace("&", " ").replace("+", " ");
		}
		
		return cadena;
	}
	
	/***
	 * Obtener información de la compañía y organización
	 * 
	 * @param invoice comprobante a imprimir
	 * @return info de la compañía y organización
	 */
	private ClientOrgInfo getClientOrgInfo(MInvoice invoice) {
		ClientOrgInfo coi = new ClientOrgInfo();
		// Compañía
		MClient client = MClient.get(invoice.getCtx(), invoice.getAD_Client_ID());
		MClientInfo ci = client.getInfo();
		MOrg org = MOrg.get(invoice.getCtx(), invoice.getAD_Org_ID());
		MOrgInfo oi = org.getInfo();
		Integer ivaID = Util.isEmpty(oi.getC_Categoria_IVA_ID(), true) ? ci.getC_Categoria_Iva_ID()
				: oi.getC_Categoria_IVA_ID();
		String categoriaIVAName = "";
		if(!Util.isEmpty(ivaID, true)) {
			MCategoriaIva iva = new MCategoriaIva(invoice.getCtx(), ivaID,null);
			categoriaIVAName = iva.getName();
		}
		Integer locationID = Util.isEmpty(oi.getC_Location_ID(), true) ? ci.getC_Location_ID() : oi.getC_Location_ID();
		String address = "";
		String city = "";
		String postalCode = "";
		String regionName = "";
		String countryName = "";
		if(!Util.isEmpty(locationID, true)) {
			MLocation l = MLocation.get(invoice.getCtx(), locationID, null);
			address = l.getAddress1();
			city = l.getCity();
			postalCode = l.getPostal();
			if(!Util.isEmpty(l.getC_Region_ID(), true)) {
				MRegion r = MRegion.get(ctx, l.getC_Region_ID());
				regionName = r.getName();
			}
			if(!Util.isEmpty(l.getC_Country_ID(), true)) {
				MCountry c = MCountry.get(ctx, l.getC_Country_ID());
				countryName = c.getName();
			}
		}
		// Setear la info
		coi.setClientName(client.getName());
		coi.setOrgName(org.getName());
		coi.setCuit(!Util.isEmpty(oi.getCUIT(), true)?oi.getCUIT():client.getCUIT());
		coi.setIIBB(ci.getIIBB());
		coi.setCategoriaIVA(categoriaIVAName);
		coi.setAddress(address);
		coi.setCity(city);
		coi.setRegionName(regionName);
		return coi;
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
			if (mDocumentDiscount.isManualGeneralDiscountKind()
					&& !MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice
							.equals(mDocumentDiscount.getDiscountApplication())) {
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
							document.isTaxIncluded(), // Los importes en DocumentDiscount incluyen siempre el impuesto
							mDiscountByTax.getTaxRate());
					// Si el descuento es manual, entonces le cambio la
					// descripción a uno más corto
					if (mDiscountByTax.getDiscountKind().equals(
							MDocumentDiscount.DISCOUNTKIND_ManualDiscount)) {
						discountLine.setDescription(Msg.getMsg(Env.getCtx(),
								"FiscalTicketLineManualDiscount"));
					}
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
					document.isTaxIncluded() // Incluye impuestos
				)
			);
		}
		
	}

	/**
	 * Parser entre el tender type del MPayment y el tendertype enumerador del
	 * payment fiscal
	 * 
	 * @param paymentTenderType
	 *            valor de la columna TenderType de C_Payment
	 * @return el tipo de pago en base al tendertype parámetro, OTROS en caso
	 *         que no se pueda parsear
	 */
	protected TenderType parseTenderType(String paymentTenderType){
		TenderType tt = TenderType.OTROS;
		if(MPayment.TENDERTYPE_Cash.equals(paymentTenderType)){
			tt = TenderType.EFECTIVO;
		} 
		else if(MPayment.TENDERTYPE_Check.equals(paymentTenderType)){
			tt = TenderType.CHEQUE;
		}
		else if(MPayment.TENDERTYPE_CreditCard.equals(paymentTenderType)){
			tt = TenderType.TARJETA;
		}
		else if(MPayment.TENDERTYPE_DirectDeposit.equals(paymentTenderType)){
			tt = TenderType.TRANSFERENCIA_BANCARIA;
		}
		return tt;
	}
	
	/**
	 * Carga los pagos en la factura a emitir a partir de las imputaciones que
	 * tenga la factura en la BD.
	 */
	private void loadInvoicePayments(Invoice invoice, MInvoice mInvoice) {
		BigDecimal totalPaidAmt = BigDecimal.ZERO;
		BigDecimal totalChangeAmt = BigDecimal.ZERO;
		final String OTHERS_DESC = Msg.translate(ctx, "FiscalTicketOthersPayment");
		final String CASH_DESC = Msg.translate(ctx, "FiscalTicketCashPayment");
		// Lista temporal de pagos creados a partir de los allocations
		List<Payment> payments = new ArrayList<Payment>();
		
		// Obtiene todas las imputaciones de la factura agrupadas por pago.
		String sql = 
			"SELECT C_Payment_ID, " +
			       "C_CashLine_ID, " +
			       "C_Invoice_Credit_ID, " +
			       "SUM(Amount + DiscountAmt + WriteoffAmt) AS PaidAmount, " +
			       "SUM(ChangeAmt) AS ChangeAmt " +
			"FROM C_AllocationLine " +
			"WHERE C_Invoice_ID = ? " +
			"GROUP BY C_Payment_ID, C_CashLine_ID, C_Invoice_Credit_ID " +
			"ORDER BY PaidAmount ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// Crea los pagos de Efectivo y Otros para acumular montos de sendos tipos.
		Payment othersPayment = new Payment(BigDecimal.ZERO, OTHERS_DESC, TenderType.OTROS);
		Payment cashPayment = new CashPayment(BigDecimal.ZERO, CASH_DESC);
		
		try {
			pstmt = DB.prepareStatement(sql, getTrxName());
			pstmt.setInt(1, mInvoice.getC_Invoice_ID());
			rs = pstmt.executeQuery();
			
			int paymentID;
			int cashLineID;
			int invoiceCreditID;
			BigDecimal paidAmt = null;
			String description = null;
			BigDecimal changeAmt = BigDecimal.ZERO;
			TenderType tenderType = null;
			// Pago que se crea en caso de que la imputación no entre en la clase
			// Efectivo u Otros Pagos.
			Payment payment = null;
			Payment creditCardCashRetirementPayment = null;
			while (rs.next()) {
				// Obtiene los IDs de los documentos para determinar cual es el que
				// se utilizó para el pago
				paymentID = rs.getInt("C_Payment_ID");
				cashLineID = rs.getInt("C_CashLine_ID");
				invoiceCreditID = rs.getInt("C_Invoice_Credit_ID");
				paidAmt = rs.getBigDecimal("PaidAmount");
				changeAmt = rs.getBigDecimal("ChangeAmt");
				
				description = null;
				payment = null;
				creditCardCashRetirementPayment = null;
				
				// 1. Imputación con un C_Payment.
				if (paymentID > 0) {
					// Obtiene la descripción.
					MPayment mPayment = new MPayment(mInvoice.getCtx(), paymentID, getTrxName());
					description = getInvoicePaymentDescription(mPayment);
					tenderType = parseTenderType(mPayment.getTenderType());
					// Retiro de efectivo de tarjeta de crédito
					if (MPayment.TENDERTYPE_CreditCard.equals(mPayment
							.getTenderType()) && !Util.isEmpty(changeAmt, true)) {
						creditCardCashRetirementPayment = new CashRetirementPayment(
								changeAmt, "["+changeAmt+"] "
										+ Msg.getMsg(ctx, "Retirement")
										+ " " + description);
						changeAmt = BigDecimal.ZERO;
					}
				// 2. Imputación con Línea de Caja
				} else if (cashLineID > 0) {
					// Todas las imputaciones con líneas de caja se suman al pago
					// global en Efectivo para imprimir una única línea que diga
					// "Efectivo".
					cashPayment.setAmount(cashPayment.getAmount().add(paidAmt).add(changeAmt));
				// 3. Imputación con Factura de Crédito (NC)
				} else if (invoiceCreditID > 0) {
					// Obtiene la descripción.
					description = getInvoicePaymentDescription(new MInvoice(
							ctx, invoiceCreditID, getTrxName()));
					tenderType = TenderType.CREDITO;
				}
				
				// Si es un tipo que entra dentro de "Otros Pagos", se suma el importe
				// al payment de "Otros Pagos".
				if (OTHERS_DESC.equals(description)) {
					othersPayment.setAmount(othersPayment.getAmount().add(paidAmt).add(changeAmt));
				// Caso Contrario (Tarjeta, Cheque, Transferencia, NC, etc), se crea el pago
				// con la descripción.
				} else if (description != null) {
					payment = new Payment(paidAmt.add(changeAmt), description, tenderType);
				}
				
				// Si se creó un nuevo pago se agrega a la lista ordenada de pagos
				// según el mayor importe.
				if (payment != null) {
					payments.add(payment);
				}
				if(creditCardCashRetirementPayment != null){
					payments.add(creditCardCashRetirementPayment);
				}
				
				totalPaidAmt = totalPaidAmt.add(paidAmt).add(changeAmt);
				totalChangeAmt = totalChangeAmt.add(changeAmt);
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
			String paymentMedium = null;
			// Si existe el medio de cobro a crédito dentro de la factura y la
			// forma de pago es A Crédito, entonces imprimo el medio de cobro
			if (paymentRule.equals(MInvoice.PAYMENTRULE_OnCredit)
					&& !Util.isEmpty(
							mInvoice.getC_POSPaymentMedium_Credit_ID(), true)) {
				MPOSPaymentMedium creditPaymentMedium = new MPOSPaymentMedium(
						ctx, mInvoice.getC_POSPaymentMedium_Credit_ID(),
						getTrxName());
				paymentMedium = creditPaymentMedium.getName();
			}
			paymentMedium = paymentMedium == null?MRefList.getListName(ctx,
					MInvoice.PAYMENTRULE_AD_Reference_ID, paymentRule):paymentMedium;
			invoice.addPayment(new Payment(mInvoice.getGrandTotal(),
					paymentMedium, TenderType.CUENTA_CORRIENTE));
		// Si hay pagos, se cargan a los pagos de la factura a emitir.
		} else {
			int paymentQty = 0;
			// Primero el efectivo
			if (cashPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				// TODO: Si se necesita mostrar en la impresión el medio de
				// cobro del efectivo, se debe tomar de C_Cashline, columna
				// C_POSPaymentMedium_ID
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
		
		invoice.setChangeAmt(totalChangeAmt);
	}
	
	/**
	 * Carga y devuelve impuestos 
	 * @param doc
	 * @param mInvoice
	 */
	private List<org.openXpertya.print.fiscal.document.Tax> loadTaxes(Document doc, MInvoice mInvoice, boolean manualTaxes){
		String sql = "SELECT t.c_tax_id, t.name, t.rate, it.taxbaseamt, it.taxamt, t.ispercepcion, t.PerceptionType "
					+ "FROM c_invoicetax as it "
					+ "INNER JOIN c_tax as t ON it.c_tax_id = t.c_tax_id "
					+ "INNER JOIN c_taxcategory as tc ON t.c_taxcategory_id = tc.c_taxcategory_id "
					+ "WHERE (c_invoice_id = ?) AND (ismanual = '"+(manualTaxes?"Y":"N")+"') "
					+ "ORDER BY t.name ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<org.openXpertya.print.fiscal.document.Tax> taxes = new ArrayList<Tax>();
		org.openXpertya.print.fiscal.document.Tax tax = null;
		try{
			ps = DB.prepareStatement(sql, getTrxName());
			ps.setInt(1, mInvoice.getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				tax = new Tax(rs.getInt("c_tax_id"), rs.getString("name"),
						rs.getBigDecimal("rate"),
						rs.getBigDecimal("taxbaseamt"),
						rs.getBigDecimal("taxamt"), 
						rs.getString("ispercepcion").equals("Y"), 
						rs.getString("PerceptionType"));
				taxes.add(tax);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error getting invoice taxes", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (Exception e) {}
		}
		return taxes;
	}
	
	/**
	 * Carga los impuestos adicionales
	 * @param doc
	 * @param mInvoice
	 */
	private void loadOtherTaxes(Document doc, MInvoice mInvoice){
		doc.setOtherTaxes(loadTaxes(doc, mInvoice, true));
	}
	
	/**
	 * Carga los impuestos automáticos
	 * @param doc
	 * @param mInvoice
	 */
	private void loadAutomaticTaxes(Document doc, MInvoice mInvoice){
		doc.setTaxes(loadTaxes(doc, mInvoice, false));
	}
	
	/**
	 * Devuelve la descripción a imprimir para un pago según un MPayment.
	 */
	private String getInvoicePaymentDescription(MPayment mPayment) {
		Properties ctx = mPayment.getCtx();
		String description = null;
		// Primero verificamos si podemos obtener el medio de pago involucrado,
		// en ese caso utilizamos ese en la impresión
		if(!Util.isEmpty(mPayment.getC_POSPaymentMedium_ID(), true)){
			MPOSPaymentMedium paymentMedium = new MPOSPaymentMedium(ctx,
					mPayment.getC_POSPaymentMedium_ID(), getTrxName());
			description = paymentMedium.getName();
		}
		// - Tarjeta de Crédito: NombreTarjeta NroCupon.
		//   Ej: VISA 1248
		if (MPayment.TENDERTYPE_CreditCard.equals(mPayment.getTenderType())) {
			description = (description == null?MRefList.getListName(ctx,
					MPayment.CREDITCARDTYPE_AD_Reference_ID,
					mPayment.getCreditCardType()):description) + " " + mPayment.getCouponNumber();
		// - Cheque: Cheque NumeroCheque
		//   Ej: Cheque 00032456	
		} else if (MPayment.TENDERTYPE_Check.equals(mPayment.getTenderType())) {
			description = (description == null ? Msg.translate(ctx,
					"FiscalTicketCheckPayment") : description)
					+ " "
					+ mPayment.getCheckNo();
		// - Transferencia: Transf NroDeTransferencia
		//   Ej: Transf 893276662	
		} else if (MPayment.TENDERTYPE_DirectDeposit.equals(mPayment.getTenderType())) {
			description = (description == null ? Msg.translate(ctx,
					"FiscalTicketTransferPayment") : description)
					+ " "
					+ mPayment.getCheckNo(); // En CheckNo se guarda el nro de
											// transferencia actualmente.
		// Otros tipos: Otros pagos
		} else {
			description = (description == null ? Msg.translate(ctx,
					"FiscalTicketOthersPayment") : description);
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
	
	/**
	 *  Obtener el ultimo nro impreso para este tipo de documento y documento fiscal
	 * @param document
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	private int getLastPrintedNumber(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		
			log("FiscalDocumentPrint.getLastPrintedNumber (PO document). FiscalPrinter=" + getFiscalPrinter());
			
			// Obtener el estado de la impresora fiscal
			// la Hasar 2da Generacion devuelve ultimo impreso si se envia el codigo de comprobante correspondiente
			String lastNro = getFiscalPrinter().getLastDocumentNoPrinted(
					document.getDocumentType(), document.getLetter());
			Integer lastNroInt = -1;
			try {
				lastNroInt = Integer.parseInt(lastNro);
			}catch(Exception ex) {
				log.warning("Error al leer ultimo numero impreso : " + lastNro);
			}
		
		return lastNroInt;
	}
	
	private void saveDocumentData(MInvoice oxpDocument, Document document) throws Exception{
		String dtType = document.getDocumentType();
		
		/////////////////////////////////////////////////////////////////
		// -- Numero del comprobante emitido -- 
		// Solo para facturas, notas de crédito y débito.
		if (dtType.equals(Document.DT_INVOICE) ||
		    dtType.equals(Document.DT_CREDIT_NOTE) ||
		    dtType.equals(Document.DT_DEBIT_NOTE)) {
		
			// Si no tiene el nro de documento, entonces hay que obtenerlo de la
			// impresora fiscal
			if(Util.isEmpty(document.getDocumentNo(), true)){
				// Obtener el estado de la impresora fiscal
				String lastNro = getFiscalPrinter().getLastDocumentNoPrinted(
						dtType, document.getLetter());
				Integer lastNroInt = Integer.parseInt(lastNro);
				// Buscar el nro de comprobante en la base para ver si ya existe
				// impreso y que no sea éste comprobante
				if(MInvoice.existInvoiceFiscalPrinted(ctx,
						document.getLetter(), oxpDocument.getPuntoDeVenta(),
						lastNroInt, oxpDocument.getC_DocTypeTarget_ID(),
						oxpDocument.getID(), getTrxName())){
					throw new Exception(Msg.getMsg(ctx,
							"AlreadyExistsFiscalPrintedDocument",
							new Object[] { CalloutInvoiceExt
									.GenerarNumeroDeDocumento(
											oxpDocument.getPuntoDeVenta(),
											lastNroInt, document.getLetter(),
											true, false) }));
				}
				// Setearle el nro del comprobante
				document.setDocumentNo(String.valueOf(lastNro));
				
				log.warning("El documento NO tenia numero asignado, por ende se toma el del controlador fiscal y se guarda en el comprobante! # " + lastNro);
				
			}
			
			/**
			 * TODO: (confirmar esta accion) CONFIRMADO!
			 * 
			 * Si no coincide numero de comprobante devuelto por el controlador fiscal
			 * con el numero del comprobante obtenido desde el contador, mostrar mensaje
			 * pero no reemplazar el numero del Invoice
			 *  
			 *  dREHER 
			 */
			log.warning("Numero obtenido desde el controlador fiscal: " + document.getDocumentNo());
			
			
			try {
				
				int receiptNo = Integer.parseInt(document.getDocumentNo());
				
				/**
				
				 * No pisar numero del comprobante, mostrar mensaje para gestionar...
				 *  
				
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
				
				*/
				
				// Si el numero del controlador no coincide con el numero del invoice, debe excepcionar
				if(receiptNo != oxpDocument.getNumeroComprobante()) 
					throw new Exception("El numero obtenido desde el controlador NO coincide con el numero Libertya, gestionar!");
				
				// Ya existe y marcado como impreso el numero recibido, provocar excepcion...
				if(oxpDocument.existInvoiceFiscalPrinted(receiptNo))
					throw new Exception("El numero obtenido desde el controlador fiscal YA EXISTE, gestionar!");
					
				
			} catch(NumberFormatException nfe) {
				// Error al parsear el nro a entero, esto significa que ya tiene un número
				// asignado, probablemente tenga letras
			}
		}
		
		/////////////////////////////////////////////////////////////////
		// -- Numero del CAI -- 
		// Solo para facturas y notas de débito, siempre y cuando
		// la impresora haya seteado alguno.
		if (dtType.equals(Document.DT_INVOICE) ||
		    dtType.equals(Document.DT_DEBIT_NOTE)) { 
			
			// TODO para los casos en que el ticket se imprimió correctamente
			// pero saltó un error y el usuario decidió que la impresión fue
			// correcta, hay que buscar el CAI. El problema es que deberíamos
			// determinar si el CAI es para esta factura o no
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
		
		/////////////////////////////////////////////////////////////////
		// -- Último Documento Impreso por Impresora fiscal --
		// Se guarda el último documento impreso en el tipo de documento
		// FIXME Por lo pronto se comenta por cuestiones de performance 
		// saveLastDocumentPrinted(oxpDocument);
		/////////////////////////////////////////////////////////////////
		
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
		fireStatusReported(cFiscal, cFiscal.getStatus());
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
	
	protected void fireDocumentPrintAsk(String errorTitle, String errorDesc, String printerStatus) {
		for (FiscalDocumentPrintListener fdpl : getDocumentPrintListeners()) {
			fdpl.documentPrintAsk(this, errorTitle, errorDesc, printerStatus);
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
		if(cFiscal.getStatus().equals(MControladorFiscal.STATUS_ERROR)) {
			
			log.warning("C_Controlador_Fiscal.Status=ERR-. El controlador fiscal esta en estado de Error!");
			
			fireStatusReported(cFiscal);
			// Dependiendo de si hay que ignorar o no el estado de error
			// se continua con la impresión. (Esto se utiliza para evitar
			// los casos en que la impresora quede marcada como error en 
			// la BD pero el dispositivo ya no contenga mas este error.
			if(isIgnoreErrorStatus()){ 
				
				log.warning("Se ignora error, se setea la impresora como lista y se continua...");
				
				// Por ello se setea la impresora como Lista y se intenta
				// continuar con la impresión.
				setFiscalPrinterStatus(cFiscal, MControladorFiscal.STATUS_IDLE);
				
			}
			else{
				// Si no se pueden ignorar estados de error, entonces 
				// no es posible continuar con la impresión.
				if(isThrowExceptionInCancelCheckStatus()){
					log.warning("La impresora esta con estado ERROR, no se puede continuar impresion!");
					throw new Exception(Msg.translate(ctx,"FiscalPrintCancelError"));
				}
				else{
					setCancelWaiting(true);
					log.warning("Controlador fiscal. Se cancela la espera...");
					return false;
				}
			}
		}			

		// Mientras el status sea BUSY, espera 5 segundos y vuelve a chequear.
		while(cFiscal.getStatus().equals(MControladorFiscal.STATUS_BUSY) && !isCancelWaiting()) {
			fireStatusReported(cFiscal);
			Thread.sleep(BSY_SLEEP_TIME);
			bsyCount++;
			cFiscal.load((String)null);
			if(bsyCount == MAX_BSY_SLEEP_COUNT) {
				log.warning("La impresora esta OCUPADA, no se puede continuar impresion!");
				throw new IOException(Msg.translate(ctx,"FiscalPrinterBusyTimeoutError"));
			}
		}
		// Si fue cancelada la operacion de espera entonces se retorna, indicando
		// que el estado no es correcto.
		if(isCancelWaiting()) { 
			if(isThrowExceptionInCancelCheckStatus()){
				log.warning("La impresora esta EN ESPERA, no se puede continuar impresion!");
				throw new Exception(Msg.translate(ctx,"FiscalPrintCancelError"));
			}
			else{
				log.warning("Fiscal printer wait canceled");
				return false;
			}
		}
			
		fireStatusReported(cFiscal, MControladorFiscal.STATUS_IDLE);
		// Se asigna el status de la impresora, el usuario que realiza la operación
		// y la fecha de operación.
		cFiscal.setStatus(MControladorFiscal.STATUS_BUSY);
		cFiscal.setUsedBy_ID(Env.getAD_User_ID(ctx));
		cFiscal.setoperation_date(new Timestamp(System.currentTimeMillis()));
		// No se usa trx dado que los cambios deben ser visibles 
		// inmediatamente por otros usuarios.
		cFiscal.save(); 
		return true;
	}
	
	private void setFiscalPrinterStatus(MControladorFiscal cFiscal, String status) {
		if(cFiscal != null) {
			cFiscal.setStatus(status);
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
		if(!mInvoice.isThermalFiscalPrint(mInvoice.getC_DocTypeTarget_ID())) {
			MDocType docType = MDocType.get(ctx, mInvoice.getC_DocTypeTarget_ID());
			
			String lastDocumentNos = getFiscalPrinter().getLastDocumentNo();
			if(lastDocumentNos!=null && lastDocumentNos.trim().length() > 8)
				lastDocumentNos = lastDocumentNos.substring(lastDocumentNos.trim().length()-8);
			
			log.info("lastDocumentNos (impreso): " + lastDocumentNos);
			
			Integer lastDocumentNo = new Integer(lastDocumentNos);
			// Se obtiene la secuencia del tipo de documento...
			if(docType.getDocNoSequence_ID() != 0 && docType.isFiscalDocument()) {
				
				// dREHER Verificar si se trata de un punto de venta terminado en CERO
				// Ej B067000001806
				// Prefijo B06   NextCurrent 7000001706
				int seqNo = docType.getDocNoSequence_ID();
				MSequence seq = new MSequence(Env.getCtx(), seqNo, mInvoice.get_TrxName());
				
				// dREHER Solo actualizar si el ultimo numero impreso de este tipo de comprobante
				// es mayor a la proxima secuencia
				
				log.warning("Proximo numero segun secuencia: " + mInvoice.getNextSequence(seqNo) +
						"Ultimo numero impreso desde fiscal: " + lastDocumentNo +
						"Ultimo numero impreso guardado    : " + mInvoice.getLastFiscalDocumentNumeroComprobantePrinted());
				
				if(mInvoice.getNextSequence(seqNo) <= lastDocumentNo && mInvoice.getLastFiscalDocumentNumeroComprobantePrinted() <= lastDocumentNo) {

					if(seq.getPrefix()!=null && seq.getPrefix().length()==3) {

						String prefix = mInvoice.getDocumentNo().substring(3,5);
						lastDocumentNos = prefix + lastDocumentNos;
						seq.setCurrentNext((new BigDecimal(lastDocumentNos)).add(Env.ONE));
						seq.save();


					}else {
						// Actualiza la secuencia con el nuevo número de documento
						MSequence.setFiscalDocTypeNextNroComprobante(
								docType.getDocNoSequence_ID(), lastDocumentNo + 1,
								getTrxName());
					}

				}
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
				// Si se encuentra marcada la opción "Solo Descripción de Línea" y la descripción no es vacía.
				if (cFiscal.isOnlyLineDescription() && (!Util.isEmpty(mLine.getDescription()))){
					description = mLine.getDescription();
				}
				else{
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
					total = total.add(invoiceLine.getTotalPriceListWithTax())
								 .subtract(invoiceLine.getTotalDocumentDiscountUnityAmtWithTax());
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
			
			
			/**
			 * En algunas ocaciones el precio de lista queda guardado con el descuento incluido.
			 * Ya que no se pudo encontrar cuando esto se produce, se procede a cambiar el calculo
			 * del precio de lista
			 * 
			 * 2024-01-026
			 * dREHER
			 
			
				unitPrice = (mLine.getPriceList().multiply(mLine.getQtyEntered())
					.subtract(mLine.getLineDiscountAmt())).divide(
					mLine.getQtyEntered(), scale, RoundingMode.HALF_DOWN);
			*/
			
			unitPrice = mLine.getPriceList();
			if(unitPrice.compareTo(mLine.getPriceActual()) <= 0) {
				unitPrice = mLine.getPriceActual().add(mLine.getLineBonusAmt()).add(mLine.getLineDiscountAmt());
			}
			
		}
		return unitPrice;
	}

	/**
	 * @return true si se debe preguntar en caso de error, false caso contrario
	 */
	public boolean ask(){
		return isAskAllowed() && cFiscal.isAskWhenError() && isAskMoment();
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
	
	public void saveLastDocumentPrinted(MInvoice document){
		// Actualizar el último documento impreso fiscalmente en el tipo de
		// documento
		String sql = "UPDATE c_doctype SET c_invoice_id = " + document.getID()
				+ " WHERE c_doctype_id = " + document.getC_DocTypeTarget_ID();
		int no = DB.executeUpdate(sql, getTrxName());
	}

	/**
	 * @param maxAmountCF The maxAmountCF to set.
	 */
	public static void setMaxAmountCF(BigDecimal maxAmountCF) {
		FiscalDocumentPrint.maxAmountCF = maxAmountCF;
	}
	
	/**
	 * Guardar el lote de comandos de log ejecutados
	 * @param isError
	 * @return
	 */
	private boolean saveFiscalLog(boolean isError){
		if (getFiscalPrinter().getFiscalPrinterLogger() != null) {
			// Guardar el log si es posible
			if(getFiscalPrinter().getFiscalPrinterLogger().canSaveRecord(isError)){
				MControladorFiscalLog fiscalLog;
				String logType = isError ? MControladorFiscalLog.LOGTYPE_Error
						: MControladorFiscalLog.LOGTYPE_Info;
				for (FiscalPrinterLogRecord logRecord : getFiscalPrinter()
						.getFiscalPrinterLogger().getFiscalLogRecords()) {
					fiscalLog = new MControladorFiscalLog(ctx, 0, getTrxName());
					fiscalLog.setC_Invoice_ID(getOxpDocument() != null?getOxpDocument().getID():0);
					fiscalLog.setC_Controlador_Fiscal_ID(cFiscal.getID());
					fiscalLog.setCommand(logRecord.getCommand());
					fiscalLog.setResponse(logRecord.getResponse());
					fiscalLog.setLogType(logType);
					if(!fiscalLog.save()){
						log.severe("ERROR saving fiscal log record: "+CLogger.retrieveErrorAsString());
						return false;
					}
				}
			}
			// Limpio el lote de registros de log creados
			getFiscalPrinter().getFiscalPrinterLogger().clearBatchLog();
		}
		return true;
	}
	
	/**
	 * Guardar la respuesta del comando de cierre
	 * 
	 * @param closingType tipo de cierre fiscal
	 * @param closingResponse
	 *            respuesta del comando de cierre
	 * @throws Exception
	 */
	private void saveFiscalClosing(String closingType, FiscalPacket closingResponse) {
		// Obtener el dto con todos los valores de respuesta del comando de cierre
		FiscalClosingResponseDTO dto = getFiscalPrinter().decodeClosingResponse(closingResponse);
		dto.closingType = closingType;
		// Crear el registro
		MControladorFiscalClosingInfo cinfo = new MControladorFiscalClosingInfo(ctx, 0, getTrxName());
		cinfo.setAD_Org_ID(Env.getAD_Org_ID(ctx));
		cinfo.setC_Controlador_Fiscal_ID(cFiscal.getID());
		// El punto de venta lo obtenemos de la info de la fiscal
		if(getFiscalInitData() != null){
			cinfo.setPuntoDeVenta(getFiscalInitData().posNo);
		}
		cinfo.setCreditNote_A_LastEmitted(dto.creditnote_a_lastemitted);
		cinfo.setCreditNote_BC_LastEmitted(dto.creditnote_bc_lastemitted);
		cinfo.setCreditNoteAmt(dto.creditnoteamt);
		cinfo.setCreditNoteExemptAmt(dto.creditnoteexemptamt);
		cinfo.setCreditNoteGravadoAmt(dto.creditnotegravadoamt);
		cinfo.setCreditNoteNoGravadoAmt(dto.creditnotenogravadoamt);
		cinfo.setCreditNoteInternalTaxAmt(dto.creditnoteinternaltaxamt);
		cinfo.setCreditNoteNotRegisteredTaxAmt(dto.creditnotenotregisteredtaxamt);
		cinfo.setCreditNotePerceptionAmt(dto.creditnoteperceptionamt);
		cinfo.setCreditNoteTaxAmt(dto.creditnotetaxamt);
		cinfo.setFiscalClosingNo(dto.fiscalclosingno);
		cinfo.setFiscalClosingType(closingType);
		cinfo.setFiscalClosingDate(dto.closingDate);
		cinfo.setFiscalDocument_A_LastEmitted(dto.fiscaldocument_a_lastemitted);
		cinfo.setFiscalDocument_BC_LastEmitted(dto.fiscaldocument_bc_lastemitted);
		cinfo.setFiscalDocumentAmt(dto.fiscaldocumentamt);
		cinfo.setFiscalDocumentExemptAmt(dto.fiscaldocumentexemptamt);
		cinfo.setFiscalDocumentGravadoAmt(dto.fiscaldocumentgravadoamt);
		cinfo.setFiscalDocumentNoGravadoAmt(dto.fiscaldocumentnogravadoamt);
		cinfo.setFiscalDocumentInternalTaxAmt(dto.fiscaldocumentinternaltaxamt);
		cinfo.setFiscalDocumentNotRegisteredTaxAmt(dto.fiscaldocumentnotregisteredtaxamt);
		cinfo.setFiscalDocumentPerceptionAmt(dto.fiscaldocumentperceptionamt);
		cinfo.setFiscalDocumentTaxAmt(dto.fiscaldocumenttaxamt);
		cinfo.setNoFiscalHomologatedAmt(dto.nofiscalhomologatedamt);
		cinfo.setQtyCanceledCreditNote(dto.qtycanceledcreditnote);
		cinfo.setQtyCanceledFiscalDocument(dto.qtycanceledfiscaldocument);
		cinfo.setQtyCreditNote(dto.qtycreditnote);
		cinfo.setQtyCreditNoteA(dto.qtycreditnotea);
		cinfo.setQtyCreditNoteBC(dto.qtycreditnotebc);
		cinfo.setQtyFiscalDocument(dto.qtyfiscaldocument);
		cinfo.setQtyFiscalDocumentA(dto.qtyfiscaldocumenta);
		cinfo.setQtyFiscalDocumentBC(dto.qtyfiscaldocumentbc);
		cinfo.setQtyNoFiscalDocument(dto.qtynofiscaldocument);
		cinfo.setQtyNoFiscalHomologated(dto.qtynofiscalhomologated);
		cinfo.setProcessed(true);
		if(!cinfo.save()){
			log.saveError("SaveError", CLogger.retrieveErrorAsString());
		}
	}

	/**
	 * Carga el código del comprobante para imprimirlo como código de barras o QR
	 * 
	 * @param mInvoice
	 * @param document
	 */
	private void loadQRCode(Document document, MInvoice mInvoice) {
		// Código QR
		MDocType dt = MDocType.get(mInvoice.getCtx(), mInvoice.getC_DocTypeTarget_ID());
		MClient client = MClient.get(mInvoice.getCtx(), mInvoice.getAD_Client_ID());
		if(MDocType.isElectronicDocType(dt.getID())){
			FacturaElectronicaQRCodeGenerator feQRCodeGenerator = new FacturaElectronicaQRCodeGenerator(mInvoice, dt,
					client.getCUIT(mInvoice.getAD_Org_ID()));
			document.setQRCode(feQRCodeGenerator.getQRCode());
		}
	}

	/**
	 * Obtiene la descripción fiscal de la categoría de iva definida como preference
	 * con la siguiente nomenclatura:
	 * CategoriaIVA_LeyendaFiscal_<codigo_categoria_iva>, donde
	 * <codigo_categoria_iva> es el código numérico de la categoría de iva.
	 * 
	 * @param codigoCategoriaIVA
	 * @return descripción fiscal de la categoría de iva
	 * 
	 */
	protected String getCategoriaIVAFiscalDescription(int codigoCategoriaIVA) {
		return MPreference.searchCustomPreferenceValue(
				CATEGORIAIVA_LEYENDAFISCAL_PREFIX_PREFERENCE_NAME + codigoCategoriaIVA, Env.getAD_Client_ID(ctx),
				Env.getAD_Org_ID(ctx), Env.getAD_User_ID(ctx), false);
	}
	
	public boolean isThrowExceptionInCancelCheckStatus() {
		return throwExceptionInCancelCheckStatus;
	}

	public void setThrowExceptionInCancelCheckStatus(
			boolean throwExceptionInCancelCheckStatus) {
		this.throwExceptionInCancelCheckStatus = throwExceptionInCancelCheckStatus;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
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

	protected boolean isAlwaysOpenDrawer() {
		return alwaysOpenDrawer;
	}

	protected void setAlwaysOpenDrawer(boolean alwaysOpenDrawer) {
		this.alwaysOpenDrawer = alwaysOpenDrawer;
	}

	public FiscalInitData getFiscalInitData() {
		return fiscalInitData;
	}

	public void setFiscalInitData(FiscalInitData fiscalInitData) {
		this.fiscalInitData = fiscalInitData;
	}
}
