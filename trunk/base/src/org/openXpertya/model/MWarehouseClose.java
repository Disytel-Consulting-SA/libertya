package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MWarehouseClose extends X_M_Warehouse_Close implements DocAction{

	// Constantes
	
	/** Lista con los doc base a verificar el período */
	
	private static final List<String> docBaseTypes = Arrays.asList("MMS","MMI");
	
	// Variables de clase
	
	private static CLogger log = CLogger.getCLogger("MWarehouseClose");
	
	// Constructores
	
	public MWarehouseClose(Properties ctx, int M_Warehouse_Close_ID, String trxName) {
		super(ctx, M_Warehouse_Close_ID, trxName);
	}

	public MWarehouseClose(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	
	// Métodos estáticos
	
	public static MWarehouseClose get(Properties ctx, int warehouseID, Date dateTrx, String trxName){
		// Creo el where y el arreglo de parámetros para buscar el PO
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		if(warehouseID != 0){
			whereClause.append("(m_warehouse_id = ?)");
			params.add(warehouseID);
		}
		if(dateTrx != null){
			if(whereClause.length() > 0){
				whereClause.append(" AND ");
			}
			whereClause.append("(datetrx = ?)");
			params.add(new java.sql.Date(dateTrx.getTime()));
		}
		return (MWarehouseClose) PO.findFirst(ctx, "m_warehouse_close", whereClause.toString(), params.toArray(), null, trxName);		
	}
	
	/**
	 * Verifica si la compañía actual tiene el control de cierre de almacén activado.
	 * @param ctx
	 * @return
	 */
	public static boolean isWarehouseCloseControlActivated(){
		// Debe estar el control del cierre de almacén activado
		// Busco asi la info de la compañía porque la cache no me 
		// actualiza al modificar algun campo de la info, hay que reiniciar
		MClientInfo clientInfo = (MClientInfo)PO.findFirst(Env.getCtx(), "ad_clientinfo", "ad_client_id = ?", new Object[]{Env.getAD_Client_ID(Env.getCtx())}, null, null);
		return clientInfo.isWarehouseCloseControl();
	}
	
	/**
	 * Chequea s un almacén está cerrado para una determinada fecha y tipo de documento base.
	 * @param docBaseType Tipo de documento base a consultar
	 * @param dateTrx Fecha de consulta
	 * @param warehouseID ID del almacén
	 * @return <code>true</code> si el almacén está cerrado para esa fecha y no se pueden
	 * procesaro documentos con el <code>docBaseType</code>, o <code>false</code> en caso
	 * de que el almacén esté abierto.
	 */
	public static boolean isClosed(String docBaseType, Date dateTrx, Integer warehouseID){
		Properties ctx = Env.getCtx();
		// El control de cierre de almacén está activo y el docbase requiere validación
		// de cierre de almacén...
		if (isWarehouseCloseControlActivated() 
				&& docBaseTypes.contains(docBaseType)) {
			// Si no existe almacén, error
			if(warehouseID == null || warehouseID == 0){
				log.severe(Msg.getMsg(ctx, "NotWarehouseForWarehouseClosePeriod"));
				return true;
			}
			// El almacén se considera abierto (para una fecha y docbase) si existe
			// un cierre completado para el día anterior a la fecha y NO existe un
			// cierre completado para la fecha en cuestión.
			
			// Verifica si existe el cierre completado para el día anterior y
			// que haya más de 1 registro en la BD
			if (!existsPreviousDayCloseCompleted(dateTrx, warehouseID, null)) {
				if (getWarehouseCloseCount(warehouseID, null) > 1) {
					log.severe("Need complete previous day close");
					return true;
				}
			}
			// Verifica si existe el cierre completado para la fecha en cuestión.
			MWarehouseClose dateTrxWC = MWarehouseClose.get(ctx, warehouseID, dateTrx, null);
			if(dateTrxWC != null && dateTrxWC.isCompleted()) {
				log.severe(Msg.getMsg(ctx, "ExistsWarehouseCloseCompletedForPeriod"));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifica si existe un Cierre de Almacén completo para un día anterior a la
	 * fecha indicada.
	 * @param ctx Contexto de la aplicación
	 * @param date Fecha origen de la verificación. Se buscará un cierre para 
	 * <code>date - 1</code>.
	 * @param warehouseID Almacén a consultar
	 * @param trxName Transacción utilizada para instanciación de POs.
	 * @return <code>true</code> si existe un cierre en estado completado, <code>false</code>
	 * en caso contrario (no existe la tupla de cierre o existe pero en estado Borrador)
	 */
	public static boolean existsPreviousDayCloseCompleted(Date date, int warehouseID, String trxName) {
		// Obtiene el día anterior a la fecha parámetro
		Calendar previousDayCalendar = Calendar.getInstance();
		previousDayCalendar.setTimeInMillis(date.getTime());
		previousDayCalendar.add(Calendar.DATE, -1);
		Date previousDay = previousDayCalendar.getTime();
		
		// Busca un cierre para el día anterior
		MWarehouseClose previousDayWC = MWarehouseClose.get(Env.getCtx(), warehouseID, previousDay, trxName); 
		// Se genera la condición de retorno. El cierre debe existir y estar en 
		// estado Completado.
		return previousDayWC != null && previousDayWC.isCompleted();
	}
	
	@Override
	protected boolean beforeSave( boolean newRecord ) {
		// Está activado el control del cierre de almacén
		if(!MWarehouseClose.isWarehouseCloseControlActivated()){
			log.saveError("WarehouseCloseControlDisabled", "");
			return false;
		}
		// Verificar si hay una tupla para la misma fecha y el mismo almacén
		MWarehouseClose closure = MWarehouseClose.get(getCtx(), getM_Warehouse_ID(), getDateTrx(), get_TrxName());
		if(closure != null){
			if(newRecord || closure.getID() != getID()){
	            // Hay una tupla para la misma fecha y almacén, por lo tanto no seguir
				log.saveError("WarehouseCloseRepeated", "");
				return false;
			}
        }
        return true;
    }

	/**
	 * @param warehouseID
	 *            id de almacén
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return cantidad de registros para el almacén actual
	 */
	private static int getWarehouseCloseCount(int warehouseID, String trxName){
		return DB.getSQLValue(trxName, 
			"SELECT coalesce(count(*),0) " +
			"FROM m_warehouse_close " +
			"WHERE m_warehouse_id = ?", warehouseID);
	}
		
	// DOC ACTION

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean closeIt() {
		m_processMsg = "@NotAllowedCloseWarehouseClose@";
		return false;
	}

	@Override
	public String completeIt() {
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return DocAction.STATUS_Completed;
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
	public String prepareIt() {
		// Verificar que haya cierre del día anterior completo para este almacén
		if(!existsPreviousDayCloseCompleted(getDateTrx(), getM_Warehouse_ID(), get_TrxName())) {
			// Aquí sabemos que no existe un cierre completado para el día anterior.
			// Además, hay que tener en cuenta que si es el primer cierre que está completando
			// el resultado no es error debido a que el primer cierre siempre
			// hay que dejar completarlo.
			if (getWarehouseCloseCount(getM_Warehouse_ID(), get_TrxName()) > 1) {
				m_processMsg = "@NotExistBeforeWarehouseClose@";
				return DocAction.STATUS_Invalid;
			}
		}
		
		// Verificar que no haya remitos de salida en borrador o en progreso
		List<PO> inouts = PO.find(getCtx(), "m_inout", "(ad_client_id = ?) AND (issotrx = 'Y') AND (m_warehouse_id = ?) AND (docstatus IN ('DR','IP')) AND (date_trunc('day',movementdate) = date_trunc('day',?::date))", new Object[]{getAD_Client_ID(),getM_Warehouse_ID(),getDateTrx()}, null, get_TrxName());
		if(inouts.size() > 0){
			// Existen remitos de salida con esos estados, tirar mensaje de error
			MInOut inoutAux;
			StringBuffer msg = new StringBuffer();
			msg.append(Msg.getMsg(getCtx(), "ExistsShipmentsNotCompleted"));
			msg.append("<ul>");
			for (PO inout : inouts) {
				inoutAux = (MInOut)inout;
				msg.append(Util.getHTMLListElement(inoutAux.getDocumentNo()));
			}
			msg.append("</ul>");
			msg.append(Msg.getMsg(getCtx(), "ManageShipmentsDocs"));
			m_processMsg = msg.toString();
			return DocAction.STATUS_Invalid;
		}
		
		// Error si hay documentos transferencia entre sucursales y 
		// movimientos de dos etapas vencidos de entrada en borrador
		List<MTransfer> expiredIncomingTransfers = MTransfer.getTransfersExpiredFor(getCtx(), getDateTrx(), getM_Warehouse_ID(), MTransfer.MOVEMENTTYPE_Incoming, get_TrxName()); 
		if(!expiredIncomingTransfers.isEmpty()){
			// No está vacía la lista con transferencias de entrada
			StringBuffer msg = new StringBuffer();
			msg.append(Msg.getMsg(getCtx(), "ExistsTransfersNotCompleted"));
			msg.append("<ul>");
			for (MTransfer incomingTransfer : expiredIncomingTransfers) {
				msg.append(Util.getHTMLListElement(incomingTransfer.getDocumentNo()));
			}
			msg.append("</ul>");
			msg.append(Msg.getMsg(getCtx(), "ManageTransfersDocs"));
			m_processMsg = msg.toString();
			return DocAction.STATUS_Invalid;
		}
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
        DocumentEngine engine = new DocumentEngine( this,getDocStatus());
        return engine.processIt(action,getDocAction(),log);
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
		m_processMsg = "@NotAllowedReverseWarehouseClose@";
		return false;
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		m_processMsg = "@NotAllowedVoidWarehouseClose@";
		return false;
	} 
	
	/**
	 * @return Indica si este cierre está en estado Completado.
	 */
	public boolean isCompleted() {
		return DOCSTATUS_Completed.equals(getDocStatus());
	}
}
