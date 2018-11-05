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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.compiere.swing.CCheckBox;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.InOutLine;
import org.openXpertya.grid.CreateFromModel.OrderLine;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ReservedUtil;

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

	protected CCheckBox allOrder;
    
    /** Descripción de Campos */
    private MInOut m_inout = null;
    
    /** Factura que invoca este Crear Desde */
    private MInvoice invoice = null;
    
	/** Este crear desde lleva la lógica de facturas o la lógica de remitos? */
    private Boolean isForInvoice = null;
    
    /** Tipo de Documento a crear */
    private MDocType docType;
    
	@Override
	protected CreateFromModel createHelper(){
    	return new CreateFromInvoiceModel();
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
				
				// Si el check allOrder esta seleccionado se muestran todos los
				// pedidos del proveedor
				// que fue seleccionado.
				if (allOrder != null && allOrder.isSelected()) {
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
        StringBuffer sql = ((CreateFromInvoiceModel)getHelper()).loadShipmentQuery();
        List<SourceEntity> data = new ArrayList<SourceEntity>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	pstmt = DB.prepareStatement(sql.toString());
            pstmt.setInt( 1,M_InOut_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                InOutLine docLine = new InOutLine();
                ((CreateFromInvoiceModel)getHelper()).loadShipmentLine(docLine, rs);
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
    	((CreateFromInvoiceModel)getHelper()).save(p_order, getInvoice(), m_inout, getDocType(), getSelectedSourceEntities(), getTrxName(), this);
    }    // saveInvoice

    @Override
    protected String getRemainingQtySQLLine(boolean forInvoice, boolean allowDeliveryReturns){
    	String sqlLine = super.getRemainingQtySQLLine(forInvoice, allowDeliveryReturns);
    	if(!forInvoice){
    		String sqlRealDeliveredColumns = ReservedUtil.getSQLPendingQtyByColumns(getCtx(), "l");
    		sqlLine = " (CASE WHEN "+sqlRealDeliveredColumns+" > l.QtyInvoiced "
    				+ "			THEN l.QtyInvoiced "
    				+ "			ELSE "+sqlRealDeliveredColumns+" END) ";
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
		shipmentField = VComponentsFactory.VLookupFactory("M_InOut_ID", "M_InOut", p_WindowNo, DisplayType.Search,
				whereClause, false, addSecurityValidation());
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
		invoiceOrderField = VComponentsFactory.VLookupFactory("C_Invoice_ID", "C_Invoice", p_WindowNo,
				DisplayType.Search, whereClause, false, addSecurityValidation());
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
        	((CreateFromInvoiceModel)getHelper()).setPaymentRule(invoice.getPaymentRule());
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
	

	protected void customizarPanel() {
		// Si es Perfil Compras agrego el check Ver todos los pedidos
		if (!isSOTrx()) {
			allOrder = new CCheckBox();
			allOrder.setText("Ver todos los pedidos");
			allOrder.addActionListener(this);
			parameterStdPanel.add(allOrder, new GridBagConstraints(0, 1, 1, 1,
					0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(5, 5, 5, 5), 0, 0));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		log.config("Action=" + e.getActionCommand());

		// Si selecciona el check "Seleccionar
		if (e.getSource().equals(allOrder))
			showAllOrder();
		else
			super.actionPerformed(e);

	} // actionPerformed

	
	public MInOut getM_inout() {
		return m_inout;
	}

	public void setM_inout(MInOut m_inout) {
		this.m_inout = m_inout;
	}
	

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
		orderField.getM_button().setVisible(state);
		orderField.setShowInfo(state);
		invoiceOrderField.setReadWrite(state);
		invoiceOrderField.getM_button().setVisible(state);
		invoiceOrderField.setShowInfo(state);
		shipmentField.setReadWrite(state);
		shipmentField.getM_button().setVisible(state);
		shipmentField.setShowInfo(state);
	}
	
	// El método muestra todos los pedidos del proveedor seleccionado
		public void showAllOrder() {
			activeDesactiveField(!allOrder.isSelected());
			initDataTable();
			if (allOrder.isSelected()) {
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
						List<OrderLine> data = (List<OrderLine>) ((CreateFromTableModel) dataTable
								.getModel()).getSourceEntities();
						Iterator<? extends SourceEntity> it = data.iterator();
						while (it.hasNext()) {
							dataAux.add((OrderLine) it.next());
						}
					}
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
		}
		// Si el check de mostrar todos los pedidos esta seleccionado crea el modelo
		// de tabla para líneas de remitos.
		// En caso contratio crea el modelo de tabla para líneas de documentos
		// (Pedidos, Remitos, Facturas)
		protected CreateFromTableModel createTableModelInstance() {
			if (!isSOTrx() && allOrder != null && allOrder.isSelected()) {
				return new DocumentLineTableModelFromInvoice();
			} else {
				return new DocumentLineTableModel();
			}
		}

		/**
		 * Modelo de tabla para líneas de facturas.
		 */
		public class DocumentLineTableModelFromInvoice extends
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
				OrderLine docLine = (OrderLine) getDocumentLine(rowIndex);
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

		@Override
		protected boolean addSecurityValidation() {
			return getRole().isAddSecurityValidation_CreateFromInvoice();
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
