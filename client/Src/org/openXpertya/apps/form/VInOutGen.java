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



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ADialogDialog;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
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

public class VInOutGen extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    private static final String DETAIL = "Order_Detail";

	private static final String INFO = "Order_Info";

	private static final String DOC_TYPE = "C_DocType_ID";

	private static final String DOC_ACTION = "DocAction";

	/**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "En vInOutGen" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            fillPicks();
            jbInit();
            dynInit();
            frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
            //Añadido por ConSerTi para seleccionar todas las filas
            m_frame.getContentPane().add(allselectPane,BorderLayout.LINE_END);
            //Fin añadido
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
            splitPane.setDividerLocation(700);
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"init",ex );
        }
    }    // init

    private ListSelectionListener listSelectionListener = new ListSelectionListener() {

		public void valueChanged(ListSelectionEvent e) {
			int row = miniTable.getSelectedRow();
			
			if (row >= 0 && miniTable.getRowCount() > 0 && !e.getValueIsAdjusting()) { 
				try {
					int orderId = ((IDColumn) miniTable.getValueAt(row, 0)).getRecord_ID();
					showOrder(orderId);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			
			}
		}
    	
    };
    
    private final String ACTION_SEARCH = "Search";
    
    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private boolean m_selectionActive = true;

    /** Descripción de Campos */

    private String m_whereClause;

    /** Descripción de Campos */

    private Object m_M_Warehouse_ID = null;

    /** Descripción de Campos */

    private Object m_C_BPartner_ID = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VInOutGen.class );

    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    //  Añadido por ConSerti, para poder seleccionar todas las filas a la vez.
    private CTabbedPane allselectPane = new CTabbedPane();
    private CPanel selNorthPanel_aux = new CPanel();
    //Fin añadido
    /** Descripción de Campos */

    private CPanel selPanel = new CPanel();

    private CPanel selSouthPanel = new CPanel();
    
    private LayoutManager selSouthPanelLayout = new BorderLayout();
    
    private VLookup fieldDocType;
    
    private CLabel labelDocType = new CLabel(Msg.translate(Env.getCtx(), "C_DocTypeTarget_ID"));

    private VLookup fieldDocAction;
    
    private CLabel labelDocAction = new CLabel(Msg.translate(Env.getCtx(), "DocAction"));
    
    /** Descripción de Campos */

    private CPanel selNorthPanel = new CPanel();

    private CPanel docCommandPanel = new CPanel();
    
    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CLabel lWarehouse = new CLabel();

    /** Descripción de Campos */

    private VLookup fWarehouse;

    /** Descripción de Campos */

    private CLabel lBPartner = new CLabel();

    /** Descripción de Campos */

    private VLookup fBPartner;

    private CButton bSearch = new CButton(Msg.getMsg(Env.getCtx(), "Search"));
    
    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelGen = new ConfirmPanel( false,false,false,false,false,false,true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private CPanel genPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout genLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTextPane info = new CTextPane();

    /** Descripción de Campos */

    JPanel orderPanel = new JPanel();

    
    private JScrollPane scrollPane = new JScrollPane();

    private JPanel detailPanel = new JPanel();

    private JScrollPane detailScroll = new JScrollPane();
    
    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, orderPanel, detailPanel);
    
    
    /** Descripción de Campos */

    private MiniTable miniTable = new MiniTable();
   
    private MiniTable detail = new MiniTable();
    
    //  Añadido por ConSerti, para poder seleccionar todas las filas a la vez. 
    private CCheckBox automatico = new CCheckBox();
    //Fin añadido
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private JLabel lDetail = new JLabel(Msg.translate(Env.getCtx(), DETAIL));
    
    void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //
        selNorthPanel_aux.setLayout(new BorderLayout());
        selPanel.setLayout( selPanelLayout );
        lWarehouse.setLabelFor( fWarehouse );
        lBPartner.setLabelFor( fBPartner );
      //  lBPartner.setText( "BPartner" );
        selNorthPanel.setLayout( northPanelLayout );
        selSouthPanel.setLayout(selSouthPanelLayout);
        docCommandPanel.add(labelDocType);
        docCommandPanel.add(fieldDocType);
        docCommandPanel.add(labelDocAction);
        docCommandPanel.add(fieldDocAction);
        docCommandPanel.setLayout(new FlowLayout());
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.getMsg( Env.getCtx(),"Select" ));
        //Añadido por ConSerTi para la posicion de seleccionar todo
        selNorthPanel_aux.add(selNorthPanel,BorderLayout.SOUTH);
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
        selPanel.add( selSouthPanel, BorderLayout.SOUTH);
        
        //Fin añadido
        selNorthPanel.add( lWarehouse,null );
        selNorthPanel.add( fWarehouse,null );
        selNorthPanel.add( lBPartner,null );
        selNorthPanel.add( fBPartner,null );
        bSearch.setActionCommand(ACTION_SEARCH);
        bSearch.addActionListener(this);
        selNorthPanel.add(bSearch);
        selPanel.setName( "selPanel" );
        
        selSouthPanel.add(docCommandPanel, BorderLayout.WEST);
        selSouthPanel.add(confirmPanelSel,BorderLayout.EAST);
        selPanel.add( selSouthPanel,BorderLayout.SOUTH );
        //Modificado por ConSerTi para seleccionar una tabla entera
        automatico.setText("Seleccionar Todos");  
        automatico.setSelected(true);
        automatico.addActionListener(this);
       // selPanel.add(automatico);
        automatico.setEnabled(true);
        selNorthPanel_aux.add(automatico,BorderLayout.NORTH);
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
        //Fin Modificacion
        //selPanel.add( scrollPane,BorderLayout.CENTER );
        orderPanel.setLayout(new BorderLayout());
        orderPanel.add(new JLabel(Msg.translate(Env.getCtx(), "C_Order_ID")), BorderLayout.NORTH);
        orderPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.setLayout(new BorderLayout());
        detailPanel.add(lDetail, BorderLayout.NORTH);
        detailPanel.add( detailScroll, BorderLayout.CENTER);
        // Acá
        selPanel.add( splitPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( miniTable,null );
        detailScroll.getViewport().add( detail,null );
        confirmPanelSel.addActionListener( this );
        splitPane.setResizeWeight(0.25);        

        //splitPane.setDividerLocation(0.75);
        //

        tabbedPane.add( genPanel,Msg.getMsg( Env.getCtx(),"Generate" ));
        genPanel.setLayout( genLayout );
        genPanel.add( info,BorderLayout.CENTER );
        genPanel.setEnabled( false );
        info.setBackground( CompierePLAF.getFieldBackground_Inactive());
        info.setEditable( false );
        genPanel.add( confirmPanelGen,BorderLayout.SOUTH );
        confirmPanelGen.addActionListener( this );
        loadDefaults();
    }    // jbInit

    protected void showOrder(int orderId) {
		MOrder order = new MOrder(Env.getCtx(), orderId, null);
		MOrderLine[] lines = order.getLines();
		detail.setRowCount(lines.length);
		lDetail.setText(Msg.translate(Env.getCtx(), INFO) + " " + order.getDocumentNo());
		
		for (int i = 0; i < lines.length; i++) {
			MProduct product = new MProduct(Env.getCtx(), lines[i].getM_Product_ID(), null);
			detail.setValueAt(product.getName(), i, 0);
			detail.setValueAt(lines[i].getQtyOrdered().subtract(lines[i].getQtyDelivered()), i, 1);
		}
    }

    protected void clearOrder() {
		miniTable.clearSelection();
    	detail.setRowCount(0);
    	lDetail.setText(Msg.translate(Env.getCtx(), DETAIL));
    }
    
	/**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {

        // C_OrderLine.M_Warehouse_ID

        MLookup orgL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2223,DisplayType.TableDir );

        fWarehouse = new VLookup( "M_Warehouse_ID",true,false,true,orgL );
        lWarehouse.setText( Msg.translate( Env.getCtx(),"M_Warehouse_ID" ));
        fWarehouse.addVetoableChangeListener( this );
        //Nueva linea, añadida por ConSerTi 05/01/2007.
        m_M_Warehouse_ID = fWarehouse.getValue();

        // C_Order.C_BPartner_ID

        MLookup bpL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2762,DisplayType.Search );

        fBPartner = new VLookup( "C_BPartner_ID",false,false,true,bpL );
        lBPartner.setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        fBPartner.addVetoableChangeListener( this );


        M_Table tableInOut = M_Table.get(Env.getCtx(), MInOut.Table_ID); 
        M_Column dtCol = tableInOut.getColumn("C_DocType_ID");
        MLookup dtLU = MLookupFactory.get( Env.getCtx(), m_WindowNo, 0, dtCol.getAD_Column_ID(), dtCol.getAD_Reference_ID()); 
        fieldDocType = new VLookup("C_DocTypeTarget_ID", true, false, true, dtLU);
    

        M_Column daCol = tableInOut.getColumn("DocAction");
        MLookup daLU = MLookupFactory.get( Env.getCtx(), m_WindowNo, 0, daCol.getAD_Column_ID(), DisplayType.List); 
        fieldDocAction = new VLookup("DocAction", true, false, true, daLU);

        
    }    // fillPicks

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // create Columns

        miniTable.addColumn( "C_Order_ID" );
        miniTable.addColumn( "AD_Org_ID" );
        miniTable.addColumn( "Type" );
        miniTable.addColumn( "DocumentNo" );
        miniTable.addColumn( "C_BPartner_ID" );
        miniTable.addColumn( "DateOrdered" );
        miniTable.addColumn( "TotalLines" );
        miniTable.addColumn( "DatePromised" );
        miniTable.addColumn( "DeliveryRule" );
        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"Type" ));
        miniTable.setColumnClass( 3,String.class,true,Msg.translate( Env.getCtx(),"DocumentNo" ));
        miniTable.setColumnClass( 4,String.class,true,Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        miniTable.setColumnClass( 5,Timestamp.class,true,Msg.translate( Env.getCtx(),"DateOrdered" ));
        miniTable.setColumnClass( 6,BigDecimal.class,true,Msg.translate( Env.getCtx(),"TotalLines" ));
        miniTable.setColumnClass( 7, Timestamp.class, true, Msg.translate( Env.getCtx(), "DatePromised"));
        miniTable.setColumnClass( 8, String.class, true, Msg.translate(Env.getCtx(), "DeliveryRule"));

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"InOutGenerateSel" ));    // @@
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );

        tabbedPane.setEnabled(false);
        
        detail.addColumn(Msg.translate(Env.getCtx(), "M_Product_ID"));
        detail.addColumn(Msg.translate(Env.getCtx(), "Qty"));
        detail.setColumnClass(0, String.class,true);
        detail.setColumnClass(1, BigDecimal.class, true);

        miniTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {

    	clearOrder();
        //splitPane.setDividerLocation(0.75);

        log.info( "En ExecuteQuery de VInOutGen" );

        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
        

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT C_Order_ID, o.Name, dt.Name, DocumentNo, bp.Name, DateOrdered, TotalLines, DatePromised, ic.DeliveryRule " + "FROM M_InOut_Candidate_v ic, AD_Org o, C_BPartner bp, C_DocType dt " + "WHERE ic.AD_Org_ID=o.AD_Org_ID" + " AND ic.C_BPartner_ID=bp.C_BPartner_ID" + " AND ic.C_DocType_ID=dt.C_DocType_ID" + " AND ic.AD_Client_ID=?" );

        if( m_M_Warehouse_ID != null ) {
            sql.append( " AND ic.M_Warehouse_ID=" ).append( m_M_Warehouse_ID );
        }

        if( m_C_BPartner_ID != null ) {
            sql.append( " AND ic.C_BPartner_ID=" ).append( m_C_BPartner_ID );
        }

        //

        sql.append( " ORDER BY o.Name,bp.Name,DateOrdered" );
        log.fine( sql.toString());

        // reset table

        int row = 0;

        miniTable.setRowCount( row );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values

                String deliveryRule = "";
                
                String sqlDeliveryRule = " SELECT t.name FROM AD_Ref_List l INNER JOIN AD_Ref_List_Trl t ON t.AD_Ref_list_ID = l.AD_Ref_List_ID WHERE l.AD_Reference_ID = 151 AND l.Value = ? AND t.ad_language = ? ";   
                CPreparedStatement pstmDR = DB.prepareStatement(sqlDeliveryRule);
                pstmDR.setString(1, rs.getString(9));
                pstmDR.setString(2, Env.getAD_Language(Env.getCtx()));

                ResultSet rsDR = pstmDR.executeQuery();
                
                if (rsDR.next()) {
                	deliveryRule = rsDR.getString("name");
                } else {
                	MRefList ref = MRefList.get(Env.getCtx(), 151, rs.getString(9), null);
                	if (ref != null) {
                		deliveryRule = ref.getName();
                	}
                
                }
                
                miniTable.setValueAt( new IDColumn( rs.getInt( 1 )),row,0 );    // C_Order_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );       // Org
                miniTable.setValueAt( rs.getString( 3 ),row,2 );       // DocType
                miniTable.setValueAt( rs.getString( 4 ),row,3 );       // Doc No
                miniTable.setValueAt( rs.getString( 5 ),row,4 );       // BPartner
                miniTable.setValueAt( rs.getTimestamp( 6 ),row,5 );    // DateOrdered
                miniTable.setValueAt( rs.getBigDecimal( 7 ),row,6 );    // TotalLines
                miniTable.setValueAt( rs.getTimestamp( 8 ), row, 7);
                miniTable.setValueAt( deliveryRule, row, 8);
                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        //

        miniTable.autoSize();

        // statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));

    }    // executeQuery

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

//  Añadido por ConSerTi para seleccionar todas las columnas
    public void seleccionarTodos() {
        for( int i = 0;i < miniTable.getRowCount();i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );
                      id.setSelected(automatico.isSelected());
                     // miniTable.setValueAt( new Boolean( automatico.isSelected()),i,6 );

            miniTable.getModel().setValueAt( id,i,0 );
        }
    }
    //Fin añadido.
    
    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( "Cmd=" + e.getActionCommand());

       
        //Añadido por ConSerTi, para seleccionar todas las filas.
        if (e.getSource().equals(automatico))
                     seleccionarTodos();
        //fin Añadido

        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
            return;
        }
        else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			m_whereClause = saveSelection();
			log.info("m_whereClause.length= " + m_whereClause.length()+" m_selectionActive = "+ m_selectionActive);
			if (m_whereClause.length() > 0 && m_selectionActive)
				generateShipments ();
			else {
				executeQuery();
				tabbedPane.setSelectedIndex(0);
				//dispose();
			}
		} else if (e.getActionCommand().equals(ACTION_SEARCH)) {
			executeQuery();			
		}

        //

       /* m_whereClause = saveSelection();

        if( (m_whereClause.length() > 0) && m_selectionActive && (m_M_Warehouse_ID != null) ) {
            generateShipments();
        } else {
            dispose();
        }
        
        else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			m_whereClause = saveSelection();
			if (m_whereClause.length() > 0 && m_selectionActive)
				generateShipments ();
			else
				dispose();
		}*/
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.info("en VetoAbleChange" +e.getPropertyName() + "=" + e.getNewValue());

        if( e.getPropertyName().equals( "M_Warehouse_ID" )) {
            m_M_Warehouse_ID = e.getNewValue();
        }

        if( e.getPropertyName().equals( "C_BPartner_ID" )) {
            m_C_BPartner_ID = e.getNewValue();
            fBPartner.setValue( m_C_BPartner_ID );    // display value
        }

        //executeQuery();
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
    	log.info("En stateChanged con index= "+ tabbedPane.getSelectedIndex() );
        int index = tabbedPane.getSelectedIndex();

        m_selectionActive = ( index == 0 );
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        int rowsSelected = 0;
        int rows         = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            if( (id != null) && id.isSelected()) {
                rowsSelected++;
            }
        }

        statusBar.setStatusDB( " " + rowsSelected + " " );
    }    // tableChanged

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String saveSelection() {
        log.info( "saveSelection" );

        // ID selection may be pending

        miniTable.editingStopped( new ChangeEvent( this ));

        // Array of Integers

        ArrayList results = new ArrayList();

        // Get selected entries

        int rows = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            // log.fine( "Row=" + i + " - " + id);

            if( (id != null) && id.isSelected()) {
                results.add( id.getRecord_ID());
            }
        }

        if( results.size() == 0 ) {
            return "";
        }

        // Query String

        String       keyColumn = "C_Order_ID";
        StringBuffer sb        = new StringBuffer( keyColumn );

        if( results.size() > 1 ) {
            sb.append( " IN (" );
        } else {
            sb.append( "=" );
        }

        // Add elements

        for( int i = 0;i < results.size();i++ ) {
            if( i > 0 ) {
                sb.append( "," );
            }

            if( keyColumn.endsWith( "_ID" )) {
                sb.append( results.get( i ).toString());
            } else {
                sb.append( "'" ).append( results.get( i ).toString());
            }
        }

        if( results.size() > 1 ) {
            sb.append( ")" );
        }

        //

        log.config(" retornando = "+sb.toString());

        return sb.toString();
    }    // saveSelection

    /**
     * Descripción de Método
     *
     */

    private void generateShipments() {
        log.info( "En generateShipments M_Warehouse_ID=" + m_M_Warehouse_ID );

        saveDefaults();
        
        // String trxName = Trx.createTrxName("IOG");
        // Trx trx = Trx.get(trxName, true);       trx needs to be committed too

        String trxName = null;
        Trx    trx     = null;

        // Reset Selection

        String sql = "UPDATE C_Order SET IsSelected = 'N' " + "WHERE IsSelected='Y'" + " AND AD_Client_ID=" + Env.getAD_Client_ID( Env.getCtx());
        int no = DB.executeUpdate( sql,trxName );

        log.config( "Reset=" + no );

        // Set Selection

        sql = "UPDATE C_Order SET IsSelected='Y' WHERE " + m_whereClause;
        no  = DB.executeUpdate( sql,trxName );
        log.fine( sql );

        if( no == 0 ) {
            String msg = "No Shipments";    // not translated!

            log.config( msg );
            info.setText( msg );

            return;
        }

        log.config( "Set=" + no );
        m_selectionActive = false;    // prevents from being called twice
        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"InOutGenerateGen" ));
        statusBar.setStatusDB( String.valueOf( no ));

        // Prepare Process

        int        AD_Process_ID = 199;    // HARDCODED    M_InOutCreate
        MPInstance instance      = new MPInstance( Env.getCtx(),AD_Process_ID,0,null );

        if( !instance.save()) {
            info.setText( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));

            return;
        }

        ProcessInfo pi = new ProcessInfo( "",AD_Process_ID );

        pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

        // Add Parameter - Selection=Y

        MPInstancePara ip = new MPInstancePara( instance,10 );

        ip.setParameter( "Selection","Y" );
        
        if( !ip.save()) {
            String msg = "No Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        }

        MPInstancePara pDocType = new  MPInstancePara( instance, 20 );
        pDocType.setParameter( "C_DocType_ID", (Integer) fieldDocType.getValue());
        
        if (!pDocType.save()) {
            String msg = "No Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        	
        }
        
        
        MPInstancePara pAction = new MPInstancePara( instance, 30 );
        pAction.setParameter( "DocAction" , (String) fieldDocAction.getValue());
        
        if (!pAction.save()) {
            String msg = "No Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        	
        }

        

        // Add Parameter - M_Warehouse_ID=x

        ip = new MPInstancePara( instance,40 );
        ip.setParameter( "M_Warehouse_ID",Integer.parseInt( m_M_Warehouse_ID.toString()));

        if( !ip.save()) {
            String msg = "No Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        }

        // Execute Process

        ProcessCtl worker = new ProcessCtl( this,pi,trx );

        worker.start();    // complete tasks in unlockUI / generateShipments_complete

        //

    }    // generateShipments

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void generateShipments_complete( ProcessInfo pi ) {

        // Switch Tabs

        tabbedPane.setSelectedIndex( 1 );

        //

        ProcessInfoUtil.setLogFromDB( pi );

        StringBuffer iText = new StringBuffer();

        iText.append( "<b>" ).append( pi.getSummary()).append( "</b><br>(" ).append( Msg.getMsg( Env.getCtx(),"InOutGenerateInfo" ))

        // Shipments are generated depending on the Delivery Rule selection in the Order

        //.append( ")<br>" ).append( pi.getLogInfo( true ));
        .append( ")<br>" );
        info.setText( iText.toString());

        // Reset Selection

        statusBar.setStatusLine("");
        
        String sql = "UPDATE C_Order SET IsSelected='N' WHERE " + m_whereClause;
        int no = DB.executeUpdate( sql );

        log.config( "VInOutGen.generateShipments_complete - Reset=" + no );

        // Get results

        int[] ids = pi.getIDs();

        if( (ids == null) || (ids.length == 0) ) {
            return;
        }

        log.config( "VInOutGen.generateShipments_complete - PrintItems=" + ids.length );
        confirmPanelGen.getOKButton().setEnabled( false );

        // OK to print shipments

        if( ADialog.ask( m_WindowNo,this,"PrintShipments" )) {

            // info.append("\n\n" + Msg.getMsg(Env.getCtx(), "PrintShipments"));

            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

            int retValue = ADialogDialog.A_CANCEL;    // see also ProcessDialog.printShipments/Invoices

            do {

                // Loop through all items

                for( int i = 0;i < ids.length;i++ ) {
                    int M_InOut_ID = ids[ i ];

                    ReportCtl.startDocumentPrint( ReportEngine.SHIPMENT,M_InOut_ID,true );
                }

                ADialogDialog d = new ADialogDialog( m_frame,Env.getHeader( Env.getCtx(),m_WindowNo ),Msg.getMsg( Env.getCtx(),"PrintoutOK?" ),JOptionPane.QUESTION_MESSAGE );

                retValue = d.getReturnCode();
            } while( retValue == ADialogDialog.A_CANCEL );

            setCursor( Cursor.getDefaultCursor());
        }    // OK to print shipments

        //

        confirmPanelGen.getOKButton().setEnabled( true );
    }    // generateShipments_complete

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        this.setEnabled( false );
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {
        this.setEnabled( true );
        this.setCursor( Cursor.getDefaultCursor());

        //

        generateShipments_complete( pi );
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return this.isEnabled();
    }    // isUILocked


	private void saveDefaults() {
		
		int userId = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
		MPreference pDocType = MPreference.getUserPreference(Env.getCtx(), DOC_TYPE, null);
		MPreference pDocAction = MPreference.getUserPreference(Env.getCtx(), DOC_ACTION, null);
		
		if (pDocType == null) {
			pDocType = new MPreference(Env.getCtx(), DOC_TYPE, fieldDocType.getValue().toString(), null);
			pDocType.setAD_User_ID(userId);
		} else {
			pDocType.setValue(fieldDocType.getValue().toString());
		}

		if (pDocAction == null) {
			pDocAction = new MPreference(Env.getCtx(), DOC_ACTION, fieldDocAction.getValue().toString(), null);
			pDocAction.setAD_User_ID(userId);
		} else {
			pDocAction.setValue(fieldDocAction.getValue().toString());
		}


		pDocType.save();
		pDocAction.save();
		
	}

	private void loadDefaults() {
		MPreference pDocType = MPreference.getUserPreference(Env.getCtx(), DOC_TYPE, null);
		MPreference pDocAction = MPreference.getUserPreference(Env.getCtx(), DOC_ACTION, null);
		if (pDocType != null) {
			fieldDocType.setValue(Integer.parseInt(pDocType.getValue()));
		}
		
		if (pDocAction != null) {
			fieldDocAction.setValue(pDocAction.getValue());
		}
	}
	
	
    public void executeASync( ProcessInfo pi ) {}    // executeASync
}    // VInOutGen



/*
 *  @(#)VInOutGen.java   02.07.07
 *
 *  Fin del fichero VInOutGen.java
 *
 *  Versión 2.2
 *
 */
