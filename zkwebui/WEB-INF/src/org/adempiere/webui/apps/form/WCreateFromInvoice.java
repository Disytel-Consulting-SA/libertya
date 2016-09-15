package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Checkbox;
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
import org.openXpertya.util.Msg;
import org.zkoss.zk.ui.event.Event;

public class WCreateFromInvoice extends WCreateFrom {

    protected WCreateFromInvoice( MTab mTab ) {
        super( mTab );
        log.info( mTab.toString());
		p_WindowNo = mTab.getWindowNo();
		AEnv.showWindow(window);
    }    
	
    /** Descripción de Campos */
    private MInOut m_inout = null;
    
    //protected Checkbox allInOut;
    
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
				// Si el check allOrder esta seleccionado se muestran todos los
				// pedidos del proveedor
				// que fue seleccionado.
				if (allInOut != null && allInOut.isSelected()) {
					bPartnerField.setValue(evt.getNewValue());
					showAllOrder();
				}
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
        	getHelper().setPaymentRule(invoice.getPaymentRule());
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

	// El método agrega el check al panel parameterStdPanel
	@Override
	protected void customizarPanel() {
		// Si es Perfil Compras agrego el check Ver todos los pedidos
		if (!isSOTrx()) {
			allInOut = new Checkbox();
			allInOut.setText("Ver todos los pedidos");
			allInOut.addActionListener(this);
			window.getParameterPanel().appendChild(allInOut);
		}

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
	
	public void onEvent(Event e) throws Exception {
		log.config("Action=" + e.getName());

		// Si selecciona el check "Seleccionar
		if (e.getTarget().equals(allInOut))
			showAllOrder();
		else
			super.onEvent(e);

	} // actionPerformed

	
	// El siguiente método limpia los campos (invoiceField, orderField,
	// invoiceField) y los desactiva cuando
	// se selecciona el check "Seleccionar todos los pedidos".
	public void activeDesactiveField(boolean state) {
		if (!state) {
			shipmentField.setValue("");
			orderField.setValue("");
			invoiceOrderField.setValue("");
		}
		orderField.setReadWrite(state);
		invoiceOrderField.setReadWrite(state);
		shipmentField.setReadWrite(state);
	}
	
	// El método muestra todos los pedidos del proveedor seleccionado
		public void showAllOrder() {
			activeDesactiveField(!allInOut.isSelected());
			if (allInOut.isSelected()) {
				StringBuffer sql;

				// La consulta obtiene los pedidos, del proveedor seleccionado
				// aplicando el filtro getOrderFilter.
				sql = new StringBuffer();
				sql.append(
						"select * from C_Order where C_BPartner_ID = "
								+ bPartnerField.getValue() + " AND ").append(
						getOrderFilter());

				log.finer(sql.toString());

				PreparedStatement pstmt = null;
				ResultSet rs = null;

				try {
					pstmt = DB.prepareStatement(sql.toString());
					rs = pstmt.executeQuery();
					List<OrderLine> dataAux = new ArrayList<OrderLine>();
					// Itero por cada uno de los pedidos almacenando en dataAux
					// todas las lineas de todos los pedidos retornados en
					// la consulta anterior.
					while (rs.next()) {
						loadOrder(rs.getInt("C_Order_ID"), isForInvoice(), allowDeliveryReturned(), false);
						List<OrderLine> data = (List<OrderLine>) ((CreateFromTableModel) window.getDataTable().getModel()).getSourceEntities();
						Iterator<? extends SourceEntity> it = data.iterator();
						while (it.hasNext()) {
							dataAux.add((OrderLine) it.next());
						}
					}
					initDataTable();
					filtrarColumnaInstanceName(dataAux);
					loadTable(dataAux);

				} catch (Exception e) {
					log.log(Level.SEVERE, sql.toString(), e);
				} finally {
					try {
						if (rs != null)
							rs.close();
						if (pstmt != null)
							pstmt.close();
					} catch (Exception e) {
					}
				}
			}
			else {
				// Debe inicializarse nuevamente a fin de que se vacie la grilla
				initDataTable();
				window.tableChanged(null);
			}
		}
		
		// Si el check de mostrar todos los pedidos esta seleccionado crea el modelo
		// de tabla para líneas de remitos.
		// En caso contratio crea el modelo de tabla para líneas de documentos
		// (Pedidos, Remitos, Facturas)
		protected CreateFromTableModel createTableModelInstance() {
			if (!isSOTrx() && allInOut != null && allInOut.isSelected()) {
				return new DocumentLineTableModelFromInvoice();
			} else {
				return new DocumentLineTableModel();
			}
		}
		
		/**
		 * Modelo de tabla para líneas de pedidos.
		 */
		public class DocumentLineTableModelFromInvoice extends
				DocumentLineTableModel {
			// Constantes de índices de las columnas en la grilla.
			// Constantes de índices de las columnas en la grilla.
			public static final int COL_IDX_LINE = 3;
			public static final int COL_IDX_ITEM_CODE = 4;
			public static final int COL_IDX_PRODUCT = 5;
			public static final int COL_IDX_UOM = 7;
			public static final int COL_IDX_QTY = 8;
			public static final int COL_IDX_REMAINING = 9;

			public static final int COL_IDX_ORDER = 1;
			public static final int COL_IDX_DATE = 2;
			public static final int COL_IDX_INSTANCE_NAME = 6;
			
			public int visibles = 10;

			@Override
			protected void setColumnNames() {
				setColumnName(COL_IDX_LINE, Msg.getElement(getCtx(), "Line"));
				setColumnName(COL_IDX_ITEM_CODE,
						Msg.translate(Env.getCtx(), "Value"));
				setColumnName(COL_IDX_PRODUCT,
						Msg.translate(Env.getCtx(), "M_Product_ID"));
				setColumnName(COL_IDX_UOM, Msg.translate(Env.getCtx(), "C_UOM_ID"));
				setColumnName(COL_IDX_QTY, Msg.translate(Env.getCtx(), "Quantity"));
				setColumnName(COL_IDX_REMAINING,
						Msg.translate(Env.getCtx(), "RemainingQty"));

				setColumnName(COL_IDX_ORDER, Msg.getElement(getCtx(), "C_Order_ID"));
				setColumnName(COL_IDX_INSTANCE_NAME,
						Msg.translate(Env.getCtx(), "Attributes"));
				setColumnName(COL_IDX_DATE,
						Msg.translate(Env.getCtx(), "DateOrdered"));
			}

			@Override
			protected void setColumnClasses() {
				setColumnClass(COL_IDX_LINE, Integer.class);
				setColumnClass(COL_IDX_ITEM_CODE, String.class);
				setColumnClass(COL_IDX_PRODUCT, String.class);
				setColumnClass(COL_IDX_UOM, String.class);
				setColumnClass(COL_IDX_QTY, BigDecimal.class);
				setColumnClass(COL_IDX_REMAINING, BigDecimal.class);

				setColumnClass(COL_IDX_ORDER, String.class);
				setColumnClass(COL_IDX_INSTANCE_NAME, String.class);
				setColumnClass(COL_IDX_DATE, Date.class);
			}

			@Override
			public Object getValueAt(int rowIndex, int colIndex) {
				OrderLine docLine = (OrderLine)getDocumentLine(rowIndex);
				Object value = null;
				switch (colIndex) {
				case COL_IDX_LINE:
					value = docLine.lineNo;
					break;
				case COL_IDX_ITEM_CODE:
					value = docLine.itemCode;
					break;
				case COL_IDX_PRODUCT:
					value = docLine.productName;
					break;
				case COL_IDX_UOM:
					value = docLine.uomName;
					break;
				case COL_IDX_QTY:
					value = docLine.lineQty;
					break;
				case COL_IDX_REMAINING:
					value = docLine.remainingQty;
					break;
				case COL_IDX_ORDER:
					value = docLine.documentNo;
					break;
				case COL_IDX_INSTANCE_NAME:
					value = docLine.instanceName;
					break;
				case COL_IDX_DATE:
					value = docLine.dateOrderLine;
					break;
				default:
					value = super.getValueAt(rowIndex, colIndex);
					break;
				}
				return value;
			}

			@Override
			public int getColumnCount() {
				return visibles;
			}
		}
	
}
