package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MUOM;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;



public class CreateFromModel {

	CLogger log = CLogger.getCLogger("CreateFromModel");
	Properties ctx = Env.getCtx();

	// =============================================================================================
	// Logica para redefinición en plugins
	// =============================================================================================
	public interface CreateFromPluginInterface {
		// El siguiente metodo podrá ser redefinido por un plugin para agregar una funcionalidad particular.
		// El metodo es invocado antes de hacer el save de la linea
		public void customMethod(PO ol, PO iol);
	}
	
	// =============================================================================================
	// Logica en comun para la carga de pedidos
	// =============================================================================================

	/**
	 * Consulta para carga de pedidos
	 */
	public StringBuffer loadOrderQuery(String remainingQtySQLLine) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ")
		   .append(   "l.C_Order_ID, ")
		   .append(   "l.C_OrderLine_ID, ")
		   .append(   "l.DateOrdered, ")
		   .append(   "l.Line, ")
		   .append(   "COALESCE(l.M_Product_ID,0) AS M_Product_ID, ")
		   .append(   "COALESCE(p.Name,c.Name) AS ProductName, ")
		   .append(   "l.Description, ")
		   .append(   "l.C_UOM_ID, ")
		   .append(   "l.QtyOrdered, " )
		   .append(   "l.QtyInvoiced, " )
		   .append(   "l.QtyDelivered, " )
		   .append(   remainingQtySQLLine )
		   .append(   " AS RemainingQty, ")
		   .append(   "(CASE l.QtyOrdered WHEN 0 THEN 0 ELSE l.QtyEntered/l.QtyOrdered END) AS Multiplier, ")
		   .append(   "p.value AS ItemCode, ")
		   .append(   "p.producttype AS ProductType, ")
		   .append(   "l.M_AttributeSetInstance_ID AS AttributeSetInstance_ID ")

		   .append("FROM C_OrderLine l ")
		   .append("LEFT OUTER JOIN M_Product p ON (l.M_Product_ID=p.M_Product_ID) ") 
		   .append("LEFT OUTER JOIN C_Charge c ON (l.C_Charge_ID=c.C_Charge_ID) ")
		   //
		   //Añadido por Conserti, para que no saque los cargos en los albaranes. // and l.c_charge_id is null
		   //
		   .append("WHERE l.C_Order_ID=? and l.C_Charge_ID is NULL ")
		   .append("ORDER BY l.DateOrdered,l.C_Order_ID,l.Line,ItemCode");
		return sql;
	}
	
	/**
	 * Adicion al query principal por defecto en carga de pedidos
	 */
	public String getRemainingQtySQLLine(boolean forInvoice, boolean allowDeliveryReturns) {
    	// Para facturas se compara la cantidad facturada, para remitos la cantidad
    	// entregada/recibida.
		// Si no se puede entregar lo devuelto, entonces también se debe agregar
		// esta condición para remitos
		String compareColumn = forInvoice ? "l.QtyInvoiced"
				: "(l.QtyDelivered+l.QtyTransferred)"
						+ (allowDeliveryReturns ? "" : " + coalesce(("
								+ MInOut.getNotAllowedQtyReturnedSumQuery()
								+ "),0)");
    	return "l.QtyOrdered-"+compareColumn;
	}
	

	/**
	 *	Volcado a un OrderLine a partir del resultSet 
	 */
	public void loadOrderLine(MOrder p_order, OrderLine orderLine, ResultSet rs) throws Exception {		
		// Por defecto no está seleccionada para ser procesada	
		orderLine.selected = false;
		// ID del pedido
		orderLine.documentNo = p_order.getDocumentNo();
		// Fecha del pedido
		orderLine.dateOrderLine = rs.getDate("DateOrdered");
		// ID de la línea del pedido
		orderLine.orderLineID = rs.getInt("C_OrderLine_ID");
		// Nro de línea
		orderLine.lineNo = rs.getInt("Line");
		// Descripción
		orderLine.description = rs.getString("Description");
		// Cantidades
		BigDecimal multiplier = rs.getBigDecimal("Multiplier");
		BigDecimal qtyOrdered = rs.getBigDecimal("QtyOrdered").multiply(multiplier);
		BigDecimal remainingQty = rs.getBigDecimal("RemainingQty").multiply(multiplier);
		orderLine.lineQty = qtyOrdered;
		orderLine.remainingQty = remainingQty;
		orderLine.qtyInvoiced = rs.getBigDecimal("QtyInvoiced");
		orderLine.qtyDelivered = rs.getBigDecimal("QtyDelivered");
		// Artículo
		orderLine.productID = rs.getInt("M_Product_ID");
		orderLine.productName = rs.getString("ProductName");
		orderLine.itemCode = rs.getString("ItemCode");
		orderLine.instanceName = getInstanceName(rs.getInt("AttributeSetInstance_ID"));
		orderLine.productType = rs.getString("ProductType");
		// Unidad de Medida
		orderLine.uomID = rs.getInt("C_UOM_ID");
		orderLine.uomName = getUOMName(orderLine.uomID);
	}
	
    // Dado un attributeSetInstance_ID retorna:
    // El nombre de la instacia completo. Ejemplo: Para una remera con Talle: S y Color: B retorna S - B
    // La descripcion de M_AttributeSetInstance en caso que que la consulta no obtenga resultados. 
    // null si M_AttributeSetInstance_ID es 0
    public String getInstanceName(int attributeSetInstance_ID){
		StringBuffer sql;
		String instanceName = null;

	    sql = new StringBuffer();
		sql.append("select t.value, u.seqno from M_AttributeSetInstance i ")
		.append("INNER JOIN M_AttributeSet s ON (s.M_AttributeSet_ID = i.M_AttributeSet_ID) ") 
		.append("LEFT JOIN M_AttributeUse u ON (u.M_AttributeSet_ID = s.M_AttributeSet_ID) ")
		.append("LEFT JOIN M_AttributeInstance t ON (t.M_Attribute_ID = u.M_Attribute_ID) ")
		.append("where (t.M_AttributeSetInstance_ID = "+ attributeSetInstance_ID +") ")
		.append("group by t.value, u.seqno ")
		.append("order by u.seqno");
		   
		log.finer( sql.toString());

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()){
    			instanceName = rs.getString("Value");
    			while( rs.next()) {
    				instanceName = instanceName + " - " + rs.getString("Value");
        		}
    			return instanceName;
    		}
    		else{
    			StringBuffer sql2;
    			sql2 = new StringBuffer();
    			sql2.append("select Description from M_AttributeSetInstance where (M_AttributeSetInstance_ID <> 0) AND (M_AttributeSetInstance_ID = "+ attributeSetInstance_ID +")");
    			pstmt = DB.prepareStatement( sql2.toString());
        		rs = pstmt.executeQuery();
        		if(rs.next()){
        			return rs.getString("Description");
        		}			
    		}
    	} catch( Exception e ) {
    		log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}
		
		return instanceName;
	}
	
    
    /**
     * Método helper que obtiene el Símbolo o Nombre de una UM para ser
     * mostrado en la grilla.
     * @param uomID ID de la UM
     * @return {@link MUOM#getUOMSymbol()} si no es null o {@link MUOM#getName()}
     */
    public String getUOMName(int uomID) {
		MUOM uom = MUOM.get(ctx, uomID);
		return uom.getUOMSymbol() != null && uom.getUOMSymbol().length() > 0 
			? uom.getUOMSymbol() 
			: uom.getName();
    }
	
    /**
     * @return Devuelve el filtro que se aplica al Lookup de Pedidos.
     * Por defecto este filtro respeta el filtro que se aplicaba en el método
     * {@link #initBPartnerOIS(int, boolean)} para mantener la compatibilidad
     * con otros CreateFroms. Este método se puede sobrescribir para redefinir
     * el filtro según sea necesario.
     */
    public String getOrderFilter(boolean isForInvoice, String isSOTrx) {
    	StringBuffer filter = new StringBuffer();
        String compareColumn = "";

        if( isForInvoice ) {
            compareColumn = "ol.QtyInvoiced";
        } else { // InOut
        	compareColumn = "ol.QtyDelivered+ol.QtyTransferred";
        }

     	filter
     		.append("C_Order.IsSOTrx='").append(isSOTrx).append("' AND ")
     		.append("C_Order.DocStatus IN ('CL','CO') AND ")
     		.append("C_Order.C_Order_ID IN (")
     		.append(   "SELECT ol.C_Order_ID ")
     		.append(   "FROM C_OrderLine ol ")
     		.append(   "WHERE ol.QtyOrdered > (").append(compareColumn).append("))");

     	return filter.toString();
    }
	
	// =============================================================================================
	// SourceEntityListImpl y sus subclases
	// =============================================================================================
	
	/**
	 * Esta interfaz define la necesidad de poder convertir
	 * una instancia de SourceEntityListImpl a un array de valores
	 */
	public interface ListedSourceEntityInterface {
		
    	/** Requiere la conversion del objeto a un array de valores */
    	public ArrayList<Object> toList();
		
	}
	
    /**
     * Esta es la superclase abstracta de todas las entidades que pueden ser origen
     * para la creación de un documento. Una entidad origen puede ser por ejemplo una
     * línea de pedido o factura, un pago, o cualquier PO de la aplicación. A partir
     * de los parámetros de la ventana, se cargan en la grilla un conjunto de 
     * entidades origen para la creación del documento destino. Por ejemplo, al seleccionar
     * un pedido en el VLookup parámetro, se cargan todas las líneas del pedido en la grilla
     * en donde una línea de pedido es una entidad origen para la creación del documento
     * destino.<br><br>
     * 
     * Las subclases de esta ventana puede especializar esta clase en caso de necesitar
     * cargar entidades origen que aún no estén soportadas.<br><br>
     * 
     * Las instancias concretas de esta clase (subclases en verdad) son el modelo que
     * está asociado a la grilla:<br><br>
     *  
     * <code>JTable --> CreateFromTableModel --> List< SourceEntityListImpl ></code> 
     */
    public static abstract class SourceEntity {
    	
    	/** Indica si esta entidad debe ser procesada o no */
    	public Boolean selected = false;

    }

    /**
     * Entidad origen: líneas de un documento (Pedido, Remito, Factura).<br><br>
     * 
     * Esta clase es abstracta y contiene los atributos compartidos por una línea
     * de pedido, remito y factura debido a que su estructura es muy similar.
     * 
     */
    public static abstract class DocumentLine extends SourceEntity {
    	/** Número de línea en el documento original (Columna Line) */
    	public Integer lineNo = 0;
		/** Código del artículo asociado a la línea */
    	public String itemCode = null;
		/** Nombre de la instancia */
		public String instanceName = null;
    	/** Nombre del artículo o cargo asociado a la línea */
		public String productName = null;
    	/** Tipo del artículo o cargo asociado a la línea */
    	public String productType = null;
    	/** ID del artículo o cargo asociado a la línea */
    	public Integer productID = 0;
    	/** Nombre o Descripción de la UM indicada en la línea */
    	public String uomName = null;
    	/** ID de la UM indicada en la línea */
    	public Integer uomID = 0;
    	/** Cantidad total de la línea. Para facturas es igual al valor de
    	 * QtyInvoiced, para pedidos es QtyOrdered y para remitos MovementQty. */
    	public BigDecimal lineQty = BigDecimal.ZERO;
    	/** Cantidad pendiente de la línea. Esta es la cantidad que efectivamente
    	 * se debe utilizar para crear la línea del documento en cuestión. Esta
    	 * cantidad es menor o igual que <code>lineQty</code>  */
    	public BigDecimal remainingQty = BigDecimal.ZERO;
    	/** Descripción de la línea del documento */
    	public String description = null;

		/**
    	 * @return Indica si esta entidad es una línea de un pedido
    	 */
    	public boolean isOrderLine() {
    		return false; 
    	}
    	
    	/**
    	 * @return Indica si esta entidad es una línea de un remito
    	 */
    	public boolean isInOutLine() {
    		return false;
    	}

    	/**
    	 * @return Indica si esta entidad es una línea de una factura
    	 */
    	public boolean isInvoiceLine() {
    		return false;
    	}
    	
    	public Integer getLineNo() {
			return lineNo;
		}

		public void setLineNo(Integer lineNo) {
			this.lineNo = lineNo;
		}

		public Integer getProductID() {
			return productID;
		}

		public void setProductID(Integer productID) {
			this.productID = productID;
		}

		public Integer getUomID() {
			return uomID;
		}

		public void setUomID(Integer uomID) {
			this.uomID = uomID;
		}

		public BigDecimal getRemainingQty() {
			return remainingQty;
		}

		public void setRemainingQty(BigDecimal remainingQty) {
			this.remainingQty = remainingQty;
		}
    }
    
    /**
     * Entidad Origen: Línea de Pedido<br><br>
     *
     * Clase concreta de una entidad origen que contiene la referencia a una línea
     * de pedido.
     */
    public static class OrderLine extends DocumentLine {
    	/** ID de la línea de pedido */
    	public int orderLineID = 0;
    	/** Cantidad factura de la línea */
    	public BigDecimal qtyDelivered = BigDecimal.ZERO;
    	/** Cantidad entregada/recibida de la línea */
    	public BigDecimal qtyInvoiced = BigDecimal.ZERO;
    	
    	/** DocumentNo del pedido */
    	public String documentNo = null;
		
		/** Fecha de la línea de pedido */
    	public Date dateOrderLine = null;

		@Override
		public boolean isOrderLine() {
			return true;
		}
    }
    
    
	/**
	 * Entidad Orígen: Línea de Remito
	 */
	public static class InOutLine extends DocumentLine {
		/** ID de la línea de remito */
		public int inOutLineID = 0;
		/** La línea de remito puede tener asociada a su vez una línea de pedido */
		public int orderLineID = 0;

		@Override
		public boolean isInOutLine() {
			return true;
		}
	}
	
	
	/**
	 * Entidad Orígen: Línea de Factura
	 */
	public static class InvoiceLine extends DocumentLine {
		/** ID de la línea de factura */
		public int invoiceLineID = 0;
		/**
		 * La línea de factura puede tener asociada a su vez una línea de pedido
		 */
		public int orderLineID = 0;

		// /** Nombre de la instancia */
		// protected String instanceName = null;

		@Override
		public boolean isInvoiceLine() {
			return true;
		}
	}
	
	
	/**
     * Entidad Oríden: Pagos
     */
    public static class Payment extends DocumentLine {
    	/** ID del pago */
    	public int paymentID = 0;
    	/** Nro de Documento del pago */
    	public String documentNo = null;
    	/** Fecha de transacción */
    	public Timestamp dateTrx = null;
        /** Nombre o descripción de la EC asociada al pago */
    	public String bPartnerName = null;
    	/** ID de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	public int currencyID = 0;
    	/** Código ISO de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	public String currencyISO = null;
        /** Importe del pago expresado en la moneda currencyID */
    	public BigDecimal payAmt = BigDecimal.ZERO;
        /** Importe del pago convertido a la moneda de la cuenta bancaria a la que pertence*/
    	public BigDecimal convertedAmt = BigDecimal.ZERO;
    	/** Fecha de vencimiento */
    	public Timestamp dueDate=null;
    	/** Tipo del Pago */
    	protected String tenderType = null;
    }
    
    /**
     * Entidad Orígen: Pagos
     */
    public static class ProcessParameter extends DocumentLine {
    	/** ID del parámetro del proceso */
    	public int processParaID = 0;
    	/** Nro de secuencia */
    	public Integer seqNo = null;
    	/** Nombre */
    	public String name = null;
        /** Nombre de la columna */
    	public String columnName = null;    
    }
    
	// =============================================================================================
	// Excepciones al persistir
	// =============================================================================================

    /**
     * Excepción lanzada en casos de error en el guardado
     */
    @SuppressWarnings("serial")
	public static class CreateFromSaveException extends Exception {

		public CreateFromSaveException() {
			super();
		}

		public CreateFromSaveException(String message, Throwable cause) {
			super(message, cause);
		}

		public CreateFromSaveException(String message) {
			super(message);
		}
    }
    

}
