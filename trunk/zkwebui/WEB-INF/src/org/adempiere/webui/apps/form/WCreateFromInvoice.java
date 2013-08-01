package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

public class WCreateFromInvoice extends WCreateFrom {

    protected WCreateFromInvoice( MTab mTab ) {
        super( mTab );
        log.info( mTab.toString());
		p_WindowNo = mTab.getWindowNo();
		AEnv.showWindow(window);
    }    // VCreateFromInvoice
	
    /** Descripción de Campos */

    private MInOut m_inout = null;
    
    /** Factura que invoca este Crear Desde */
    private MInvoice invoice = null;
    
	/** Este crear desde lleva la lógica de facturas o la lógica de remitos? */
    private Boolean isForInvoice = null;
    
    /** Tipo de Documento a crear */
    private MDocType docType;
    
   
    
    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected boolean dynInit() throws Exception {
        log.config( "" );
        initIsForInvoice();
        initShipmentLookup();
        initInvoiceOrderLookup();
        
        invoiceLabel.setVisible( false );
        locatorLabel.setVisible( false );
        locatorField.setVisible( false );
        initBPartner( true );
        bPartnerField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent evt) {
				// Se limpia la selección de remito
				shipmentField.setValue(null);
				shipmentChanged(0);
			}
		});
        
    	// Se setea el pedido que esté configurado en el encabezado de la factura
    	if (getInvoice().getC_Order_ID() > 0) {
    		orderField.setValue(getInvoice().getC_Order_ID());
    		orderChanged(getInvoice().getC_Order_ID());
    		invoiceOrderLabel.setVisible(false);
        	invoiceOrderField.setVisible(false);
        	shipmentField.setVisible(false);
        	shipmentLabel.setVisible(false);
    	}
    	
    	// Se setea el remito que esté configurado para esta factura
		Integer inoutID = DB.getSQLValue(getTrxName(),
				"SELECT m_inout_id FROM m_inout WHERE c_invoice_id = ?",
				getInvoice().getID());
    	if(inoutID != null && inoutID > 0){
        	shipmentChanged(inoutID);
        	shipmentField.setValue(inoutID);
    		invoiceOrderLabel.setVisible(false);
        	invoiceOrderField.setVisible(false);
        	shipmentField.setVisible(true);
        	shipmentLabel.setVisible(true);
        	if(getInvoice().getC_Order_ID() > 0){
        		orderField.setVisible(false);
        		orderLabel.setVisible(false);
        	}
		}
		
    	// Si la factura ya tiene lineas no se ingresa nada
    	if (getInvoice().getLines().length > 0) {
    		orderField.setReadWrite(false);
    		invoiceOrderField.setReadWrite(false);
    		shipmentField.setReadWrite(false);
    		bPartnerField.setReadWrite(false);
    	}

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    protected void initBPDetails( int C_BPartner_ID ) {
 
    }    // initDetails

    /**
     * Descripción de Método
     *
     *
     * @param M_InOut_ID
     */

    private void loadShipment( int M_InOut_ID ) {
        log.config( "M_InOut_ID=" + M_InOut_ID );
        
        initDataTable();
        
        p_order = null;
        
        if (M_InOut_ID > 0) {
    		m_inout = new MInOut( Env.getCtx(),M_InOut_ID,null );
    		// Se carga la EC del remito.
    		if (bPartnerField != null) {
    			bPartnerField.setValue(m_inout.getC_BPartner_ID());
    		}
            if( m_inout.getC_Order_ID() != 0 ) {
                p_order = new MOrder( Env.getCtx(),m_inout.getC_Order_ID(),null );
            }
        }

        //
        List<SourceEntity> data = new ArrayList<SourceEntity>();
        
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

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = DB.prepareStatement( sql.toString());
            pstmt.setInt( 1,M_InOut_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                InOutLine docLine = new InOutLine();

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

                // Agrega la línea a la lista
                data.add(docLine);
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        } finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}
        filtrarColumnaInstanceName(data);
        loadTable(data);
    }    // loadShipment

    /**
     * Descripción de Método
     *
     */

    protected void info() {
        int count = 0;
        for (SourceEntity sourceEntity : getSourceEntities()) {
			if (sourceEntity.selected) {
				count++; 
			}
		}
        window.setStatusLine(count, "");
    }    // info

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected void save() throws CreateFromSaveException {

    	// Actualiza el encabezado de la factura
        MInvoice invoice = getInvoice();
		invoice.setDragDocumentDiscountAmts(getDocType()
				.isDragOrderDocumentDiscounts());
        log.config( invoice.toString());
        // Asociación con el pedido
        if( p_order != null ) {
            invoice.setOrder( p_order, true );    // overwrite header values
			invoice.setManageDragOrderDiscounts(getDocType()
					.isDragOrderDocumentDiscounts()
					|| getDocType().isDragOrderLineDiscounts());
			invoice.setIsExchange(p_order.isExchange());
            if (!invoice.save()) {
            	throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
            }
        }
        // Asocia el remito con la factura si es que se está creando a partir
        // de un remito
        if( (m_inout != null) && (m_inout.getM_InOut_ID() != 0) && (m_inout.getC_Invoice_ID() == 0)) {    // only first time
            m_inout.setC_Invoice_ID(invoice.getC_Invoice_ID());
            if (!m_inout.save(getTrxName())) {
            	throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
            }
        }

        // Lines

        for (SourceEntity sourceEntity : getSelectedSourceEntities()) {

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
			invoiceLine.setDragDocumentDiscountAmts(getDocType()
					.isDragOrderDocumentDiscounts());
			invoiceLine.setDragLineDiscountAmts(getDocType()
					.isDragOrderLineDiscounts());
			invoiceLine.setDragOrderPrice(getDocType().isDragOrderPrice());
			
            // Info
            MOrderLine orderLine = null;

            if( C_OrderLine_ID != 0 ) {
                orderLine = new MOrderLine( Env.getCtx(),C_OrderLine_ID, getTrxName());
            }

            MInOutLine inoutLine = null;

            if( M_InOutLine_ID != 0 ) {
                inoutLine = new MInOutLine( Env.getCtx(),M_InOutLine_ID, getTrxName());

                if( (orderLine == null) && (inoutLine.getC_OrderLine_ID() != 0) ) {
                    C_OrderLine_ID = inoutLine.getC_OrderLine_ID();
                    orderLine      = new MOrderLine( Env.getCtx(),C_OrderLine_ID, getTrxName());
                }
            } else {
                MInOutLine[] lines = MInOutLine.getOfOrderLine( Env.getCtx(),C_OrderLine_ID,null,getTrxName());

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
                customMethod(inoutLine,invoiceLine);
                
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
                customMethod(orderLine,invoiceLine);

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
        if(getDocType().isDragOrderDocumentDiscounts() && p_order != null){
			try{
				invoice.updateTotalDocumentDiscount();	
			} catch(Exception e){
				throw new CreateFromSaveException(e.getMessage());
			}
        }
    }    // saveInvoice

    @Override
    protected String getRemainingQtySQLLine(boolean forInvoice, boolean allowDeliveryReturns){
    	String sqlLine = super.getRemainingQtySQLLine(forInvoice, allowDeliveryReturns);
    	if(!forInvoice){
    		sqlLine = " (CASE WHEN (l.QtyOrdered - l.QtyDelivered - l.QtyTransferred) > l.QtyInvoiced THEN l.QtyInvoiced ELSE (l.QtyOrdered - l.QtyDelivered- l.QtyTransferred) END) ";
    	}
    	return sqlLine;
    }
    
	/**
	 * Inicializa la condición que determina si es para una factura o no. En
	 * realidad es para una factura, pero esto también determina algunos filtros
	 * y datos a mostrar, por lo que si es un crédito para cliente o proveedor
	 * entonces es devolución de mercadería
	 */
    private void initIsForInvoice(){
		// Es para factura, pero si estamos por crear una NC para el cliente o
		// proveedor con un pedido asociado me tengo que guiar por lo que queda
		// entregar, no por lo facturado, ya que si se realiza una NC a un
		// cliente de una factura que contiene un pedido entonces significa que
		// se le debe devolver plata. Para determinar que estoy sobre una NC
		// debo verificar el tipo de documento base del tipo de documento
		// configurado en la factura. El tipo de doc. base debe ser Abono de
		// Cliente o Abono de Proveedor
    	MInvoice invoice = getInvoice();
		setDocType(new MDocType(getCtx(), invoice.getC_DocTypeTarget_ID(),
				getTrxName()));
		isForInvoice = !getDocType().getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo)
				&& !getDocType().getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);
    }
    
	@Override
	protected boolean isForInvoice() {
		if(isForInvoice == null)
			initIsForInvoice();
		return isForInvoice;
	}

	@Override
	protected void orderChanged(int orderID) {
		// set Shipment to Null
		shipmentField.setValue(null);
		invoiceOrderField.setValue(null);
		loadOrder( orderID,isForInvoice(), allowDeliveryReturned(),true );
	}
	
	/**
	 * @return Devuelve la factura origen de este CreateFrom
	 */
	public MInvoice getInvoice() {
		if (invoice == null) {
			invoice = new MInvoice(getCtx(), (Integer)p_mTab.getValue("C_Invoice_ID"), null);
		}
		invoice.set_TrxName(getTrxName());
		return invoice;
	}
	
	@Override
	protected boolean beforeAddOrderLine(OrderLine orderLine) {
		// Si la línea de pedido ya está asociada con alguna línea de la factura entonces
		// no debe ser mostrada en la grilla. No se permite que dos líneas de una misma
		// factura compartan una línea del pedido. Todo en el caso que no se
		// esté creando una factura sino un crédito
		String sql = 
			"SELECT COUNT(*) FROM C_InvoiceLine WHERE C_Invoice_ID = ? AND C_OrderLine_ID = ?";
		Long count = (Long)DB.getSQLObject(null, sql, 
				new Object[] { getInvoice().getC_Invoice_ID(), orderLine.orderLineID }
		);
		return (count == null || count == 0);
	}
	
	/**
	 * Inicializa el lookup de remitos
	 */
	private void initShipmentLookup() {
    	String whereClause = getShipmentFilter();     	
    	/* FEDE:TODO: ESTO DEBERIA REFACTORIZARSE A OTRO LUGAR */
    	int colID = 3521; 	// M_InOut.M_InOut_ID
    	MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),p_WindowNo,p_mTab.getTabNo(),colID,DisplayType.Search, whereClause );
    	info.ZoomQuery = new MQuery();
    	MLookup lookup       = new MLookup(info, p_mTab.getTabNo());
    	if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
    	/* */
    	shipmentField = new WSearchEditor("M_InOut_ID", true, false, true, lookup);
    	
    	shipmentField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent e) {
				Integer inOutID = (Integer)e.getNewValue();
				shipmentChanged(inOutID == null ? 0 : inOutID);
			}
		});
	}
	
	
	/**
	 * Inicializa el lookup de facturas con pedidos asociados.
	 */
	private void initInvoiceOrderLookup() {
    	String whereClause = getInvoiceOrderFilter(); 
    	/* FEDE:TODO: ESTO DEBERIA REFACTORIZARSE A OTRO LUGAR */
    	int colID = 3484; 	// C_Invoice.C_Invoice_ID
    	MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),p_WindowNo,p_mTab.getTabNo(),colID,DisplayType.Search, whereClause );
    	info.ZoomQuery = new MQuery();
    	MLookup lookup       = new MLookup(info, p_mTab.getTabNo());
    	if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
    	/* */
    	invoiceOrderField = new WSearchEditor("C_Invoice_ID", true, false, true, lookup);

    	
    	invoiceOrderField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent e) { 
				Integer invoiceID = (Integer)e.getNewValue();
				invoiceOrderChanged(invoiceID == null ? 0 : invoiceID);
			}
		});
	}
	
	/**
     * Este método es invocado cuando el usuario cambia la factura con pedido seleccionada
     * en el VLookup. Las subclases puede sobrescribir este comportamiento.
	 * @param invoiceID ID de la nueva factura seleccionada.
     */
	protected void invoiceOrderChanged(int invoiceID) {
        int relatedOrderID = 0;
		orderField.setValue(null);
		shipmentField.setValue(null);
        if (invoiceID > 0) {
        	MInvoice invoice = new MInvoice(getCtx(), invoiceID, getTrxName());
        	relatedOrderID = invoice.getC_Order_ID();
        }
        loadOrder(relatedOrderID, false, allowDeliveryReturned(), true);
        if (relatedOrderID > 0) {
        	orderField.setValue(relatedOrderID);
        }
	}
	
	
	/**
	 * @return Devuelve el filtro que se aplica al Lookup de Facturas asociadas
	 * a pedidos.
	 */
	protected String getInvoiceOrderFilter() {
    	StringBuffer filter = new StringBuffer();

     	filter
	     	.append("C_Invoice.IsSOTrx='").append(getIsSOTrx()).append("' AND ")
	     	.append("C_Invoice.DocStatus IN ('CL','CO') AND ") 
	     	.append("C_Invoice.C_Order_ID IS NOT NULL AND ")
	     	.append("C_Invoice.C_Order_ID IN (")
	     	.append(   "SELECT C_Order.C_Order_ID ") 
	     	.append(   "FROM C_Order ")
	     	.append(   "WHERE (")
	     	.append(   getOrderFilter()).append(")")
	     	.append(")");
     	   	
     	return filter.toString();
	}
	
	/**
	 * @return Devuelve el filtro que se aplica al Lookup de remitos.
	 */
	protected String getShipmentFilter() {
    	StringBuffer filter = new StringBuffer();

     	filter
	     	.append("M_InOut.IsSOTrx='").append(getIsSOTrx()).append("' AND ")
	     	.append("M_InOut.DocStatus IN ('CL','CO') AND ") 
	     	.append("M_InOut.M_InOut_ID IN (")
	     	.append(   "SELECT sl.M_InOut_ID ")
	     	.append(   "FROM M_InOutLine sl ")
	     	.append(   "LEFT OUTER JOIN M_MatchInv mi ON (sl.M_InOutLine_ID=mi.M_InOutLine_ID) ")
	     	.append(   "GROUP BY sl.M_InOut_ID,mi.M_InOutLine_ID,sl.MovementQty ")
	     	.append(   "HAVING (sl.MovementQty<>SUM(mi.Qty) AND mi.M_InOutLine_ID IS NOT NULL) OR mi.M_InOutLine_ID IS NULL) ");
     	
     	return filter.toString();
	}
	
    /**
     * Este método es invocado cuando el usuario cambia el remito seleccionado
     * en el VLookup. Las subclases puede sobrescribir este comportamiento.
	 * @param shipmentID ID del nuevo remito seleccionado.
     */
	protected void shipmentChanged(int shipmentID) {
        orderField.setValue(null);
        invoiceOrderField.setValue(null);
        loadShipment(shipmentID);
	}
	
	/**
	 * Entidad Orígen: Línea de Remito
	 */
	protected class InOutLine extends DocumentLine {
		/** ID de la línea de remito */
		protected int inOutLineID = 0;
		/** La línea de remito puede tener asociada a su vez una línea de pedido */
		protected int orderLineID = 0;

		@Override
		public boolean isInOutLine() {
			return true;
		}

	}

	@Override
	protected void customizarPanel() {
		// TODO Auto-generated method stub
		
	}

	protected void setDocType(MDocType docType) {
		this.docType = docType;
	}

	protected MDocType getDocType() {
		return docType;
	}
	
	public MInOut getM_inout() {
		return m_inout;
	}

	public void setM_inout(MInOut m_inout) {
		this.m_inout = m_inout;
	}
	
	// El siguiente metodo podrá ser redefinido por un plugin para agregar una funcionalidad particular.
	// El metodo es invocado antes de hacer el save de la linea
	protected void customMethod(PO ol, PO iol) {
	}

	@Override
	protected boolean lazyEvaluation() {
		return false;
	}

	public void showWindow()
	{
		window.setVisible(true);
	}
	
	public void closeWindow()
	{
		window.dispose();
	}
	
}
