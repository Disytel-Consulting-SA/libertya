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
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MProduct;
import org.openXpertya.process.ProcessInfo;
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

public class VProductSparepart extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public VProductSparepart() {}    // VProductSparepart

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "VProductSparepart.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            fillPicks();
            jbInit();
            dynInit();
            frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VProductSparepart,init",ex );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    // private Object                       m_AD_Org_ID = null;
    // private Object                       m_C_BPartner_ID = null;
    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel selPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel selNorthPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CLabel lProduct = new CLabel();

    /** Descripción de Campos */

    private VLookup fProduct;

    /** Descripción de Campos */

    private Object m_M_Product_ID = null;

    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();

    // modificar la creacion del confirm panel para a�adir la lupa

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( false,false,false,false,false,true,true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    // private BorderLayout genLayout = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane scrollPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable miniTable = new MiniTable();

    // indice inicial de columna seleccionada

    /** Descripción de Campos */

    private int m_keyColumnIndex = -1;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VProductSparepart.class );

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
        lProduct.setLabelFor( fProduct );
        lProduct.setText( Msg.translate( Env.getCtx(),"M_Product_ID" ));
        selNorthPanel.setLayout( northPanelLayout );
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.translate( Env.getCtx(),"M_Product_Spareparts_ID" ));
        selPanel.add( selNorthPanel,BorderLayout.NORTH );
        selNorthPanel.add( lProduct,null );
        selNorthPanel.add( fProduct,null );
        selPanel.setName( "selPanel" );
        selPanel.add( confirmPanelSel,BorderLayout.SOUTH );
        selPanel.add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( miniTable,null );
        confirmPanelSel.addActionListener( this );

        //

    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {
        MLookup bpL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,1402,DisplayType.Search );

        fProduct = new VLookup( "M_Product_ID",false,false,true,bpL );
        lProduct.setText( Msg.translate( Env.getCtx(),"M_Product_ID" ));
        fProduct.addVetoableChangeListener( this );
    }    // fillPicks

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // create Columns

        miniTable.addColumn( "M_Product_ID" );
        miniTable.addColumn( "Name" );
        miniTable.addColumn( "Description" );

        //

        miniTable.setMultiSelection( false );
        miniTable.setRowSelectionAllowed( false );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );

        // sentencia a�adida para que se sepa que IDColumn.class est� en la 0

        m_keyColumnIndex = 0;
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"Name" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"Description" ));

        //

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.translate( Env.getCtx(),"M_Product_Spareparts_ID" ));    // @@
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {
        log.info( "VProductSparepart.executeQuery" );

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT t.M_Product_ID, t.Name, t.Description" + " FROM M_Product p " + " INNER JOIN M_Product_Spareparts s ON (s.M_Product_ID=p.M_Product_ID AND s.IsActive='Y')" + " INNER JOIN M_Product t ON (t.M_Product_ID=s.M_ProductSpareparts_ID)" );

        if( m_M_Product_ID != null ) {
            sql.append( " WHERE p.M_Product_ID=" ).append( m_M_Product_ID );
        } else {
            sql.append( " WHERE 0=1" );
        }

        //

        sql.append( " ORDER BY t.M_Product_ID" );

        // reset table

        int row = 0;

        miniTable.setRowCount( row );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values

                miniTable.setValueAt( new IDColumn( rs.getInt( 1 )),row,0 );    // M_Product_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );    // Name
                miniTable.setValueAt( rs.getString( 3 ),row,2 );    // Description

                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VProductSparepart.executeQuery",e );
        }

        //

        miniTable.autoSize();
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
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( "VProductSparepart.actionPerformed - " + e.getActionCommand());

        //

        if( e.getActionCommand().equals( ConfirmPanel.A_ZOOM )) {
            zoom();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            dispose();

            return;
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    void zoom() {
        log.info( "VProductSparepart.zoom" );

        Integer M_Product_ID_zoom = getSelectedRowKey();

        if( M_Product_ID_zoom == null ) {
            return;
        }

        AEnv.zoom( MProduct.Table_ID,M_Product_ID_zoom.intValue());
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

    // a�adido para posibilitar el zoom a pedidos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected Integer getSelectedRowKey() {
        int row = miniTable.getSelectedRow();

        if( (row != -1) && (m_keyColumnIndex != -1) ) {
            Object data = miniTable.getModel().getValueAt( row,m_keyColumnIndex );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // a�adido para posibilitar el zoom

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.info( "VProductSparepart.vetoableChange - " + e.getPropertyName() + "=" + e.getNewValue());

        if( e.getPropertyName().equals( "M_Product_ID" )) {
            m_M_Product_ID = e.getNewValue();
        }

/*              if (e.getPropertyName().equals("C_BPartner_ID"))
                {
                        m_C_BPartner_ID = e.getNewValue();
                        fBPartner.setValue(m_C_BPartner_ID);    //      display value
                }
*/

        executeQuery();
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        tabbedPane.getSelectedIndex();
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {}    // tableChanged

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
}    // VProductSparepart



/*
 *  @(#)VProductSparepart.java   02.07.07
 * 
 *  Fin del fichero VProductSparepart.java
 *  
 *  Versión 2.2
 *
 */
