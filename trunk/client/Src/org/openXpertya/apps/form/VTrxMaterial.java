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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JLabel;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.GridController;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLocator;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MLocatorLookup;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MWindow;
import org.openXpertya.model.MWindowVO;
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

public class VTrxMaterial extends CPanel implements FormPanel,ActionListener,VetoableChangeListener {

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private GridController m_gridController = null;

    /** Descripción de Campos */

    private MWindow m_mWindow = null;

    /** Descripción de Campos */

    private MTab m_mTab = null;

    /** Descripción de Campos */

    private MQuery m_staticQuery = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VTrxMaterial.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel parameterPanel = new CPanel();

    /** Descripción de Campos */

    private JLabel orgLabel = new JLabel();

    /** Descripción de Campos */

    private VLookup orgField;

    /** Descripción de Campos */

    private JLabel locatorLabel = new JLabel();

    /** Descripción de Campos */

    private VLocator locatorField;

    /** Descripción de Campos */

    private JLabel productLabel = new JLabel();

    /** Descripción de Campos */

    private VLookup productField;

    /** Descripción de Campos */

    private JLabel dateFLabel = new JLabel();

    /** Descripción de Campos */

    private VDate dateFField;

    /** Descripción de Campos */

    private JLabel dateTLabel = new JLabel();

    /** Descripción de Campos */

    private VDate dateTField;

    /** Descripción de Campos */

    private JLabel mtypeLabel = new JLabel();

    /** Descripción de Campos */

    private VLookup mtypeField;

    /** Descripción de Campos */

    private GridBagLayout parameterLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true,true,false,false,false,true,true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "" );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        try {
            dynParameter();
            jbInit();
            dynInit();
            frame.getContentPane().add( mainPanel,BorderLayout.CENTER );
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"",ex );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        mainPanel.setLayout( mainLayout );
        mainLayout.setVgap( 10 );
        parameterPanel.setLayout( parameterLayout );

        //

        orgLabel.setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        locatorLabel.setText( Msg.translate( Env.getCtx(),"M_Locator_ID" ));
        productLabel.setText( Msg.translate( Env.getCtx(),"M_Product_ID" ));
        dateFLabel.setText( Msg.translate( Env.getCtx(),"DateFrom" ));
        dateTLabel.setText( Msg.translate( Env.getCtx(),"DateTo" ));
        mtypeLabel.setText( Msg.translate( Env.getCtx(),"MovementType" ));

        //

        mainPanel.add( parameterPanel,BorderLayout.NORTH );
        parameterPanel.add( orgLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( orgField,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        parameterPanel.add( mtypeLabel,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( mtypeField,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        parameterPanel.add( dateFLabel,new GridBagConstraints( 4,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( dateFField,new GridBagConstraints( 5,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        parameterPanel.add( locatorLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( locatorField,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        parameterPanel.add( productLabel,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( productField,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        parameterPanel.add( dateTLabel,new GridBagConstraints( 4,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        parameterPanel.add( dateTField,new GridBagConstraints( 5,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));

        //

        southPanel.setLayout( southLayout );
        southPanel.add( confirmPanel,BorderLayout.NORTH );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        mainPanel.add( southPanel,BorderLayout.SOUTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void dynParameter() throws Exception {
        Properties ctx = Env.getCtx();

        // Organization

        MLookup orgLookup = MLookupFactory.get( ctx,m_WindowNo,0,3660,DisplayType.TableDir );

        orgField = new VLookup( "AD_Org_ID",false,false,true,orgLookup );

        // orgField.addVetoableChangeListener(this);
        // Locator

        MLocatorLookup locatorLookup = new MLocatorLookup( ctx,m_WindowNo );

        locatorField = new VLocator( "M_Locator_ID",false,false,true,locatorLookup,m_WindowNo );

        // locatorField.addVetoableChangeListener(this);
        // Product

        MLookup productLookup = MLookupFactory.get( ctx,m_WindowNo,0,3668,DisplayType.Search );

        productField = new VLookup( "M_Product_ID",false,false,true,productLookup );
        productField.addVetoableChangeListener( this );

        // Movement Type

        MLookup mtypeLookup = MLookupFactory.get( ctx,m_WindowNo,0,3666,DisplayType.List );

        mtypeField = new VLookup( "MovementType",false,false,true,mtypeLookup );

        // Dates

        dateFField = new VDate( "DateFrom",false,false,true,DisplayType.Date,Msg.getMsg( Env.getCtx(),"DateFrom" ));
        dateTField = new VDate( "DateTo",false,false,true,DisplayType.Date,Msg.getMsg( Env.getCtx(),"DateTo" ));

        //

        confirmPanel.addActionListener( this );
        statusBar.setStatusLine( "" );
    }    // dynParameter

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        m_staticQuery = new MQuery();
        m_staticQuery.addRestriction( "AD_Client_ID",MQuery.EQUAL,Env.getAD_Client_ID( Env.getCtx()));

        int       AD_Window_ID = 223;    // Hardcoded
        MWindowVO wVO          = AEnv.getMWindowVO( m_WindowNo,AD_Window_ID,0 );

        if( wVO == null ) {
            return;
        }

        m_mWindow = new MWindow( wVO );
        m_mTab    = m_mWindow.getTab( 0 );

        //

        m_gridController = new GridController();
        m_gridController.initGrid( m_mTab,true,m_WindowNo,null,null );
        mainPanel.add( m_gridController,BorderLayout.CENTER );

        //

        m_mTab.setQuery( MQuery.getEqualQuery( "1","2" ));
        m_mTab.query( false );
        statusBar.setStatusLine( " ",false );
        statusBar.setStatusDB( " " );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_gridController != null ) {
            m_gridController.dispose();
        }

        m_gridController = null;
        m_mTab           = null;

        if( m_mWindow != null ) {
            m_mWindow.dispose();
        }

        m_mWindow    = null;
        orgField     = null;
        locatorField = null;
        productField = null;
        mtypeField   = null;
        dateFField   = null;
        dateTField   = null;

        //

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
        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_REFRESH ) || e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            refresh();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_ZOOM )) {
            zoom();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        if( e.getPropertyName().equals( "M_Product_ID" )) {
            productField.setValue( e.getNewValue());
        }
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     */

    private void refresh() {
        MQuery query = m_staticQuery.deepCopy();

        // Organization

        Object value = orgField.getValue();

        if( (value != null) && (value.toString().length() > 0) ) {
            query.addRestriction( "AD_Org_ID",MQuery.EQUAL,value );
        }

        // Locator

        value = locatorField.getValue();

        if( (value != null) && (value.toString().length() > 0) ) {
            query.addRestriction( "M_Locator_ID",MQuery.EQUAL,value );
        }

        // Product

        value = productField.getValue();

        if( (value != null) && (value.toString().length() > 0) ) {
            query.addRestriction( "M_Product_ID",MQuery.EQUAL,value );
        }

        // MovementType

        value = mtypeField.getValue();

        if( (value != null) && (value.toString().length() > 0) ) {
            query.addRestriction( "MovementType",MQuery.EQUAL,value );
        }

        // DateFrom

        Timestamp ts = ( Timestamp )dateFField.getValue();

        if( ts != null ) {
            query.addRestriction( "TRUNC(MovementDate)",MQuery.GREATER_EQUAL,ts );
        }

        // DateTO

        ts = ( Timestamp )dateTField.getValue();

        if( ts != null ) {
            query.addRestriction( "TRUNC(MovementDate)",MQuery.LESS_EQUAL,ts );
        }

        log.info( "VTrxMaterial.refresh query=" + query.toString());
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"StartSearch" ),false );

        //

        m_mTab.setQuery( query );
        m_mTab.query( false );

        //

        setCursor( Cursor.getDefaultCursor());

        int no = m_mTab.getRowCount();

        statusBar.setStatusLine( " ",false );
        statusBar.setStatusDB( Integer.toString( no ));
    }    // refresh

    /**
     * Descripción de Método
     *
     */

    private void zoom() {
        log.info( "" );

        //

        int    AD_Window_ID = 0;
        String ColumnName   = null;
        String SQL          = null;

        //

        int lineID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_InOutLine_ID" );

        if( lineID != 0 ) {
            log.fine( "M_InOutLine_ID=" + lineID );

            if( Env.getContext( Env.getCtx(),m_WindowNo,"MovementType" ).startsWith( "C" )) {
                AD_Window_ID = 169;    // Customer
            } else {
                AD_Window_ID = 184;    // Vendor
            }

            ColumnName = "M_InOut_ID";
            SQL        = "SELECT M_InOut_ID FROM M_InOutLine WHERE M_InOutLine_ID=?";
        } else {
            lineID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_InventoryLine_ID" );

            if( lineID != 0 ) {
                log.fine( "M_InventoryLine_ID=" + lineID );
                AD_Window_ID = 168;
                ColumnName   = "M_Inventory_ID";
                SQL          = "SELECT M_Inventory_ID FROM M_InventoryLine WHERE M_InventoryLine_ID=?";
            } else {
                lineID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_MovementLine_ID" );

                if( lineID != 0 ) {
                    log.fine( "M_MovementLine_ID=" + lineID );
                    AD_Window_ID = 170;
                    ColumnName   = "M_Movement_ID";
                    SQL          = "SELECT M_Movement_ID FROM M_MovementLine WHERE M_MovementLine_ID=?";
                } else {
                    lineID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_ProductionLine_ID" );

                    if( lineID != 0 ) {
                        log.fine( "M_ProductionLine_ID=" + lineID );
                        AD_Window_ID = 191;
                        ColumnName   = "M_Production_ID";
                        SQL          = "SELECT M_Production_ID FROM M_ProductionLine WHERE M_ProductionLine_ID=?";
                    } else {
                        log.fine( "Not found WindowNo=" + m_WindowNo );
                    }
                }
            }
        }

        if( AD_Window_ID == 0 ) {
            return;
        }

        // Get Parent ID

        int parentID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,lineID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                parentID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,SQL,e );
        }

        MQuery query = MQuery.getEqualQuery( ColumnName,parentID );

        log.config( "AD_Window_ID=" + AD_Window_ID + " - " + query );

        if( parentID == 0 ) {
            log.log( Level.SEVERE,"No ParentValue - " + SQL + " - " + lineID );
        }

        // Zoom

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            setCursor( Cursor.getDefaultCursor());

            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
        setCursor( Cursor.getDefaultCursor());
    }    // zoom
}    // VTrxMaterial



/*
 *  @(#)VTrxMaterial.java   02.07.07
 * 
 *  Fin del fichero VTrxMaterial.java
 *  
 *  Versión 2.2
 *
 */
