package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.form.WCreateFromShipment.DocumentLineTableModelFromShipment;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WLocatorEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.plugin.common.PluginWCreateFromUtils;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.CreateFromModel;
import org.openXpertya.grid.CreateFromModel.CreateFromPluginInterface;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.DocumentLine;
import org.openXpertya.grid.CreateFromModel.ListedSourceEntityInterface;
import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.grid.VCreateFrom;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Space;

public abstract class WCreateFrom extends ADForm implements EventListener, CreateFromPluginInterface {

	@Override
	protected void initForm() {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param mTab
     *
     * @return
     */

    public static WCreateFrom create( MTab mTab ) {

        // dynamic init preparation
        int AD_Table_ID = Env.getContextAsInt( Env.getCtx(),mTab.getWindowNo(),"BaseTable_ID" );
        WCreateFrom retValue = null;

        // Busca una definición de CreateFrom en los plugins activos
        retValue = PluginWCreateFromUtils.getCreateFrom(mTab);
        
        if (retValue != null) {
        	; // Devuelve retValue que es la instancia del CreateFrom del plugin
        } else if( AD_Table_ID == 392 ) {    // C_BankStatement
            retValue = new WCreateFromStatement( mTab );
        } else if( AD_Table_ID == 318 ) {    // C_Invoice
            retValue = new WCreateFromInvoice( mTab );
        } else if( AD_Table_ID == 319 ) {    // M_InOut
            retValue = new WCreateFromShipment( mTab );
        } else if( AD_Table_ID == 426 ) {    // C_PaySelection
            return null;                     // ignore - will call process C_PaySelection_CreateFrom
        } else                               // Not supported CreateFrom
        {
            log.log( Level.SEVERE,"Unsupported AD_Table_ID=" + AD_Table_ID );

            return null;
        }

        return retValue;
    }    // create

    /**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     */

    protected WCreateFrom( MTab mTab ) {
        log.info( mTab.toString());
        p_WindowNo = mTab.getWindowNo();
        p_mTab     = mTab;
		window = new WCreateFromWindow(this, mTab.getWindowNo());
		window.setTitle(Msg.translate( Env.getCtx(),"CreateFrom"));

        try {
        	initOrderLookup();
        	
        	if(lazyEvaluation()){
        		initDataTable();
        	}
        	
        	if( !dynInit()) {
                return;
            }

            zkInit();

            if(!lazyEvaluation()){
            	initDataTable();
            }
            p_initOK = true;
        } catch( Exception e ) { 
            log.log( Level.SEVERE,"",e );
            p_initOK = false;
        }
    }    // VCreateFrom

	/** Descripción de Campos */

    protected int p_WindowNo;

    /** Descripción de Campos */

    protected MTab p_mTab;

    /** Descripción de Campos */

    protected boolean p_initOK = false;

    /** Descripción de Campos */

    protected MOrder p_order = null;

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( VCreateFrom.class );

    /** Descripción de Campos */

    protected Label bankAccountLabel = new Label();
    
    protected WStringEditor nroLote = new WStringEditor();
    
    protected Checkbox agrupacionporcupones = new Checkbox();

    /** Descripción de Campos */

    private Label bPartnerLabel = new Label();

    /** Descripción de Campos */

    protected WSearchEditor bankAccountField;

    protected Checkbox automatico = new Checkbox();

    /** Descripción de Campos */

    protected WSearchEditor bPartnerField;

    /** Descripción de Campos */

    protected Label orderLabel = new Label();

    /** Descripción de Campos */

    protected WSearchEditor orderField = null;
    
    /** Label Pedido de factura */
    protected Label invoiceOrderLabel = new Label();
    
    /** Pedido de una factura */
    protected WSearchEditor invoiceOrderField = null;

    /** Descripción de Campos */

    protected Label invoiceLabel = new Label();

    /** Descripción de Campos */

    protected WSearchEditor invoiceField = null;

    /** Descripción de Campos */

    protected Label shipmentLabel = new Label();

    /** Descripción de Campos */

    protected WSearchEditor shipmentField = null;

    /** Descripción de Campos */

    protected Label locatorLabel = new Label();

    /** Descripción de Campos */

    protected WLocatorEditor locatorField = new WLocatorEditor();
    
    /** Nombre de la transacción para hacer el save */
    private String trxName = null;

    protected WCreateFromWindow window;

    protected boolean m_actionActive = false;
    
	protected Checkbox allInOut;
	
    /** Helper para centralizar lógica de modelo */
	protected CreateFromModel helper = new CreateFromModel();

	
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void zkInit() throws Exception {
        bankAccountLabel.setText( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));
        nroLote.getLabel().setText("Nro de Lote");
        nroLote.setValue("");
        nroLote.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent e) {
				filtrar();
			}
		});
        agrupacionporcupones.setText("Agrupación por cupones");
        agrupacionporcupones.addActionListener(new EventListener() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				filtrar();
			}
        });
        bPartnerLabel.setText( Msg.getElement( Env.getCtx(),"C_BPartner_ID" ));
        orderLabel.setText( Msg.getElement( Env.getCtx(),"C_Order_ID",false ));
        invoiceLabel.setText( Msg.getElement( Env.getCtx(),"C_Invoice_ID",false ));
        shipmentLabel.setText( Msg.getElement( Env.getCtx(),"M_InOut_ID",false ));
        locatorLabel.setText( Msg.translate( Env.getCtx(),"M_Locator_ID" ));
        automatico.setText("Seleccionar todos");
		automatico.addActionListener(this);
		invoiceOrderLabel.setText(Msg.translate(getCtx(), "InvoiceOrder"));
		
		Borderlayout parameterLayout = new Borderlayout();
		parameterLayout.setHeight("110px");
		parameterLayout.setWidth("100%");
    	Panel parameterPanel = window.getParameterPanel();
		parameterPanel.appendChild(parameterLayout);
        	
		Grid parameterStdLayout = GridFactory.newGridLayout();
    	Panel parameterStdPanel = new Panel();
		parameterStdPanel.appendChild(parameterStdLayout);

		Center center = new Center();
		parameterLayout.appendChild(center);
		center.appendChild(parameterStdPanel);
		
		Rows rows = (Rows) parameterStdLayout.newRows();
		Row row = rows.newRow();
		if (bPartnerField != null) {
			row.appendChild(bPartnerLabel.rightAlign());
			row.appendChild(bPartnerField.getComponent());
		}
		if (orderField!=null) {
			row.appendChild(orderLabel.rightAlign());
			row.appendChild(orderField.getComponent());
		}
		
		row = rows.newRow();
		row.appendChild(new Space());
		row.appendChild(new Space());

		if (invoiceOrderField!=null) {
			row.appendChild(invoiceOrderLabel.rightAlign());
			row.appendChild(invoiceOrderField.getComponent());
		}
        
		row = rows.newRow();
		if (locatorField!=null && locatorField.isVisible()) {
			row.appendChild(locatorLabel.rightAlign());
			row.appendChild(locatorField.getComponent());
		}
		else {
			row.appendChild(new Space());
			row.appendChild(new Space());
		}
		if (invoiceField!=null) {
			row.appendChild(invoiceLabel.rightAlign());
			row.appendChild(invoiceField.getComponent());
		}
		if (shipmentField!=null) {
			row.appendChild(shipmentLabel.rightAlign());
			row.appendChild(shipmentField.getComponent());
		}
		
        customizarPanel();
    }    // jbInit

    protected void filtrar() {
		//Acá filtra
	}

	// El siguiente método permite customizar el panel en las correspondientes subclases.
    protected abstract void customizarPanel();

	/**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInitOK() {
        return p_initOK;
    }    // isInitOK

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    abstract boolean dynInit() throws Exception;

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    abstract void initBPDetails( int C_BPartner_ID );



    /**
     * Descripción de Método
     *
     *
     * @return
     */

    abstract void save() throws CreateFromSaveException; 
   

    /**
     * Descripción de Método
     *
     *
     * @param forInvoice
     *
     * @throws Exception
     */

    protected void initBPartner( boolean forInvoice ) throws Exception {

        // load BPartner

        int     AD_Column_ID = 3499;    // C_Invoice.C_BPartner_ID
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.Search );
		bPartnerField = new WSearchEditor("C_BPartner_ID", true, false, true, lookup);

        //

        int C_BPartner_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BPartner_ID" );

        bPartnerField.setValue( new Integer( C_BPartner_ID ));

        bPartnerField.addValueChangeListener( new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent evt) {
				Integer bPartnerID = (Integer)evt.getNewValue();
				// Setea el valor en el contexto para que al abrir el Info
				// se cargue este valor en el lookup de filtro del mismo.
				if (bPartnerID != null && bPartnerID > 0) {
					Env.setContext(getCtx(), p_WindowNo, "C_BPartner_ID", bPartnerID);
				}
				// Se borra la selección de pedido.
				orderField.setValue(null);
				orderChanged(0);
				// Actualiza información y componentes relacionados a la entidad
				// comercial
				updateBPDetails(bPartnerID, true);
			}
		});
        // initial loading

        initBPartnerOIS( C_BPartner_ID,forInvoice );
    }    // initBPartner

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     * @param forInvoice
     */

    protected void initBPartnerOIS( int C_BPartner_ID,boolean forInvoice ) {
        log.config( "C_BPartner_ID=" + C_BPartner_ID );
        initBPDetails( C_BPartner_ID );
    }    // initBPartnerOIS

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     * @param forInvoice
     */

    protected void loadOrder( int C_Order_ID,boolean forInvoice, boolean allowDeliveryReturns, boolean filter ) {
    	log.config( "C_Order_ID=" + C_Order_ID );
    	
    	initDataTable();
    	
    	if (C_Order_ID > 0) {
    		p_order = new MOrder( Env.getCtx(),C_Order_ID,null );    // save
    	}
    	else{
    		p_order = null;
    	}

    	List<OrderLine>       data = new ArrayList<OrderLine>();
    	// La consulta obtiene la líneas del pedido, calculando la cantidad pendiente
    	// directamente desde las cantidades de la línea (QtyOrdered, QtyDelivered, QtyInvoiced).
    	// Se quitó la diferenciación entre IsSOTrx Y o N debido a que MMatchPO actualiza
    	// las cantidades en las líneas de pedido tal como se hace para IsSOTrx = Y.
    	StringBuffer sql = helper.loadOrderQuery(getRemainingQtySQLLine(forInvoice, allowDeliveryReturns));

    	log.finer( sql.toString());

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		pstmt.setInt( 1,C_Order_ID );
    		rs = pstmt.executeQuery();

    		while( rs.next()) {
    			OrderLineListImpl orderLine = new OrderLineListImpl();
    			helper.loadOrderLine(p_order, orderLine, rs);
    			// Agrega la línea a la lista solo si tiene cantidad pendiente o tiene asociado un producto de tipo Gasto.
    			if (beforeAddOrderLine(orderLine) 
    					&& (orderLine.remainingQty.compareTo(BigDecimal.ZERO) > 0 || orderLine.productType.equals("E"))) {
    				data.add(orderLine);
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
    	
    	if(filter){
    		filtrarColumnaInstanceName(data);
    	}
    	
    	loadTable(data);
    	// Se carga la EC del pedido.
		if (bPartnerField != null && p_order != null) {
			if(bPartnerField.getValue() == null 
					|| (Integer)bPartnerField.getValue() != p_order.getC_BPartner_ID()){
				bPartnerField.setValue(p_order.getC_BPartner_ID());
				updateBPDetails(p_order.getC_BPartner_ID(), true);
			}
		}
    }    // LoadOrder

	/**
	 * @param forInvoice
	 * @return la línea del sql que determina la cantidad a facturar
	 */
    protected String getRemainingQtySQLLine(boolean forInvoice, boolean allowDeliveryReturns){
    	return helper.getRemainingQtySQLLine(forInvoice, allowDeliveryReturns);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param data
     */
    
     protected void loadTable(List<? extends SourceEntity> data) {
        // Se obtiene el modelo de la tabla y se asigna la nueva lista de líneas de documento.
    	((CreateFromTableModel)window.getDataTable().getModel()).clear();
    	((CreateFromTableModel)window.getDataTable().getModel()).setSourceEntities(data);
    	window.getDataTable().autoSize();
        loadTableOIS(data);
    }    // loadOrder
        
    /**
     * @return Devuelve un booleano que representa el valor de IsSOTrx 
     * para la pestaña.
     */
    protected boolean isSOTrx() {
    	return "Y".equals(getIsSOTrx());
    }
    
    /**
     * @return Devuelve el valor de IsSOTrx para la pestaña
     */
    protected String getIsSOTrx() {
    	return Env.getContext(Env.getCtx(), p_mTab.getWindowNo(), p_mTab.getTabNo(), "IsSOTrx");
    }
    
    /**
     * Inicializa el lookup de Pedidos
     */
    private void initOrderLookup() {
    	String whereClause = getOrderFilter(); 
    	/* FEDE:TODO: ESTO DEBERIA REFACTORIZARSE A OTRO LUGAR */
    	int colID = 2161; 	// C_Order.C_Order_ID
    	MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),p_WindowNo,p_mTab.getTabNo(),colID,DisplayType.Search, whereClause );
    	info.ZoomQuery = new MQuery();
    	MLookup lookup       = new MLookup(info, p_mTab.getTabNo());
    	if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
    	/* */
    	
    	orderField = new WSearchEditor("C_Order_ID", true, false, true, lookup);
    	orderField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent e) {
				Integer orderID = (Integer)e.getNewValue();
				orderChanged(orderID == null ? 0 : orderID);
			}
		});
    }
    
    /**
     * Inicializa la grilla que muestra las líneas del documento origen
     */
    protected void initDataTable() {
        // Crea el modelo de la tabla y asigna este objeto como listener
    	// de sus eventos.
    	CreateFromTableModel model = createTableModelInstance();
        model.addTableModelListener(window);
        // Asigna el modelo al control de la tabla.
        window.getDataTable().setModel(model);
        // Asigna las clases de cada columna para personalizar la presentación y edición
        // de los datos en cada una de ellas.
        for(int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
        	window.getDataTable().setColumnClass(
        		columnIndex, 
        		model.getColumnClass(columnIndex), 
        		!model.isColumnEditable(columnIndex)
        	);
        }

        // Si es Perfil Ventas no se muestra la columna COL_IDX_INSTANCE_NAME
        if (isSOTrx()) {
    		((CreateFromTableModel) window.getDataTable().getModel()).updateColumns();
    	}
    }
    
    /**
     * @return Crea y devuelve la instancia del modelo de tabla a utilizar
     */
    protected CreateFromTableModel createTableModelInstance() {
    	return new DocumentLineTableModel();
    }
    
    /**
     * @return Indica si este Crear Desde es para creación de Facturas.
     * Por defecto este método devuelve <code>false</code>, las subclases
     * que implementen un Crear Desde para Facturas deben sobrescribir este
     * método y devolver <code>true</code>.
     */
    protected boolean isForInvoice() {
    	return false;
    }
    
    /**
	 * @return true si se debe entregar más mercadería de la devuelta, este
	 *         valor va por defecto, falso en caso contrario. Las subclases que
	 *         implementen un Crear Desde para remitos deben sobrescribir este
	 *         método y devolver falso sea el caso
	 */
    protected boolean allowDeliveryReturned(){
    	return true;
    }
    
    /**
     * @return Devuelve el filtro que se aplica al Lookup de Pedidos.
     * Por defecto este filtro respeta el filtro que se aplicaba en el método
     * {@link #initBPartnerOIS(int, boolean)} para mantener la compatibilidad
     * con otros CreateFroms. Este método se puede sobrescribir para redefinir
     * el filtro según sea necesario.
     */
    protected String getOrderFilter() {
    	return helper.getOrderFilter(isForInvoice(), getIsSOTrx());
    }
    
    /**
     * Este método es invocado cuando el usuario cambia el pedido seleccionado
     * en el VLookup. Por defecto solo invoca {@link #loadOrder(int, boolean)} pero
     * las subclases puede sobrescribir este comportamiento.
     * @param orderID ID del nuevo pedido seleccionado.
     */
    protected void orderChanged(int orderID) {
    	loadOrder(orderID, isForInvoice(), allowDeliveryReturned(),true);
    }
    
    /**
     * @return Devuelve el contexto de la aplicación
     */
    public Properties getCtx() {
    	return Env.getCtx();
    }
    
    /**
     * @return Devuelve la lista con las entidades origen cargadas actualmente
     * en la grilla de la ventana.
     */
    protected List<? extends SourceEntity> getSourceEntities() {
    	if (window!=null)
    		return ((CreateFromTableModel)window.getDataTable().getModel()).getSourceEntities();
    	return new ArrayList();
    }
    
    /**
     * @return Devuelve una lista solo con las líneas de documento que están
     * seleccionadas en la grilla de la ventana.
     */
    public List<? extends SourceEntity> getSelectedSourceEntities() {
    	List<SourceEntity> selected = new ArrayList<SourceEntity>();
    	for (SourceEntity sourceEntity : getSourceEntities()) {
			if (sourceEntity.selected) {
				selected.add(sourceEntity);
			}
		}
    	return selected;
    }
    
    
    
    /**
     * Filtro ejecutado antes de agregar una línea de pedido a la lista de líneas
     * del pedido cargado, que luego se mostrará en la grilla. Por defecto esta
     * implmenetación solo devuelve <code>true</code> indicando que la línea debe 
     * ser agregada. Las subclases deben implementar este método en caso de requerir
     * ciertas validaciones o lógica para determinar si una línea de pedido se
     * muestra o no en la grilla.
     * @param orderLine Línea del pedido que se está por agregar
     * @return <code>true</code> si la línea debe ser agregada a la lista,
     * <code>false</code> si no.
     */
    protected boolean beforeAddOrderLine(OrderLine orderLine) {
    	return true;
    }
    
    /**
	 * Actualización de información cuando se modifica la entidad comercial
	 * 
	 * @param bpartnerID
	 *            id de entidad comercial
	 * @param resetDocument
	 *            elimina la selección del documento en caso que se deba
	 */
    protected void updateBPDetails(Integer bpartnerID, boolean resetDocument){
    	
    }
        

    public abstract class SourceEntityListImpl extends SourceEntity implements ListedSourceEntityInterface  {
    	
    }

    /**
     * Entidad origen: líneas de un documento (Pedido, Remito, Factura).<br><br>
     * 
     * Esta clase es abstracta y contiene los atributos compartidos por una línea
     * de pedido, remito y factura debido a que su estructura es muy similar.
     * 
     */
    protected abstract class DocumentLineListImpl extends DocumentLine implements ListedSourceEntityInterface {
		
    }
    
    /**
     * Entidad Origen: Línea de Pedido<br><br>
     *
     * Clase concreta de una entidad origen que contiene la referencia a una línea
     * de pedido.
     */
    protected class OrderLineListImpl extends OrderLine implements ListedSourceEntityInterface {

		public ArrayList<Object> baseList() {
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
    	
		/**
		 * Esta redefinicion se utiliza solo en el caso de Ver todos los Pedidos para creacion de remitos 
		 */
		public ArrayList<Object> toList() {
			// Si allInOut es nulo o no esta seleccionado, entonces usar la definicion tradicional. TODO: Mejorar
			if (allInOut==null || !allInOut.isSelected())
				return baseList();
			ArrayList<Object> result = new ArrayList<Object>();
			CreateFromTableModel model = (CreateFromTableModel)window.getDataTable().getModel();
			for (int i=0; i < model.getColumnCount(); i++ ) {
				Object value = null;
				switch (i) {
					case DocumentLineTableModelFromShipment.COL_IDX_SELECTION:
						value = selected; break;
					case DocumentLineTableModelFromShipment.COL_IDX_ORDER:
						value = new KeyNamePair(orderLineID, documentNo) ; break;						
					case DocumentLineTableModelFromShipment.COL_IDX_DATE:
						value = dateOrderLine; break;
					case DocumentLineTableModelFromShipment.COL_IDX_LINE:
						value = lineNo; break;
					case DocumentLineTableModelFromShipment.COL_IDX_ITEM_CODE:
						value = itemCode; break;						
					case DocumentLineTableModelFromShipment.COL_IDX_PRODUCT:
						value = new KeyNamePair(productID, productName) ; break;
					case DocumentLineTableModelFromShipment.COL_IDX_UOM:
						value = new KeyNamePair(uomID, uomName) ; break;
					case DocumentLineTableModelFromShipment.COL_IDX_QTY:
						value = lineQty; break;
					case DocumentLineTableModelFromShipment.COL_IDX_REMAINING:
						value = remainingQty; break;
					case DocumentLineTableModelFromShipment.COL_IDX_INSTANCE_NAME:
						value = instanceName; break;
					default:
						value = null; break;
				}
				result.add(value);
			}
			return result;
		}
    }
    
    /**
     * Invoca la operación de guardado de la clase concreta
     * realizando el manejo de transacción de BD.
     */
    boolean doSave() {
        trxName = Trx.createTrxName("VCreateFrom");
        Trx trx = Trx.get(trxName, true);
        try {
        	// Deben existir entidades origen en la grilla
        	if (getSourceEntities().size() == 0) {
        		throw new CreateFromSaveException("@CreateFromNeedSourceEntity@");
        	}
        	// Al menos una entidad origen debe estar seleccionada
        	if (getSelectedSourceEntities().size() == 0) {
        		throw new CreateFromSaveException("@CreateFromEmptySelectionError@");
        	}
        	// Efectua el guardado en la clase concreta.
        	save();
        	// Si todo va bien comitea la transacción.
        	trx.commit();
        	dispose();
        	return true;
        } catch (CreateFromSaveException e) {
			trx.rollback();
			FDialog.error(p_WindowNo, this, "Error", e.getMessage());
			return false;
		} finally {
			trx.close();
		}
    }
        
    /**
	 * @return the trxName
	 */
	public String getTrxName() {
		return trxName;
	}
	
	/**
	 * @return Devuelve el table model de la grilla.
	 */
	protected CreateFromTableModel getDataTableModel() {
		return (CreateFromTableModel)window.getDataTable().getModel();
	}
	
    protected void filtrarColumnaInstanceName(List<? extends SourceEntity> dataAux){
    	// Si es Perfil Compras
    	if (!isSOTrx()) {
    		Iterator<? extends SourceEntity> it = dataAux.iterator();
    		boolean mostrarColumna = false;
    		// Itero por la columna instanceName buscando una celda con algun valor.
    		while( (it.hasNext()) && !mostrarColumna){
    			DocumentLine element = (DocumentLine) it.next(); 
    			mostrarColumna = (element.instanceName != null);
    		}
    		// Si en el recorrido anterior ninguna celda de la columna tenia un valor para la columna
    		// Descripcion (nombre de la instacia) se elimina la columna de la tabla y no se visualiza.
// TODO: Pendiente de migrar. No es de alta prioridad.
//    		if(!mostrarColumna){
//    			if( (window.getDataTable().getModel()) instanceof DocumentLineTableModelFromShipment ){
//    				((DocumentLineTableModelFromShipment)window.getDataTable().getModel()).visibles = ((DocumentLineTableModelFromShipment) window.getDataTable().getModel()).visibles - 1; 
//    				window.getDataTable().getModel().remove(window.getDataTable().getModel().get(DocumentLineTableModelFromShipment.COL_IDX_INSTANCE_NAME));	
//    			}
//    			else{
//    				((DocumentLineTableModel)window.getDataTable().getModel()).visibles = ((DocumentLineTableModel)window.getDataTable().getModel()).visibles - 1; 
//    				window.getDataTable().getModel().remove(window.getDataTable().getModel().get(DocumentLineTableModel.COL_IDX_INSTANCE_NAME));	
//    			}
//    		}
    	}
    }
    
	protected abstract boolean lazyEvaluation();
 
	public abstract void showWindow();
	
	public abstract void closeWindow();

	/**
	 *  Load Order/Invoice/Shipment data into Table
	 *  @param data data
	 */
	protected void loadTableOIS (List<? extends SourceEntity> data)
	{
		window.getWListbox().clear();
		//  Set Model
		CreateFromTableModel model = (CreateFromTableModel)window.getDataTable().getModel();
		for (SourceEntity entity : data) {
			model.add(((ListedSourceEntityInterface)entity).toList());
		}
		window.getWListbox().setData(model, ((DocumentLineTableModel)window.getWListbox().getModel()).getColumnNamesAsList());
		window.getWListbox().autoSize();
	}
	
    // ==========================================================================================================================
    // Table model y demas clases adicionales
    // ==========================================================================================================================

	
	/**
     * Modelo de la Tabla que muestra las Entidades Origen. Esta clase es abstracta
     * y contiene toda la funcionalidad común que necesitarán las subclases concretas.<br><br>
     * 
     * Esta clase solo contiene la columna de Selección (booleano) que compartirán todos los
     * sub modelo en caso de que no sobrescriban la estructura de columnas.<br><br>
     * 
     * Las subclases deberán implementar los siguientes métodos:<br>
     * <ul>
     * <li>{@link CreateFromTableModel#setColumnNames()}</li>
     * <li>{@link CreateFromTableModel#setColumnClasses()}</li>
     * <li>{@link CreateFromTableModel#getValueAt(int, int)}</li>
     * <li>{@link CreateFromTableModel#setValueAt(int, int)} (opcional, solo si alguna columna es editable)</li>
     * </ul>
     */
	public abstract class CreateFromTableModel extends ListModelTable {

		private static final long serialVersionUID = -8392194814827177806L;

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_SELECTION = 0;
    	
    	/** Entidades Origen que muestra la grilla */
    	private List<? extends SourceEntity> sourceEntity;
    	/** Nombres de los encabezados de las columnas */
    	private Map<Integer,String> columnNames;
    	/** Clases de las columnas */
    	private Map<Integer,Class<?>> columnClasses;
    	/** Posibilidad de edición de las columnas */
    	private Map<Integer,Boolean> columnEditable;
    	

        public void sort(Comparator cmpr, boolean ascending)
        { 
            // Se desactiva la posibilidad de ordenamiento debido a que la lógica está "atada"
            // a que el modelo de la tabla sea instancia de DefaultTableModel. Debido a que esta
            // tabla no tiene un DefaultTableModel, se desactiva al posibilidad de ordenar para
            // que no se disparen errores (además no es tan requerida esa funcionalidad aquí).        	
        }
    	
		/**
    	 * Constructor por defecto que no asigna los datos del modelo.
    	 * Luego se deben cargar los datos invocando el método 
    	 * {@link #setSourceEntities(List)}
    	 */
    	public CreateFromTableModel() {
    		super();
            // Crea la Map para los nombres de columnas y asigna el nombre
    		// de la primer columna que es la columna de selección.
    		columnNames = new HashMap<Integer,String>();
    		setColumnName(COL_IDX_SELECTION, Msg.getMsg(getCtx(), "Select"));
    		setColumnNames();
    		
    		// Crea la Map para las clases de columnas y asigna la clase
    		// de la primer columna (selección).
    		columnClasses = new HashMap<Integer, Class<?>>();
    		setColumnClass(COL_IDX_SELECTION, Boolean.class);
    		setColumnClasses();
    		
    		// Crea la Map que contiene la posibilidad de edición de cada
    		// columna. Por defecto, habilita la edición de la columna selección
    		columnEditable = new HashMap<Integer, Boolean>();
    		setColumnEditable(COL_IDX_SELECTION, true);
    		
            // Se crea una lista vacía de entidades origen
            sourceEntity = new ArrayList<SourceEntity>();
    	}
    	
    	/**
    	 * Asigna los nombres de las columnas que tendrá el modelo utilizando el método
    	 * {@link #setColumnName(int, String)}
    	 */
    	protected abstract void setColumnNames();
    	
    	/**
    	 * Asigna el nombre a mostrar para una columna
    	 * @param columnIndex Índice de la columna
    	 * @param name Nombre a asignar
    	 */
    	public void setColumnName(int columnIndex, String name) {
    		getColumnNames().put(columnIndex, name);
    	}

    	/**
    	 * Asigna las clases de las columnas que tendrá el modelo utilizando el método
    	 * {@link #setColumnClass(int, Class)}
    	 */
    	protected abstract void setColumnClasses();

    	/**
    	 * Asigna la clase que determina el tipo de una columna
    	 * @param columnIndex Índice de la columna
    	 * @param clazz Clase a asignar
    	 */
    	public void setColumnClass(int columnIndex, Class<?> clazz) {
    		getColumnClasses().put(columnIndex, clazz);
    	}
    	
    	/**
    	 * Asigna la posibilidad o no de edición para una columna específica.
    	 * @param columnIndex Índice de la columna
    	 * @param editable <code>true</code> si es posible editar la columna, 
    	 * <code>false</code> caso contrario.
    	 */
    	public void setColumnEditable(int columnIndex, boolean editable) {
    		getColumnEditable().put(columnIndex, editable);
    	}
    	
		public int getColumnCount() {
			return getColumnNames().size();
		}

		@Override
		public int getRowCount() {
			return getSourceEntities().size();
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			SourceEntity sourceEntity = getSourceEntity(rowIndex);
			Object value = null;
			if (colIndex == COL_IDX_SELECTION) {
				value = sourceEntity.selected;
			}
			return value;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Solo la marca de selección es editable.
			if (columnIndex == COL_IDX_SELECTION) {
				getSourceEntity(rowIndex).selected = (Boolean)aValue;
			} else {
				super.setValueAt(aValue, rowIndex, columnIndex);
			}
		}

		public String getColumnName(int column) {
			return getColumnNames().get(column);
		}

		public Class<?> getColumnClass(int columnIndex) {
			Class<?> clazz = null;
			if (getColumnClasses().containsKey(columnIndex)) {
				clazz = getColumnClasses().get(columnIndex);
			} else {
//				clazz = super.getColumnClass(columnIndex);
			}
			return clazz;
		}

		/**
		 * Indica si una columna es editable
		 * @param columnIndex Índice de la columna
		 * @return <code>true</code> si es posible editar la columna,
		 * <code>false</code> caso contrario.
		 */
		public boolean isColumnEditable(int columnIndex) {
			Boolean value = false; 
			if (getColumnEditable().containsKey(columnIndex)) {
				value = getColumnEditable().get(columnIndex);
			}
			return value;
		}

		/**
		 * @return the columnEditable
		 */
		private Map<Integer, Boolean> getColumnEditable() {
			return columnEditable;
		}

		/**
		 * @return the sourceEntitys
		 */
		public List<? extends SourceEntity> getSourceEntities() {
			return sourceEntity;
		}

		/**
		 * @param sourceEntitys the sourceEntitys to set
		 */
		public void setSourceEntities(List<? extends SourceEntity> sourceEntity) {
			this.sourceEntity = sourceEntity;
		}

    	/**
		 * @return the columnNames
		 */
		public Map<Integer,String> getColumnNames() {
			return columnNames;
		}
		
    	/**
		 * @return the columnNames
		 */
		public Vector<String> getColumnNamesAsList() {
			Vector<String> retValue = new Vector<String>();
			for (String colName : columnNames.values())
				retValue.add(colName);
			return retValue;
		}
		
		/**
		 * @return the columnClasses
		 */
		private Map<Integer, Class<?>> getColumnClasses() {
			return columnClasses;
		}
		
		/**
		 * Devuelve la Entidad Oŕigen ubicada en una fila de la tabla
		 * @param rowIndex Índice de la fila
		 * @return {@link SourceEntity}
		 */
		public SourceEntity getSourceEntity(int rowIndex) {
			return getSourceEntities().get(rowIndex);
		}
		
		protected abstract void updateColumns();
    }
    
    /**
     * Modelo de tabla para líneas de documentos (Pedidos, Remitos, Facturas)
     */
    protected class DocumentLineTableModel extends CreateFromTableModel {

		private static final long serialVersionUID = 6952795549012591849L;

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_LINE      = 1;
    	public static final int COL_IDX_ITEM_CODE = 2;
    	public static final int COL_IDX_PRODUCT   = 3;
    	public static final int COL_IDX_UOM       = 5;
    	public static final int COL_IDX_QTY       = 6;
    	public static final int COL_IDX_REMAINING = 7;
    	
    	public static final int COL_IDX_INSTANCE_NAME = 4;
    	public int visibles = 8;

		@Override
		protected void setColumnNames() {
            setColumnName(COL_IDX_LINE, Msg.getElement(getCtx(),"Line"));
            setColumnName(COL_IDX_ITEM_CODE, Msg.translate( Env.getCtx(),"Value" ));
            setColumnName(COL_IDX_PRODUCT, Msg.translate( Env.getCtx(),"M_Product_ID" ));
            setColumnName(COL_IDX_UOM, Msg.translate( Env.getCtx(),"C_UOM_ID" ));
            setColumnName(COL_IDX_QTY, Msg.translate( Env.getCtx(),"Quantity" ));
            setColumnName(COL_IDX_REMAINING, Msg.translate( Env.getCtx(),"RemainingQty" ));
            
            setColumnName(COL_IDX_INSTANCE_NAME, Msg.translate( Env.getCtx(),"Description" ));
		}
		
		@Override
		protected void setColumnClasses() {
			setColumnClass(COL_IDX_LINE, Integer.class);
			setColumnClass(COL_IDX_ITEM_CODE, String.class);
			setColumnClass(COL_IDX_PRODUCT, String.class);
			setColumnClass(COL_IDX_UOM, String.class);
			setColumnClass(COL_IDX_QTY, BigDecimal.class);
			setColumnClass(COL_IDX_REMAINING, BigDecimal.class);
			
			setColumnClass(COL_IDX_INSTANCE_NAME, String.class);
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			DocumentLine docLine = getDocumentLine(rowIndex);
			Object value = null;
			switch (colIndex) {
				case COL_IDX_LINE:
					value = docLine.lineNo; break;
				case COL_IDX_ITEM_CODE:
					value = docLine.itemCode; break;
				case COL_IDX_PRODUCT:
					value = docLine.productName; break;
				case COL_IDX_UOM:
					value = docLine.uomName; break;
				case COL_IDX_QTY:
					value = docLine.lineQty; break;
				case COL_IDX_REMAINING:
					value = docLine.remainingQty; break;
				case COL_IDX_INSTANCE_NAME:
					value = docLine.instanceName; break;
				default:
					value = super.getValueAt(rowIndex, colIndex); break;
			}
			return value;
		}
		
		@Override
		public int getColumnCount() {
			return visibles;
		}

		/**
		 * Devuelve la línea de documento ubicada en una fila de la grilla.
		 * @param rowIndex Índice de la fila
		 * @return {@link DocumentLine}
		 */
		public DocumentLine getDocumentLine(int rowIndex) {
			return (DocumentLine)getSourceEntity(rowIndex);
		}
		
		@Override
		protected void updateColumns() {
// TODO: Pendiente de migrar. No es de alta prioridad.
//			window.getDataTable()dataTable.getColumnModel().removeColumn(dataTable.getColumnModel().getColumn(DocumentLineTableModel.COL_IDX_INSTANCE_NAME));
//			((DocumentLineTableModel)dataTable.getModel()).visibles = ((DocumentLineTableModel) window.getDataTable().getModel()).visibles - 1; 
		}
    }
    
}
