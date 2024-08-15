package org.openXpertya.clover.model;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.apps.VTrxPaymentTerminalForm;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.pos.paymentterminal.IPaymentTerminal;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;

import com.clover.remote.Challenge;
import com.clover.remote.client.CloverConnectorFactory;
import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.messages.CloseoutRequest;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.RefundPaymentRequest;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.extras.RegionalExtras;
import com.clover.remote.client.transport.ICloverTransport;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Refund;
import com.clover.sdk.v3.payments.TransactionInfo;
import com.clover.utils.CloverConnectUtils;

/**
 * 
 * Contiene la info de la transaccion clover actual
 * 
 * @author dREHER
 *
 */
public class TrxClover implements IPaymentTerminal{
	
	private static final SecureRandom random = new SecureRandom();
	private static final char[] vals = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}; // Crockford's base 32 chars
	private static final String URL_CLOVER = "URL_CLOVER";
	private static final String PORT_CLOVER = "PORT_CLOVER";
	
	public static final int errorCode_NO_ERROR = -1;
	public static final int errorCode_NO_COINCIDEIDEXTERNO = 0;
	public static final int errorCode_NO_TERMINOCOBROOK = 1;
	public static final int errorCode_SEPRODUJOEXCEPCION = 2;
	public static final int errorCode_ENVIANDOINFOVENTA = 3;
	public static final int errorCode_NOLOGRACONECTAR = 4;
	public static final int errorCode_REMOTEPAYAPAGADO = 5;
	public static final int errorCode_NO_TERMINOANULACIONOK = 6;
	public static final int errorCode_ENVIANDOINFOANULACION = 7;
	
	private static final String FISCAL_ESTABLISHMENT_NUMBER = "com.clover.regionalextras.ar.MERCHANT_ID_KEY";
	
	
	String cardNumber = "";
	String couponNumber = "";
	String couponBatchNumber = "";
	BigDecimal cashRetirementAmt = Env.ZERO;
	BigDecimal payAmt = Env.ZERO;
	BigDecimal payAmtACobrar = Env.ZERO;
	String card = "";
	String cardType = "";
	String referenceID = "";
	String authorizationID = "";
	String transactionNumber = "";
	String AID = "";
	String documentNo = "";
	int cuotas = 0;
	String plan = "";
	boolean isRetiraEfectivo = true;
	String titularName = "";
	String marcaTarjeta = "";
	String MedioPago = "";
	Payment payment;
	String numeroComercio = "";
	
	private boolean isDebug = true;
	private CloverConnectUtils cu = null;
	
	private static ICloverConnector cloverConnector;

	private static SaleRequest pendingSale;
	private boolean isError = false;
	private String errorMsg = "";
	private MExternalService externalService = null;
	
	private String serviceName = "Clover";
	
	static String urlDefault = "192.168.1.105";
	static int portDefault = 12345;
	
	private boolean isOkConnection = false;
	
	private static VTrxPaymentTerminalForm info = null;
	
	private int reintent = 0;
	private int connected = 0;
	private int reintentos = 4;
	private int timeOut = 5000; // dREHER Valor por defecto del timeOut del WebSocket TODO: ver si puedo hacerlo configurable
		
	private boolean isCargaManual = false;
	private boolean isYaCobrado = false;
	
	private int errorCode = -1;
	private String tipoTransaccion = "COBRO";

	// dREHER Permite guardar la info recibida de una transaccion y la devuelva por Clover
	private StringBuilder trxLog = new StringBuilder();
	private boolean isSaveLog = false;
	
	public boolean isSaveLog() {
		return isSaveLog;
	}

	public void setSaveLog(boolean isSaveLog) {
		this.isSaveLog = isSaveLog;
	}

	public BigDecimal getPayAmtACobrar() {
		return payAmtACobrar;
	}

	public void setPayAmtACobrar(BigDecimal payAmtACobrar) {
		this.payAmtACobrar = payAmtACobrar;
	}

	public TrxClover() {
		// Carga la configuracion de servicio externo
		getExternalService();
		
		// Cargar la cantidad de reintentos que hara Clover ante fallos de conexion
		getReintentosClover();
	}
	
	private void getReintentosClover() {
		String r = MPreference.GetCustomPreferenceValue("ReintentosConectaClover");
		if(r!=null && !r.isEmpty()) {
			try {
				reintentos = Integer.valueOf(r);
			}catch(Exception ex) {
				debug("Mal configurada preferencia 'ReintentosConectaClover'");
			}
		}
	}

	public String getNextId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 13; i++) {
			int idx = random.nextInt(vals.length);
			sb.append(vals[idx]);
		}
		return sb.toString();
	}
	
	public void exitConnect() {
		synchronized (cloverConnector) {
			cloverConnector.notifyAll();
			
			// dREHER v3.0
			if(isSaveLog()) {
				addLog("====================================================\n");
				saveLog();
			}
		}
	}

	public boolean SaleRequest(final VTrxPaymentTerminalForm info, String urlC, String portC) {
		
		// TODO NOTE:  Replace the hard-coded IP address with the correct address from your device
		// Muestra el codigo de paridad para que el usuario lo coloque en Clover
		
		setError(false);
		
		String url = urlC;
		if(url==null || url.isEmpty())
			url = getUrl();
		
		int port = -1;
		if(portC!=null && !portC.isEmpty())
			port = Integer.valueOf(portC);
			
		if(port <= 0)
			port = getPort();
		
		if(url==null || url.isEmpty())
			url = urlDefault;
		if(port<=0)
			port = portDefault;
		
		setDataConnector(url, port);
		
		cu = new CloverConnectUtils();
		CloverDeviceConfiguration devConf = cu.getNetworkConfiguration(url, port, info);
		cloverConnector = CloverConnectorFactory.createICloverConnector(devConf);
		
		cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {

			@Override
			public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
				debug("Confirm Payment Request");

				Challenge[] challenges = request.getChallenges();
				if (challenges != null && challenges.length > 0)
				{
					for (Challenge challenge : challenges) {
						debug("Received a challenge: " + challenge.type);
					}
				}

				debug("Automatically processing challenges");
				cloverConnector.acceptPayment(request.getPayment());
			}


			@Override
			public void onDeviceReady(MerchantInfo merchantInfo) {
				super.onDeviceReady(merchantInfo);
				try {

					pendingSale = new SaleRequest(getPayAmt().longValue(), cu.getNextId());

					debug("Armado del cobro clover:");
					debug("  External ID: " + pendingSale.getExternalId());
					debug("  Amount: " + BigDecimalDiv100(getPayAmt()));
					debug("  Tip Amount: " + pendingSale.getTipAmount());
					debug("  Tax Amount: " + pendingSale.getTaxAmount());
					
					// dREHER antes de enviar la venta, se puede configurar que NO se pueda retirar efectivo
					if(!isRetiraEfectivo())
						pendingSale.setDisableCashback(true);

					// pendingSale.setCardEntryMethods(4); // 1 Solo magnetica, 2 Magnetica/Chip (pide firma y DNI), 4 Magnetica/Chip pero sin firma

					String documentNo = getDocumentNo();

					Map<String, String> extras = new HashMap<String, String>();
					extras.put(RegionalExtras.FISCAL_INVOICE_NUMBER_KEY, getDocumentNo(documentNo));
					extras.put(RegionalExtras.INSTALLMENT_NUMBER_KEY, String.valueOf(getCuotas()));
					extras.put(RegionalExtras.INSTALLMENT_PLAN_KEY, getPlan());
					if(getNumeroComercio()!=null && !getNumeroComercio().isEmpty())
						extras.put(FISCAL_ESTABLISHMENT_NUMBER, getNumeroComercio());
					
					pendingSale.setRegionalExtras(extras);

					// info.setPairingCode("Clover en linea...");
					info.setMsg("Comienza cobro en dispositivo", false);
					info.setMsg("Monto: " + BigDecimalDiv100(getPayAmt()), false);
					info.setMsg("Ticket: " + getDocumentNo(documentNo), false);
					info.setMsg("Cuotas: " + getCuotas(), false);
					info.setMsg("Tarjeta:" + getMarcaTarjeta(), false);
					info.setMsg("Medio Pago:" + getMedioPago(), false);
					info.setMsg("Numero de Comercio:" + getNumeroComercio(), false);
					info.setMsg("Habilita Retiro de Efectivo:" + (isRetiraEfectivo()?"Si":"No"), false);
					if(isRetiraEfectivo())
						info.setMsg("Retirar Efectivo:" + info.getCashARetirementAmt(), false);
					
					setTipoTransaccion("COBRO");
					
					// dREHER v3.0
					addLog("Transaccion: " + "Inicia cobro");
					addLog("Fecha y Hora: " + getTimestamp());
					addLog("Datos recibidos desde: " + "TPV");
					addLog("Monto: " + BigDecimalDiv100(getPayAmt()));
					addLog("Ticket: " + getDocumentNo(documentNo));
					addLog("Cuotas: " + getCuotas());
					addLog("Tarjeta: " + getMarcaTarjeta());
					addLog("Medio Pago: " + getMedioPago());
					addLog("Plan: " + getPlan());
					addLog("Numero de Comercio: " + getNumeroComercio());
					addLog("Habilita Retiro de Efectivo: " + (isRetiraEfectivo()?"Si":"No"));
					addLog("Debe retirar $: " + info.getCashARetirementAmt());
					addLog("----------------------------------------------------");
				
					cloverConnector.sale(pendingSale);
					
				} catch (Exception ex) {
					debug("Error enviando informacion de venta");
					ex.printStackTrace();
					setErrorMsg("Error enviando informacion de venta");
					setError(true);
					setErrorCode(errorCode_ENVIANDOINFOVENTA);
					info.setMsg("ERROR: No se pudo procesar cobro!", true);
					
					// dREHER v3.0
					addLog("Datos recibidos desde : " + "CLOVER");
					addLog("Estado: " + "Error");
					addLog("Codigo de error: " + errorCode_ENVIANDOINFOVENTA);
					addLog("Detalle error: " + "ERROR: No se pudo procesar cobro!");
					
					exitConnect();
				}
			}

			@Override
			public void onSaleResponse(SaleResponse response) {
				try {
					if (response.isSuccess()) {
						
						System.out.println("==> onSaleResponse. Tipo Transaccion: " + getTipoTransaccion());
						
						Payment payment = response.getPayment();
						if (payment.getExternalPaymentId().equals(pendingSale.getExternalId())) {
							
							// Validar que paso con el Payment amount
							// Se encontraron casos donde el monto cobrado en Clover 
							// vuelve multiplicado por 1000 y no por 100
							// dREHER
							

							// Por ahora se pide confirmacion manual desde formulario de TrxClover 
							// cuando encuentre esta diferencia...
							
							// v 3.0
							/*
							if(getPayAmtACobrar().compareTo(Long2BigDecimal(payment.getAmount())) < 0) {
								
								if(getPayAmtACobrar().compareTo(BigDecimalDiv100(Long2BigDecimal(payment.getAmount()))) == 0) {
									payment.setAmount(formatAmount(payment.getAmount()));
								}
								
							}
							*/
							
							// dREHER ATENCION!!!!! SOLO PARA PROVOCAR ERROR
							// payment.setAmount(payment.getAmount()*100L);
							
							
							debug("Clover -> monto cobrado=" + payment.getAmount());
							
							BigDecimal montoClover = Long2BigDecimal(payment.getAmount());
							
							debug("Monto ajustado (%100)=" + montoClover);
							debug("Monto a Cobrar (long)=" + getPayAmtACobrar().longValue());
							
							if(getPayAmtACobrar().longValue() == formatAmount(payment.getAmount())) {
							
								montoClover = getPayAmtACobrar();
								
								debug("Coinciden los montos (long), tomar el origen como correcto=" + montoClover);
							}
							
							
							debug("Solicitud de Cobro exitoso:");
							debug("  ID: " + payment.getId());
							debug("  External ID: " + payment.getExternalPaymentId());
							debug("  Order ID: " + payment.getOrder().getId());
							debug("  Monto: " + Long2BigDecimal(payment.getAmount()));
							// debug("  Tip Amount: " + payment.getTipAmount());
							// debug("  Tax Amount: " + payment.getTaxAmount());
							// debug("  Offline: " + payment.getOffline());
							debug("  Codigo autorizacion: " + payment.getCardTransaction().getAuthCode());
							debug("  Tipo Tarjeta: " + payment.getCardTransaction().getCardType());
							debug("  Ultimos 4: " + payment.getCardTransaction().getLast4());

							// dREHER
							// Titular, dependiendo del tipo de lectura (CardEntryMethods=2)
							debug("  Titular: " + payment.getCardTransaction().getCardholderName());

							// Tipo de tarjeta, Chip, Banda Magnetica, etc.
							debug("  Tipo lectura tarjeta: " + payment.getCardTransaction().getEntryType());

							// Retiro de efectivo
							debug("  Monto retiro efectivo: " + payment.getCashbackAmount());

							// Primeros seis digitos
							debug("  Primeros 6 digitos tarjeta: " + payment.getCardTransaction().getFirst6());

							// Numero de transaccion
							debug("  Transaccion nro: " + payment.getCardTransaction().getTransactionNo());

							debug("  EndBalance: " + payment.getCardTransaction().getEndBalance());

							// Contiene la informacion de la transaccion
							// \"transactionInfo\":{\"cardSymbol\":\"V1\",\"cardTypeLabel\":\"VISA\",\"installmentsQuantity\":2,\"fiscalInvoiceNumber\":\"0001-00000012\","
							//	+ "\"receiptNumber\":\"1452\",\"batchNumber\":\"001\"

							TransactionInfo transactionInfo = payment.getTransactionInfo();

							debug("  Monto #				: " + Long2BigDecimal(payment.getAmount()));
							debug("  Lote #				: " + transactionInfo.getBatchNumber());
							debug("  Cupon #				: " + transactionInfo.getReceiptNumber());
							debug("  Factura #			: " + transactionInfo.getFiscalInvoiceNumber());
							debug("  Plan Code 			: " + transactionInfo.getInstallmentsPlanCode());
							debug("  Plan Desc 			: " + transactionInfo.getInstallmentsPlanDesc());
							debug("  Cuotas 				: " + transactionInfo.getInstallmentsQuantity());
							debug("  Tipo Tarjeta label	: " + transactionInfo.getCardTypeLabel());
							debug("  Tipo Tarjeta symbol	: " + transactionInfo.getCardSymbol());

							// resultado de un payment:

							setPayAmt(Long2BigDecimal(payment.getAmount()));
							if(transactionInfo.getInstallmentsQuantity()!=null)
								setCuotas(transactionInfo.getInstallmentsQuantity());
							setPlan(transactionInfo.getInstallmentsPlanCode());

							setAuthorizationID(payment.getCardTransaction().getAuthCode());
							setCard(transactionInfo.getCardSymbol());
							
							// Paso los numeros que puedo obtener para comparar
							setCardNumber(payment.getCardTransaction().getFirst6() +
									"-" +
									payment.getCardTransaction().getLast4());
							
							setCardType(transactionInfo.getCardTypeLabel());
							
							if(payment.getCashbackAmount()!=null)
								setCashRetirementAmt(Long2BigDecimal(payment.getCashbackAmount()));
							else
								setCashRetirementAmt(new BigDecimal(0));
							
							// TODO OJJJJJJOOOOOOOOOOOOOOOOOOOOOOO!!! Solo para probar
							// setCashRetirementAmt(new BigDecimal(10000));
							
							// *********************************************************************
							// HardCoded test only!!!
							// setCashRetirementAmt(new BigDecimal(100));
							// debug("ATENCION!!! Forzando por codigo retiro en efectivo de $ 100");
							// *********************************************************************
							
							setCouponBatchNumber(transactionInfo.getBatchNumber());
							setCouponNumber(transactionInfo.getReceiptNumber());
							setReferenceID(payment.getId());
							setTransactionNumber(payment.getExternalPaymentId());
							setTitularName(payment.getCardTransaction().getCardholderName());
							
							// Guardo la informacion del ultimo pago realizado
							info.setPayment(payment);
							setPayment(payment);
							
							info.setMsg("Termino de cobrar correctamente!", false);
							info.setMsg("Monto:" + Long2BigDecimal(payment.getAmount()), isError);
							info.setMsg("Cuotas: " + getCuotas(), false);
							info.setMsg("Tarjeta: " + transactionInfo.getCardTypeLabel(), false);
							info.setMsg("Lote: " + transactionInfo.getBatchNumber(), false);
							info.setMsg("Cupon: " + transactionInfo.getReceiptNumber(), false);
							info.setMsg("Titular: " + payment.getCardTransaction().getCardholderName(), false);
							info.setMsg("Primeros 6 digitos:" + payment.getCardTransaction().getFirst6(), false);
							info.setMsg("Ultimos 4 digitos:" + payment.getCardTransaction().getLast4(), false);
							if(payment.getCashbackAmount()!=null)
								info.setMsg("Retiro Efectivo:" + getCashRetirementAmt(), false);
							
							
							// dREHER v3.0
							addLog("Datos recibidos desde: " + "CLOVER");
							addLog("Referencia ID: " + payment.getId());
							addLog("Transaction Number: " + payment.getExternalPaymentId());
							addLog("Estado: " + "Transaccion Ok");
							addLog("Monto: " + Long2BigDecimal(payment.getAmount()));
							addLog("Tarjeta: " + transactionInfo.getCardTypeLabel());
							addLog("Lote: " + transactionInfo.getBatchNumber());
							addLog("Cupon: " + transactionInfo.getReceiptNumber());
							addLog("Titular: " + payment.getCardTransaction().getCardholderName());
							addLog("Primeros 6 digitos: " + payment.getCardTransaction().getFirst6());
							addLog("Ultimos 4 digitos: " + payment.getCardTransaction().getLast4());
							addLog("Retiro de Efectivo $: " + payment.getCashbackAmount());

							
						} else {
							debug("Sale Request/Response mismatch - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
							setErrorMsg("Sale Request/Response mismatch - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
							setError(true);
							info.setMsg("ERROR: " +
									"Diferencias en el Envio/Respuesta del cobro - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId(), true);
							setErrorCode(errorCode_NO_COINCIDEIDEXTERNO);
							
							// dREHER v3.0
							addLog("Datos recibidos desde : " + "CLOVER");
							addLog("Estado: " + "Error");
							addLog("Codigo de error: " + errorCode_NO_COINCIDEIDEXTERNO);
							addLog("Detalle error: " + "Diferencias en el Envio/Respuesta del cobro - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
						}
					} else {
						debug("Sale Request Failed - response.Success=NO. " + response.getReason());
						setErrorMsg("Sale Request Failed - response.Success=NO. " + response.getReason());
						setError(true);
						info.setMsg("ERROR: " +
									"Respuesta de Clover con error: " + response.getReason(), true);
						setErrorCode(errorCode_NO_TERMINOCOBROOK);
						
						// dREHER v3.0
						addLog("Datos recibidos desde : " + "CLOVER");
						addLog("Estado: " + "Error");
						addLog("Codigo de error: " + errorCode_NO_TERMINOCOBROOK);
						addLog("Detalle error: " + "Respuesta de Clover con error: " + response.getReason());
					}
				} catch (Exception ex) {
					debug("Error handling sale response");
					setErrorMsg("Error handling sale response");
					setError(true);
					info.setMsg("ERROR: " + 
							" No se puede procesar respuesta del cobro", true);
					setErrorCode(errorCode_SEPRODUJOEXCEPCION);
					
					// dREHER v3.0
					addLog("Datos recibidos desde : " + "CLOVER");
					addLog("Estado: " + "Error");
					addLog("Codigo de error: " + errorCode_SEPRODUJOEXCEPCION);
					addLog("Detalle error: " + "No se puede procesar respuesta del cobro ");
					
					ex.printStackTrace();
				}

				// Desconectar de Clover
				exitConnect();
				
			}

			@Override
			public void onVerifySignatureRequest(VerifySignatureRequest request) {
				super.onVerifySignatureRequest(request);
				debug("Verify Signature Request - Signature automatically accepted by default");
			}
			
			@Override
			public void onDeviceConnected() {
				connected++;
				debug("Esta conectado..." + connected);
				if(connected > 1) {
					info.setMsg("App Secure Remote Pay NO ENCENDIDO", true);
					setError(true);
					setErrorCode(errorCode_REMOTEPAYAPAGADO);
					exitConnect();
				}
			}
			
			@Override 
			public void onDeviceDisconnected() {
				
				reintent++;
				info.setMsg("Intento de conexion: " + reintent, true);
				if(reintent > getReintentos()) {
					System.out.println("Error Connection, abort!!!");
					info.setMsg("No se pudo conector con Clover, abortando...", true);
					info.setPayment(null);
					setError(true);
					setErrorCode(errorCode_NOLOGRACONECTAR);
					exitConnect();
				}
			}

		});

		// Inicializa conexion
		cloverConnector.initializeConnection();

		// comienza espera de avance de cobro en Clover
		synchronized (cloverConnector) {
			try {
				cloverConnector.wait();
			} catch (Exception ex) {
				debug("Exit signaled");
			}
		}

		cloverConnector.dispose();

		return !isError(); // true SI NO SE PRODUJO ERROR!
	}

	// dREHER v3.0
	protected void addLog(String string) {
		if(isSaveLog())
			trxLog.append(string + "\n");
	}
	
	// dREHER v3.0
	protected void saveLog() {
		String fileName = "trxClover_" + getDocumentNo().substring(0,5) + ".log";
		 
		if(!isSaveLog())
			return;
		
		try {
			String userHome = System.getProperty("user.home");
			String fileSeparator = System.getProperty("file.separator");
			FileWriter writer = new FileWriter(userHome + fileSeparator + fileName, true); // true para append
			writer.write(getTrxLog().toString());
			writer.close();
			debug("Log guardado en: " + userHome + fileSeparator + fileName);
		} catch (IOException e) {
			debug("Error al escribir en el archivo de log: " + e.getMessage());
		}
		
	}

	public StringBuilder getTrxLog() {
		return trxLog;
	}

	public void setTrxLog(StringBuilder trxLog) {
		this.trxLog = trxLog;
	}

	protected String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date now = new Date();
		String strDate = sdf.format(now);
		//  System.out.println("Fecha y hora actual: " + strDate);
		return strDate;
	}

	protected BigDecimal BigDecimalDiv100(BigDecimal payAmt2) {
		return payAmt2.divide(Env.ONEHUNDRED, 2, RoundingMode.HALF_UP);
	}

	protected BigDecimal Long2BigDecimal(Long amount) {
		return new BigDecimal(amount).divide(Env.ONEHUNDRED, 2, RoundingMode.HALF_UP);
	}

	public boolean RefundSaleRequest(final VTrxPaymentTerminalForm info, String urlC, String portC, final Payment payment) {
		
		// TODO NOTE:  Replace the hard-coded IP address with the correct address from your device
		// Muestra el codigo de paridad para que el usuario lo coloque en Clover
		
		setError(false);
		String url = urlC;
		if(url==null || url.isEmpty())
			url = getUrl();
		
		int port = -1;
		if(portC!=null && !portC.isEmpty())
			port = Integer.valueOf(portC);
			
		if(port <= 0)
			port = getPort();
		
		if(url==null || url.isEmpty())
			url = urlDefault;
		if(port<=0)
			port = portDefault;
		
		setDataConnector(url, port);
		
		cu = new CloverConnectUtils();
		CloverDeviceConfiguration devConf = cu.getNetworkConfiguration(url, port, info);
		cloverConnector = CloverConnectorFactory.createICloverConnector(devConf);
		
		cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {

			@Override
			public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
				debug("Confirm Payment Request");

				Challenge[] challenges = request.getChallenges();
				if (challenges != null && challenges.length > 0)
				{
					for (Challenge challenge : challenges) {
						debug("Received a challenge: " + challenge.type);
					}
				}

				debug("Automatically processing challenges");
				cloverConnector.acceptPayment(request.getPayment());
			}


			@Override
			public void onDeviceReady(MerchantInfo merchantInfo) {
				super.onDeviceReady(merchantInfo);
				try {

					System.out.println("Issuing Refund...");
					
					info.setMsg("Comienza devolucion en dispositivo", false);
					info.setMsg("Importe: " + Long2BigDecimal(payment.getAmount()), false);
					info.setMsg("Pago: " + payment.getOrder().getId(), false);
					info.setMsg("Ticket: " + payment.getId(), false);
					info.setMsg("Tarjeta:" + getMarcaTarjeta(), false);
					info.setMsg("Medio Pago:" + getMedioPago(), false);
					
					RefundPaymentRequest refundRequest = new RefundPaymentRequest();
					refundRequest.setPaymentId(payment.getId());
					refundRequest.setOrderId(payment.getOrder().getId());
					refundRequest.setFullRefund(true);
					
					setTipoTransaccion("ANULACION");
					
					cloverConnector.refundPayment(refundRequest);
					
					// dREHER v3.0
					addLog("Transaccion: " + "Inicia anulacion cobro");
					addLog("Fecha y Hora: " + getTimestamp());
					addLog("Datos recibidos desde : " + "TPV");
					addLog("Monto: " + Long2BigDecimal(payment.getAmount()));
					addLog("Pago: " + payment.getOrder().getId());
					addLog("Ticket: " + payment.getId());
					addLog("Tarjeta: " + getMarcaTarjeta());
					addLog("Medio Pago: " + getMedioPago());
					addLog("----------------------------------------------------");

				} catch (Exception ex) {
					debug("Error enviando informacion de anulacion de venta");
					ex.printStackTrace();
					setErrorMsg("Error enviando informacion de anulacion de venta");
					setError(true);
					info.setMsg("ERROR: No se pudo procesar anulacion de cobro!", true);
					setErrorCode(errorCode_ENVIANDOINFOANULACION);

					// dREHER v3.0
					addLog("Datos recibidos desde : " + "CLOVER");
					addLog("Estado: " + "Error");
					addLog("Codigo de error: " + errorCode_ENVIANDOINFOANULACION);
					addLog("Detalle error: " + "ERROR: No se pudo procesar anulacion de cobro!");
					
					exitConnect();
					
				}
			}
			
			@Override
			public void onRefundPaymentResponse(RefundPaymentResponse response) {
				try {
					if (response.isSuccess()) {
						Refund refund = response.getRefund();
						debug("Refund Request Successful");
						debug("  ID: " + refund.getId());
						debug("  Amount: " + Long2BigDecimal(refund.getAmount()));
						debug("  Order ID: " + response.getOrderId());
						debug("  Payment ID: " + response.getPaymentId());
						
						info.setMsg("Termino de anular cobro correctamente!", false);
						info.setMsg("ID anulacion: " + refund.getId(), false);
						info.setMsg("  Monto: " + Long2BigDecimal(refund.getAmount()), false);
						info.setMsg("  Pedido: " + response.getOrderId(), false);
						info.setMsg("  Pago ID: " + response.getPaymentId(), false);

						// dREHER v3.0
						addLog("Datos recibidos desde : " + "CLOVER");
						addLog("Estado: " + " Anulado ok");
						addLog("Monto anulado: " + Long2BigDecimal(refund.getAmount()));
						addLog("----------------------------------------------------");
						
					} else {
						debug("Refund Request Failed - " + response.getReason());
						setError(true);
						setErrorCode(errorCode_NO_TERMINOANULACIONOK);
						info.setMsg("ERROR: La anulacion no termino OK - " + response.getReason(), true);

						// dREHER v3.0
						addLog("Datos recibidos desde : " + "CLOVER");
						addLog("Estado: " + "Error");
						addLog("Codigo de error: " + errorCode_NO_TERMINOANULACIONOK);
						addLog("Detalle error: " + "ERROR: La anulacion no termino OK - " + response.getReason());
					}

				} catch (Exception ex) {
					debug("Error handling refund sale response");
					info.setMsg("ERROR: no se pudo procesar respuesta de anulacion Clover!", true);
					setErrorCode(errorCode_SEPRODUJOEXCEPCION);
					ex.printStackTrace();

					// dREHER v3.0
					addLog("Datos recibidos desde : " + "CLOVER");
					addLog("Estado: " + "Error");
					addLog("Codigo de error: " + errorCode_SEPRODUJOEXCEPCION);
					addLog("Detalle error: " + "ERROR: no se pudo procesar respuesta de anulacion Clover!");
				}

				// Desconectar de Clover
				exitConnect();
				
				
			}
			
			@Override
			public void onVerifySignatureRequest(VerifySignatureRequest request) {
				super.onVerifySignatureRequest(request);
				debug("Verify Signature Request - Signature automatically accepted by default");
			}
			
			@Override
			public void onDeviceConnected() {
				connected++;
				debug("Esta conectado..." + connected);
				if(connected > 1) {
					info.setMsg("App Secure Remote Pay NO ENCENDIDO", true);
					setError(true);
					setErrorCode(errorCode_REMOTEPAYAPAGADO);
					exitConnect();
				}
			}
			
			@Override 
			public void onDeviceDisconnected() {
				
				reintent++;
				info.setMsg("Intento de conexion: " + reintent, true);
				if(reintent > getReintentos()) {
					System.out.println("Error Connection, abort!!!");
					info.setMsg("No se pudo conector con Clover, abortando...", true);
					info.setPayment(null);
					setError(true);
					setErrorCode(errorCode_NOLOGRACONECTAR);
					exitConnect();
				}
			}

		});

		// Inicializa conexion y comienza espera de avance de cobro en Clover
		cloverConnector.initializeConnection();

		synchronized (cloverConnector) {
			try {
				cloverConnector.wait();
			} catch (Exception ex) {
				debug("Exit signaled");
			}
		}

		cloverConnector.dispose();

		return !isError(); // true SI NO SE PRODUJO ERROR!
	}
	
	/**
	 * Permite validar la conexion con el dispositivo Clover
	 * @param urlC
	 * @param portC
	 * @return true->conexion OK, false=no conecta
	 * dREHER
	 */
	public boolean isOkConnection(final VTrxPaymentTerminalForm info, String urlC, String portC) {
		
		return isOkConnection(info, urlC, portC, false);
	}
	
	/**
	 * Permite validar la conexion con el dispositivo Clover
	 * @param urlC
	 * @param portC
	 * @param clean -> limpia la transaccion de Clover
	 * @return true->conexion OK, false=no conecta
	 * dREHER
	 */
	public boolean isOkConnection(final VTrxPaymentTerminalForm info, String urlC, String portC, final boolean clean) {
		
		setError(false);
		String url = urlC;
		if(url==null || url.isEmpty())
			url = getUrl();

		int port = -1;
		if(portC!=null && !portC.isEmpty())
			port = Integer.valueOf(portC);

		if(port <= 0)
			port = getPort();

		if(url==null || url.isEmpty())
			url = urlDefault;
		if(port<=0)
			port = portDefault;

		setDataConnector(url, port);
		
		setTipoTransaccion("TESTCONEXION");

		cu = new CloverConnectUtils();
		CloverDeviceConfiguration devConf = cu.getNetworkConfiguration(url, port, info);
		cloverConnector = CloverConnectorFactory.createICloverConnector(devConf);

		cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
			
			@Override
			public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
				System.out.println("==> Test de conexion. request=" + request);
			}

			@Override
			public void onDeviceReady(MerchantInfo merchantInfo) {
				// funciona ok super.onDeviceReady(merchantInfo);
				
				if(clean) {
					
					// CloseoutRequest cr = new CloseoutRequest();
					// cr.setExternalReferenceId();
					// cloverConnector.closeout(cr);
					// cloverConnector.cancel();
					cloverConnector.resetDevice();
				}

				super.onDeviceReady(merchantInfo);

				
				System.out.println("Test Connection Successful!!!");
				info.setMsg("Conecto Ok!", false);
				isOkConnection = true;
				exitConnect();
			}
			
			@Override
			public void onDeviceConnected() {
				connected++;
				debug("Esta conectado..." + connected);
				if(connected > 1) {
					info.setMsg("App Secure Remote Pay NO ENCENDIDO", true);
					setError(true);
					exitConnect();
				}
			}
			
			@Override 
			public void onDeviceDisconnected() {
				
				reintent++;
				info.setMsg("Intento de conexion: " + reintent, true);
				if(reintent > getReintentos()) {
					System.out.println("Error Connection, abort!!!");
					info.setMsg("ERROR: No se pudo conector con Clover, abortando...", true);
					setError(true);
					exitConnect();
				}
			}

			
		});

		cloverConnector.initializeConnection();
		
		synchronized (cloverConnector) {
			try {
				cloverConnector.wait();
			} catch (RuntimeException e) {
				System.out.println("RuntimeException. Exit signaled");
			} catch (Exception e) {
				System.out.println("Exception. Exit signaled");
			}
		}

		cloverConnector.dispose();
		return isOkConnection;
	}

	/**
	 * Devuelve la cantidad que debe reintentar cuando no logra conectar con Clover
	 * @return int
	 * dREHER
	 */
	public int getReintentos() {
		return reintentos;
	}
	/**
	 * Setea la cantidad que debe reintentar cuando no logra conectar con Clover
	 * @return int
	 * dREHER
	 */
	public void setReintentos(int reintentos) {
		this.reintentos = reintentos;
	}
	

	private void debug(String string) {
		if(isDebug)
			System.out.println("TrxClover. " + string);
	}

	/**
	 * Se divide por 100 ya que Clover muestra $1,00 como $ 100
	 * @param amount
	 * @return
	 * dREHER
	 */
	private Long formatAmount(Long amount) {
		
		return amount / 100;
	}

	/**
	 * Este metodo realiza el cierre de la transaccion Clover
	 * 
	 */
	public void close() {
		
		synchronized (cloverConnector) {
			cloverConnector.notifyAll();
		}
		
		cloverConnector.dispose();
	}

	/**
	 * Inicializa con info de ejemplo basica
	 */
	public void setDefault() {
		this.setAID("");
		this.setAuthorizationID(authorizationID);
		this.setCard(card);
		this.setCardNumber(cardNumber);
		this.setCardType(cardType);
		this.setCashRetirementAmt(cashRetirementAmt);
		this.setCouponBatchNumber(couponBatchNumber);
		this.setCouponNumber(couponNumber);
		this.setPayAmt(payAmt);
		this.setReferenceID(referenceID);
		this.setTransactionNumber(transactionNumber);
		this.setCargaManual(false);
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCouponNumber() {
		return couponNumber;
	}

	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
	}

	public String getCouponBatchNumber() {
		return couponBatchNumber;
	}

	public void setCouponBatchNumber(String couponBatchNumber) {
		this.couponBatchNumber = couponBatchNumber;
	}

	public BigDecimal getCashRetirementAmt() {
		return cashRetirementAmt;
	}

	public void setCashRetirementAmt(BigDecimal cashRetirementAmt) {
		this.cashRetirementAmt = cashRetirementAmt;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public String getNumeroComercio() {
		return numeroComercio;
	}

	public void setNumeroComercio(String numeroComercio) {
		this.numeroComercio = numeroComercio;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	public String getAuthorizationID() {
		return authorizationID;
	}

	public void setAuthorizationID(String authorizationID) {
		this.authorizationID = authorizationID;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getAID() {
		return AID;
	}

	public void setAID(String aID) {
		AID = aID;
	}

	/**
	 * Formateo de numero de documento para Clover
	 * @param documentNo
	 * @return nnnnn-nnnnnnnn
	 */
	public String getDocumentNo(String documentNo) {
		if(documentNo==null)
			documentNo = getDocumentNo();
		if(documentNo==null)
			return "";
		
		String formatedDocumentNo = documentNo;
		if(documentNo.length() >= 13) {
			formatedDocumentNo = documentNo.substring(1);
			formatedDocumentNo = formatedDocumentNo.substring(0, 4) + "-" + formatedDocumentNo.substring(4); 
		}
		
		debug("getDocumentNo. Numero factura original: " + documentNo + " Clover format=" + formatedDocumentNo);
		
		return formatedDocumentNo;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public static SaleRequest getPendingSale() {
		return pendingSale;
	}

	public static void setPendingSale(SaleRequest pendingSale) {
		TrxClover.pendingSale = pendingSale;
	}

	public boolean isRetiraEfectivo() {
		return isRetiraEfectivo;
	}

	public void setRetiraEfectivo(boolean isRetiraEfectivo) {
		this.isRetiraEfectivo = isRetiraEfectivo;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	

	@Override
	public String getTitularName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTitularName(String titularName) {
		// TODO Auto-generated method stub
		
	}
	
	public Payment getPayment() {
		return payment;
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
// -------------------------- Metodos de transaccion Clover ------------------------------ //	

	@Override
	public void getExternalService() {
		
		if(externalService!=null)
			return;
		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	UPPER(name) = UPPER(?) ");

		int C_ExternalService_ID = DB.getSQLValue(null, sql.toString(), serviceName);
		if(C_ExternalService_ID > 0)
			externalService = new MExternalService(Env.getCtx(), C_ExternalService_ID, null);
	}
	
	@Override
	public String getExternalServiceAttribute(String attrib) {
		
		if(externalService==null)
			return null;
		
		X_C_ExternalServiceAttributes attr = externalService.getAttributeByName(attrib);
		String value = attr.getName();
		
		return value;
	}

	@Override
	public String getUrl() {
		String url = Ini.getProperty( URL_CLOVER );
		if(url.isEmpty())
			url = null;
		return url;
	}

	@Override
	public int getPort() {
		String tmp = Ini.getProperty( PORT_CLOVER );
		if(tmp.isEmpty())
			tmp = "-1";

		int port = -1;
		try {
			port = Integer.valueOf(tmp);
		}catch(Exception ex) {}

		if(port <= 0)
			port = -1;		
		return port;
	}
	

	/**
	 * TODO: ver luego si esto no se guarda en el TPV como parte de la configuracion...
	 * @param url
	 * @param port
	 */
	private void setDataConnector(String url, int port) {
		Ini.setProperty( URL_CLOVER, url);
		Ini.setProperty( PORT_CLOVER, port);
	}

	public VTrxPaymentTerminalForm getInfo() {
		return info;
	}

	public void setInfo(VTrxPaymentTerminalForm info) {
		TrxClover.info = info;
	}

	public String getMarcaTarjeta() {
		return marcaTarjeta;
	}

	public void setMarcaTarjeta(String marcaTarjeta) {
		this.marcaTarjeta = marcaTarjeta;
	}

	public String getMedioPago() {
		return MedioPago;
	}

	public void setMedioPago(String medioPago) {
		MedioPago = medioPago;
	}

	public boolean isCargaManual() {
		return isCargaManual;
	}

	public void setCargaManual(boolean isCargaManual) {
		this.isCargaManual = isCargaManual;
	}

	public boolean isYaCobrado() {
		return isYaCobrado;
	}

	public void setYaCobrado(boolean isYaCobrado) {
		this.isYaCobrado = isYaCobrado;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getTipoTransaccion() {
		return tipoTransaccion;
	}

	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}

}
