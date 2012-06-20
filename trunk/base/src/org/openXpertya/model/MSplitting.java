package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Fraccionamiento de Artículo
 * 
 * @author Franco Bonafine - Disytel
 */
public class MSplitting extends X_M_Splitting implements DocAction {

	/** Cache de líneas del fraccionamiento */
	private List<MSplittingLine> lines = null;
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param M_Splitting_ID
	 * @param trxName
	 */
	public MSplitting(Properties ctx, int M_Splitting_ID, String trxName) {
		super(ctx, M_Splitting_ID, trxName);
		// Valores por defecto para nuevos registros.
		if (M_Splitting_ID == 0) {
			setDateTrx(new Timestamp(System.currentTimeMillis()));
			setProductQty(BigDecimal.ZERO);
			setShrinkQty(BigDecimal.ZERO);
			setSplitQty(BigDecimal.ZERO);
			setConvertedProductQty(BigDecimal.ZERO);
			setConvertedShrinkQty(BigDecimal.ZERO);
			setConvertedSplitQty(BigDecimal.ZERO);
            setDocStatus(DOCSTATUS_InProgress);
            setDocAction(DOCACTION_Complete);
            setProcessed(false);
		}
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MSplitting(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
		
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// No es posible cambiar el almacén si existen líneas debido a que las 
		// mismas referencian ubicaciones del almacén previo.
		if (is_ValueChanged("M_Warehouse_ID") && hasLines()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "CannotChangeSplitWarehouse"));
			return false;
		}
		
		// El artículo debe tener configurado artículos destino de un fraccionamiento.
		if (getProduct().getProductFractions().isEmpty()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitProductNeedFractions"));
			return false;
		}
		
		// El artículo a fraccionar debe tener al menos una conversión de UM
		if (getProduct().getUOMConversions().isEmpty()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitProductNeedConversions"));
			return false;
		}
		
		// La cantidad del artículo debe ser mayor que cero
		if (getProductQty().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Quantity"), "0" }));
			return false;
		}
		
		// No es posible modificar el artículo o la UM de conversión si ya existen
		// líneas creadas para este fraccionamiento.
		if ((is_ValueChanged("M_Product_ID") || is_ValueChanged("C_Conversion_UOM_ID"))
				&& hasLines()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "CannotChangeProductOrUOM"));
			return false;
		}
		
		// Si cambia el artículo, la cantidad la UM  de conversión se recalculan
		// las cantidades del fraccionamiento.
		if (is_ValueChanged("M_Product_ID") || 
				is_ValueChanged("ProductQty") || 
				is_ValueChanged("C_Conversion_UOM_ID")) {
			calculateQuantities();
		}
		
		// Se permite modificar la cantidad del artículo siempre y cuando la cantidad
		// no quede por debajo del total fraccionado según las líneas del fraccionamiento.
		if (is_ValueChanged("ProductQty") 
				&& getConvertedShrinkQty().compareTo(BigDecimal.ZERO) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitQtyUnderLinesQty"));
			return false;
		}
			
		
		return true;
	}
	
	/**
	 * Realiza validaciones sobre una línea del fraccionamiento antes de ser
	 * guardada.
	 * @param line Línea del fraccionamiento 
	 * @return true si la línea no contiene errores, false en caso contrario.
	 * En caso de error se setea en el log el mensaje correspondiente.
	 */
	protected boolean beforeSaveLine(MSplittingLine line) {
		// La cantidad convertida de la línea no puede superar la cantidad convertida
		// disponible para fraccionamiento. En el cálculo de esta cantidad disponible
		// se tiene en cuenta el hecho de que se esté actualizando una línea, ya sea por
		// incremento o decremento de su cantidad.
		BigDecimal convertedAvailableQty = getConvertedAvailableQty(line);
		if (line.getConvertedQty().compareTo(convertedAvailableQty) > 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "SplitQuantityOverflow",
					new Object[] { line.getConvertedQty(), convertedAvailableQty }));
			return false;
		}
		return true;
	}
	
	/**
	 * Actualización de los valores del fraccionamiento. Este método es invocado
	 * luego de que se guarda una línea en este fraccionamiento.
	 */
	protected void update() {
		// Se recalculan las cantidades.
		calculateQuantities();
	}
	
	/**
	 * @return Devuelve el artículo a fraccionar.
	 */
	public MProduct getProduct() {
		return MProduct.get(getCtx(), getM_Product_ID());
	}

	/**
	 * Calcula y asigna las cantidades del fraccionamiento.
	 */
	private void calculateQuantities() {
		// Se calculan la cantidad del artículo, fraccionada y la merma, 
		// ambas en la UM de conversión.
		calculateConvertedSplitQty();
		setConvertedProductQty(convertToConversionUOM(getProductQty()));
		// La merma es igual a la cantidad del artículo menos la cantidad fraccionada.
		setConvertedShrinkQty(getConvertedProductQty().subtract(getConvertedSplitQty()));
		// A partir de las cantidades convertidas, se obtienen las cantidades en la UM
		// del artículo realizando una conversión inversa desde la UM común hacia
		// la UM del artículo.
		setSplitQty(convertToProductUOM(getConvertedSplitQty()));
		setShrinkQty(convertToProductUOM(getConvertedShrinkQty()));
	}
	
	/**
	 * Calcula la cantidad fraccionada convertida a la UM de conversión, a partir
	 * de las cantidades de las líneas del fraccionamiento.
	 */
	private void calculateConvertedSplitQty() {
		BigDecimal convertedSplitQty = BigDecimal.ZERO;
		// La cantidad fraccionada es la suma de las cantidades (ambas convertidas)
		// de cada línea.
		for (MSplittingLine line : getLines(true)) {
			convertedSplitQty = convertedSplitQty.add(line.getConvertedQty());
		}
		setConvertedSplitQty(convertedSplitQty);
	}
	
	/**
	 * Calcula y devuelve la cantidad disponible para fraccionamiento teniendo
	 * en cuenta una determinada línea. La cantidad devuelta va a estar ponderada
	 * por la cantidad de la línea. Si <code>forLine</code> es <code>null</code>
	 * entonces la cantidad disponible es exacamente igual a la cantidad de merma.
	 * @param forLine Línea para la cual que se quiere obtener la cantidad disponible.
	 * Puede ser null.
	 * @return Cantidad disponible según línea o merma si la línea es null.
	 */
	private BigDecimal getConvertedAvailableQty(MSplittingLine forLine) {
		BigDecimal convertedAvailableQty = BigDecimal.ZERO;
		// Si no hay que ignorar ninguna línea del fraccionamiento entonces la
		// cantidad disponible para fraccionar es justamente la cantidad de
		// merma actual.
		if (forLine == null) {
			convertedAvailableQty = getConvertedShrinkQty(); 
		} else {
			// La cantidad disponible para una línea se obtiene sumando el total
			// fraccionado de cada una de las líneas excepto la línea en cuestión.
			// Luego se le resta al total del artículo a fraccionar este total
			// fraccionado sin tener en cuenta la cantidad de la línea.
			// De esta forma, se obtiene la cantidad disponible para esta 
			// ya que se ignora su cantidad como si no existiera, a pesar de 
			// que pueda estar ya persistida en la BD (en ese caso estamos frente
			// a una actualización de la cantidad de la línea).
			BigDecimal convertedSplitQty = BigDecimal.ZERO;
			for (MSplittingLine line : getLines(true)) {
				if (line.getM_SplittingLine_ID() != forLine.getM_SplittingLine_ID()) {
					convertedSplitQty = convertedSplitQty.add(line.getConvertedQty());
				}
			}
			convertedAvailableQty = 
				getConvertedProductQty().subtract(convertedSplitQty);
		}
		return convertedAvailableQty;
	}
	
	/**
	 * Realiza la conversión de una cantidad expresada en la UM del artículo
	 * hacia la UM común de conversión, según las conversiones configuradas
	 * para el artículo a fraccionar.
	 * @param quantity Cantidad a convertir
	 * @return Cantidad convertida
	 */
	private BigDecimal convertToConversionUOM(BigDecimal quantity) {
		return convertToConversionUOM(quantity, getM_Product_ID());
	}

	/**
	 * Realiza la conversión de una cantidad expresada en la UM de conversión
	 * del fraccionamiento hacia la UM del artículo, según las conversiones configuradas
	 * para el artículo a fraccionar.
	 * @param quantity Cantidad a convertir
	 * @return Cantidad convertida
	 */
	private BigDecimal convertToProductUOM(BigDecimal quantity) {
		return convertToProductUOM(quantity, getM_Product_ID());
	}
	
	/**
	 * Realiza la conversión de una cantidad para un artículo determinado hacia
	 * la UM común de conversión del fraccionamiento. Como UM origen se toma la UM
	 * definida para el artículo.
	 * @param quantity Cantidad a Convertir
	 * @param productID ID del artículo cuya cantidad se quiere convertir
	 * @return Cantidad convertida a la UM común de conversión del fraccionamiento
	 */
	public BigDecimal convertToConversionUOM(BigDecimal quantity, Integer productID) {
		BigDecimal convertedQuantity = 
			MUOMConversion.convertProductFrom(
				getCtx(), 
				productID, 
				getC_Conversion_UOM_ID(), 
				quantity
			);
		return convertedQuantity;
	}

	/**
	 * Realiza la conversión de una cantidad expresada en la UM de conversión del 
	 * fraccionamiento hacia la UM del artículo, para un artículo determinado. 
	 * @param quantity Cantidad a Convertir
	 * @param productID ID del artículo cuya cantidad se quiere convertir
	 * @return Cantidad convertida a la UM del artículo.
	 */
	public BigDecimal convertToProductUOM(BigDecimal quantity, Integer productID) {
		BigDecimal convertedQuantity = 
			MUOMConversion.convertProductTo(
				getCtx(), 
				productID, 
				getC_Conversion_UOM_ID(), 
				quantity
			);
		return convertedQuantity;
	}
	
	/**
	 * Devuelve las líneas de este fraccionamiento
	 * @param reload Indica si se deben recargar las líneas desde la BD
	 * @return Lista con las líneas del fraccionamiento. Si no tiene
	 * líneas devuelve una lista cuyo@param reload tamaño es cero.
	 */
	public List<MSplittingLine> getLines(boolean reload) {
		if (lines == null || reload) {
			lines = new ArrayList<MSplittingLine>();
			String sql = "SELECT * FROM M_SplittingLine WHERE M_Splitting_ID = ?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = DB.prepareStatement(sql);
				pstmt.setInt(1, getM_Splitting_ID());
				rs = pstmt.executeQuery();
				MSplittingLine line = null; 
				while (rs.next()) {
					line = new MSplittingLine(getCtx(), rs, get_TrxName());
					lines.add(line);
				}
				
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Get Splitting Lines Error", e);
			} finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (Exception e) {	}
			}
		}
		return lines;
	}
	
	/**
	 * @return Devuelve las líneas del fraccionamiento. NO recarga
	 * la información de la base de datos. Se devuelven las líneas
	 * cacheadas en este objeto.
	 */
	public List<MSplittingLine> getLines() {
		return getLines(false);
	}
	
	/**
	 * @return Indica si este fraccionamiento contiene líneas.
	 */
	public boolean hasLines() {
		int linesCount = DB.getSQLValue(
			get_TrxName(), 
			"SELECT COUNT(*) FROM M_SplittingLine WHERE M_Splitting_ID = ?", 
			getM_Splitting_ID());
		return linesCount > 0;
	}
	
	/**
	 * @return Indica si este fraccionamiento tiene merma.
	 */
	public boolean hasShrink() {
		return getSplitQty().compareTo(BigDecimal.ZERO) > 0;
	}
	
	/**
	 * @return Devuelve el almacén asociado a este fraccionamiento.
	 */
	public MWarehouse getWarehouse() {
		// No se utiliza el MWarehouse.get(...) ya que es posible que
		// en cada operación de guardado sea necesario recargar el almacén
		// debido a que ciertas validaciones requieren información del mismo
		// y el usuario puede agregar o modificar dicha información.
		return new MWarehouse(getCtx(), getM_Warehouse_ID(), get_TrxName());
	}

	/**
	 * @return Devuelve el mensaje de descripción para el inventario
	 * generado a partir de este fraccionamiento.
	 */
	private String getInventoryDescription(boolean splittingVoid) {
		String msg = "SplittingInventoryDescription";
		if (splittingVoid) {
			msg = "SplittingVoidInventoryDescription";
		}
		return Msg.getMsg(getCtx(), msg, 
			new Object[] {
				getDocumentNo(),
				getProduct().getName()
			}
		);
	}
	
	/**
	 * @return Devuelve el inventario generado a partir del completado
	 * de este fraccionamiento. Si aún no se completó el fraccionamiento
	 * devuelve null.
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
	 * @return Devuelve el inventario generado a partir de la anulación
	 * de este fraccionamiento. Si aún no se anuló el fraccionamiento
	 * devuelve null.
	 */
	public MInventory getVoidInventory() {
		MInventory inventory = null;
		if (getVoid_Inventory_ID() > 0) {
			inventory = new MInventory(getCtx(), getVoid_Inventory_ID(), get_TrxName());
			inventory.setCallerDocument(this);
		}
		return inventory;
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
		// Si este fraccionamiento tiene un inventario asociado implica que ya ha
		// sido completado, con lo cual es necesario revertir los cambios en el stock
		// realizados al momento del completa.
		// Si no tiene un inventario, es porque el fraccionamiento está en estado Borrador
		// y aquí solo se anula el documento sin crear ningún inventario. En este caso
		// solo se modifica el estado del documento y no se realiza nada mas dado que no
		// es necesario.
		if (getM_Inventory_ID() > 0) {
			try {
				// Se crea el inventario que revierte los stocks de los artículos involucrados
				// en este fraccionamiento tal como estaban antes de completar el
				// fraccionamiento. Esto se realiza así dado que MInventory no provee la 
				// posibilidad de anulación por si mismo, con lo cual es necesario
				// hacer un inventario nuevo que revierta las cantidades.
				createInventory(true);
				// Luego crear el inventario x anulación se cierran ambos debido 
				// a que la transacción ya no puede contener mas acciones.
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
			// Se cierran los inventarios asociados a este fraccionamiento.
			closeInventories();
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
		setDocAction(DOCACTION_None);
		return true;
	}
	
	/**
	 * Ciera los inventarios asociados a este fraccionamiento.
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
		// Debe existir al menos una línea.
		if (!hasLines()) {
			m_processMsg = "@NoLines@";
			return STATUS_Invalid;
		}
		
		// Total Fraccionado + Merma = Cantidad Producto
		// Si no se cumple esta relación entre las cantidades no es posible completar.
		if (getSplitQty().add(getShrinkQty()).compareTo(getProductQty()) != 0) {
			m_processMsg = "@SplitInvalidQuantities@";
			return STATUS_Invalid;
		}
		
		// Se valida si el almacén tiene configurado los cargos para la contabilización
		// de las cantidades de fraccionamiento y mermas.
		MWarehouse warehouse = getWarehouse();
		if (warehouse.getSplitting_Charge_ID() == 0 ||
				(hasShrink() && warehouse.getShrink_Charge_ID() == 0)) {
			m_processMsg = "@SplittingChargesRequired@";
			return STATUS_Invalid;
		}
		
        // Cuando está activado el control de cierres de almacenes se actualiza la 
        // fecha en caso de que la misma sea menor a la fecha actual. Esto es necesario 
        // para que se pueda completar el documento pasando la validación de cierre. 
        // (además es lógico que la fecha real del fraccionamiento sea igual a la fecha 
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
			// Se crea el inventario que refleja las altas y bajas de los artículos
			// involucrados en este fraccionamiento. En caso de producirse algún
			// error en la creación o procesamiento del inventario se genera una
			// excepción y el proceso de completar el fraccionamiento también
			// devuelve error.
			createInventory(false);
			
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return STATUS_Invalid;
		}
		// Finaliza correctamente la acción
		setProcessed(true);
		setDocAction(DOCACTION_Void);
		return DOCSTATUS_Completed;
	}
	
	/**
	 * Crea el inventario que da de alta / baja los artículos según el fraccionamiento
	 * @param splittingVoid Indica si se está anulando el fraccionamiento. En ese caso, 
	 * se creará un inventario físico que revierta las cantidades de los artículos
	 * tal como estaban antes de crear y completar este fraccionamiento.
	 * @throws Exception Cuando se produce algún error al guardar o procesar el encabezado
	 * o líneas del inventario.
	 */
	private void createInventory(boolean splittingVoid) throws Exception {
		// Se crea el inventario para el almacén del fraccionamiento.
		MWarehouse warehouse = getWarehouse();
		MInventory inventory = new MInventory(warehouse);
		inventory.setMovementDate(getDateTrx());
		inventory.setDescription(getInventoryDescription(splittingVoid));
		// Intenta guardar el inventario, si no es posible se lanza una excepción.
		if (!inventory.save()) {
			throw new Exception("@InventoryCreateError@: " + CLogger.retrieveErrorAsString());
		}
		
		// Crea la línea de inventario que decrementa (completado) o incrementa (anulación) 
		// el stock del producto fraccionado, según la cantidad del total 
		// fraccionado (sin merma).
		// La línea se crea con el cargo de fraccionamientos.
		createInventoryLine(
				inventory, getProduct(), 
				getM_Locator_ID(), getSplitQty().negate(), 
				warehouse.getSplitting_Charge_ID(),
				splittingVoid
		);
		
		// Crea la línea de inventario que representa la merma del artículo fraccionado.
		// Al igual que la línea del artículo fraccionado, la merma se decrementa en caso
		// de estar completando el fraccionamiento, o se incrementa en caso de estar
		// anulando el mismo.
		// La línea se crea con el cargo de mermas.
		createInventoryLine(
				inventory, getProduct(), 
				getM_Locator_ID(), getShrinkQty().negate(), 
				warehouse.getShrink_Charge_ID(),
				splittingVoid
		);
		
		// Crea una línea de inventario por cada línea del fraccionamiento, para 
		// incrementar (completado) o decrementar (anulación)  el stock de los artículos 
		// resultantes.
		// Se asigna el cargo de fraccionamientos a la línea.
		for (MSplittingLine splittingLine : getLines(true)) {
			createInventoryLine(
					inventory, 
					splittingLine.getProductTo(), 
					splittingLine.getM_Locator_ID(), 
					splittingLine.getProductQty(), 
					warehouse.getSplitting_Charge_ID(),
					splittingVoid
			);
		}
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
		// Asocia el inventario creado con este fraccionamiento dependiendo si es 
		// una anulación o no.
		if (!splittingVoid) {
			setM_Inventory_ID(inventory.getM_Inventory_ID());
		} else {
			setVoid_Inventory_ID(inventory.getM_Inventory_ID());
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
	 * @param splittingVoid Indica si se está anulando este fraccionamiento, en cuyo
	 * caso invierte los signos de las cantidades para generar líneas inversas a las
	 * líneas creadas para el inventario generado al completar este fraccionamiento.
	 * @throws Exception Cuando se produce un error en el guardado de la línea.
	 */
	private void createInventoryLine(MInventory inventory, MProduct product, 
			Integer locatorID, BigDecimal qty, Integer chargeID, boolean splittingVoid) throws Exception {

		// Crea la nueva línea de inventario. Cantidad Contada y del Sistema se asignan
		// a cero porque se utiliza la cantidad interna.
		MInventoryLine inventoryLine = 
				new MInventoryLine(inventory, locatorID, product.getM_Product_ID(), 
								   0, BigDecimal.ZERO, BigDecimal.ZERO);
		
		inventoryLine.setInventoryType(MInventoryLine.INVENTORYTYPE_ChargeAccount);
		inventoryLine.setC_Charge_ID(chargeID);
		// Por definición en MInventory, si QtyInternalUse es positivo entonces
		// resta el stock, y si es negativo suma. Dado que este método recibe la cantidad
		// en sentido común (positivo suma y negativo resta), aquí se debe invertir el
		// signo de la cantidad para que el inventario se realice correctamente.
		BigDecimal lineQty = qty.negate();
		// A su vez, si se está anulando el fraccionamiento se invierte el signo
		// de la cantidad para revertir el inventario creado en el momento del completado
		// de este fraccionamiento.
		if (splittingVoid) {
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
		return 0;
	}

	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
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
