package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CreateFromShipmentModel extends CreateFromModel {

	// =============================================================================================
	// Logica en comun para la carga de facturas
	// =============================================================================================

	/**
	 * Consulta para carga de facturas
	 */
	public StringBuffer loadInvoiceQuery() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ")
		// Entered UOM
		.append("l.C_InvoiceLine_ID, ")
		.append("l.Line, ")
		.append("l.Description, ")
		.append("l.M_Product_ID, ")
		.append("p.Name AS ProductName, ")
		.append("p.value AS ItemCode, ")
		.append("p.producttype AS ProductType, ")
		.append("l.C_UOM_ID, ")
		.append("QtyInvoiced, ")
		.append("l.QtyInvoiced-SUM(NVL(mi.Qty,0)) AS RemainingQty, ")
		.append("l.QtyEntered/l.QtyInvoiced AS Multiplier, ")
		.append("COALESCE(l.C_OrderLine_ID,0) AS C_OrderLine_ID, ")
		.append("l.M_AttributeSetInstance_ID AS AttributeSetInstance_ID ")

		.append("FROM C_UOM uom, C_InvoiceLine l, M_Product p, M_MatchInv mi ")

		.append("WHERE l.C_UOM_ID=uom.C_UOM_ID ")
		.append("AND l.M_Product_ID=p.M_Product_ID ")
		.append("AND l.C_InvoiceLine_ID=mi.C_InvoiceLine_ID(+) ")
		.append("AND l.C_Invoice_ID=? ")
		.append("GROUP BY l.QtyInvoiced, l.QtyEntered/l.QtyInvoiced, l.C_UOM_ID, l.M_Product_ID, p.Name, l.C_InvoiceLine_ID, l.Line, l.C_OrderLine_ID, l.Description, p.value,l.M_AttributeSetInstance_ID,p.producttype ")
		.append("ORDER BY l.Line ");
		return sql;
	}
	
	public void loadInvoiceLine(InvoiceLine invoiceLine, ResultSet rs) throws SQLException {

		// Por defecto no está seleccionada para ser procesada
		invoiceLine.selected = false;

		// ID de la línea de factura
		invoiceLine.invoiceLineID = rs.getInt("C_InvoiceLine_ID");

		// Nro de línea
		invoiceLine.lineNo = rs.getInt("Line");

		// Descripción
		invoiceLine.description = rs.getString("Description");

		// Cantidades
		BigDecimal multiplier = rs.getBigDecimal("Multiplier");
		BigDecimal qtyInvoiced = rs.getBigDecimal("QtyInvoiced")
				.multiply(multiplier);
		BigDecimal remainingQty = rs.getBigDecimal("RemainingQty")
				.multiply(multiplier);
		invoiceLine.lineQty = qtyInvoiced;
		invoiceLine.remainingQty = remainingQty;

		// Artículo
		invoiceLine.productID = rs.getInt("M_Product_ID");
		invoiceLine.productName = rs.getString("ProductName");
		invoiceLine.itemCode = rs.getString("ItemCode");
		invoiceLine.instanceName = getInstanceName(rs
				.getInt("AttributeSetInstance_ID"));
		invoiceLine.productType = rs.getString("ProductType");

		// Unidad de Medida
		invoiceLine.uomID = rs.getInt("C_UOM_ID");
		invoiceLine.uomName = getUOMName(invoiceLine.uomID);

		// Línea de pedido (puede ser 0)
		invoiceLine.orderLineID = rs.getInt("C_OrderLine_ID");
	}
	

	public String getRemainingQtySQLLine(MInOut inout, boolean forInvoice, boolean allowDeliveryReturns){
		boolean afterInvoicing = (inout.getDeliveryRule().equals(
				MInOut.DELIVERYRULE_AfterInvoicing) || inout.getDeliveryRule()
				.equals(MInOut.DELIVERYRULE_Force_AfterInvoicing))
				&& inout.getMovementType().endsWith("-");
		String srcColumn = afterInvoicing ? "l.QtyInvoiced" : "l.QtyOrdered";
		return srcColumn
				+ " - (l.QtyDelivered+l.QtyTransferred)"
				+ (allowDeliveryReturns ? ""
						: " - coalesce((select sum(iol.movementqty) as qty from c_orderline as ol inner join m_inoutline as iol on iol.c_orderline_id = ol.c_orderline_id inner join m_inout as io on io.m_inout_id = iol.m_inout_id inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id where ol.c_orderline_id = l.c_orderline_id AND dt.doctypekey = 'DC' and io.docstatus IN ('CL','CO')),0)");
	}
	
	/**
	 * Efectiviza la persistencia los POs
	 */
	public void save(Integer locatorID, MInOut inOut, MOrder p_order, MInvoice m_invoice, List<? extends SourceEntity> selectedSourceEntities, String trxName, boolean isSOTrx, CreateFromPluginInterface handler) throws CreateFromSaveException {

		// La ubicación es obligatoria
		if (locatorID == null || (locatorID == 0)) {
//			locatorField.setBackground(CompierePLAF.getFieldBackground_Error());
			throw new CreateFromSaveException("@NoLocator@");
		}

		// Actualiza el encabezado del remito (necesario para validaciones en
		// las
		// líneas a crear del remito)
		MInOut inout = inOut;
		log.config(inout + ", C_Locator_ID=" + locatorID);
		// Asocia el pedido
		if (p_order != null) {
			inout.setC_Order_ID(p_order.getC_Order_ID());
			inout.setDateOrdered(p_order.getDateOrdered());
			inout.setC_Project_ID(p_order.getC_Project_ID());
			inout.setPOReference(p_order.getPOReference());
			inout.setAD_Org_ID(p_order.getAD_Org_ID());
			inout.setAD_OrgTrx_ID(p_order.getAD_OrgTrx_ID());
			inout.setC_Campaign_ID(p_order.getC_Campaign_ID());
			inout.setUser1_ID(p_order.getUser1_ID());
			inout.setUser2_ID(p_order.getUser2_ID());
			setWarehouse(inout, p_order, trxName);
			inout.setDeliveryRule(p_order.getDeliveryRule());
			inout.setDeliveryViaRule(p_order.getDeliveryViaRule());
			inout.setM_Shipper_ID(p_order.getM_Shipper_ID());
			inout.setFreightCostRule(p_order.getFreightCostRule());
			inout.setFreightAmt(p_order.getFreightAmt());
			inout.setC_BPartner_ID(p_order.getBill_BPartner_ID());
		}
		// Asocia la factura
		if ((m_invoice != null) && (m_invoice.getC_Invoice_ID() != 0)) {
			inout.setC_Invoice_ID(m_invoice.getC_Invoice_ID());
		}

		// Guarda el encabezado. Si hay error cancela la operación
		if (!inout.save()) {
			throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
		}

		// Lines
		Integer productLocatorID = null;
		MLocator productLocator = null;
		for (SourceEntity sourceEntity : selectedSourceEntities) {
			DocumentLine docLine = (DocumentLine) sourceEntity;
			BigDecimal movementQty = docLine.remainingQty;
			int C_UOM_ID = docLine.uomID;
			int M_Product_ID = docLine.productID;
			
			// Determinar la ubicación relacionada al artículo y verificar que
			// se encuentre dentro del almacén del remito. Si se encuentra en
			// este almacén, entonces setearle la ubicación del artículo, sino
			// la ubicación por defecto. Sólo para movimientos de ventas.
			productLocatorID = null;
			if(isSOTrx){
				// Obtengo el id de la ubicación del artículo
				productLocatorID = MProduct.getLocatorID(M_Product_ID, trxName);
				// Si posee una configurada, verifico que sea del mismo almacén,
				// sino seteo a null el id de la ubicación para que setee el que
				// viene por defecto
				if(!Util.isEmpty(productLocatorID, true)){
					productLocator = MLocator.get(ctx, productLocatorID);
					productLocatorID = productLocator.getM_Warehouse_ID() != inout
							.getM_Warehouse_ID() ? null : productLocatorID;
				}
			}
			
			// Crea la línea del remito
			
			MInOutLine iol = new MInOutLine(inout);
			iol.setM_Product_ID(M_Product_ID, C_UOM_ID); // Line UOM
			iol.setQty(movementQty); // Movement/Entered
			iol.setM_Locator_ID(Util.isEmpty(productLocatorID, true) ? locatorID
					: productLocatorID); // Locator
			iol.setDescription(docLine.description);

			MInvoiceLine il = null;
			MOrderLine ol = null;

			// La línea del remito se crea a partir de una línea de pedido
			if (docLine.isOrderLine()) {
				OrderLine orderLine = (OrderLine) docLine;
				// Asocia línea remito -> línea pedido
				iol.setC_OrderLine_ID(orderLine.orderLineID);
				ol = new MOrderLine(Env.getCtx(), orderLine.orderLineID,
						trxName);
				// Proyecto
				iol.setC_Project_ID(ol.getC_Project_ID());
				if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0) {
					iol.setMovementQty(movementQty.multiply(ol.getQtyOrdered())
							.divide(ol.getQtyEntered(),
									BigDecimal.ROUND_HALF_UP));
					iol.setC_UOM_ID(ol.getC_UOM_ID());
				}
				// Instancia de atributo
				if (ol.getM_AttributeSetInstance_ID() != 0) {
					iol.setM_AttributeSetInstance_ID(ol
							.getM_AttributeSetInstance_ID());
				}
				// Cargo (si no existe el artículo)
				if (M_Product_ID == 0 && ol.getC_Charge_ID() != 0) {
					iol.setC_Charge_ID(ol.getC_Charge_ID());
				}
				
				// Este metodo es redefinido por un plugin
				handler.customMethod(ol,iol);

				// La línea del remito se crea a partir de una línea de factura
			} else if (docLine.isInvoiceLine()) {
				InvoiceLine invoiceLine = (InvoiceLine) docLine;
				// Credit Memo - negative Qty
				if (m_invoice != null && m_invoice.isCreditMemo()) {
					movementQty = movementQty.negate();
				}
				il = new MInvoiceLine(Env.getCtx(), invoiceLine.invoiceLineID,
						trxName);
				// Proyecto
				iol.setC_Project_ID(il.getC_Project_ID());
				if (il.getQtyEntered().compareTo(il.getQtyInvoiced()) != 0) {
					iol.setQtyEntered(movementQty.multiply(il.getQtyInvoiced())
							.divide(il.getQtyEntered(),
									BigDecimal.ROUND_HALF_UP));
					iol.setC_UOM_ID(il.getC_UOM_ID());
				}
				// Instancia de atributo
				if (il.getM_AttributeSetInstance_ID() != 0) {
					iol.setM_AttributeSetInstance_ID(il
							.getM_AttributeSetInstance_ID());
				}
				// Cargo (si no existe el artículo)
				if (M_Product_ID == 0 && il.getC_Charge_ID() != 0) {
					iol.setC_Charge_ID(il.getC_Charge_ID());
				}
				// Si la línea de factura estaba relacionada con una línea de
				// pedido
				// entonces se hace la asociación a la línea del remito. Esto es
				// necesario
				// para que se actualicen los valores QtyOrdered y QtyReserved
				// en el Storage
				// a la hora de completar el remito.
				if (invoiceLine.orderLineID > 0) {
					iol.setC_OrderLine_ID(invoiceLine.orderLineID);
				}
				iol.setC_InvoiceLine_ID(invoiceLine.invoiceLineID);
			}
			// Guarda la línea de remito
			if (!iol.save()) {
				throw new CreateFromSaveException("@InOutLineSaveError@ (# "
						+ docLine.lineNo + "):<br>"
						+ CLogger.retrieveErrorAsString());

				// Create Invoice Line Link
			} else if (il != null) {
				il.setM_InOutLine_ID(iol.getM_InOutLine_ID());
				if (!il.save()) {
					throw new CreateFromSaveException(
							"@InvoiceLineSaveError@ (# " + il.getLine()
									+ "):<br>"
									+ CLogger.retrieveErrorAsString());
				}
			}
		} // for all rows
	} // save
	
	
	public boolean beforeAddOrderLine(OrderLine orderLine, MInOut inOut, boolean isSOTrx) {
		// Si la línea de pedido ya está asociada con alguna línea del remito
		// entonces
		// no debe ser mostrada en la grilla. No se permite que dos líneas de un
		// mismo
		// remito compartan una línea del pedido.
		String sql = "SELECT COUNT(*) FROM M_InOutLine WHERE M_InOut_ID = ? AND C_OrderLine_ID = ?";
		Long count = (Long) DB.getSQLObject(null, sql, new Object[] {
				inOut.getM_InOut_ID(), orderLine.orderLineID });
		if (count != null && count > 0) {
			return false;
		}

		// Para devoluciones de clientes, la cantidad pendiente es en realidad
		// la cantidad que se le ha entregado.
		if ((isSOTrx && inOut.getMovementType().endsWith("+"))
				|| (!isSOTrx && inOut.getMovementType().endsWith("-"))) {
			orderLine.remainingQty = orderLine.qtyDelivered;
		}

		return true;
	}
	
	/*
	 * Setea el warehouse del remito a partir del especificado en el pedido
	 * UNICAMENTE si la configuración del tipo de documento así lo especifica,
	 * o bien si todavía este dato no se encuentra especificado
	 */
	protected void setWarehouse(MInOut inOut, MOrder order, String trxName) {
		// Recuperar tipo de documento
		X_C_DocType inOutDocType = new X_C_DocType(ctx, inOut.getC_DocType_ID(), trxName);
		// Si no esta seteado o bien hay que forzar el warehouse del pedido, setearlo
		if ((inOut.getM_Warehouse_ID() <= 0) || inOutDocType.isUseOrderWarehouse()) {
			inOut.setM_Warehouse_ID(order.getM_Warehouse_ID());
		}
	}
}
