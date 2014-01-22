package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class CreateFromInvoiceModel extends CreateFromModel {

	// =============================================================================================
	// Logica en comun para la carga de envios
	// =============================================================================================

	/**
	 * Consulta para carga de envios
	 */
	public StringBuffer loadShipmentQuery() {

        StringBuffer sql  = new StringBuffer();
        sql.append("SELECT ")
           .append(   "l.M_InOutLine_ID, ")
           .append(   "l.Line, ")
           .append(   "l.M_Product_ID, ")
           .append(   "p.Name AS ProductName, ")
           .append("p.value AS ItemCode, ")
           .append(   "l.C_UOM_ID, ")
           .append(   "l.MovementQty, ")
           .append(   "l.MovementQty-SUM(NVL(mi.Qty,0)) AS RemainingQty, ")
           .append(   "l.QtyEntered/l.MovementQty AS Multiplier, ")
           .append(   "COALESCE(l.C_OrderLine_ID,0) AS C_OrderLine_ID, ")  
           .append("l.M_AttributeSetInstance_ID AS AttributeSetInstance_ID ")
           
           .append("FROM M_InOutLine l, M_Product p, M_MatchInv mi " )
           .append("WHERE l.M_Product_ID=p.M_Product_ID " )
           // begin vpj-cd e-evolution 03/15/2005
	       // .append(" AND l.M_InOutLine_ID=mi.M_InOutLine_ID(+)")
	       .append(  "AND l.M_InOutLine_ID=mi.M_InOutLine_ID(+) " )
	       // end vpj-cd e-evolution 03/15/2005
	       .append(  "AND l.M_InOut_ID=? " )    // #1
           .append("GROUP BY l.MovementQty, l.QtyEntered/l.MovementQty, l.C_UOM_ID, l.M_Product_ID, p.Name, l.M_InOutLine_ID, l.Line, l.C_OrderLine_ID, p.value,l.M_AttributeSetInstance_ID " )
           .append("ORDER BY l.Line" );
        
        return sql;
	}
	
	/**
	 *	Volcado a un InOutLine a partir del resultSet 
	 */
	public void loadShipmentLine(InOutLine docLine, ResultSet rs) throws SQLException {
        // Por defecto no está seleccionada para ser procesada
        docLine.selected = false;
        // ID de la línea de remito
        docLine.inOutLineID = rs.getInt("M_InOutLine_ID");
		// Nro de línea
        docLine.lineNo = rs.getInt("Line");
        // Cantidades
        BigDecimal multiplier  = rs.getBigDecimal("Multiplier");
        BigDecimal movementQty = rs.getBigDecimal("MovementQty").multiply(multiplier);
        BigDecimal remainingQty = rs.getBigDecimal("RemainingQty").multiply(multiplier);
        docLine.lineQty = movementQty;
        docLine.remainingQty = remainingQty;
        // Artículo
		docLine.productID = rs.getInt("M_Product_ID");
		docLine.productName = rs.getString("ProductName");
		docLine.itemCode = rs.getString("ItemCode");
		docLine.instanceName = getInstanceName(rs.getInt("AttributeSetInstance_ID"));
		// Unidad de Medida
		docLine.uomID = rs.getInt("C_UOM_ID");
        docLine.uomName = getUOMName(docLine.uomID);
        // Línea de pedido (puede ser 0)
        docLine.orderLineID = rs.getInt("C_OrderLine_ID");
	}

	
	/**
	 * Efectiviza la persistencia los POs
	 */
    public void save(MOrder p_order, MInvoice invoice, MInOut m_inout, MDocType docType, List<? extends SourceEntity> selectedSourceEntities, String trxName, CreateFromPluginInterface handler) throws CreateFromSaveException {

    	// Actualiza el encabezado de la factura
		invoice.setDragDocumentDiscountAmts(docType
				.isDragOrderDocumentDiscounts());
        log.config( invoice.toString());
        // Asociación con el pedido
        if( p_order != null ) {
            invoice.setOrder( p_order, true );    // overwrite header values
			boolean manageOrderDiscounts = (docType
					.isDragOrderDocumentDiscounts() || docType
					.isDragOrderLineDiscounts())
					&& MOrder.isDiscountsApplied(p_order);
			invoice.setManageDragOrderDiscounts(manageOrderDiscounts);
			// Si se deben arrastrar los descuentos del pedido, resetear el
			// descuento de la cabecera
			if(manageOrderDiscounts){
				invoice.setManualGeneralDiscount(BigDecimal.ZERO);
				invoice.setSkipManualGeneralDiscountValidation(true);
			}
			invoice.setIsExchange(p_order.isExchange());
            if (!invoice.save()) {
            	throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
            }
        }
        // Asocia el remito con la factura si es que se está creando a partir
        // de un remito
        if( (m_inout != null) && (m_inout.getM_InOut_ID() != 0) && (m_inout.getC_Invoice_ID() == 0)) {    // only first time
            m_inout.setC_Invoice_ID(invoice.getC_Invoice_ID());
            if (!m_inout.save(trxName)) {
            	throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
            }
        }

        // Lines

        for (SourceEntity sourceEntity : selectedSourceEntities) {

            // variable values
        	DocumentLine docLine = (DocumentLine) sourceEntity;
            BigDecimal  QtyEntered = docLine.remainingQty;
            int C_UOM_ID = docLine.uomID;
            int M_Product_ID = docLine.productID;
            int C_Charge_ID = 0;
            int C_OrderLine_ID = 0;
            int M_InOutLine_ID = 0;

            if (docLine.isOrderLine()) {
            	C_OrderLine_ID = ((OrderLine)docLine).orderLineID;
            } else if (docLine.isInOutLine()) {
            	M_InOutLine_ID = ((InOutLine)docLine).inOutLineID;
            	C_OrderLine_ID = ((InOutLine)docLine).orderLineID;
            }
            
            //
            log.fine( "Line QtyEntered=" + QtyEntered + ", Product_ID=" + M_Product_ID + ", OrderLine_ID=" + C_OrderLine_ID + ", InOutLine_ID=" + M_InOutLine_ID );

            MInvoiceLine invoiceLine = new MInvoiceLine(invoice);

            invoiceLine.setM_Product_ID(M_Product_ID, C_UOM_ID);    // Line UOM
            invoiceLine.setQty(QtyEntered);    // Invoiced/Entered
            invoiceLine.setDescription(docLine.description);
			invoiceLine.setDragDocumentDiscountAmts(docType
					.isDragOrderDocumentDiscounts());
			invoiceLine.setDragLineDiscountAmts(docType
					.isDragOrderLineDiscounts());
			invoiceLine.setDragOrderPrice(docType.isDragOrderPrice());
			
            // Info
            MOrderLine orderLine = null;

            if( C_OrderLine_ID != 0 ) {
                orderLine = new MOrderLine( Env.getCtx(),C_OrderLine_ID, trxName);
            }

            MInOutLine inoutLine = null;

            if( M_InOutLine_ID != 0 ) {
                inoutLine = new MInOutLine( Env.getCtx(),M_InOutLine_ID, trxName);

                if( (orderLine == null) && (inoutLine.getC_OrderLine_ID() != 0) ) {
                    C_OrderLine_ID = inoutLine.getC_OrderLine_ID();
                    orderLine      = new MOrderLine( Env.getCtx(),C_OrderLine_ID, trxName);
                }
            } else {
                MInOutLine[] lines = MInOutLine.getOfOrderLine( Env.getCtx(),C_OrderLine_ID,null,trxName);

                log.fine( "Receipt Lines with OrderLine = #" + lines.length );

                if( lines.length > 0 ) {
                    for( int j = 0;j < lines.length;j++ ) {
                        MInOutLine line = lines[ j ];

                        if( line.getQtyEntered().compareTo( QtyEntered ) == 0 ) {
                            inoutLine      = line;
                            M_InOutLine_ID = inoutLine.getM_InOutLine_ID();

                            break;
                        }
                    }

                    if( inoutLine == null ) {
                        inoutLine      = lines[ 0 ];     // first as default
                        M_InOutLine_ID = inoutLine.getM_InOutLine_ID();
                    }
                }
            }                                            // get Ship info

            // Shipment Info

            if( inoutLine != null ) {
                invoiceLine.setShipLine( inoutLine );    // overwrites

                // Este metodo es redefinido por un plugin
                handler.customMethod(inoutLine,invoiceLine);
                
                if( inoutLine.getQtyEntered().compareTo( inoutLine.getMovementQty()) != 0 ) {
                    invoiceLine.setQtyInvoiced( QtyEntered.multiply( inoutLine.getMovementQty()).divide( inoutLine.getQtyEntered(),BigDecimal.ROUND_HALF_UP ));
                }
            } else {
                log.fine( "No Receipt Line" );
            }

            // Order Info

            if( orderLine != null) {
                invoiceLine.setOrderLine( orderLine );    // overwrites
                
                // Este metodo es redefinido por un plugin
                handler.customMethod(orderLine,invoiceLine);

                if( orderLine.getQtyEntered().compareTo( orderLine.getQtyOrdered()) != 0 ) {
                    invoiceLine.setQtyInvoiced( QtyEntered.multiply( orderLine.getQtyOrdered()).divide( orderLine.getQtyEntered(),BigDecimal.ROUND_HALF_UP ));
                }
            } else {
                log.fine( "No Order Line" );
                invoiceLine.setPrice();
                invoiceLine.setTax();
            }

            if( !invoiceLine.save()) {
                throw new CreateFromSaveException(
             		   "@InvoiceLineSaveError@ (# " + docLine.lineNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);
            }
        }        // for all rows
        
		// Actualización de la cabecera por totales de descuentos e impuestos
		// siempre y cuando el tipo de documento lo permita
        if(docType.isDragOrderDocumentDiscounts() && p_order != null){
			try{
				invoice.updateTotalDocumentDiscount();	
			} catch(Exception e){
				throw new CreateFromSaveException(e.getMessage());
			}
        }
    }    // saveInvoice
		
    
	/**
	 * @return Devuelve el filtro que se aplica al Lookup de Facturas asociadas
	 * a pedidos.
	 */
	public static String getInvoiceOrderFilter(String isSOTrx, String orderFilter) {
    	StringBuffer filter = new StringBuffer();

     	filter
	     	.append("C_Invoice.IsSOTrx='").append(isSOTrx).append("' AND ")
	     	.append("C_Invoice.DocStatus IN ('CL','CO') AND ") 
	     	.append("C_Invoice.C_Order_ID IS NOT NULL AND ")
	     	.append("C_Invoice.C_Order_ID IN (")
	     	.append(   "SELECT C_Order.C_Order_ID ") 
	     	.append(   "FROM C_Order ")
	     	.append(   "WHERE (")
	     	.append(   orderFilter).append(")")
	     	.append(")");
     	   	
     	return filter.toString();
	}
	
	
	/**
	 * @return Devuelve el filtro que se aplica al Lookup de remitos.
	 */
	public static String getShipmentFilter(String isSOTrx) {
    	StringBuffer filter = new StringBuffer();

     	filter
	     	.append("M_InOut.IsSOTrx='").append(isSOTrx).append("' AND ")
	     	.append("M_InOut.DocStatus IN ('CL','CO') AND ") 
	     	.append("M_InOut.M_InOut_ID IN (")
	     	.append(   "SELECT sl.M_InOut_ID ")
	     	.append(   "FROM M_InOutLine sl ")
	     	.append(   "LEFT OUTER JOIN M_MatchInv mi ON (sl.M_InOutLine_ID=mi.M_InOutLine_ID) ")
	     	.append(   "GROUP BY sl.M_InOut_ID,mi.M_InOutLine_ID,sl.MovementQty ")
	     	.append(   "HAVING (sl.MovementQty<>SUM(mi.Qty) AND mi.M_InOutLine_ID IS NOT NULL) OR mi.M_InOutLine_ID IS NULL) ");
     	
     	return filter.toString();
	}
	
	public static boolean beforeAddOrderLine(OrderLine orderLine, MInvoice invoice) {
		// Si la línea de pedido ya está asociada con alguna línea de la factura entonces
		// no debe ser mostrada en la grilla. No se permite que dos líneas de una misma
		// factura compartan una línea del pedido. Todo en el caso que no se
		// esté creando una factura sino un crédito
		String sql = 
			"SELECT COUNT(*) FROM C_InvoiceLine WHERE C_Invoice_ID = ? AND C_OrderLine_ID = ?";
		Long count = (Long)DB.getSQLObject(null, sql, 
				new Object[] { invoice.getC_Invoice_ID(), orderLine.orderLineID }
		);
		return (count == null || count == 0);

	}
}
