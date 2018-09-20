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
import org.adempiere.webui.editor.WLocatorEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.plaf.CompierePLAF;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.CreateFromInvoiceModel;
import org.openXpertya.grid.CreateFromModel;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.DocumentLine;
import org.openXpertya.grid.CreateFromModel.InvoiceLine;
import org.openXpertya.grid.CreateFromModel.ListedSourceEntityInterface;
import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.grid.CreateFromShipmentModel;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MLocatorLookup;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.zkoss.zk.ui.event.Event;

public class WCreateFromShipment extends WCreateFrom {


	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param mTab
	 */

	protected WCreateFromShipment(MTab mTab) {
		super(mTab);
        log.info( mTab.toString());
		p_WindowNo = mTab.getWindowNo();
		AEnv.showWindow(window);
	}

	/** Descripción de Campos */

	private MInvoice m_invoice = null;

	/** Remito que invoca este Crear Desde */
	private MInOut inOut = null;

	private String bpDeliveryRule = null;
    
    private MBPartner bpartner = null;
    
    /** Tipo de Documento a crear */
    private MDocType docType;
    
    @Override
	protected CreateFromModel createHelper(){
    	return new CreateFromShipmentModel();
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
		log.config("");

		// Buscador de facturas
		initInvoiceLookup();

		// Buscador de pedidos asociados a factura
		initInvoiceOrderLookup();

		// load Locator

		int AD_Column_ID = 3537; // M_InOut.M_Locator_ID
		MLocatorLookup locator = new MLocatorLookup(Env.getCtx(), p_WindowNo);
		locatorField = new WLocatorEditor("M_Locator_ID", true, false, true, locator, p_WindowNo);
		setDefaultLocator();

		// Entidad Comercial
		initBPartner(false);

		bPartnerField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent evt) {
				// Se limpia la selección de factura
				invoiceField.setValue(null);
				invoiceChanged(0);

				// Si el check allInOut esta seleccionado se muestran todos los
				// pedidos del proveedor
				// que fue seleccionado.
				if (allInOut != null && allInOut.isSelected()) {
					bPartnerField.setValue(evt.getNewValue());
					showAllOrder();
				}
			}
		});

		// Remitos de Salida:
		if (isSOTrx()) {
			// Por el momento solo se permiten crear remitos de salida a partir
			// de pedidos
			// ya que el remito de salida debe estar asociado si o si a un
			// pedido.
			invoiceLabel.setVisible(false);
			invoiceField.setVisible(false);

			// Se setea el pedido que esté configurado en el encabezado del
			// remito
			if (getInOut().getC_Order_ID() > 0) {
				orderField.setValue(getInOut().getC_Order_ID());
				orderChanged(getInOut().getC_Order_ID());
			}

			// Si el remito ya contiene líneas entonces no puede editarse el
			// pedido
			// debido a que las líneas del remito están si o si asociadas con
			// las
			// líneas del pedido previamente asociado.
			if (getInOut().getLines().length > 0) {
				orderField.setReadWrite(false);
				invoiceOrderField.setReadWrite(false);
				bPartnerField.setReadWrite(false);
			}
		}
		
		setDocType(new MDocType(getCtx(), getInOut().getC_DocType_ID(), getTrxName()));

		return true;
	} // dynInit

	private void setDefaultLocator() {
		Integer warehouseId = (Integer) p_mTab.getValue("M_Warehouse_ID");
		if (warehouseId != null) {
			MWarehouse warehouse = new MWarehouse(Env.getCtx(),
					warehouseId.intValue(), null);
			MLocator locator = warehouse.getDefaultLocator();
			if (locator != null) {
				locatorField.setValue(new Integer(locator.getM_Locator_ID()));
			}
		}

	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param C_BPartner_ID
	 */

	protected void initBPDetails(int C_BPartner_ID) {
		log.config("C_BPartner_ID=" + C_BPartner_ID);
		updateBPDetails(C_BPartner_ID, true);
	} // initBPDetails

	@Override
    protected void updateBPDetails(Integer bpartnerID, boolean resetDocument){
    	 String deliveryRule = null;
         if(!Util.isEmpty(bpartnerID,true)){
         	setBpartner(new MBPartner(getCtx(), bpartnerID, getTrxName()));
         	deliveryRule = getBpartner().getDeliveryRule();
         }
         else{
        	 setBpartner(null);
         }
         setBpDeliveryRule(deliveryRule);
         
 		// Si la regla de envío de mercadería de la entidad comercial es Después
 		// de la Facturación entonces se debe ocultar el campo para crear remito
 		// a partir del pedido
		if (getBpDeliveryRule() != null
				&& (getBpDeliveryRule().equals(
						MBPartner.DELIVERYRULE_AfterInvoicing) || getBpDeliveryRule()
						.equals(MBPartner.DELIVERYRULE_Force_AfterInvoicing))) {
 			orderLabel.setVisible(false);
 			orderField.setVisible(false);
 			if(resetDocument){
 				// Se borra la selección de pedido.
 				orderField.setValue(null);
 				orderChanged(0);
 	 			// Se limpia la selección de factura
 				invoiceField.setValue(null);
 				invoiceChanged(0); 				
 	 		}	
         }
 		else{
 			orderLabel.setVisible(true);
 			orderField.setVisible(true);
 		}
    }
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param C_Invoice_ID
	 */

	private void loadInvoice(int C_Invoice_ID) {
		log.config("C_Invoice_ID=" + C_Invoice_ID);
		
		initDataTable();
		
		if (C_Invoice_ID > 0) {
			m_invoice = new MInvoice(Env.getCtx(), C_Invoice_ID, null); // save
		}
		else{
    		m_invoice = null;
    	}
		
		p_order = null;
		List<InvoiceLine> data = new ArrayList<InvoiceLine>();
		StringBuffer sql = ((CreateFromShipmentModel)getHelper()).loadInvoiceQuery();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql.toString());
			pstmt.setInt(1, C_Invoice_ID);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				InvoiceLineListImpl invoiceLine = new InvoiceLineListImpl();
				((CreateFromShipmentModel)getHelper()).loadInvoiceLine(invoiceLine, rs);
				// Agrega la línea a la lista solo si tiene cantidad pendiente
				if (invoiceLine.remainingQty.compareTo(BigDecimal.ZERO) > 0 || invoiceLine.productType.equals("E")) {
					data.add(invoiceLine);
				}
			}

		} catch (SQLException e) {
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
		
		filtrarColumnaInstanceName(data);
		
		loadTable(data);
		// Se carga la EC de la factura.
		if (bPartnerField != null && m_invoice != null) {
			if(bPartnerField.getValue() == null 
					|| (Integer)bPartnerField.getValue() != m_invoice.getC_BPartner_ID()){
				bPartnerField.setValue(m_invoice.getC_BPartner_ID());
				updateBPDetails(m_invoice.getC_BPartner_ID(), true);
			}
		}
	} // loadInvoice

	
	@Override
	public String getRemainingQtySQLLine(boolean forInvoice, boolean allowDeliveryReturns){
		return ((CreateFromShipmentModel)getHelper()).getRemainingQtySQLLine(getInOut(), forInvoice, allowDeliveryReturns);
	}
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	protected void save() throws CreateFromSaveException {
		Integer locatorID = (Integer) locatorField.getValue();
		((CreateFromShipmentModel)getHelper()).save(locatorID, getInOut(), p_order, m_invoice, getSelectedSourceEntities(), getTrxName(), isSOTrx(), this); 
	} // save

	@Override
	protected String getOrderFilter() {
		return MInOut.getOrderFilter(getInOut());
	}

	@Override
	protected void orderChanged(int orderID) {
		// set Invoice and Shipment to Null
		invoiceField.setValue(null);
		invoiceOrderField.setValue(null);
		loadOrder(orderID, false, allowDeliveryReturned(), true);
		m_invoice = null;
	}

	/**
	 * Este método es invocado cuando el usuario cambia la factura seleccionada
	 * en el VLookup. Las subclases puede sobrescribir este comportamiento.
	 * 
	 * @param invoiceID
	 *            ID de la nueva factura seleccionada.
	 */
	protected void invoiceChanged(int invoiceID) {
		orderField.setValue(null);
		invoiceOrderField.setValue(null);
		loadInvoice(invoiceID);
	}

	/**
	 * Este método es invocado cuando el usuario cambia la factura con pedido
	 * seleccionada en el VLookup. Las subclases puede sobrescribir este
	 * comportamiento.
	 * 
	 * @param invoiceID
	 *            ID de la nueva factura seleccionada.
	 */
	protected void invoiceOrderChanged(int invoiceID) {
		int relatedOrderID = 0;
		orderField.setValue(null);
		invoiceField.setValue(null);
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
	 * @return Devuelve el remito origen de este CreateFrom.
	 */
	public MInOut getInOut() {
		if (inOut == null) {
			inOut = new MInOut(getCtx(),
					(Integer) p_mTab.getValue("M_InOut_ID"), getTrxName());
		}
		inOut.set_TrxName(getTrxName());
		return inOut;
	}

	@Override
	protected boolean beforeAddOrderLine(OrderLine orderLine) {
		return ((CreateFromShipmentModel)getHelper()).beforeAddOrderLine(orderLine, getInOut(), isSOTrx());	}

	/**
	 * Inicializa el lookup de facturas
	 */
	private void initInvoiceLookup() {
		String whereClause = getInvoiceFilter();
//		invoiceField = VComponentsFactory.VLookupFactory("C_Invoice_ID", "C_Invoice", p_WindowNo, DisplayType.Search, whereClause, false);
		/* FEDE:TODO: ESTO DEBERIA REFACTORIZARSE A OTRO LUGAR */
		int colID = 3484; 	// C_Invoice.C_Invoice_ID
		MLookupInfo info = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),
				p_WindowNo, p_mTab.getTabNo(), colID, DisplayType.Search,
				whereClause, addSecurityValidation());
    	info.ZoomQuery = new MQuery();
    	MLookup lookup       = new MLookup(info, p_mTab.getTabNo());
    	if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
    	/* */
    	
    	invoiceField = new WSearchEditor("C_Invoice_ID", true, false, true, lookup);
		invoiceField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent e)  {
				Integer invoiceID = (Integer) e.getNewValue();
				invoiceChanged(invoiceID == null ? 0 : invoiceID);
			}
		});
	}

	/**
	 * @return Devuelve el filtro que se aplica al Lookup de Facturas.
	 */
	protected String getInvoiceFilter() {
		return MInOut.getInvoiceFilter(getInOut());
	}

	/**
	 * Inicializa el lookup de facturas con pedidos asociados.
	 */
	private void initInvoiceOrderLookup() {
		String whereClause = getInvoiceOrderFilter();
//		invoiceOrderField = VComponentsFactory.VLookupFactory("C_Invoice_ID", "C_Invoice", p_WindowNo,	DisplayType.Search, whereClause, false);
		/* FEDE:TODO: ESTO DEBERIA REFACTORIZARSE A OTRO LUGAR */
		int colID = 3484; 	// C_Invoice.C_Invoice_ID
		MLookupInfo info = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),
				p_WindowNo, p_mTab.getTabNo(), colID, DisplayType.Search,
				whereClause, addSecurityValidation());
    	info.ZoomQuery = new MQuery();
    	MLookup lookup = new MLookup(info, p_mTab.getTabNo());
    	if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
    	/* */
    	
    	invoiceOrderField = new WSearchEditor("C_Invoice_ID", true, false, true, lookup);
		invoiceOrderField.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent e)  {
						Integer invoiceID = (Integer) e.getNewValue();
						invoiceOrderChanged(invoiceID == null ? 0 : invoiceID);
					}
				});
	}

	/**
	 * @return Devuelve el filtro que se aplica al Lookup de Facturas asociadas
	 *         a pedidos.
	 */
	protected String getInvoiceOrderFilter() {
		return MInOut.getInvoiceOrderFilter(getInOut());
	}

	public void setBpDeliveryRule(String bpDeliveryRule) {
		// TODO Sólo para ventas y movimiento de salida es necesario verificar
		// la regla de envío y el comportamiento relacionado sobre los
		// componentes e información. Cuando para proveedores también deba
		// seguir el mismo comportamiento, eliminar la condición del if de issotrx
		if (isSOTrx() && getInOut().getMovementType().endsWith("-")) {
			this.bpDeliveryRule = bpDeliveryRule;
		}
	}

	public String getBpDeliveryRule() {
		return bpDeliveryRule;
	}

	protected void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}

	protected MBPartner getBpartner() {
		return bpartner;
	}
	
	/**
	 * Entidad Orígen: Línea de Factura
	 */
	protected class InvoiceLineListImpl extends InvoiceLine implements ListedSourceEntityInterface {
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
			invoiceField.setValue("");
			orderField.setValue("");
			invoiceOrderField.setValue("");
		}
		orderField.setReadWrite(state);
		invoiceOrderField.setReadWrite(state);
		invoiceField.setReadWrite(state);
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
			return new DocumentLineTableModelFromShipment();
		} else {
			return new DocumentLineTableModel();
		}
	}

	/**
	 * Modelo de tabla para líneas de remitos.
	 */
	public class DocumentLineTableModelFromShipment extends
			DocumentLineTableModel {
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
	
	// El siguiente metodo podrá ser redefinido por un plugin para agregar una funcionalidad particular.
	// El metodo es invocado antes de hacer el save de la linea
	public void customMethod(PO ol, PO iol) {
	} 
	
	@Override
	protected boolean lazyEvaluation() {
		return false;
	}

	protected MDocType getDocType() {
		return docType;
	}

	protected void setDocType(MDocType docType) {
		this.docType = docType;
	}
	
	public void showWindow()
	{
		window.setVisible(true);
	}
	
	public void closeWindow()
	{
		window.dispose();
	}

	@Override
	protected boolean addSecurityValidation() {
		return getRole().isAddSecurityValidation_CreateFromShipment();
	}

}
