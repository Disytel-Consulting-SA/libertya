package org.openXpertya.replication.multiOrg;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.result.DocumentResultBean;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

import ws.libertya.org.LibertyaWSServiceLocator;
import ws.libertya.org.LibertyaWSSoapBindingStub;



public class RemoteOrderLineIntegrityCheck {

	/** Tiempo de timeout de validacion por defecto al procesar */
	public static final int DEFAULT_TIMEOUT_AT_PROCESS_MS = 30 * 1000;
	/** Preferencia: ¿Chequear integridad al procesar? */
	public static final String REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS_PROPERTY 			= "REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS";
	/** Preferencia: ¿timeout al procesar? */
	public static final String REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS_TIMEOUT_PROPERTY 	= "REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS_TIMEOUT";

	/** Tiempo de timeout de validacion por defecto al crear desde */
	public static final int DEFAULT_TIMEOUT_AT_CREATEFROM_MS = 10 * 1000;
	/** Preferencia: ¿Chequear integridad al crear desde? */
	public static final String REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM_PROPERTY 			= "REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM";
	/** Preferencia: ¿timeout al crear desde? */
	public static final String REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM_TIMEOUT_PROPERTY 	= "REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM_TIMEOUT"; 
	
	/** Tipo de Error: Error en la conexion remota */
	public static final int ERROR_TYPE_CONNECTION 	= 1;
	/** Tipo de Error: Error en el proceso de determinacion de valores */
	public static final int ERROR_TYPE_INTERACTION 	= 2;
	/** Tipo de Error: Discrepancia en la validacion de cantidades */
	public static final int ERROR_TYPE_INTEGRITY 	= 3;

	/** Posibles etapas de validacion: durante el crearDesde o bien al procesar */
	public static enum ValidationStage {AT_CREATEFROM, AT_PROCESS};
	
	// Etapa actual de validacion
	public ValidationStage currentStage = null;
	// Contexto
	protected Properties ctx = null;
	// Trx
	protected String trx = null;
	// Pedido sobre el cual verificar
	protected Integer orderID = null;
	// Host remoto
	protected Integer remoteHostOrg = null;
	// Este host
	protected Integer thisHostOrg = null;
	// Nomina de valores qty* de cada linea de pedido previo y posteriormente de las modificaciones   
	protected ArrayList<Data> orderLines = new ArrayList<Data>();
	// Resultado obtenido luego de la invocacion remota 
	protected Result result = new Result();

	
	/** Constructor */
	public RemoteOrderLineIntegrityCheck(Properties ctx, String trx, Integer orderID, ValidationStage stage) {
		this.ctx = ctx;
		this.trx = trx;
		this.orderID = orderID;
		this.currentStage = stage;
		shouldCheckUpdate();
	}
	
	/** Creacion de una nueva linea de pedido a confirmar.  Debe ser invocado ante cada nueva linea de pedido! Retorna el numero de lineas del array de pedidos */
	public int newOrderLine(Integer orderLineID) {
		Data d = new Data(orderLineID);
		orderLines.add(d);
		return orderLines.size();
	}

	/** Incorpora valores (actuales/nuevos) a la orderLine actual. Si current es false, se volcaran a new */
	public void setValuesToOrderLine(int orderLineID, HashMap<String, BigDecimal> values, boolean current) {
		// Si no es una linea de pedido valida, ignorar
		if (orderLineID <= 0)
			return;
		// Recuperar la posicion del orderLineID en el array 
		int pos = findOrderLineID(orderLineID);
		if (pos==-1)
			pos = newOrderLine(orderLineID) - 1;
		// Cargarlo en el array de lineas de pedido
		if (current) {
			orderLines.get(pos).currentValues = values;
		} else {
			orderLines.get(pos).newValues = values;
		}
	}
	
	/** Incorpora valores actuales a la orderLine actual */
	public void setCurrentValuesToOrderLine(int orderLineID, HashMap<String, BigDecimal> values) {
		setValuesToOrderLine(orderLineID, values, true);
	}

	/** Incorpora valores nuevos a la orderLine actual */
	public void setNewValuesToOrderLine(int orderLineID, HashMap<String, BigDecimal> values) {
		setValuesToOrderLine(orderLineID, values, false);
	}

	/** Incorpora valores explicitos a la orderLine actual. Solo incluye los que no son null.  Si current es true, se interpreta como valores actuales, y si es false como los nuevos valores a asignar */
	public void setValuesToOrderLine(int orderLineID, BigDecimal qtyOrdered, BigDecimal qtyReserved, BigDecimal qtyDelivered, BigDecimal qtyTransferred, BigDecimal qtyInvoiced, BigDecimal qtyReturned, boolean current) {
		HashMap<String, BigDecimal> values = new HashMap<String, BigDecimal>();
		putIfNotNull(values, "qtyOrdered", qtyOrdered);
		putIfNotNull(values, "qtyReserved", qtyReserved);
		putIfNotNull(values, "qtyDelivered", qtyDelivered);
		putIfNotNull(values, "qtyTransferred", qtyTransferred);
		putIfNotNull(values, "qtyInvoiced", qtyInvoiced);
		putIfNotNull(values, "qtyReturned", qtyReturned);
		setValuesToOrderLine(orderLineID, values, current);
	}
	
	/** Incorpora los valores explicitos actuales a la orderLine actual. Solo incluye los que no son null. */
	public void setCurrentValuesToOrderLine(int orderLineID, BigDecimal qtyOrdered, BigDecimal qtyReserved, BigDecimal qtyDelivered, BigDecimal qtyTransferred, BigDecimal qtyInvoiced, BigDecimal qtyReturned) {
		setValuesToOrderLine(orderLineID, qtyOrdered, qtyReserved, qtyDelivered, qtyTransferred, qtyInvoiced, qtyReturned, true);		
	}
	
	/** Incorpora los valores explicitos nuevos a la orderLine actual. Solo incluye los que no son null. */
	public void setNewValuesToOrderLine(int orderLineID, BigDecimal qtyOrdered, BigDecimal qtyReserved, BigDecimal qtyDelivered, BigDecimal qtyTransferred, BigDecimal qtyInvoiced, BigDecimal qtyReturned) {
		setValuesToOrderLine(orderLineID, qtyOrdered, qtyReserved, qtyDelivered, qtyTransferred, qtyInvoiced, qtyReturned, false);		
	}
	
	/** Incorpora todas las lineas de pedido a partir de las lineas de un documento dado (remito / factura) */
	public void setAllValuesFromDocument(String docTableName, int documentID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned, boolean current) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Columnas a recuperar
			String sqlCols =	" SELECT ol.C_OrderLine_ID, ol.retrieveUID, ol.qtyOrdered, ol.qtyReserved, ol.qtyDelivered, ol.qtyTransferred, ol.qtyInvoiced, ol.qtyReturned ";
			// From generico si es desde las lineas de remito o factura 			
			String sqlFrom = 	" FROM C_OrderLine ol " +
								" INNER JOIN " + docTableName + "Line docl ON ol.C_OrderLine_ID = docl.C_OrderLine_ID " +
								" INNER JOIN " + docTableName + " doc ON docl." + docTableName + "_ID = doc." + docTableName + "_ID " + 
								" WHERE doc." + docTableName + "_ID = " + documentID;
			// From especifico si son las lineas de un pedido (todas las lineas)
			if (X_C_Order.Table_Name.equals(docTableName)) 
				sqlFrom =		" FROM C_OrderLine ol WHERE C_Order_ID = " + documentID;
			
			pstmt = DB.prepareStatement(sqlCols + sqlFrom, trx);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				setValuesToOrderLine(	rs.getInt("c_orderline_id"), 
										(setQtyOrdered		? rs.getBigDecimal("qtyordered")	: null),
										(setQtyReserved		? rs.getBigDecimal("qtyreserved")	: null),
										(setQtyDelivered	? rs.getBigDecimal("qtydelivered")	: null),
										(setQtyTransferred	? rs.getBigDecimal("qtytransferred"): null),
										(setQtyInvoiced		? rs.getBigDecimal("qtyinvoiced")	: null),
										(setQtyReturned		? rs.getBigDecimal("qtyreturned")	: null),
										current);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs!=null)
					rs.close();
				if (pstmt!=null)
					pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Incorpora todos los valores actuales de las lineas de pedido a partir de las lineas de un remito dado */
	public void setAllCurrentValuesFromInOut(int inOutID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_M_InOut.Table_Name, inOutID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, true);		
	}
	
	/** Incorpora todos los valores nuevos de las lineas pedido a partir de las lineas de un remito dado */
	public void setAllNewValuesFromInOut(int inOutID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_M_InOut.Table_Name, inOutID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, false);
	}
	
	/** Incorpora todos los valores actuales de las lineas de pedido a partir de las lineas de una factura dada */
	public void setAllCurrentValuesFromInvoice(int invoiceID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_C_Invoice.Table_Name, invoiceID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, true);		
	}
	
	/** Incorpora todos los valores nuevos de las lineas pedido a partir de las lineas de una factura dada */
	public void setAllNewValuesFromInvoice(int invoiceID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_C_Invoice.Table_Name, invoiceID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, false);
	}
	
	/** Incorpora todos los valores actuales de las lineas de un pedido en particular */
	public void setAllCurrentValuesFromOrder(int orderID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_C_Order.Table_Name, orderID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, true);
	}
	
	/** Incorpora todos los valores nuevos de las lineas de un pedido en particular */
	public void setAllNewValuesFromOrder(int orderID, boolean setQtyOrdered, boolean setQtyReserved, boolean setQtyDelivered, boolean setQtyTransferred, boolean setQtyInvoiced, boolean setQtyReturned) {
		setAllValuesFromDocument(X_C_Order.Table_Name, orderID, setQtyOrdered, setQtyReserved, setQtyDelivered, setQtyTransferred, setQtyInvoiced, setQtyReturned, false);
	}
	
	/** Incorpora a la map unicamente si el value no es un valor nulo */
	protected void putIfNotNull(HashMap<String, BigDecimal> map, String key, BigDecimal value) {
		if (value!=null)
			map.put(key, value);
	}
	
	/** 
	 * Realiza la interaccion con el host remoto.  Retorna un Result con el resultado de la informacion 
	 * correspondiente a la validación o bien null si no debía realizarse validación alguna.
	 */
	public Result doCheckUpdate() {
		// No debe realizarse validación alguna? retornar null
		if (!shouldCheckUpdate())
			return null;
		try {
			// Conectar al WS
			LibertyaWSServiceLocator locator = new LibertyaWSServiceLocator();
			MReplicationHost rh = MReplicationHost.getForOrg(getRemoteHostOrg(), null, ctx);
			locator.setLibertyaWSEndpointAddress("http://" + 
													rh.getHostName() + ":" + 	// Ejemplo:  200.54.291.212
													rh.getHostPort() + 			// Ejemplo:  "8080"
													rh.getHostAccessKey());
			ws.libertya.org.LibertyaWS lyws = locator.getLibertyaWS();
			((LibertyaWSSoapBindingStub)lyws).setTimeout(getTimeOutMS()); 

			// Rellenar argumentos e invocar
			DocumentResultBean response = lyws.orderLinesCheckUpdate(loadBean(rh.getUserName(), rh.getPassword(), 0, 0));
			result = loadResponse(response);
		} catch (Exception e) {
			result.error = true;
			result.errorMsg = e.getMessage();
			result.errorType = ERROR_TYPE_CONNECTION;
		}
		return result;
	}
	
	/** Vuelca la informacion cargada en esta instancia en la estructura utilizada por el WS */
	protected DocumentParameterBean loadBean(String user, String pass, int client, int org) {
		// Resolver los UIDs a partir de los IDs
		resolveUIDs();
		// Creacion del argumento para envio al WS
		DocumentParameterBean bean = new DocumentParameterBean(user, pass, client, org);
		// Iterar por las lineas
		for (Data orderLine : orderLines) {
			// Crear nueva linea
			bean.newDocumentLine();
			// Cargar UID
			bean.addColumnToCurrentLine(ReplicationConstants.COLUMN_RETRIEVEUID, orderLine.UID);
			// Cargar current*
			for (String key : orderLine.currentValues.keySet()) {
				bean.addColumnToCurrentLine("current"+key, ""+orderLine.currentValues.get(key));
			}
			// Cargar new*
			for (String key : orderLine.newValues.keySet()) {
				bean.addColumnToCurrentLine("new"+key, ""+orderLine.newValues.get(key));
			}
		}
		return bean;
	}
	
	/** Vuelca la informacion obtenida como respuesta del WS hacia el resultado a devolver */
	protected Result loadResponse(DocumentResultBean response) {
		result.error = response.isError();
		result.errorMsg = response.getErrorMsg();
		result.errorType = (response.getDocumentLines().size() == 0 ? ERROR_TYPE_INTERACTION : ERROR_TYPE_INTEGRITY);
		// Iterar por las lineas de la respuesta
		for (HashMap<String, String> lines : response.getDocumentLines()) {
			Data correction = new Data(getIDFromUID(lines.get(ReplicationConstants.COLUMN_RETRIEVEUID)));
			correction.UID = lines.get(ReplicationConstants.COLUMN_RETRIEVEUID);
			// Recorrer las correcciones que deberan ser realizadas
			for (String corrVal : lines.keySet()) {
				if (ReplicationConstants.COLUMN_RETRIEVEUID.toLowerCase().equals(corrVal.toLowerCase()))
					continue;
				correction.correctValues.put(corrVal, new BigDecimal(lines.get(corrVal)));
			}
			result.detail.add(correction);
		}
		setCorrectionSQL();
		return result;
	}
	
	/** Genera la sentencia de correction SQL para las lineas incorrectas */
	protected void setCorrectionSQL() {
		// Si no hay correcciones a realizar, no hacer nada
		if (result.detail.size()==0)
			return;
		StringBuffer buf = new StringBuffer();		// SQL
		StringBuffer bufCol = new StringBuffer();	// Coloquial
		for (Data data : result.detail) {
			if (data.correctValues.size() == 0) {
				continue;
			}
			buf.append("UPDATE C_OrderLine SET ");
			bufCol.append("\n").append(DB.getSQLValueString(null, "SELECT 'LINEA ' || ol.line || '. ' || p.value || ' (' || p.name || ')' FROM C_OrderLine ol INNER JOIN M_Product p ON ol.M_Product_ID = p.M_Product_ID WHERE ol.C_OrderLine_ID = ?", data.ID)).append(": ");
			for (String key : data.correctValues.keySet()) {
				buf.append(key).append("=").append(data.correctValues.get(key)).append(", ");
				bufCol.append(Msg.translate(ctx, key)).append("=").append(data.correctValues.get(key)).append(", ");
			}
			buf.setLength(buf.length() - 2);
			buf.append(" WHERE retrieveUID = '").append(data.UID).append("';");
			bufCol.setLength(bufCol.length() - 2);
			bufCol.append(".  ");
		}
		result.correctionSQL = buf.toString();
		result.correctionColloquial = bufCol.toString();
	}
	
	/** Retorna el SQL de correccion para las lineas de pedido incorrectas */
	public String getCorrectionSQL() {
		return result.correctionSQL;
	}
	
	/** Retorna el detalle de correcciones de manera coloquial */
	public String getCorrectionColloquial() {
		return result.correctionColloquial;
	}
	
	/** Recupera los retrieveUIDs de las lineas de pedido a partir de su ID*/
	protected void resolveUIDs() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Generar la nomina de IDs a recuperar
			StringBuffer ids = new StringBuffer();
			for (Data orderLine : orderLines) {
				ids.append(orderLine.ID).append(", ");
			}
			ids.setLength(ids.length() - 2);
			// Obtener los UIDs y asignarlos
			pstmt = DB.prepareStatement("SELECT C_OrderLine_ID, retrieveUID FROM C_OrderLine WHERE C_OrderLine_ID IN (" + ids.toString() + ")", trx);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("c_orderline_id");
				String uid = rs.getString("retrieveuid");
				for (Data orderLine : orderLines) {
					if (orderLine.ID == id) {
						orderLine.UID = uid;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs!=null)
					rs.close();
				if (pstmt!=null)
					pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Retorna la posicion de la orderLineID dada en el array de orderLines */
	protected int findOrderLineID(int orderLineID) {
		int i = 0;
		for (Data orderLine : orderLines) {
			if (orderLine.ID == orderLineID) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	/** Retorna los el OrderLineID de una linea de pedido a partir de su UID */
	protected Integer getIDFromUID(String uid) {
		for (Data orderLine : orderLines) {
			if (orderLine.UID.equals(uid)) {
				return orderLine.ID;
			}
		}
		return null;
	}
	
	/** Deben realizarse validaciones remotas? */
	public boolean shouldCheckUpdate() {
		// Unicamente si estamos bajo replicacion y las organizaciones del pedido y local no coinciden, considerar el chequeo de consistencias
		return shouldValidateStage() && (getThisHostOrg() != -1) && (getRemoteHostOrg() != getThisHostOrg());
	}

	/** Retorna la organizacion de este host. Si ya fue invocado, reutiliza el valor de la primera invocacion */
	protected int getThisHostOrg() {
		if (thisHostOrg==null)
			thisHostOrg = DB.getSQLValue(null, "SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thishost = 'Y'");
		return thisHostOrg;
	}
	
	/** Retorna la organizacion del host remoto. Si ya fue invocado, reutiliza el valor de la primera invocacion, lo cual presupone que todas las lineas de pedido son del mismo host remoto */
	protected int getRemoteHostOrg() {
		if (remoteHostOrg==null) {
			remoteHostOrg = DB.getSQLValue(null, "SELECT AD_Org_ID FROM C_Order WHERE C_Order_ID = " + orderID);
		}
		return remoteHostOrg;
	}
	
	/** Retorna el time out previo a cancelar la operacion, paar la etapa actual */
	protected int getTimeOutMS() {
		int retValue = (currentStage == ValidationStage.AT_CREATEFROM ? DEFAULT_TIMEOUT_AT_CREATEFROM_MS : DEFAULT_TIMEOUT_AT_PROCESS_MS);
		try {
			String property = (currentStage == ValidationStage.AT_CREATEFROM ? REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM_TIMEOUT_PROPERTY : REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS_TIMEOUT_PROPERTY);
			String timeOut = MPreference.GetCustomPreferenceValue(property); 
			retValue = Integer.parseInt(timeOut);
		} catch (Exception e) { /* Si no existe o no es parseable, ignorar */}		
		return retValue;
	}
	
	/** Retorna si en la etapa actual hay que validar/recuperar info remota  */
	protected boolean shouldValidateStage() {
		boolean retValue = true;
		try {
			String property = (currentStage == ValidationStage.AT_CREATEFROM ? REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_CREATEFROM_PROPERTY : REMOTE_ORDERLINE_INTEGRITY_CHECK_AT_PROCESS_PROPERTY);
			String value = MPreference.GetCustomPreferenceValue(property);
			if (!Util.isEmpty(value)) {
				retValue = "Y".equalsIgnoreCase(value);
			}
		} catch (Exception e) { /* Si no existe o no es parseable, ignorar */}		
		return retValue;
	}
	
		
	/**
	 * 	Informacion de entrada
	 */
	public class Data {
		// UID de la linea de pedido
		public String UID = null;
		// ID de la linea de pedido
		public Integer ID = null;
		// Valores previo al cambio
		public HashMap<String, BigDecimal> currentValues = new HashMap<String, BigDecimal>();
		// Valores luego del cambio
		public HashMap<String, BigDecimal> newValues = new HashMap<String, BigDecimal>();
		// Valores correctos de la sucursal origen que deberan corregirse localmente
		public HashMap<String, BigDecimal> correctValues = new HashMap<String, BigDecimal>();
		/** Constructor */
		public Data(Integer id) {
			this.ID = id;
		}
	}
	
	/**
	 * 	Informacion de respuesta
	 */
	public class Result {
		// Se recibio un error?
		public boolean error = false;
		// Tipo de error, si es que hay
		public Integer errorType = null;
		// Mensaje de error, si es que hay
		public String errorMsg = null;
		// Valores actuales reales recibidos que deberan corregirse localmente
		public ArrayList<Data> detail = new ArrayList<Data>();
		// SQL correctivo
		protected String correctionSQL = null;
		// Mensaje de error coloquial
		protected String correctionColloquial = null;
	}

}
