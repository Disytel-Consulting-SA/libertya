package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;
import java.util.Date;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.document.Tax;

/**
 * Comandos que interpretan las impresoras fiscales Hasar.
 * @author Franco Bonafine
 * @date 21/01/2008
 */
public interface HasarCommands {

	///////////////////////////////////////////////////////////////////////
	// Códigos de los comandos
	///////////////////////////////////////////////////////////////////////
	
	// Comandos de configuración y consulta

	/** Consultar estado. (0x2A) */
	public int CMD_STATUS_REQUEST             = 0x2A;
	/** Consultar estado intermedio. (0xA1) */
	public int CMD_STATPRN                    = 0xA1;
	/** Consultar versión del controlador fiscal. (0x7F) */
	public int CMD_GET_FISCAL_DEVICE_VERSION  = 0x7F;
	/** Dar de baja la memoria fiscal. (0xB1) */
	public int CMD_KILL_FISCAL_MEMORY         = 0xB1;
	/** Cambiar velocidad de comunicación. (0xA0) */
	public int CMD_SET_COM_SPEED              = 0xA0;
	/** Cambiar fecha y hora. (0x58) */
	public int CMD_SET_DATE_TIME              = 0x58;
	/** Consultar fecha y hora. (0x59) */
	public int CMD_GET_DATE_TIME              = 0x59;
	/** Cargar configuración, comando nuevo. (0x95) */
	public int CMD_SET_GENERAL_CONFIGURATION  = 0x95;
	/** Consultar configuración, comando nuevo. (0x96) */
	public int CMD_GET_GENERAL_CONFIGURATION  = 0x96;
	/** Cargar configuración, comando viejo. (0x65) */
	public int CMD_SET_CONFIGURATION_BY_BLOCK = 0x65;
	/** Consultar configuración, comando viejo. (0x66) */
	public int CMD_GET_CONFIGURATION_BY_BLOCK = 0x66;
	/** Cargar configuración por parámetro. (0x64) */
	public int CMD_SET_CONFIGURATION_BY_ONE   = 0x64;
	/** Consultar datos de inicialización. (0x73) */
	public int CMD_GET_INIT_DATA              = 0x73;
	/** Cambiar responsabilidad frente al IVA. (0x63) */
	public int CMD_CHANGE_IVA_RESPONSIBILITY  = 0x63;
	/** Cambiar número de Ingresos Brutos. (0x6E) */
	public int CMD_CHANGE_IB_NUMBER           = 0x6E;
	/** Cargar nombre de fantasía del propietario. (0x5F) */
	public int CMD_SET_FANTASY_NAME           = 0x5F;
	/** Consultar nombre de fantasía del propietario. (0x92) */
	public int CMD_GET_FANTASY_NAME           = 0x92;
	/** Cargar encabezamiento y pie de documentos. (0x5D) */
	public int CMD_SET_HEADER_TRAILER         = 0x5D;
	/**
	 * Cargar encabezamiento y pie de documentos. Impresora Fiscal Hasar 2da
	 * Generación. (0x9E)
	 */
	public int CMD_SET_HEADER_TRAILER_2G	  = 0x9E;
	/** Consultar encabezamiento y pie de documentos. (0x5E) */
	public int CMD_GET_HEADER_TRAILER         = 0x5E;
	/** Cargar logotipo del cliente. (0x90) */
	public int CMD_STORE_LOGO_DATA            = 0x90;
	/** Borrar logotipo del cliente. (0x91) */
	public int CMD_RESET_LOGO_DATA            = 0x91;

	// Comandos de control fiscal

	/** Consultar capacidad restante de registros diarios. (0x37) */
	public int CMD_GET_HISTORY_CAPACITY       = 0x37;
	/** Cerrar jornada fiscal imprimiendo reporte. (0x39) */
	public int CMD_DAILY_CLOSE                = 0x39;
	/** Imprimir reporte de auditoría por fecha. (0x3A) */
	public int CMD_DAILY_CLOSE_BY_DATE        = 0x3A;
	/** Imprimir reporte de auditoría por número de Z. (0x3B) */
	public int CMD_DAILY_CLOSE_BY_NUMBER      = 0x3B;
	/** Consultar registro diario. (0x3C) */
	public int CMD_GET_DAILY_REPORT           = 0x3C;
	/** Consultar memoria de trabajo. (0x67) */
	public int CMD_GET_WORKING_MEMORY         = 0x67;
	/** Iniciar consulta de la información de IVA. (0x70) */
	public int CMD_SEND_FIRST_IVA             = 0x70;
	/** Continuar consulta de la información de IVA. (0x71) */
	public int CMD_NEXT_TRANSMISSION          = 0x71;

	// Comandos comunes a varios tipos de documentos

	/** Cancelar documento. (0x98) */
	public int CMD_CANCEL_DOCUMENT            = 0x98;
	/** Reimprimir el último documento emitido. (0x99) */
	public int CMD_REPRINT_DOCUMENT           = 0x99;
	/** Cargar código de barras. (0x5A) */
	public int CMD_SET_BAR_CODE               = 0x5A;
	/** Cargar datos del cliente. (0x62) */
	public int CMD_SET_CUSTOMER_DATA          = 0x62;
	/** Cargar información de remito o de comprobante original. (0x93) */
	public int CMD_SET_EMBARK_NUMBER          = 0x93;
	/** Consultar información de remito o de comprobante original. (0x94) */
	public int CMD_GET_EMBARK_NUMBER          = 0x94;
	/** Avanzar papel de ticket. (0x50) */
	public int CMD_FEED_TICKET                = 0x50;
	/** Avanzar papel de auditoría. (0x51) */
	public int CMD_FEED_JOURNAL               = 0x51;
	/** Avanzar papeles de ticket y auditor�a. (0x52) */
	public int CMD_FEED_TICKET_AND_JOURNAL    = 0x52;

	// Comandos de documentos fiscales

	/** DF: Abrir documento fiscal. (0x40) */
	public int CMD_OPEN_FISCAL_RECEIPT        = 0x40;
	/** DF: Imprimir texto fiscal. (0x41) */
	public int CMD_PRINT_FISCAL_TEXT          = 0x41;
	/** DF: Imprimir ítem. (0x42) */
	public int CMD_PRINT_LINE_ITEM            = 0x42;
	/** DF: Descuento o recargo sobre el último ítem. (0x55) */
	public int CMD_LAST_ITEM_DISCOUNT         = 0x55;
	/** DF: Descuento general. (0x54) */
	public int CMD_GENERAL_DISCOUNT           = 0x54;
	/** DF: Devolución de envases, bonificaciones y recargos. (0x6D) */
	public int CMD_RETURN_RECHARGE            = 0x6D;
	/** DF: Recargo de IVA a responsable no inscripto. (0x61) */
	public int CMD_CHARGE_NON_REGISTERED_TAX  = 0x61;
	/** DF: Percepciones sobre el IVA. (0x60) */
	public int CMD_PERCEPTIONS                = 0x60;
	/** DF: Percepciones. Impresora Fiscal Hasar 2da Generación. (0x61) */
	public int CMD_PERCEPTIONS_2G			  = 0x61;
	/** DF: Consultar subtotal. (0x43) */
	public int CMD_SUBTOTAL                   = 0x43;
	/** DF: Definir líneas de texto en recibos. (0x97) */
	public int CMD_RECEIPT_TEXT               = 0x97;
	/** DF: Imprimir total y pago. (0x44) */
	public int CMD_TOTAL_TENDER               = 0x44;
	/** DF: Cerrar documento fiscal. (0x45) */
	public int CMD_CLOSE_FISCAL_RECEIPT       = 0x45;

	// Comandos de documentos no fiscales

	/** DNF: Abrir documento no fiscal en impresora Ticket. (0x48) */
	public int CMD_OPEN_NON_FISCAL_RECEIPT            = 0x48;
	/** DNF: Abrir documento no fiscal en impresora Slip. (0x47) */
	public int CMD_OPEN_NFD_SLIP              = 0x47;
	/** DNF: Imprimir texto no fiscal. (0x49) */
	public int CMD_PRINT_NON_FISCAL_TEXT      = 0x49;
	/** DNF: Cerrar documento no fiscal. (0x4A) */
	public int CMD_CLOSE_NON_FISCAL_RECEIPT                  = 0x4A;
	/** DNF: Cortar documento no fiscal. (0x4B) */
	public int CMD_CUT_NFD                    = 0x4B;

	// Comandos de documentos no fiscales homologados

	/** DNFH: Abrir documento no fiscal homologado. (0x80) */
	public int CMD_OPEN_DNFH                  = 0x80;
	/** DNFH: Imprimir ítem en remito u orden de salida. (0x82) */
	public int CMD_PRINT_EMBARK_ITEM          = 0x82;
	/** DNFH: Imprimir ítem en resumen de cuenta o en cargo a la habitaci�n. (0x83) */
	public int CMD_PRINT_ACCOUNT_ITEM         = 0x83;
	/** DNFH: Imprimir ítem en cotización. (0x84) */
	public int CMD_PRINT_QUOTATION_ITEM       = 0x84;
	/** DNFH: Cerrar documento no fiscal homologado. (0x81) */
	public int CMD_CLOSE_DNFH                 = 0x81;
	/** Imprimir documento no fiscal homologado para farmacias. (0x68) */
	public int CMD_PRINT_PHARMACY_NFHD        = 0x68;
	/** Imprimir documento no fiscal homologado para reparto. (0x69) */
	public int CMD_PRINT_DELIVERY_NFHD        = 0x69;

	// Comandos de documentos voucher

	/** Voucher: Iniciar carga de datos del voucher. (0x6A) */
	public int CMD_SET_VOUCHER_DATA_1         = 0x6A;
	/** Voucher: Finalizar carga de datos del voucher. (0x6B) */
	public int CMD_SET_VOUCHER_DATA_2         = 0x6B;
	/** Voucher: Imprimir voucher. (0x6C) */
	public int CMD_PRINT_VOUCHER              = 0x6C;

	// Otros comandos

	/** Abrir cajón de dinero. (0x7B) */
	public int CMD_OPEN_DRAWER                = 0x7B;
	/** Escribir en visor. (0xB2) */
	public int CMD_WRITE_DISPLAY              = 0xB2;
	/** Seleccionar fuente de doble ancho para imprimir la línea */
	public int CMD_DOUBLE_WIDTH               = 0xF4;
	
	// Otros valores 
	
	/** Delete de líneas de cabecera o cola de impresión */
	public String SET_HEADER_TRAILER_DEL	  = "DEL";
	
	///////////////////////////////////////////////////////////////////////
	// Comandos de inicialización, baja y configuración.
	///////////////////////////////////////////////////////////////////////
	
	/**
	 *  Comando que establece la velocidad de comunicación entre el host y 
	 *  el controlador fiscal.
	 *  @param speed: velocidad de comunicación 
	 *  <ul>
	 *  	<li>2400   : 2.400 bps</li>		
	 *  	<li>4800   : 4.800 bps</li>			
	 *  	<li>9600   : 9.600 bps</li>		
	 *  	<li>19200  : 19.200 bps</li>			
	 *  	<li>38400  : 38.400 bps</li>			
	 *  	<li>57600  : 57.600 bps</li>			
	 *  	<li>115200 : 115.200 bps</li>
	 *  </ul>
	 *  @return String que representa el comando para la impresora.
	 **/
	public FiscalPacket cmdSetComSpeed(Long speed);

	/**
	 * Comando que configura en bloque los parámetros de funcionamiento
	 * del controlador.
	 * @param printConfigReport: Iiprimir el reporte de configuración.
	 * @param loadDefaultData: carga los valores por defecto de los 
	 *        parámetros ausentes.  
	 * @param finalConsumerLimit: monto límite a partir del cual las facturas 
	 *        y notas de débito a consumidor final deben llevar obligatoriamente
	 *        los datos del comprador. 
	 * @param ticketInvoiceLimit: límite de Ticket-Factura.
	 * @param ivaNotInscript: porcentaje a aplicar a consumidores responsables
	 *        no inscriptos.
	 * @param copies: cantidad máxima de copias que se imprimen de cada 
	 *        documento.
	 * @param printChange: imprime las leyendas 'CAMBIO $0.00'.
	 * @param printLabels: imprime los datos de Ingresos Brutos, leyenda
	 *        'A CONSUMIDOR FINAL' y las líneas en blanco.
	 * @param ticketCutType: tipo de corte que el troquelador realizará una
	 *        vez impreso el comprobante. 
	 * @param printFramework: impresión de marco de los documentos.
	 * @param reprintDocuments: reimpresión automática de documentos cancelados
	 *        por corte de energía.
	 * @param balanceText: texto a imprimir como saldo de medio de pago.
	 * @param paperSound: emitir señal sonora que indica la falta de papel.
	 * @param paperSize: tamaño de la hoja.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetGeneralConfiguration(boolean printConfigReport, boolean loadDefaultData,
			BigDecimal finalConsumerLimit, BigDecimal ticketInvoiceLimit, BigDecimal ivaNonInscript, 
			Integer copies, Boolean printChange, Boolean printLabels, String ticketCutType, Boolean printFramework, 
			Boolean reprintDocuments, String balanceText, Boolean paperSound, String paperSize);

	/**
	 * Comando que cambia el valor de responsabilidad frente al IVA
	 * almacenado en la memoria fiscal del controlador.
	 * @param ivaResponsability: nueva responsabilidad frente al IVA.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdChangeIVAResponsibility(String ivaResponsability);

	///////////////////////////////////////////////////////////////////////
	// Comandos de diagnóstico y consulta
	///////////////////////////////////////////////////////////////////////

	/**
	 * Comando que consulta el estado en que se encuentra el controlador
	 * fiscal, el hardware del impresor y los documentos emitidos.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdStatusRequest();

	/**
	 * Comando que consulta el estado en que se encuentra el controlador fiscal
	 * y el hardware del impresor aún cuando la impresora no ha terminado de
	 * procesar el comando.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSTATPRN();

	/**
	 * Comando que consulta la configuración del controlador.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetGeneralConfigurationData();

	///////////////////////////////////////////////////////////////////////
	// Comandos de control fiscal
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * Comando para cierre de jornada fiscal.
	 * @param docType: tipo de cierre a realizar.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdDailyClose(String docType);

	/**
	 * Comando que consulta los datos guardados en la memoria de trabajo
	 * durante la jornada fiscal.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetWorkingMemory();

	/**
	 * Comando para iniciar el envió de montos asociados a porcentajes de IVA,
	 * impuestos internos y percepciones.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSendFirstIVA();

	/**
	 * Continúa el envió de montos asociados a porcentajes de IVA, impuestos
	 * internos y percepciones. Es el complementdo de {@link #cmdSendFirstIVA()}
	 * ya que permite obtener los importes de impuestos calculados por cada tasa
	 * de IVA, luego percepciones e impuestos internos. 
	 * @return <code>FiscalPacket</code> que representa el comando para la
	 *         impresora.
	 */
	public FiscalPacket cmdNextIVATransmission();

	///////////////////////////////////////////////////////////////////////
	// Comandos de comprobante fiscal y nota de crédito
	///////////////////////////////////////////////////////////////////////

	/**
	 * Comando para abrir un comprobante fiscal.
	 * @param docType: tipo del documento.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdOpenFiscalReceipt(String docType);

	/**
	 * Comando para imprimir texto fiscal.
	 * @param text: texto fiscal a imprimir.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintFiscalText(String text, Integer display);

	/**
	 * Comando para imprimir un item fiscal.
	 * @param description: descripción del item.
	 * @param quantity: cantidad del item.
	 * @param price: precio unitario del item.
	 * @param ivaPercent: porcentaje del IVA que se aplica al item. 
	 * @param substract: imputación del item.
	 *                   <code>True</code> resta en el comprobante fiscal.
	 *                   <code>False</code> suma en el comprobante fiscal.
	 * @param internalTaxes: impuestos internos.
	 * @param basePrice: indica si el precio tiene el IVA incluído o no.
	 *                   <code>True</code> indica que el precio no tiene IVA incluído.
	 *                   <code>False</code> indica que el precio tiene IVA incluído.
	 * @param display: tipo de impresión. 
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity,
			BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes,
			boolean basePrice, Integer display);

	/**
	 * Comando para aplicar un decuento/recargo sobre el último item vendido.
	 * @param description: descripción del descuent/recargo.
	 * @param amount: monto a aplicar.
	 * @param substract: Imputación del monto.
	 *                   <code>True</code> resta en el comprobante fiscal.
	 *                   <code>False</code> suma en el comprobante fiscal.
	 * @param baseAmount: Indica si el monto tiene el IVA incluído o no.
	 *                   <code>True</code> indica que el monto no tiene IVA incluído.
	 *                   <code>False</code> indica que el monto tiene IVA incluído.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdLastItemDiscount(String description, BigDecimal amount,
			boolean substract, boolean baseAmount, Integer display);

	/**
	 * Comando para aplicar un descuento o recargo general.
	 * @param description: descripción del descuento/recargo.
	 * @param amount: monto a aplicar.
	 * @param substract: imputación del monto.
	 *                   <code>True</code> resta en el comprobante fiscal.
	 *                   <code>False</code> suma en el comprobante fiscal.
	 * @param baseAmount: indica si el monto tiene el IVA incluído o no.
	 *                   <code>True</code> indica que el monto no tiene IVA incluído.
	 *                   <code>False</code> indica que el monto tiene IVA incluído.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGeneralDiscount(String description, BigDecimal amount,
			boolean substract, boolean baseAmount, Integer display);

	/**
	 * Comando para generar percepciones.
	 * 
	 * @param tributo
	 * @return <code>FiscalPacket</code> que representa el comando para la
	 *         impresora.
	 */
	public FiscalPacket cmdPerceptions(Tax otherTax);

	/**
	 * Comando para calcular el subtotal del comprobante abierto.
	 * @param print: imprime texto y monto.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSubtotal(boolean print, Integer display);

	/**
	 * Comando para calcular el total del comprobante abierto.
	 * @param description: descripción.
	 * @param amount: monto total.
	 * @param cancel: cancela el comprobante fiscal abierto.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display);

	/**
	 * Comando para cerrar un comprobante fiscal.
	 * @param copies: Cantidad de copias a imprimir.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdCloseFiscalReceipt(Integer copies);
	
	/**
	 * Comando para realizar Devolución de envases, Bonificaciones y Recargos
	 * @param description: descripción del descuent/recargo.
	 * @param amount: monto a aplicar.
	 * @param ivaPercent: porcentaje del IVA que se aplica al item.
	 * @param substract: Imputación del monto.
	 *                   <code>True</code> resta en el comprobante fiscal.
	 *                   <code>False</code> suma en el comprobante fiscal.
	 * @param internalTaxes: impuestos internos.
	 * @param baseAmount: Indica si el monto tiene el IVA incluído o no.
	 *                   <code>True</code> indica que el monto no tiene IVA incluído.
	 *                   <code>False</code> indica que el monto tiene IVA incluído.
	 * @param display: tipo de impresión.
	 * @param operation: "B" descuento / recargo, otro caracter para devolución de envases
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display, String operation);
	
	///////////////////////////////////////////////////////////////////////
	// Comandos de comprobante no fiscal
	///////////////////////////////////////////////////////////////////////

	/**
	 * Comando para abrir un comprobante no fiscal.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdOpenNonFiscalReceipt();

	/**
	 * Comando para imprimir un texto no fiscal dentro de un comprobante 
	 * no fiscal.
	 * @param text: texto a imprimir.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintNonFiscalText(String text, Integer display);

	/**
	 * Comando para cerrar un comprobante no fiscal.
	 * @param copies: Cantidad de copias a imprimir.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdCloseNonFiscalReceipt(Integer copies);
	
	///////////////////////////////////////////////////////////////////////
	// Comandos de documentos no fiscales homologados
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * Comando para abrir un documento no fiscal homologado.
	 * @param docType: tipo del documento.
	 * @param identification: identificación del documento.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora. 
	 */
	public FiscalPacket cmdOpenDNFH(String docType, String identification);

	/**
	 * Comando para imprimir una línea dividida en dos campos (descripción y
	 * cantidad) en un documento no fiscal homologado.
	 * @param description: descriptión del item.
	 * @param quantity: cantidad del item.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintEmbarkItem(String description, BigDecimal quantity, Integer display);

	/**
	 * Comando para imprimir item en resumen de cuenta o cargo a la habitación.
	 * @param date: fecha del comprobante. 
	 * @param docNumber: número del comprobante.
	 * @param description: descripción de venta o servicio.
	 * @param debitAmount: monto del debe.
	 * @param creditAmount: monto del haber.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintAccountItem(Date date, String docNumber,
			String description, BigDecimal debitAmount, BigDecimal creditAmount,
			Integer display);

	/**
	 * Comando para imprimir un item en una cotización.
	 * @param description: descipción del item.
	 * @param display: tipo de impresión.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdPrintQuotationItem(String description, Integer display);

	/**
	 * Comando para cerrar un documento no fiscal homologado.
	 * @param copies: Cantidad de copias a imprimir. 
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdCloseDNFH(Integer copies);

	///////////////////////////////////////////////////////////////////////
	// Comandos comunes a varios tipos de documentos
	///////////////////////////////////////////////////////////////////////

	/**
	 * Comando para cancelación del documento abierto.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdCancelDocument();

	/**
	 * Comando para reimpremir el último documento emitido.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdReprint();

	/**
	 * Comando para almacenar los datos de un código de barras e imprimirlo
	 * automáticamente.
	 * @param codeType: tipo del código de barras.
	 * <ul>
	 * <li><code>HasarFiscalPrinter.EAN_13</code></li>
	 * <li><code>HasarFiscalPrinter.EAN_8</code></li>
	 * <li><code>HasarFiscalPrinter.UPCA</code></li>
	 * <li><code>HasarFiscalPrinter.ITS</code></li>
	 * </ul>
	 * @param data: número del código de barras.
	 * @param printNumbers: imprimir números.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdBarCode(Integer codeType, String data,
			boolean printNumbers);

	///////////////////////////////////////////////////////////////////////
	// Comandos de fecha, hora, encabezamiento y cola de documentos
	///////////////////////////////////////////////////////////////////////

	/**
	 * Comando para asignar la fecha y hora al controlador.
	 * @param dateTime: fecha y hora.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetDateTime(Date dateTime);

	/**
	 * Comando para consultar la fecha y hora del controlador.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetDateTime();

	/**
	 * Comando para programar el texto del nombre de fantasía del propietario.
	 * @param line: número de línea del nombre de fantasía.
	 * @param text: texto.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetFantasyName(int line, String text);

	/**
	 * Comando para consultar el texto del nombre de fantasía del propietario.
	 * @param line: número de línea del nombre de fantasía.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetFantasyName(int line);

	/**
	 * Comando para programar texto de encabezamiento y cola de documentos.
	 * @param line: número de línea de encabezamiento o cola.
	 * @param text: texto de la línea.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetHeaderTrailer(int line, String text);

	/**
	 * Comando para consultar el texto de encabezamiento y cola de documentos.
	 * @param line: número de línea de encabezamiento o cola.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetHeaderTrailer(int line);

	/**
	 * Comando para setear los datos del comprador de la factura.
	 * @param name: nombre del comprador.
	 * @param customerDocNumber: nro. de documento / CUIT.
	 * @param ivaResponsibility: responsabilidad frente al IVA
	 * <ul>
	 * 		<li><code>HasarFiscalPrinter.RESPONSABLE_NO_INSCRIPTO</code></li>
	 * 		<li><code>HasarFiscalPrinter.EXENTO</code></li>
	 * 		<li><code>NO_RESPONSABLE</code></li>
	 * 		<li><code>HasarFiscalPrinter.CONSUMIDOR_FINAL</code></li>
	 * 		<li><code>HasarFiscalPrinter.RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO</code></li>
	 * 		<li><code>HasarFiscalPrinter.RESPONSABLE_MONOTRIBUTO</code></li>
	 * 		<li><code>HasarFiscalPrinter.MONOTRIBUTISTA_SOCIAL</code></li>
	 * 		<li><code>HasarFiscalPrinter.PEQUENO_CONTRIBUYENTE_EVENTUAL</code></li>
	 * 		<li><code>HasarFiscalPrinter.PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL</code></li>
	 * 		<li><code>HasarFiscalPrinter.NO_CATEGORIZADO</code></li>
	 * </ul>
	 * @param docType: tipo de documento
	 * <ul>
	 * 		<li><code>HasarFiscalPrinter.CUIT</code></li>
	 * 		<li><code>HasarFiscalPrinter.CUIL</code></li>
	 * 		<li><code>HasarFiscalPrinter.LIBRETA_DE_ENROLAMIENTO</code></li>
	 * 		<li><code>HasarFiscalPrinter.LIBRETA_CIVICA</code></li>
	 * 		<li><code>HasarFiscalPrinter.DNI</code></li>
	 * 		<li><code>HasarFiscalPrinter.PASAPORTE</code></li>
	 * 		<li><code>HasarFiscalPrinter.CEDULA</code></li>
	 * 		<li><code>HasarFiscalPrinter.SIN_CALIFICADOR</code></li>
	 * </ul>
	 * @param location: domicilio comercial.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetCustomerData(String name, String customerDocNumber,
			String ivaResponsibility, String docType, String location);

	/**
	 * Comando para cargar información del remito, comprobante original
	 * o crédito.
	 * @param line: número de línea del remito o comprobante original.
	 * @param text: texto de la línea.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdSetEmbarkNumber(int line, String text);

	
	/**
	 * Comando para consultar información del remito o comprobante original.
	 * @param line: número de línea a consultar.
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora.
	 */
	public FiscalPacket cmdGetEmbarkNumber(int line);
	
	/**
	 * Comando para abrir el cajón de dinero
	 * 
	 * @return <code>FiscalPacket</code> que representa el comando para la
	 *         impresora, null si la impresora no implementa el comando.
	 */
	public FiscalPacket cmdOpenDrawer();
	
	/**
	 * Elimina la línea de cabecera o cola de impresión dependiendo el nro
	 * 
	 * @param lineNo
	 *            nro de línea a eliminar
	 * @return <code>FiscalPacket</code> que representa el comando para la
	 *         impresora, null si la impresora no implementa el comando.
	 */
	public FiscalPacket cmdDeleteHeaderTrailerLine(int lineNo);
	
	/**
	 * Elimina el contenido de la cabecera, cola o ambas
	 * 
	 * @param delOption
	 *            opción de eliminación.
	 *            <ul>
	 *            <li>0: Borra todas las líneas del encabezado y cola de impresión.</li>
	 *            <li>-1: Borra todas las líneas del encabezado.</li>
	 *            <li>-2: Borra todas las líneas de la cola de impresión.</li>
	 *            </ul> 
	 *          
	 * @return <code>FiscalPacket</code> que representa el comando para la
	 *         impresora, null si la impresora no implementa el comando.
	 */
	public FiscalPacket cmdDeleteHeaderTrailerGroup(int delOption);
	
	///////////////////////////////////////////////////////////////////////
	// Comandos para tipos de letra
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * Comando para establecer a tamaño doble el tipo de letra para 
	 * imprimir la línea. 
	 * @return <code>FiscalPacket</code> que representa el comando para la impresora. 
	 */
	public FiscalPacket cmdDoubleWidth();
}
