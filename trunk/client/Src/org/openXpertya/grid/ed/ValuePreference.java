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
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogMgt;
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

public class ValuePreference extends JDialog implements ActionListener {

    /**
     * Descripción de Método
     *
     *
     * @param mField
     * @param aValue
     *
     * @return
     */

    public static ValuePreference start( MField mField,Object aValue ) {
        return start( mField,aValue,null );
    }    // start

    /**
     * Descripción de Método
     *
     *
     * @param mField
     * @param aValue
     * @param aDisplayValue
     *
     * @return
     */

    public static ValuePreference start( MField mField,Object aValue,String aDisplayValue ) {
        if( !mField.isEditable( false )) {
            log.info( "ValuePreference.start - Field not editable (R/O)" );

            return null;
        }

        // Set Value/DisplayValue

        String Value        = null;
        String DisplayValue = null;

        if( aValue != null ) {
            Value        = aValue.toString();
            DisplayValue = ( aDisplayValue == null )
                           ?Value
                           :aDisplayValue;
        }

        // Get from mField
        // AD_Window_ID, DisplayAttribute, Attribute, DisplayType, AD_Referenece_ID

        int    AD_Window_ID     = mField.getAD_Window_ID();
        String Attribute        = mField.getColumnName();
        String DisplayAttribute = mField.getHeader();
        int    displayType      = mField.getDisplayType();
        int    AD_Reference_ID  = 0;
        int    WindowNo         = mField.getWindowNo();

        // Get from Environment (WindowNo)
        // AD_Client_ID, AD_Org_ID, AD_User_ID, Frame

        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
        int AD_Org_ID    = Env.getContextAsInt( Env.getCtx(),WindowNo,"AD_Org_ID" );
        int   AD_User_ID = Env.getAD_User_ID( Env.getCtx());
        Frame frame      = Env.getWindow( WindowNo );

        // Create Editor

        ValuePreference vp = new ValuePreference( frame,WindowNo,AD_Client_ID,AD_Org_ID,AD_User_ID,AD_Window_ID,Attribute,DisplayAttribute,Value,DisplayValue,displayType,AD_Reference_ID );

        return vp;
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @param l
     * @param popupMenu
     *
     * @return
     */

    public static JMenuItem addMenu( ActionListener l,JPopupMenu popupMenu ) {
        JMenuItem mi = new JMenuItem( Msg.getMsg( Env.getCtx(),NAME ),s_icon );

        mi.setActionCommand( NAME );
        mi.addActionListener( l );
        popupMenu.add( mi );

        return mi;
    }    // addMenu

    /** Descripción de Campos */

    public static final String NAME = "ValuePreference";

    /** Descripción de Campos */

    private static Icon s_icon = new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/VPreference16.gif" ));

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ValuePreference.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param AD_User_ID
     * @param AD_Window_ID
     * @param Attribute
     * @param DisplayAttribute
     * @param Value
     * @param DisplayValue
     * @param displayType
     * @param AD_Reference_ID
     */

    public ValuePreference( Frame frame,int WindowNo,int AD_Client_ID,int AD_Org_ID,int AD_User_ID,int AD_Window_ID,String Attribute,String DisplayAttribute,String Value,String DisplayValue,int displayType,int AD_Reference_ID ) {
        super( frame,Msg.getMsg( Env.getCtx(),NAME ) + " " + DisplayAttribute,true );
        log.config( "WindowNo=" + WindowNo + ", Client_ID=" + AD_Client_ID + ", Org_ID=" + AD_Org_ID + ", User_ID=" + AD_User_ID + ", Window_ID=" + AD_Window_ID + ",  Attribute=" + Attribute + "/" + DisplayAttribute + ",  Value=" + Value + "/" + DisplayValue + ",  DisplayType=" + displayType + ", Reference_ID=" + AD_Reference_ID );
        m_ctx              = Env.getCtx();
        m_WindowNo         = WindowNo;
        m_AD_Client_ID     = AD_Client_ID;
        m_AD_Org_ID        = AD_Org_ID;
        m_AD_User_ID       = AD_User_ID;
        m_AD_Window_ID     = AD_Window_ID;
        m_Attribute        = Attribute;
        m_DisplayAttribute = DisplayAttribute;
        m_Value            = Value;
        m_DisplayValue     = DisplayValue;
        m_DisplayType      = displayType;
        m_AD_Reference_ID  = AD_Reference_ID;

        //

        m_role = MRole.getDefault();

        try {
            jbInit();
            dynInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"ValuePreference",ex );
        }

        AEnv.showCenterScreen( this );
    }    // ValuePreference

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private int m_AD_Client_ID;

    /** Descripción de Campos */

    private int m_AD_Org_ID;

    /** Descripción de Campos */

    private int m_AD_User_ID;

    /** Descripción de Campos */

    private int m_AD_Window_ID;

    /** Descripción de Campos */

    private String m_Attribute;

    /** Descripción de Campos */

    private String m_DisplayAttribute;

    /** Descripción de Campos */

    private String m_Value;

    /** Descripción de Campos */

    private String m_DisplayValue;

    /** Descripción de Campos */

    private int m_DisplayType;

    /** Descripción de Campos */

    private int m_AD_Reference_ID;

    /** Descripción de Campos */

    private MRole m_role;

    // Display

    /** Descripción de Campos */

    private CPanel setPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout setLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel lAttribute = new CLabel();

    /** Descripción de Campos */

    private CTextField fAttribute = new CTextField();

    /** Descripción de Campos */

    private CLabel lAttributeValue = new CLabel();

    /** Descripción de Campos */

    private CLabel lValue = new CLabel();

    /** Descripción de Campos */

    private CLabel lValueValue = new CLabel();

    /** Descripción de Campos */

    private CTextField fValue = new CTextField();

    /** Descripción de Campos */

    private CLabel lSetFor = new CLabel();

    /** Descripción de Campos */

    private VCheckBox cbClient = new VCheckBox();

    /** Descripción de Campos */

    private VCheckBox cbOrg = new VCheckBox();

    /** Descripción de Campos */

    private VCheckBox cbUser = new VCheckBox();

    /** Descripción de Campos */

    private VCheckBox cbWindow = new VCheckBox();

    /** Descripción de Campos */

    private CLabel lExplanation = new CLabel();

    /** Descripción de Campos */

    private CPanel currentPanel = new CPanel();

    /** Descripción de Campos */

    private TitledBorder titledBorder;

    /** Descripción de Campos */

    private JScrollPane scrollPane = new JScrollPane();

    /** Descripción de Campos */

    private BorderLayout currentLayout = new BorderLayout();

    /** Descripción de Campos */

    private JTable table = new JTable();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private JButton bDelete;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        titledBorder = new TitledBorder( BorderFactory.createEtchedBorder( Color.white,new Color( 148,145,140 )),Msg.getMsg( m_ctx,"CurrentSettings" ));

        //

        lAttribute.setText( Msg.translate( m_ctx,"Attribute" ));
        lValue.setText( Msg.translate( m_ctx,"Value" ));
        lSetFor.setText( Msg.getMsg( m_ctx,"ValuePreferenceSetFor" ));
        cbClient.setText( Msg.translate( m_ctx,"AD_Client_ID" ));
        cbOrg.setText( Msg.translate( m_ctx,"AD_Org_ID" ));
        cbUser.setText( Msg.translate( m_ctx,"AD_User_ID" ));
        cbWindow.setText( Msg.translate( m_ctx,"AD_Window_ID" ));
        cbWindow.setSelected( true );

        //

        setPanel.setLayout( setLayout );
        fAttribute.setEditable( false );
        fValue.setEditable( false );
        this.getContentPane().add( setPanel,BorderLayout.NORTH );
        setPanel.add( lAttribute,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( fAttribute,new GridBagConstraints( 1,0,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( lValue,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( fValue,new GridBagConstraints( 1,1,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( lSetFor,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( cbClient,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( cbOrg,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( cbUser,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( cbWindow,new GridBagConstraints( 4,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        setPanel.add( lAttributeValue,new GridBagConstraints( 5,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        setPanel.add( lValueValue,new GridBagConstraints( 5,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        setPanel.add( lExplanation,new GridBagConstraints( 1,3,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        //

        currentPanel.setBorder( titledBorder );
        currentPanel.setLayout( currentLayout );

        // this.getContentPane().add(currentPanel, BorderLayout.CENTER);

        currentPanel.add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( table,null );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // Set Attribute/Value

        fAttribute.setText( m_DisplayAttribute );
        lAttributeValue.setText( m_Attribute );
        fValue.setText( m_DisplayValue );
        lValueValue.setText( m_Value );

        if( CLogMgt.isLevelFine()) {
            lAttributeValue.setVisible( false );
            lValueValue.setVisible( false );
        }

        // ActionListener

        cbClient.setEnabled( false );
        cbClient.setSelected( true );

        // cbClient.addActionListener(this);

        // Can Change Org

        if( MRole.PREFERENCETYPE_Client.equals( m_role.getPreferenceType())) {
            cbOrg.addActionListener( this );
        } else {
            cbOrg.setEnabled( false );
            cbOrg.setSelected( true );
        }

        // Can Change User

        if( MRole.PREFERENCETYPE_Client.equals( m_role.getPreferenceType()) || MRole.PREFERENCETYPE_Organization.equals( m_role.getPreferenceType())) {
            cbUser.addActionListener( this );
        } else {
            cbUser.setEnabled( false );
            cbUser.setSelected( true );
        }

        // Can change all/specific

        cbWindow.addActionListener( this );

        // Other

        confirmPanel.addActionListener( this );
        bDelete = confirmPanel.addButton( "Delete",Msg.getMsg( Env.getCtx(),"Delete" ),Env.getImageIcon( "Delete24.gif" ),0 );
        bDelete.addActionListener( this );
        setExplanation();
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            insert();
            dispose();
        } else if( e.getSource() == bDelete ) {
            int no = delete();

            if( no == 0 ) {
                ADialog.warn( m_WindowNo,this,"ValuePreferenceNotFound" );
            } else {
                ADialog.info( m_WindowNo,this,"ValuePreferenceDeleted",String.valueOf( no ));
            }

            dispose();
        } else {
            setExplanation();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void setExplanation() {
        StringBuffer expl = new StringBuffer( "For " );

        if( cbClient.isSelected() && cbOrg.isSelected()) {
            expl.append( "this Client and Organization" );
        } else if( cbClient.isSelected() &&!cbOrg.isSelected()) {
            expl.append( "all Organizations of this Client" );
        } else if( !cbClient.isSelected() && cbOrg.isSelected()) {
            cbOrg.setSelected( false );
            expl.append( "entire System" );
        } else {
            expl.append( "entire System" );
        }

        //

        if( cbUser.isSelected()) {
            expl.append( ", this User" );
        } else {
            expl.append( ", all Users" );
        }

        //

        if( cbWindow.isSelected()) {
            expl.append( " and this Window" );
        } else {
            expl.append( " and all Windows" );
        }

        //

        if( Env.getLanguage( Env.getCtx()).isBaseLanguage()) {
            lExplanation.setText( expl.toString());
            this.pack();
        }
    }    // setExplanation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int delete() {
        log.info( "" );

        StringBuffer sql = new StringBuffer( "DELETE FROM AD_Preference WHERE " );

        sql.append( "AD_Client_ID=" ).append( cbClient.isSelected()
                ?m_AD_Client_ID
                :0 );
        sql.append( " AND AD_Org_ID=" ).append( cbOrg.isSelected()
                ?m_AD_Org_ID
                :0 );

        if( cbUser.isSelected()) {
            sql.append( " AND AD_User_ID=" ).append( m_AD_User_ID );
        } else {
            sql.append( " AND AD_User_ID IS NULL" );
        }

        if( cbWindow.isSelected()) {
            sql.append( " AND AD_Window_ID=" ).append( m_AD_Window_ID );
        } else {
            sql.append( " AND AD_Window_ID IS NULL" );
        }

        sql.append( " AND Attribute='" ).append( m_Attribute ).append( "'" );

        //

        log.fine( sql.toString());

        int no = DB.executeUpdate( sql.toString());

        if( no > 0 ) {
            Env.setContext( m_ctx,getContextKey(),( String )null );
        }

        return no;
    }    // delete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getContextKey() {
        if( cbWindow.isSelected()) {
            return "P" + m_AD_Window_ID + "|" + m_Attribute;
        } else {
            return "P|" + m_Attribute;
        }
    }    // getContextKey

    /**
     * Descripción de Método
     *
     */

    public void insert() {
        log.info( "" );

        // --- Delete first

        int no = delete();

        // Handle NULL

        if( (m_Value == null) || (m_Value.length() == 0) ) {
            if( DisplayType.isLookup( m_DisplayType )) {
                m_Value = "-1";
            } else if( DisplayType.isDate( m_DisplayType )) {
                m_Value = " ";
            } else {
                ADialog.warn( m_WindowNo,this,"ValuePreferenceNotInserted" );

                return;
            }
        }

        // --- Inserting

        int Client_ID        = cbClient.isSelected()
                               ?m_AD_Client_ID
                               :0;
        int Org_ID           = cbOrg.isSelected()
                               ?m_AD_Org_ID
                               :0;
        int AD_Preference_ID = DB.getNextID( m_ctx,"AD_Preference",null );

        //

        StringBuffer sql = new StringBuffer( "INSERT INTO AD_Preference (" + "AD_Preference_ID, AD_Client_ID, AD_Org_ID, IsActive, Created,CreatedBy,Updated,UpdatedBy," + "AD_Window_ID, AD_User_ID, Attribute, Value) VALUES (" );

        sql.append( AD_Preference_ID ).append( "," ).append( Client_ID ).append( "," ).append( Org_ID ).append( ", 'Y',SysDate," ).append( m_AD_User_ID ).append( ",SysDate," ).append( m_AD_User_ID ).append( ", " );

        if( cbWindow.isSelected()) {
            sql.append( m_AD_Window_ID ).append( "," );
        } else {
            sql.append( "NULL," );
        }

        if( cbUser.isSelected()) {
            sql.append( m_AD_User_ID ).append( "," );
        } else {
            sql.append( "NULL," );
        }

        //

        sql.append( "'" ).append( m_Attribute ).append( "','" ).append( m_Value ).append( "')" );

        //

        log.fine( sql.toString());
        no = DB.executeUpdate( sql.toString());

        if( no == 1 ) {
            Env.setContext( m_ctx,getContextKey(),m_Value );
            ADialog.info( m_WindowNo,this,"ValuePreferenceInserted" );
        } else {
            ADialog.warn( m_WindowNo,this,"ValuePreferenceNotInserted" );
        }
    }    // insert
}    // ValuePreference



/*
 *  @(#)ValuePreference.java   02.07.07
 * 
 *  Fin del fichero ValuePreference.java
 *  
 *  Versión 2.2
 *
 */
