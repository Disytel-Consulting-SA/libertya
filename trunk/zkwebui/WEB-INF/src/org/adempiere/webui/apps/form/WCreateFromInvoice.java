package org.adempiere.webui.apps.form;

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
import org.openXpertya.grid.CreateFromInvoiceModel;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.InOutLine;
import org.openXpertya.grid.CreateFromModel.ListedSourceEntityInterface;
import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

public class WCreateFromInvoice extends WCreateFrom {

    protected WCreateFromInvoice( MTab mTab ) {
        super( mTab );
        log.info( mTab.toString());
		p_WindowNo = mTab.getWindowNo();
		AEnv.showWindow(window);
    }    
	
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
        StringBuffer sql = getHelper().loadShipmentQuery();
        List<SourceEntity> data = new ArrayList<SourceEntity>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {        	
            pstmt = DB.prepareStatement(sql.toString());
            pstmt.setInt( 1,M_InOut_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                InOutLineListImpl docLine = new InOutLineListImpl();
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
    	String whereClause = CreateFromInvoiceModel.getInvoiceOrderFilter(getIsSOTrx(), getOrderFilter()); 
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
	protected class InOutLineListImpl extends InOutLine implements ListedSourceEntityInterface {
		/**
		 * Convierte a ArrayList el SourceEntityListImpl para que el mismo pueda ser cargado en el WListBox 
		 */
		public ArrayList<Object> toList() {
			ArrayList<Object> result = new ArrayList<Object>();
			CreateFromTableModel model = (CreateFromTableModel)window.getDataTable().getModel();
			for (int i=0; i < model.getColumnCount(); i++ ) {
				Object value = null;
				switch (i) {
					case DocumentLineTableModel.COL_IDX_SELECTION:
						value = selected; break;
					case DocumentLineTableModel.COL_IDX_LINE:
						value = lineNo; break;
					case DocumentLineTableModel.COL_IDX_ITEM_CODE:
						value = itemCode; break;
					case DocumentLineTableModel.COL_IDX_PRODUCT:
						value = new KeyNamePair(productID, productName) ; break;
					case DocumentLineTableModel.COL_IDX_UOM:
						value = new KeyNamePair(uomID, uomName) ; break;
					case DocumentLineTableModel.COL_IDX_QTY:
						value = lineQty; break;
					case DocumentLineTableModel.COL_IDX_REMAINING:
						value = remainingQty; break;
					case DocumentLineTableModel.COL_IDX_INSTANCE_NAME:
						value = instanceName; break;
					default:
						value = null; break;
				}
				result.add(value);
			}
			return result;
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
	public void customMethod(PO ol, PO iol) {
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
