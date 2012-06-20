package org.openXpertya.print.fiscal.msg;

import java.util.HashMap;
import java.util.Map;


/**
 * Fuente de mensajes por defecto. Los mensajes se encuentran en castellano.
 * Esta clase es un singleton para evitar instanciar todos los strings de los
 * mensajes una y otra vez.
 * @author Franco Bonafine
 * @date 06/02/2008
 */
public class DefaultMsgSource implements MsgSource {

	private static DefaultMsgSource instance;
	private Map<String, String> msgs;
	
	public static DefaultMsgSource getInstance() {
		if(instance == null)
			instance = new DefaultMsgSource();
		return instance;
	}
	
	private DefaultMsgSource () {
		super();
		msgs = new HashMap<String, String>();
		// ** Se cargan los mensajes por defecto **//
		
		// Mensajes varios
		msgs.put("UnknownHostError","El Host ingresado es desconocido.");
		msgs.put("NullRequestError","El paquete de petición no puede ser null.");
		msgs.put("NullResponseError","El paquete de respuesta no puede ser null.");
		msgs.put("DeviceNotConnectedError","No existe conexión con el dispositivo.");
		msgs.put("SpoolerConnectError","No es posible conectarse con el spooler de la impresora fiscal.");
		msgs.put("FiscalRequest","Petición");
		msgs.put("FiscalResponse", "Respuesta");
		msgs.put("Line","Línea");
		msgs.put("Name","Nombre");
		msgs.put("Location","Domicilio comercial");
		msgs.put("IdentificationNumber","Número de identificación");
		msgs.put("ResponseFormatError","La respuesta recibida tiene un formato no válido. Faltan datos del estado fiscal o general del controlador.");
		
		// Mensajes de validaciones de documentos.
		msgs.put("InvalidCustomerCUIT","El número de CUIT/CUIL del cliente es inválido.");
		msgs.put("IdentificationTypeMustBeCUIT","El tipo de identificación del cliente debe ser C.U.I.T.");
		msgs.put("CustomerIdentificationRequired","Es necesario que el cliente contenga una forma de identificación.");
		msgs.put("CustomerNameRequired","Se debe ingresar el nombre del cliente.");
		msgs.put("CustomerLocationRequired","Se debe ingresar el domicilio comercial del cliente.");
		msgs.put("CustomerIdNumberRequired","Se debe ingresar el número de identificación del cliente.");
		msgs.put("LineQuantityInvalid","La cantidad de la línea del documento debe ser mayor que cero.");
		msgs.put("LineUnitPriceInvalid","El precio unitario de la línea del documento debe ser distinto que cero.");
		msgs.put("LineDescriptionInvalid","Se debe ingresar una descripción para la línea del documento.");
		msgs.put("DiscountDescriptionInvalid","Se debe ingresar una descripción para la línea de descuento.");
		msgs.put("InvalidPaymentAmount","El monto del pago debe ser mayor que cero.");
		msgs.put("InvalidPaymentDescription","Se debe ingresar una descripción para el pago efectuado.");
		msgs.put("InvalidDocumentLinesCount","El documento debe contener al menos una línea");
		msgs.put("InvalidOriginalDocNumber","Se debe ingresar el número del documento original de la nota de crédito.");
		msgs.put("InvalidDocumentTotalAmount","El monto total del documento debe ser distinto de cero.");
		msgs.put("InvalidCFInvoiceAmount","La factura a Consumidor Final sin identificación del cliente supera el monto máximo permitido.");
		msgs.put("CompleteCustomerFields","Se deben completar los datos del cliente");
		
		// Mensajes de estado fiscal de la impresora.
		msgs.put("FstFiscalMemoryCrcErrorTitle","Error en chequeo de memoria fiscal");
		msgs.put("FstFiscalMemoryCrcErrorDesc","Al encenderse la impresora se produjo un error en el checksum. La impresora no funcionará.");
		msgs.put("FstWorkingMemoryCrcErrorTitle","Error en chequeo de memoria de trabajo");
		msgs.put("FstWorkingMemoryCrcErrorDesc","Al encenderse la impresora se produjo un error en el checksum. La impresora no funcionará.");
		msgs.put("FstUnknownCommandTitle", "Comando desconocido");
		msgs.put("FstUnknownCommandDesc","El comando recibido no fue reconocido.");
		msgs.put("FstInvalidDataFieldTitle","Datos no válidos en un campo");
		msgs.put("FstInvalidDataFieldDesc","Uno de los campos del comando recibido tiene datos no válidos. (diferencia de tipos, etc).");
		msgs.put("FstInvalidCommandTitle","Comando no válido para el estado fiscal actual");
		msgs.put("FstInvalidCommandDesc","No es posible ejecutar el comando en el estado que se encuentra el controlador.");
		msgs.put("FstAccumulatorOverflowTitle","Desborde del total");
		msgs.put("FstAccumulatorOverflowDesc","El acumulador de una transacción, del total diario o del IVA se desbordará a raíz del comando recibido. El comando no fué ejecutado.");
		msgs.put("FstFiscalMemoryFullTitle","Memoria fiscal llena, bloqueada o dada de baja");
		msgs.put("FstFiscalMemoryFullDesc","En este estado no es posible abrir un comprobante fiscal.");
		msgs.put("FstFiscalMemoryAlmostFullTitle","Memoria fiscal a punto de llenarse");
		msgs.put("FstFiscalMemoryAlmostFullDesc","La memoria fiscal tiene 30 o menos registros libres.");
		msgs.put("FstDeviceCertifiedTitle","Terminal fiscal certificada");
		msgs.put("FstDeviceCertifiedDesc","La impresora ha sido inicializada correctamente.");
		msgs.put("FstDeviceFiscalizedTitle","Terminal fiscal fiscalizada");
		msgs.put("FstDeviceFiscalizedDesc","La impresora ha sido inicializada correctamente.");
		msgs.put("FstDateErrorTitle","Error en ingreso de fecha");
		msgs.put("FstDateErrorDesc","Se ha ingresado una fecha no válida.");
		msgs.put("FstFiscalDocumentOpenTitle","Documento fiscal abierto");
		msgs.put("FstFiscalDocumentOpenDesc","La impresora contiene un documento fiscal abierto (factura, recibo oficial o nota de crédito).");
		msgs.put("FstDocumentOpenTitle","Documento abierto");
		msgs.put("FstDocumentOpenDesc","La impresora contiene un documento abierto (fiscal, no fiscal o no fiscal homologado).");
		msgs.put("FstSTATPRNActiveTitle","STATPRN activado");
		msgs.put("FstSTATPRNActiveDesc","El comando enviado fue rechazado porque el STATPRN se encuentra activo.");
		
		// Mensajes de estado general de la impresora.
		msgs.put("PstPrinterBusyTitle","Impresora ocupada");
		msgs.put("PstPrinterBusyDesc","La impresora se encuentra momentáneamente ocupada. Realizará automáticamente las tareas pendientes cuando se desocupe.");
		msgs.put("PstPrinterErrorTitle","Error de impresora");
		msgs.put("PstPrinterErrorDesc","Se ha interrumpido la conexión entre el controlador fiscal y la impresora.");
		msgs.put("PstPrinterOfflineTitle","Impresora offline");
		msgs.put("PstPrinterOfflineDesc","La impresora no ha logrado comunicarse dentro del período de tiempo establecido.");
		msgs.put("PstJournalPaperOutTitle","Falta papel de diario");
		msgs.put("PstJournalPaperOutDesc","El sensor de papel del diario ha detectado falta de papel.");
		msgs.put("PstTicketPaperOutTitle","Falta papel de tickets");
		msgs.put("PstTicketPaperOutDesc","El sensor de papel de ticket ha detectado falta de papel.");
		msgs.put("PstPrintBufferFullTitle","Buffer de impresora lleno");
		msgs.put("PstPrintBufferFullDesc","El buffer de comandos fiscales se está aproximando a su cantidad máxima. Cualquier comando enviado a la impresora no será ejecutado.");
		msgs.put("PstPrintBufferEmptyTitle","Buffer de impresora vacio");
		msgs.put("PstPrintBufferEmptyDesc","El buffer de comandos fiscales se encuentra vacio. Todos los comandos enviados fueron enviados al controlador fiscal.");
		msgs.put("PstPrinterCoverOpenTitle","Tapa de impresora abierta");
		msgs.put("PstPrinterCoverOpenDesc","No es posible continuar hasta que la tapa sea cerrada.");
		msgs.put("PstMoneyDrawerClosedTitle","Cajón de dinero cerrado o ausente");
		msgs.put("PstMoneyDrawerClosedDesc","");
	}
	
	public String get(String key) {
		String msg = msgs.get(key);
		if(msg == null)
			msg = key;
		return msg;		
	}
}
