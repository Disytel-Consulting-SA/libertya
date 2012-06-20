package org.openXpertya.rc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.apps.form.VOrdenPagoModel.MedioPago;
import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountCalculator.GeneralDiscountKind;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDiscountConfig;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.pos.model.AmountHelper;
import org.openXpertya.pos.model.Order;
import org.openXpertya.pos.model.Payment;
import org.openXpertya.rc.Invoice;
import org.openXpertya.rc.InvoiceLine;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ReciboDeCliente {

	public static final BigDecimal ROUND_TOLERANCE = new BigDecimal(0.01);
	
	/** Tipos de descuento */
	
	public static final String BPARTNER_DISCOUNT = "BPD";
	public static final String PAYMENTMEDIUM_DISCOUNT = "PMD";
	public static final String PAYMENTTERM_DISCOUNT = "PTD";
	public static final String CHARGE_DISCOUNT = "CD";
	
	/**
	 * Factura Global, es la agrupación de las facturas a pagar en una sola.
	 * NOTA: Esta factura se crea solamente para facilitar los cálculos del
	 * DiscountCalculator y hacerlo más parecido al TPV. NO se guarda en la BD y
	 * no se debería.
	 */
	private Invoice globalInvoice;
		
	/** Calculador de descuentos para la factura global */
	private DiscountCalculator discountCalculator;
	
	/** Facturas reales seleccionadas para cobrar */
	private List<Invoice> realInvoices;
	
	/** Esquema de descuento de la entidad comercial */
	private MDiscountSchema bPartnerDiscountSchema;
	
	/** Objeto que contiene el actual descuento general a aplicar */
	private MDiscountSchema currentGeneralDiscountSchema;
	
	/** Entidad Comercial */
	private MBPartner bpartner;
	
	/** Transacción en curso */
	private String trxName;
	
	/** Contexto actual */
	private Properties ctx;
	
	/** Fecha del recibo de cliente */
	private Timestamp date;
	
	/** Lista de créditos a imputar en la factura */
	private List<MedioPago> credits;
	
	/** Lista de retenciones a imputar en la factura */
	private List<RetencionProcessor> retenciones;
	
	/** Configuración de descuento inicial */
	private MDiscountConfig discountConfig;

	/**
	 * Monto total de cargo por organización (Sólo es utilizado para crear el
	 * débito o crédito correspondiente, no debe entrar como monto base para
	 * ningún descuento)
	 */
	private BigDecimal orgCharge;
		
	// Constructores
	
	public ReciboDeCliente(Properties ctx, String trxName){
		setCtx(ctx);
		setTrxName(trxName);
		setCredits(new ArrayList<MedioPago>());
		setRetenciones(new ArrayList<RetencionProcessor>());
		setRealInvoices(new ArrayList<Invoice>());
		updateDiscountConfig();
	}
	
	/**
	 * Actualiza la entidad comercial y los datos adicionales
	 * @param bPartnerID
	 */
	public void updateBPartner(Integer bPartnerID){
		MBPartner bp = null;
		// Si no es vacío y no es el mismo al ya configurado
		if(!Util.isEmpty(bPartnerID,true)){
			if ((getBpartner() == null)
					|| (getBpartner() != null && getBpartner().getID() != bPartnerID)) {
				bp = new MBPartner(getCtx(), bPartnerID, getTrxName());
			}
		}
		setBpartner(bp);
		updateBPDiscountSchema();
	}
	
	/**
	 * Actualiza la configuración de descuentos
	 */
	private void updateDiscountConfig(){
		// Obtener una configuración de descuentos principal, que luego se
		// cambiará por la del discount calculator
		setDiscountConfig(MDiscountConfig.get(Env.getAD_Org_ID(getCtx())));
		if(getDiscountConfig() == null){
			setDiscountConfig(MDiscountConfig.get(0));	
		}
	}
	
	/**
	 * Limpia las estructuras locales
	 */
	public void clear(){
		// Lista de facturas reales
		if (getRealInvoices() == null) {
			setRealInvoices(new ArrayList<Invoice>());
		}
		getRealInvoices().clear();
		// Factura global
		setGlobalInvoice(null);
	}
	
	/**
	 * Agregar una factura real a la lista de facturas reales a cobrar 
	 * @param invoice factura
	 */
	public void add(Invoice invoice){
		getRealInvoices().add(invoice);
	}

	/**
	 * Creo una factura {@link Invoice} y le asocio es id parámetro, junto con
	 * el monto ingresado manualmente a cobrar por el operador
	 * 
	 * @param invoiceID
	 *            id de factura
	 * @param manualAmt
	 *            monto manual
	 * @param sincronizeInvoice
	 *            determina si se debe sincronizar la factura con la de la BD
	 */
	public void add(Integer invoiceID, BigDecimal manualAmt, boolean sincronizeInvoice){
		add(invoiceID, manualAmt, BigDecimal.ZERO, sincronizeInvoice);
	}

	/**
	 * Creo una factura {@link Invoice} y le asocio es id parámetro, junto con
	 * el monto ingresado manualmente a cobrar por el operador y el monto del
	 * esquema de vencimientos
	 * 
	 * @param invoiceID
	 *            id de factura
	 * @param manualAmt
	 *            monto manual (incluye monto de esquema de vencimientos)
	 * @param paymentTermDiscount
	 *            descuento de esquema de vencimientos
	 * @param sincronizeInvoice
	 *            determina si se debe sincronizar la factura con la de la BD
	 */
	public void add(Integer invoiceID, BigDecimal manualAmt, BigDecimal paymentTermDiscount, boolean sincronizeInvoice){
		// Creo una factura
		Invoice invoice = new Invoice(getCtx(), getTrxName());
		invoice.setInvoiceID(invoiceID);
		if(sincronizeInvoice){
			invoice.sincronize();
		}
		invoice.setManualAmt(manualAmt);
		invoice.setTotalPaymentTermDiscount(paymentTermDiscount);
		add(invoice);
	}
	
	/**
	 * Crea la factura global que servirá para realizar más simples los cálculos
	 * de descuentos/recargos
	 */
	public void makeGlobalInvoice() {
		// Creo la factura global para luego asignarle toda la info y a las
		// líneas globales
		setGlobalInvoice(new Invoice(getCtx(), getTrxName()));
		// Creo el discount calculator para la factura global
		setDiscountCalculator(DiscountCalculator.create(getGlobalInvoice(),
				MBPartner.DISCOUNTCONTEXT_Receipt));
		// Seteo la fecha actual
		getGlobalInvoice().setDate(new Date());
		// Copiar las líneas de las facturas reales a las líneas de factura global
		InvoiceLine realInvoicelineCopied;
		List<InvoiceLine> globalInvoiceLines = new ArrayList<InvoiceLine>();
		BigDecimal totalAmt = BigDecimal.ZERO;
		BigDecimal totalPTDiscount = BigDecimal.ZERO;
		for (Invoice realInvoice : getRealInvoices()) {
			totalAmt = totalAmt.add(realInvoice.getManualAmt());
			totalPTDiscount = totalPTDiscount.add(realInvoice
					.getTotalPaymentTermDiscount());
			getGlobalInvoice().setManualAmt(totalAmt);
			getGlobalInvoice().setTotalPaymentTermDiscount(totalPTDiscount);
			for (InvoiceLine realInvoiceLine : realInvoice.getLines()) {
				// Clono la línea real 
				try {
					realInvoicelineCopied = (InvoiceLine)realInvoiceLine.clone();				
				} catch (CloneNotSupportedException cnse) {
					cnse.printStackTrace();
					return;
				}
				// A la línea copiada le asocio la factura global como header  
				realInvoicelineCopied.setInvoice(getGlobalInvoice());
				globalInvoiceLines.add(realInvoicelineCopied);
			}
		}
		getGlobalInvoice().setLines(globalInvoiceLines);
		getGlobalInvoice().setBpartnerDiscountSchemaBaseAmt(totalAmt);
		// Descuento de entidad comercial
		updateBPDiscountSchema();
		updateDiscounts();
	}

	/**
	 * Obtengo el descuento desde un plan de entidad financiera
	 * 
	 * @param plan
	 *            plan de entidad financiera
	 * @return esquema de descuento configurado en el plan, null si no existe
	 *         ningún descuento configurado
	 */
	public MDiscountSchema getDiscountFrom(MEntidadFinancieraPlan plan){
		MDiscountSchema schema = null;
		if(plan != null && !Util.isEmpty(plan.getM_DiscountSchema_ID(), true)){
			schema = new MDiscountSchema(getCtx(), plan.getM_DiscountSchema_ID(), getTrxName());
		}
		return schema;
	}

	/**
	 * Obtengo el descuento desde un medio de pago
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 * @return esquema de descuento configurado en el medio de pago, null si no
	 *         existe ningún descuento configurado
	 */
	public MDiscountSchema getDiscountFrom(MPOSPaymentMedium paymentMedium){
		MDiscountSchema schema = null;
		if(paymentMedium != null && !Util.isEmpty(paymentMedium.getM_DiscountSchema_ID(), true)){
			schema = new MDiscountSchema(getCtx(), paymentMedium.getM_DiscountSchema_ID(), getTrxName());
		}
		return schema;
	}

	/**
	 * Obtengo el descuento dependiendo el medio de pago parámetro
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 * @param plan
	 *            plan de entidad financiera
	 * @return esquema de descuento a utilizar adecuado
	 */
	public MDiscountSchema getDiscount(MPOSPaymentMedium paymentMedium, MEntidadFinancieraPlan plan){
		MDiscountSchema schema = null;
		if(paymentMedium != null){
			if(paymentMedium.getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_CreditCard)){
				schema = getDiscountFrom(plan); 
			}
			else{
				schema = getDiscountFrom(paymentMedium);
			}
		}
		return schema;
	}
		
	/**
	 * Actualizo el esquema de descuento de la entidad comercial
	 */
	public void updateBPDiscountSchema(){
		if ((getBpartner() == null)
				|| Util.isEmpty(getBpartner().getM_DiscountSchema_ID(), true)) {
			setbPartnerDiscountSchema(null);
		}
		else{
			// Si cambió el esquema de descuento entonces lo modifico también en el modelo 
			if ((getbPartnerDiscountSchema() == null)
					|| (getbPartnerDiscountSchema().getID() != getBpartner()
							.getM_DiscountSchema_ID())) {
				setbPartnerDiscountSchema(new MDiscountSchema(getCtx(),
						getBpartner().getM_DiscountSchema_ID(), getTrxName()));
			}
			applyBPartnerDiscount();
		}
	}
	
	/**
	 * Aplica el esquema de descuento a las facturas existentes si pueden ser
	 * aplicables 
	 */
	public void applyBPartnerDiscount(){
		if (getDiscountCalculator() != null && getbPartnerDiscountSchema() != null) {
			boolean isApplicable = true;
			// Cargo el descuento de entidad comercial con el monto base
			getDiscountCalculator().loadBPartnerDiscount(
					getbPartnerDiscountSchema(),
					getGlobalInvoice() != null ? getGlobalInvoice()
							.getBpartnerDiscountSchemaBaseAmt()
							: BigDecimal.ZERO, getBpartner().getFlatDiscount(),
					getBpartner().getDiscountContext());
			// Si no es aplicable entonces descargo el descuento de entidad
			// comercial anteriormente cargado
			if (!getDiscountCalculator().isBPartnerDiscountApplicable(
					getbPartnerDiscountSchema().getDiscountContextType(),
					getBpartner().getDiscountContext())) {
				// Cargo el descuento de la entidad comercial
				getDiscountCalculator().loadBPartnerDiscount(null, null, null);
				isApplicable = false;
			}
			else{
				// Aplico los descuentos configurados, entre ellos el de
				// entidad comercial asociado
				getDiscountCalculator().applyDocumentHeaderDiscounts();
			}
			// Si no es aplicable lo saco 
			if(!isApplicable){
				setbPartnerDiscountSchema(null);
			}
		}
	}
	
	/**
	 * Aplicar un descuento general con un monto base
	 * @param baseAmt monto base que ingresa el usuario
	 */
	public void applyGeneralDiscount(BigDecimal baseAmt){
		if (getDiscountCalculator() != null
				&& getDiscountCalculator().isGeneralDocumentDiscountApplicable(
						getCurrentGeneralDiscountSchema()
								.getDiscountContextType())) {
			// Cargo el descuento de documento
			getDiscountCalculator().addGeneralDiscount(getCurrentGeneralDiscountSchema(),
					GeneralDiscountKind.PaymentMedium, baseAmt);
			// Aplico descuentos de documento de cabecera
			getDiscountCalculator().applyDocumentHeaderDiscounts();
		}
		// Actualizo a null al próximo esquema de descuento general ya que se
		// acaba de usar el que existía antes 
		setCurrentGeneralDiscountSchema(null);
	}

	/**
	 * @return El importe total del pedido, incluyendo impuestos y descuentos a
	 *         nivel de documento.
	 */
	public BigDecimal getTotalAmount() {
		return getGlobalInvoice() != null ? getGlobalInvoice()
				.getTotalAmt(true) : BigDecimal.ZERO;
	}
	
	public BigDecimal getBalance() {
		return AmountHelper.scale(getPaidAmount().subtract(getTotalAmount()));
	}
	
	public BigDecimal getPaidAmount() {
		BigDecimal paidAmt = BigDecimal.ZERO;
		// Medios de pago
		for (MedioPago payment : getCredits()) {
			paidAmt = paidAmt.add(payment.getImporte());
		}
		// Retenciones
		for (RetencionProcessor retencion : getRetenciones()) {
			paidAmt = paidAmt.add(retencion.getAmount());
		}
		return AmountHelper.scale(paidAmt);
	}

	/**
	 * @return El importe total neto pagado (sin contemplar
	 *         descuentos/recargos). Este método devuelve un {@link BigDecimal}
	 *         menor o igual que {@link #getOrderProductsTotalAmt()}
	 */
	public BigDecimal getRealPaidAmount() {
		BigDecimal realPaidAmt = BigDecimal.ZERO;
		// Medios de pago
		for (MedioPago payment : getCredits()) {
			realPaidAmt = realPaidAmt.add(payment.getRealAmt());
		}
		// Retenciones
		for (RetencionProcessor retencion : getRetenciones()) {
			realPaidAmt = realPaidAmt.add(retencion.getAmount());
		}
		return realPaidAmt;
	}
	
	/**
	 * @return saldo a pagar sin contemplar descuentos/recargos
	 */
	public BigDecimal getOpenAmt(){
		BigDecimal toPay = BigDecimal.ZERO;
		if (getGlobalInvoice() != null
				&& getBalance().compareTo(BigDecimal.ZERO) <= 0) {
			//toPay = getOrderProductsTotalAmt().subtract(getPaidAmount());
			toPay = getGlobalInvoice().getTotalAmt(false).subtract(
					getRealPaidAmount()).subtract(
					getGlobalInvoice().getTotalBPartnerDiscount());
		}
		return toPay;
	}
	
	/**
	 * @param withDocumentDiscount
	 *            true si se debe contemplar descuentos/recargos a nivel de doc,
	 *            false caso contrario
	 * @return El importe total del recibo (monto manual de la factura global)
	 *         incluyendo impuestos. El parámetro determina si se debe
	 *         decrementar el importe de descuento de documento al total.
	 */
	public BigDecimal getTotalAmt(boolean withDocumentDiscount) {
		return getGlobalInvoice().getTotalAmt(withDocumentDiscount);
	}

	/**
	 * El monto a pagar para este recibo calculado en base al esquema paraḿetro,
	 * es la suma de los montos a pagar de cada factura.
	 * 
	 * @return el monto a pagar para este recibo de cliente aplicado el
	 *         descuento parámetro si es que es posible, sino devuelvo el monto
	 *         abierto de todas las facturas
	 */
	public BigDecimal getToPayAmt(MDiscountSchema discountSchema){
		// Calcula el descuento de documento según el esquema parámetro
		// Se ignora el esquema de EC ya que el mismo ya está (o no) aplicado
		// dentro de cada factura
		BigDecimal discountAmt = BigDecimal.ZERO;
		BigDecimal openAmt = getOpenAmt();
		// Calcula el importe del descuento general a partir de importe
		// pendiente (que incluye los descuentos realizados al documento)
		if (getDiscountCalculator() != null
				&& discountSchema != null
				&& getDiscountCalculator().isGeneralDocumentDiscountApplicable(
						discountSchema.getDiscountContextType())) {
			discountAmt = getDiscountCalculator().calculateDiscount(discountSchema,
					openAmt);
		}
		// Al pendiente se le resta el descuento parámetro y se le
		// quita también el descuento total del pedido ya que ese importe no se
		// debe pagar.
		openAmt = openAmt.subtract(discountAmt);
		return openAmt;
	}

	/**
	 * Calcula el importe real de un pago. Código copiado mayormente desde
	 * {@link Order#addPayment(org.openXpertya.pos.model.Payment)
	 * #calculatePaymentRealAmount(MedioPago)}.
	 * 
	 * @param payment
	 *            Pago
	 */
	private void calculatePaymentRealAmount(MedioPago payment) {
		/*
		 * Determina cual es el importe REAL del pago. El importe real es el
		 * importe que este pago cubre del total. Está aumentado o
		 * decrementado según el descuento que tenga asociado el medio de pago.
		 * Es decir, debemos calcular RP de forma que:
		 * 
		 *   RP = P / (1 - T)
		 * 
		 * Donde: 
		 * - RP: Importe real del pago 
		 * - P: Importe ingresado del pago 
		 * - T: Tasa del descuento aplicado (-1 < T < 1)
		 * 
		 * La tasa no la conocemos ya que un esquema de descuento puede tener
		 * una tasa del 10% pero sea aplicable solo a un subconjunto de las
		 * líneas del pedido, en ese caso la tasa no sería 10% sobre el total.
		 * Es por esto que se aplica el esquema de descuento sobre un importe
		 * constante, y al obtener el importe de descuento podemos obtener
		 * también la tasa de esa aplicación. De esta forma:
		 * 
		 *   T = C / D
		 * 
		 * Donde: 
		 * - C: Importe constante de aplicación (se usará 100) 
		 * - D: Importe del descuento basado en C.
		 */
		
		MPOSPaymentMedium paymentMedium = payment.getPaymentMedium();
		MDiscountSchema discountSchema = getDiscountFrom(paymentMedium);
		BigDecimal currentToPayAmt = getToPayAmt(discountSchema);
		// Obtiene el importe del pago para el cálculo de descuento.
		// Si el pago es menor o igual al pendiente a pagar según el medio de
		// pago, entonces el importe del pago se toma como base para calcular el
		// descuento del medio de pago
		BigDecimal paymentAmt = null;
		if (payment.getImporte().compareTo(currentToPayAmt) <= 0) {
			paymentAmt = payment.getImporte();

		// Si el pago supera el importe pendiente, para no calcular
		// descuentos sobre importes que no se van a cobrar (e.d vueltos o
		// créditos a favor del cliente), el importe base para el descuento
		// es el importe pendiente de pago según el medio de pago indicado.
		} else {
			paymentAmt = currentToPayAmt;
		}
		
		// Previene la división por cero en el cálculo. (solo se puede dar para
		// recálculos de descuentos de líneas).
		if (paymentAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
	
		// Efectúa el cálculo de la fórmula
		final int SCALE           = 20; 
		BigDecimal discount       = BigDecimal.ZERO;     // D
		BigDecimal rate           = BigDecimal.ZERO;     // T
		BigDecimal paymentRealAmt = null;                // RP
		BigDecimal constantAmt    = new BigDecimal(100); // C. Utilizado para calcular T
		
		// Calcula la tasa real de aplicación (puede diferir de la tasa del
		// esquema debido a aplicaciones parciales en las líneas - descuentos
		// selectivos -)
		if (discountSchema != null
				&& getDiscountCalculator().isGeneralDocumentDiscountApplicable(
						discountSchema.getDiscountContextType())) {
			discount = getDiscountCalculator().calculateDiscount(discountSchema, constantAmt);
			rate = discount.divide(constantAmt, SCALE, BigDecimal.ROUND_HALF_EVEN);
		}
		// Calcula el importe real del pago a partir del importe original y la
		// tasa calculada.
		paymentRealAmt = paymentAmt.divide(BigDecimal.ONE.subtract(rate), SCALE,
				BigDecimal.ROUND_HALF_EVEN);
		
		// Obtiene el pendiente y lo compara con el importe real del pago. Si
		// hay una diferencia mínima de centavos agrega esta diferencia al pago
		// real para permitir completar el pedido
		BigDecimal openAmt = getOpenAmt();
		BigDecimal diff = openAmt.subtract(paymentRealAmt);
		if (diff.abs().compareTo(ROUND_TOLERANCE) <= 0) {
			paymentRealAmt = paymentRealAmt.add(diff);
		}
		
		// Guarda el importe real en el pago
		payment.setRealAmt(paymentRealAmt);
	}

	/**
	 * @return el total de los descuentos a nivel de documento de la factura
	 *         global
	 */
	public BigDecimal getTotalDiscountAmt(){
		BigDecimal discount = BigDecimal.ZERO;
		if(getGlobalInvoice() != null){
			discount = getGlobalInvoice().getTotalDocumentDiscount();
		}
		return discount;
	}

	/**
	 * Agrega un medio de pago a la lista de medios de pago y actualizo los
	 * descuentos. Código copiado mayormente desde {@link Order#addPayment(Payment)} si se modifica, verificar.
	 * 
	 * @param mp
	 *            medio de pago
	 */
	public void addMedioPago(MedioPago mp){
		//
		// Actualización de descuentos
		//
		
		// Actualiza el pago para determinar el importe real del mismo.
		calculatePaymentRealAmount(mp);
		
		// Agrega el esquema de descuento del pago como un descuento
		// general para el calculo del descuento total general del pedido.
		// Los descuentos se totalizan segun el medio de pago. Por eso que el ID
		// del descuento dentro del calculador se guarda y obtiene desde el
		// medio de pago.
		Integer discountID = mp.getDiscountInternalID();
		if (getDiscountCalculator().containsDiscount(discountID)) {
			getDiscountCalculator().addDiscountBaseAmount(discountID, mp.getRealAmt());
		} else {
			discountID = getDiscountCalculator().addGeneralDiscount(
					mp.getDiscountSchemaToApply(),
					GeneralDiscountKind.PaymentMedium,
					mp.getRealAmt(),
					mp.getPaymentMedium().getName());
			mp.setDiscountInternalID(discountID);
		}

		// Se aplican los descuentos generales al pedido
		getDiscountCalculator().applyDocumentHeaderDiscounts();
		
		// Se agrega el pago a la lista. Si era un pago existente, anteriormente
		// se había eliminado para el recálculo de descuentos y ahora se vuelve
		// a agregar
		getCredits().add(mp);
	}

	/**
	 * Elimina un medio de pago de la lista y actualiza los descuentos
	 * 
	 * @param mp
	 *            medio de pago a eliminar
	 */
	public void removeMedioPago(MedioPago mp){
		getCredits().remove(mp);
		getDiscountCalculator().subtractDiscountBaseAmount(
				mp.getDiscountInternalID(), mp.getRealAmt());
		updateDiscounts();
	}

	/**
	 * Agrega una retención a la lista de retenciones
	 * 
	 * @param processor
	 *            procesador de retenciones
	 */
	public void addRetencion(RetencionProcessor processor){
		getRetenciones().add(processor);
	} 
	
	/**
	 * Elimina una retención a la lista de retenciones
	 * 
	 * @param processor
	 *            procesador de retenciones
	 */
	public void removeRetencion(RetencionProcessor processor){
		getRetenciones().remove(processor);
		updateDiscounts();
	}

	/**
	 * Agrega un débito a la lista de facturas reales
	 * 
	 * @param invoice
	 *            débito
	 */
	public void addDebit(MInvoice invoice){
		Invoice inv = new Invoice(getCtx(), getTrxName());
		inv.sincronize(invoice);
		add(inv);
	}

	/**
	 * Elimina un débito de la lista
	 * 
	 * @param invoice
	 *            factura real
	 */
	public void removeDebit(MInvoice invoice){
		int totalInvoices = getRealInvoices().size();
		Invoice realInv = null;
		for (int i = 0; i < totalInvoices && realInv == null ; i++) {
			// Si la factura guardada contiene esta factura parámetro
			if(getRealInvoices().get(i).getRealInvoice().equals(invoice)){
				realInv = getRealInvoices().get(i);
			}
		}
		if(realInv != null){
			getRealInvoices().remove(realInv);
		}
	}
	
	/**
	 * Recalcula los descuentos a nivel de documento. 
	 */
	public void updateDiscounts() {
		// Guarda una copia de la lista de pagos actuales y elimina cada pago de
		// la lista de pagos asociados a este recibo
		List<MedioPago> currentCredits = new ArrayList<MedioPago>(getCredits());
		for (MedioPago payment : currentCredits) {
			removeMedioPago(payment);
		}
		// Agrega nuevamente los pagos.
		for (MedioPago payment : currentCredits) {
			addMedioPago(payment);
		}
		// Guarda una copia de la lista de retenciones actuales y elimina cada pago de
		// la lista de pagos asociados a este recibo
		List<RetencionProcessor> currentRetenciones = new ArrayList<RetencionProcessor>(getRetenciones());
		for (RetencionProcessor retencion : currentRetenciones) {
			removeRetencion(retencion);
		}
		// Agrega nuevamente las retenciones
		for (RetencionProcessor retencion : currentRetenciones) {
			addRetencion(retencion);
		}
		// Recalcula descuentos a nivel de documento.
		getDiscountCalculator().applyDocumentHeaderDiscounts();
	}
	
	/**
	 * @param discountTypeContext
	 *            tipo de contexto del esquema de descuento de entidad comercial
	 * @param bpartnerDiscountSchemaContext
	 *            contexto de uso del esquema de descuento de la entidad
	 *            comercial
	 * @return true si es aplicable el descuento de entidad comercial, false
	 *         caso contrario
	 */
	public boolean isBPartnerDiscountApplicable(String discountTypeContext, String bpartnerDiscountSchemaContext){
		boolean applyBPDiscount = false;
		if(getDiscountCalculator() != null){
			applyBPDiscount = getDiscountCalculator()
					.isBPartnerDiscountApplicable(discountTypeContext,
							bpartnerDiscountSchemaContext);
		}
		return applyBPDiscount;
	}

	/**
	 * Determina si es posible aplicar el descuento general
	 * 
	 * @param discountContextType
	 *            Tipo de contexto del esquema de descuento específico
	 * @return true si es posible aplicarlo, false caso contrario
	 */
	public boolean isGeneralDocumentDiscountApplicable(String discountContextType){
		return getDiscountCalculator().isGeneralDocumentDiscountApplicable(discountContextType);
	}

	/**
	 * @return el tipo de documento general configurado para créditos
	 */
	public String getGeneralCreditDocType(){
		return getDiscountCalculator() != null ? getDiscountCalculator()
				.getDiscountConfig().getCreditDocumentType()
				: getDiscountConfig().getCreditDocumentType();
	}
	
	/**
	 * @return el tipo de documento general configurado para débitos
	 */
	public String getGeneralDebitDocType(){
		return getDiscountCalculator() != null ? getDiscountCalculator()
				.getDiscountConfig().getDebitDocumentType()
				: getDiscountConfig().getDebitDocumentType();
	}
	
	/**
	 * @return el tipo de documento configurado para crédito
	 */
	public Integer getCreditDocType(){
		return getDiscountCalculator() != null ? getDiscountCalculator()
				.getDiscountConfig().getCredit_DocType_ID()
				: getDiscountConfig().getCredit_DocType_ID();
	}
	
	/**
	 * @return el tipo de documento configurado para débito
	 */
	public Integer getDebitDocType(){
		return getDiscountCalculator() != null ? getDiscountCalculator()
				.getDiscountConfig().getDebit_DocType_ID()
				: getDiscountConfig().getDebit_DocType_ID();
	}

	/**
	 * @param isCredit
	 *            si estamos con un crédito
	 * @return 1 si el signo para las líneas del comprobante deben ser
	 *         positivas, -1 caso contrario
	 */
	public Integer getGeneralDocTypeSign(boolean isCredit){
		// Obtengo el tipo de documento general
		MDiscountConfig config = getDiscountCalculator() != null ? getDiscountCalculator()
				.getDiscountConfig()
				: getDiscountConfig(); 
		String generalDocType = isCredit ? config.getCreditDocumentType()
				: config.getDebitDocumentType();
		// Obtengo el signo para las líneas del documento
		Integer sign = isCredit ? getGeneralCreditDocTypeSign(generalDocType)
				: getGeneralDebitDocTypeSign(generalDocType);
		return sign;
	}

	/**
	 * Obtengo el artículo de la configuración de descuentos. El artículo se
	 * determina si es a crédito y/o débito verificando el tipo de doc para
	 * descuento o recargo y por el tipo de descuento.
	 * 
	 * @param isCredit
	 *            true si es un crédito, false para débito
	 * @param discountKind
	 *            tipo de descuento: Descuento de Entidad Comercial, Medio de
	 *            pago, esquema de vencimientos, cargos fijos, etc.
	 * @return id del producto configurado para los criterios definidos
	 */
	public Integer getConfigProductID(boolean isCredit, String discountKind){
		Integer productID = null;
		if(isCredit){
			if(discountKind.equals(BPARTNER_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig().getBPartner_DiscountProduct_ID()
						: getDiscountConfig().getBPartner_DiscountProduct_ID();
			}
			else if(discountKind.equals(PAYMENTMEDIUM_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig()
						.getPaymentMedium_DiscountProduct_ID()
						: getDiscountConfig()
								.getPaymentMedium_DiscountProduct_ID();
			}
			else if(discountKind.equals(PAYMENTTERM_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig()
						.getPaymentTerm_DiscountProduct_ID()
						: getDiscountConfig()
								.getPaymentTerm_DiscountProduct_ID();
			}
			else if(discountKind.equals(CHARGE_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig().getCharge_DiscountProduct_ID()
						: getDiscountConfig().getCharge_DiscountProduct_ID();
			}
		}
		else{
			if(discountKind.equals(BPARTNER_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig().getBPartner_SurchargeProduct_ID()
						: getDiscountConfig().getBPartner_SurchargeProduct_ID();
			}
			else if(discountKind.equals(PAYMENTMEDIUM_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig()
						.getPaymentMedium_SurchargeProduct_ID()
						: getDiscountConfig()
								.getPaymentMedium_SurchargeProduct_ID();
			}
			else if(discountKind.equals(PAYMENTTERM_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig()
						.getPaymentTerm_SurchargeProduct_ID()
						: getDiscountConfig()
								.getPaymentTerm_SurchargeProduct_ID();
			}
			else if(discountKind.equals(CHARGE_DISCOUNT)){
				productID = getDiscountCalculator() != null ? getDiscountCalculator()
						.getDiscountConfig().getCharge_SurchargeProduct_ID()
						: getDiscountConfig().getCharge_SurchargeProduct_ID();
			}
		}
		return productID;
	} 
	
	/**
	 * Obtengo el signo para las líneas del documento crédito a crear.
	 * 
	 * @param generalCreditDocumentType
	 *            tipo de documento general para créditos
	 * @return el signo a utilizar para los documentos créditos
	 */
	public Integer getGeneralCreditDocTypeSign(String generalCreditDocumentType){
		Integer sign = 1;
		// Si para crédito se debe crear una factura de cliente, ésta debe ir
		// negativa porque es crédito, no débito 
//		if (generalCreditDocumentType
//				.equals(MDiscountConfig.CREDITDOCUMENTTYPE_Invoice)) {
//			sign = -1;
//		}
		return sign;
	}
	
	/**
	 * Obtengo el signo para las líneas del documento débito a crear.
	 * 
	 * @param generalDebitDocumentType
	 *            tipo de documento general para débitos
	 * @return el signo a utilizar para los documentos débitos
	 */
	public Integer getGeneralDebitDocTypeSign(String generalDebitDocumentType){
		Integer sign = 1;
//		// Si para débito se debe crear un abono de cliente, ésta debe ir
//		// negativa porque es débito, no crédito 
//		if (generalDebitDocumentType
//				.equals(MDiscountConfig.CREDITDOCUMENTTYPE_CreditNote)) {
//			sign = -1;
//		}
		return sign;
	}
	
	/**
	 * @return map con montos agrupados por tipo de descuento
	 */
	public Map<String, BigDecimal> getDiscountsSumPerKind(){
		Map<String, BigDecimal> discountsPerKind = new HashMap<String, BigDecimal>();
		// El monto de descuento de la entidad comercial de la factura global 
		BigDecimal bpDis = getGlobalInvoice().getTotalBPartnerDiscount();
		// El monto de descuento de los medios de pago de la factura global,
		// restando el de la entidad comercial
		BigDecimal pmDis = getGlobalInvoice().getTotalDocumentDiscount().subtract(bpDis);
		// El monto de descuento de esquemas de vencimiento
		BigDecimal ptDis = getGlobalInvoice().getTotalPaymentTermDiscount();
		// Agrego los montos a la map
		discountsPerKind.put(BPARTNER_DISCOUNT, bpDis);
		discountsPerKind.put(PAYMENTMEDIUM_DISCOUNT, pmDis);
		discountsPerKind.put(PAYMENTTERM_DISCOUNT, ptDis);
		discountsPerKind.put(CHARGE_DISCOUNT, getOrgCharge());
		return discountsPerKind;
	}

	/**
	 * @return el monto total a pagar de cada débito teniendo en cuenta los
	 *         descuentos aplicados
	 */
	public BigDecimal getTotalAPagar(){
		BigDecimal total = getGlobalInvoice().getTotalAmt(false);
		// Aplico los descuentos realizados
		total = total.subtract(getGlobalInvoice().getTotalBPartnerDiscount())
				.subtract(getGlobalInvoice().getTotalDocumentDiscount());
		return total;
	}

	/**
	 * Actualizo la configuración de descuentos en base a la organización
	 * 
	 * @param orgID
	 *            id de organización
	 */
	public void updateOrg(Integer orgID){
		if(getDiscountCalculator() != null){
			getDiscountCalculator().setDiscountConfig(orgID);
			updateDiscounts();
		}
		else{
			setDiscountConfig(MDiscountConfig.get(orgID));			
		}
	}
	
	public void setbPartnerDiscountSchema(MDiscountSchema bPartnerDiscountSchema) {
		this.bPartnerDiscountSchema = bPartnerDiscountSchema;
	}

	public MDiscountSchema getbPartnerDiscountSchema() {
		return bPartnerDiscountSchema;
	}

	public void setCurrentGeneralDiscountSchema(
			MDiscountSchema currentGeneralDiscountSchema) {
		this.currentGeneralDiscountSchema = currentGeneralDiscountSchema;
	}

	public MDiscountSchema getCurrentGeneralDiscountSchema() {
		return currentGeneralDiscountSchema;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}

	public MBPartner getBpartner() {
		return bpartner;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setGlobalInvoice(Invoice globalInvoice) {
		this.globalInvoice = globalInvoice;
	}

	public Invoice getGlobalInvoice() {
		return globalInvoice;
	}

	public void setDiscountCalculator(DiscountCalculator discountCalculator) {
		this.discountCalculator = discountCalculator;
	}

	public DiscountCalculator getDiscountCalculator() {
		return discountCalculator;
	}

	public void setRealInvoices(List<Invoice> realInvoices) {
		this.realInvoices = realInvoices;
	}

	public List<Invoice> getRealInvoices() {
		return realInvoices;
	}

	public void setCredits(List<MedioPago> credits) {
		this.credits = credits;
	}

	public List<MedioPago> getCredits() {
		return credits;
	}

	protected void setDiscountConfig(MDiscountConfig discountConfig) {
		this.discountConfig = discountConfig;
	}

	protected MDiscountConfig getDiscountConfig() {
		return discountConfig;
	}

	public void setOrgCharge(BigDecimal orgCharge) {
		this.orgCharge = orgCharge;
	}

	public BigDecimal getOrgCharge() {
		return orgCharge;
	}
	
	/**
	 * @param value
	 *            Indica si el calculador de descuento debe asumir que existe un
	 *            descuento general agregado para calcular los descuento. Esto
	 *            es utilizado para el caso en que la configuración de
	 *            descuentos prioriza los esquemas generales, y actualmente la
	 *            EC tiene un esquema de dto y no se ha agregado ningún medio de
	 *            cobro con descuento. En el momento de seleccionar un medio de
	 *            cobro con descuento se debe anular el descuento de entidad
	 *            comercial.<br>
	 *            Al cambiar este valor se recalcularan los descuentos automáticamente.
	 */
	public void setAssumeGeneralDiscountAdded(boolean value) {
		if(getDiscountCalculator() != null){
			getDiscountCalculator().setAssumeGeneralDiscountAdded(value);
			updateDiscounts();	
		}
	}

	protected void setRetenciones(List<RetencionProcessor> retenciones) {
		this.retenciones = retenciones;
	}

	protected List<RetencionProcessor> getRetenciones() {
		return retenciones;
	}
}
