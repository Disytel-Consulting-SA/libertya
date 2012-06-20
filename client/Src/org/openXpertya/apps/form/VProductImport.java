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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.X_I_Product;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
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

public class VProductImport extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public VProductImport() {
        m_AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
    }

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "VProductImport.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            fillPicks();
            jbInit();
            dynInit();
            m_frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
            m_frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
            importProductBegin();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VProductImport,init",ex );
        }
    }

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private boolean begin = true;

    /** Descripción de Campos */

    private int m_AD_Client_ID;

    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel selPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanelSel = new CPanel();

    /** Descripción de Campos */

    private CPanel newPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout newPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanelNew = new CPanel();

    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( true,false,false,false,false,true,true );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelNew = new ConfirmPanel( true,false,false,false,false,true,true );

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

    private JScrollPane scrollPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable miniTable = new MiniTable();

    /** Descripción de Campos */

    private JScrollPane scrollNewPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable miniNewTable = new MiniTable();

    /** Descripción de Campos */

    private CCheckBox automatico = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox autoNuevo = new CCheckBox();

    /** Descripción de Campos */

    private Object m_M_Product_ID;

    /** Descripción de Campos */

    private CLabel lProduct = new CLabel();

    /** Descripción de Campos */

    private VLookup fProduct;

    /** Descripción de Campos */

    private CLabel lAsignar = new CLabel();

    /** Descripción de Campos */

    private CButton m_AsignarProducto = new CButton( Env.getImageIcon( "Last24.gif" ));

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VProductImport.class );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {
        MLookup proL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,1402,DisplayType.Search );

        fProduct = new VLookup( "M_Product_ID",false,false,true,proL );
        fProduct.addVetoableChangeListener( this );
    }

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //

        selPanel.setLayout( selPanelLayout );
        automatico.setText( "Seleccionar Todos" );    // A�adido por ConSerTi para seleccionar una tabla entera
        automatico.setSelected( true );
        automatico.addActionListener( this );
        autoNuevo.setText( "Seleccionar Todos" );    // A�adido por ConSerTi para seleccionar una tabla entera
        autoNuevo.setSelected( true );
        autoNuevo.addActionListener( this );
        northPanelSel.setLayout( northPanelLayout );
        northPanelNew.setLayout( northPanelLayout );
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.getMsg( Env.getCtx(),"Select" ));
        selPanel.setName( "selPanel" );
        selPanel.add( confirmPanelSel,BorderLayout.SOUTH );
        selPanel.add( scrollPane,BorderLayout.CENTER );
        selPanel.add( northPanelSel,BorderLayout.NORTH );
        northPanelSel.add( automatico );
        scrollPane.getViewport().add( miniTable,null );
        confirmPanelSel.addActionListener( this );

        //

        tabbedPane.add( newPanel,Msg.getMsg( Env.getCtx(),"Nuevos" ));
        newPanel.setLayout( newPanelLayout );
        newPanel.add( confirmPanelNew,BorderLayout.SOUTH );
        newPanel.add( scrollNewPane,BorderLayout.CENTER );
        newPanel.add( northPanelNew,BorderLayout.NORTH );
        northPanelNew.add( autoNuevo );
        lProduct.setText( Msg.translate( Env.getCtx(),"Product" ));
        lProduct.setLabelFor( fProduct );
        northPanelNew.add( lProduct );
        northPanelNew.add( fProduct );
        lAsignar.setText( Msg.translate( Env.getCtx(),"Asignar" ));
        lAsignar.setLabelFor( m_AsignarProducto );
        northPanelNew.add( lAsignar );
        northPanelNew.add( m_AsignarProducto );
        m_AsignarProducto.setToolTipText( Msg.getMsg( Env.getCtx(),"Asignar producto existente a nuevo producto" ));
        m_AsignarProducto.addActionListener( this );
        scrollNewPane.getViewport().add( miniNewTable,null );
        confirmPanelNew.addActionListener( this );
        tabbedPane.add( genPanel,Msg.getMsg( Env.getCtx(),"Generate" ));
        genPanel.setLayout( genLayout );
        genPanel.add( info,BorderLayout.CENTER );
        genPanel.setEnabled( false );
        info.setBackground( CompierePLAF.getFieldBackground_Inactive());
        info.setEditable( false );
        genPanel.add( confirmPanelGen,BorderLayout.SOUTH );
        confirmPanelGen.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // create Columns

        miniTable.addColumn( "I_Product_ID" );
        miniTable.addColumn( "Value" );
        miniTable.addColumn( "Name" );
        miniTable.addColumn( "UPC" );
        miniTable.addColumn( "M_Product_ID" );

        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"Value" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"Name" ));
        miniTable.setColumnClass( 3,String.class,true,Msg.translate( Env.getCtx(),"UPC" ));
        miniTable.setColumnClass( 4,String.class,true,Msg.translate( Env.getCtx(),"M_Product_ID" ));

        //

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // create Columns

        miniNewTable.addColumn( "I_Product_ID" );
        miniNewTable.addColumn( "Value" );
        miniNewTable.addColumn( "Name" );
        miniNewTable.addColumn( "UPC" );
        miniNewTable.addColumn( "M_Product_ID" );
        miniNewTable.addColumn( "Value" );

        //

        miniNewTable.setMultiSelection( true );
        miniNewTable.setRowSelectionAllowed( true );

        // set details

        miniNewTable.setColumnClass( 0,IDColumn.class,false," " );
        miniNewTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"Value" ));
        miniNewTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"Name" ));
        miniNewTable.setColumnClass( 3,String.class,true,Msg.translate( Env.getCtx(),"UPC" ));
        miniNewTable.setColumnClass( 4,String.class,true,Msg.translate( Env.getCtx(),"M_Product_ID" ));
        miniNewTable.setColumnClass( 5,String.class,true,Msg.translate( Env.getCtx(),"Value" ));

        //

        miniNewTable.autoSize();
        miniNewTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"MProduct" ));
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {
        log.info( "VProductImport.executeQuery" );

        int row = 0;

        miniTable.setRowCount( row );

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT I_Product_ID, value, name, upc, m_product_id" );

        sql.append( " FROM I_Product WHERE m_product_id IS NOT NULL AND I_IsImported<>'Y' AND AD_Client_ID=" );
        sql.append( m_AD_Client_ID );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values

                IDColumn m_idColumn = new IDColumn( rs.getInt( 1 ));

                m_idColumn.setSelected( true );
                miniTable.setValueAt( m_idColumn,row,0 );           // M_Product_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );    // Value
                miniTable.setValueAt( rs.getString( 3 ),row,2 );    // Name
                miniTable.setValueAt( rs.getString( 4 ),row,3 );    // UPC
                miniTable.setValueAt( rs.getString( 5 ),row,4 );    // M_Product_ID

                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VProductImport.executeQuery " + sql.toString(),e );
        }

        //

        miniTable.autoSize();
        statusBar.setStatusDB( String.valueOf( miniTable.getRowCount()));
        automatico.setSelected( true );
    }    // executeQuery

    /**
     * Descripción de Método
     *
     */

    private void executeNewQuery() {
        log.info( "VProductImport.executeNewQuery" );

        int row = 0;

        miniNewTable.setRowCount( row );

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT I_Product_ID, value, name, upc, '', ''" );

        sql.append( " FROM I_Product WHERE m_product_id IS NULL AND I_IsImported<>'Y' AND AD_Client_ID=" );
        sql.append( m_AD_Client_ID );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniNewTable.setRowCount( row + 1 );

                // set values

                IDColumn m_idColumn = new IDColumn( rs.getInt( 1 ));

                m_idColumn.setSelected( true );
                miniNewTable.setValueAt( m_idColumn,row,0 );           // M_Product_ID
                miniNewTable.setValueAt( rs.getString( 2 ),row,1 );    // Value
                miniNewTable.setValueAt( rs.getString( 3 ),row,2 );    // Name
                miniNewTable.setValueAt( rs.getString( 4 ),row,3 );    // UPC
                miniNewTable.setValueAt( rs.getString( 5 ),row,4 );    // M_Product_ID
                miniNewTable.setValueAt( rs.getString( 6 ),row,5 );

                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VProductImport.executeNewQuery " + sql.toString(),e );
        }

        //

        miniNewTable.autoSize();
        statusBar.setStatusDB( String.valueOf( miniNewTable.getRowCount()));
        autoNuevo.setSelected( true );
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

    /**
     * Descripción de Método
     *
     */

    public void seleccionarTodos() {
        for( int i = 0;i < miniTable.getRowCount();i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );

            id.setSelected( automatico.isSelected());
            miniTable.getModel().setValueAt( id,i,0 );
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void seleccionarNuevos() {
        for( int i = 0;i < miniNewTable.getRowCount();i++ ) {
            IDColumn id = ( IDColumn )miniNewTable.getModel().getValueAt( i,0 );

            id.setSelected( autoNuevo.isSelected());
            miniNewTable.getModel().setValueAt( id,i,0 );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( "VProductImport.actionPerformed - " + e.getActionCommand());

        //

        if( e.getSource().equals( automatico )) {
            seleccionarTodos();
        } else if( e.getSource().equals( autoNuevo )) {
            seleccionarNuevos();
        }

        if( e.getActionCommand().equals( ConfirmPanel.A_ZOOM )) {
            if( tabbedPane.getSelectedIndex() == 0 ) {
                zoom();
            } else {
                zoomNuevo();
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            if( tabbedPane.getSelectedIndex() == 1 ) {
                tabbedPane.setSelectedIndex( 0 );
            } else {
                dispose();
            }

            return;
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            int panel = tabbedPane.getSelectedIndex();

            if( panel == 0 ) {
                updateIt();
            } else if( panel == 1 ) {
                updateNew();
                importProducts();
            } else {
                dispose();
            }
        }

        if( e.getSource().equals( m_AsignarProducto )) {
            cmd_AsignarProducto();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void cmd_AsignarProducto() {
        if( m_M_Product_ID != null ) {
            int row = miniNewTable.getSelectedRow();

            if( row != -1 ) {
                miniNewTable.setValueAt( m_M_Product_ID.toString(),row,4 );

                MProduct prod = new MProduct( Env.getCtx(),(( Integer )m_M_Product_ID ).intValue(),null );

                miniNewTable.setValueAt( prod.getValue(),row,5 );
                m_M_Product_ID = null;
                fProduct.setValue( m_M_Product_ID );
            }
        }
    }

    /**
     * Descripción de Método
     *
     */

    private void zoom() {
        log.info( "VProductImport.zoom" );

        Integer M_Product_ID = getSelectedRowKey();

        if( M_Product_ID == null ) {
            return;
        }

        AEnv.zoom( MProduct.Table_ID,M_Product_ID.intValue());
    }

    /**
     * Descripción de Método
     *
     */

    private void zoomNuevo() {
        log.info( "VProductImport.zoomNuevo" );

        Integer M_Product_ID = getNewSelectedRowKey();

        if( M_Product_ID == null ) {
            return;
        }

        AEnv.zoom( X_I_Product.Table_ID,M_Product_ID.intValue());
    }

    // a�adido para posibilitar el zoom a pedidos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    // hasZoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected Integer getSelectedRowKey() {
        int row = miniTable.getSelectedRow();

        if( row != -1 ) {
            Object data = miniTable.getModel().getValueAt( row,0 );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // a�adido para posibilitar el zoom a procuctos importados

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Integer getNewSelectedRowKey() {
        int row = miniNewTable.getSelectedRow();

        if( row != -1 ) {
            Object data = miniNewTable.getModel().getValueAt( row,0 );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // a�adido para posibilitar el zoom a pedidos

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.info( "VProductImport.vetoableChange - " + e.getPropertyName() + "=" + e.getNewValue());

        if( e.getPropertyName().equals( "M_Product_ID" )) {
            m_M_Product_ID = e.getNewValue();
            fProduct.setValue( m_M_Product_ID );
        }
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        int index = tabbedPane.getSelectedIndex();
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
     */

    private void importProducts() {

        // Prepare Process

        int proc_ID = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='ImportProductEnd'" );

        if( proc_ID != -1 ) {
            MPInstance instance = new MPInstance( Env.getCtx(),proc_ID,0,null );

            if( !instance.save()) {
                info.setText( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));

                return;
            }

            ProcessInfo pi = new ProcessInfo( "Importar Productos",proc_ID );

            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

            // Add Parameter - AD_Client_ID=x

            MPInstancePara ip = new MPInstancePara( instance,10 );

            ip.setParameter( "AD_Client_ID",String.valueOf( m_AD_Client_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Execute Process

            ProcessCtl worker = new ProcessCtl( this,pi,null );

            worker.start();    // complete tasks in unlockUI / afterCreateProductPrices
        }

        confirmPanelGen.getOKButton().setEnabled( false );
    }

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void afterImportProductEnd( ProcessInfo pi ) {

        // Switch Tabs

        tabbedPane.setSelectedIndex( 2 );

        //

        ProcessInfoUtil.setLogFromDB( pi );

        StringBuffer iText = new StringBuffer();

        iText.append( "</b><br>" ).append( "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"2\">" );
        iText.append( "</hr>" );
        iText.append( pi.getSummary());
        iText.append( "</table>" );
        info.setText( iText.toString());
        confirmPanelGen.getOKButton().setEnabled( true );
    }

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

        if( begin ) {
            afterImportProductBegin( pi );
        } else {
            afterImportProductEnd( pi );
        }
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

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {}    // executeASync

    /**
     * Descripción de Método
     *
     */

    private void importProductBegin() {

        // EJECUTAR PROCESO IMPORT PRODUCT BEGIN
        // Prepare Process

        int proc_ID = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='ImportProductBegin'" );

        if( proc_ID != -1 ) {
            MPInstance instance = new MPInstance( Env.getCtx(),proc_ID,0,null );

            if( !instance.save()) {
                info.setText( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));

                return;
            }

            ProcessInfo pi = new ProcessInfo( "Preparar Impotación",proc_ID );

            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

            // Add Parameter - AD_Client_ID=x

            MPInstancePara ip = new MPInstancePara( instance,10 );

            ip.setParameter( "AD_Client_ID",String.valueOf( m_AD_Client_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Execute Process

            ProcessCtl worker = new ProcessCtl( this,pi,null );

            worker.start();    // complete tasks in unlockUI / afterCreateProductPrices
        }
    }    // importProductBegin

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void afterImportProductBegin( ProcessInfo pi ) {

        // Switch Tabs

        tabbedPane.setSelectedIndex( 0 );

        //

        ProcessInfoUtil.setLogFromDB( pi );

        //

        begin = false;
        executeQuery();
    }

    /**
     * Descripción de Método
     *
     */

    private void updateIt() {

        // ID selection may be pending

        miniTable.editingStopped( new ChangeEvent( this ));

        // Array of Integers

        ArrayList resultsSelected    = new ArrayList();
        ArrayList resultsNotSelected = new ArrayList();

        // Get selected entries

        int rows = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            if( id != null ) {
                if( id.isSelected()) {
                    resultsSelected.add( id.getRecord_ID());
                } else {
                    resultsNotSelected.add( id.getRecord_ID());
                }
            }
        }

        int no;

        // Selected

        if( resultsSelected.size() != 0 ) {
            StringBuffer sbSelected = new StringBuffer( "UPDATE I_Product SET I_IsImported='N' WHERE I_Product_ID" );

            if( resultsSelected.size() > 1 ) {
                sbSelected.append( " IN (" );
            } else {
                sbSelected.append( " = " );
            }

            for( int i = 0;i < resultsSelected.size();i++ ) {
                if( i > 0 ) {
                    sbSelected.append( "," );
                }

                sbSelected.append( resultsSelected.get( i ).toString());
            }

            if( resultsSelected.size() > 1 ) {
                sbSelected.append( ")" );
            }

            try {
                no = DB.executeUpdate( sbSelected.toString());
            } catch( Exception e ) {
                log.log( Level.SEVERE,"VProductImport.updateIt " + e );
            }
        }

        // NotSelected

        if( resultsNotSelected.size() != 0 ) {
            StringBuffer sbNotSelected = new StringBuffer( "UPDATE I_Product SET I_IsImported='Y', I_ERRORMSG='Not Selected' WHERE I_Product_ID" );

            if( resultsNotSelected.size() > 1 ) {
                sbNotSelected.append( " IN (" );
            } else {
                sbNotSelected.append( " = " );
            }

            for( int i = 0;i < resultsNotSelected.size();i++ ) {
                if( i > 0 ) {
                    sbNotSelected.append( "," );
                }

                sbNotSelected.append( resultsNotSelected.get( i ).toString());
            }

            if( resultsNotSelected.size() > 1 ) {
                sbNotSelected.append( ")" );
            }

            try {
                no = DB.executeUpdate( sbNotSelected.toString());
            } catch( Exception e ) {
                log.log( Level.SEVERE,"VProductImport.updateIt " + e );
            }
        }

        tabbedPane.setSelectedIndex( 1 );
        executeNewQuery();
    }    // updateIt

    /**
     * Descripción de Método
     *
     */

    private void updateNew() {
        miniNewTable.editingStopped( new ChangeEvent( this ));

        ArrayList    resultsSelected    = new ArrayList();
        ArrayList    resultsNotSelected = new ArrayList();
        int          no;
        StringBuffer sb;

        try {
            int rows = miniNewTable.getRowCount();

            for( int i = 0;i < rows;i++ ) {
                IDColumn I_Product_ID = ( IDColumn )miniNewTable.getValueAt( i,0 );
                String M_Product_ID = ( String )miniNewTable.getValueAt( i,4 );

                if( I_Product_ID != null ) {
                    if( M_Product_ID != null ) {
                    	log.fine("MProduct_ID="+M_Product_ID);
                        sb = new StringBuffer( "UPDATE I_Product SET M_Product_ID = " );
                        sb.append( M_Product_ID );
                        sb.append( " WHERE I_Product_ID = " );
                        sb.append( I_Product_ID.getRecord_ID());
                        no = DB.executeUpdate( sb.toString());
                    }

                    if( I_Product_ID.isSelected()) {
                        resultsSelected.add( I_Product_ID.getRecord_ID());
                    } else {
                        resultsNotSelected.add( I_Product_ID.getRecord_ID());
                    }
                }
            }

            // Selected

            if( resultsSelected.size() != 0 ) {
                sb = new StringBuffer( "UPDATE I_Product SET I_IsImported='N'" );
                sb.append( " WHERE I_Product_ID" );

                if( resultsSelected.size() > 1 ) {
                    sb.append( " IN (" );
                } else {
                    sb.append( " = " );
                }

                for( int i = 0;i < resultsSelected.size();i++ ) {
                    if( i > 0 ) {
                        sb.append( "," );
                    }

                    sb.append( resultsSelected.get( i ).toString());
                }

                if( resultsSelected.size() > 1 ) {
                    sb.append( ")" );
                }

                no = DB.executeUpdate( sb.toString());
            }

            // notSelected

            if( resultsNotSelected.size() != 0 ) {
                sb = new StringBuffer( "UPDATE I_Product SET I_IsImported = 'Y' " );
                sb.append( " WHERE I_Product_ID" );

                if( resultsNotSelected.size() > 1 ) {
                    sb.append( " IN (" );
                } else {
                    sb.append( " = " );
                }

                for( int i = 0;i < resultsNotSelected.size();i++ ) {
                    if( i > 0 ) {
                        sb.append( "," );
                    }

                    sb.append( resultsNotSelected.get( i ).toString());
                }

                if( resultsNotSelected.size() > 1 ) {
                    sb.append( ")" );
                }

                no = DB.executeUpdate( sb.toString());
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VProductImport.UpdateNew " + e );
        }
    }    // updateNew
}    // VProdPricGen --> PriceList_Create



/*
 *  @(#)VProductImport.java   02.07.07
 * 
 *  Fin del fichero VProductImport.java
 *  
 *  Versión 2.2
 *
 */
