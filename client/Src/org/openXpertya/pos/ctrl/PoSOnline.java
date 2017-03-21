package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.form.VModelHelper;
import org.openXpertya.cc.CurrentAccountBalanceStrategy;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.GeneratorPercepciones;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MAttributeSet;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MCategoriaIva;
import org.openXpertya.model.MCheckCuitControl;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoiceTax;
import org.openXpertya.model.MLetraComprobante;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MOrderTax;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductPO;
import org.openXpertya.model.MProductPrice;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MUser;
import org.openXpertya.model.M_Tab;
import org.openXpertya.model.PO;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.pos.exceptions.FiscalPrintException;
import org.openXpertya.pos.exceptions.InsufficientBalanceException;
import org.openXpertya.pos.exceptions.InsufficientCreditException;
import org.openXpertya.pos.exceptions.InvalidOrderException;
import org.openXpertya.pos.exceptions.InvalidPaymentException;
import org.openXpertya.pos.exceptions.InvalidProductException;
import org.openXpertya.pos.exceptions.InvoiceCreateException;
import org.openXpertya.pos.exceptions.PosException;
import org.openXpertya.pos.exceptions.UserException;
import org.openXpertya.pos.model.BankTransferPayment;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.CashPayment;
import org.openXpertya.pos.model.CheckPayment;
import org.openXpertya.pos.model.CreditCardPayment;
import org.openXpertya.pos.model.CreditNotePayment;
import org.openXpertya.pos.model.CreditPayment;
import org.openXpertya.pos.model.DiscountSchema;
import org.openXpertya.pos.model.EntidadFinanciera;
import org.openXpertya.pos.model.EntidadFinancieraPlan;
import org.openXpertya.pos.model.Location;
import org.openXpertya.pos.model.Order;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.Organization;
import org.openXpertya.pos.model.Payment;
import org.openXpertya.pos.model.PaymentMedium;
import org.openXpertya.pos.model.PaymentTerm;
import org.openXpertya.pos.model.PriceList;
import org.openXpertya.pos.model.PriceListVersion;
import org.openXpertya.pos.model.Product;
import org.openXpertya.pos.model.ProductList;
import org.openXpertya.pos.model.Tax;
import org.openXpertya.pos.model.User;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.print.View;
import org.openXpertya.print.fiscal.document.CurrentAccountInfo;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.InvoiceGlobalVoiding;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AccumulableTask;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.MProductCache;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeStatsAccumulator;
import org.openXpertya.util.TimeStatsLogger;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class PoSOnline extends PoSConnectionState {

	private final boolean LOCAL_AR_ACTIVE = CalloutInvoiceExt.ComprobantesFiscalesActivos();
	private final boolean CHECK_CUIT_CONTROL_ACTIVE = MCheckCuitControl.isCheckCUITControlActive();
	
	private final String MAX_ORDER_LINE_QTY_PREFERENCE_NAME = "L_AR_MaxOrderLineQty";
	
	// ID de pestañas utilizadas para obtener los reportes de impresión.
	private final int ORDER_TAB_ID = 186;
	private final int INVOICE_TAB_ID = 263;
	
	private Properties ctx = Env.getCtx();
	private String trxName = null;
	//private Trx trx = null;
	private MBPartner partner = null;
	private Timestamp invoiceDate = null;
	//private int C_Currency_ID ;
	private MOrder morder = null;
	private MInvoice invoice = null;
	private MInOut shipment = null;
	private MAllocationHdr allocHdr = null;
	private MOrg mOrg = null;
	
	private HashMap<Integer, MPayment> mpayments = new HashMap<Integer, MPayment>();
	// private Vector<MPayment> mpayments = new Vector<MPayment>();
	private Vector<MAllocationLine> allocLines = new Vector<MAllocationLine>();
	
	protected BigDecimal sumaPagos = null;
	protected BigDecimal sumaProductos = null;
	
	protected BigDecimal sumaCashPayments = null;
	protected BigDecimal sumaCheckPayments = null;
	protected BigDecimal sumaCreditCardPayments = null;
	protected BigDecimal sumaCreditPayments = null;
	protected BigDecimal sumaCreditNotePayments = null;
	protected BigDecimal sumaBankTransferPayments = null;
	
	protected Vector<CashPayment> cashPayments = new Vector<CashPayment>();
	protected Vector<CheckPayment> checkPayments = new Vector<CheckPayment>();
	protected Vector<CreditCardPayment> creditCardPayments = new Vector<CreditCardPayment>();
	protected Vector<CreditPayment> creditPayments = new Vector<CreditPayment>();
	protected Vector<CreditNotePayment> creditNotePayments = new Vector<CreditNotePayment>();
	protected Vector<BankTransferPayment> bankTransferPayments = new Vector<BankTransferPayment>();
	
	private BigDecimal sobraPorCheques = null;
	private BigDecimal faltantePorRedondeo = null;
	
	private boolean shouldCreateInvoice;
	private boolean shouldCreateInout;
	private boolean shouldUpdateBPBalance;
	
	private Map<Integer, MCashLine> mCashLines = new HashMap<Integer, MCashLine>();
	private Map<PO, Object> aditionalWorkResults = new HashMap<PO, Object>();
	
	private Map<Integer, PaymentTerm> paymentTerms = new HashMap<Integer, PaymentTerm>();
	
	private List<Integer> checkDeadLines = null;
	private List<EntidadFinanciera> entidadesFinancieras = null;
	
	private DiscountCalculator discountCalculator = null;
	
	private Map<String, BigDecimal> currentAccountSalesConditions;
	
	private CreatePOSPaymentValidations createPOSPaymentValidations;
	
	private GeneratorPercepciones generatorPercepciones;
	
	private CompleteOrderPOSValidations completeOrderPOSValidations;
	
	private MDocType paymentDocType = null;
	
	private MTax taxExento = null;
	
	/** Perfil actual */
	private MRole role = null;
	
	private int dolarCurrencyID = 0;
	
	private MDocType allocationDocType = null;
	
	public PoSOnline() {
		super();
		setCreatePOSPaymentValidations(new CreatePOSPaymentValidations());
		setCompleteOrderPOSValidations(new CompleteOrderPOSValidations());
		setmOrg(MOrg.get(ctx, Env.getAD_Org_ID(ctx)));
		setGeneratorPercepciones(new GeneratorPercepciones(getCtx(), null));
		getGeneratorPercepciones().setTPVInstance(true);
		setPaymentDocType(MDocType.getDocType(ctx,
				MDocType.DOCTYPE_CustomerReceipt, null));
		setTaxExento(MTax.getTaxExemptRate(ctx, null));
		setRole(MRole.get(getCtx(), Env.getAD_Role_ID(getCtx())));
		setAllocationDocType(MDocType.getDocType(ctx,
				MDocType.DOCTYPE_POS, null));
	}
	
	private static void throwIfFalse(boolean b, DocAction sourceDocActionPO, Class posExceptionClass) throws PosException {
		if (!b) 
		{
			ValueNamePair np = CLogger.retrieveError();
			String msg = null;
			// Se intenta obtener el mensaje a partir del Logger.
			if (np != null) {

				String name = (np.getName() != null) ? Msg.translate(Env.getCtx(), np.getName()) : "";
				String value = (np.getValue() != null) ? Msg.translate(Env.getCtx(), np.getValue()) : "";
				if (name.length() > 0 && value.length() > 0)
					msg = value + ": " + name;
				else if (name.length() > 0)
					msg = name;
				else if (value.length() > 0)
					msg = value;
				else
					msg = "";
			
			// Se intenta obtener un mensaje a partir del mensaje de los POs que implementan DocAction.
			} else if (sourceDocActionPO != null && sourceDocActionPO.getProcessMsg() != null &&
					 sourceDocActionPO.getProcessMsg().length() > 0) {
			
				msg = Msg.parseTranslation(Env.getCtx(), sourceDocActionPO.getProcessMsg());
			
			}
		
			PosException e;
			try {
				e =(PosException)posExceptionClass.newInstance();
			} catch (Exception e2) {
				e2.printStackTrace();
				e = new PosException(); 
			}
			if (msg != null)
				e.setMessage(msg);
			
			throw e;
		}
	}
	
	private static void throwIfFalse(boolean b) throws PosException {
		throwIfFalse(b, null, PosException.class);
	}
	
	private static void throwIfFalse(boolean b, Class posExceptionClass) throws PosException {
		throwIfFalse(b, null, posExceptionClass);
	}

	private static void throwIfFalse(boolean b, DocAction sourceDocActionPO) throws PosException {
		throwIfFalse(b, sourceDocActionPO, PosException.class);
	}
	
	/**
	 * El metodo completeOrder:
	 * 
	 * <ol>
	 * <li>Valida que el saldo sea cero (si no lo es, dispara una InsufficientBalanceException). 
	 * 
	 * <li>Valida si tiene credito (si no lo tiene, dispara una InsufficientCreditException).  
	 * 
	 * <li>Crea el pedido (El pedido es un MOrder). 
	 * 
	 * <li>Crea la factura (MInvoice). 
	 * 
	 * <li>Crea el albarán (MInOut, solo si está configurado para tal fin). 
	 * 
	 * <li>Crea los pagos (MPayment, MCashLine). 
	 * 
	 * <li>Crea el allocation (MAllocationHdr). 
	 * </ol>
	 * 
	 * @param order la orden que se desea completar
	 * @throws PosException
	 * @throws InsufficientCreditException
	 */
	@Override
	public void completeOrder(Order order, Set <Integer> ordersId) throws PosException, InsufficientCreditException, InsufficientBalanceException, InvalidPaymentException, InvalidProductException {
		Trx trx = null; // LOCAL. Solo para hacer rollback o commit
		try {
		
			trxName = createTrxName();
			trx = Trx.get(trxName, true);
			getGeneratorPercepciones().setTrxName(trxName);
			//ADER, para que aparezca en el log de postgres
			//DB.getSQLObject(getTrxName(), "select 'Comenzando completeOrder'", null);		
	
			//ADER, iniciazliacion de caches
			initCachesFromOrder(order);
			
			// Validaciones extra iniciales
			getCompleteOrderPOSValidations().validateInitialCompleteOrder(this, order);
			
			// clearState(order);
			debug("Chequeando saldo y crédito");
			checkSaldo(order);
			
			// Se controla el crédito sólo si hay que crear factura
			if(getShouldCreateInvoice()){
				checkCredit(order);
				
				// Control de CUIT de cheques
				checkCUITControl(order);
			}
			
			// MOrder
			// Validaciones extras al crear el pedido
			getCompleteOrderPOSValidations().validateOrder(this, order);
			debug("Creando Pedido (MOrder)");
			morder = createOxpOrder(order);
			
			// MInvoice 
			
			if (getShouldCreateInvoice()) {
				// Validaciones extras al crear la factura
				getCompleteOrderPOSValidations().validateInvoice(this, order);
				debug("Creando Factura (MInvoice)");
				invoice = createOxpInvoice(order);
				
				invoice.addDocActionStatusListener(getDocActionStatusListener());
				debug("Chequeando Factura");
				checkInvoice();
			}
			
			debug("Guardando los descuentos");
			// Guarda los descuentos
			saveDiscounts(order);
			
			// TODO: Crear TICKET ?
			
			// MInOut: Albarán, Remito.
			
			if (getShouldCreateInout()) {
				// Validaciones extras al crear el remito
				getCompleteOrderPOSValidations().validateInOut(this, order);
				debug("Creando Remito (MInOut)");
				shipment = createOxpInOut(order); 
			}
			
			// Se crea el allocation y los pagos en el caso que se cree la
			// factura sino no tiene sentido
			
			if (getShouldCreateInvoice()) {
				// Validaciones extras al crear el allocation
				getCompleteOrderPOSValidations().validateAllocation(this, order);
				// Allocation Header
				debug("Creando Allocation");
				allocHdr = createOxpAllocation();
				
				adjustPayments(order);
				
				// Crear los MPayments & MAllocationLine, o MCashLine
				// Validaciones extras al crear los pagos
				getCompleteOrderPOSValidations().validatePayments(this, order);
				debug("Creando los pagos (MPayment & MCashLine)");
				createOxpPayments(order);
				debug("Completando el allocation");
				doCompleteAllocation();
			}

			// Validaciones extras al finalizar
			getCompleteOrderPOSValidations().validateEndCompleteOrder(this, order);
			
			// Realizar las tareas de cuenta corriente antes de finalizar
			if (shouldUpdateBPBalance) {
				debug("Acciones de cuenta corriente");
				performAditionalCurrentAccountWork(order);
			}
//
//			FB - Comentado a partir del quitado de la impresión fiscal de 
//			la transacción principal
//			
//			/*
//			 * IMPORTANTE: Completado de Factura.
//			 * Aquí se completa la factura solo si debe ser emitida mediante un controlador
//			 * fiscal. Esta operación debe ser la última del conjunto dado que una vez
//			 * emitido el comrpobante impreso, la transacción debe confirmarse para que
//			 * los datos queden consistentes. (papel impreso con factura creada en la BD).
//			 * Si luego de esta operación se agrega otra (por ejemplo la creación de otro
//			 * documento) y esto falla, la transacción se anularía dejando como resultado
//			 * un comprobante impreso sin su factura correspondiente en la base de datos.
//			 */
//			if (invoice != null && needFiscalPrint(invoice)){
//				debug("Completando la factura para Impresión Fiscal");
//				throwIfFalse(invoice.processIt(DocAction.ACTION_Complete), invoice, InvoiceCreateException.class);
//			}
			
			// Actualizar el crédito de la entidad comercial
			if(shouldUpdateBPBalance){
				debug("Actualizando crédito de la Entidad Comercial");
				afterProcessDocuments(order);
			}
			//ADER, para que aparezca en el log de postgres
			DB.getSQLObject(getTrxName(), "select 'Finalizando completeOrder'", null);		
			
			debug("Commit de Transaccion");
			throwIfFalse(trx.commit());
			TimeStatsLogger.endTask(MeasurableTask.POS_SAVE_DOCUMENTS);
			
			trxName = null;
		} catch (PosException e) {
			/*
			try {
				if (morder != null && morder.getID() > 0) {
					morder.processIt(DocAction.ACTION_Void);
					morder.save();
				}
			} catch (Exception e2) {}

			try {
				if (invoice != null && invoice.getID() > 0) {
					morder.processIt(DocAction.ACTION_Void);
					morder.save();
				}
			} catch (Exception e2) {}
			
			try {
				if (shipment != null && shipment.getID() > 0) {
					shipment.processIt(DocAction.ACTION_Void);
					shipment.save();
				}
			} catch (Exception e2) {}
			*/
			try {
				trx.rollback();
			} catch (Exception e2) {
				
			}
			
			throw e;
		} catch (Exception e) {
			try {
				trx.rollback();
			} catch (Exception e2) {}
			throw new PosException(e);
		} finally {
			trxName = null;
			getGeneratorPercepciones().setTrxName(trxName);
			try {
				trx.close();
			} catch (Exception e2) {
				
			}
		}
		
		debug("Impresion de venta");
		
		for (Integer id : ordersId) {
			MOrder orderTPV = new MOrder(getCtx(), id, null);
			orderTPV.setIsTpvUsed(true);
			orderTPV.save();
		}
		
		// Aquí estamos fuera de la transacción. Ahora sí emitimos la factura
		// por el controlador fiscal en caso de ser necesario.
		if (getShouldCreateInvoice() && invoice.requireFiscalPrint()) {
			debug("Imprimiendo ticket fiscal");
			// Recargamos la factura con TRX NULL. Si usamos la MInvoice con un
			// nombre de transacción entonces obtendríamos los mismos bloqueos que
			// cuando la emisión fiscal se hacía dentro de la transacción principal.
			MInvoice tmpInvoice = new MInvoice(getCtx(), invoice.getC_Invoice_ID(), null);
			tmpInvoice.addDocActionStatusListener(getDocActionStatusListener());
			tmpInvoice.setThrowExceptionInCancelCheckStatus(true);
			// Lanza la impresión fiscal
			CallResult callResult = tmpInvoice.doFiscalPrint(true);
			if (callResult.isError()) {
				throw new FiscalPrintException();				
			}
		}
		
		try {
			// Impresión del ticket convencional (solo si no fue emitido por
			// controlador fiscal)
			printTicket();
			// Impresión del documento de artículos a retirar por almacén
			printWarehouseDeliveryTicket(order);
			// Impresión del documento con datos del cliente en cuenta corriente
			printCurrentAccountTicket(order);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public boolean reprintInvoice(Order order, FiscalDocumentPrint fdp){
		boolean success = fdp.reprintDocument();
		if(success){
			try {
				// Impresión del documento de artículos a retirar por almacén
				printWarehouseDeliveryTicket(order);
				// Impresión del documento con datos del cliente en cuenta corriente
				printCurrentAccountTicket(order);
			} catch (Exception e) {
				
			}
		}
		return success;
	}
	
	private boolean getShouldCreateInvoice() {
		return shouldCreateInvoice;
	}
	
	private boolean getShouldCreateInout() {
		return shouldCreateInout;
	}
	
	/**
	 * Este método ajusta los pagos en efectivo, restándole lo que se le dió de vuelto 
	 * al cliente. 
	 * 
	 * Eso es, si el cliente tiene que pagar una orden por 85 y paga con 100, el pago 
	 * en efectivo llega en 100 y acá se ajusta a 85, quitándole los 15 que se le 
	 * devolvieron. 
	 * 
	 * Solo para pagos en Efectivo.
	 * 
	 * @param order
	 */
	private void adjustPayments(Order order) {
		
		BigDecimal change = order.getTotalChangeCashAmt();
		
		for (int i = 0; i<cashPayments.size(); i++) {
			CashPayment p = cashPayments.get(i);
			BigDecimal amt = currencyConvert(p.getAmount(), p.getCurrencyId());
			
			if (amt.compareTo(change) > 0) 
			{
				amt = amt.subtract(change);
				p.setAmount(currencyConvert(amt, getPoSCOnfig().getCurrencyID(), p.getCurrencyId()));
				p.setChangeAmt(change);
			} 
			else 
			{
				change = change.subtract(amt);
				cashPayments.remove(i);
				--i;
			}
		}
		
	}
	
	public boolean balanceValidate(Order order) {
		try {
			checkSaldo(order);
			return true;
		} catch (PosException e) {
			return false;
		}
	}
	
	private String getTrxName() {
		//return trx != null ? trx.getTrxName() : null;
		return trxName;
	}
	
	private void clearState(Order order) {
		
		ctx = Env.getCtx();
		invoiceDate = Env.getTimestamp();
		
//		if (trx != null) {
//			try {
//				trx.close();
//			} catch (Exception e) {}
//			trx = null;
//		}
//		
//		trx = Trx.get(this.toString() + invoiceDate.toString() + Thread.currentThread().getId(), true);
//		trx.start();
		
		if (order.getBusinessPartner() != null)
			partner = new MBPartner(ctx, order.getBusinessPartner().getId(), getTrxName());
		else
			partner = null;
		
		
		boolean isPoSOrder = (MDocType.get(ctx, getPoSCOnfig().getOrderDocTypeID()).getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_POSOrder));
		// Al completar el TicketTPV se crean la factura y el remito, con lo cual en este caso
		// el TPV NO debe crear ninguno de estos documentos.
		shouldCreateInvoice = getPoSCOnfig().isCreateInvoice() && !isPoSOrder;
		shouldCreateInout = getPoSCOnfig().isCreateInOut() &&  !isPoSOrder;
		shouldUpdateBPBalance = false;
		
		discountCalculator = null;
		
		morder = null;
		invoice = null;
		shipment = null;
		allocHdr = null;
		
		mpayments.clear();
		allocLines.clear();
		mCashLines.clear();
		
		aditionalWorkResults.clear();
		
		sobraPorCheques = null;
		faltantePorRedondeo = null;
		
		sumaPagos = BigDecimal.ZERO;
		sumaProductos = BigDecimal.ZERO;
		
		sumaCashPayments = BigDecimal.ZERO;
		sumaCheckPayments = BigDecimal.ZERO;
		sumaCreditCardPayments = BigDecimal.ZERO;
		sumaCreditPayments = BigDecimal.ZERO;
		sumaCreditNotePayments = BigDecimal.ZERO;
		sumaBankTransferPayments = BigDecimal.ZERO;
		
		cashPayments.clear();
		checkPayments.clear();
		creditCardPayments.clear();
		creditPayments.clear();
		creditNotePayments.clear();
		bankTransferPayments.clear();
		
		
		for (Payment p : order.getPayments()) {
			BigDecimal amount = currencyConvert(p.getAmount(), p.getCurrencyId());
			
			if (p.isCashPayment()) {
				sumaCashPayments = sumaCashPayments.add(amount);
				cashPayments.add((CashPayment)p);
			} else if (p.isCheckPayment()) {
				sumaCheckPayments = sumaCheckPayments.add(amount);
				checkPayments.add((CheckPayment)p);
			} else if (p.isCreditCardPayment()) {
				sumaCreditCardPayments = sumaCreditCardPayments.add(amount);
				creditCardPayments.add((CreditCardPayment)p);
			} else if (p.isCreditPayment()) {
				sumaCreditPayments = sumaCreditPayments.add(amount);
				creditPayments.add((CreditPayment)p);
			} else if (p.isCreditNotePayment()) {
				sumaCreditNotePayments = sumaCreditNotePayments.add(amount);
				creditNotePayments.add((CreditNotePayment)p);
			} else if (p.isBankTransferPayment()) {
				sumaBankTransferPayments = sumaBankTransferPayments.add(amount);
				bankTransferPayments.add((BankTransferPayment)p);
			}
		}
		
	}
	
	private boolean VerificarSaldo(BigDecimal sumaProductos, BigDecimal sumaPagos) {

		if (sumaPagos.compareTo(sumaProductos) >= 0)
			return true;
		
		BigDecimal diff = sumaPagos.subtract(sumaProductos).abs();
		
		if (diff.compareTo(VModelHelper.GetRedondeoMoneda(ctx, getClientCurrencyID() )) < 0)
			return true;
		
		return false;
		
	}
	
	public BigDecimal currencyConvert(BigDecimal amount, int fromCurrency) { 
		return currencyConvert(amount, fromCurrency, getPoSCOnfig().getCurrencyID());
	}
	
	public BigDecimal currencyConvert(BigDecimal amount, int fromCurrency, int toCurrency) {
		return VModelHelper.currencyConvert(amount, fromCurrency, toCurrency, invoiceDate);
	}
	
	private void checkInvoice() throws PosException {

		BigDecimal totalPagar = invoice.getGrandTotal().subtract(sumaCreditPayments);
		BigDecimal sumaPagos = 
			sumaCashPayments.
			add(sumaCheckPayments).
			add(sumaCreditCardPayments).
			add(sumaCreditNotePayments).
			add(sumaBankTransferPayments);
		
		BigDecimal redondeo = VModelHelper.GetRedondeoMoneda(ctx, getClientCurrencyID());

		// Diff es la cantidad exacta que FALTA PAGAR para que la diferencia sea PRECISAMENTE CERO. 
		BigDecimal diff = totalPagar.subtract(sumaPagos);

		// Hay una diferencia menor o igual al redodeo estándar ? 
		
		if (diff.abs().compareTo(redondeo) <= 0) 
		{
			faltantePorRedondeo = diff;
			sumaPagos.add(diff);
		}
		else if (diff.compareTo(redondeo) >= 0) 
		{
			// Falta dinero 
			throw new InsufficientBalanceException();
		}
		
		// En este punto sumaPagos es >= totalPagar
		
		// Sobra dinero ?
		// Se prioriza el efectivo para el cambio, sobra cheques siempre y
		// cuando sacando el efectivo
		BigDecimal sobraCheques = sumaPagos.subtract(sumaCashPayments);
		if (sobraCheques.compareTo(totalPagar) > 0
				&& sumaCheckPayments.compareTo(BigDecimal.ZERO) > 0) {
			sobraPorCheques = sobraCheques.subtract(totalPagar);
		}
	}
	
	/**
	 * 
	 * Verifica que los pagos están bien hechos; que no falte ni sobre dinero. 
	 * 
	 * @param order
	 * @throws PosException
	 */
	private void checkSaldo(Order order) throws PosException {
		
		BigDecimal cashChange = BigDecimal.ZERO;
		boolean invalidPayment = false;
		
		clearState(order);
		
		sumaProductos = BigDecimal.ZERO;
		sumaPagos = BigDecimal.ZERO;
		
		// Si no hay pagos, no deja procesar.
		
		if (order.getPayments().size() == 0)
			throw new InvalidPaymentException();
		
		// Si no hay pedidos, no deja procesar.
		
		if (order.getOrderProducts().size() == 0)
			throw new InvalidProductException();
		
		MPriceList priceList = new MPriceList(ctx, getPoSCOnfig().getPriceListID(), null);
		int priceListCurrencyID = priceList.getC_Currency_ID();
		
		// Suma productos se calcula según el algoritmo del Order, que tiene en
		// cuenta los redondeos en el cálculo general de impuestos
		sumaProductos = currencyConvert(order.getTotalAmount(), priceListCurrencyID);
		
		for (Payment p : order.getPayments()) {
			int fromCurrency = p.getCurrencyId();
			
			BigDecimal amt = currencyConvert(p.getAmount(), fromCurrency);
			
			sumaPagos = sumaPagos.add( amt );
		}
		
		// Scalado de importes finales. Si hay descuento, puede suceder que
		// sumaProductos tenga una escala mayor a la utilizada por la moneda, y
		// la comparación puede fallar erróneamente
		int stdScale = priceList.getStandardPrecision();
		sumaProductos = sumaProductos.setScale(stdScale, BigDecimal.ROUND_HALF_UP);
		sumaPagos = sumaPagos.setScale(stdScale, BigDecimal.ROUND_HALF_UP);
		
		// Si no alcanzan los pagos para pagar los productos, no deja procesar.
		
		if (!VerificarSaldo(sumaProductos, sumaPagos))
			throw new InsufficientBalanceException();
		
		// Si hay algun pago que no sea cheque, y están pagando de más, no permito que se efectue la operacion
		
		BigDecimal redondeo = VModelHelper.GetRedondeoMoneda(ctx, getClientCurrencyID());
		boolean sobraPlata = sumaPagos.subtract(sumaProductos).compareTo(redondeo) >= 0;  
		
		
		if (sobraPlata) { // order.getOrderProducts().size() != checkPayments.size()) {
			
			// if (sobraPlata)
			//	throw new InvalidPaymentException();
			
			BigDecimal x = 
				(sumaCreditPayments).
				add(sumaCreditCardPayments).
				add(sumaCreditNotePayments).
				add(sumaBankTransferPayments);
			
			// si x > sumaProductos, sobra plata -> ERROR
			
			if (x.subtract(sumaProductos).compareTo(redondeo) > 0)
				invalidPayment = true;
		
			x = x.add(sumaCheckPayments);
			
			// si x > sumaProductos , sobra por cheque -> OK
			
			if (x.subtract(sumaProductos).compareTo(redondeo) > 0)
			{
				// Sobra plata -> Todo el efectivo se manda de vuelta
				
				cashChange = sumaCashPayments;
			}
			else
			{
				// Falta plata -> de lo que tengo en efectivo, saco lo que me falta pagar
				
				cashChange = sumaCashPayments.subtract(sumaProductos.subtract(x).abs());
			}
		}
		
		// Inicializo todos los cambios en efectivo en cero
		for (CashPayment payment : cashPayments) {
			payment.setChangeAmt(BigDecimal.ZERO);
		}
		
		// El cambio de efectivo se debe agregar a los cobros de efectivo
		BigDecimal changeAux, convertedAmt;
		for (int c = 0; c < cashPayments.size()
				&& cashChange.compareTo(BigDecimal.ZERO) > 0; c++) {
			convertedAmt = currencyConvert(cashPayments.get(c).getAmount(), cashPayments.get(c).getCurrencyId());
			changeAux = convertedAmt.compareTo(cashChange) >= 0 ? cashChange
					: convertedAmt;
			cashPayments.get(c).setChangeAmt(changeAux);
			cashChange = cashChange.subtract(changeAux); 
		}
		
		// Forma de pago del pedido
		if(Util.isEmpty(sumaCreditPayments, true)){
			order.setPaymentRule(MInvoice.PAYMENTRULE_Cash);
		}
		else{
			order.setPaymentRule(MInvoice.PAYMENTRULE_OnCredit);
		}
		
		if (invalidPayment)
			throw new InvalidPaymentException();
	}
	
	public ProductList searchProduct(String code) {
		List<Integer> vendors;
		//ArrayList<Product> productList = new ArrayList<Product>();
		ProductList productList = new ProductList();
		int m_PriceList_ID = getPoSCOnfig().getPriceListID(); 
		// Obtengo la tarifa, y pido la version valida.
		MPriceList mPriceList = new MPriceList(Env.getCtx(),m_PriceList_ID,null);
		int m_PriceList_Version_ID = mPriceList.getPriceListVersion(null).getID();
		Product product = null;
		
		StringBuffer sql = new StringBuffer();
		// Obtiene el id, nombre y el precio en la tarifa del producto.
		sql
		  .append("SELECT DISTINCT ")
		  .append(   "u.M_Product_ID, ")
		  .append(   "bomPriceStd(u.M_Product_ID, M_PriceList_Version_ID, u.M_AttributeSetInstance_ID), ")
		  .append(	 "p.value, ")
		  .append(	 "p.Name, ")
		  .append(   "bomPriceLimit(u.M_Product_ID, M_PriceList_Version_ID, u.M_AttributeSetInstance_ID), ")
		  .append(   "u.M_AttributeSetInstance_ID, ")
		  .append(   "masi.description, ")
		  .append(   "u.MatchType, ")
		  .append(   "s.MandatoryType, ")
		  .append(   "p.m_product_category_id, ")
		  .append(   "p.CheckoutPlace, ")
		  .append(   "p.IsSold ")
		  .append("FROM ( "); 
		
		boolean needUnion = false;  // Indicador de concatenación de UNION a la consulta
		int codeParameterCount = 0; // Cantidad de veces que hay que agregar 'code' como 
		                            // parámetro de la consulta (varía según la conf del TPV) 
		String query = null;
		// Se agrega la sub-consulta de búsqueda por UPC en caso de que la configuración
		// del TPV así lo indique.
		if (getPoSCOnfig().isSearchByUPCConfigured()) {
			query = getSearchByUPCQuery();
			sql.append(query);
			codeParameterCount += parametersCount(query);
			needUnion = true;
		}
		// Se agrega la sub-consulta de búsqueda por Clave de Búsqueda en caso de que la 
		// configuración del TPV así lo indique.
		if (getPoSCOnfig().isSearchByValueConfigured()) {
			query = getSearchByValueQuery();
			sql.append(needUnion?" UNION ":"");
			sql.append(query);
			codeParameterCount += parametersCount(query);
			needUnion = true;
		}
		// Se agrega la sub-consulta de búsqueda por Nombre en caso de que la configuración
		// del TPV así lo indique.
		if (getPoSCOnfig().isSearchByNameConfigured()) {
			query = getSearchByNameQuery();
			sql.append(needUnion?" UNION ":"");
			sql.append(query);
			codeParameterCount += parametersCount(query);
			needUnion = true;
		}
		
		sql
		  .append(") u ")
		  .append("INNER JOIN M_Product p ON (p.M_Product_id = u.M_Product_id) ")
		  .append("INNER JOIN M_ProductPrice pp ON (pp.M_PriceList_Version_ID = ?) ")
		  .append("LEFT JOIN M_AttributeSet s ON (p.M_AttributeSet_ID = s.M_AttributeSet_ID) ") 
		  .append("LEFT JOIN M_AttributeSetInstance masi ON (u.M_AttributeSetInstance_ID = masi.M_AttributeSetInstance_ID) ")
          .append("WHERE u.M_Product_ID = pp.M_Product_ID ")
          .append(  "AND u.M_Product_ID = p.M_Product_ID ")
          .append(  "AND p.IsActive = 'Y' ")
          .append(  "AND p.isSold = 'Y' ")
          .append("ORDER BY u.MatchType ASC ");

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			int i = 1;
			// Se carga el código del artículo como parámetro las veces que la consulta
			// lo requiera
			while(i <= codeParameterCount) {
				pstmt.setString(i++, code);
			}
			pstmt.setInt(i++,m_PriceList_Version_ID);
			
			int prevMasiId = -1;
			
			ResultSet rs = pstmt.executeQuery();
			Map<Integer, Integer> productMatch = new HashMap<Integer, Integer>();
			while(rs.next()) {
				int m_Product_Id = rs.getInt(1);
				int matchType = rs.getInt("MatchType");
				
				// Verifica si el artículo ya fue agregado a la lista (para
				// evitar agregar dos veces el mismo artículo con diferente
				// Matching). Si ya está agregado, se queda con el matching de
				// mayor prioridad.
				if (productMatch.containsKey(m_Product_Id)
						&& productMatch.get(m_Product_Id) <= matchType) {
					continue;
				}
				
				BigDecimal productPrice = rs.getBigDecimal(2);
				String productName = rs.getString(3)+" - "+rs.getString(4);
				BigDecimal productLimitPrice = rs.getBigDecimal(5); 
				
				int M_AttributeSetInstance_ID = rs.getInt(6);
				String masiDescription = rs.getString(7);
				boolean masiMandatory = MAttributeSet.MANDATORYTYPE_AlwaysMandatory.equals(rs.getString("MandatoryType"));
				String checkoutPlace = rs.getString("CheckoutPlace");
				boolean sold = "Y".equals(rs.getString("IsSold"));
				
				// Me quedo solo con el primer Product si el MASI es > 0, 
				// o con todos en el resto de los casos (todos los masi = 0).
				
				if (prevMasiId > 0)
					break;
				
				prevMasiId = M_AttributeSetInstance_ID;
				
				// Creo el producto.
				vendors = getVendors(m_Product_Id);
				product = new Product(
						m_Product_Id, 
						code, 
						productName, 
						productPrice, 
						productLimitPrice, 
						M_AttributeSetInstance_ID, 
						masiDescription, 
						getPoSCOnfig().isPriceListWithTax(), 
						getPoSCOnfig().isPriceListWithPerception(),
						masiMandatory, 
						rs.getInt("m_product_category_id"), 
						vendors,
						checkoutPlace,
						sold);
				
				productList.addProduct(product, matchType);
				productMatch.put(product.getId(), matchType);
			}
			
			rs.close();
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}
	
	/**
	 * Realizar las validaciones de crédito de la entidad comercial 
	 * @param order
	 * @throws InsufficientCreditException
	 */
	private void checkCredit(Order order) throws InsufficientCreditException, Exception {
		currentAccountSalesConditions = new HashMap<String, BigDecimal>();
		if(MBPartner.PAYMENTRULE_OnCredit.equals(order.getPaymentRule())){
			MBPartner bp = new MBPartner(getCtx(), order.getBusinessPartner().getId(), getTrxName());
			MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
			// Obtengo el manager actual
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(this);
			// Seteo el estado actual del cliente y lo obtengo
			CallResult result = manager.setCurrentAccountStatus(getCtx(), bp, org,
					null);
			// Si hubo error, obtengo el mensaje y tiro la excepción
			if (result.isError()) {
				throw new InsufficientCreditException(result.getMsg());
			}
			// Me guardo el estado de la entidad comercial
			String creditStatus = (String)result.getResult(); 
			// Determino los tipos de pago a verificar el estado de crédito
			result = manager.getTenderTypesToControlStatus(getCtx(), org, bp,
					getTrxName());
			// Si hubo error, obtengo el mensaje y tiro la excepción
			if (result.isError()) {
				throw new Exception(result.getMsg());
			}
			// Me guardo la lista de tipos de pago a verificar
			Set<String> tenderTypesAllowed = (Set<String>)result.getResult();
			Map<String, BigDecimal> pays = new HashMap<String, BigDecimal>();
			BigDecimal convertedPayAmt;
			String paymentRule;
			for (Payment pay : order.getPayments()) {
				// Verificar por el manager de cuentas corrientes si debo verificar
				// el estado de crédito en base a los tipos de pago obtenidos
				paymentRule = CurrentAccountBalanceStrategy
						.getPaymentRuleEquivalent(pay.getTenderType());
				if(!Util.isEmpty(paymentRule, true)){
					if (tenderTypesAllowed != null
							&& tenderTypesAllowed.contains(pay.getTenderType())) {
						shouldUpdateBPBalance = true;
						// Verificar la situación de crédito de la entidad comercial
						result = manager.validateCurrentAccountStatus(getCtx(), org, bp, 
								creditStatus, getTrxName());
						// Si hubo error, obtengo el mensaje y tiro la excepción
						if (result.isError()) {
							throw new InsufficientCreditException(result.getMsg());
						}
						currentAccountSalesConditions.put(paymentRule, BigDecimal.ZERO);
					}
					// Convierto el monto del pago a partir de su moneda
					convertedPayAmt = MConversionRate.convertBase(getCtx(), pay
							.getAmount(), pay.getCurrencyId(), order.getDate(), 0, Env
							.getAD_Client_ID(getCtx()), Env.getAD_Org_ID(getCtx()));
					pays.put(paymentRule, convertedPayAmt);
					// Si existe el payment rule significa que se agregó porque es
					// paymentrule de cuenta corriente, entonces actualizar su monto
					if(currentAccountSalesConditions.get(paymentRule) != null){
						currentAccountSalesConditions.put(paymentRule, convertedPayAmt);
					}
				}
			}		
			// Verificar el crédito con la factura
			result = manager.checkInvoicePaymentRulesBalance(getCtx(), bp, org,
					pays, getTrxName());
			// Si hubo error, obtengo el mensaje y tiro la excepción
			if (result.isError()) {
				throw new InsufficientCreditException(result.getMsg());
			}
		}
	}

	/**
	 * Control de cuit en cheques
	 * @param order
	 * @throws Exception
	 */
	public void checkCUITControl(Order order) throws Exception{
		if(isCheckCUITControlActivated()){
			// Obtener los diferentes CUITs de los diferentes cheques existentes
			// y la suma de los montos de cada uno de ellos
			Map<String, BigDecimal> cuits = new HashMap<String, BigDecimal>();
			String auxCuit;
			for (Payment payment : order.getPayments()) {
				if(payment.isCheckPayment()){
					auxCuit = ((CheckPayment)payment).getCuitLibrador().trim();
					auxCuit.replace("-", "");
					cuits.put(
							auxCuit,
							payment.getAmount()
									.add(cuits.get(auxCuit) == null ? BigDecimal.ZERO
											: cuits.get(auxCuit)));
				}
			}
			// Validar que existan en la tabla de control de CUITS, si no están se crean
			// Verificar que no superen el límite impuesto en el control y que estén activos
			MCheckCuitControl cuitControl;
			BigDecimal balance;
			int orgID = Env.getAD_Org_ID(getCtx());
			BigDecimal initialCheckLimit = MCheckCuitControl
					.getInitialCheckLimit(Env.getAD_Org_ID(getCtx()),
							getTrxName());
			for (String cuit : cuits.keySet()) {
				// Obtener el control para el cuit y organización actual
				cuitControl = MCheckCuitControl.get(getCtx(), orgID, cuit,
						true, initialCheckLimit, getTrxName());			
				// Verificar si está activo y no supera el límite impuesto en el
				// control
				if(!cuitControl.isActive()){
					throw new Exception(Msg.getMsg(getCtx(), "CUITNotActive",
							new Object[] { cuit }));
				}
				// Verificar que la suma de los cheques con fecha de vencimiento
				// mayor a la actual y la suma de todos los cheques de esta
				// venta para este cuit no superen el límite impuesto en el
				// control
				balance = cuitControl.getBalance(Env.getDate());
				if((balance.add(cuits.get(cuit))).compareTo(cuitControl.getCheckLimit()) > 0){
					throw new Exception(Msg.getMsg(
							getCtx(),
							"CUITSurpassCheckLimit",
							new Object[] { cuit, cuitControl.getCheckLimit(),
									balance, cuits.get(cuit),
									balance.add(cuits.get(cuit)) }));
				}
			}
		} 
	}
	
	/**
	 * Realizar tareas adicionales para la gestión de crédito de clientes
	 * @param order pedido
	 * @throws Exception en caso de error
	 */
	private void performAditionalCurrentAccountWork(Order order) throws Exception{
		MBPartner bp = new MBPartner(getCtx(), order.getBusinessPartner()
				.getId(), getTrxName());
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager(this);
		// Realizo las tareas adicionales necesarias
		// Factura
		if(invoice != null){
			performAditionalCurrentAccountWork(org, bp, manager, invoice, true);
		}
		// Payments
		for (MPayment pay : mpayments.values()) {
			performAditionalCurrentAccountWork(org, bp, manager, pay, true);
		}
		// Cashlines
		for (MCashLine cashLine : mCashLines.values()) {
			performAditionalCurrentAccountWork(org, bp, manager, cashLine, true);
		}
	}

	/**
	 * Realiza las tareas adicionales en base a los paramétros.
	 * 
	 * @param org
	 *            organización actual
	 * @param bp
	 *            entidad comercial de la venta actual
	 * @param manager
	 *            manager de cuentas corrientes actual
	 * @param po
	 *            documento o transacción involucrada, puede ser Invoice,
	 *            Payment o Cashline.
	 * @param addToWorkResults
	 *            true si el resultado de estas tareas se debe colocar dentro de
	 *            la map de resultados (variable de instancia
	 *            aditionalWorkResults)
	 * @return el resultado de las tareas adicionales si es que existe
	 * @throws Exception
	 *             si hubo algún error dentro de la ejecución de esas tareas
	 */
	private Object performAditionalCurrentAccountWork(MOrg org, MBPartner bp,
			CurrentAccountManager manager, PO po, boolean addToWorkResults) throws Exception {
		// Realizo las tareas adicionales
		CallResult result = manager.performAditionalWork(getCtx(), org, bp, po,
				true, getTrxName());
		// Si es error devuelvo una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		// Lo agrego a la map si me lo permite el parámetro y tengo un resultado
		if(addToWorkResults && result != null){
			getAditionalWorkResults().put(po, result.getResult());
		}
		return result.getResult();
	}

	/**
	 * Realizar tareas de gestión de crédito luego de procesar los documentos,
	 * como por ejemplo actualización del crédito de la entidad comercial, etc.
	 * 
	 * @param order
	 *            pedido actual
	 */
	private void afterProcessDocuments(Order order){
		MBPartner bp = new MBPartner(getCtx(), order.getBusinessPartner()
				.getId(), getTrxName());
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager(this);
		// Actualizo el crédito
		CallResult result = new CallResult();
		try{
			result = manager.afterProcessDocument(getCtx(), org, bp,
					getAditionalWorkResults(), getTrxName());
		} catch(Exception e){
			result.setMsg(e.getMessage(), true);
		}
		if(result.isError()){
			log.severe(result.getMsg());
		}
	}
	

	private MOrder createOxpOrder(Order order) throws PosException {
		
		
		List<OrderProduct> products = order.getOrderProducts();
		
		MOrder mo = new MOrder(ctx, 0, getTrxName());

		setCaches(mo); //Ader: caches
		
		mo.setIsSOTrx( true );
		// mo.setC_DocTypeTarget_ID();
		mo.setIsTpvUsed(true);
		mo.setTPVInstance(true);
		// TODO: Cual de los dos ?
		
		mo.setC_DocType_ID(getPoSCOnfig().getOrderDocTypeID());
		mo.setC_DocTypeTarget_ID(getPoSCOnfig().getOrderDocTypeID());
		
		mo.setDocAction(MOrder.DOCACTION_Complete);
		mo.setDocStatus(MOrder.DOCSTATUS_Drafted);
		
		mo.setBPartner(partner);
		mo.setC_BPartner_Location_ID(order.getBusinessPartner().getLocationId());
		mo.setAD_User_ID(Env.getAD_User_ID(ctx));
		mo.setM_PriceList_ID(getPoSCOnfig().getPriceListID());
		mo.setM_Warehouse_ID(getPoSCOnfig().getWarehouseID());
		//RESP
		mo.setSalesRep_ID(order.getOrderRep());
		//mo.setSalesRep_ID(Env.getContextAsInt(ctx, "#SalesRep_ID"));
        
		// Si el pedido tiene asociado un esquema de vencimientos entonces le
		// seteo ese al original, sino busco el de la config
		int paymentTermID = order.getPaymentTerm() != null ? order
				.getPaymentTerm().getId() : getPoSCOnfig().getPaymentTermID(); 
		mo.setC_PaymentTerm_ID(paymentTermID);
		
		String cuit = partner.getTaxID();
		mo.setCUIT(cuit);

		mo.setNombreCli(order.getBusinessPartner().getCustomerName());
		mo.setInvoice_Adress(order.getBusinessPartner().getCustomerAddress());
		mo.setNroIdentificCliente(order.getBusinessPartner().getCustomerIdentification());
		
		mo.setPaymentRule(order.getPaymentRule());
		
		debug("Guardando el Pedido (Encabezado, sin líneas aún)");
		throwIfFalse(mo.save(), mo);
		
		// obtener el total lineas a generar 
		int currentProduct = 0;
		int productCount = products.size();
		int numOrderLine = 0; //ADER: mejora de performance (y arreglo de bug), al setearse explicitamente 
		Map<Integer, Integer> manualLinesDiscounts = new HashMap<Integer, Integer>();
		
		for (OrderProduct op : products) {
			currentProduct++;
			
			//MProduct product = new MProduct(ctx, op.getProduct().getId(), getTrxName());
			//Ader : caches; si esto es null bueno....
			MProduct product = getProductFromCache(op.getProduct().getId());
			
			MOrderLine line = new MOrderLine(mo);

			line.setDirectInsert(true);
			line.setProduct( product );
			line.setM_AttributeSetInstance_ID( op.getProduct().getAttributeSetInstanceID() );
	        line.setQty(op.getCount());
	        line.setPrice(getPoSCOnfig().getPriceListID());    // sets List/limit

	        line.setDescription(op.getLineDescription());
	        
	        numOrderLine +=10;
	        line.setLine(numOrderLine); //ADER: mejora de seteo explicito
	        line.setPrice(op.getPrice());
	        line.setPriceList(op.getPriceList());
	        
	        line.setC_Tax_ID(op.getTax().getId());
	        line.setDiscount();
	        line.setLineNetAmt();
	        line.setLineBonusAmt(op.getLineBonusAmt());
	        line.setLineDiscountAmt(op.getLineDiscountAmt());
			// Guarda el lugar de retiro de la línea para luego determinar si se
			// reserva o no el stock en pedido según el lugar de retiro.
	        // Los artículos retirados por almacén requieren que se haga la
			// reserva de stock al completar el pedido. Los artículos retirados
			// por TPV evitarán la reserva de stock en MOrden ya que luego se
			// hace el remito que le da salida.

	        // NOTA: aquí no se tiene en cuenta si se crea el remito o no para
			// determinar el lugar de retiro ya que esa lógica de decisión está
			// contemplada a la hora de agregar el artículo al pedido del TPV.
			// Esto implica que en este punto ya está definido si la línea se
			// retira por Almacén o por TPV (habiendo contemplado que si el TPV
			// no hace remito, todos los artículos van a estar con lugar de
			// retiro Almacén) -> ver métodos createOrderProduct(...).	 
			// Matías Cap 20120329 - Se modificó esta lógica ya que ahora se
			// debe persistir ese dato en al base de datos
	        line.setCheckoutPlace(op.getCheckoutPlace());
	       
	        // unicamente la ultima linea actualizará el encabezado con información de impuestos
	        line.setShouldUpdateHeader(currentProduct==productCount);
	        debug("Guardando línea #" + currentProduct);
	        throwIfFalse(line.save());
	        
	        op.setOrderLineID(line.getC_OrderLine_ID());
	        // Guardar el id de descuento interno manual de la línea
	        manualLinesDiscounts.put(line.getID(), op.getLineManualDiscountID());
		}
		debug("Guardando el Pedido (Encabezado, con líneas ya creadas)");
		throwIfFalse(mo.save(), mo);
		
		// Descuentos: leer las líneas del pedido para ya tenerlas cacheadas y setear 
		// 				el shouldUpdateHeader a false en todas menos la última
		currentProduct = 0;
		for (MOrderLine orderLine : mo.getLines()) 
		{ 
			currentProduct++;
			// Seteo el id interno de descuento manual de línea
			orderLine.setLineManualDiscountID(manualLinesDiscounts.get(orderLine.getID()));
			orderLine.setShouldUpdateHeader(currentProduct==productCount);
		}
		
		// Crea un calculador de descuentos a partir del calculador de
		// descuentos asociado al pedido de TPV, asociando al nuevo calculador
		// el pedido MOrder creado (wrapper). Luego aplica los descuentos.
		discountCalculator = DiscountCalculator.create(mo.getDiscountableWrapper(), order.getDiscountCalculator());
		debug("Aplicando descuentos al Pedido (DiscountCalculator)");
		discountCalculator.applyDiscounts();
		debug("Guardando el Pedido nuevamente (luego de aplicar descuentos)");
		throwIfFalse(mo.save(), mo);
		
		// Cargar los impuestos adicionales en C_Order_Tax
		createOXPOrderTaxes(mo, order);
		
		// Reload Order
		
		mo = new MOrder(ctx, mo.getID(), getTrxName());
		
		setCaches(mo); //caches
		// Se reserva stock solo si el remito realizado desde tpv se genera en
		// borrador
		mo.setForceReserveStock(getPoSCOnfig().isCreateInOut()
				&& getPoSCOnfig().isDraftedInOut());
		// Completar Orden
		debug("Completando el pedido");
		throwIfFalse(mo.processIt(DocAction.ACTION_Complete), mo);
		debug("Guardando el pedido (luego de completar)");
		throwIfFalse(mo.save(), mo);
		
		// Actualizo la instancia del documento del discount calculator, ya que
		// son instancias diferentes
		discountCalculator.setDocument(mo.getDiscountableWrapper());
		order.setGeneratedOrderID(mo.getC_Order_ID());
		return mo;
	}
	
	private void createOXPOrderTaxes(MOrder mo, Order order) throws PosException{
		BigDecimal totalNet = order.getOtherTaxes() != null
				&& order.getOtherTaxes().size() > 0 ? mo.getTotalLinesNetWithoutDocumentDiscount()
				: BigDecimal.ZERO;
		MOrderTax orderTax;
		for (Tax tax : order.getOtherTaxes()) {
			// FIXME Cuando se pasen las percepciones a las M, se debe colocar
			// bypass para aquellos impuestos que son percepciones
			orderTax = new MOrderTax(getCtx(), 0, getTrxName());
			orderTax.setC_Order_ID(mo.getID());
			orderTax.setC_Tax_ID(tax.getId());
			orderTax.setTaxAmt(totalNet.multiply(tax.getTaxRateMultiplier()));
			orderTax.setTaxBaseAmt(totalNet);
			throwIfFalse(orderTax.save(), mo);
		}
	}
	
	private MInvoice createOxpInvoice(Order order) throws PosException {

		MInvoice inv;
	
		// Se crea una factura de Argentina.
		if (LOCAL_AR_ACTIVE)
			inv = createLocaleInvoice(order);
		// Se crea una factura estándar.
		else
			inv = new MInvoice(morder, getPoSCOnfig().getInvoiceDocTypeID(), invoiceDate);
		
		// Se indica que no se debe crear una línea de caja al completar la factura ya
		// que es el propio TPV el que se encarga de crear los pagos e imputarlos con
		// la factura (esto soluciona el problema de líneas de caja duplicadas que 
		// se había detectado).
		inv.setCreateCashLine(false);
		
		// Se skippea la actualización del descuento manual general de la
		// cabecera para la ventana de facturas 
		inv.setSkipManualGeneralDiscount(true);
		
		inv.setTPVInstance(true);
		
		inv.setDocAction(MInvoice.DOCACTION_Complete);
		inv.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		
		// Esquema de vencimientos
		inv.setC_PaymentTerm_ID(morder.getC_PaymentTerm_ID());
		
		// Se copia el importe de descuento/recargo
		inv.setChargeAmt(morder.getChargeAmt());
		inv.setC_Charge_ID(morder.getC_Charge_ID());
		
		// Monto a crédito inicial y medio de cobro a crédito
		inv.setInitialCurrentAccountAmt(sumaCreditPayments);
		if(order.getCreditPOSPaymentMediumID() != null){
			inv.setC_POSPaymentMedium_Credit_ID(order.getCreditPOSPaymentMediumID());
		}
		
		inv.setPaymentRule(order.getPaymentRule());
		
		throwIfFalse(inv.save(), inv, InvoiceCreateException.class);
		
		MOrderLine[] moLines = morder.getLines(true);
		int lineNumber = 10;
		
		// obtener el total lineas a generar 
		int currentLine = 0;
		int lineCount = moLines.length;
		
		TimeStatsAccumulator.getAccumulator(AccumulableTask.NEW_INVOICE_LINE).resetAccumulator();
		for (MOrderLine line : moLines) {
			currentLine++;
			MInvoiceLine invLine = new MInvoiceLine(inv);
			
			invLine.setDirectInsert(true);
			invLine.setOrderLine(line);
			invLine.setQty(line.getQtyOrdered());
			invLine.setLine(lineNumber);
			invLine.setDocumentDiscountAmt(line.getDocumentDiscountAmt());
			invLine.setLineBonusAmt(line.getLineBonusAmt());
			invLine.setLineDiscountAmt(line.getLineDiscountAmt());
			invLine.setC_Project_ID(inv.getC_Project_ID());
			
			// Se asigna el impuesto. Aquí se recalcula la tasa a partir de los datos
			// de la factura.
			// IMPORTANTE: la nueva tasa puede variar de la que se calculó previamente
			// en el ingreso de productos al pedido de TPV, dado que la entidad comercial
			// puede diferir.
			invLine.setC_Tax_ID(line.getC_Tax_ID());

			// la ultima linea unicamente se encargá de setear los impuestos correspondientes
			invLine.setShouldUpdateHeader(currentLine==lineCount);
			
			// Se skippea la actualización del descuento manual general de la
			// cabecera para la ventana de facturas 
			invLine.setSkipManualGeneralDiscount(true);
			
			debug("Guardando línea #" + invLine.getLine());
			throwIfFalse(invLine.save(), InvoiceCreateException.class);
			
			lineNumber += 10;
		}
		
		// Crear los impuestos adicionales a la factura
		createOXPInvoiceTaxes(inv, order);
		
		// Recargar la factura
		
		inv = new MInvoice(ctx, inv.getID(), getTrxName());
		// Seteo el bypass de la factura para que no chequee el saldo del
		// cliente porque ya lo chequea el tpv
		inv.setCurrentAccountVerified(true);
		// Seteo el bypass para que no actualice el crédito del cliente ya
		// que se realiza luego al finalizar las operaciones
		inv.setUpdateBPBalance(false);
		
		// Se skippea la actualización del descuento manual general de la
		// cabecera para la ventana de facturas 
		inv.setSkipManualGeneralDiscount(true);
		inv.setTPVInstance(true);
		//
		// FB - Comentado a partir del quitado de la impresión fiscal de 
		// la transacción principal
		//
		// Completar Factura. 
		// IMPORTANTE: Si la factura debe ser emitida mediante un controlador fiscal, no
		// se completa aquí dado que esto dispararía la impresión fiscal, y luego, en caso
		// de producirse algún error por ejemplo al crear los pagos, quedaría inconsistente
		// la información emitida con la guardada, dado que en caso de error no se
		// guarda la factura OXP pero si podría emitirse el comprobante por la impresora
		// fiscal. El completado y emisión de comprobante se deben hacer al final del proceso
		// de creación del pedido.
		//if (!needFiscalPrint(inv)) {
		//	throwIfFalse(inv.processIt(DocAction.ACTION_Complete), inv, InvoiceCreateException.class);
		//}
		
		// Ignora la impresión fiscal al completar. Se hace luego fuera de la transacción. 
		inv.setIgnoreFiscalPrint(true);
		inv.skipAfterAndBeforeSave = true;
		throwIfFalse(inv.processIt(DocAction.ACTION_Complete), inv, InvoiceCreateException.class);
		throwIfFalse(inv.save(), inv, InvoiceCreateException.class);
		
		order.setGeneratedInvoiceID(inv.getC_Invoice_ID());
		morder.setTpvGeneratedInvoiceID(inv.getC_Invoice_ID());
		
		return inv;
	}
	
	private void createOXPInvoiceTaxes(MInvoice mi, Order order) throws PosException{
		BigDecimal totalNet = order.getOtherTaxes() != null
				&& order.getOtherTaxes().size() > 0 ? mi.getTotalLinesNet()
				: BigDecimal.ZERO;
		MInvoiceTax invoiceTax;
		for (Tax tax : order.getOtherTaxes()) {
			// Se hace un bypass de las percepciones ya que ya se deberían haber creado
			if(!tax.isPercepcion()){
				invoiceTax = new MInvoiceTax(mi.getCtx(), 0, mi.get_TrxName());
				invoiceTax.setC_Invoice_ID(mi.getID());
				invoiceTax.setC_Tax_ID(tax.getId());
				invoiceTax.setTaxAmt(totalNet.multiply(tax.getTaxRateMultiplier()));
				invoiceTax.setTaxBaseAmt(totalNet);
				throwIfFalse(invoiceTax.save(), mi);
			}
		}
	}
	
	private MInvoice createLocaleInvoice(Order order) throws PosException {
		// Se obtiene la letra del comprobante
		MLetraComprobante mLetraComprobante = getLocaleArLetraComprobante();
		
		// Se obtiene la letra y el nro de punto de venta para determinar el tipo
		// de documento de la factura.
		String letra = mLetraComprobante.getLetra();
		
		// Obtener el punto de venta específicamente para esta letra en la
		// config
		Integer posNumber = getPoSCOnfig().getPosNumberLetters().get(
				mLetraComprobante.getLetra());
		if(Util.isEmpty(posNumber, true)){
			posNumber = getPoSCOnfig().getPosNumber();
		}
		
		// Se obtiene el tipo de documento para la factura.
		MDocType mDocType = MDocType.getDocType(ctx, Env.getAD_Org_ID(getCtx()),
				MDocType.DOCTYPE_CustomerInvoice, letra, posNumber,
				getTrxName());
		if (mDocType == null) 
			throw new InvoiceCreateException(Msg.getMsg(ctx, "NonexistentPOSDocType", new Object[] {letra, posNumber}));
		
		MInvoice inv = new MInvoice(morder, mDocType.getC_DocType_ID(), invoiceDate);
		
		// Se asigna la letra de comprobante, punto de venta y número de comprobante
		// a la factura creada.
		inv.setC_Letra_Comprobante_ID(mLetraComprobante.getID());
		inv.setPuntoDeVenta(posNumber);
		// Nro de comprobante.
		//String documentNo = MSequence.getDocumentNo(mDocType.getID(), getTrxName());
		Integer nroComprobante = CalloutInvoiceExt.getNextNroComprobante(mDocType.getID());
		if (nroComprobante != null)
			inv.setNumeroComprobante(nroComprobante);
		
		// Asignación de CUIT en caso de que se requiera.
//		MCategoriaIva mCategoriaIvaCus = new MCategoriaIva(ctx, partner.getC_Categoria_Iva_ID(), getTrxName());
		String cuit = partner.getTaxID();
		inv.setCUIT(cuit);

		// Siempre se asignan los datos del comprador
		// Se asignan los datos de consumidor final.
		// Nombre, dirección e identificación para los casos en que el monto de la factura
		// sea mayor que el permitido a consumidor final.
//		if (mCategoriaIvaCus.getCodigo() == MCategoriaIva.CONSUMIDOR_FINAL) {
			inv.setNombreCli(order.getBusinessPartner().getCustomerName());
			inv.setInvoice_Adress(order.getBusinessPartner().getCustomerAddress());
			inv.setNroIdentificCliente(order.getBusinessPartner().getCustomerIdentification());
//		}
		
		return inv;
	}
	
	/**
	 * Indica si la factura debe ser emitida mediante un controlador fiscal.
	 * @param invoice Factura a evaluar.
	 */
	private boolean needFiscalPrint(MInvoice invoice) {
		return MDocType.isFiscalDocType(invoice.getC_DocTypeTarget_ID()) && LOCAL_AR_ACTIVE;
	}
	
	private MInOut createOxpInOut(Order order) throws PosException {
		/*
		 * Ampliado y corregido por Franco Bonafine - Disytel - 2010-04-07
		 * 
		 * El remito ahora no se crea con la totalidad de los artículos que se agregaron
		 * al pedido del TPV  sino que solo se agregan aquellos artículos cuyo lugar de 
		 * retiro sea el TPV. Los artículos cuyo retiro sea por almacén no serán 
		 * incluidos en este remito, quedando sus cantidades pendientes en el pedido 
		 * previamente creado.
		 */
		shipment = null;
		// El remito se realiza solo si la configuración del TPV así lo indica y además
		// si el pedido de TPV contiene al menos 1 artículo cuyo retiro sea por TPV (si
		// todos los artículos del pedido se retiran por almacén entonces el remito no
		// se debe hacer ya que no contendría líneas).
		if (getPoSCOnfig().isCreateInOut() && order.getPOSCheckoutProductsCount() > 0) { 
			// Crea el encabezado del remito a partir del pedido, y lo guarda
			shipment = new MInOut(morder, 0, morder.getDateOrdered());
			shipment.setDocAction(MInOut.DOCACTION_Complete);
			shipment.setDocStatus(MInOut.DOCSTATUS_Drafted);
			shipment.setTPVInstance(true);
			// Tipo de documento del remito
			if (!Util.isEmpty(getPoSCOnfig().getInoutDocTypeID(), true)) {
				shipment.setC_DocType_ID(getPoSCOnfig().getInoutDocTypeID());
			}
			
			throwIfFalse(shipment.save(), shipment);
			
			//Ader: evitar que se reelean las MOrderLInes; esto evita 20 accesos
			//y otros tantos potencialemten (al evitar acceer a la cache tradcionale
			//de productos
			HashMap<Integer,MOrderLine> mapMo = new HashMap<Integer,MOrderLine>();
			//no deberia ser necesario getLines(true); si deberia, que se resete
			//antes de llamar a este metodo
			MOrderLine[] moLines = morder.getLines();
			for (int i = 0; i < moLines.length; i++)
			{
				MOrderLine moLine = moLines[i];
				mapMo.put(moLine.getC_OrderLine_ID(),moLine);
			}
			
			// FIXME: Ader: notar que lo siguiente rompe la logica BOM...
			
			// Se recorren todos los artículos del pedido TPV para ir agregándolos
			// al remito. Es necesario iterar sobre esta colección (y no sobre los 
			// MOrderLines del mOrder creado previamente) ya que en la colección
			// se encuentra el lugar de retiro del artículo, y de este dato depende
			// si el artículo se agrega o no al remito (a diferencia del MOrder y MInvoice
			// que se crean con todas las líneas cargadas al TPV).
			for (OrderProduct orderProduct : order.getOrderProducts()) {
				// El remito solo incluye los artículos que se retiren por el TPV
				// Aquellos artículos cuyo retiro sea por almacén quedan fuera de este
				// remito, quedando pendientes de entrega.
				if (orderProduct.isPOSCheckout()) {
					// Obtiene la instancia de la línea de pedido creada.
					/*MOrderLine orderLine = new MOrderLine(
							morder.getCtx(), 
							orderProduct.getOrderLineID(), 
							getTrxName());
					*/
					//evitar accesos a DB; de paso, esta MorderLine va a tener
					//la cahe de productos seteada
					MOrderLine orderLine = mapMo.get(orderProduct.getOrderLineID());
					
					// Crea la línea del remito a partir de la línea del pedido
					MInOutLine shipmentLine = new MInOutLine(shipment); 
					shipmentLine.setDirectInsert(true);
					shipmentLine.setOrderLine(orderLine, 0, orderLine.getQtyEntered());
					shipmentLine.setQty(orderLine.getQtyEntered());
					shipmentLine.setTPVInstance(true);
					// Guarda los cambios.
					debug("Guardando línea #" + shipmentLine.getLine());
					throwIfFalse(shipmentLine.save());
				}
			}
			// Completa el remito
			if(!getPoSCOnfig().isDraftedInOut()){
				throwIfFalse(shipment.processIt(DocAction.ACTION_Complete), shipment);
			}
			throwIfFalse(shipment.save(), shipment);
		}
		return shipment;
	}
	
	private MPayment createOxpMPayment(String TenderType, int C_Currency_ID, BigDecimal Amt, String documentNo) {
		
		MPayment pay = new MPayment(ctx, 0, getTrxName());
		
		if(documentNo != null){
			pay.setDocumentNo(documentNo);
		}
		pay.setTenderType(TenderType);
		
		pay.setC_DocType_ID(true);
		pay.setC_BPartner_ID(partner.getC_BPartner_ID());
		
		pay.setDateTrx(this.invoiceDate);
		pay.setDateAcct(this.invoiceDate);
		
		pay.setC_BankAccount_ID(getPoSCOnfig().getCheckBankAccountID()); 
		
		pay.setC_Order_ID(morder.getC_Order_ID());
		
		// Asignación del tipo de documento Cobro a Cliente
		pay.setC_DocType_ID(getPaymentDocType().getC_DocType_ID());
		
		// Esta asignación de RoutingNo y AccountNo es incorrecta!
		// Aquí se está asignando los datos de la cuenta bancario destino del cobro, mientras
		// que esos campos existen para ingresar la información relacionada con el ORIGEN
		// del cobro. Con lo cual se está mezclando la información y esto confunde al usuario
		// En la ventana de Cobros estos campos son visibles solo para Cheques y Transferencias
		// con lo cual, se deberían cargar con la identificación del Banco y la Cuenta origen
		// del Cheque o Transferencia.
		// Por el momento se comentan estas línas para no seguir cargando erróneamente
		// esta información.
		// -->
		//String RoutingNo = VModelHelper.getSQLValueString(null, " select routingno from c_bank inner join c_bankaccount on (c_bank.c_bank_id=c_bankaccount.c_bank_id) where c_bankaccount.c_bankaccount_id = ? ", pay.getC_BankAccount_ID() );
		//String AccountNo = VModelHelper.getSQLValueString(null, " select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ", pay.getC_BankAccount_ID() );

		//pay.setRoutingNo(RoutingNo);
		//pay.setAccountNo(AccountNo);
		// <-- Fin comentario
	
		pay.setAmount(C_Currency_ID, Amt);
		pay.setDiscountAmt(BigDecimal.ZERO);
		pay.setWriteOffAmt(BigDecimal.ZERO);
		pay.setOverUnderAmt(BigDecimal.ZERO);
		
		return pay;
		
	}
	
	private MAllocationLine createOxpMAllocationLine(Payment p, MPayment pay) throws PosException {
		return createOxpMAllocationLine(p, pay, null, null);
	}
	
	private MAllocationLine createOxpMAllocationLine(Payment p, MCashLine cashLine) throws PosException {
		return createOxpMAllocationLine(p, null, cashLine, null);
	}
	
	private MAllocationLine createOxpMAllocationLine(Payment p, Integer creditInvoiceID) throws PosException {
		return createOxpMAllocationLine(p, null, null, creditInvoiceID);
	}
	
	/**
	 * @param p
	 * @param pay
	 * @param cashLine
	 * @param creditInvoiceID
	 * @return
	 * @throws PosException
	 */
	private MAllocationLine createOxpMAllocationLine(Payment p, MPayment pay, MCashLine cashLine, Integer creditInvoiceID) throws PosException {
		return createOxpMAllocationLine(
				getShouldCreateInvoice() ? invoice.getC_Invoice_ID() : 0, p,
				pay, cashLine, creditInvoiceID);
	}
	
	/**
	 * @param p
	 * @param pay
	 * @param cashLine
	 * @param creditInvoiceID
	 * @return
	 * @throws PosException
	 */
	private MAllocationLine createOxpMAllocationLine(Integer debitInvoiceID, Payment p, MPayment pay, MCashLine cashLine, Integer creditInvoiceID) throws PosException {
		return createOxpMAllocationLine(debitInvoiceID, p, pay, cashLine, creditInvoiceID, null, false);
	}
	
	private MAllocationLine createOxpMAllocationLine(Integer debitInvoiceID, Payment p, MPayment pay, MCashLine cashLine, Integer creditInvoiceID, BigDecimal amount, boolean isReturn) throws PosException {
		BigDecimal allocLineAmt = currencyConvert(amount == null ? p
				.getAmount().abs() : amount.abs(), p.getCurrencyId(),
				allocHdr.getC_Currency_ID());
		BigDecimal changeAmt = p.getChangeAmt().abs();
		BigDecimal writeOffAmt = BigDecimal.ZERO;
		
		if (faltantePorRedondeo != null) {
			writeOffAmt = faltantePorRedondeo;
			faltantePorRedondeo = null;
		}
		
		// Si el monto de la línea del allocation es distinto al total del payment, entonces va a writeoff
		if (!isReturn && p.getCurrencyId() != allocHdr.getC_Currency_ID()
				&& (allocLineAmt.add(changeAmt)).compareTo(p.getConvertedAmount()) != 0) {
			writeOffAmt = writeOffAmt.add(p.getConvertedAmount().subtract((allocLineAmt.add(changeAmt))));
		}
		
		MAllocationLine allocLine = new MAllocationLine(allocHdr, allocLineAmt, BigDecimal.ZERO, writeOffAmt, BigDecimal.ZERO);
		allocLine.setChangeAmt(changeAmt);
		
		if(!Util.isEmpty(debitInvoiceID, true)){
			allocLine.setC_Invoice_ID(debitInvoiceID);
		}

		allocLine.setC_Order_ID(morder.getC_Order_ID());
		allocLine.setC_BPartner_ID(morder.getC_BPartner_ID());
		
		if (pay != null) {
			allocLine.setC_Payment_ID(pay.getC_Payment_ID());
		} else if (cashLine != null) {
			allocLine.setC_CashLine_ID(cashLine.getC_CashLine_ID());
		} else if (creditInvoiceID != null) {
			allocLine.setC_Invoice_Credit_ID(creditInvoiceID);
		}
		
		throwIfFalse(allocLine.save());
		
		allocLines.add(allocLine);
		
		return allocLine;
	}
	
	private void createOxpPayments(Order order) throws PosException {
		createPOSPaymentValidations.validatePayments(this, order);
		
		for (CashPayment p : cashPayments)
			createOxpCashPayment(p);
		
		for (CheckPayment p : checkPayments)
			createOxpCheckPayment(p);

		for (CreditCardPayment p : creditCardPayments)
			createOxpCreditCardPayment(p, order);

		for (CreditPayment p : creditPayments)
			createOxpCreditPayment(p);
		
		for (CreditNotePayment p : creditNotePayments)
			createOxpCreditNotePayment(p);
		
		for (BankTransferPayment p : bankTransferPayments)
			createOxpBankTransferPayment(p);

		// Completar Pagos. En este punto ya están creadas y guardadas las lineas de imputacion (MAllocationLine),
		// por lo que puedo completarlos sin que se generen conflictos con la imputacion automática.
		// Para más informacion mirar completeIt() (allocateIt()) de MPayment.
		
		for (MPayment p : mpayments.values()) {
			// Seteo el bypass para que no actualice el crédito del cliente ya
			// que se realiza luego al finalizar las operaciones
			p.setUpdateBPBalance(false);
			throwIfFalse(p.processIt(DocAction.ACTION_Complete), p);
			throwIfFalse(p.save(), p);
		}
		// Ajustar el cambio a entregar al cliente dependiendo del remanente en
		// efectivo existente y las devoluciones de efectivo de las NC
		// Por ahora no se ajusta el cambio y se deja sólo el cambio de efectivo
		// para setearse en la factura
//		adjustChange(order);
	}
	
	private void createOxpCashPayment(CashPayment p) throws PosException {
		createOxpCashPayment(
				getShouldCreateInvoice() ? invoice.getC_Invoice_ID() : 0, p,
				true, true);
	}
	
	private MCashLine createOxpCashPayment(Integer debitInvoiceID, CashPayment p, Integer chargeID, boolean addToCashLineList, boolean addToAllocation) throws PosException {
		// MCashBook cashBook = new MCashBook(ctx, posConfig.getC_CashBook_ID(), trxName);
		MCash cash = null;
		// Si el config tiene asociado el Cash entonces se usa ese (Cajas Diarias)
		if (getPoSCOnfig().getCashID() > 0) {
			cash = new MCash(ctx, getPoSCOnfig().getCashID(), getTrxName());
		// Sino se obtiene uno para la fecha el Libro indicado en el config
		} else {
			cash = MCash.get(ctx, getPoSCOnfig().getCashBookID(), invoiceDate, getTrxName());
		}
		
		throwIfFalse(cash.getC_Cash_ID() > 0);
		MCashLine cashLine = new MCashLine(cash);
		String cashType = p.getCashType();
		// Verificar que el CurrencyID sea valido con respecto al Amount 
		
		BigDecimal convertedAmt = p.getAmount();
		
		cashLine.setDescription("");
		
		if(!Util.isEmpty(debitInvoiceID, true)){
			cashType = Util.isEmpty(cashType, true)?MCashLine.CASHTYPE_Invoice:cashType;
			cashLine.setC_Invoice_ID(debitInvoiceID);
		}
		else{
			if(!Util.isEmpty(chargeID, true)){
				cashType = Util.isEmpty(cashType, true)?MCashLine.CASHTYPE_Charge:cashType;
				cashLine.setC_Charge_ID(chargeID);
			}
			else{
				cashType = Util.isEmpty(cashType, true)?MCashLine.CASHTYPE_GeneralReceipts:cashType;
			}
		}
		
		cashLine.setCashType(cashType);
		cashLine.setUpdateBPBalance(false);
		cashLine.setC_Currency_ID(p.getCurrencyId());
		cashLine.setAmount(convertedAmt);
		cashLine.setDiscountAmt(BigDecimal.ZERO);
		cashLine.setWriteOffAmt(BigDecimal.ZERO);
		cashLine.setIsGenerated(true);
		cashLine.setIgnoreAllocCreate(true);
		cashLine.setDescription(p.getDescription());
		if(p.getPaymentMedium() != null){
			cashLine.setC_POSPaymentMedium_ID(p.getPaymentMedium().getId());
		}
		cashLine.setIgnoreInvoiceOpen(true);
		
		throwIfFalse(cashLine.save(), cashLine); // Necesario para que se asigne el C_CashLine_ID
		throwIfFalse(cashLine.processIt(MCashLine.ACTION_Complete),cashLine);
		throwIfFalse(cashLine.save(), cashLine);

		// Agrego el cashline para llevar su registro
		if(addToCashLineList){
			getMCashLines().put(cashLine.getID(), cashLine);
		}
		if(addToAllocation){
			MAllocationLine allocLine = createOxpMAllocationLine(p, cashLine);
		}
		return cashLine;
	}
	
	private MCashLine createOxpCashPayment(Integer debitInvoiceID, CashPayment p, boolean addToCashLineList, boolean addToAllocation) throws PosException {
		return createOxpCashPayment(debitInvoiceID, p, null, addToCashLineList, addToAllocation);
	}
	
	private void createOxpCheckPayment(CheckPayment p) throws PosException {
		
		MPayment pay = createOxpMPayment(MPayment.TENDERTYPE_Check, getClientCurrencyID(), p.getAmount(), null);
		String sucursal = VModelHelper.getSQLValueString(null, " select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ", p.getBankAccountID() );
		pay.setDateAcct(invoice.getDateAcct()); 
		pay.setDateTrx(p.getDateTrx()); 
		pay.setDateEmissionCheck(p.getEmissionDate());
		
		pay.setC_BankAccount_ID(p.getBankAccountID());
		pay.setCheckNo(p.getCheckNumber()); // Numero de cheque
		pay.setMicr(sucursal + ";" + p.getBankAccountID() + ";" + p.getCheckNumber()); // Sucursal; cta; No. cheque
		pay.setA_Name(""); // Nombre
		pay.setA_Bank(p.getBankName());
		pay.setA_CUIT(p.getCuitLibrador());
		pay.setDueDate(p.getAcctDate()); // AcctDate es la fecha de Vencimiento
		pay.setDescription(p.getDescription());
		pay.setC_POSPaymentMedium_ID(p.getPaymentMedium().getId());
		
		throwIfFalse(pay.save(), pay);
		mpayments.put(pay.getC_Payment_ID(), pay);
		MAllocationLine allocLine = createOxpMAllocationLine(p, pay);
		
		if (sobraPorCheques != null && p.getAmount().compareTo(sobraPorCheques) > 0) {
			allocLine.setAmount(allocLine.getAmount().subtract(sobraPorCheques));
			
			sobraPorCheques = null;
		}
	}
	
	private void createOxpCreditCardPayment(CreditCardPayment p, Order order) throws PosException {
		// El monto de cupón es el monto del cobro + el monto de retiro
		BigDecimal amount = p.getAmount();
		amount = amount
				.add(Util.isEmpty(p.getChangeAmt(), true) ? BigDecimal.ZERO : p
						.getChangeAmt());
		
		MPayment pay = createOxpMPayment(MPayment.TENDERTYPE_CreditCard, getClientCurrencyID(), amount, p.getCouponNumber());
		MEntidadFinanciera entidadFinanciera = new MEntidadFinanciera(ctx, p.getEntidadFinancieraID(), null);  
		
		String CreditCardType = entidadFinanciera.getCreditCardType();
		
		pay.setCreditCard(MPayment.TRXTYPE_Sales, CreditCardType, p.getCreditCardNumber(), "", 0, 0 );
		pay.setC_BankAccount_ID(entidadFinanciera.getC_BankAccount_ID());
		pay.setM_EntidadFinancieraPlan_ID(p.getPlan().getEntidadFinancieraPlanID());
		pay.setCouponNumber(p.getCouponNumber());
		pay.setCouponBatchNumber(p.getCouponBatchNumber());
		pay.setA_Bank(p.getBankName());
		// Setea el nombre del cliente o de la entidad comercial al responsable
		// del cobro con tarjeta de crédito
		pay.setA_Name(!Util.isEmpty(order.getBusinessPartner()
				.getCustomerName(), true) ? order.getBusinessPartner()
				.getCustomerName() : order.getBusinessPartner().getName());
		pay.setA_Street(order.getBusinessPartner().getCustomerAddress());
		pay.setDescription(p.getDescription());
		pay.setPosnet(p.getPosnet());
		pay.setC_POSPaymentMedium_ID(p.getPaymentMedium().getId());
		
		throwIfFalse(pay.save(), pay);
		mpayments.put(pay.getC_Payment_ID(), pay);
		createOxpMAllocationLine(p, pay);
		
		// Si el cobro posee retiro de efectivo entonces creo el débito y lo
		// asocio en el allocation
		createCreditCardRetirementInvoice(p, pay, order);
	}
	
	private void createCreditCardRetirementInvoice(CreditCardPayment p, MPayment pay, Order order) throws PosException{
		if(Util.isEmpty(p.getChangeAmt(), true)){
			return;
		}
		// Crear el débito
		MInvoice creditCardRetirementInvoice = new MInvoice(getCtx(), 0, getTrxName());
		MBPartner bPartner = new MBPartner(getCtx(), order.getBusinessPartner().getId(), getTrxName());		
		creditCardRetirementInvoice.setBPartner(bPartner);
		creditCardRetirementInvoice.setCUIT(bPartner.getTaxID());
		creditCardRetirementInvoice.setNombreCli(order.getBusinessPartner().getCustomerName());
		creditCardRetirementInvoice.setInvoice_Adress(order.getBusinessPartner().getCustomerAddress());
		creditCardRetirementInvoice.setNroIdentificCliente(order.getBusinessPartner().getCustomerIdentification());
		creditCardRetirementInvoice.setPaymentRule(invoice.getPaymentRule());
		// Tipo de documento de retiro de efectivo de tarjeta de crédito de la
		// config del tpv
		creditCardRetirementInvoice.setC_DocTypeTarget_ID(getPoSCOnfig()
				.getCreditCardCashRetirementDocTypeID());
		creditCardRetirementInvoice.setC_DocType_ID(getPoSCOnfig()
				.getCreditCardCashRetirementDocTypeID());
		creditCardRetirementInvoice.setCreateCashLine(false);
		creditCardRetirementInvoice.setSkipManualGeneralDiscount(true);
		creditCardRetirementInvoice.setTPVInstance(true);
		creditCardRetirementInvoice.setIsVoidable(true);
		creditCardRetirementInvoice.setDocAction(MInvoice.DOCACTION_Complete);
		creditCardRetirementInvoice.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		// Guardo la factura
		throwIfFalse(creditCardRetirementInvoice.save(),
				creditCardRetirementInvoice, InvoiceCreateException.class);
		// Crear la línea con un artículo en particular
		MInvoiceLine creditCardRetirementInvoiceLine = new MInvoiceLine(creditCardRetirementInvoice);
		
		creditCardRetirementInvoiceLine.setDirectInsert(true);
		creditCardRetirementInvoiceLine.setQty(BigDecimal.ONE);
		creditCardRetirementInvoiceLine.setLine(10);
		creditCardRetirementInvoiceLine.setPrice(p.getChangeAmt());
		creditCardRetirementInvoiceLine.setC_Project_ID(creditCardRetirementInvoice.getC_Project_ID());
		// Artículo de retiro de efectivo de tarjeta de crédito de la config del tpv
		creditCardRetirementInvoiceLine.setM_Product_ID(getPoSCOnfig()
				.getCreditCardCashRetirementProductID());
		
		// Impuesto Exento
		creditCardRetirementInvoiceLine
				.setC_Tax_ID(getTaxExento() != null ? getTaxExento().getID()
						: 0);
		creditCardRetirementInvoiceLine.setSkipManualGeneralDiscount(true);
		
		// Guardo la línea
		throwIfFalse(creditCardRetirementInvoiceLine.save(),
				InvoiceCreateException.class);
		// Completo la factura
		throwIfFalse(
				creditCardRetirementInvoice
						.processIt(DocAction.ACTION_Complete),
				creditCardRetirementInvoice, InvoiceCreateException.class);
		// Guardo la factura
		throwIfFalse(creditCardRetirementInvoice.save(),
				creditCardRetirementInvoice, InvoiceCreateException.class);
		// Creo la línea del allocation
		createOxpMAllocationLine(creditCardRetirementInvoice.getID(), p, pay,
				null, null, p.getChangeAmt(), true);
		
		// Crear la línea del efectivo para el retiro de la caja
		CashPayment cashPayment = new CashPayment(p.getChangeAmt().negate());
		cashPayment.setCashType(MCashLine.CASHTYPE_Charge);
		cashPayment.setCurrencyId(p.getCurrencyId());
		cashPayment.setAmount(p.getChangeAmt().negate());
		cashPayment.setDescription(Msg.parseTranslation(
				getCtx(),
				"@CashRetirement@" + " " + "@CouponNumber@" + " " 
						+ p.getCouponNumber()));
		// Cargo de retiro de efectivo de tarjeta de crédito de la config del tpv
		MCashLine cashLine = createOxpCashPayment(0, cashPayment,
				getPoSCOnfig().getCreditCardCashRetirementChargeID(), false,
				false);
		// Asocio la línea de caja con el pago de tarjeta para futuras consultas
		cashLine.setC_Payment_ID(pay.getID());
		throwIfFalse(cashLine.save(), cashLine);
		// Agregar al allocation de manera unidireccional
		createOxpMAllocationLine(0, cashPayment, null, cashLine, null, null, true);
	}
	
	private void createOxpCreditPayment(CreditPayment p) throws PosException {
		// NULL. Nothing to be done. Please, move along.
	}

	private void createOxpCreditNotePayment(CreditNotePayment p) throws PosException {
		// Validar que la NC tenga las mismas formas de pago que la factura
		String creditNotePaymentRule = getPaymentRule(p.getInvoiceID());
		if(!invoice.getPaymentRule().equals(creditNotePaymentRule)){
			throw new PosException(Msg.getMsg(getCtx(), "NotAllowedAllocateCreditDiffPaymentRule",
					new Object[] {
							MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
									creditNotePaymentRule),
							MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
									invoice.getPaymentRule()) }));
		}
		// Si la factura finaliza con forma de pago A Crédito, entonces las NC
		// deben ser de la misma EC
		if(invoice.getC_BPartner_ID() != getBPartnerID(p.getInvoiceID())){
			throw new PosException(Msg.getMsg(getCtx(), "BPCreditNoteMustBeSameInvoiceCC", new Object[] {
					MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID, invoice.getPaymentRule()) }));
		}
		// No se debe crear ningún documento de pago ya que la nota de crédito ya
		// existe en el sistema. Solo se hace la imputación contra la factura del pedido
		createOxpMAllocationLine(p, p.getInvoiceID());
		// Se deben crear los pagos en efectivo para las devoluciones en
		// efectivo e imputarlo a la NC
		if (p.isReturnCash() && p.getChangeAmt() != null
				&& p.getChangeAmt().compareTo(BigDecimal.ZERO) > 0) {
			CashPayment cashPayment = new CashPayment(p.getChangeAmt().negate());
			cashPayment.setCashType(MCashLine.CASHTYPE_Invoice);
			cashPayment.setCurrencyId(p.getCurrencyId());
			cashPayment.setAmount(p.getChangeAmt().negate());
			cashPayment.setDescription(Msg.translate(getCtx(), "CNCashReturning"));
			MCashLine cashLine = createOxpCashPayment(p.getInvoiceID(), cashPayment, false, false);
			createOxpMAllocationLine(p.getInvoiceID(), cashPayment, null, cashLine, null, null, true);
		}
	}

	private void createOxpBankTransferPayment(BankTransferPayment p) throws PosException {
		MPayment pay = createOxpMPayment(MPayment.TENDERTYPE_DirectDeposit, p.getCurrencyId(), p.getAmount(), null);
		
		pay.setDateTrx(p.getTransferDate());
		pay.setDateAcct(invoice.getDateAcct());
		pay.setC_BankAccount_ID(p.getBankAccountID());
		pay.setCheckNo(p.getTransferNumber());
		pay.setDescription(p.getDescription());
		pay.setC_POSPaymentMedium_ID(p.getPaymentMedium().getId());
		throwIfFalse(pay.save(), pay);
		mpayments.put(pay.getC_Payment_ID(), pay);
		createOxpMAllocationLine(p, pay);
	}
	
	private MAllocationHdr createOxpAllocation() throws PosException {
		
		MAllocationHdr hdr = new MAllocationHdr(ctx, 0, getTrxName());
		
		hdr.setAllocationType(MAllocationHdr.ALLOCATIONTYPE_SalesTransaction);
		
		if(getAllocationDocType() != null){
			hdr.setC_DocType_ID(getAllocationDocType().getID());
		}

		BigDecimal approvalAmt = sumaPagos;
		
		hdr.setApprovalAmt(approvalAmt);
		hdr.setGrandTotal(approvalAmt); // GrandTotal = approvalAmt - Retenciones
		hdr.setRetencion_Amt(BigDecimal.ZERO);

		hdr.setC_BPartner_ID(partner.getC_BPartner_ID());
		hdr.setC_Currency_ID(getClientCurrencyID());
		
		hdr.setDateAcct(this.invoiceDate);
		hdr.setDateTrx(this.invoiceDate);
		
		hdr.setDescription("TPV: ");
		hdr.setIsManual(false);
		
		throwIfFalse(hdr.save(), hdr);
		
		return hdr;
	}
	
	private void doCompleteAllocation() throws PosException {
		
		
		for (MAllocationLine al : allocLines) 
		{
			throwIfFalse(al.save());
		}
		
		// Completar Allocation
		
		// Seteo el bypass para que no actualice el crédito del cliente ya
		// que se realiza luego al finalizar las operaciones
		allocHdr.setUpdateBPBalance(false);
		
		if (allocLines.size() > 0) {
			throwIfFalse(allocHdr.processIt(DocAction.ACTION_Complete), allocHdr);
			throwIfFalse(allocHdr.save(), allocHdr);
		} else if (creditPayments.size() > 0) {
			/*
			 * Si el medio de pago es crédito no se crea ningún elemento de pago ya 
			 * que la factura creada quedará con un saldo a pagar.
			 * 
			 * En este caso, no hay que crear ningun allocation.
			 * 
			 */
			throwIfFalse(allocHdr.processIt(DocAction.ACTION_Void), allocHdr);
			throwIfFalse(allocHdr.save(), allocHdr);
		} else {
			throw new PosException("doCompleteAllocation: allocLines.size() == 0 && creditPayments.size() == 0");
		}
		
	}

	/**
	 * Actualiza el cambio con la suma del cambio existente actualmente y la
	 * suma de las devoluciones de notas de crédito imputadas
	 * 
	 * @param order
	 *            pedido actual
	 */
	protected void adjustChange(Order order){
		order.getTotalChangeAmt();
	}
	
	@Override
	public User searchUser(String name, String password) throws UserException {
		MUser mUser = MUser.get(Env.getCtx(),name,password);
		// El usuario o clave son incorrectos;
		if(mUser == null)
			throw new UserException("InvalidUserPassError");
		
		return getUser(mUser.getID());
	}
	
	@Override
	public BusinessPartner getBPartner(int bPartnerID) {
		MBPartner mBPartner = new MBPartner(Env.getCtx(),bPartnerID,null);
		// Se toma siempre el M_DiscountSchema_ID de la EC ya que es transacción de ventas.
		DiscountSchema discountSchema = getDiscountSchema(mBPartner.getM_DiscountSchema_ID());
		// Esquema de vencimientos de la entidad comercial
		PaymentTerm paymentTerm = getPaymentTerm(mBPartner.getC_PaymentTerm_ID());
		// Medio de Pago de la entidad comercial
		PaymentMedium paymentMedium = getPaymentMedium(mBPartner.getC_POSPaymentMedium_ID());
		BusinessPartner rBPartner = new BusinessPartner(bPartnerID, 0,
				mBPartner.getTaxID(), mBPartner.getName(), mBPartner
						.getM_PriceList_ID(), discountSchema, mBPartner
						.getFlatDiscount(), paymentTerm, paymentMedium);
		rBPartner.setDiscountSchemaContext(mBPartner.getDiscountContext());
		int codigoIVA = 0;
		boolean isPercepcionLiable = false;
		if(mBPartner.getC_Categoria_Iva_ID() > 0){
			MCategoriaIva catIva = new MCategoriaIva(getCtx(), mBPartner.getC_Categoria_Iva_ID(), null);
			codigoIVA = catIva.getCodigo();
			isPercepcionLiable = catIva.isPercepcionLiable();
			if(!Util.isEmpty(catIva.getC_Tax_ID(), true)){
				MTax mTax = MTax.get(getCtx(), catIva.getC_Tax_ID(), null);
				rBPartner.setTax(new Tax(mTax.getID(), mTax.getRate(), mTax
						.isPercepcion()));
			}
		}
		rBPartner.setIVACategory(codigoIVA);
		rBPartner.setPercepcionLiable(isPercepcionLiable);
		rBPartner.setAutomaticCreditNote(mBPartner.isAutomaticCreditNotes());
		
		rBPartner.setCustomerName(mBPartner.getName());
		// Si no es la misma EC que la por defecto en la config, se cargan los datos
		// de la EC como datos del comprador (DNI y Dirección).
		if (bPartnerID != getPoSCOnfig().getBPartnerCashTrxID()) {
			rBPartner.setCustomerAddress(getBPartnerLocations(bPartnerID).get(0).toString());
			rBPartner.setCustomerIdentification(mBPartner.getTaxID());
			// Indica que los datos del comprador se deben mantener sincronizados
			// con los datos de la EC.
			rBPartner.setCustomerSynchronized(true);
		}
		return rBPartner;
	}

	@Override
	public List<Location> getBPartnerLocations(int bPartnerID) {
		MBPartnerLocation[] bpLocations = MBPartnerLocation.getForBPartner(Env.getCtx(),bPartnerID);
		List<Location> locations = new ArrayList<Location>();
		
		for (int i = 0; i < bpLocations.length; i++) {
			if (bpLocations[i].isActive()) {
				MLocation mLocation = bpLocations[i].getLocation(false);
				Location location = new Location(bpLocations[i].getID(), mLocation.toStringShort());
				locations.add(location);
			}
		}
		return locations;
	}

	@Override
	public boolean productStockValidate(int productId, BigDecimal count, int attributeSetInstanceID) {
		MProduct mProduct = new MProduct(ctx,productId,null);
		boolean stockAvailable;
        if( mProduct.isStocked()) {
            int M_Warehouse_ID = getPoSCOnfig().getWarehouseID(); 
            BigDecimal availableCount;
            // Se consulta el stock para el articulo con instancia de atributo si la misma
            // existe.
            if (attributeSetInstanceID > 0)
            	availableCount = MStorage.getQtyAvailable( M_Warehouse_ID, productId, attributeSetInstanceID, null );
            else
            	availableCount = MStorage.getQtyAvailable( M_Warehouse_ID, productId);
            
            if( availableCount == null ) {
            	stockAvailable = false;
            } else { 
            	stockAvailable =  
            		(availableCount.compareTo(count) >= 0 );
                
            }
        } else
        	stockAvailable = true;

		return stockAvailable;
	}

	@Override
	public int getOrgCityId() {
		MOrgInfo orgInfo = MOrgInfo.get(ctx,Env.getAD_Org_ID(ctx));
		int orgLocId = orgInfo.getC_Location_ID();
		MLocation orgLoc = MLocation.get(ctx,orgLocId,null);
		return orgLoc.getC_City_ID();
	}

	@Override
	public List<EntidadFinanciera> getEntidadesFinancieras() {
		if(entidadesFinancieras == null){
			loadEntidadesFinancieras();
		}
		return entidadesFinancieras;
	}
	
	/**
	 * Cargo las entidades financieras
	 */
	public void loadEntidadesFinancieras(){
		List<EntidadFinanciera> entidades = new ArrayList<EntidadFinanciera>();
		String sql = "SELECT M_EntidadFinanciera_ID, Name, CardMask, isallowcreditcardcashretirement, creditcardcashretirementlimit " +
			 	     "FROM M_EntidadFinanciera " + 
			 	     "WHERE ((C_City_ID IS NULL) OR (C_City_ID = ?)) AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND IsActive = 'Y' ";
		
		// Se comenta esta línea ya que se cargas las entidades financieras de
		// la organización actual o de la 0
		//sql = MRole.getDefault().addAccessSQL( sql, "M_EntidadFinanciera", false, true );
		
		// FIXME El filtro de las ciudad de la organización tiene sentido?
		
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql);
			// Seteo parametros
			pstmt.setInt(1,getOrgCityId());
			pstmt.setInt(2,Env.getAD_Org_ID(getCtx()));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int c_EntidadFinanciera_ID = rs.getInt(1);
				String name = rs.getString(2);
				
				EntidadFinanciera entidad = new EntidadFinanciera(
						c_EntidadFinanciera_ID, name, rs.getString("CardMask"),
						rs.getString("isallowcreditcardcashretirement").equals(
								"Y"), rs.getBigDecimal("creditcardcashretirementlimit"));
				entidades.add(entidad);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		setEntidadesFinancieras(entidades);
	}

	@Override
	public List<PoSConfig> getPoSConfigs() {
		int AD_Client_ID = Env.getAD_Client_ID(ctx);
		int AD_Org_ID = Env.getAD_Org_ID(ctx);
		int AD_User_ID = Env.getAD_User_ID(ctx);
		Timestamp today = Env.getDate();
		List<PoSConfig> posConfigs;
		
		// Cajas Diarias Activas. Verifica si hay al menos una caja diaria
		// abierta para este usuario, fecha y org y obtiene las configs a partir
		// de esas cajas diarias.
		if (MPOSJournal.isActivated()){
			posConfigs = getJournalPOSConfigs(AD_Org_ID, AD_User_ID, today);
		
		// Cajas Diarias NO Activas. Busca configuraciones de TPV con modo
		// de operación simple (único usuario - Modo Antiguo) para el
		// usuario y organización actual. (este es el comportamiento
		// original antes del agregado de Cajas Diarias)
		} else {
			String sqlWhere = " AD_Client_ID = " + AD_Client_ID + 
				              " AND AD_Org_ID = " + AD_Org_ID +
				              " AND SalesRep_ID = " + AD_User_ID +
				              " AND IsActive = 'Y' " +
				              " AND OperationMode = '" + MPOS.OPERATIONMODE_POSSimple + "'"; 
			
			int[] posIds = MPOS.getAllIDs("C_Pos",sqlWhere,null);
			posConfigs = new ArrayList<PoSConfig>();
			
			for (int i = 0; i < posIds.length; i++) {
				int C_Pos_ID = posIds[i];
				MPOS mPos = new MPOS(ctx, C_Pos_ID, null);
				posConfigs.add(new PoSConfig(mPos));
			}
		}
		return posConfigs;
	}
	
	/**
	 * 
	 */
	public void printTicket() throws Exception{
		// Salimos si ya se imprimió mediante controlador fiscal
		if (invoice != null && invoice.requireFiscalPrint()) {
			return;
		}

		TimeStatsLogger.beginTask(MeasurableTask.POS_PRINT_TICKET);
		
		int processID = 0;
		int tableID = 0;
		int recordID = 0;
		// Por defecto se lanza el informe configurado en la pestaña de encabezado
		// del pedido.
		processID = new M_Tab(ctx, ORDER_TAB_ID, getTrxName()).getAD_Process_ID();
		tableID = MOrder.Table_ID;
		recordID = morder.getC_Order_ID();
		
		// En caso de que se haya emitido una factura, entonces se lanza el informe
		// de la factura.
		if (getShouldCreateInvoice()) {
			// Primero se busca el informe en el Tipo de Documento de la Factura
			MDocType docType = MDocType.get(getCtx(), invoice.getC_DocType_ID());
			if (docType.getAD_Process_ID() > 0) {
				processID = docType.getAD_Process_ID();
			// Si el tipo de documento no tiene un informe asociado, se utiliza el
			// informe configurado para la ventana de Facturas de Clientes.
			} else {
				processID = new M_Tab(ctx, INVOICE_TAB_ID, null)
						.getAD_Process_ID();
			}
			tableID = MInvoice.Table_ID;
			recordID = invoice.getC_Invoice_ID();
		}

		// Lanza el informe determinado en caso de existir.
        if( processID > 0 ) {
	        ProcessInfo pi = new ProcessInfo("TPV", processID, tableID, recordID);	            
	        ProcessCtl.process(getProcessListener(), 0, pi, null );    // calls lockUI, unlockUI
        } else {
			// Método antoguo. En caso de que no se haya podido determinar un
			// Informe (AD_Process) se intenta imprimir un informe a partir de
			// los formatos de impresión.
        	printTicketFromPrintFormat();
        }

        //
		TimeStatsLogger.endTask(MeasurableTask.POS_PRINT_TICKET);
	}

	private void printTicketFromPrintFormat() {
		int order_ID = morder.getC_Order_ID();
		MPrintFormat pf = getTicketPrintFormat();
		if (pf != null) {
			// Se configura la consulta para la impresión.
			MQuery query = new MQuery( "C_Order" );
			query.addRestriction( "C_Order_ID", MQuery.EQUAL, new Integer(order_ID), "", "" );
			// Se crea la información para la impresión.
			PrintInfo info = new PrintInfo( pf.getName(), pf.getAD_Table_ID(), order_ID );
			info.setDescription( query.getInfo());
			// Se instancia el motor de reportes.
			ReportEngine re = new ReportEngine( Env.getCtx(), pf, query, info );
			View vv = re.getView();
			// TODO: printer name ?
			re.getPrintInfo().setWithDialog( true );
			re.print();
		}
	}
	
	private MPrintFormat getTicketPrintFormat() {
		MPrintFormat pf = null;
		// Consulta de formato de impresión. 
		// 1. Primero dá prioridad a la configuración realizada
		// en la ventana de formatos, filtrando por compañia y organización.
		// 2. Si no encuentra un formato específico para la organización, obtiene
		// uno asignado para la compañia sin filtrar por organización.
		// 3. Finalmente, si tampoco hay resultados en 2, consulta el formato
		// de impresión asignado al tipo de documento, en la tabla de DocTypes.
		String sql =
			" SELECT AD_PrintFormat_ID "+
			" FROM ( "+
			"	(SELECT Order_Printformat_ID AS AD_PrintFormat_ID "+
			"	FROM AD_PrintForm "+
			"	WHERE AD_Client_ID = ? AND AD_Org_ID = ?) "+
	
			"	UNION "+
	
			" 	(SELECT Order_Printformat_ID AS AD_PrintFormat_ID "+
			"	FROM AD_PrintForm "+
			"	WHERE AD_Client_ID = ?) "+
	
			"	UNION "+
	
			"	(SELECT AD_PrintFormat_ID "+
			"	FROM C_DocType "+
			"	WHERE C_DocType_ID = ?) "+
			" ) f "+
			" WHERE AD_PrintFormat_ID IS NOT NULL ";
		
		// Se obtiene la compañia y organización para consultar
		// el formato de impresión asignado.
		int AD_Client_ID = Env.getAD_Client_ID(ctx);
		int AD_Org_ID = Env.getAD_Org_ID(ctx);

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql);
			// Se asignan los parámetros.
			int i = 1;
			pstmt.setInt(i++, AD_Client_ID);
			pstmt.setInt(i++, AD_Org_ID);
			pstmt.setInt(i++, AD_Client_ID);
			pstmt.setInt(i++, getPoSCOnfig().getOrderDocTypeID());
			// Se consultan los datos, si no se encuentra ningún formato retorna
			// un resulset vacio.
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int printFormatID = rs.getInt("AD_PrintFormat_ID");
				pf = MPrintFormat.get( Env.getCtx(), printFormatID, false);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return pf;
	}

	@Override
	public Product getProduct(int productId, int attributeSetInstanceId) {
		List<Integer> vendorsIDs = null;
		int m_PriceList_ID = getPoSCOnfig().getPriceListID(); 
		// Obtengo la tarifa, y pido la version valida.
		MPriceList mPriceList = new MPriceList(Env.getCtx(),m_PriceList_ID,null);
		int m_PriceList_Version_ID = mPriceList.getPriceListVersion(null).getID();
		Product product = null;
		
		String sql;
		
		// Obtiene el id, nombre y el precio en la tarifa del producto.
		/*
		sql = "SELECT p.M_Product_ID, pp.PriceStd, p.Name, pp.PriceLimit, p.upc " +
			  "FROM M_ProductPrice pp, M_Product p " +
              "WHERE p.M_Product_ID = pp.M_Product_ID " +
              "	 AND p.M_Product_ID = ? " +
              "  AND M_PriceList_Version_ID = ? " +
              "  AND p.IsActive = 'Y' " +
              "  AND pp.IsActive = 'Y' ";
		 */
		
		sql = " SELECT p.M_Product_ID, bomPriceStd(p.M_Product_ID, ?, ?), p.value, p.name, bomPriceLimit(p.M_Product_ID, ?, ?), p.upc, s.MandatoryType, p.m_product_category_id, p.CheckoutPlace, p.IsSold, p.Value " +    
			  "	FROM M_Product p " +
			  "	LEFT JOIN M_AttributeSet s ON (p.M_AttributeSet_ID = s.M_AttributeSet_ID)     " +
			  " WHERE p.M_Product_ID = ? " +
			  "   AND p.IsActive = 'Y'";

		
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql);
			int i = 1;
			pstmt.setInt(i++,m_PriceList_Version_ID);
			pstmt.setInt(i++,attributeSetInstanceId);

			pstmt.setInt(i++,m_PriceList_Version_ID);
			pstmt.setInt(i++,attributeSetInstanceId);

			pstmt.setInt(i++,productId);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				int m_Product_Id = rs.getInt(1);
				BigDecimal productPrice = rs.getBigDecimal(2);
				String productName = rs.getString(3)+" - "+rs.getString(4);
				BigDecimal productLimitPrice = rs.getBigDecimal(5);
				String code = rs.getString(6);
				if (code == null || code.trim().isEmpty() || code.trim().equals("0")) {
					code = rs.getString("Value");
				}
				boolean masiMandatory = MAttributeSet.MANDATORYTYPE_AlwaysMandatory.equals(rs.getString("MandatoryType"));
				String masiDescription = (String)DB.getSQLObject(null, "SELECT Description FROM m_attributesetinstance WHERE m_attributesetinstance_id = ?", new Object[]{attributeSetInstanceId});
				vendorsIDs = getVendors(m_Product_Id);
				String checkoutPlace = rs.getString("CheckoutPlace");
				boolean sold = "Y".equals(rs.getString("IsSold"));
				// Creo el producto.
				product = new Product(
								m_Product_Id, 
								code, 
								productName, 
								productPrice, 
								productLimitPrice, 
								attributeSetInstanceId, 
								masiDescription, 
								getPoSCOnfig().isPriceListWithTax(), 
								getPoSCOnfig().isPriceListWithPerception(),
								masiMandatory, 
								rs.getInt("m_product_category_id"), 
								vendorsIDs,
								checkoutPlace,
								sold);
			}
			
			rs.close();
			pstmt.close();
			
			return product;
		
		} catch (SQLException e) {
			//TODO Relanzar una excepcion definida.
			return null;
		}
	}

	@Override
	public void reloadPoSConfig(int windowNo) {
		Properties ctx = Env.getCtx();
		Env.setContext(ctx, windowNo,"M_PriceList_ID", getPoSCOnfig().getPriceListID());
		Env.setContext(ctx, windowNo,"M_Warehouse_ID", getPoSCOnfig().getWarehouseID());
	}

	@Override
	public Tax getProductTax(int productId, int locationID) {
		Timestamp now = Env.getDate();
		BigDecimal taxRate;
		
		// Se obtiene el Tax del producto.
		int taxId = org.openXpertya.model.Tax.get( ctx,productId, 0, now, now, Env.getAD_Org_ID(ctx),getPoSCOnfig().getWarehouseID(), locationID,locationID,true);
		boolean isPercepcion = false;
		MTax mTax = MTax.get(ctx,taxId,null);
		if(mTax != null){
			taxRate = mTax.getRate();
			isPercepcion = mTax.isPercepcion();
		}
		else{
			taxRate = BigDecimal.ZERO;
		}
		return new org.openXpertya.pos.model.Tax(taxId, taxRate, isPercepcion);
	}
	
	public Tax getProductTax(int productID) {
		// Se obtiene la EC para efectivo para obtener la direcion para
		// calcular el impuesto del producto.
		MBPartner bPartner = new MBPartner(ctx,getPoSCOnfig().getBPartnerCashTrxID(),null);
		int locID = 0;
		if(bPartner.getID() > 0) {
			MBPartnerLocation[] locs = bPartner.getLocations(false);
			locID = (locs.length > 0 ? locs[0].getID() : 0);
		}
		
		return getProductTax(productID, locID);
	}

	@Override
	public Order loadOrder(int orderId, boolean loadLines) throws InvalidOrderException, PosException {
		if (orderId == 0)
			throw new InvalidOrderException();
		
		Order order = new Order(getOrganization());
		MOrder mOrder = new MOrder(ctx, orderId, getTrxName());
		if (!"CO".equals(mOrder.getDocStatus()) && ! "CL".equals(mOrder.getDocStatus()))
			throw new InvalidOrderException(Msg.translate(ctx, "POSOrderStatusError"));
		
		BusinessPartner bPartner = getBPartner(mOrder.getC_BPartner_ID());
		order.setId(orderId);
		order.setDate(mOrder.getDateOrdered());
		order.setOrderRep(mOrder.getSalesRep_ID());
		
		// Carga las líneas si así se indicó 
		if (loadLines) {
			loadOrderLines(order);
		}
		
		order.setBusinessPartner(bPartner);
		order.setOtherTaxes(getOtherTaxes(order.getDiscountableOrderWrapper()));
		
		return order;
	}

	@Override
	public void loadOrderLines(Order order) {
		
		// Nada que cargar...
		if (order.getId() == 0) {
			return;
		}
		
		MOrder mOrder = new MOrder(ctx, order.getId(), getTrxName());
		MOrderLine[] oLines = mOrder.getLines();
		for (int i = 0; i < oLines.length; i++) {
			MOrderLine line = oLines[i];
			if (line.getM_Product_ID() == 0)
				continue;
			if (line.getQtyOrdered().compareTo(BigDecimal.ZERO) == 0)
				continue;

			order.addOrderProduct(createOrderProduct(line));
		}
	}

	/**
	 * Crea un <code>OrderProduct</code> a partir de una línea de un pedido
	 * del sistema (C_OrderLine)
	 * @param line Línea del pedido.
	 * @return La línea del pedido de TPV.
	 */
	private OrderProduct createOrderProduct(MOrderLine line) {
		int m_Product_ID = line.getM_Product_ID();
		OrderProduct orderProduct = null;
		Product product = null; 
		
		product = getProduct(m_Product_ID, line.getM_AttributeSetInstance_ID());
		product.setStdPrice(line.getPriceList());
		
		MTax mTax = MTax.get(ctx, line.getC_Tax_ID(), null);
		// Lugar de retiro del artículo.
		String checkoutPlace;
		// Si el TPV no crea remito siempre el retiro es por almacén. Además, si crea remito
		// y está configurado para que los pedidos pre-creados agregados al pedido TPV sean
		// entregados por Almacén, entonces el retiro también es por almacén.
		if (!getPoSCOnfig().isCreateInOut()
				|| getPoSCOnfig().isDeliverOrderInWarehouse()) {
			checkoutPlace = MProduct.CHECKOUTPLACE_Warehouse;
		// Aquí, se crean remitos y los pedidos pre-creados agregados se incluyen en
		// el remito del TPV, por consiguiente el retiro es por TPV.
		} else {
			checkoutPlace = MProduct.CHECKOUTPLACE_PointOfSale;
		}
					
		orderProduct = 
			new OrderProduct(line.getQtyEntered(), 
					         line.getDiscount(),
					         new Tax(mTax.getID(), mTax.getRate(), mTax.isPercepcion()),
					         product, checkoutPlace);
		
		orderProduct.setPrice(line.getPriceActual());
		orderProduct.setLineDescription(line.getDescription());
		return orderProduct;
	}

	@Override
	public void validatePoSConfig() throws PosException {
		getPoSCOnfig().validateOnline();
	}

	@Override
	public User getCurrentUser() {
		return getUser(getPoSCOnfig().getCurrentUserID());
	}
	
	@Override
	public User getUser(int userID) {
		int supervisorRoleID = getPoSCOnfig().getSupervisorRoleID();
		MUser mUser = MUser.get(ctx, userID);
		// Validación de usuario.
		if (mUser == null)
			return null;
		
		User user = new User(mUser.getName(), mUser.getPassword());
		// Se chequea si el los perfiles del usuario, alguno tiene permiso
		// para sobreescribir el precio limite de los productos.
		boolean overwritePriceLimit = false;
		MRole[] roles = mUser.getRoles(Env.getAD_Org_ID(ctx));
		for (int i = 0; i < roles.length && !overwritePriceLimit; i++) {
			MRole userRole = roles[i];
			if(userRole.isOverwritePriceLimit()) {
				overwritePriceLimit = true;
			}
		}
		user.setOverwriteLimitPrice(overwritePriceLimit);

		// Verifico si el usuario tiene el perfil de supervisor del TPV
		// para poder realizar modificaciones de productos en el pedido.
		user.setPoSSupervisor(false);
		MRole[] userRoles = mUser.getRoles(Env.getAD_Org_ID(Env.getCtx()));
		for (int i = 0; i < userRoles.length && !user.isPoSSupervisor(); i++) {
			MRole userRole = userRoles[i];
			if(userRole.getAD_Role_ID() == supervisorRoleID)
				user.setPoSSupervisor(true);
		}
		
		return user;
	}

	@Override
	public List<PriceList> getPriceLists() {
		// Buscar las listas de precios de ventas
		List<PO> priceLists = PO.find(Env.getCtx(), "m_pricelist", "ad_client_id = ? and isactive = 'Y' and issopricelist = 'Y'", new Object[]{Env.getAD_Client_ID(Env.getCtx())}, null, null);  
		List<PriceList> lists = new ArrayList<PriceList>();
		PriceList newPriceList;
		MPriceList mPriceList;
		for (PO priceList : priceLists) {
			mPriceList = (MPriceList)priceList; 
			newPriceList = new PriceList(mPriceList.getID(),mPriceList.getName(),mPriceList.getDescription(),mPriceList.getC_Currency_ID(),mPriceList.isTaxIncluded(),mPriceList.isPerceptionsIncluded(),mPriceList.isSOPriceList(),mPriceList.isDefault(),mPriceList.getPricePrecision());
			lists.add(newPriceList);
		}
		return lists;
	}

	@Override
	public PriceList getCurrentPriceList(int windowNo) {
		MPriceList priceList = new MPriceList(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), windowNo, "M_PriceList_ID"), null);
		PriceList newPriceList = new PriceList(priceList.getID(),priceList.getName(), priceList.getDescription(),priceList.getC_Currency_ID(),priceList.isTaxIncluded(),priceList.isPerceptionsIncluded(),priceList.isSOPriceList(),priceList.isDefault(),priceList.getPricePrecision());
		return newPriceList;
	}

	@Override
	public void updatePriceList(PriceList newPriceList, int windowNo) {
		// Seteo la nueva tarifa dentro del contexto
		Env.setContext(ctx, windowNo,"M_PriceList_ID", newPriceList.getId());
	}
	
	@Override
	public void updatePriceList(Integer priceListID, int windowNo) {
		// Seteo la nueva tarifa dentro del contexto
		Env.setContext(ctx, windowNo,"M_PriceList_ID", priceListID);
	}
	
	@Override
	public PriceListVersion getCurrentPriceListVersion(PriceList priceList, int windowNo) {
		Timestamp priceDate = null;
        // Sales Order Date
        String dateStr = Env.getContext( Env.getCtx(),windowNo,"DateOrdered" );
        if( (dateStr != null) && (dateStr.length() > 0) ) {
            priceDate = Env.getContextAsDate( Env.getCtx(),windowNo,"DateOrdered" );
        } 
        else {// Invoice Date
            dateStr = Env.getContext( Env.getCtx(),windowNo,"DateInvoiced" );
            if( (dateStr != null) && (dateStr.length() > 0) ) {
                priceDate = Env.getContextAsDate( Env.getCtx(),windowNo,"DateInvoiced" );
            }
        }
        // Today
        if( priceDate == null ) {
            priceDate = Env.getDate();
        }
        int versionID = 0;
        PriceListVersion version = null;
        String SQL = "SELECT plv.M_PriceList_Version_ID, plv.ValidFrom " + "FROM M_PriceList pl, M_PriceList_Version plv " + "WHERE pl.M_PriceList_ID=plv.M_PriceList_ID" + " AND plv.IsActive='Y'" + " AND pl.M_PriceList_ID=? "    // 1
                          + "ORDER BY plv.ValidFrom DESC";
        // find newest one
        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            pstmt.setInt( 1,priceList.getId());
            ResultSet rs = pstmt.executeQuery();
            while( rs.next() && (versionID == 0) ) {
                Timestamp plDate = rs.getTimestamp( 2 );
                if( !priceDate.before( plDate )) {
                	versionID = rs.getInt( 1 );
                }
            }
            if(versionID != 0){
            	MPriceListVersion pLVersion = new MPriceListVersion(Env.getCtx(), versionID, null);
            	version = new PriceListVersion(pLVersion.getID(), pLVersion.getName(), pLVersion.getDescription(), pLVersion.getM_DiscountSchema_ID(), pLVersion.getValidFrom());
            }
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            e.printStackTrace();
        }
//        Env.setContext( Env.getCtx(),windowNo,"M_PriceList_Version_ID",retValue );
        return version;
	}

	@Override
	public BigDecimal getProductPrice(Product product, PriceListVersion priceListVersion) {
		BigDecimal price = null;
		if(priceListVersion != null && product != null){
			PO productPrice = PO.findFirst(Env.getCtx(), "m_productprice", "m_product_id = ? AND m_pricelist_version_id = ?", new Object[]{product.getId(),priceListVersion.getId()}, new String[]{"created desc"}, null);
			if(productPrice != null){
				// En el TPV el precio de lista es el precio std
				price = ((MProductPrice)productPrice).getPriceStd();
			}
		}		
		return price;
	}

	@Override
	public void updateBPartner(BusinessPartner bpartner, int windowNo) {
		Env.setContext(ctx, windowNo,"C_BPartner_ID", bpartner == null?0:bpartner.getId());
	}

	@Override
	public DiscountSchema getDiscountSchema(int discountSchemaID) {
		DiscountSchema discountSchema = null;
		if(discountSchemaID > 0){
			discountSchema = new DiscountSchema(discountSchemaID, this);
			// Se ignoran los esquemas que no son válidos para la fecha actual.
			if (!discountSchema.isValid()) {
				discountSchema = null;
			}
		}
		return discountSchema;
	}

	@Override
	public List<Integer> getVendors(int productID) {
		List<PO> vendors = PO.find(ctx, "M_Product_PO", "m_product_id= ? AND iscurrentvendor='Y'", new Object[]{productID}, null, null);
		List<Integer> vendorsID = new ArrayList<Integer>();
		MProductPO productPO;
		for (PO mProductPO : vendors) {
			productPO = (MProductPO)mProductPO;
			vendorsID.add(productPO.getC_BPartner_ID());
		}
		return vendorsID;
	}
	
	/**
	 * @return Devuelve la sub-consulta para búsqueda exacta y/o parcial de artículos
	 * por UPC
	 */
	private String getSearchByUPCQuery() {
		StringBuffer query = new StringBuffer();
		// La consulta base contiene partes sin completar encerradas entre <>. Estos tags
		// son luego reemplazados por valores concretos según la necesidad de hacer una 
		// búsqueda exacta y/o parcial. De esta forma la consulta se esribe una única vez
		// y se reutiliza en ambas búsquedas.
		StringBuffer baseQuery = new StringBuffer();
	    baseQuery
	      // 1. Busca en los UPC de instancias. Estos son los que tiene mayor prioridad
	      .append("(SELECT M_Product_ID, M_AttributeSetInstance_ID, <MT_M_Product_Upc_Instance> As MatchType ")
	      .append( "FROM M_Product_Upc_Instance ")
	      .append( "WHERE (UPPER(UPC) <COMPARATOR> UPPER(<VALUE>)) AND IsActive = 'Y') ")
	      .append("UNION ")
		  // 2. Busca en los UPC asociados al artículo. Primero se lista el UPC predeterminado.
	      .append("(SELECT M_Product_ID, 0, <MT_M_ProductUPC> ") 
		  .append( "FROM M_ProductUPC ")
		  .append( "WHERE (UPPER(UPC) <COMPARATOR> UPPER(<VALUE>)) AND IsActive = 'Y' ")
		  .append( "ORDER BY IsDefault DESC) ")
		  .append("UNION ")
		  // 3. Buscan por UPC en los artículos asociados al proveedor
		  .append("(SELECT M_Product_ID, 0, <MT_M_Product_PO> ")
		  .append( "FROM M_Product_PO ")
		  .append( "WHERE (UPPER(UPC) <COMPARATOR> UPPER(<VALUE>)) AND IsActive = 'Y') ") 
		  .append("UNION ")
		  // 4. Busca por VendorProductNo en los artículos asociados a entidades comerciales 
		  .append("(SELECT M_Product_ID, 0, <MT_C_BPartner_Product> ")
		  .append( "FROM C_BPartner_Product ")
		  .append( "WHERE (UPPER(VendorProductNo) <COMPARATOR> UPPER(<VALUE>)) AND isActive = 'Y') ");

	    String exactQuery = null;
	    String partialQuery = null;
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda exacta por UPC
	    if (getPoSCOnfig().isSearchByUPC()) {
	    	exactQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por igualdad exacta
	    	exactQuery = exactQuery.replaceAll("<COMPARATOR>", "=");
	    	exactQuery = exactQuery.replaceAll("<VALUE>", "?");
	    	// Reemplaza los Tipos de Matching
	    	exactQuery = exactQuery.replaceAll("<MT_M_Product_Upc_Instance>", String.valueOf(ProductList.MASI_UPC_EXACT_MATCH));
	    	exactQuery = exactQuery.replaceAll("<MT_M_ProductUPC>", String.valueOf(ProductList.UPC_EXACT_MATCH));
	    	exactQuery = exactQuery.replaceAll("<MT_M_Product_PO>", String.valueOf(ProductList.PO_UPC_EXACT_MATCH));
	    	exactQuery = exactQuery.replaceAll("<MT_C_BPartner_Product>", String.valueOf(ProductList.BP_CODE_EXACT_MATCH));
	    	query.append(exactQuery);
	    }
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda parcial por UPC
	    if (getPoSCOnfig().isSearchByUPCLike()) {
	    	partialQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por ILIKE para no tener 
	    	// en cuenta mayúsculas y minúsculas, y solo se agrega un comodín al final de la
	    	// cadena. Debido a que los UPC son comúnmente numéricos, si se agregara un
	    	// comodín al inicio también produciría demasiados resultados desechables.
	    	partialQuery = partialQuery.replaceAll("<COMPARATOR>", "ILIKE");
	    	partialQuery = partialQuery.replaceAll("<VALUE>", "(? || '%')");
	    	// Reemplaza los Tipos de Matching
	    	partialQuery = partialQuery.replaceAll("<MT_M_Product_Upc_Instance>", String.valueOf(ProductList.MASI_UPC_PARTIAL_MATCH));
	    	partialQuery = partialQuery.replaceAll("<MT_M_ProductUPC>", String.valueOf(ProductList.UPC_PARTIAL_MATCH));
	    	partialQuery = partialQuery.replaceAll("<MT_M_Product_PO>", String.valueOf(ProductList.PO_UPC_PARTIAL_MATCH));
	    	partialQuery = partialQuery.replaceAll("<MT_C_BPartner_Product>", String.valueOf(ProductList.BP_CODE_PARTIAL_MATCH));
	    	// Si se requirió la búsqueda exacta es necesario unir los resultados con esta
	    	// búsqueda.
	    	if (exactQuery != null) {
	    		query.append("UNION ");
	    	}
	    	query.append(partialQuery);
	    }
		
		return query.toString();
	}
	
	/**
	 * @return Devuelve la sub-consulta para búsqueda exacta y/o parcial de artículos
	 * por Clave de Búsqueda
	 */
	private String getSearchByValueQuery() {
		StringBuffer query = new StringBuffer();
		// La consulta base contiene partes sin completar encerradas entre <>. Estos tags
		// son luego reemplazados por valores concretos según la necesidad de hacer una 
		// búsqueda exacta y/o parcial. De esta forma la consulta se esribe una única vez
		// y se reutiliza en ambas búsquedas.
		StringBuffer baseQuery = new StringBuffer();
	    baseQuery
		  // 1. Buscan por Value en la tabla de artículos
		  .append("(SELECT M_Product_ID, 0 AS M_AttributeSetInstance_ID, <MT_M_Product> AS MatchType ")
		  .append( "FROM M_Product ")
		  .append( "WHERE (UPPER(Value) <COMPARATOR> UPPER(<VALUE>)) AND IsActive = 'Y') "); 

	    String exactQuery = null;
	    String partialQuery = null;
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda exacta por Clave
	    // de Búsqueda
	    if (getPoSCOnfig().isSearchByValue()) {
	    	exactQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por igualdad exacta
	    	exactQuery = exactQuery.replaceAll("<COMPARATOR>", "=");
	    	exactQuery = exactQuery.replaceAll("<VALUE>", "?");
	    	// Reemplaza los Tipos de Matching
	    	exactQuery = exactQuery.replaceAll("<MT_M_Product>", String.valueOf(ProductList.VALUE_EXACT_MATCH));
	    	query.append(exactQuery);
	    }
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda parcial por UPC
	    if (getPoSCOnfig().isSearchByValueLike()) {
	    	partialQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por ILIKE para no tener 
	    	// en cuenta mayúsculas y minúsculas, y solo se agrega un comodín al final de la
	    	// cadena.
	    	partialQuery = partialQuery.replaceAll("<COMPARATOR>", "ILIKE");
	    	partialQuery = partialQuery.replaceAll("<VALUE>", "(? || '%')");
	    	// Reemplaza los Tipos de Matching
	    	partialQuery = partialQuery.replaceAll("<MT_M_Product>", String.valueOf(ProductList.VALUE_PARTIAL_MATCH));
	    	// Si se requirió la búsqueda exacta es necesario unir los resultados con esta
	    	// búsqueda.
	    	if (exactQuery != null) {
	    		query.append("UNION ");
	    	}
	    	query.append(partialQuery);
	    }
		
		return query.toString();
	}

	/**
	 * @return Devuelve la sub-consulta para búsqueda exacta y/o parcial de artículos
	 * por Nombre
	 */
	private String getSearchByNameQuery() {
		StringBuffer query = new StringBuffer();
		// La consulta base contiene partes sin completar encerradas entre <>. Estos tags
		// son luego reemplazados por valores concretos según la necesidad de hacer una 
		// búsqueda exacta y/o parcial. De esta forma la consulta se esribe una única vez
		// y se reutiliza en ambas búsquedas.
		StringBuffer baseQuery = new StringBuffer();
	    baseQuery
		  // 1. Buscan por Value en la tabla de artículos
		  .append("(SELECT M_Product_ID, 0 AS M_AttributeSetInstance_ID, <MT_M_Product> AS MatchType ")
		  .append( "FROM M_Product ")
		  .append( "WHERE (UPPER(Name) <COMPARATOR> UPPER(<VALUE>)) AND IsActive = 'Y') "); 

	    String exactQuery = null;
	    String partialQuery = null;
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda exacta por Clave
	    // de Búsqueda
	    if (getPoSCOnfig().isSearchByName()) {
	    	exactQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por igualdad de strings
	    	// sin case sentsitive para generar mejores resultados debido a que los nombres
	    	// de artículos no se diferencian por mayúsculas o minúsculas sino por la propia
	    	// cadena de nombres.
	    	exactQuery = exactQuery.replaceAll("<COMPARATOR>", "ILIKE");
	    	exactQuery = exactQuery.replaceAll("<VALUE>", "?");
	    	// Reemplaza los Tipos de Matching
	    	exactQuery = exactQuery.replaceAll("<MT_M_Product>", String.valueOf(ProductList.NAME_EXACT_MATCH));
	    	query.append(exactQuery);
	    }
	    
	    // La configuración de TPV indica que hay que realizar una búsqueda parcial por UPC
	    if (getPoSCOnfig().isSearchByNameLike()) {
	    	partialQuery = baseQuery.toString();
	    	// Reemplazada el comparador y el valor. Aquí se compara por ILIKE para no tener 
	    	// en cuenta mayúsculas y minúsculas, y se agregan un comodines al inicio y 
	    	// final de la cadena.
	    	partialQuery = partialQuery.replaceAll("<COMPARATOR>", "ILIKE");
	    	partialQuery = partialQuery.replaceAll("<VALUE>", "('%' || ? || '%')");
	    	// Reemplaza los Tipos de Matching
	    	partialQuery = partialQuery.replaceAll("<MT_M_Product>", String.valueOf(ProductList.NAME_PARTIAL_MATCH));
	    	// Si se requirió la búsqueda exacta es necesario unir los resultados con esta
	    	// búsqueda.
	    	if (exactQuery != null) {
	    		query.append("UNION ");
	    	}
	    	query.append(partialQuery);
	    }
		
		return query.toString();
	}

	/**
	 * Devuelve la cantidad de parámetros que requiere una consulta SQL para se utilizada
	 * como una {@link PreparedStatement}. Simplemente cuenta la cantidad de caracteres 
	 * <code>?</code> que existen dentro del String.
	 * @param sql Consulta SQL origen
	 * @return Cantidad de parametros requeridos.
	 */
	private int parametersCount(String sql) {
		int count = 0;
		char[] chars = sql.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '?') {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Imprime el comprobante para retiro de artículos por almacén en caso de estar
	 * indicada esta opción en la configuración del TPV.
	 */
	public void printWarehouseDeliveryTicket(Order order) throws Exception{
		/*
		 * Por el momento SOLO se imprime el comprobante mediante una Impresora Fiscal, con
		 * lo cual se deben cumplir estas condiciones:
		 * 1) Está activada la localización Argentina
		 * 2) El TPV está configurado para realizar la factura
		 * 3) El tipo de documento de la factura está configurado para que se emita
		 *    el comprobante mediante un controlador fiscal, teniendo este tipo asociada
		 *    la impresora fiscal a utilizar
		 * 
		 * Al cumplirse estas 3 condiciones, aquí se obtiene la impresora fiscal asociada
		 * al tipo de documento de la factura creada, y se emite el ticket para retiro x
		 * almacén en esa impresora fiscal.   
		 */
		
		// El pedido tiene al menos un artículo que se retira por almacén, 
		// además se creó la factura y el TPV está configurado para emitir el 
		// documento de retiro 		
		if (getPoSCOnfig().isPrintWarehouseDeliverDocument()  
				&& invoice != null 
				&& order.getWarehouseCheckoutProductsCount() > 0) {
			// El tipo de documento de la factura debe ser fiscal y tener asociado
			// un controlador fiscal.
			MDocType docType = MDocType.get(ctx, invoice.getC_DocType_ID());
			if (docType.getC_Controlador_Fiscal_ID() > 0) {
				// Impresor de comprobantes.
				FiscalDocumentPrint fdp = new FiscalDocumentPrint();
				fdp.addDocumentPrintListener(getFiscalDocumentPrintListener());
				fdp.setPrinterEventListener(getFiscalPrinterEventListener());
				// Se recarga el pedido para no interferir con transacciones
				MOrder orderAux = new MOrder(getCtx(), morder.getID(), null);
				MInvoice invoiceAux = new MInvoice(getCtx(), invoice.getID(), null);
				if(!fdp.printDeliveryDocument(docType.getC_Controlador_Fiscal_ID(), orderAux, invoiceAux)) {
					
				}
			}
			// Impresión del comprobante Jasper que muestra los artículos que 
			// se deben retirar por almacén
			else{
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("C_Order_ID", morder.getID());
				params.put("C_Invoice_ID", invoice.getID());
				ProcessInfo info = MProcess.execute(getCtx(), MInvoice
						.getWarehouseDeliverDocumentProcessID(null),
						params, null);
				if(info.isError()){
					
				}
			}
		}
	}

	/**
	 * Imprime el comprobante con datos del cliente en cuenta corriente si así
	 * lo indica esta opción en la configuración del TPV.
	 */
	public void printCurrentAccountTicket(Order order) throws Exception{
		// Se imprime este comprobante cuando existe una condición de venta de
		// la cual es cuenta corriente. Además, existe un flag en la config del
		// TPV que permite imprimir o no este documento
		// Si no hay ninguna condición de venta de cuenta corriente, también se
		// debe imprimir este documento en el caso que el cliente posea notas de
		// crédito automáticas y exista al menos alguna nota de crédito cargada
		CurrentAccountInfo currentAccountInfo = null;
		List<CurrentAccountInfo> infos = new ArrayList<CurrentAccountInfo>();
		CurrentAccountInfo infoOnCredit = null;
		if (getPoSCOnfig().isPrintCurrentAccountDocument() && invoice != null
				&& (!Util.isEmpty(currentAccountSalesConditions.keySet()) || (order
						.getBusinessPartner().isAutomaticCreditNote() && sumaCreditNotePayments
						.compareTo(BigDecimal.ZERO) > 0))) {
			MBPartner partner = new MBPartner(getCtx(), order
					.getBusinessPartner().getId(), null);
			// Itero por las condiciones de venta de cuenta corriente
			for (String paymentRule : currentAccountSalesConditions.keySet()) {
				currentAccountInfo = new CurrentAccountInfo(null,
						MRefList.getListName(getCtx(),
								MInvoice.PAYMENTRULE_AD_Reference_ID,
								paymentRule),
						currentAccountSalesConditions.get(paymentRule));
				infos.add(currentAccountInfo);
				if(MInvoice.PAYMENTRULE_OnCredit.equals(paymentRule)){
					infoOnCredit = currentAccountInfo;
				}
			}
			// Sumar lo de NC siempre que sea automática y tildar aquella
			// condición a crédito
			if (order.getBusinessPartner().isAutomaticCreditNote()) {
				if(infoOnCredit != null){
					infoOnCredit.setAmount(infoOnCredit.getAmount().add(
							sumaCreditNotePayments));
				}
				else{
					currentAccountInfo = new CurrentAccountInfo(null,
							MRefList.getListName(getCtx(),
									MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
									MPOSPaymentMedium.TENDERTYPE_CreditNote),
									sumaCreditNotePayments);
					infos.add(currentAccountInfo);
				}
			}
			
			// Imprimo el ticket no fiscal de cuenta corriente, en caso que no
			// se imprima por la fiscal, entonces va al formato jasper
			MDocType docType = MDocType.get(ctx, invoice.getC_DocType_ID());
			if (docType.getC_Controlador_Fiscal_ID() > 0) {
				// Impresor de comprobantes.
				FiscalDocumentPrint fdp = new FiscalDocumentPrint();
				fdp.addDocumentPrintListener(getFiscalDocumentPrintListener());
				fdp.setPrinterEventListener(getFiscalPrinterEventListener());
				MInvoice invoiceAux = new MInvoice(getCtx(), invoice.getID(), null);
				if (!fdp.printCurrentAccountDocument(
						docType.getC_Controlador_Fiscal_ID(), partner, invoiceAux, infos)) {
					
				}
			}
			else{
				Map<String, Object> params = new HashMap<String, Object>();
				// FIXME Por ahora hay un sólo medio de pago de cuenta corriente
				// (A Crédito). Si existen otros medios de pago con esas
				// condiciones, se debe dar soporte para ellos dentro de este
				// bloque, además de pasarlos como parámetro al reporte
				params.put("AD_Org_ID", Env.getAD_Org_ID(getCtx()));
				params.put("C_BPartner_ID", partner.getID());
				params.put("C_Invoice_ID", invoice.getID());
				params.put(
						"PaymentRule_1",
						infoOnCredit != null ? order.getPaymentTerm().getName()
								: MRefList
										.getListName(
												getCtx(),
												MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
												MPOSPaymentMedium.TENDERTYPE_CreditNote));
				params.put("PaymentRule_Amt_1",
						infoOnCredit != null ? infoOnCredit.getAmount()
								: currentAccountInfo.getAmount());
				ProcessInfo info = MProcess.execute(getCtx(), MInvoice
						.getCurrentAccountDocumentProcessID(null),
						params, null);
				if(info.isError()){
					
				}
			}
		}
	}
	
	@Override
	public List<PaymentMedium> getPaymentMediums() {
		List<PaymentMedium> paymentMediums = new ArrayList<PaymentMedium>();
		// Se buscan los medios de pago que sean válidos para la fecha actual y
		// que su contexto de uso no sea Recibos de Cliente
		List<MPOSPaymentMedium> mPaymentMediums = MPOSPaymentMedium
				.getAvailablePaymentMediums(getCtx(), null,
						MPOSPaymentMedium.CONTEXT_CustomerReceiptsOnly, true,
						getTrxName());
		PaymentMedium paymentMedium = null;
		int discountSchemaID = 0;
		for (MPOSPaymentMedium mposPaymentMedium : mPaymentMediums) {
			// Crea la instancia del medio de pago con los datos del C_POSPaymentMedium
			paymentMedium = new PaymentMedium(mposPaymentMedium.getID(),
					mposPaymentMedium.getName(), mposPaymentMedium
							.getTenderType(), mposPaymentMedium
							.getC_Currency_ID());
			// Si tiene esquema de descuento se obtiene la instancia y se asocia al
			// medio de pago.
			discountSchemaID = mposPaymentMedium.getM_DiscountSchema_ID();
			if (discountSchemaID > 0) {
				paymentMedium.setDiscountSchema(getDiscountSchema(discountSchemaID));
			}
			// Si es un medio de pago de tipo Tarjeta de Crédito se guarda el ID de 
			// la entidad financiera y se cargan los planes de tarjeta disponibles
			// para la misma
			if (paymentMedium.isCreditCard()) {
				paymentMedium
						.setEntidadFinanciera(getEntidadFinanciera(mposPaymentMedium
								.getM_EntidadFinanciera_ID()));
				paymentMedium
						.setCreditCardPlans(getCreditCardPlans(paymentMedium
								.getEntidadFinanciera().getId()));
			}
			
			// Si es un medio de pago de tipo Cheque se guarda el plazo de cobro
			if (paymentMedium.isCheck()) {
				paymentMedium.setCheckDeadLine(Integer
						.parseInt(mposPaymentMedium.getCheckDeadLine()));
				paymentMedium
						.setValidationBeforeCheckDeadLines(mposPaymentMedium
								.isValidateBeforeCheckDeadLines());
				paymentMedium.setBeforeCheckDeadLineFrom(mposPaymentMedium
						.getBeforeCheckDeadLineFrom() != null ? Integer
						.parseInt(mposPaymentMedium
								.getBeforeCheckDeadLineFrom()) : null);
				paymentMedium.setBeforeCheckDeadLineTo(mposPaymentMedium
						.getBeforeCheckDeadLineTo() != null ? Integer
						.parseInt(mposPaymentMedium.getBeforeCheckDeadLineTo())
						: null);
				paymentMedium
						.setBeforeCheckDeadLinesToValidate(getBeforeCheckDeadLinesToValidate(mposPaymentMedium));
			}
			
			// Cheques y Tarjetas pueden contener un banco (lista de referencia)
			// Se guarda el value en el objeto.
			if (paymentMedium.isCreditCard() || paymentMedium.isCheck()) {
				paymentMedium.setBank(mposPaymentMedium.getBank());
			}
			
			// Se asigna el nombre del tipo de pago a partir de la lista de referencia
			paymentMedium.setTenderTypeName(
				MRefList.getListName(
						ctx, 
						MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID, 
						paymentMedium.getTenderType()
				)
			);
			
			paymentMediums.add(paymentMedium);
		}
		
		return paymentMediums;
	}

	/**
	 * @param entidadFinancieraID
	 *            id de entidad financiera
	 * @return la entidad financiera con el id parámetro
	 */
	protected EntidadFinanciera getEntidadFinanciera(int entidadFinancieraID){
		EntidadFinanciera financiera = null;
		boolean found = false;
		for (int i = 0; i < getEntidadesFinancieras().size() && !found; i++){
			if(getEntidadesFinancieras().get(i).getId() == entidadFinancieraID){
				found = true;
				financiera = getEntidadesFinancieras().get(i); 
			}
		}
		return financiera;
	}
	
	/**
	 * Devuelve una lista con todos los planes válidos para la fecha actual de una
	 * Entidad Financiera.
	 * @param entidadFinancieraID ID de Entidad Financiera
	 * @return {@link List} de {@link EntidadFinancieraPlan}
	 */
	protected List<EntidadFinancieraPlan> getCreditCardPlans(int entidadFinancieraID) {
		List<EntidadFinancieraPlan> creditCardPlans = new ArrayList<EntidadFinancieraPlan>();
		// Se buscan los planes de la entidad financiera cuyo rango de validez
		// contenga la fecha actual.
		List<MEntidadFinancieraPlan> mCreditCardPlans = MEntidadFinancieraPlan
				.getPlansAvailables(getCtx(), entidadFinancieraID, getTrxName());
		EntidadFinancieraPlan plan = null;
		int discountSchemaID;
		for (MEntidadFinancieraPlan mEntidadFinancieraPlan : mCreditCardPlans) {
			// Crea la instancia del plan
			plan = new EntidadFinancieraPlan(
				mEntidadFinancieraPlan.getID(),
				entidadFinancieraID,
				mEntidadFinancieraPlan.getName(),
				mEntidadFinancieraPlan.getCuotasPago()
			);
			// Asocia el esquema de descuento si tiene
			discountSchemaID = mEntidadFinancieraPlan.getM_DiscountSchema_ID();
			if (discountSchemaID > 0) {
				plan.setDiscountSchema(getDiscountSchema(discountSchemaID));
			}
			creditCardPlans.add(plan);
		}	
		return creditCardPlans;
	}

	@Override
	public BigDecimal getCreditAvailableAmount(int invoiceID) {
		try {
			BigDecimal AvailableamountToConvert = 
				(BigDecimal)DB.getSQLObject(null, "SELECT invoiceOpen(?, 0)", new Object[] { invoiceID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(null, "SELECT C_Currency_ID From C_Invoice where C_Invoice_ID = " + invoiceID));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return the ctx
	 */
	public Properties getCtx() {
		return ctx;
	}
	
	/**
	 * Efectua el guardado a la BD de los descuentos aplicados al pedido TPV
	 * @param order Pedido TPV.
	 */
	private void saveDiscounts(Order order) throws PosException {
		// Los descuentos a guardar son del discountcalculator creado a partir
		// del MOrder creado, no este ya que puede tener distintas tasas de IVA
		// de impuestos adicionales
//		throwIfFalse(order.getDiscountCalculator().saveDiscounts(getTrxName()));
		throwIfFalse(discountCalculator.saveDiscounts(getTrxName()));
	}
	
	/**
	 * Busca la lista de Cajas Diarias abiertas para un usuario en una organización 
	 * determinada, indicando además la fecha de validez de la misma.
	 * @param orgID ID de organización
	 * @param userID ID de usuario
	 * @param date Fecha de la caja
	 * @return Lista de Configs encontrados.
	 */
	private List<PoSConfig> getJournalPOSConfigs(int orgID, int userID, Timestamp date) {
		List<PoSConfig> configs = new ArrayList<PoSConfig>();
		
		String sql =
			"SELECT * " +
			"FROM C_POSJournal " +
			"WHERE AD_Org_ID = ? " +
			  "AND AD_User_ID = ? " +
			  "AND DateTrx::date = ?::date " +
			  "AND DocStatus = ? ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql, null, true);
			int i = 1;
			pstmt.setInt(i++, orgID);
			pstmt.setInt(i++, userID);
			pstmt.setTimestamp(i++, date);
			pstmt.setString(i++, MPOSJournal.DOCSTATUS_Opened);
			
			rs = pstmt.executeQuery();
			MPOSJournal journal = null;
			while (rs.next()) {
				journal = new MPOSJournal(getCtx(), rs, null);
				if (journal.getC_POS_ID() > 0) {
					configs.add(new PoSConfig(journal));
				}
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "POS: Error getting POS Journals", e);
		}
		
		
		return configs;
	}

	public void setMCashLines(Map<Integer, MCashLine> cashLines) {
		this.mCashLines = cashLines;
	}

	public Map<Integer, MCashLine> getMCashLines() {
		return mCashLines;
	}

	public void setAditionalWorkResults(Map<PO, Object> aditionalWorkResults) {
		this.aditionalWorkResults = aditionalWorkResults;
	}

	public Map<PO, Object> getAditionalWorkResults() {
		return aditionalWorkResults;
	}
	
	@Override
	public List<PaymentTerm> getPaymentTerms() {
		String sql = "SELECT c_paymentterm_id, name, c_pospaymentmedium_id FROM c_paymentterm WHERE ad_client_id = ? AND isactive = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<PaymentTerm> pts = new ArrayList<PaymentTerm>();
		PaymentTerm pt = null;
		try {
			ps = DB.prepareStatement(sql, getTrxName());
			ps.setInt(1, Env.getAD_Client_ID(getCtx()));
			rs = ps.executeQuery();
			while(rs.next()){
				pt = new PaymentTerm(rs.getInt("c_paymentterm_id"), rs
						.getString("name"), rs.getInt("c_pospaymentmedium_id"));
				pts.add(pt);
				paymentTerms.put(pt.getId(), pt);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "POS: Error getting Payment Terms", e);
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();	
			} catch (Exception e2) {
				log.log(Level.SEVERE, "POS: Error getting Payment Terms", e2);
			}			
		}
		return pts;
	}

	@Override
	public PaymentTerm getPaymentTerm(int paymentTermID) {
		// Si no hay esquemas de vencimientos cargados entonces los cargo
		if(paymentTerms == null || paymentTerms.size() == 0){
			getPaymentTerms();
		}
		return paymentTerms.get(paymentTermID);
	}
	
	@Override
	public PaymentMedium getPaymentMedium(Integer paymentMediumID) {
		PaymentMedium pm = null;
		if(!Util.isEmpty(paymentMediumID, true)){
			MPOSPaymentMedium posPM = new MPOSPaymentMedium(getCtx(),
					paymentMediumID, getTrxName());
			pm = new PaymentMedium(posPM.getID(), posPM.getName(), posPM
					.getTenderType(), posPM.getC_Currency_ID());
		}
		return pm;
	}
	
	private void debug(String text) {
		System.out.println("TPV DEBUG ==> "
				+ DB.getSQLValueTimestamp(null, "select now()") + " - " + text);
	}
	
	
	/**
	 * @return Crea un nuevo nombre de transacción
	 */
	protected String createTrxName() {
		return Trx.createTrxName(this.toString() + invoiceDate.toString()
				+ Thread.currentThread().getId());
	}

	@Override
	public void voidDocuments() throws PosException {
		/*
		 * PRECONDICION: se asume que el TPV ha generado la factura ya que una
		 * anulación de documentos solo es realizable en caso de no haber podido
		 * emitir la factura mediante el controlador fiscal.
		 */
		String trxName = createTrxName();

		try {
//			// Primero anulamos la imputación
//			if (allocHdr != null && allocHdr.getC_AllocationHdr_ID() > 0) {
//				MAllocationHdr vAlloc = new MAllocationHdr(getCtx(), allocHdr.getC_AllocationHdr_ID(), trxName);
//				vAlloc.setAllocationAction(MAllocationHdr.ALLOCATIONACTION_VoidPayments);
//				if (!DocumentEngine.processAndSave(vAlloc, MAllocationHdr.ACTION_Void, false)) {
//					throw new PosException(vAlloc.getProcessMsg());
//				}
//			}
//			
//			// Luego anulamos el remito (si existe)
//			if (shipment != null && shipment.getM_InOut_ID() > 0) {
//				MInOut vShipment = new MInOut(getCtx(), shipment.getM_InOut_ID(), trxName);
//				if (!DocumentEngine.processAndSave(vShipment, MInOut.ACTION_Void, false)) {
//					throw new PosException(vShipment.getProcessMsg());
//				}
//			}
//			
//			// Luego la factura
//			if (invoice != null && invoice.getC_Invoice_ID() > 0) {
//				MInvoice vInvoice = new MInvoice(getCtx(), invoice.getC_Invoice_ID(), trxName);
//				if (!DocumentEngine.processAndSave(vInvoice, MInvoice.ACTION_Void, false)) {
//					throw new PosException(vInvoice.getProcessMsg());
//				}
//			}
//			
//			// Finalmente el Pedido
//			if (morder != null && morder.getC_Order_ID() > 0) {
//				MOrder vOrder = new MOrder(getCtx(), morder.getC_Order_ID(), trxName);
//				if (!DocumentEngine.processAndSave(vOrder, MOrder.ACTION_Void, false)) {
//					throw new PosException(vOrder.getProcessMsg());
//				}
//			}
			
			
			
			InvoiceGlobalVoiding voidingProcess = new InvoiceGlobalVoiding(
					invoice.getC_Invoice_ID(), getCtx(), trxName);
			voidingProcess.setControlMoreDebitsInAllocation(false);
			voidingProcess.start();
			
			Trx.getTrx(trxName).commit();
			
		} catch (Exception e) {
			Trx.getTrx(trxName).rollback();
			throw new PosException(Msg.parseTranslation(getCtx(), e.getMessage()));
		} finally {
			Trx.getTrx(trxName).close();
		}
	}
	
	//Ader: manejo de caches multi-documento
	private MProductCache m_prodCache;
	private MProductCache getProdCache()
	{
		return m_prodCache;
	}
	private void initCachesFromOrder(Order o)
	{
		//por ahora solo productos
		initCacheProdFromOrder(o);

    	
	}
	
	private void setCaches(MOrder mo)
	{
		//por ahora solo productos
		mo.setProductCache(m_prodCache);
	}
	private boolean initCacheProdFromOrder(Order o)
	{
		m_prodCache = new MProductCache(ctx,trxName);
    	List<Integer> newIds = new ArrayList<Integer>();
    	for (OrderProduct op: o.getOrderProducts())
    	{
    		int M_Product_ID = op.getProduct().getId();
    		if (M_Product_ID <= 0)
    			continue;
    		if (m_prodCache.contains(M_Product_ID))
    			continue;
    		if (newIds.contains(M_Product_ID))
    		    continue;
    		newIds.add(M_Product_ID);
    	}
    	
    	if (newIds.size() <= 0)
    		return true; 
    	
    	//carga masiva en cache; un solo acceso a DB
    	int qtyCached = m_prodCache.loadMasive(newIds);
    	
    	if (qtyCached != newIds.size())
    		return false; //algunos no se cargaron...
    	return true;
	}
	private MProduct getProductFromCache(int M_Product_ID)
	{
	   	if (m_prodCache == null)
     		m_prodCache = new MProductCache(getCtx(),trxName);
    	if (M_Product_ID <=0)
    		return null;
    	MProduct p = m_prodCache.get(M_Product_ID);
    	return p;
	}

	@Override
	public Integer getMaxOrderLineQty() {
		Integer maxQty = null;
		// Para locale ar verificamos la cantidad máxima de la preferencia
		// configurada
		if(LOCAL_AR_ACTIVE){
			// Obtenerlo desde las preferencias
			String maxQtyValue = MPreference.GetCustomPreferenceValue(
					MAX_ORDER_LINE_QTY_PREFERENCE_NAME,
					Env.getAD_Client_ID(getCtx()));
			maxQty = Util.isEmpty(maxQtyValue, true) ? null : Integer
					.valueOf(maxQtyValue);
		}
		return maxQty;
	}

	@Override
	public String getNextInvoiceDocumentNo() {
		String documentNo = null;
		Integer docTypeID = getActualDocTypeID();
		// Se obtiene el próximo nro de doc, si es que tengo tipo de doc
		if(!Util.isEmpty(docTypeID, true)){
			documentNo = CalloutInvoiceExt.getNextDocumentNo(getCtx(), docTypeID, getTrxName());
		}
		return documentNo;
	}

	/**
	 * Obtener el tipo de documento de la localización argentina
	 * 
	 * @return tipo de documento obtenido a partir de la categoría de iva de la
	 *         entidad comercial y de la compañía
	 * @throws PosException
	 *             en caso de error en la obtención
	 */
	public MDocType getLocaleArDocType() throws PosException{
		// Se obtiene la letra del comprobante
		MLetraComprobante mLetraComprobante = getLocaleArLetraComprobante();
		
		// Obtener el punto de venta específicamente para esta letra en la
		// config
		Integer posNumber = getPoSCOnfig().getPosNumberLetters().get(
				mLetraComprobante.getLetra());
		if(Util.isEmpty(posNumber, true)){
			posNumber = getPoSCOnfig().getPosNumber();
		}
		
		// Se obtiene el tipo de documento para la factura.
		return MDocType.getDocType(ctx, Env.getAD_Org_ID(getCtx()), 
				MDocType.DOCTYPE_CustomerInvoice, mLetraComprobante.getLetra(), posNumber,
				getTrxName());
	}

	/**
	 * Obtener la letra del comprobante para esta transacción de la localización
	 * argentina
	 * 
	 * @return letra de comprobante obtenido a partir de la categoría de iva de
	 *         la entidad comercial y de la compañía
	 * @throws PosException
	 *             en caso de error en la obtención
	 */
	public MLetraComprobante getLocaleArLetraComprobante() throws PosException{
		Integer categoriaIVAclient = CalloutInvoiceExt.darCategoriaIvaClient();
		Integer categoriaIVACustomer = partner == null ? 0 : partner
				.getC_Categoria_Iva_ID();
		
		// Se validan las categorias de IVA de la compañia y el cliente.
		if (categoriaIVAclient == null || categoriaIVAclient == 0) {
			throw new InvoiceCreateException("ClientWithoutIVAError");
		} else if (categoriaIVACustomer == null || categoriaIVACustomer == 0) {
			throw new InvoiceCreateException("BPartnerWithoutIVAError");
		}
		
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		Integer letraID = CalloutInvoiceExt.darLetraComprobante(categoriaIVACustomer, categoriaIVAclient);
		if (letraID == null || letraID == 0)
			throw new InvoiceCreateException("LetraCalculationError");
		
		// Se obtiene el PO de letra del comprobante.
		return new MLetraComprobante(ctx, letraID, getTrxName());
	}

	/**
	 * Obtengo los plazos de pagos de cheques existentes
	 */
	private void loadCheckDeadLines(){
		List<String> values = MRefList.getValueList(getCtx(),
				MPOSPaymentMedium.CHECKDEADLINE_AD_Reference_ID, getTrxName());
		List<Integer> intValues = new ArrayList<Integer>(); 
		for (String deadline : values) {
			try {
				intValues.add(Integer.parseInt(deadline));
			} catch(NumberFormatException nfe){
				log.severe(nfe.getMessage());
			}
		}
		setCheckDeadLines(intValues);
		Collections.sort(getCheckDeadLines());
	}
	
	public void setCheckDeadLines(List<Integer> checkDeadLines) {
		this.checkDeadLines = checkDeadLines;
	}

	public List<Integer> getCheckDeadLines() {
		if(checkDeadLines == null){
			loadCheckDeadLines();
		}
		return checkDeadLines;
	}

	@Override
	public boolean isCheckDeadLineInRange(Integer checkDeadLineToCompare,
			Integer checkDeadLineFrom, Integer checkDeadLineTo,
			Integer checkDeadLineActual) {
		return checkDeadLineFrom <= checkDeadLineToCompare
				&& (checkDeadLineTo != null ? (checkDeadLineTo >= checkDeadLineToCompare)
						: (checkDeadLineActual > checkDeadLineToCompare));
	}

	/**
	 * @param posPaymentMedium
	 *            medio de pago
	 * @return la lista de plazos anteriores de cheques que se deben validar
	 *         sobre el medio de pago cheque parámetro
	 */
	protected List<Integer> getBeforeCheckDeadLinesToValidate(MPOSPaymentMedium posPaymentMedium){
		List<Integer> befores = new ArrayList<Integer>();
		// Si el medio de pago necesita validar los plazos anteriores
		if(posPaymentMedium.isValidateBeforeCheckDeadLines()){
			// Itero por los plazos existentes de los cheques y cargo la lista
			// con cada uno de ellos. Si el final del rango es null, entonces se
			// toma el plazo anterior al actual
			for (Integer deadline : getCheckDeadLines()) {
				if (isCheckDeadLineInRange(deadline,
						Integer.parseInt(posPaymentMedium.getBeforeCheckDeadLineFrom()),
						!Util.isEmpty(
								posPaymentMedium.getBeforeCheckDeadLineTo(),
								true) ? Integer.parseInt(posPaymentMedium
								.getBeforeCheckDeadLineTo()) : null,
						Integer.parseInt(posPaymentMedium.getCheckDeadLine()))) {
					befores.add(deadline);
				}
			}
		}
		return befores;
	}

	public void setEntidadesFinancieras(List<EntidadFinanciera> entidadesFinancieras) {
		this.entidadesFinancieras = entidadesFinancieras;
	}

	@Override
	public List<Tax> getOtherTaxes(IDocument document) {
		getGeneratorPercepciones().loadDocument(document);
		return getOtherTaxes();
	}
	
	public List<Tax> getOtherTaxes() {
		List<Tax> otherTaxes = new ArrayList<Tax>();
		List<MTax> mOtherTaxes = new ArrayList<MTax>();
		// Percepciones
		try{
			mOtherTaxes.addAll(getGeneratorPercepciones().getApplyPercepciones());
		} catch(Exception e){
			e.printStackTrace();
		}
		// Itero por los impuestos adicionales
		for (MTax mTax : mOtherTaxes) {
			otherTaxes.add(new Tax(mTax.getID(), mTax.getRate(), mTax.isPercepcion()));
		}
		
		return otherTaxes;
	}

	@Override
	public Tax getTax(Integer taxID) {
		MTax mTax = MTax.get(ctx,taxID,null);
		boolean isPercepcion = false;
		BigDecimal taxRate = BigDecimal.ZERO; 
		if(mTax != null){
			taxRate = mTax.getRate();
			isPercepcion = mTax.isPercepcion();
		}
		return new org.openXpertya.pos.model.Tax(taxID, taxRate, isPercepcion);
	}

	@Override
	public boolean isCheckCUITControlActivated() {
		return CHECK_CUIT_CONTROL_ACTIVE;
	}

	@Override
	public boolean hasCreditNotesAvailables(Integer bpartnerID,	boolean excludeCreditNotes) {
		Map<Integer, BigDecimal> excludedCredits = new HashMap<Integer, BigDecimal>();
		if(excludeCreditNotes){
			BigDecimal creditNoteAmt;
			for (CreditNotePayment cnp : creditNotePayments) {
				creditNoteAmt = excludedCredits.get(cnp.getInvoiceID());
				if(creditNoteAmt != null){
					creditNoteAmt = creditNoteAmt.add(cnp.getAmount());
				}
				else{
					creditNoteAmt = cnp.getAmount();
				}
				excludedCredits.put(cnp.getInvoiceID(), creditNoteAmt);
			}
		}
		return MInvoice.hasCreditsOpen(getCtx(), bpartnerID, true,
				excludedCredits, getTrxName());
	}

	public CreatePOSPaymentValidations getCreatePOSPaymentValidations() {
		return createPOSPaymentValidations;
	}

	public void setCreatePOSPaymentValidations(
			CreatePOSPaymentValidations createPOSPaymentValidations) {
		this.createPOSPaymentValidations = createPOSPaymentValidations;
	}

	@Override
	public Organization getOrganization() {
		return new Organization(getmOrg().getID(), getmOrg().getName());
	}

	public MOrg getmOrg() {
		return mOrg;
	}

	public void setmOrg(MOrg mOrg) {
		this.mOrg = mOrg;
	}

	public GeneratorPercepciones getGeneratorPercepciones() {
		return generatorPercepciones;
	}

	public void setGeneratorPercepciones(GeneratorPercepciones generatorPercepciones) {
		this.generatorPercepciones = generatorPercepciones;
	}

	@Override
	public List<Tax> loadBPOtherTaxes(BusinessPartner bp) {
		getGeneratorPercepciones().loadBPartner(bp.getId());
		return getOtherTaxes();
	}

	protected CompleteOrderPOSValidations getCompleteOrderPOSValidations() {
		return completeOrderPOSValidations;
	}

	protected void setCompleteOrderPOSValidations(
			CompleteOrderPOSValidations completeOrderPOSValidations) {
		this.completeOrderPOSValidations = completeOrderPOSValidations;
	}

	private MDocType getPaymentDocType() {
		return paymentDocType;
	}

	private void setPaymentDocType(MDocType paymentDocType) {
		this.paymentDocType = paymentDocType;
	}

	public MTax getTaxExento() {
		return taxExento;
	}

	public void setTaxExento(MTax taxExento) {
		this.taxExento = taxExento;
	}

	@Override
	public boolean addSecurityValidationToCN() {
		return getRole().isAddSecurityValidation_POS_NC();
	}

	protected MRole getRole() {
		return role;
	}

	protected void setRole(MRole role) {
		this.role = role;
	}

	@Override
	public int getDolarCurrencyID() {
		if(dolarCurrencyID == 0){
			dolarCurrencyID = MCurrency.get(getCtx(), "USD").getID();
		}
		return dolarCurrencyID;
	}

	protected MDocType getAllocationDocType() {
		return allocationDocType;
	}

	protected void setAllocationDocType(MDocType allocationDocType) {
		this.allocationDocType = allocationDocType;
	}
	
	protected String getPaymentRule(Integer invoiceID){
		return DB.getSQLValueString(trxName, "SELECT paymentrule FROM c_invoice WHERE c_invoice_id = ?", invoiceID);
	}
	
	protected int getBPartnerID(Integer invoiceID){
		return DB.getSQLValue(trxName, "SELECT c_bpartner_id FROM c_invoice WHERE c_invoice_id = ?", invoiceID);
	}

	@Override
	public boolean isSOTrx() {
		return true;
	}

	@Override
	public boolean isSkipCurrentAccount() {
		MDocType dt = MDocType.get(getCtx(), getActualDocTypeID());
		return dt != null && dt.isSkipCurrentAccounts();
	}

	@Override
	public Integer getActualDocTypeID() {
		Integer docTypeID = 0;
		if(getShouldCreateInvoice()){
			// Si locale ar está activo, entonces hay que obtenerlo desde la
			// conjunción de la categoría de IVA de la entidad comercial y de la
			// Compañía 
			if(LOCAL_AR_ACTIVE){
				MDocType docType = null;
				try{
					docType = getLocaleArDocType();
					docTypeID = docType.getID();
				} catch(PosException pose){
					log.severe(Msg.getMsg(getCtx(), pose.getMessage()));
				}
			}
			// Si no es L_AR, obtenerlo desde el tipo de doc de factura
			// configurado dentro de la config del TPV
			else{
				docTypeID = getPoSCOnfig().getInvoiceDocTypeID();
			}	
		}
		return docTypeID;
	}
}
