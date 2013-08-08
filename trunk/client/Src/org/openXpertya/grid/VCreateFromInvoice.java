/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.grid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.DocumentLine;
import org.openXpertya.grid.CreateFromModel.InOutLine;
import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VCreateFromInvoice extends VCreateFrom {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     */

    protected VCreateFromInvoice( MTab mTab ) {
        super( mTab );
        log.info( mTab.toString());
    }    // VCreateFromInvoice

    /** Descripción de Campos */
    private MInOut m_inout = null;
    
    /** Factura que invoca este Crear Desde */
    private MInvoice invoice = null;
    
	/** Este crear desde lleva la lógica de facturas o la lógica de remitos? */
    private Boolean isForInvoice = null;
    
    /** Tipo de Documento a crear */
    private MDocType docType;

    /** Helper para centralizar lógica de modelo */
	protected CreateFromInvoiceModel helper = null;
    
	protected CreateFromInvoiceModel getHelper() {
		if (helper == null)
			helper = new CreateFromInvoiceModel();
		return helper;
	}

	
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
        setTitle( Msg.getElement( Env.getCtx(),"C_Invoice_ID",false ) + " .. " + Msg.translate( Env.getCtx(),"CreateFrom" ));
        
        initShipmentLookup();
        
        initInvoiceOrderLookup();
        
        parameterBankPanel.setVisible( false );
        invoiceLabel.setVisible( false );
        locatorLabel.setVisible( false );
        locatorField.setVisible( false );
        initBPartner( true );
        bPartnerField.addVetoableChangeListener(new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
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
        StringBuffer sql = getHelper().loadShipmentQuery();
        List<SourceEntity> data = new ArrayList<SourceEntity>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	pstmt = DB.prepareStatement(sql.toString());
            pstmt.setInt( 1,M_InOut_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                InOutLine docLine = new InOutLine();
                getHelper().loadShipmentLine(docLine, rs);
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
        statusBar.setStatusLine(String.valueOf(count));
    }    // info

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected void save() throws CreateFromSaveException {
    	getHelper().save(p_order, getInvoice(), m_inout, getDocType(), getSelectedSourceEntities(), getTrxName(), this);
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
		return CreateFromInvoiceModel.beforeAddOrderLine(orderLine, getInvoice());
	}
	
	/**
	 * Inicializa el lookup de remitos
	 */
	private void initShipmentLookup() {
    	String whereClause = CreateFromInvoiceModel.getShipmentFilter(getIsSOTrx()); 
    	shipmentField = VComponentsFactory.VLookupFactory("M_InOut_ID", "M_InOut", p_WindowNo, DisplayType.Search, whereClause, false);
    	shipmentField.addVetoableChangeListener(new VetoableChangeListener() {
			
			@Override
			public void vetoableChange(PropertyChangeEvent e)
					throws PropertyVetoException {
				Integer inOutID = (Integer)e.getNewValue();
				shipmentChanged(inOutID == null ? 0 : inOutID);
			}
		});
	}
	
	
	/**
	 * Inicializa el lookup de facturas con pedidos asociados.
	 */
	private void initInvoiceOrderLookup() {
    	String whereClause = CreateFromInvoiceModel.getInvoiceOrderFilter(getIsSOTrx(), getOrderFilter()); 
    	invoiceOrderField = VComponentsFactory.VLookupFactory("C_Invoice_ID", "C_Invoice", p_WindowNo, DisplayType.Search, whereClause, false);
    	invoiceOrderField.addVetoableChangeListener(new VetoableChangeListener() {
			
			@Override
			public void vetoableChange(PropertyChangeEvent e)
					throws PropertyVetoException {
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
     * Este método es invocado cuando el usuario cambia el remito seleccionado
     * en el VLookup. Las subclases puede sobrescribir este comportamiento.
	 * @param shipmentID ID del nuevo remito seleccionado.
     */
	protected void shipmentChanged(int shipmentID) {
        orderField.setValue(null);
        invoiceOrderField.setValue(null);
        loadShipment(shipmentID);
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
	
	public void customMethod(PO ol, PO iol) {
	}

	@Override
	protected boolean lazyEvaluation() {
		return false;
	}

}    // VCreateFromInvoice



/*
 *  @(#)VCreateFromInvoice.java   02.07.07
 * 
 *  Fin del fichero VCreateFromInvoice.java
 *  
 *  Versión 2.2
 *
 */
