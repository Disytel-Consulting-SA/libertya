package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Modelo de Cambio de Artículos 
 * 
 * @author Franco Bonafine - Disytel
 */
public class MProductChange extends X_M_ProductChange implements DocAction {

	/** Nombre de la preferencia que guarda la variación de precio permitido para
	 * el cambio de un artículo. El valor de la preferencia debe estar entre 0 y 100.*/
	private static final String PRICE_VARIATION_PREFERENCE = "ProductChangePriceVariation";
	
	/** Tasa de variación de precio permitida para un cambio de artículo (calculada
	 * a partir del valor de la preferencia PRICE_VARIATION_PREFERENCE */
	private BigDecimal priceVariationRate = null;
	/** ID de la tarifa de ventas a utilizar para la validación de variación
	 * de precios permitida */
	private Integer priceListID = null;
	/** ID de la versión de tarifa a utilizar para la validación de variación
	 * de precios permitida. */
	private Integer priceListVersionID = null;
	/** Moneda utilizada para escalar números calculados. Por defecto
	 * la moneda de la compañía */
	private Integer currencyID = null;
	
	/**
	 * Constructor de clase.
	 * @param ctx
	 * @param M_ProductChange_ID
	 * @param trxName
	 */
	public MProductChange(Properties ctx, int M_ProductChange_ID, String trxName) {
		super(ctx, M_ProductChange_ID, trxName);
	}

	/**
	 * Constructor de clase.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MProductChange(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// La cantidad del artículo debe ser mayor que cero
		if (getProductQty().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Quantity"), "0" }));
			return false;
		}
		
		// Los artículo origen y destino deben ser diferentes.
		if (getM_Product_ID() == getM_Product_To_ID()) {
			if(getM_AttributeSetInstance_ID()==0&&getM_AttributeSetInstanceTo_ID()==0){
				log.saveError("SaveError", Msg.translate(getCtx(), "SameSourceTargetProductsError"));
				return false;
			}
			else{
				if(getM_AttributeSetInstance_ID()==getM_AttributeSetInstanceTo_ID()){
					//log.saveError("SaveError", Msg.translate(getCtx(), "SameSourceTargetInstancesProductsError"));
					return false;
				}
			}
		}
		
		return true; 
	}
	
	/**
	 * @return Devuelve el artículo origen del cambio.
	 */
	public MProduct getProduct() {
		MProduct product = null;
		if (getM_Product_ID() > 0) {
			product = MProduct.get(getCtx(), getM_Product_ID());
		}
		return product;
	}
	
	/**
	 * @return Devuelve la instancia origen del cambio.
	 */
	public MAttributeSetInstance getInstance() {
		MAttributeSetInstance instance = null;
		if (getM_AttributeSetInstance_ID() > 0) {
			instance = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(),null);
		}
		return instance;
	}
	
	/**
	 * @return Devuelve el artículo destino del cambio.
	 */
	public MProduct getProductTo() {
		MProduct product = null;
		if (getM_Product_To_ID() > 0) {
			product = MProduct.get(getCtx(), getM_Product_To_ID());
		}
		return product;
	}
	
	/**
	 * @return Devuelve la instancia destino del cambio.
	 */
	public MAttributeSetInstance getInstanceTo() {
		MAttributeSetInstance instanceTo = null;
		if (getM_AttributeSetInstanceTo_ID() > 0) {
			instanceTo = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstanceTo_ID(),null);
		}
		return instanceTo;
	}

	/**
	 * Valida las condiciones de precios requeridas para el cambio de artículos.
	 */
	private boolean validatePrices() {
		// Obtiene y calcula la tasa de variación de precio permitida. Si no
		// es posible el método guarda el mensaje de error apropiado en m_processMsg
		// y este método devuelve falso, indicando que hubo un error en la validación
		// de precios.
		if (!calculatePriceVariationRate()) {
			return false;
		}
		
		// Debe existir la tarifa de ventas para comparar los precios de artículos. 
		if (getPriceListID() == null) {
			m_processMsg = "@ProductChangeSoPriceListRequired@";
			return false;
		}

		// Debe existir una versión válida de la tarifa de ventas.
		if (getPriceListVersionID() == null) {
			m_processMsg = Msg.getMsg(getCtx(), "ProductChangePriceListVersionRequired", 
				new Object[] { 
					MPriceList.get(getCtx(), getPriceListID(), get_TrxName()).getName(),
					getPriceValidationDate().toString().split(" ")[0]
				}
			);
			return false;
		}
		// Obtiene el precio de los artículos origen y destino
		BigDecimal productPrice = getProductPrice(getM_Product_ID());
		BigDecimal productToPrice = getProductPrice(getM_Product_To_ID());
		// Calcula la diferencia permitida para el cambio. Se multiplica la tasa de 
		// variación permitida por el precio de artículo a cambiar.
		// Si el precio del artículo origen es cero, entonces la diferencia permitida
		// también será cero y solo se podrá realizar el cambio de artículo si el precio
		// del artículo destino también es cero.
		BigDecimal allowedDif = productPrice.multiply(getPriceVariationRate());
		// Se obtiene la diferencia de precio real entre los artículos. 
		BigDecimal realDif = productPrice.subtract(productToPrice).abs();
		
		log.fine("Product Price = " + productPrice + ", Product_To Price = " + productToPrice + 
				", Allowed Difference = " + allowedDif + ", Real Difference = " + realDif);
		
		BigDecimal variationPercent = scaleAmount(getPriceVariationRate().multiply(new BigDecimal(100)));
		
		if (realDif.compareTo(allowedDif) > 0) {
			m_processMsg = Msg.getMsg(getCtx(), "NotAllowedPriceDifference", 
				new Object[] { 
					variationPercent,
					productPrice,
					productToPrice,
					scaleAmount(allowedDif),
					scaleAmount(realDif)
				}
			);
			return false;
		}
		
		setProductPrice(productPrice);
		setProductToPrice(productToPrice);
		setMaxPriceVariationPerc(variationPercent);
		
		return true;
	}

	/**
	 * @return Calcula la tasa de variación de precio permitida para un cambio de
	 * artículo. Esta tasa es calculada a partir del valor de la preferencia
	 * indicada por PRICE_VARIATION_PREFERENCE. Si no está configurada esta preferencia
	 * o el valor configurado es erróneo entonces devuelve false y setea en m_processMsg
	 * un mensaje de error apropiado según el caso.
	 * En caso de que la preferencia esté bien configurada devuelve true y guarda en
	 * <code>priceVariationRate</code> el valor de la tasa prmitida, que un valor entre 
	 * 0 y 1 donde 0 implica 0% de tolerancia, y 1 implica 100% de tolerancia.
	 */
	public boolean calculatePriceVariationRate() {
		BigDecimal rate = null;
		// Se obtiene el valor de la preferencia que contiene el % de variación permitida
		// 1) Por Usuario; 2) Por Organización; 3) Por Compañía
		// 1)
		String variation = MPreference.GetCustomPreferenceValue(
				PRICE_VARIATION_PREFERENCE, null, null,
				Env.getAD_User_ID(getCtx()), true);
		// 2) 
		if(Util.isEmpty(variation, true)){
			variation = MPreference.GetCustomPreferenceValue(
					PRICE_VARIATION_PREFERENCE, null, getAD_Org_ID(), null,
					true);
		}
		// 3)
		if(Util.isEmpty(variation, true)){
			variation = MPreference.GetCustomPreferenceValue(
					PRICE_VARIATION_PREFERENCE, getAD_Client_ID(), 0, null,
					true);
		}
		// La preferencia no existe... 
		if (Util.isEmpty(variation, true)) {
			m_processMsg = getPriceVariationErrorMsg("PriceVariationPreferenceNotConfigured");
		// La preferencia existe...
		} else {
			try {
				// Se convierte el valor a BigDecimal y se calcula la tasa dividiendo
				// por 100. La preferencia contiene un valor que no es numérico aquí
				// se genera una NumberFormatException.
				rate = new BigDecimal(variation.trim()).divide(new BigDecimal(100));
				// En este punto la preferencia era válida (valor numérico) y la tasa
				// está calculada. Solo resta validar que sea un valor positivo, si es 
				// negativo entonces se asigna un mensaje apropiado de error y se
				// borra la tasa.
				if (rate.compareTo(BigDecimal.ZERO) < 0) {
					m_processMsg = getPriceVariationErrorMsg("PriceVariationPreferenceUnderZero");
					rate = null;
				}
			} catch (NumberFormatException e) {
				// Si hubo error de formato se asigna el mensaje de error apropiado.
				m_processMsg = getPriceVariationErrorMsg("PriceVariationPreferenceInvalidFormat");
				log.warning("Invalid " + PRICE_VARIATION_PREFERENCE + " preference configured");
			}
		}
		priceVariationRate = rate;
		return rate != null;
	}
	
	/**
	 * Parsea el mensaje de error en la configuración de la variación de precio permitida. 
	 */
	private String getPriceVariationErrorMsg(String sourceMsg) {
		return Msg.getMsg(getCtx(), sourceMsg, new Object[] { PRICE_VARIATION_PREFERENCE });
	}

	/**
	 * @return Devuelve la tasa de variación de precio permitida. Si no está correctamente
	 * configurada devuelve null.
	 */
	public BigDecimal getPriceVariationRate() {
		return priceVariationRate;
	}
	
	/**
	 * @return Devuelve la tarifa de ventas a utilizar para comparar los precios de los
	 * artículos a cambiar.
	 */
	public Integer getPriceListID() {
		if (priceListID == null) {
			String sql =
				// Primero se obtienen todas las tarifas de venta activas de la compañía
				// que a su vez pertenezcan a la organización que realiza el cambio de 
				// artículo. El resultado se ordena por la marca de "por defecto", dando
				// prioridad a las tarifas que están marcadas por defecto.
				"(SELECT M_PriceList_ID " +
				" FROM M_PriceList " +
				" WHERE IsSOPriceList = 'Y' AND IsActive = 'Y' " +
				"   AND AD_Client_ID = ? AND AD_Org_ID = ? " +
				" ORDER BY IsDefault DESC)" +

				" UNION ALL " +
				// Si no existen tarifas para la organización, se agregan al resultado
				// las tarifas que cumplen con la misma condición anterior salvo que
				// no sean de la organización del cambio de artículo, sino que pueden
				// pertenecer a cualqeuir organización. Tener en cuenta que estas tarifas
				// tienen menos prioridad que aquellas que son de la organización
				// de este cambio de artículo.
				"(SELECT M_PriceList_ID " +
				" FROM M_PriceList " +
				" WHERE IsSOPriceList = 'Y' AND IsActive = 'Y' " +
				"   AND AD_Client_ID = ? " +
				" ORDER BY IsDefault DESC)";

			// Se ejecuta la consulta y se asigna el valor de la tarifa, en caso de no
			// existir ninguna tarifa en el resultado priceListID quedará con valor null.
			priceListID = (Integer)DB.getSQLObject(get_TrxName(), sql, new Object[] {
				getAD_Client_ID(), getAD_Org_ID(), getAD_Client_ID()
			});
		}
		return priceListID;
	}
	
	/**
	 * @return Devuelve la versión de tarifa a utilizar para la obtención de los
	 * precios de los artículos. Si no existe una versión valida para la fecha
	 * de comparación, o no existe la tarifa en sí, entonces devuelve null.
	 */
	public Integer getPriceListVersionID() {
		// Debe existir la tarifa de ventas.
		if (priceListVersionID == null && getPriceListID() != null) {
			// Se obtiene la tarifa de ventas y se le pide la versión
			// para la fecha de comparación de precios.
			MPriceList priceList = 
				MPriceList.get(getCtx(), getPriceListID(), get_TrxName());
			MPriceListVersion priceListVersion = priceList.getPriceListVersion(getPriceValidationDate());
			// Si existe una versión retorna el ID de la misma.
			if (priceListVersion != null) {
				priceListVersionID = priceListVersion.getM_PriceList_Version_ID();
			}
		}
		return priceListVersionID;
	}
	
	/**
	 * @return Devuelve la fecha que se utiliza para obtener los precios de los
	 * artículos a cambiar.
	 */
	public Timestamp getPriceValidationDate() {
		// Por le momento se utiliza la fecha actual para obtener los precios de los 
		// artículos. Es posible que sea necesario cambiar esto por la fecha del cambio
		// o tal vez que depende de alguna configuración externa, que indique que fecha
		// utilizar para los precios.
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Devuelve el precio de un artículo utilizando la tarifa y versión
	 * calculada por este cambio de artículo.
	 */
	public BigDecimal getProductPrice(int productID) {
		BigDecimal price = null;
		if (getPriceListVersionID() != null) {
			// Se instancia la clase que permite averiguar el precio de un artículo
			// para una determinada tarifa, versión y fecha.
			MProductPricing pp = new MProductPricing(productID, 0, BigDecimal.ONE, true);
			pp.setM_PriceList_ID(getPriceListID());
			pp.setM_PriceList_Version_ID(getPriceListVersionID());
			pp.setPriceDate(getPriceValidationDate());
			// Obtiene el precio estándar (o de referencia)
			price = pp.getPriceStd();
			// Si es null es porque no tiene precio asignado en la versión, con lo cual
			// se asume que es 0.00
			if (price == null) {
				price = BigDecimal.ZERO;
			}
			log.fine("Product Price: M_Product_ID="+productID + ", Price="+price);
		}
		return price;
	}
	
	/**
	 * Modifica la precisión de un monto según la moneda del sistema.
	 */
	public BigDecimal scaleAmount(BigDecimal amount) {
		return amount.setScale(MCurrency.getStdPrecision(getCtx(), getC_Currency_ID()), BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * @return Devuelve el mensaje de descripción para el inventario
	 * generado a partir de este cambio de artículo.
	 */
	private String getInventoryDescription(boolean productChangeVoid) {
		String msg = "ProdChangeInventoryDescription";
		if (productChangeVoid) {
			msg = "ProdChangeVoidInventoryDescription";
		}
		return Msg.getMsg(getCtx(), msg, 
			new Object[] {
				getDocumentNo()
			}
		);
	}
	
	/**
	 * @return Devuelve el inventario asociado a este
	 * cambio de artículo. Si no se ha generado el inventario devuelve
	 * null.
	 */
	public MInventory getInventory() {
		MInventory inventory = null;
		if (getM_Inventory_ID() > 0) {
			inventory = new MInventory(getCtx(), getM_Inventory_ID(), get_TrxName());
			inventory.setCallerDocument(this);
		}
		return inventory;
	}

	/**
	 * @return Devuelve el inventario físico asociado a este
	 * cambio de artículo generado por la anulación de este documento.
	 * Si este documento no ha sido anulado entonces devuelve null.
	 */
	public MInventory getVoidInventory() {
		MInventory inventory = null;
		if (getVoid_Inventory_ID() > 0) {
			inventory = new MInventory(getCtx(), getVoid_Inventory_ID(), get_TrxName());
			inventory.setCallerDocument(this);
		}
		return inventory;
	}
	
	/**
	 * @return Devuelve el almacén asociado a este cambio de código.
	 */
	public MWarehouse getWarehouse() {
		// No se utiliza el MWarehouse.get(...) ya que es posible que
		// en cada operación de guardado sea necesario recargar el almacén
		// debido a que ciertas validaciones requieren información del mismo
		// y el usuario puede agregar o modificar dicha información.
		return new MWarehouse(getCtx(), getM_Warehouse_ID(), get_TrxName());
	}

	//
	// Implementación de métodos de DocAction
	//

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(action, getDocAction(),log );
	}
	
	@Override
	public boolean voidIt() {
		// Si este cambio de artículo ya fue completado y se creó el inventario 
		// para modificar los stocks de los artículos origen y destino,
		// es necesario entonces crear un nuevo inventario que revierta los cambios
		// de stock realizados por el inventario generado al completar el documento.
		if (getM_Inventory_ID() > 0) {
			try {
				// Crea el inventario indicando que es una anulación.
				createInventory(true);
				// Luego se cierran los inventarios asociados a este documento
				// a fin de que no puedan ser modificados externamente.
				closeInventories();
				
			} catch (Exception e) {
				m_processMsg = e.getMessage();
				return false;
			}
		}
		// Finaliza correctamente la acción
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean closeIt() {
		
		try {
			// Se cierran los inventarios asociados a este documento.
			closeInventories();
			
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
		setDocAction(DOCACTION_None);
		return true;
	}

	/**
	 * Cierra los inventarios asociados a este documento.
	 * @throws Exception si se produce un error al cerrar o guardar
	 * alguno de los inventarios.
	 */
	private void closeInventories() throws Exception {
		// Cierra el inventario original si existe.
		if (getM_Inventory_ID() > 0) {
			closeInventory(getInventory());
		}
		// Cierra el inventario por anulación si existe.
		if (getVoid_Inventory_ID() > 0) {
			closeInventory(getVoidInventory());
		}
	}
	
	/**
	 * Cierra un inventario.
	 * @throws Exception si se produce un error en el procesamiento de la acción
	 * o en el guardado de los cambios.
	 */
	private void closeInventory(MInventory inventory) throws Exception {
		String error = null;
		// Intenta cerrar y guardar el inventario.
		if (!inventory.processIt(MInventory.DOCACTION_Close)) {
			error = inventory.getProcessMsg();
			// En los casos de cierre es posible que processMsg sea null
			// con lo cual en ese caso se asigna un error por defecto
			// para detectar la condición de error.
			if (error == null) {
				error = "...";
			}
		} else if (!inventory.save()) {
			error = CLogger.retrieveErrorAsString();
		}
		if (error != null) {
			throw new Exception("@CloseInventoryError@ (" 
					+ inventory.getDocumentNo() + "): " + error);
		}
	}

	@Override
	public String prepareIt() {
		
		// La UM del artículo origen y destino debe ser la misma.
		if (getProduct().getC_UOM_ID() != getProductTo().getC_UOM_ID()) {
			m_processMsg = Msg.getMsg(getCtx(), "InvalidProductsUOM", 
					new Object[] { MUOM.get(getCtx(), getProduct().getC_UOM_ID()).getName(), MUOM.get(getCtx(), getProductTo().getC_UOM_ID()).getName() });
			return STATUS_Invalid;
		}
		
		// Se valida si el almacén tiene configurado el cargo para la contabilización
		// de las cantidades de este cambio de artículo
		MWarehouse warehouse = getWarehouse();
		if (warehouse.getProductChangeCharge_ID() == 0) {
			m_processMsg = "@ProductChangeChargeRequired@";
			return STATUS_Invalid;
		}
		
		// Valida las condiciones de los precios de los artículos a cambiar.		
		if (!validatePrices()) {
			return STATUS_Invalid;
		}
		
        // Cuando está activado el control de cierres de almacenes se actualiza la 
        // fecha en caso de que la misma sea menor a la fecha actual. Esto es necesario 
        // para que se pueda completar el documento pasando la validación de cierre. 
        // (además es lógico que la fecha real del cambio de artículo sea igual a la fecha 
        // en que se completó el mismo, y no a la fecha en que se creó).
		if (MWarehouseClose.isWarehouseCloseControlActivated()
				&& getDateTrx().compareTo(Env.getDate()) < 0) {
			setDateTrx(Env.getDate());
		}
		
		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	@Override
	public String completeIt() {
		
		try {
			// Crea el inventario que incrementa el stock del artículo
			// origen y decrementa el del artículo destino.
			createInventory(false);
		
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return STATUS_Invalid;
		}
		// Finaliza correctamente la acción
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DOCSTATUS_Completed;
	}
	
	/**
	 * Crea el  inventario que modifica los stocks de los artículos
	 * origen y destino según corresponda.
	 * @throws Exception cuando se produce algún error al crear o procesar el inventario.
	 */
	private void createInventory(boolean productChangeVoid) throws Exception {
		// Se crea el inventario.
		MWarehouse warehouse = getWarehouse();
		MInventory inventory = new MInventory(warehouse);
		inventory.setMovementDate(getDateTrx());
		inventory.setDescription(getInventoryDescription(productChangeVoid));
		// Intenta guardar el inventory, si no es posible se lanza una excepción.
		if (!inventory.save()) {
			throw new Exception("@InventoryCreateError@: " + CLogger.retrieveErrorAsString());
		}
		
		// Crea la línea del inventario que incrementa el stock del artículo original
		// debido a que es el artículo que erróneamente había salido del stock.
		createInventoryLine(
				inventory, 
				getProduct(), 
				getInstance(),
				getM_Locator_ID(), 
				getProductQty(),
				warehouse.getProductChangeCharge_ID(),
				productChangeVoid
		);
		
		// Crea la línea del inventario que decrementa el stock del artículo destino, 
		// debido a que es este artículo el que se compró/vendió efectivamente.
		createInventoryLine(
				inventory, 
				getProductTo(), 
				getInstanceTo(),
				getM_Locator_To_ID(), 
				getProductQty().negate(), 
				warehouse.getProductChangeCharge_ID(),
				productChangeVoid
		);
		
		// Finalmente, se completa el inventario creado. Si se produce algún
		// error al completar o guardar los cambios se levanta una excepción.
		String error = null;
		if (!inventory.processIt(MInventory.DOCACTION_Complete)) {
			error = inventory.getProcessMsg();
		} else if (!inventory.save()) {
			error = CLogger.retrieveErrorAsString();
		}
		if (error != null) {
			throw new Exception("@InventoryCompleteError@: " + error);
		}
		// Por ultimo se asocia el inventario creado a este cambio de artículo dependiendo
		// si es una anulación o no.
		if (productChangeVoid) {
			setVoid_Inventory_ID(inventory.getM_Inventory_ID());
		} else {
			setM_Inventory_ID(inventory.getM_Inventory_ID());
		}
	}
	
	/**
	 * Crea y guarda una línea de un inventario.
	 * @param inventory Invertario contenedor de la línea
	 * @param product Artículo de la línea
	 * @param locatorID Ubicación dentro del almacén
	 * @param qty Cantidad de la línea (si es positiva suma stock, si es
	 * negativa resta stock)
	 * @param chargeID Cargo para contabilización de la línea
	 * @param productChangeVoid Indica si se está anulando este cambio de código, en cuyo
	 * caso invierte los signos de las cantidades para generar líneas inversas a las
	 * líneas creadas para el inventario generado al completar el documento.
	 * @throws Exception Cuando se produce un error en el guardado de la línea.
	 */
	private void createInventoryLine(MInventory inventory, MProduct product, MAttributeSetInstance instance, 
			Integer locatorID, BigDecimal qty, Integer chargeID, boolean productChangeVoid) throws Exception {
		
		// Crea la nueva línea de inventario. Cantidad Contada y del Sistema se asignan
		// a cero porque se utiliza la cantidad interna.
		MInventoryLine inventoryLine = 
				new MInventoryLine(inventory, locatorID, product.getM_Product_ID(), 
								   0, BigDecimal.ZERO, BigDecimal.ZERO);
		
		inventoryLine.setInventoryType(MInventoryLine.INVENTORYTYPE_ChargeAccount);
		inventoryLine.setC_Charge_ID(chargeID);
		try{
			inventoryLine.setM_AttributeSetInstance_ID(instance.getM_AttributeSetInstance_ID());
		}
		catch(NullPointerException ex){
			
		}
		// Por definición en MInventory, si QtyInternalUse es positivo entonces
		// resta el stock, y si es negativo suma. Dado que este método recibe la cantidad
		// en sentido común (positivo suma y negativo resta), aquí se debe invertir el
		// signo de la cantidad para que el inventario se realice correctamente.
		BigDecimal lineQty = qty.negate();
		// A su vez, si se está anulando el cambio de código se invierte el signo
		// de la cantidad para revertir el inventario creado en el momento del completado
		// de este documento.
		if (productChangeVoid) {
			lineQty = lineQty.negate();
		} 
		inventoryLine.setQtyInternalUse(lineQty);
		
		// Intenta guardar la línea, si no es posible se levanta una excepción.
		if (!inventoryLine.save()) {
			throw new Exception(
					"@InventoryLineCreateError@ (" + product.getName() + "): " +
					CLogger.retrieveErrorAsString());
		}
	}
	
	//
	// Métodos de DocAction que no aplican para fraccionamientos
	//

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}

	@Override
	public int getC_Currency_ID() {
		if (currencyID == null) {
			currencyID = Env.getContextAsInt(getCtx(), "$C_Currency_ID");
		}
		return currencyID;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	

}
