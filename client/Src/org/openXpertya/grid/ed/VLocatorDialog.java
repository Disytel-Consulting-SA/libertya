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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MLocatorLookup;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VLocatorDialog extends JDialog implements ActionListener,KeyListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param mLocator
     * @param M_Locator_ID
     * @param mandatory
     * @param only_Warehouse_ID
     */

    public VLocatorDialog( Frame frame,String title,MLocatorLookup mLocator,int M_Locator_ID,boolean mandatory,int only_Warehouse_ID ) {
        super( frame,title,true );
        m_WindowNo = Env.getWindowNo( frame );
        log.fine("En VlocatorDialog");

        try {
            jbInit();
            setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VLocatorDialog",ex );
        }

        //

        m_mLocator          = mLocator;
        m_M_Locator_ID      = M_Locator_ID;
        m_mandatory         = mandatory;
        m_only_Warehouse_ID = only_Warehouse_ID;

        //

        initLocator();
        AEnv.positionCenterWindow( frame,this );
    }    // VLocatorDialog

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private boolean m_change = false;

    /** Descripción de Campos */

    private MLocatorLookup m_mLocator;

    /** Descripción de Campos */

    private int m_M_Locator_ID;

    /** Descripción de Campos */

    private boolean m_mandatory = false;

    /** Descripción de Campos */

    private int m_only_Warehouse_ID = 0;

    //

    /** Descripción de Campos */

    private int m_M_Warehouse_ID;

    /** Descripción de Campos */

    private String m_M_WarehouseName;

    /** Descripción de Campos */

    private String m_M_WarehouseValue;

    /** Descripción de Campos */

    private String m_Separator;

    /** Descripción de Campos */

    private int m_AD_Client_ID;

    /** Descripción de Campos */

    private int m_AD_Org_ID;
    
    private int m_OriginalLocatorID = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VLocatorDialog.class );

    //

    /** Descripción de Campos */

    private CPanel panel = new CPanel();

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout panelLayout = new BorderLayout();

    /** Descripción de Campos */

    private GridBagLayout gridBagLayout = new GridBagLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    //

    /** Descripción de Campos */

    private VComboBox fLocator = new VComboBox();

    /** Descripción de Campos */

    private CComboBox fWarehouse = new CComboBox();

    /** Descripción de Campos */

    private JCheckBox fCreateNew = new JCheckBox();

    /** Descripción de Campos */

    private CTextField fX = new CTextField();

    /** Descripción de Campos */

    private CTextField fY = new CTextField();

    /** Descripción de Campos */

    private CTextField fZ = new CTextField();

    /** Descripción de Campos */

    private JLabel lLocator = new JLabel();

    /** Descripción de Campos */

    private CTextField fWarehouseInfo = new CTextField();

    /** Descripción de Campos */

    private CTextField fValue = new CTextField();

    /** Descripción de Campos */

    private JLabel lWarehouseInfo = new JLabel();

    /** Descripción de Campos */

    private JLabel lWarehouse = new JLabel();

    /** Descripción de Campos */

    private JLabel lX = new JLabel();

    /** Descripción de Campos */

    private JLabel lY = new JLabel();

    /** Descripción de Campos */

    private JLabel lZ = new JLabel();

    /** Descripción de Campos */

    private JLabel lValue = new JLabel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        panel.setLayout( panelLayout );
        southPanel.setLayout( southLayout );
        mainPanel.setLayout( gridBagLayout );
        panelLayout.setHgap( 5 );
        panelLayout.setVgap( 10 );
        fCreateNew.setText( Msg.getMsg( Env.getCtx(),"CreateNew" ));
        fX.setColumns( 15 );
        fY.setColumns( 15 );
        fZ.setColumns( 15 );
        lLocator.setLabelFor( fLocator );
        lLocator.setText( Msg.translate( Env.getCtx(),"M_Locator_ID" ));
        fWarehouseInfo.setBackground( CompierePLAF.getFieldBackground_Inactive());
        fWarehouseInfo.setReadWrite( false );
        fWarehouseInfo.setColumns( 15 );
        fValue.setColumns( 15 );
        lWarehouseInfo.setLabelFor( fWarehouseInfo );
        lWarehouseInfo.setText( Msg.translate( Env.getCtx(),"M_Warehouse_ID" ));
        lWarehouse.setLabelFor( fWarehouse );
        lWarehouse.setText( Msg.translate( Env.getCtx(),"M_Warehouse_ID" ));
        lX.setLabelFor( fX );
        lX.setText( Msg.getElement( Env.getCtx(),"X" ));
        lY.setLabelFor( fY );
        lY.setText( Msg.getElement( Env.getCtx(),"Y" ));
        lZ.setLabelFor( fZ );
        lZ.setText( Msg.getElement( Env.getCtx(),"Z" ));
        lValue.setLabelFor( fValue );
        lValue.setText( Msg.translate( Env.getCtx(),"Value" ));
        getContentPane().add( panel );
        panel.add( mainPanel,BorderLayout.CENTER );

        //

        mainPanel.add( lLocator,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fLocator,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( fCreateNew,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 10,0,0,5 ),0,0 ));
        mainPanel.add( lWarehouseInfo,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fWarehouseInfo,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( lWarehouse,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fWarehouse,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( lX,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fX,new GridBagConstraints( 1,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( lY,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fY,new GridBagConstraints( 1,5,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( lZ,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fZ,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        mainPanel.add( lValue,new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        mainPanel.add( fValue,new GridBagConstraints( 1,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));

        //

        panel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( confirmPanel,BorderLayout.NORTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void initLocator() {
        log.fine( "" );

        // Load Warehouse

        String sql = "SELECT M_Warehouse_ID, Name FROM M_Warehouse";

        if( m_only_Warehouse_ID != 0 ) {
            sql += " WHERE M_Warehouse_ID=" + m_only_Warehouse_ID;
        }

        String SQL = MRole.getDefault().addAccessSQL( sql,"M_Warehouse",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ) + " ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                fWarehouse.addItem( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"warehouse",e );
        }

        log.fine( "Warehouses=" + fWarehouse.getItemCount());

        // Load existing Locators

        m_mLocator.fillComboBox( m_mandatory,true,true,false );
        log.fine( m_mLocator.toString());
        fLocator.setModel( m_mLocator );
        fLocator.setValue( m_M_Locator_ID );
        fLocator.addActionListener( this );
        displayLocator();
        
        //

        fCreateNew.setSelected( false );
        fCreateNew.addActionListener( this );
        enableNew();

        //

        fWarehouse.addActionListener( this );
        fX.addKeyListener( this );
        fY.addKeyListener( this );
        fZ.addKeyListener( this );

        // Guarda el ID de la ubicación con la cual se invocó el constructor
        // de este diálogo. Este ID se utiliza en caso de cancelar el diálogo
        // para recuperar el valor que tenía previamente el ID de ubicación.
        m_OriginalLocatorID = m_M_Locator_ID;
        
        // Update UI

        pack();
    }    // initLocator

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        //

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            actionOK();
            m_change = true;
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
        	fLocator.setValue(m_OriginalLocatorID);
        	m_change = false;
            dispose();
        }

        // Locator Change

        else if( e.getSource() == fLocator ) {
            displayLocator();

            // New Value Change

        } else if( source == fCreateNew ) {
            enableNew();

            // Entered/Changed data for Value

        } else if( fCreateNew.isSelected() && (source == fWarehouse) ) {
            createValue();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyPressed( KeyEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyReleased( KeyEvent e ) {
        if( fCreateNew.isSelected()) {
            createValue();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyTyped( KeyEvent e ) {}

    /**
     * Descripción de Método
     *
     */

    private void displayLocator() {
        MLocator l = ( MLocator )fLocator.getSelectedItem();

        if( l == null ) {
            return;
        }

        //

        m_M_Locator_ID = l.getM_Locator_ID();
        fWarehouseInfo.setText( l.getWarehouseName());
        fX.setText( l.getX());
        fY.setText( l.getY());
        fZ.setText( l.getZ());
        fValue.setText( l.getValue());
        getWarehouseInfo( l.getM_Warehouse_ID());

        // Set Warehouse

        int size = fWarehouse.getItemCount();

        for( int i = 0;i < size;i++ ) {
            KeyNamePair pp = ( KeyNamePair )fWarehouse.getItemAt( i );

            if( pp.getKey() == l.getM_Warehouse_ID()) {
                fWarehouse.setSelectedIndex( i );

                continue;
            }
        }
    }    // displayLocator

    /**
     * Descripción de Método
     *
     */

    private void enableNew() {
        boolean sel = fCreateNew.isSelected();

        lWarehouse.setVisible( sel );
        fWarehouse.setVisible( sel );
        lWarehouseInfo.setVisible( !sel );
        fWarehouseInfo.setVisible( !sel );
        fX.setReadWrite( sel );
        fY.setReadWrite( sel );
        fZ.setReadWrite( sel );
        fValue.setReadWrite( sel );
        pack();
    }    // enableNew

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     */

    private void getWarehouseInfo( int M_Warehouse_ID ) {
        if( M_Warehouse_ID == m_M_Warehouse_ID ) {
            return;
        }

        // Defaults

        m_M_Warehouse_ID   = 0;
        m_M_WarehouseName  = "";
        m_M_WarehouseValue = "";
        m_Separator        = ".";
        m_AD_Client_ID     = 0;
        m_AD_Org_ID        = 0;

        //

        String SQL = "SELECT M_Warehouse_ID, Value, Name, Separator, AD_Client_ID, AD_Org_ID " + "FROM M_Warehouse WHERE M_Warehouse_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,M_Warehouse_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_M_Warehouse_ID   = rs.getInt( 1 );
                m_M_WarehouseValue = rs.getString( 2 );
                m_M_WarehouseName  = rs.getString( 3 );
                m_Separator        = rs.getString( 4 );
                m_AD_Client_ID     = rs.getInt( 5 );
                m_AD_Org_ID        = rs.getInt( 6 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getWarehouseInfo",e );
        }
    }    // getWarehouseInfo

    /**
     * Descripción de Método
     *
     */

    private void createValue() {

        // Get Warehouse Info

        KeyNamePair pp = ( KeyNamePair )fWarehouse.getSelectedItem();

        if( pp == null ) {
            return;
        }

        getWarehouseInfo( pp.getKey());

        //

        StringBuffer buf = new StringBuffer( m_M_WarehouseValue );

        buf.append( m_Separator ).append( fX.getText());
        buf.append( m_Separator ).append( fY.getText());
        buf.append( m_Separator ).append( fZ.getText());
        fValue.setText( buf.toString());
    }    // createValue

    /**
     * Descripción de Método
     *
     */

    private void actionOK() {
        if( fCreateNew.isSelected()) {

            // Get Warehouse Info

            KeyNamePair pp = ( KeyNamePair )fWarehouse.getSelectedItem();

            if( pp != null ) {
                getWarehouseInfo( pp.getKey());
            }

            // Check mandatory values

            String mandatoryFields = "";

            if( m_M_Warehouse_ID == 0 ) {
                mandatoryFields += lWarehouse.getText() + " - ";
            }

            if( fValue.getText().length() == 0 ) {
                mandatoryFields += lValue.getText() + " - ";
            }

            if( fX.getText().length() == 0 ) {
                mandatoryFields += lX.getText() + " - ";
            }

            if( fY.getText().length() == 0 ) {
                mandatoryFields += lY.getText() + " - ";
            }

            if( fZ.getText().length() == 0 ) {
                mandatoryFields += lZ.getText() + " - ";
            }

            if( mandatoryFields.length() != 0 ) {
                ADialog.error( m_WindowNo,this,"FillMandatory",mandatoryFields.substring( 0,mandatoryFields.length() - 3 ));

                return;
            }

            MLocator loc = MLocator.get( Env.getCtx(),m_M_Warehouse_ID,fValue.getText(),fX.getText(),fY.getText(),fZ.getText());

            m_M_Locator_ID = loc.getM_Locator_ID();
            fLocator.addItem( loc );
            fLocator.setSelectedItem( loc );
        }    // createNew

        //

        log.config( "M_Locator_ID=" + m_M_Locator_ID );
    }    // actionOK

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Integer getValue() {
        MLocator l = ( MLocator )fLocator.getSelectedItem();

        if( (l != null) && (l.getM_Locator_ID() != 0) ) {
            return new Integer( l.getM_Locator_ID());
        }

        return null;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChanged() {
        if( m_change ) {
            MLocator l = ( MLocator )fLocator.getSelectedItem();

            if( l != null ) {
                return l.getM_Locator_ID() == m_M_Locator_ID;
            }
        }

        return m_change;
    }    // getChange
}    // VLocatorDialog



/*
 *  @(#)VLocatorDialog.java   02.07.07
 * 
 *  Fin del fichero VLocatorDialog.java
 *  
 *  Versión 2.2
 *
 */
