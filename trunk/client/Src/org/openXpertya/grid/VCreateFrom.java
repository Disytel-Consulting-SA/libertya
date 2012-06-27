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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.VCreateFromShipment.DocumentLineTableModelFromShipment;
import org.openXpertya.grid.ed.VLocator;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MUOM;
import org.openXpertya.plugin.common.PluginCreateFromUtils;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public abstract class VCreateFrom extends JDialog implements ActionListener,TableModelListener {

    /**
     * Descripción de Método
     *
     *
     * @param mTab
     *
     * @return
     */

    public static VCreateFrom create( MTab mTab ) {

        // dynamic init preparation
        int AD_Table_ID = Env.getContextAsInt( Env.getCtx(),mTab.getWindowNo(),"BaseTable_ID" );
        VCreateFrom retValue = null;

        // Busca una definición de CreateFrom en los plugins activos
        retValue = PluginCreateFromUtils.getCreateFrom(mTab);
        
        if (retValue != null) {
        	; // Devuelve retValue que es la instancia del CreateFrom del plugin
        } else if( AD_Table_ID == 392 ) {           // C_BankStatement
            retValue = new VCreateFromStatement( mTab );
        } else if( AD_Table_ID == 318 ) {    // C_Invoice
            retValue = new VCreateFromInvoice( mTab );
        } else if( AD_Table_ID == 319 ) {    // M_InOut
            retValue = new VCreateFromShipment( mTab );
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

    protected VCreateFrom( MTab mTab ) {
        super( Env.getWindow( mTab.getWindowNo()),true );
        log.info( mTab.toString());
        p_WindowNo = mTab.getWindowNo();
        p_mTab     = mTab;

        try {
        	initOrderLookup();
        	
            
        	if( !dynInit()) {
                return;
            }

            jbInit();
            confirmPanel.addActionListener( this );

            initDataTable();
            // Set status

            statusBar.setStatusDB( "" );
            tableChanged( null );
            p_initOK = true;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
            p_initOK = false;
        }

        AEnv.positionCenterWindow( Env.getWindow( p_WindowNo ),this );
    }    // VCreateFrom

    /** Descripción de Campos */

    protected int p_WindowNo;

    /** Descripción de Campos */

    protected MTab p_mTab;

    /** Descripción de Campos */

    private boolean p_initOK = false;

    /** Descripción de Campos */

    protected MOrder p_order = null;

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( VCreateFrom.class );

    //

    /** Descripción de Campos */

    private CPanel parameterPanel = new CPanel();

    /** Descripción de Campos */

    protected CPanel parameterBankPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout parameterLayout = new BorderLayout();

    /** Descripción de Campos */

    private JLabel bankAccountLabel = new JLabel();

    /** Descripción de Campos */

    protected CPanel parameterStdPanel = new CPanel();

    /** Descripción de Campos */

    private JLabel bPartnerLabel = new JLabel();

    /** Descripción de Campos */

    protected VLookup bankAccountField;

    protected CCheckBox automatico = new CCheckBox();
    
    /** Descripción de Campos */

    private GridBagLayout parameterStdLayout = new GridBagLayout();

    /** Descripción de Campos */

    private GridBagLayout parameterBankLayout = new GridBagLayout();

    /** Descripción de Campos */

    protected VLookup bPartnerField;

    /** Descripción de Campos */

    protected JLabel orderLabel = new JLabel();

    /** Descripción de Campos */

    protected VLookup orderField = null;
    
    /** Label Pedido de factura */
    protected JLabel invoiceOrderLabel = new JLabel();
    
    /** Pedido de una factura */
    protected VLookup invoiceOrderField = null;

    /** Descripción de Campos */

    protected JLabel invoiceLabel = new JLabel();

    /** Descripción de Campos */

    protected VLookup invoiceField = null;

    /** Descripción de Campos */

    protected JLabel shipmentLabel = new JLabel();

    /** Descripción de Campos */

    protected VLookup shipmentField = null;

    /** Descripción de Campos */

    private JScrollPane dataPane = new JScrollPane();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    protected StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    protected MiniTable dataTable = new MiniTable();

    /** Descripción de Campos */

    protected JLabel locatorLabel = new JLabel();

    /** Descripción de Campos */

    protected VLocator locatorField = new VLocator();
    
    /** Nombre de la transacción para hacer el save */
    private String trxName = null;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        parameterPanel.setLayout( parameterLayout );
        parameterStdPanel.setLayout( parameterStdLayout );
        parameterBankPanel.setLayout( parameterBankLayout );

        //

        bankAccountLabel.setText( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));
        bPartnerLabel.setText( Msg.getElement( Env.getCtx(),"C_BPartner_ID" ));
        orderLabel.setText( Msg.getElement( Env.getCtx(),"C_Order_ID",false ));
        invoiceLabel.setText( Msg.getElement( Env.getCtx(),"C_Invoice_ID",false ));
        shipmentLabel.setText( Msg.getElement( Env.getCtx(),"M_InOut_ID",false ));
        locatorLabel.setText( Msg.translate( Env.getCtx(),"M_Locator_ID" ));
        automatico.setText("Seleccionar todos");
		automatico.addActionListener(this);
		
		invoiceOrderLabel.setText(Msg.translate(getCtx(), "InvoiceOrder"));
        	
		//

        this.getContentPane().add( parameterPanel,BorderLayout.NORTH );
        parameterPanel.add( parameterBankPanel,BorderLayout.NORTH );
        parameterBankPanel.add( bankAccountLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        if( bankAccountField != null ) {
            parameterBankPanel.add( bankAccountField,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        }

        parameterPanel.add( parameterStdPanel,BorderLayout.CENTER );
        parameterStdPanel.add( bPartnerLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        if( bPartnerField != null ) {
            parameterStdPanel.add( bPartnerField,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        }

        parameterStdPanel.add( orderLabel,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterStdPanel.add( orderField,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        int y = 1;
	    if (invoiceOrderField != null) {
	        parameterStdPanel.add( invoiceOrderLabel,new GridBagConstraints( 2,y,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
	        parameterStdPanel.add( invoiceOrderField,new GridBagConstraints( 3,y++,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
	    }
        if (invoiceField != null) {
	        parameterStdPanel.add( invoiceLabel,new GridBagConstraints( 2,y,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
	        parameterStdPanel.add( invoiceField,new GridBagConstraints( 3,y++,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        }
	    if (shipmentField != null) {
	        parameterStdPanel.add( shipmentLabel,new GridBagConstraints( 2,y,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
	        parameterStdPanel.add( shipmentField,new GridBagConstraints( 3,y++,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
	    }
	    int y2 = 1;
        if (locatorField.isVisible()) {
		    parameterStdPanel.add( locatorLabel,new GridBagConstraints( 0,y2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
	        parameterStdPanel.add( locatorField,new GridBagConstraints( 1,y2++,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        }
	    parameterStdPanel.add(automatico, new GridBagConstraints(1, y2, 1, 1, 0.0, 0.0
    			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    
        this.getContentPane().add( dataPane,BorderLayout.CENTER );
        dataPane.getViewport().add( dataTable,null );
        
        //

        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        southPanel.setLayout( southLayout );
        southPanel.add( confirmPanel,BorderLayout.CENTER );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        customizarPanel();
    }    // jbInit

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
     */

    abstract void info();

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
     * @param e
     */
    public void selectall ()
	{
		for (int i=0; i < dataTable.getRowCount(); i++)
		{
			Boolean id = new Boolean(automatico.isSelected());
			dataTable.getModel().setValueAt(id, i, CreateFromTableModel.COL_IDX_SELECTION);
		}
		dataTable.repaint();
	}
    
    public void actionPerformed( ActionEvent e ) {
        log.config( "Action=" + e.getActionCommand());

        // if (m_action)
        // return;
        // m_action = true;

        // OK - Save

		if (e.getSource().equals(automatico))
					selectall();
		//  OK - Save

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
        	doSave();
        }

        // Cancel

        else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }

        // m_action = false;

    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        int type = -1;

        if( e != null ) {
            type = e.getType();

            if( type != TableModelEvent.UPDATE ) {
                return;
            }
        }
        
        // El OK solo se habilita si hay al menos una línea seleccionada
        confirmPanel.getOKButton().setEnabled(getSelectedSourceEntities().size() > 0);

        log.config( "Type=" + type );
        info();
    }    // tableChanged

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

        bPartnerField = new VLookup( "C_BPartner_ID",true,false,true,lookup );

        //

        int C_BPartner_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BPartner_ID" );

        bPartnerField.setValue( new Integer( C_BPartner_ID ));

        bPartnerField.addVetoableChangeListener(new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
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
        /*
         * 2010-02-08 FB - Disytel
         * No se utiliza mas ya que se cambió el combo de pedidos por un Vlookup
         * Solo al final de este método se invoca a initBPDetails(C_BPartner_ID)
         * para las subclases
         * 
         * 
        KeyNamePair pp = new KeyNamePair( 0,"" );

        // load PO Orders - Closed, Completed

        orderField.removeActionListener( this );
        orderField.removeAllItems();
        orderField.addItem( pp );

        // Display

        StringBuffer display = new StringBuffer( "o.DocumentNo||' - ' ||" ).append( DB.TO_CHAR( "o.DateOrdered",DisplayType.Date,Env.getAD_Language( Env.getCtx()))).append( "||' - '||" ).append( DB.TO_CHAR( "o.GrandTotal",DisplayType.Amount,Env.getAD_Language( Env.getCtx())));

        //

        String column = "m.M_InOutLine_ID";

        if( forInvoice ) {
            column = "m.C_InvoiceLine_ID";
        }

        StringBuffer sql;
        
        String vIsSOTrx = getIsSOTrx();
        
        // Si es de Venta + para Factura muestra los Pedidos que tienes alguna línea que pueda facturar, que la cantidad
        // cantidad de la orden sea mayor a la cantidad facturada
        if (vIsSOTrx == "Y" && forInvoice){
        	sql = new StringBuffer( "SELECT o.C_Order_ID," ).append( display ).append( " FROM C_Order o " + "WHERE o.C_BPartner_ID=? AND o.IsSOTrx='"+vIsSOTrx+"' AND o.DocStatus IN ('CL','CO')" + " AND o.C_Order_ID IN " + "(select ol.c_order_id from c_orderline ol where ol.qtyordered > ol.qtyinvoiced)" + "ORDER BY o.DateOrdered" );
        } else{
        	sql = new StringBuffer( "SELECT o.C_Order_ID," ).append( display ).append( " FROM C_Order o " + "WHERE o.C_BPartner_ID=? AND o.IsSOTrx='"+vIsSOTrx+"' AND o.DocStatus IN ('CL','CO')" + " AND o.C_Order_ID IN " + "(SELECT ol.C_Order_ID FROM C_OrderLine ol" + " LEFT OUTER JOIN M_MatchPO m ON (ol.C_OrderLine_ID=m.C_OrderLine_ID) " + "GROUP BY ol.C_Order_ID,ol.C_OrderLine_ID, ol.QtyOrdered," ).append( column ).append( " HAVING (ol.QtyOrdered <> SUM(m.Qty) AND " ).append( column ).append( " IS NOT NULL) OR " ).append( column ).append( " IS NULL) " + "ORDER BY o.DateOrdered" );
        }
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,C_BPartner_ID );
            // Ahora se pasa en el StringBuffer como vIsSOTrx
            //pstmt.setString(2, Env.getContext(Env.getCtx(), p_mTab.getWindowNo(), p_mTab.getTabNo(), "IsSOTrx"));
            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));
                orderField.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        orderField.setSelectedIndex( 0 );
        orderField.addActionListener( this );
        */
        initBPDetails( C_BPartner_ID );
    }    // initBPartnerOIS

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     * @param forInvoice
     */

    protected void loadOrder( int C_Order_ID,boolean forInvoice, boolean filter ) {
    	log.config( "C_Order_ID=" + C_Order_ID );
    	
    	initDataTable();
    	
    	if (C_Order_ID > 0) {
    		p_order = new MOrder( Env.getCtx(),C_Order_ID,null );    // save
    	}
    	else{
    		p_order = null;
    	}
    	
    	StringBuffer sql;

    	List<OrderLine>       data = new ArrayList<OrderLine>();
    	// La consulta obtiene la líneas del pedido, calculando la cantidad pendiente
    	// directamente desde las cantidades de la línea (QtyOrdered, QtyDelivered, QtyInvoiced).
    	// Se quitó la diferenciación entre IsSOTrx Y o N debido a que MMatchPO actualiza
    	// las cantidades en las líneas de pedido tal como se hace para IsSOTrx = Y.
		sql = new StringBuffer();
		sql.append("SELECT ")
		   .append(   "l.C_Order_ID, ")
		   .append(   "l.C_OrderLine_ID, ")
		   .append(   "l.DateOrdered, ")
		   .append(   "l.Line, ")
		   .append(   "COALESCE(l.M_Product_ID,0) AS M_Product_ID, ")
		   .append(   "COALESCE(p.Name,c.Name) AS ProductName, ")
		   .append(   "l.Description, ")
		   .append(   "l.C_UOM_ID, ")
		   .append(   "l.QtyOrdered, " )
		   .append(   "l.QtyInvoiced, " )
		   .append(   "l.QtyDelivered, " )
		   .append(   getRemainingQtySQLLine(forInvoice) )
		   .append(   " AS RemainingQty, ")
		   .append(   "(CASE l.QtyOrdered WHEN 0 THEN 0 ELSE l.QtyEntered/l.QtyOrdered END) AS Multiplier, ")
		   .append(   "p.value AS ItemCode, ")
		   .append(   "p.producttype AS ProductType, ")
		   .append(   "l.M_AttributeSetInstance_ID AS AttributeSetInstance_ID ")

		   .append("FROM C_OrderLine l ")
		   .append("LEFT OUTER JOIN M_Product p ON (l.M_Product_ID=p.M_Product_ID) ") 
		   .append("LEFT OUTER JOIN C_Charge c ON (l.C_Charge_ID=c.C_Charge_ID) ")
		   //
		   //Añadido por Conserti, para que no saque los cargos en los albaranes. // and l.c_charge_id is null
		   //
		   .append("WHERE l.C_Order_ID=? and l.C_Charge_ID is NULL ")
		   .append("ORDER BY l.DateOrdered,l.C_Order_ID,l.Line,ItemCode");

    	log.finer( sql.toString());

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		pstmt.setInt( 1,C_Order_ID );
    		rs = pstmt.executeQuery();

    		while( rs.next()) {
    			OrderLine orderLine = new OrderLine();
    			
    			// Por defecto no está seleccionada para ser procesada	
    			orderLine.selected = false;
    			
    			// ID del pedido
    			orderLine.documentNo = p_order.getDocumentNo();
    			
    			// Fecha del pedido
    			orderLine.dateOrderLine = rs.getDate("DateOrdered");
				
    			// ID de la línea del pedido
    			orderLine.orderLineID = rs.getInt("C_OrderLine_ID");
    			
    			// Nro de línea
    			orderLine.lineNo = rs.getInt("Line");
    			
    			// Descripción
    			orderLine.description = rs.getString("Description");
    			
    			// Cantidades
    			BigDecimal multiplier = rs.getBigDecimal("Multiplier");
    			BigDecimal qtyOrdered = rs.getBigDecimal("QtyOrdered").multiply(multiplier);
    			BigDecimal remainingQty = rs.getBigDecimal("RemainingQty").multiply(multiplier);
    			orderLine.lineQty = qtyOrdered;
    			orderLine.remainingQty = remainingQty;
    			orderLine.qtyInvoiced = rs.getBigDecimal("QtyInvoiced");
    			orderLine.qtyDelivered = rs.getBigDecimal("QtyDelivered");
    			
    			// Artículo
    			orderLine.productID = rs.getInt("M_Product_ID");
				orderLine.productName = rs.getString("ProductName");
				orderLine.itemCode = rs.getString("ItemCode");
				orderLine.instanceName = getInstanceName(rs.getInt("AttributeSetInstance_ID"));
				orderLine.productType = rs.getString("ProductType");

    			// Unidad de Medida
    			orderLine.uomID = rs.getInt("C_UOM_ID");
    			orderLine.uomName = getUOMName(orderLine.uomID);
    			
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
    protected String getRemainingQtySQLLine(boolean forInvoice){
    	// Para facturas se compara la cantidad facturada, para remitos la cantidad
    	// entregada/recibida.
    	String compareColumn = forInvoice ? "QtyInvoiced" : "QtyDelivered";
    	return "l.QtyOrdered-NVL(l."+compareColumn+",0)";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param data
     */
    
     protected void loadTable(List<? extends SourceEntity> data) {
        // Se obtiene el modelo de la tabla y se asigna la nueva lista de líneas de documento.
    	((CreateFromTableModel)dataTable.getModel()).setSourceEntities(data);
        dataTable.autoSize();
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
    	orderField = VComponentsFactory.VLookupFactory("C_Order_ID", "C_Order", p_WindowNo, DisplayType.Search, whereClause, false);
    	orderField.addVetoableChangeListener(new VetoableChangeListener() {
			
			@Override
			public void vetoableChange(PropertyChangeEvent e)
					throws PropertyVetoException {
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
        model.addTableModelListener(this);
        // Asigna el modelo al control de la tabla.
        dataTable.setModel(model);
        // Asigna las clases de cada columna para personalizar la presentación y edición
        // de los datos en cada una de ellas.
        for(int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
        	dataTable.setColumnClass(
        		columnIndex, 
        		model.getColumnClass(columnIndex), 
        		!model.isColumnEditable(columnIndex)
        	);
        }

        // Se desactiva la posibilidad de ordenamiento debido a que la lógica está "atada"
        // a que el modelo de la tabla sea instancia de DefaultTableModel. Debido a que esta
        // tabla no tiene un DefaultTableModel, se desactiva al posibilidad de ordenar para
        // que no se disparen errores (además no es tan requerida esa funcionalidad aquí).
        dataTable.setSorted(false);
        
     // Si es Perfil Ventas no se muestra la columna COL_IDX_INSTANCE_NAME
    	if (isSOTrx()) {
    		dataTable.getColumnModel().removeColumn(dataTable.getColumnModel().getColumn(DocumentLineTableModel.COL_IDX_INSTANCE_NAME));
    		((DocumentLineTableModel)dataTable.getModel()).visibles = ((DocumentLineTableModel) dataTable.getModel()).visibles - 1; 
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
     * @return Devuelve el filtro que se aplica al Lookup de Pedidos.
     * Por defecto este filtro respeta el filtro que se aplicaba en el método
     * {@link #initBPartnerOIS(int, boolean)} para mantener la compatibilidad
     * con otros CreateFroms. Este método se puede sobrescribir para redefinir
     * el filtro según sea necesario.
     */
    protected String getOrderFilter() {
    	StringBuffer filter = new StringBuffer();
        String compareColumn = "";

        if( isForInvoice() ) {
            compareColumn = "QtyInvoiced";
        } else { // InOut
        	compareColumn = "QtyDelivered";
        }

     	filter
     		.append("C_Order.IsSOTrx='").append(getIsSOTrx()).append("' AND ")
     		.append("C_Order.DocStatus IN ('CL','CO') AND ")
     		.append("C_Order.C_Order_ID IN (")
     		.append(   "SELECT ol.C_Order_ID ")
     		.append(   "FROM C_OrderLine ol ")
     		.append(   "WHERE ol.QtyOrdered > ol.").append(compareColumn).append(")");

     	return filter.toString();
    }
    
    /**
     * Este método es invocado cuando el usuario cambia el pedido seleccionado
     * en el VLookup. Por defecto solo invoca {@link #loadOrder(int, boolean)} pero
     * las subclases puede sobrescribir este comportamiento.
     * @param orderID ID del nuevo pedido seleccionado.
     */
    protected void orderChanged(int orderID) {
    	loadOrder(orderID, isForInvoice(),true);
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
    	return ((CreateFromTableModel) dataTable.getModel()).getSourceEntities();
    }
    
    /**
     * @return Devuelve una lista solo con las líneas de documento que están
     * seleccionadas en la grilla de la ventana.
     */
    protected List<? extends SourceEntity> getSelectedSourceEntities() {
    	List<SourceEntity> selected = new ArrayList<SourceEntity>();
    	for (SourceEntity sourceEntity : getSourceEntities()) {
			if (sourceEntity.selected) {
				selected.add(sourceEntity);
			}
		}
    	return selected;
    }
    
    /**
     * Método helper que obtiene el Símbolo o Nombre de una UM para ser
     * mostrado en la grilla.
     * @param uomID ID de la UM
     * @return {@link MUOM#getUOMSymbol()} si no es null o {@link MUOM#getName()}
     */
    protected String getUOMName(int uomID) {
		MUOM uom = MUOM.get(getCtx(), uomID);
		return uom.getUOMSymbol() != null && uom.getUOMSymbol().length() > 0 
			? uom.getUOMSymbol() 
			: uom.getName();
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
        
    /**
     * Esta es la superclase abstracta de todas las entidades que pueden ser origen
     * para la creación de un documento. Una entidad origen puede ser por ejemplo una
     * línea de pedido o factura, un pago, o cualquier PO de la aplicación. A partir
     * de los parámetros de la ventana, se cargan en la grilla un conjunto de 
     * entidades origen para la creación del documento destino. Por ejemplo, al seleccionar
     * un pedido en el VLookup parámetro, se cargan todas las líneas del pedido en la grilla
     * en donde una línea de pedido es una entidad origen para la creación del documento
     * destino.<br><br>
     * 
     * Las subclases de esta ventana puede especializar esta clase en caso de necesitar
     * cargar entidades origen que aún no estén soportadas.<br><br>
     * 
     * Las instancias concretas de esta clase (subclases en verdad) son el modelo que
     * está asociado a la grilla:<br><br>
     *  
     * <code>JTable --> CreateFromTableModel --> List< SourceEntity ></code> 
     */
    protected abstract class SourceEntity {
    	
    	/** Indica si esta entidad debe ser procesada o no */
    	protected Boolean selected = false;

    }

    /**
     * Entidad origen: líneas de un documento (Pedido, Remito, Factura).<br><br>
     * 
     * Esta clase es abstracta y contiene los atributos compartidos por una línea
     * de pedido, remito y factura debido a que su estructura es muy similar.
     * 
     */
    protected abstract class DocumentLine extends SourceEntity {
    	/** Número de línea en el documento original (Columna Line) */
    	protected Integer lineNo = 0;
		/** Código del artículo asociado a la línea */
		protected String itemCode = null;
		/** Nombre de la instancia */
		protected String instanceName = null;
    	/** Nombre del artículo o cargo asociado a la línea */
    	protected String productName = null;
    	/** Tipo del artículo o cargo asociado a la línea */
    	protected String productType = null;
    	/** ID del artículo o cargo asociado a la línea */
    	protected Integer productID = 0;
    	/** Nombre o Descripción de la UM indicada en la línea */
    	protected String uomName = null;
    	/** ID de la UM indicada en la línea */
    	protected Integer uomID = 0;
    	/** Cantidad total de la línea. Para facturas es igual al valor de
    	 * QtyInvoiced, para pedidos es QtyOrdered y para remitos MovementQty. */
    	protected BigDecimal lineQty = BigDecimal.ZERO;
    	/** Cantidad pendiente de la línea. Esta es la cantidad que efectivamente
    	 * se debe utilizar para crear la línea del documento en cuestión. Esta
    	 * cantidad es menor o igual que <code>lineQty</code>  */
    	protected BigDecimal remainingQty = BigDecimal.ZERO;
    	/** Descripción de la línea del documento */
    	protected String description = null;

		/**
    	 * @return Indica si esta entidad es una línea de un pedido
    	 */
    	public boolean isOrderLine() {
    		return false; 
    	}
    	
    	/**
    	 * @return Indica si esta entidad es una línea de un remito
    	 */
    	public boolean isInOutLine() {
    		return false;
    	}

    	/**
    	 * @return Indica si esta entidad es una línea de una factura
    	 */
    	public boolean isInvoiceLine() {
    		return false;
    	}
    }
    
    /**
     * Entidad Origen: Línea de Pedido<br><br>
     *
     * Clase concreta de una entidad origen que contiene la referencia a una línea
     * de pedido.
     */
    protected class OrderLine extends DocumentLine {
    	/** ID de la línea de pedido */
    	protected int orderLineID = 0;
    	/** Cantidad factura de la línea */
    	protected BigDecimal qtyDelivered = BigDecimal.ZERO;
    	/** Cantidad entregada/recibida de la línea */
    	protected BigDecimal qtyInvoiced = BigDecimal.ZERO;
    	
    	/** DocumentNo del pedido */
		protected String documentNo = null;
		
		/** Fecha de la línea de pedido */
		protected Date dateOrderLine = null;

		@Override
		public boolean isOrderLine() {
			return true;
		}
    }
    
    /**
     * Invoca la operación de guardado de la clase concreta
     * realizando el manejo de transacción de BD.
     */
    private void doSave() {
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
        } catch (CreateFromSaveException e) {
			trx.rollback();
			ADialog.error(p_WindowNo, this, "Error", e.getMessage());
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
		return (CreateFromTableModel)dataTable.getModel();
	}
	
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
	protected abstract class CreateFromTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -8392194814827177806L;

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_SELECTION = 0;
    	
    	/** Entidades Origen que muestra la grilla */
    	private List<? extends SourceEntity> sourceEntities;
    	/** Nombres de los encabezados de las columnas */
    	private Map<Integer,String> columnNames;
    	/** Clases de las columnas */
    	private Map<Integer,Class<?>> columnClasses;
    	/** Posibilidad de edición de las columnas */
    	private Map<Integer,Boolean> columnEditable;
    	
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
            sourceEntities = new ArrayList<SourceEntity>();
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
    	
		@Override
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
				fireTableDataChanged();
			} else {
				super.setValueAt(aValue, rowIndex, columnIndex);
			}
		}

		@Override
		public String getColumnName(int column) {
			return getColumnNames().get(column);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			Class<?> clazz = null;
			if (getColumnClasses().containsKey(columnIndex)) {
				clazz = getColumnClasses().get(columnIndex);
			} else {
				clazz = super.getColumnClass(columnIndex);
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
		 * @return the sourceEntities
		 */
		public List<? extends SourceEntity> getSourceEntities() {
			return sourceEntities;
		}

		/**
		 * @param sourceEntities the sourceEntities to set
		 */
		public void setSourceEntities(List<? extends SourceEntity> sourceEntities) {
			this.sourceEntities = sourceEntities;
			fireTableDataChanged();
		}

    	/**
		 * @return the columnNames
		 */
		private Map<Integer,String> getColumnNames() {
			return columnNames;
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
    }
    
    /**
     * Excepción lanzada en casos de error en el guardado
     */
    @SuppressWarnings("serial")
	protected class CreateFromSaveException extends Exception {

		public CreateFromSaveException() {
			super();
		}

		public CreateFromSaveException(String message, Throwable cause) {
			super(message, cause);
		}

		public CreateFromSaveException(String message) {
			super(message);
		}
    }
    
    // Dado un attributeSetInstance_ID retorna:
    // El nombre de la instacia completo. Ejemplo: Para una remera con Talle: S y Color: B retorna S - B
    // La descripcion de M_AttributeSetInstance en caso que que la consulta no obtenga resultados. 
    // null si M_AttributeSetInstance_ID es 0
    protected String getInstanceName(int attributeSetInstance_ID){
		StringBuffer sql;
		String instanceName = null;

	    sql = new StringBuffer();
		sql.append("select t.value, u.seqno from M_AttributeSetInstance i ")
		.append("INNER JOIN M_AttributeSet s ON (s.M_AttributeSet_ID = i.M_AttributeSet_ID) ") 
		.append("LEFT JOIN M_AttributeUse u ON (u.M_AttributeSet_ID = s.M_AttributeSet_ID) ")
		.append("LEFT JOIN M_AttributeInstance t ON (t.M_Attribute_ID = u.M_Attribute_ID) ")
		.append("where (t.M_AttributeSetInstance_ID = "+ attributeSetInstance_ID +") ")
		.append("group by t.value, u.seqno ")
		.append("order by u.seqno");
		   
		log.finer( sql.toString());

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()){
    			instanceName = rs.getString("Value");
    			while( rs.next()) {
    				instanceName = instanceName + " - " + rs.getString("Value");
        		}
    			return instanceName;
    		}
    		else{
    			StringBuffer sql2;
    			sql2 = new StringBuffer();
    			sql2.append("select Description from M_AttributeSetInstance where (M_AttributeSetInstance_ID <> 0) AND (M_AttributeSetInstance_ID = "+ attributeSetInstance_ID +")");
    			pstmt = DB.prepareStatement( sql2.toString());
        		rs = pstmt.executeQuery();
        		if(rs.next()){
        			return rs.getString("Description");
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
		
		return instanceName;
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
    		if(!mostrarColumna){
    			if( (dataTable.getModel()) instanceof DocumentLineTableModelFromShipment ){
    				((DocumentLineTableModelFromShipment)dataTable.getModel()).visibles = ((DocumentLineTableModelFromShipment) dataTable.getModel()).visibles - 1; 
        			dataTable.getColumnModel().removeColumn(dataTable.getColumnModel().getColumn(DocumentLineTableModelFromShipment.COL_IDX_INSTANCE_NAME));	
    			}
    			else{
    				((DocumentLineTableModel)dataTable.getModel()).visibles = ((DocumentLineTableModel) dataTable.getModel()).visibles - 1; 
        			dataTable.getColumnModel().removeColumn(dataTable.getColumnModel().getColumn(DocumentLineTableModel.COL_IDX_INSTANCE_NAME));	
    			}
    		}
    	}
    }
 
}    // VCreateFrom



/*
 *  @(#)VCreateFrom.java   02.07.07
 *
 *  Fin del fichero VCreateFrom.java
 *
 *  Versión 2.2
 *
 */
