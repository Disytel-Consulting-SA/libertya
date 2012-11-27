package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openXpertya.model.FiscalDocumentPrintListener;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MProduct;
import org.openXpertya.pos.exceptions.InsufficientBalanceException;
import org.openXpertya.pos.exceptions.InsufficientCreditException;
import org.openXpertya.pos.exceptions.InvalidOrderException;
import org.openXpertya.pos.exceptions.InvalidPaymentException;
import org.openXpertya.pos.exceptions.InvalidProductException;
import org.openXpertya.pos.exceptions.PosException;
import org.openXpertya.pos.exceptions.ProductAddValidationFailed;
import org.openXpertya.pos.exceptions.UserException;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.CheckPayment;
import org.openXpertya.pos.model.EntidadFinanciera;
import org.openXpertya.pos.model.Location;
import org.openXpertya.pos.model.Order;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.Payment;
import org.openXpertya.pos.model.PaymentMedium;
import org.openXpertya.pos.model.PaymentTerm;
import org.openXpertya.pos.model.PriceList;
import org.openXpertya.pos.model.PriceListVersion;
import org.openXpertya.pos.model.Product;
import org.openXpertya.pos.model.ProductList;
import org.openXpertya.pos.model.Tax;
import org.openXpertya.pos.model.User;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.process.DocActionStatusListener;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.Util;

public class PoSModel {

	/** Pedido del TPV */
	private Order order;
	
	/** Tarifa utilizada para la obtención de precios */
	private PriceList priceList;
	
	/** Versión de tarifa utilizada para la obtención de precios */
	private PriceListVersion priceListVersion;
	
	/** Estado de conexión del TPV: Online/Offline */
	private PoSConnectionState connectionState;
	
	/** Contiene el último pedido de cliente cargado */
	private Order customerOrder = null;
	
	/** Pedidos de clientes agregados al pedido de TPV. La clave contiene el ID
	 * del pedido. */
	private Map<Integer,Order> addedCustomerOrders = null;
	
	/** Lista de medios de pago TPV habilitados */
	private List<PaymentMedium> paymentMediums = null;
	
	/** Lista de Esquemas de Vencimiento */
	private List<PaymentTerm> paymentTerms = null;
	
	/** Cantidad máxima en una línea de pedido */
	private Integer maxOrderLineQty;
	
	private boolean isCopyRep;
	
	public PoSModel() {
		super();
		addedCustomerOrders = new HashMap<Integer, Order>();
		setIntoOnlineMode();
		newOrder();
		getMaxOrderLineQty();
		isCopyRep=true;
	}

	public void completeOrder() throws PosException, InsufficientCreditException, InsufficientBalanceException, InvalidPaymentException, InvalidProductException {
		getConnectionState().completeOrder(getOrder(), getAddedCustomerOrders().keySet());
	}

	public void setIntoOfflineMode() {
		PoSOffline mode = new PoSOffline();
		
		mode.setPoSCOnfig(getConnectionState().getPoSCOnfig());
		
		setConnectionState(mode);
	}
	
	public void setIntoOnlineMode() {
		setConnectionState(new PoSOnline());
	}
	
	public void newOrder() {
		if(getOrder() == null) {
			setOrder(new Order(getConnectionState().getOrganization()));
		}
		
		getOrder().clear();
		isCopyRep=true;
		getAddedCustomerOrders().clear();
	}
	
	/**
	 * @return Devuelve connectionState.
	 */
	public PoSConnectionState getConnectionState() {
		return connectionState;
	}

	/**
	 * @param connectionState Fija o asigna connectionState.
	 */
	public void setConnectionState(PoSConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	/**
	 * @return Devuelve order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order Fija o asigna order.
	 */
	void setOrder(Order order) {
		this.order = order;
	}
	
	public DocActionStatusListener getDocActionStatusListener() {
		return getConnectionState().getDocActionStatusListener();
	}
	
	public void setDocActionStatusListener(DocActionStatusListener dasl) {
		getConnectionState().setDocActionStatusListener(dasl);
	}
	
	public ProductList searchProduct(String code) {
		return getConnectionState().searchProduct(code);
	}
	
	public User searchUser(String name, String password) throws UserException   {
		return getConnectionState().searchUser(name,password);
	}
	
	public User getUser(int userID){
		return getConnectionState().getUser(userID);
	}
	
	public BusinessPartner getBPartner(int bPartnerID) {
		return getConnectionState().getBPartner(bPartnerID);
	}
	
	public List<Location> getBPartnerLocations(int bPartnerID) {
		return getConnectionState().getBPartnerLocations(bPartnerID);
	}
	
	public int getCompanyCurrencyID() {
		return getConnectionState().getClientCurrencyID();
	}
	
	public boolean balanceValidate() {
		return getConnectionState().balanceValidate(getOrder());
	}
	
	public BigDecimal currencyConvert(BigDecimal amount, int fromCurrencyId) {
		return getConnectionState().currencyConvert(amount,fromCurrencyId);
	}
	
	private boolean productStockValidate(Product product, BigDecimal count) {
		boolean valid = true;
		if (!getConnectionState().getPoSCOnfig().isSellWithoutStock())
			valid = getConnectionState().productStockValidate(
					product.getId(), count, product.getAttributeSetInstanceID());
		
		return valid;
	}
	
	public String validateSearchToday(){		
		String where = "";		
		if (getConnectionState().getPoSCOnfig().isSearchToday()==true){
			Date TODAY = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			where = " AND date_trunc('day',C_Order.DateOrdered) = '"+sdf.format(TODAY)+"'::date ";
		}
		return where;
	}
	
	public boolean validateCopyEntity(){
		boolean res=false;
		if (getConnectionState().getPoSCOnfig().isCopyEntity()==true){
			res=true;
		}
		return res;
	}
	
	private OrderProduct createOrderProduct(Product product) {
		Tax productTax;
		if (getOrder().getBusinessPartner() != null)
			productTax = getConnectionState().getProductTax(
					product.getId(), getOrder().getBusinessPartner().getLocationId());
		else
			productTax = getConnectionState().getProductTax(product.getId());
		
		String checkoutPlace;
		// Si el TPV crea el remito entonces la entrega se hace en el TPV
		if (getConfig().isCreateInOut()) {
			checkoutPlace = MProduct.CHECKOUTPLACE_PointOfSale;
		// Si no crea remito, la entrega es siempre por Almacén
		} else {
			checkoutPlace = MProduct.CHECKOUTPLACE_Warehouse; 
		}
		
		OrderProduct op = new OrderProduct(BigDecimal.ZERO,null,productTax,product, checkoutPlace);
		return op;
	}
	
	public int getOrgCityId() {
		return getConnectionState().getOrgCityId();
	}
	
	public List<EntidadFinanciera> getEntidadesFinancieras() {
		return getConnectionState().getEntidadesFinancieras();
	}
	
	public List<PoSConfig> getPoSConfigs() {
		return getConnectionState().getPoSConfigs();
	}
	
	public int getPoSConfigCount() {
		return getPoSConfigs().size();
	}
	
	public void setPoSConfig(PoSConfig poSConfig) {
		getConnectionState().setPoSCOnfig(poSConfig);
		getOrder().setStdPrecision(getPoSConfig().getStdPrecision());
		getOrder().setCostingPresicion(getPoSConfig().getCostingPrecision());
	}
	
	public Product getProduct(int productId, int attributeSetInstanceId){
		return getConnectionState().getProduct(productId, attributeSetInstanceId);
	}
	
	public void reloadPoSConfig(int windowNo) {
		getConnectionState().reloadPoSConfig(windowNo);
		updateInfoPriceList(windowNo);
	}
	
	/**
	 * Carga el encabezado de un pedido de cliente. El pedido luego puede ser accedido
	 * mediante el método {@link #getCustomerOrder()}.
	 * @param orderId ID del pedido a cargar
	 * @return Devuelve el pedido {@link Order} cargado (sin las líneas)
	 * @throws InvalidOrderException cuando el pedido no se puede cargar, ya sea porque no
	 * existe el ID o porque el pedido no cumple con las condiciones necesarias para ser
	 * cargado al pedido TPV.
	 * @throws PosException cuando se produce algún error externo en la carga del pedido.
	 */
	public Order loadCustomerOrder(int orderId) throws InvalidOrderException, PosException {
		// Carga solo el encabezado del pedido de cliente
		setCustomerOrder(getConnectionState().loadOrder(orderId, false));
		return getCustomerOrder();
	}
	
	public int getDefaultBPartner() {
		return getConnectionState().getPoSCOnfig().getBPartnerCashTrxID();
	}
	
	public void validatePoSConfig() throws PosException {
		getConnectionState().validatePoSConfig();
	}
	
	public boolean priceModifyAllowed() {
		return getConnectionState().getPoSCOnfig().isModifyPrice();
	}
	
	public User getCurrentUser() {
		return getConnectionState().getCurrentUser();
	}
	
	public void recalculateOrderTotal() {
		// Se obtiene la localización del cliente.
		int locationID = getOrder().getBusinessPartner().getLocationId();
		// Se recalcula la tasa de impuesto para cada producto del pedido.
		for (OrderProduct orderProduct : getOrder().getOrderProducts()) {
			Tax newTax = getConnectionState().getProductTax(
						orderProduct.getProduct().getId(),
						locationID);
			orderProduct.setTax(newTax);
		}
	}
	
	public void updatePriceList(PriceList priceList, int windowNo){
		getConnectionState().updatePriceList(priceList, windowNo);
		updateInfoPriceList(windowNo);
	}
	
	private void updateInfoPriceList(int windowNo){
		setPriceList(getConnectionState().getCurrentPriceList(windowNo));
		setPriceListVersion(getConnectionState().getCurrentPriceListVersion(getPriceList(), windowNo));
		// Seteo la config porque la lista de precios de la config se usa en muchos lados
		getConnectionState().getPoSCOnfig().setPriceListID(getPriceList().getId());
	}
	
	
	public BigDecimal getProductPrice(Product product,PriceListVersion priceListVersion){
		return getConnectionState().getProductPrice(product,priceListVersion);
	}
	
	public void updateBPartner(int windowNo){
		getConnectionState().updateBPartner(getOrder().getBusinessPartner(),windowNo);
	}
		
	public List<PriceList> getPriceLists(){
		return getConnectionState().getPriceLists();
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceListVersion(PriceListVersion priceListVersion) {
		this.priceListVersion = priceListVersion;
	}

	public PriceListVersion getPriceListVersion() {
		return priceListVersion;
	}
	
	public PoSConfig getConfig() {
		return getConnectionState().getPoSCOnfig();
	}
	
	public boolean isUserCanAccessInfoProduct() {
		return getConfig().isUserCanAccessInfoProduct();
	}
	
	/**
	 * Agrega un artículo al pedido del TPV
	 * @param product Artículo a agregar
	 * @param count Cantidad requerida por el operador
	 * @return el {@link OrderProduct} creado y agregado al pedido
	 * @throws ProductAddValidationFailed cuando la agregación no es posible según las 
	 * condiciones necesarias del artículo.
	 */
	public OrderProduct addOrderProduct(Product product, BigDecimal count) throws ProductAddValidationFailed {
		
		// Validación del lugar de retiro del artículo
		validateProductCheckoutPlace(product);
		
		// Validación de stock de artículo
		if(!productStockValidate(product,count)) {
			throw new ProductAddValidationFailed(product, "InsufficientStockError");
		}
		
		// Validación del conjunto de atriubutos
		if (!product.validateMasi()) {
			throw new ProductAddValidationFailed(product, "MustSetAttrSetInstance", "POSMasiMandatoryError");
		}
		
		// Solo se permiten artículos que tengan la marca de Vendido.
		if (!product.isSold()) {
			throw new ProductAddValidationFailed(product, "POSProductMustBeSold");
		}
		
		// El precio de tarifa del artículo debe ser mayor a cero.
		if (product.getStdPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ProductAddValidationFailed(product, "PriceUnderZero");
		}
		
		// El precio debe ser válido
		BigDecimal scaledprice = getOrder().scalePrice(product.getStdPrice());
		if(scaledprice.compareTo(BigDecimal.ZERO) <= 0){
			throw new ProductAddValidationFailed(product, "InvalidPrice");
		}
		
		// Cantidad debe ser válida
		BigDecimal scaledQty = getOrder().scaleAmount(count);
		if(scaledQty.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ProductAddValidationFailed(product, "InvalidQty");
		}
		
		// El precio final de línea (precio * cantidad) debe ser válido
		BigDecimal scaledTotalLine = getOrder().scaleAmount(scaledprice.multiply(scaledQty));
		if(scaledTotalLine.compareTo(BigDecimal.ZERO) <= 0){
			throw new ProductAddValidationFailed(product, "InvalidFinalPrice");
		}
		
		// La cantidad de la línea no debe superar el máximo configurado
		if(countSurpassMax(count)){
			throw new ProductAddValidationFailed(product, "SurpassMaxOrderLineQty");
		}
		
		// Crea el artículo del pedido con la cantidad indicada y lo agrega al pedido
		// actual del TPV
		OrderProduct newOrderProduct = createOrderProduct(product);
		newOrderProduct.setCount(count);
		getOrder().addOrderProduct(newOrderProduct);

		// Devuelve el artículo del pedido recientemente creado y agregado. 
		return newOrderProduct;
	}
	
	/**
	 * Verifica si el artículo puede ser agregado al pedido del TPV según su lugar de
	 * retiro configurado y la configuración particular para la instancia del TPV.
	 * @param product Artículo que se intenta agregar al pedido
	 * @throws ProductAddValidationFailed cuando no es posible agregar el artículo por
	 * alguna incompatibilidad según su lugar de retiro.
	 */
	private void validateProductCheckoutPlace(Product product) throws ProductAddValidationFailed {
		// El TPV crea el remito y el artículo tiene retiro solo por almacén entonces
		// no es posible agregar manualmente este artículo al pedido de TPV (sí por pedido
		// precargado).
		if (getConfig().isCreateInOut() 
				&& MProduct.CHECKOUTPLACE_Warehouse.equals(product.getCheckoutPlace())) {
			throw new ProductAddValidationFailed(product, "WarehouseCheckoutProductNotAllowed");
		}
		
		// El TPV no hace el remito y el artículo solo se retira por Punto de Venta entonces
		// no es posible agregar el artículo al pedido. En este caso solo se pueden agregar
		// artículos que se retiren por almacén (o ambos retiros).
		if (!getConfig().isCreateInOut()
				&& MProduct.CHECKOUTPLACE_PointOfSale.equals(product.getCheckoutPlace())) {
			throw new ProductAddValidationFailed(product, "POSCheckoutProductNotAllowed");
		}
	}

	/**
	 * @return Devuelve el pedido de cliente cargado
	 */
	public Order getCustomerOrder() {
		return customerOrder;
	}

	/**
	 * @param customerOrder asigna el pedido de cliente cargado
	 */
	private void setCustomerOrder(Order customerOrder) {
		this.customerOrder = customerOrder;
	}
	
	/**
	 * Agrega las líneas del pedido de cliente cargado al pedido del TPV
	 */
	public void addCustomerOrder() throws InvalidOrderException {
		// Se valida que el pedido de cliente no haya sido agregado anteriormente
		// al pedido de TPV.
		if (getAddedCustomerOrders().containsKey(getCustomerOrder().getId())) {
			throw new InvalidOrderException("POSCustomerOrderAlreadyAdded");
		}
		
		// Carga las líneas del pedido del cliente
		getConnectionState().loadOrderLines(getCustomerOrder());
		// Agrega los artículos del pedido de cliente al pedido del TPV
		getOrder().addOrderProductsFrom(getCustomerOrder());
		// Agrega el pedido a la map que contiene los pedidos de clientes actualmente
		// asociados al pedido TPV
		getAddedCustomerOrders().put(getCustomerOrder().getId(), getCustomerOrder());
		//RESP
		//getOrder().setOrderRep(getCustomerOrder().getOrderRep());		
		// Se quita la referencia al pedido de cliente cargado
		clearCustomerOrder();		
	}

	/**
	 * @return Devuelve los pedidos de clientes agregados al pedido del TPV
	 */
	protected Map<Integer,Order> getAddedCustomerOrders() {
		return addedCustomerOrders;
	}
	
	/**
	 * Quita la referencia al pedido de cliente previamente cargado mediante el método
	 * {@link #loadCustomerOrder(int)}
	 */
	public void clearCustomerOrder() {
		setCustomerOrder(null);
	}
	
	/**
	 * Asigna los listener relacionados con la emision de documento mediante un controlador
	 * fiscal.
	 */
	public void setFiscalPrintListeners(
			FiscalDocumentPrintListener fiscalDocumentPrintListener,
			FiscalPrinterEventListener fiscalPrinterEventListener) {
		getConnectionState().setFiscalDocumentPrintListener(fiscalDocumentPrintListener);
		getConnectionState().setFiscalPrinterEventListener(fiscalPrinterEventListener);
	}
	
	/**
	 * Obtiene todos los medios de pago TPV disponibles y guarda una lista con las referencias
	 * a los mismos. Para reflejar cambios que se hagan externamente en los medios de pago
	 * se debe invocar este método nuevamente, sino los medios de pago permanecerán
	 * igual durante toda la ejecución de la ventana del TPV.
	 */
	public void loadPaymentMediums() {
		paymentMediums = getConnectionState().getPaymentMediums();
	}

	/**
	 * @return Devuelve todos los medios de pago disponibles para el TPV. Si aún no
	 * se han cargado los medios de pago se invoca el método {@link #loadPaymentMediums()}
	 * para obtenerlos.
	 */
	public List<PaymentMedium> getPaymentMediums() {
		if (paymentMediums == null) {
			loadPaymentMediums();
		}
		return paymentMediums;
	}
	
	public boolean isCopyRep() {
		return isCopyRep;
	}

	public void setCopyRep(boolean isCopyRep) {
		this.isCopyRep = isCopyRep;
	}
	
	/**
	 * Calcula el importer disponible de un crédito para ser utilizado como medio de pago
	 * @param invoiceID ID del crédito
	 * @return {@link BigDecimal} o <code>null</code> si el pendiente no se puede calcular
	 */
	public BigDecimal getCreditAvailableAmt(int invoiceID) {
		return getConnectionState().getCreditAvailableAmount(invoiceID);
	}

	public void setProcessListener(ASyncProcess listener) {
		getConnectionState().setProcessListener(listener);
	}
	
	/**
	 * Carga los esquemas de vencimiento para luego utilizarlos
	 */
	public void loadPaymentTerms() {
		paymentTerms = getConnectionState().getPaymentTerms();
	}
	
	/**
	 * @return lista de esquemas de vencimiento
	 */
	public List<PaymentTerm> getPaymentTerms() {
		if(paymentTerms == null){
			loadPaymentTerms();
		}
		return getPaymentTerms(null);
	}

	/**
	 * @return lista de esquemas de vencimiento filtrados por el medio de pago
	 *         parámetro
	 */
	public List<PaymentTerm> getPaymentTerms(PaymentMedium paymentMedium) {
		List<PaymentTerm> pts = new ArrayList<PaymentTerm>();
		if(paymentMedium == null){
			return pts;
		}
		if(paymentTerms == null){
			loadPaymentTerms();
		}
		// Si la entidad comercial tiene uno configurado, entonces se agrega
		// sólo ese
		if (getOrder() != null && getOrder().getBusinessPartner() != null
				&& getOrder().getBusinessPartner().getPaymentTerm() != null) {
			pts.add(getOrder().getBusinessPartner().getPaymentTerm());
			return pts;
		}
		// Filtar los esquemas de vencimiento que posean el medio de cobro seleccionado
		for (PaymentTerm paymentTerm : paymentTerms) {
			if(paymentMedium.getId() == paymentTerm.getPosPaymentMediumID()){
				pts.add(paymentTerm);
			}
		}
		
		return pts;
	}
	
	/**
	 * @return el esquema de vencimientos default
	 */
	public PaymentTerm getDefaultInitialPaymentTerm(){
		PaymentTerm pt = null;
		// Primero busco el de la entidad comercial que tenga configurado
		if (getOrder() != null && getOrder().getBusinessPartner() != null
				&& getOrder().getBusinessPartner().getPaymentTerm() != null) {
			pt = getOrder().getBusinessPartner().getPaymentTerm();
		}
		// Luego de la config
		else if(!Util.isEmpty(getConfig().getPaymentTermID(), true)){
			pt = getConnectionState().getPaymentTerm(getConfig().getPaymentTermID());
		}
		return pt;
	}
	
	public boolean isPOSJournalActivated() {
		return MPOSJournal.isActivated();
	}
	
	/**
	 * Anula los documentos generados hasta el momento debido a alguna falla en el proceso.
	 * @throws PosException
	 */
	public void voidDocuments() throws PosException {
		getConnectionState().voidDocuments();
	}
	
	public PoSConfig getPoSConfig() {
		return getConnectionState().getPoSCOnfig();
	}
	
	public boolean isControlCashReturns() {
		return getConnectionState().getPoSCOnfig().isControlCashReturns();
	}
	
	public boolean isCashReturnedSurpassMax(BigDecimal amt){
		return isControlCashReturns()
				&& amt.compareTo(getConnectionState().getPoSCOnfig()
						.getMaxCashReturnWithoutAuth()) > 0;
	}

	public Integer getMaxOrderLineQty() {
		if(maxOrderLineQty == null){
			maxOrderLineQty = getConnectionState().getMaxOrderLineQty();
		}
		return maxOrderLineQty;
	}
	
	/**
	 * @param count cantidad
	 * @return si la cantidad supera el máximo configurado
	 */
	public boolean countSurpassMax(BigDecimal count){
		return getMaxOrderLineQty() != null && getMaxOrderLineQty() < count.intValue();
	}

	/**
	 * @return obtener el próximo nro de factura en caso que se deba crear una
	 *         factura, dependiendo de la configuración de TPV
	 */
	public String getNextInvoiceDocumentNo(){
		return getConnectionState().getNextInvoiceDocumentNo();
	}

	/**
	 * @param paymentMedium
	 *            medio de pago cheque a agregar
	 * @return true si existen todos los cheques con los plazos requeridos por
	 *         el medio de pago parámetro, false caso contrario
	 */
	public boolean existsBeforeCheckDeadLinesFor(PaymentMedium paymentMedium){
		boolean existsAllBefores = true;
		// Armamos una lista con todos los plazos de los cheques existentes actualmente agregados a la compra y luego iteramos por los plazos a verificar por el medio de pago nuevo y validamos que existan todos ellos 
		Set<Integer> deadLinesControl = new HashSet<Integer>();
		List<Payment> payments = getOrder().getPayments();
		// Itero por todos los payments, me quedo con los plazos actuales
		for (Payment payment : payments) {
			// Si el payment es un cheque y su plazo se encuentre dentro de los
			// posibles a verificar, entonces lo agrego a la map y  
			if (payment.isCheckPayment()){
				deadLinesControl.add(((CheckPayment) payment).getCheckDeadLine());				
			}
		}
		// Itero por los plazos a verificar por el medio de pago nuevo y
		// verifico si los tengo a todos ingresados
		for (int i = 0; i < paymentMedium.getBeforeCheckDeadLinesToValidate()
				.size()
				&& (existsAllBefores = deadLinesControl.contains(paymentMedium
						.getBeforeCheckDeadLinesToValidate().get(i))); i++);
		return existsAllBefores;
	}
	
	/**
	 * Cuenta la cantidad de cheques que existen en el pedido agregados con el
	 * mismo plazo que el plazo del pago parámetro. Se incluye a si mismo en
	 * caso que el parámetro includeInCount así lo requiera, sea true.
	 * 
	 * @param checkPayment
	 *            pago parámetro
	 * @param includeInCount
	 *            true si se debe incluir al pago parámetro en la cuenta, false
	 *            caso contrario
	 * @return la cantidad de cheques con el mismo plazo parámetro
	 */
	public int getCheckDeadLineCount(CheckPayment checkPayment, boolean includeInCount){
		// Itero por los pagos y determino la cantidad de cheques que contienen
		// el mismo plazo que el plazo del cheque parámetro, contandose a si
		// mismo o no dependiendo el parámetro de inclusión
		int count = 0;
		for (Payment pay : getOrder().getPayments()) {
			// 1) Si el tipo de pago es del mismo al pago parámetro
			// 2) Si el pago no es el mismo al parámetro ó el pago es el mismo pero
			// de todas formas hay que contarlo como determina el parámetro de
			// inclusión
			// 3) Si el plazo es el mismo al plazo del pago parámetro
			// -> Si ocurren estas condiciones presentadas en los puntos, se suma 1
			// pago a la cuenta, sino 0
			count += pay.getTenderType().equals(checkPayment.getTenderType())
					&& ((pay != checkPayment) || ((pay == checkPayment) && includeInCount))
					&& ((CheckPayment) pay).getCheckDeadLine().equals(
							checkPayment.getCheckDeadLine()) ? 1 : 0;
		}
		return count;
	}

	/**
	 * @param checkPayment
	 *            pago de tipo cheque
	 * @return true si el plazo de este cheque es requerido obligatoriamente por
	 *         otro pago agregado al pedido, false caso contrario
	 */
	public boolean isCheckDeadLineRequired(CheckPayment checkPayment){
		// Itero por los pagos y verifico obligatoriedad de alguno de ellos
		boolean isRequired = false;
		for (int i = 0; i < getOrder().getPayments().size()
				&& !(isRequired = (getOrder().getPayments().get(i)
						.getPaymentMedium().getBeforeCheckDeadLinesToValidate() != null && getOrder()
						.getPayments().get(i).getPaymentMedium()
						.getBeforeCheckDeadLinesToValidate()
						.contains(checkPayment.getCheckDeadLine()))); i++)
			;
		return isRequired;
	}

	/**
	 * @param creditCardStr
	 *            string devuelto por el lector de tarjetas
	 * @return las entidades financieras en las cuales su máscara matchea con el
	 *         string devuelto
	 */
	public List<EntidadFinanciera> getEntidadesFinancieras(String creditCardStr){
		List<EntidadFinanciera> financieras = new ArrayList<EntidadFinanciera>();
		// Itero por las entidades financieras y me quedo con las que su máscara
		// respete el string parámetro devuelto por el lector 
		for (EntidadFinanciera entidadFinanciera : getConnectionState().getEntidadesFinancieras()) {
			// Si la máscara de la entidad financiera responde al string
			// devuelto por el lector entonces lo agrego a la lista 
			if (!Util.isEmpty(entidadFinanciera.getCardMask(), true)
					&& creditCardStr.toUpperCase()
							.startsWith(entidadFinanciera.getCardMask().toUpperCase())) {
				financieras.add(entidadFinanciera);
			}
		}
		return financieras;
	}
	
	public List<Tax> getOtherTaxes(){
		return getConnectionState().getOtherTaxes(
				getOrder().getDiscountableOrderWrapper());
	}
	
	public List<Tax> loadBPOtherTaxes(BusinessPartner bp){
		return getConnectionState().loadBPOtherTaxes(bp);
	}
	
	public boolean isCheckCUITControlActivated(){
		return getConnectionState().isCheckCUITControlActivated();
	}
	
	/**
	 * @param excludeCredits
	 *            true si se deben excluir los créditos agregados actualmente a
	 *            la venta, false caso contrario
	 * @return true si la entidad comercial parámetro posee créditos disponibles
	 *         para utilizar excluyendo los créditos agregados actualmente a la
	 *         venta en caso que así se requiera, false caso contrario
	 */
	public boolean hasCreditNotesAvailables(boolean excludeCredits){
		return getConnectionState().hasCreditNotesAvailables(
				getOrder().getBusinessPartner().getId(), excludeCredits);
	}
	
	
	public boolean hasPaymentsOf(List<String> includedTenderTypes, List<String> excludedTenderTypes){
		// Existe cobro de los tipos de cobro incluídos
		List<String> tenderTypes = new ArrayList<String>();
		List<String> includedTenderTypesAux = new ArrayList<String>();
		List<String> excludedTenderTypesAux = new ArrayList<String>();
		String tenderType;
		for (int i = 0; i < getOrder().getPayments().size(); i++) {
			tenderType = getOrder().getPayments().get(i).getTenderType();
			tenderTypes.add(tenderType);
			if (includedTenderTypes != null
					&& includedTenderTypes.contains(tenderType)) {
				includedTenderTypesAux.add(tenderType);
			}
			if (excludedTenderTypes != null
					&& !excludedTenderTypes.contains(tenderType)) {
				excludedTenderTypesAux.add(tenderType);
			}
		}
		return (includedTenderTypes == null || includedTenderTypesAux.size() == includedTenderTypes
				.size())
				&& (excludedTenderTypes == null || excludedTenderTypesAux
						.size() > 0); 
	}
}
