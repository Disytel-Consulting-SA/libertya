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
import org.openXpertya.util.Util;

/**
 * Transferencia de Mercadería
 * 
 * @author Franco Bonafine - Disytel
 */
public class MTransfer extends X_M_Transfer implements DocAction {

	/** Cache de líneas de la transferencia */
	private List<MTransferLine> lines = null;

	
	// Métodos de clase
	
	/**
	 * Obtengo los documentos vencidos a la fecha parámetro.
	 */
	public static List<MTransfer> getTransfersExpiredFor(Properties ctx, Timestamp dueDate, Integer warehouseID, String movementType, String trxName){
		// Si se buscan transferencias entrantes se debe filtrar por el almacén destino. Si son
		// salientes por el almacén origen.
		String warehouseColumn = 
			(MOVEMENTTYPE_Incoming.equals(movementType) ? "M_WarehouseTo_ID" : "M_Warehouse_ID");
		
		// Buscar 
		String sql = 
			"SELECT *" +
			"FROM M_Transfer " +
			"WHERE ("+ warehouseColumn +" = ?) " +
			  "AND (date_trunc('day', DueDate) <= date_trunc('day',?::timestamp)) " +
			  "AND (MovementType = ?) " +
			  "AND (DocStatus IN ('DR','IP'))";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MTransfer> transfers = new ArrayList<MTransfer>();
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, warehouseID);
			ps.setTimestamp(2, dueDate);
			ps.setString(3, movementType);
			rs = ps.executeQuery();
			while (rs.next()) {
				transfers.add(new MTransfer(ctx, rs, trxName));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}		
		return transfers; 
	}

	/**
	 * Obtener el nro de documento de la transferencia en el caso que exista el
	 * valor pasado como parámetro a la columna parámetro. Además, es posible
	 * filtrar por el registro actual en caso que se requiera
	 * 
	 * @param ctx
	 *            contexto
	 * @param columnName
	 *            nombre de la columna a verificar
	 * @param value
	 *            valor de la columna
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return el nro de documento de la transferencia si es que existe para ese
	 *         valor de columna, null caso contrario
	 */
	public static String getDocNoTransferByStrColumnCondition(Properties ctx, String columnName, String value, Integer transferSelfID, String trxName){
		if(Util.isEmpty(value, true)) return null;
		Object[] transferParams1 = new Object[] { Env.getAD_Org_ID(ctx), value };
		Object[] transferParams2 = new Object[] { Env.getAD_Org_ID(ctx), value, transferSelfID };
		boolean selfEmpty = Util.isEmpty(transferSelfID, true);
		return (String) DB
				.getSQLObject(
						null,
						"SELECT documentno as cant FROM "
								+ Table_Name
								+ " WHERE docstatus IN ('CO','CL') AND ad_org_id = ? AND upper(trim("
								+ columnName + ")) = upper(trim(?))"
								+ (selfEmpty ? "" : " AND m_transfer_id <> ?"),
						selfEmpty ? transferParams1 : transferParams2);
	}
	
	/**
	 * Constructor de la clase.
	 * @param ctx
	 * @param transfer_ID
	 * @param trxName
	 */
	public MTransfer(Properties ctx, int transfer_ID,
			String trxName) {
		super(ctx, transfer_ID, trxName);
		// Valores por defecto
		if (transfer_ID == 0) {
			setMovementType(MOVEMENTTYPE_Outgoing);
			setDocStatus(DOCSTATUS_Drafted);
			setDocAction(DOCACTION_Complete);
		}
	}

	/**
	 * Constructor de la clase.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MTransfer(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		// No es posible cambiar el almacén si existen líneas debido a que las 
		// mismas referencian ubicaciones del almacén previo.
		if (isOutgoing() && is_ValueChanged("M_Warehouse_ID") && hasLines()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "CannotChangeWarehouse"));
			return false;
		}
		
		// Para Transferencias entre Almacenes el almacén origen y destino deben ser 
		// diferentes. Para realizar movimientos de mercadería dentro de un almacén 
		// se utiliza la ventana de Movimiento de Inventario.
		if (isWarehouseTransfer() && (getM_Warehouse_ID() == getM_WarehouseTo_ID())) {
			log.saveError("SaveError", Msg.translate(getCtx(), "MaterialTransferUniqueWarehouseError"));
			return false;
		}
		
		// La fecha de vencimiento debe ser mayor o igual a la fecha de la transferencia
		if (getDueDate().compareTo(getDateTrx()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDueDate"));
			return false;
		}
		
		// En Movimientos en dos Etapas el almacén origen y destino es el mismo siempre.
		if (isTwoPhaseMovement()) {
			setM_WarehouseTo_ID(getM_Warehouse_ID());
		}
		
		// Esto se comenta ya que en un principio se pedía la validación, pero
		// luego se decidió que no se implementa
		// ------------------------------------------------------------------------
//		// Verificar que no exista la misma cadena de guía de transporte, lo
//		// cual no debe permitir guardar
//		String docNo = getDocNoTransferByStrColumnCondition(getCtx(),
//				"Transport_Guide", getTransport_Guide(), getID(), get_TrxName());
//		if(docNo != null){
//			log.saveError("", Msg.getMsg(getCtx(), "TransportGuideWarning",
//					new Object[] { docNo }));
//			return false;
//		}
		// ------------------------------------------------------------------------
		
		
		return true;
	}
	
	@Override
	protected boolean beforeDelete() {
		// Las transferencias entrantes no se puede borrar debido a que se perdería
		// la mercadería habiéndose ya realizado la salida de la misma desde el almacén
		// origen.
		if (isIncoming()) {
			log.saveError("DeleteError", Msg.translate(getCtx(), "IncomingMaterialTransferDeleteNotAllowed"));
			return false;
		}
		return true;
	}

	/**
	 * Devuelve las líneas de esta transferencia
	 * @param reload Indica si se deben recargar las líneas desde la BD
	 * @return Lista con las líneas de la transferencia. Si no tiene
	 * líneas devuelve una lista cuyo tamaño es cero.
	 */
	public List<MTransferLine> getLines(boolean reload) {
		if (lines == null || reload) {
			lines = new ArrayList<MTransferLine>();
			String sql = "SELECT * FROM M_TransferLine WHERE M_Transfer_ID = ?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = DB.prepareStatement(sql);
				pstmt.setInt(1, getM_Transfer_ID());
				rs = pstmt.executeQuery();
				MTransferLine line = null; 
				while (rs.next()) {
					line = new MTransferLine(getCtx(), rs, get_TrxName());
					lines.add(line);
				}
				
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Get Material Transfer Lines Error", e);
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
	 * @return Devuelve las líneas de la transferencia. NO recarga
	 * la información de la base de datos. Se devuelven las líneas
	 * cacheadas en este objeto.
	 */
	public List<MTransferLine> getLines() {
		return getLines(false);
	}
	
	/**
	 * @return Indica si esta transferencia contiene al menos una línea.
	 */
	public boolean hasLines() {
		int linesCount = DB.getSQLValue(
			get_TrxName(), 
			"SELECT COUNT(*) FROM M_TransferLine WHERE M_Transfer_ID = ?", 
			getM_Transfer_ID());
		return linesCount > 0;
	}
	
	/**
	 * @return Devuelve el almacén de origen de la transferencia
	 */
	public MWarehouse getWarehouse() {
		// No se utiliza el MWarehouse.get(...) ya que es posible que
		// en cada operación de guardado sea necesario recargar el almacén
		// debido a que ciertas validaciones requieren información del mismo
		// y el usuario puede agregar o modificar dicha información.
		return new MWarehouse(getCtx(), getM_Warehouse_ID(), get_TrxName());
	}
	
	/**
	 * @return Devuelve el almacén destino de la transferencia
	 */
	public MWarehouse getWarehouseTo() {
		return new MWarehouse(getCtx(), getM_WarehouseTo_ID(), get_TrxName());
	}
	
	/**
	 * @return Devuelve el mensaje de descripción para el inventario
	 * de entrada/salida generado a partir de esta transferencia
	 */
	private String getInventoryDescription() {
		String msg = "MaterialTransferInventoryDescription";
		return Msg.getMsg(getCtx(), msg, 
			new Object[] {
				getDocumentNo()
			}
		);
	}
	
	/**
	 * @return Indica si la transferencia es entrante.
	 */
	public boolean isIncoming() {
		return MOVEMENTTYPE_Incoming.equals(getMovementType());
	}

	/**
	 * @return Indica si la transferencia es saliente.
	 */
	public boolean isOutgoing() {
		return MOVEMENTTYPE_Outgoing.equals(getMovementType());
	}
	
	/**
	 * @return Indica si la transferencia es de tipo "Movimiento en
	 * dos Etapas".
	 */
	public boolean isTwoPhaseMovement() {
		return TRANSFERTYPE_TwoPhaseMovement.equals(getTransferType());
	}
	
	/**
	 * @return Indica si la transferencia es de tipo "Transferencia
	 * entre Almacenes".
	 */
	public boolean isWarehouseTransfer() {
		return TRANSFERTYPE_WarehouseTransfer.equals(getTransferType());
	}
	
	
	/**
	 * @return Devuelve el inventario generado a partir del completado
	 * de esta transferencia. Si aún no se completó este documento
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
	public boolean closeIt() {
		
		try {
			if (getM_Inventory_ID() > 0) {
				// Se cierra el inventario generado para esta transferencia.
				closeInventory(getInventory());
			}
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
		setDocAction(DOCACTION_None);
		return true;
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
		
		// Se controla sólo si es una transferencia saliente
		if(isOutgoing()){
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,
					ModelValidator.TIMING_BEFORE_PREPARE);

	        if( m_processMsg != null ) {
	            return DocAction.STATUS_Invalid;
	        }
		}
				
		// Valida la correctitud de los datos de las líneas.
		if (!validateLines()) {
			return STATUS_Invalid;
		}
		
		// Si la fecha de la transferencia es menor a la fecha actual, se debe actualizar
		// la fecha de la transferencia si:
		// 1) Es una transferencia entrante, o
		// 2) Es una transferencia saliente y está habilitado el control de cierres de
		//    almacenes.
		if (getDateTrx().compareTo(Env.getDate()) < 0
				&& MWarehouseClose.isWarehouseCloseControlActivated()
				&& !MWarehouseClose.existsWarehouseCloseInProgress(getCtx(),
						isOutgoing() ? getM_Warehouse_ID()
								: getM_WarehouseTo_ID(), get_TrxName())) {
			setDateTrx(Env.getDate());
		}

		// Dado que en lógica anterior puede haber cambiado la fecha de transacción, hay que
		// revalidar la fecha de vencimiento de la transferencia.
		if (getDueDate().compareTo(getDateTrx()) < 0) {
			// Si es una transferencia entrante solo se actualiza la fecha de vencimiento
			// a la fecha actual debido a que es un dato informativo (evaluar si tal vez
			// sea conveniente dejar la fecha de vto original quedando anterior a la fecha
			// actual / de transferencia.
			if (isIncoming()) {
				setDueDate(getDateTrx());
			// Si es una transferencia de salida, implica que la transferencia tiene fecha actual
			// y por lo tanto la fecha de vto debe ser mayor o igual que esta fecha. De esta
			// no se permite completar para que el usuario cambie la fecha.	
			} else {
				m_processMsg = "@DueDateBeforeTodayError@";
				return STATUS_Invalid;
			}
		}
		
		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}
	
	/**
	 * Realiza la validación de las líneas de la transferencia, previa a
	 * la operación de completado del documento. Setea el m_ProcessMsg en
	 * caso de que exista algún error.
	 * @return true si las líneas son correctas, false si no.
	 */
	private boolean validateLines() {
		boolean valid = true;
		// Para transferencias entrantes es necesario que el usuario
		// especifique la ubicación destino de cada artículo. Por defecto
		// la transferencia se crea con este dato vacío debido a que es
		// responsabilidad del usuario indicar a donde va a parar la mercadería.
		if (isIncoming()) {
			for (MTransferLine line : getLines()) {
				if (line.getM_Locator_To_ID() == 0) {
					valid = false;
					m_processMsg = "@NeedTargetLocator@";
					break;
				}
			}
		}
		return valid;
	}

	@Override
	public String completeIt() {
		
		try {
			// Se crea el inventario que refleja las altas o bajas de los artículos
			// involucrados en esta transferencia. En caso de producirse algún
			// error en la creación o procesamiento del inventario se genera una
			// excepción y el proceso de completar este documento también
			// devuelve error.
			createInventory();
			
			// Si es una transferencia saliente entonces se crea la transferencia
			// entrante para que sea aceptada y completada luego por el usuario
			// que reciba la mercadería.
			if (isOutgoing()) {
				createIncomingTransfer();
			}
		
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
	 * Crea el inventario que da de alta / baja los artículos de la transferencia según
	 * el tipo de movimiento que se esté realizando (Entrante/Saliente)
	 * @throws Exception Cuando se produce algún error al guardar o procesar el encabezado
	 * o líneas del inventario.
	 */
	public void createInventory() throws Exception {
		// El almacén del inventario depende de si la transferencia es
		// entrante o saliente.
		MWarehouse warehouse = null;
		if (isOutgoing()) {
			warehouse = getWarehouse();
		} else {
			warehouse = getWarehouseTo();
		}
		// Se crea el inventario.
		MInventory inventory = new MInventory(warehouse);
		inventory.setMovementDate(getDateTrx());
		inventory.setDescription(getInventoryDescription());
		// Intenta guardar el inventario, si no es posible se lanza una excepción.
		if (!inventory.save()) {
			throw new Exception("@InventoryCreateError@: " + CLogger.retrieveErrorAsString());
		}
		
		// Crea una línea de inventario por cada línea de la transferencia, para 
		// decrementar (MOVEMENTTYPE_Outgoing) o incrementar (MOVEMENTTYPE_Incoming)
		// el stock de los artículos de las líneas.
		for (MTransferLine line : getLines(true)) {
			createInventoryLine(
					inventory, 
					line.getProduct(), 
					// Si es Saliente implica hacer un egreso del artículo en la ubicación
					// origen. Para esto se hace negativa la cantidad del artículo.
					(isOutgoing() ? line.getM_Locator_ID() : line.getM_Locator_To_ID()), 
					(isOutgoing() ? line.getQty().negate() : line.getQty()), 
					getC_Charge_ID()
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
		// Asocia el inventario creado con esta transferencia.
		setM_Inventory_ID(inventory.getM_Inventory_ID());
	}

	/**
	 * Crea y guarda una línea de un inventario.
	 * @param inventory Invertario contenedor de la línea
	 * @param product Artículo de la línea
	 * @param locatorID Ubicación dentro del almacén
	 * @param qty Cantidad de la línea (si es positiva suma stock, si es
	 * negativa resta stock)
	 * @param chargeID Cargo para contabilización de la línea
	 * @throws Exception Cuando se produce un error en el guardado de la línea.
	 */
	private void createInventoryLine(MInventory inventory, MProduct product, 
			Integer locatorID, BigDecimal qty, Integer chargeID) throws Exception {

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
		inventoryLine.setQtyInternalUse(lineQty);
		
		// Intenta guardar la línea, si no es posible se levanta una excepción.
		if (!inventoryLine.save()) {
			throw new Exception(
					"@InventoryLineCreateError@ (" + product.getName() + "): " +
					CLogger.retrieveErrorAsString());
		}
	}
	
	/**
	 * Crea la transferencia entrante que se corresponde con la transferencia saliente.
	 * La transferencia creada queda en estado borrador para que sea completada 
	 * posteriormente por el usuario que recibe la mercadería.
	 * @throws Exception cuando se produce algún error al guardar el encabezado de la
	 * transferencia o alguna de sus líneas.
	 */
	private void createIncomingTransfer() throws Exception {
		// Crea la transferencia entrante para la organización del almacén
		// destino.
		MTransfer incomingTransfer = new MTransfer(getCtx(), 0, get_TrxName());
		int orgID = getWarehouseTo().getAD_Org_ID();
		// Hace una copia de esta transferencia y luego se corrigen algunas
		// propiedades que difieren.
		PO.copyValues(this, incomingTransfer);
		incomingTransfer.setAD_Org_ID(orgID);
		incomingTransfer.setMovementType(MOVEMENTTYPE_Incoming);
		incomingTransfer.setProcessed(false);
		incomingTransfer.setDocStatus(DOCSTATUS_Drafted);
		incomingTransfer.setDocAction(DOCACTION_Complete);
		incomingTransfer.setM_Inventory_ID(0);
		incomingTransfer.setDocumentNo(getDocumentNo() + "-I");
		// Intenta guardar la transferencia entrante, si no es posible 
		// se levanta una excepción.
		if (!incomingTransfer.save()) {
			throw new Exception(
					"@IncomingTransferCreateError@: " + CLogger.retrieveErrorAsString());
		}
		// Por cada línea de esta transferencia crea una línea igual para
		// la trasnferencia entrante.
		MTransferLine incomingLine = null;
		Integer locatorToID;
		for (MTransferLine outgoingLine : getLines(true)) {
			incomingLine = new MTransferLine(incomingTransfer);
			incomingLine.setLine(outgoingLine.getLine());
			incomingLine.setQty(outgoingLine.getQty());
			// FIXME: Actualmente solo está implementada la funcionalidad para
			// aceptar el total de la mercadería enviada por eso se asigna la 
			// candidad confirmada igual a la cantidad enviada. Esto debe ser
			// cambiado posteriormente debido a que la confirmación de recibo
			// se calculará con los datos de la tabla de detalle de la línea
			// que inidica si hubo mermas, devoluciones, etc.
			incomingLine.setConfirmedQty(outgoingLine.getQty());
			// ---
			incomingLine.setM_Product_ID(outgoingLine.getM_Product_ID());
			incomingLine.setM_Locator_ID(outgoingLine.getM_Locator_ID());
			locatorToID = outgoingLine.getM_Locator_To_ID();
			// Si no existe una ubicación destino, entonces agrego la que se
			// encuentra por defecto del almacén destino 
			if(Util.isEmpty(locatorToID, true)){
				MLocator locator = MLocator.getDefault(getCtx(),
						incomingTransfer.getM_WarehouseTo_ID(), false,
						get_TrxName());
				if(locator != null){
					locatorToID = locator.getID();
				}
			}
			incomingLine.setM_Locator_To_ID(locatorToID);
			// Intenta guardar la línea, si no es posible se levanta una excepción.
			if (!incomingLine.save()) {
				throw new Exception(
						"@IncomingTransferLineCreateError@ (@Line@ # " + incomingLine.getLine() + "): " +
						CLogger.retrieveErrorAsString());
			}
		}
	}
	
	//
	// Métodos de DocAction que no aplican para transferencias entre organizaciones
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

	@Override
	public boolean voidIt() {
		m_processMsg = "@MaterialTransferVoidNotAllowed@";
		return false;
	}

	public void setAD_Org_ID(int AD_Org_ID) {
		super.setAD_Org_ID(AD_Org_ID);
	} // setAD_Org_ID
	
	/**
     * Descripción de Método
     *
     *
     * @param dt
     * @param movementDate
     *
     * @return
     */
    public static MTransfer createTransfer( MOrder order, int M_Warehouse_Origin_ID, int M_Warehouse_Destination_ID, String transferType, String trxName ) {
        MTransfer transfer = new MTransfer(order);
       
        transfer.setDateTrx(Env.getDate());
        transfer.setDueDate(Env.getDate());
        transfer.setTransferType(transferType);
        transfer.setMovementType(MTransfer.MOVEMENTTYPE_Outgoing);
        transfer.setC_Order_ID(order.getC_Order_ID());
        
        /** Almacén Origen */
        transfer.setM_Warehouse_ID(M_Warehouse_Origin_ID);
        /** Almacén Destino */
        transfer.setM_WarehouseTo_ID(M_Warehouse_Destination_ID);
        
        if( transfer.getID() == 0 ) {    // not saved yet
        	transfer.save( trxName );
        }

        MOrderLine[] oLines = order.getLines( true,null );
        for( int i = 0;i < oLines.length;i++ ) {
            MOrderLine oLine = oLines[ i ];
            MTransferLine tLine = new MTransferLine( transfer );

            // Qty = Ordered - Delivered
            BigDecimal MovementQty = oLine.getQtyOrdered().subtract( oLine.getQtyDelivered());

            // Location Origin
            int M_Locator_Origin_ID = MStorage.getM_Locator_ID( M_Warehouse_Origin_ID,oLine.getM_Product_ID(),oLine.getM_AttributeSetInstance_ID(),MovementQty,trxName);
            if( M_Locator_Origin_ID == 0 ) {    // Get default Location
                MWarehouse wh = MWarehouse.get( order.getCtx(),M_Warehouse_Origin_ID);
                M_Locator_Origin_ID = wh.getDefaultLocator().getM_Locator_ID();
            }
            
            // Location Destination
            MWarehouse wh = MWarehouse.get( order.getCtx(),M_Warehouse_Destination_ID);
            int M_Locator_Destination_ID = wh.getDefaultLocator().getM_Locator_ID();

            tLine.setOrderLine(oLine, M_Locator_Origin_ID, M_Locator_Destination_ID, MovementQty);
            tLine.setQty( MovementQty );

            tLine.save( trxName);
        }
        return transfer;
    }
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     * @param C_DocTypeShipment_ID
     * @param movementDate
     */

    public MTransfer( MOrder order) {
        this( order.getCtx(),0,order.get_TrxName());
    }    // MTransfer
}
