package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;


public class CreateFromInvoiceModel extends CreateFromModel {

	/** Forma de pago para la factura */
	private String paymentRule = null; 
	
	// =============================================================================================
	// Logica en comun para la carga de envios
	// =============================================================================================

	/**
	 * Consulta para carga de envios
	 */
	public StringBuffer loadShipmentQuery(String remainingSQLQueryLine) {

        StringBuffer sql  = new StringBuffer();
        sql.append("SELECT ")
           .append(   "iol.M_InOutLine_ID, ")
           .append(   "iol.Line, ")
           .append(   "iol.M_Product_ID, ")
           .append(   "p.Name AS ProductName, ")
           .append("p.value AS ItemCode, ")
           .append(   "iol.C_UOM_ID, ")
           .append(   "iol.MovementQty, ")
		   .append(" ( CASE WHEN (iol.C_OrderLine_ID IS NULL OR dtio.InOut_Allow_Greater_QtyOrdered = 'Y') THEN iol.movementqty ELSE " + remainingSQLQueryLine + " END ) AS RemainingQty, ")
           .append(   "iol.QtyEntered/iol.MovementQty AS Multiplier, ")
           .append(   "COALESCE(iol.C_OrderLine_ID,0) AS C_OrderLine_ID, ")  
           .append("iol.M_AttributeSetInstance_ID AS AttributeSetInstance_ID ")
           
           .append("FROM M_InOutLine iol " )
           .append("JOIN M_InOut io ON io.m_inout_id = iol.m_inout_id " )
           .append("JOIN C_DocType dtio ON dtio.c_doctype_id = io.c_doctype_id " )
           .append("JOIN M_Product p ON p.m_product_id = iol.m_product_id " )
           .append("LEFT JOIN C_OrderLine l ON l.c_orderline_id = iol.c_orderline_id " )
           .append("WHERE iol.M_InOut_ID=? " )    // #1
           .append("ORDER BY iol.Line" );
        
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
		invoice.setDragDocumentDiscountAmts(docType.isDragOrderDocumentDiscounts());
		invoice.setDragDocumentSurchargesAmts(docType.isDragOrderDocumentSurcharges());
        log.config( invoice.toString());
        boolean isDebit = invoice.isDebit();
        // Asociación con el pedido
        if( p_order != null ) {
            invoice.setOrder( p_order, true );    // overwrite header values
            
            // Gestionar arrastre de descuentos del pedido 
			boolean manageOrderDiscounts = (docType
					.isDragOrderDocumentDiscounts() || docType
					.isDragOrderLineDiscounts())
					&& MOrder.isDiscountsApplied(p_order);
			invoice.setManageDragOrderDiscounts(manageOrderDiscounts);

			// Gestionar arrastre de recargos del pedido
			boolean manageOrderSurcharges = (docType
					.isDragOrderDocumentSurcharges() || docType
					.isDragOrderLineSurcharges())
					&& MOrder.isSurchargesApplied(p_order);
			invoice.setManageDragOrderSurcharges(manageOrderSurcharges);
			
			// Si se deben arrastrar los descuentos del pedido, resetear el
			// descuento de la cabecera
			if(manageOrderDiscounts || manageOrderSurcharges){
				invoice.setManualGeneralDiscount(BigDecimal.ZERO);
				invoice.setSkipManualGeneralDiscountValidation(true);
			}
			else {
				invoice.setManualGeneralDiscount(p_order.getManualGeneralDiscount());
			}
			invoice.setIsExchange(p_order.isExchange());
			if(!Util.isEmpty(getPaymentRule(), true)){
				invoice.setPaymentRule(getPaymentRule());
			}
			invoice.setCreateFrom("Y");
            if (!invoice.save()) {
            	String msg = CLogger.retrieveErrorAsString();
				msg = Util.isEmpty(msg, true) ? Msg.parseTranslation(Env.getCtx(),
						"@InvoiceSaveError@. @SeeTheLog@.") : msg;
            	throw new CreateFromSaveException(msg);
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
			invoiceLine.setDragDocumentSurchargesAmts(docType
					.isDragOrderDocumentSurcharges());
			invoiceLine.setDragLineSurchargesAmts(docType
					.isDragOrderLineSurcharges());
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
            } else if(isDebit){
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
                // Este metodo es redefinido por un plugin
                handler.customMethod(orderLine,invoiceLine);
                
                invoiceLine.setOrderLine( orderLine );    // overwrites

                if( orderLine.getQtyEntered().compareTo( orderLine.getQtyOrdered()) != 0 ) {
                    invoiceLine.setQtyInvoiced( QtyEntered.multiply( orderLine.getQtyOrdered()).divide( orderLine.getQtyEntered(),BigDecimal.ROUND_HALF_UP ));
                }
            } else {
                log.fine( "No Order Line" );
                invoiceLine.setPrice();
                invoiceLine.setTax();
            }

            invoiceLine.setTaxAmt(BigDecimal.ZERO);
            if( !invoiceLine.save()) {
                throw new CreateFromSaveException(
             		   "@InvoiceLineSaveError@ (# " + docLine.lineNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);
            }
        }        // for all rows
        
		// Actualización de la cabecera por totales de descuentos e impuestos
		// siempre y cuando el tipo de documento lo permita
		if (p_order != null && (docType.isDragOrderDocumentDiscounts() || docType.isDragOrderDocumentSurcharges())) {
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
	public static String getShipmentFilter(String isSOTrx, String orderFilter) {
    	StringBuffer filter = new StringBuffer();

     	filter
	     	.append("M_InOut.IsSOTrx='").append(isSOTrx).append("' AND ")
	     	.append("M_InOut.DocStatus IN ('CL','CO') AND ") 
	     	.append(" (CASE WHEN 'N'='").append(isSOTrx).append("' THEN ")
	     	.append(" M_InOut.M_InOut_ID IN (")
	     	.append(   "SELECT sl.M_InOut_ID ")
	     	.append(   "FROM M_InOutLine sl ")
	     	.append(   "LEFT OUTER JOIN M_MatchInv mi ON (sl.M_InOutLine_ID=mi.M_InOutLine_ID) ")
	     	.append(   "WHERE sl.M_InOut_ID = M_InOut.M_InOut_ID ") // Los M_InOut que devuelve la query interna luego tienen que respetar el criterio de la query externa, por lo tanto este filtrado mejora la performance de la query interna
	     	.append(   "GROUP BY sl.M_InOut_ID,mi.M_InOutLine_ID,sl.MovementQty ")
	     	.append(   "HAVING (sl.MovementQty<>SUM(mi.Qty) AND mi.M_InOutLine_ID IS NOT NULL) OR mi.M_InOutLine_ID IS NULL) ")
     		.append(" ELSE ")
     		.append(" M_InOut.C_Order_ID IN (")
	     	.append(   "SELECT C_Order.C_Order_ID ") 
	     	.append(   "FROM C_Order ")
	     	.append(   "WHERE (")
	     	.append(   orderFilter).append(")")
	     	.append(") END ) ");
     		
     	
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

	public String getPaymentRule() {
		return paymentRule;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}
}
