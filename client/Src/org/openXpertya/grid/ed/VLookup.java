
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.compiere.swing.CButton;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.search.Info;
import org.openXpertya.apps.search.InfoBPartner;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTab;
import org.openXpertya.model.X_AD_Table;
import org.openXpertya.plugin.common.PluginInfoUtils;
import org.openXpertya.plugin.common.PluginLookupInterface;
import org.openXpertya.plugin.common.PluginLookupUtils;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VLookup extends JComponent implements VEditor,ActionListener,FocusListener {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     *
     * @return
     */

    public static VLookup createBPartner( int WindowNo ) {
        int AD_Column_ID = 3499;    // C_Invoice.C_BPartner_ID

        try {
            Lookup lookup = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.Search );

            return new VLookup( "C_BPartner_ID",false,false,true,lookup );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createBPartner",e );
        }

        return null;
    }    // createBPartner

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     *
     * @return
     */

    public static VLookup createProduct( int WindowNo ) {
        int AD_Column_ID = 3840;    // C_InvoiceLine.M_Product_ID

        try {
            Lookup lookup = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.Search );

            return new VLookup( "M_Product_ID",false,false,true,lookup );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createProduct",e );
        }

        return null;
    }    // createProduct

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     *
     * @return
     */

    public static VLookup createUser( int WindowNo ) {
        int AD_Column_ID = 10443;    // AD_WF_Activity.AD_User_UD

        try {
            Lookup lookup = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.Search );

            return new VLookup( "AD_User_ID",false,false,true,lookup );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createUser",e );
        }

        return null;
    }    // createProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param lookup
     * @param mtab
     */

    public VLookup( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,Lookup lookup,MTab mtab ) {
    	super();
        super.setName( columnName );
        m_combo.setName( columnName );
        m_columnName = columnName;
        setMandatory( mandatory );
        m_lookup = lookup;
        m_tab=mtab;

        //

        setLayout( new BorderLayout());

        VLookup_mouseAdapter mouse = new VLookup_mouseAdapter( this );    // popup

        // ***     Text & Button   ***

        m_text.addActionListener( this );
        m_text.addFocusListener( this );
        m_text.addMouseListener( mouse );

        // Button

        m_button.addActionListener( this );
        m_button.addMouseListener( mouse );
        m_button.setFocusable( false );    // don't focus when tabbing
        m_button.setMargin( new Insets( 0,0,0,0 ));

        if( columnName.equals( "C_BPartner_ID" )) {
            m_button.setIcon( Env.getImageIcon( "BPartner10.gif" ));
        } else if( columnName.equals( "M_Product_ID" )) {
            m_button.setIcon( Env.getImageIcon( "Product10.gif" ));
        } else {
            m_button.setIcon( Env.getImageIcon( "PickOpen10.gif" ));
        }

        // *** VComboBox   ***

        if( (m_lookup != null) && (m_lookup.getDisplayType() != DisplayType.Search) )    // No Search
        {

            // Memory Leak after executing the next two lines ??

            m_lookup.fillComboBox( isMandatory(),false,false,false );
            m_combo.setModel( m_lookup );
            
            // ToolTips renderer
            m_combo.setRenderer(new ToolTipComboBoxRenderer());

            //

            m_combo.addActionListener( this );    // Selection
            m_combo.addMouseListener( mouse );    // popup

            // FocusListener to refresh selection before opening

            m_combo.addFocusListener( this );
        }

        setUI( true );

        // ReadWrite       -       decides what components to show

        if( isReadOnly ||!isUpdateable || (m_lookup == null) ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        // Popup

        if( m_lookup != null ) {
            if( ( (m_lookup.getDisplayType() == DisplayType.List) && (Env.getContextAsInt( Env.getCtx(),"#AD_Role_ID" ) == 0) ) || (m_lookup.getDisplayType() != DisplayType.List) )    // only system admins can change lists, so no need to zoom for others
            {
                mZoom = new JMenuItem( Msg.getMsg( Env.getCtx(),"Zoom" ),Env.getImageIcon( "Zoom16.gif" ));
                mZoom.addActionListener( this );
                popupMenu.add( mZoom );
            }

            mRefresh = new JMenuItem( Msg.getMsg( Env.getCtx(),"Refresh" ),Env.getImageIcon( "Refresh16.gif" ));
            mRefresh.addActionListener( this );
            popupMenu.add( mRefresh );
        }

        // VBPartner quick entry link

        if( columnName.equals( "C_BPartner_ID" )) {
        	// LY PARTNER
            mBPartnerNew = new JMenuItem( Msg.getMsg( Env.getCtx(),"New" ),Env.getImageIcon( "InfoBPartner16.gif" ));
            mBPartnerNew.addActionListener( this );
            popupMenu.add( mBPartnerNew );
            // OTHER PARTNERS - Incorporación según plugins 
            PluginLookupUtils.insertLookupEntries(mLookupEntries);
            for (PluginLookupInterface anEntry : mLookupEntries)
            {
            	for (JMenuItem anItem : anEntry.getBPartnerLookupEntries())
            	{
            		anItem.addActionListener(this);
                	popupMenu.add(anItem);	
            	}
            }
            mBPartnerUpd = new JMenuItem( Msg.getMsg( Env.getCtx(),"Update" ),Env.getImageIcon( "InfoBPartner16.gif" ));
            mBPartnerUpd.addActionListener( this );
            popupMenu.add( mBPartnerUpd );
        }

        //

        if( (m_lookup != null) && (m_lookup.getZoom() == 0) ) {
            mZoom.setEnabled( false );
        }    	
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param lookup
     */

    public VLookup( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,Lookup lookup ) {
        super();
        super.setName( columnName );
        m_combo.setName( columnName );
        m_columnName = columnName;
        setMandatory( mandatory );
        m_lookup = lookup;

        //

        setLayout( new BorderLayout());

        VLookup_mouseAdapter mouse = new VLookup_mouseAdapter( this );    // popup

        // ***     Text & Button   ***

        m_text.addActionListener( this );
        m_text.addFocusListener( this );
        m_text.addMouseListener( mouse );

        // Button

        m_button.addActionListener( this );
        m_button.addMouseListener( mouse );
        m_button.setFocusable( false );    // don't focus when tabbing
        m_button.setMargin( new Insets( 0,0,0,0 ));

        if( columnName.equals( "C_BPartner_ID" )) {
            m_button.setIcon( Env.getImageIcon( "BPartner10.gif" ));
        } else if( columnName.equals( "M_Product_ID" )) {
            m_button.setIcon( Env.getImageIcon( "Product10.gif" ));
        } else {
            m_button.setIcon( Env.getImageIcon( "PickOpen10.gif" ));
        }

        // *** VComboBox   ***

        if( (m_lookup != null) && (m_lookup.getDisplayType() != DisplayType.Search) )    // No Search
        {

            // Memory Leak after executing the next two lines ??

            m_lookup.fillComboBox( isMandatory(),false,false,false );
            m_combo.setModel( m_lookup );
            
            // ToolTips renderer
            m_combo.setRenderer(new ToolTipComboBoxRenderer());

            //

            m_combo.addActionListener( this );    // Selection
            m_combo.addMouseListener( mouse );    // popup

            // FocusListener to refresh selection before opening

            m_combo.addFocusListener( this );
        }

        setUI( true );

        // ReadWrite       -       decides what components to show

        if( isReadOnly ||!isUpdateable || (m_lookup == null) ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        // Popup

        if( m_lookup != null ) {
            if( ( (m_lookup.getDisplayType() == DisplayType.List) && (Env.getContextAsInt( Env.getCtx(),"#AD_Role_ID" ) == 0) ) || (m_lookup.getDisplayType() != DisplayType.List) )    // only system admins can change lists, so no need to zoom for others
            {
                mZoom = new JMenuItem( Msg.getMsg( Env.getCtx(),"Zoom" ),Env.getImageIcon( "Zoom16.gif" ));
                mZoom.addActionListener( this );
                popupMenu.add( mZoom );
            }

            mRefresh = new JMenuItem( Msg.getMsg( Env.getCtx(),"Refresh" ),Env.getImageIcon( "Refresh16.gif" ));
            mRefresh.addActionListener( this );
            popupMenu.add( mRefresh );
        }

        // VBPartner quick entry link

        if( columnName.equals( "C_BPartner_ID" )) {
        	// LY PARTNER
            mBPartnerNew = new JMenuItem( Msg.getMsg( Env.getCtx(),"New" ),Env.getImageIcon( "InfoBPartner16.gif" ));
            mBPartnerNew.addActionListener( this );
            popupMenu.add( mBPartnerNew );
            // OTHER PARTNERS - Incorporación según plugins 
            PluginLookupUtils.insertLookupEntries(mLookupEntries);
            for (PluginLookupInterface anEntry : mLookupEntries)
            {
            	for (JMenuItem anItem : anEntry.getBPartnerLookupEntries())
            	{
            		anItem.addActionListener(this);
                	popupMenu.add(anItem);	
            	}
            }
            mBPartnerUpd = new JMenuItem( Msg.getMsg( Env.getCtx(),"Update" ),Env.getImageIcon( "InfoBPartner16.gif" ));
            mBPartnerUpd.addActionListener( this );
            popupMenu.add( mBPartnerUpd );
        }

        //

        if( (m_lookup != null) && (m_lookup.getZoom() == 0) ) {
            mZoom.setEnabled( false );
        }
    }    // VLookup

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text   = null;
        m_button = null;
        m_lookup = null;
        m_mField = null;

        //

        m_combo.removeFocusListener( this );
        m_combo.removeActionListener( this );
        m_combo.setModel( new DefaultComboBoxModel());    // remove reference

        // m_combo.removeAllItems();

        m_combo = null;
    }    // dispose

    /** Descripción de Campos */

    public final static int DISPLAY_LENGTH = 15;

    /** Descripción de Campos */

    public static int FIELD_HIGHT = 0;

    /** Descripción de Campos */

    private CTextField m_text = new CTextField( DISPLAY_LENGTH );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    private VComboBox m_combo = new VComboBox();

    /** Descripción de Campos */

    private volatile boolean m_settingValue = false;

    /** Descripción de Campos */

    private volatile boolean m_settingFocus = false;

    /** Descripción de Campos */

    private volatile boolean m_haveFocus = false;

    /** Descripción de Campos */

    private volatile boolean m_inserting = false;

    /** Descripción de Campos */

    private String m_lastDisplay = "";

    //

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private Lookup m_lookup;

    /** Descripción de Campos */
    
    private MTab m_tab;

    /** Descripción de Campos */

    private boolean m_comboActive = true;

    /** Descripción de Campos */

    private Object m_value;

    // Popup

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mZoom;

    /** Descripción de Campos */

    private JMenuItem mRefresh;

    /** Descripción de Campos */

    private JMenuItem mBPartnerNew;

    /** Entradas adicionales para menu contextual de BPartner */
    protected Vector<PluginLookupInterface> mLookupEntries = new Vector<PluginLookupInterface>();
    
    /** Descripción de Campos */

    private JMenuItem mBPartnerUpd;

    // Field for Value Preference

    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VLookup.class );

    protected Frame   frame     = null;
    protected Object  result    = null;
    protected boolean cancelled = false;
    protected boolean resetValue = false;
    
    /**
     * Descripción de Método
     *
     *
     * @param initial
     */

    private void setUI( boolean initial ) {
        if( initial ) {
            Dimension size = m_text.getPreferredSize();

            setPreferredSize( new Dimension( size ));    // causes r/o to be the same length
            m_combo.setPreferredSize( new Dimension( size ));
            setMinimumSize( new Dimension( 30,size.height ));
            FIELD_HIGHT = size.height;

            //

            m_text.setBorder( null );

            Dimension bSize = new Dimension( size.height,size.height );

            m_button.setPreferredSize( bSize );
        }

        // What to show

        this.remove( m_combo );
        this.remove( m_button );
        this.remove( m_text );

        //

        if( !isReadWrite())                                                                     // r/o - show text only
        {
            LookAndFeel.installBorder( this,"TextField.border" );
            this.add( m_text,BorderLayout.CENTER );
            m_text.setReadWrite( false );
            m_combo.setReadWrite( false );
            m_comboActive = false;
        } else if( (m_lookup != null) && (m_lookup.getDisplayType() != DisplayType.Search) )    // show combo if not Search
        {
            this.setBorder( null );
            this.add( m_combo,BorderLayout.CENTER );
            m_comboActive = true;
        } else    // Search or unstable - show text & button
        {
            LookAndFeel.installBorder( this,"TextField.border" );
            this.add( m_text,BorderLayout.CENTER );
            this.add( m_button,BorderLayout.EAST );
            m_text.setReadWrite( true );
            m_comboActive = false;
        }
    }             // setUI

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        boolean rw = value;

        if( m_lookup == null ) {
            rw = false;
        }

        if( m_combo.isReadWrite() != value ) {
            m_combo.setReadWrite( rw );
            setUI( false );

            if( m_comboActive ) {
                setValue( m_value );
            }
        }
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_combo.isReadWrite();
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_combo.setMandatory( mandatory );
        m_text.setMandatory( mandatory );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_combo.isMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setBackground( Color color ) {
        m_text.setBackground( color );
        m_combo.setBackground( color );
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        m_text.setBackground( error );
        m_combo.setBackground( error );
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param fg
     */

    public void setForeground( Color fg ) {
        m_text.setForeground( fg );
        m_combo.setForeground( fg );
    }    // setForeground

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
    	if(value instanceof String && ((String)value).trim().length() == 0){
    		value = null;
    	}
        log.fine( "la m_columname es ="+m_columnName + "=" + value );
        m_settingValue = true;    // disable actions
        m_value        = value;
        
        // Set both for switching

        m_combo.setValue( value );

        if( value == null ) {
            m_text.setText( null );
            m_lastDisplay  = "";
            m_settingValue = false;

            return;
        }

        if( m_lookup == null ) {
            m_text.setText( value.toString());
            m_lastDisplay  = value.toString();
            m_settingValue = false;

            return;
        }
       

        // Set Display

        m_lastDisplay = m_lookup.getDisplay( value );

        if( m_lastDisplay.equals( "<-1>" )) {
            m_lastDisplay = "";
            m_value       = null;
        }

        boolean notFound = m_lastDisplay.startsWith( "<" ) && m_lastDisplay.startsWith( ">" );

        m_text.setText( m_lastDisplay );
        m_text.setCaretPosition( 0 );    // show beginning

        // Nothing showing in Combo and should be showing

        if( (m_combo.getSelectedItem() == null) && ( m_comboActive || ( m_inserting && (m_lookup.getDisplayType() != DisplayType.Search) ) ) ) {

            // lookup found nothing too

            if( notFound ) {
                log.finest( "Not found (1) " + m_lastDisplay );

                // we may have a new value

                m_lookup.refresh();
                m_combo.setValue( value );
                m_lastDisplay = m_lookup.getDisplay( value );
                m_text.setText( m_lastDisplay );
                m_text.setCaretPosition( 0 );    // show beginning
                notFound = m_lastDisplay.startsWith( "<" ) && m_lastDisplay.endsWith( ">" );
            }

            if( notFound )              // <key>
            {
                m_value = null;
                actionCombo( null );    // data binding
                log.fine( "not found - " + value );
            }

            // we have lookup

            else if( m_combo.getSelectedItem() == null ) {
                NamePair pp = m_lookup.get( value );

                if( pp != null ) {
                    log.fine( "added to combo - " + pp );

                    // Add to Combo

                    m_combo.addItem( pp );
                    m_combo.setValue( value );
                }
            }

            // Not in Lookup - set to Null

            if( m_combo.getSelectedItem() == null ) {
                log.info( "not in Lookup - set to NULL" );
                actionCombo( null );    // data binding (calls setValue again)
                m_value = null;
            }
        }

        m_settingValue = false;
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {

         log.fine( "VLookup.propertyChange"+ evt);

        if( evt.getPropertyName().equals( MField.PROPERTY )) {
            m_inserting = MField.INSERTING.equals( evt.getOldValue());    // MField.setValue
            setValue( evt.getNewValue());
            m_inserting = false;
        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        if( m_comboActive ) {
            return m_combo.getValue();
        }

        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        String retValue = null;

        if( m_comboActive ) {
            retValue = m_combo.getDisplay();

            // check lookup

        } else if( m_lookup == null ) {
            retValue = (m_value == null)
                       ?null
                       :m_value.toString();
        } else {
            retValue = m_lookup.getDisplay( m_value );
        }

         log.fine( "VLookup.getDisplay - " + retValue+ "ComboActive=" + m_comboActive);

        return retValue;
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {
        m_mField = mField;

        if( (m_mField != null) && MRole.getDefault().isShowPreference()) {
            ValuePreference.addMenu( this,popupMenu );
        }
    }    // setField

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_settingValue || m_settingFocus ) {
            return;
        }
        log.config("action Performed con m_combo= "+ m_combo+" y e.getSource= "+ e.getSource()); 
        log.config( e.getActionCommand() + ", ComboValue=" + m_combo.getSelectedItem());

         log.fine( "VLookupHash=" + this.hashCode());

        // Preference

        if( e.getActionCommand().equals( ValuePreference.NAME )) {
            if( MRole.getDefault().isShowPreference()) {
                ValuePreference.start( m_mField,getValue(),getDisplay());
            }

            return;
        }

        // Combo Selection

        else if( e.getSource() == m_combo ) {
            Object value = getValue();
            Object o     = m_combo.getSelectedItem();

            if( o != null ) {
                String s = o.toString();

                // don't allow selection of inactive

                if( s.startsWith( MLookup.INACTIVE_S ) && s.endsWith( MLookup.INACTIVE_E )) {
                    log.info( "Selection inactive set to NULL" );
                    value = null;
                }
            }

            actionCombo( value );    // data binding
        }

        // Button pressed

        else if( e.getSource() == m_button ) {
            actionButton( "" );

            // Text entered

        } else if( e.getSource() == m_text ) {
            actionText();

            // Popup Menu

        } else if( e.getSource() == mZoom ) {
            actionZoom( m_combo.getSelectedItem());
        } else if( e.getSource() == mRefresh ) {
            actionRefresh();
        } else if( e.getSource() == mBPartnerNew ) {
            actionBPartner( true );
        } else if( e.getSource() == mBPartnerUpd ) {
            actionBPartner( false );
        }
        // Click sobre una entrada del menu de un plugin?
        boolean found = false;
        for (PluginLookupInterface anEntry : mLookupEntries)
        {
        	for (JMenuItem anItem : anEntry.getBPartnerLookupEntries())
        	{
        		if (e.getSource() == anItem )
        		{
        			anEntry.doBPartnerLookupAction(anItem, true, this);
        			found = true;
        			break;
        		}
        	}
        	if (found)
        		break;
        }

    }                                // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {
        m_combo.addActionListener( listener );
        m_text.addActionListener( listener );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void actionCombo( Object value ) {
    	log.fine("En action Combo");
        log.fine( (value == null)
                  ?"null"
                  :value.toString());
        

        try {

            // -> GridController.vetoableChange
        	log.fine("En action Combo con m_columnName= "+m_columnName + "y value= "+ value);
            fireVetoableChange( m_columnName,null,value );
       } catch( PropertyVetoException pve ) {
            log.log( Level.SEVERE,"actionCombo---->",pve );
        }
       
       /*
        *Disytel - Matias Cap
        * 
        * El problema que traía era que al borrar (desde el teclado) la entidad seleccionada y
        * luego se deseaba seleccionar la misma, no se mostraba en el lookup porque en las líneas comentadas más abajo
        * sólo se mostraba el valor si cambiaba de entidad comercial. 
        * Ésto no tenía sentido, que lo muestre igual.
        * 
        * ------------------------------------------------------------------------------------------
        * Código anterior
        * ------------------------------------------------------------------------------------------
        * 
        * /*boolean updated = false;
		*
        * if( (value == null) && (m_value == null) ) {
        *     updated = true;
        * } else if( (value != null) && value.equals( m_value )) {
        *     updated = true;
        * }
		*
        * if( !updated ) {
		*
		*     // happens if VLookup is used outside of APanel/GridController (no property listener)
		*
        *     log.fine( "Value explicitly set - new=" + value + ", old=" + m_value );
        *     setValue( value );
        * }
        */

        // is the value updated ?

               
       setValue( value );
       
       /*
        * Fin de la modificación - Disytel - Matias Cap
        */
        
    }    // actionCombo

    /**
     * Descripción de Método
     *
     *
     * @param queryValue
     */

    protected void actionButton( String queryValue ) {
        m_button.setEnabled( false );    // disable double click

        if( m_lookup == null ) {
            return;    // leave button disabled
        }

//        m_text.requestFocus();    // closes other editors
        
        
        frame     = Env.getFrame( this );
        result    = null;
        cancelled = false;
        
        String col = m_lookup.getColumnName();    // fully qualified name
        if( col.indexOf( "." ) != -1 ) {
            col = col.substring( col.indexOf( "." ) + 1 );
        }
        
        // Zoom / Validation

        String whereClause = getWhereClause();
        
        // Inicio desarrollo Dataware S.L.
        // Para despliege de un search de registros segun un campo tabla dinamico
        boolean cache = true;
        int ventanaNo = Env.getWindowNo(frame);
  		int AD_Table_ID = Env.getContextAsInt(Env.getCtx(),ventanaNo,"AD_Table_ID" );
  		
  		if (AD_Table_ID != 0 && whereClause.equals("isRecordID")){
  			
  			// Vaciamos whereClause
  			whereClause = "";
  			
  			// Recogemos el nombre de la tabla destino
  			X_AD_Table tabla = new X_AD_Table( Env.getCtx(),AD_Table_ID,null);
  			String TableName = tabla.getTableName();
  			
  			// Recogemos el nombre del campo
  			String sql = "SELECT ColumnName,isKey FROM AD_Column WHERE AD_Table_ID=?";
  			String Name = "";
  				
  	        try {
  	            PreparedStatement pstmt = DB.prepareStatement( sql );

  	            pstmt.setInt( 1,AD_Table_ID );

  	            ResultSet rs = pstmt.executeQuery();

  	            while ( rs.next()) {
  	            	if (rs.getString( 2 ).equals("Y"))
  	            		Name = rs.getString( 1 );
  	            }

  	            rs.close();
  	            pstmt.close();
  	        } catch( SQLException e ) {
  	            log.log( Level.SEVERE,sql,e );
  	        }
  	        
  	        m_keyColumnName = Name;
  	        m_tableName = TableName;
  	        cache = false;
  		}
  		// Fin desarrollo Dataware S.L.

        //
  		
        log.fine( col + ", Zoom=" + m_lookup.getZoom() + " (" + whereClause + ")" );

        //

        resetValue = false;    // reset value so that is always treated as new entry

        if (isShowInfo()) {
		    if( col.equals( "M_Product_ID" )) {
		    	
		    	showInfoProduct(queryValue,whereClause);
		    
		    } else if( col.equals( "C_BPartner_ID" )) {
		    	
		    	showInfoBPartner(queryValue, whereClause);
		    
		    } else                             // General Info
		    {
		    	showGeneralInfo(AD_Table_ID, queryValue, whereClause);
		    }
        }
        // Result

        if( result != null) {
            log.config( "Result = " + result.toString() + " (" + result.getClass().getName() + ")" );

            // make sure that value is in cache

            m_lookup.getDirect( result,false,true );

            if( resetValue ) {
                actionCombo( null );
            }

            actionCombo( result );    // data binding
        } else if( cancelled ) {
            log.config( "Result = null (cancelled)" );
            actionCombo( null );
        } else {
            log.config( "Result = null (not cancelled)" );
            setValue( m_value );      // to re-display value
        }

        //

        m_button.setEnabled( true );
//        m_text.requestFocus();
    }    // actionButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getWhereClause() {
        String whereClause = "";

        if( m_lookup == null ) {
            return "";
        }

        if( m_lookup.getZoomQuery() != null ) {
            whereClause = m_lookup.getZoomQuery().getWhereClause();
        }

        if( whereClause.length() == 0 ) {
            whereClause = m_lookup.getValidation();
        }

        // log.finest( "VLookup.getWhereClause - ZoomQuery="
        // + (m_lookup.getZoomQuery()==null ? "" : m_lookup.getZoomQuery().getWhereClause())
        // + ", Validation=" + m_lookup.getValidation());

        return whereClause;
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     */

    private void actionText() {
        String text = m_text.getText();

        // Nothing entered

        if( (text == null) || (text.length() == 0) || text.equals( "%" )) {
            actionButton( text );

            return;
        }

        // Always like

        if( !text.endsWith( "%" )) {
            text += "%";
        }

        text = text.toUpperCase();
        log.config( m_columnName + " - " + text );

        // Agregado por Disytel - Franco Bonafine
        // Extrañamente se está aplicando un parseo de traducción en lugar de un parseo de
        // contexto sobre la consulta SQL. Esto hace que fallen algunas validaciones SQL
        // asociadas a las columnas que son Lookup. Por ejemplo, al parsear @M_Product_ID@
        // estaba devolviendo "Artículo" en vez del ID del artículo en el contexto, justamente
        // porque se hace un Msg.parseTranslation(...) en lugar de un Env.parseContext(...) lo
        // cual es lo mas correcto. 
        // De esta forma se agregó la línea que pasa el SQL por el parser de contexto ANTES de
        // hacer un parsing de traducción, lo cual me parece totalmente ilógico pero lo dejo
        // por cuestiones de compatibilidad.
        String sql = getDirectAccessSQL( text );
        int windowNo = (this.m_mField != null ? this.m_mField.getWindowNo() : 0);
        sql = Env.parseContext(Env.getCtx(), windowNo, sql, true, true);
        // A continuación la línea que parsea como traducción.
        String finalSQL = Msg.parseTranslation( Env.getCtx(), sql);
        // Fin Mod. + Comentario Franco Bonafine
        int id = 0;
        int idInstance=0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( finalSQL );
            ResultSet         rs    = pstmt.executeQuery();

            if( rs.next()) {
                id = rs.getInt( 1 );    // first
				if (m_mField != null
						&& m_mField.getColumnName().compareTo("M_Product_ID") == 0) {
                	idInstance= rs.getInt(2);
                }
                if( rs.next()) {
                    id = -1;            // only if unique
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,finalSQL,e );
            id = -2;
        }

        // No (unique) result

        if( id <= 0 ) {
            if( id == 0 ) {
                log.fine( "Not Found - " + finalSQL );
            } else {
                log.fine( "Not Unique - " + finalSQL );
            }

            m_value = null;    // force re-display
            //actionButton( m_text.getText());

            return;
        }

        log.fine( "Unique ID => " + id );
        m_value = null;    // forces re-display if value is unchanged but text updated and still unique
		if (m_mField != null
				&& m_mField.getColumnName().compareTo("M_Product_ID") == 0
				&& idInstance != 0) {
        	m_tab.getField("M_AttributeSetInstance_ID").setValue(idInstance,false);
			
        }
        actionCombo( new Integer( id ));    // data binding
    }                                       // actionText

    /** Descripción de Campos */

    private String m_tableName = null;

    /** Descripción de Campos */

    private String m_keyColumnName = null;

    
    private String getDirectAccessSQLForSpecificColumn(String columnName, String text){
    	StringBuffer sql = new StringBuffer();
    	
    	if( columnName.equals( "M_Product_ID" )) {

            // Reset

            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID","0" );
            Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID","0" );

            //

            sql.append("SELECT M_Product_ID, M_AttributeSetInstance_ID FROM M_Product_Upc_Instance WHERE UPC LIKE ").append( DB.TO_STRING( text )).append(" UNION "+
                    " SELECT M_Product_ID, NULL as M_AttributeSetInstance_ID FROM M_Product WHERE UPPER(Value) LIKE " ).append( DB.TO_STRING( text )).append( " OR UPC LIKE " ).append( DB.TO_STRING( text )).append( " OR UPPER(Name) LIKE " ).append( DB.TO_STRING( text ));
        } else if( columnName.equals( "C_BPartner_ID" )) {
//            sql.append( "SELECT C_BPartner_ID FROM C_BPartner WHERE (UPPER(Value) LIKE " ).append( DB.TO_STRING( text )).append( " OR UPPER(Name) LIKE " ).append( DB.TO_STRING( text )).append( ")" );
        	sql.append( "SELECT C_BPartner_ID FROM C_BPartner WHERE (UPPER(Value) LIKE " ).append( DB.TO_STRING( text )).append( " OR UPPER(Name) LIKE " ).append( DB.TO_STRING( text )).append( " OR UPPER(TaxID) LIKE " ).append( DB.TO_STRING( text )).append( ")" );
        	if(Env.getContext( Env.getCtx(),m_lookup.getWindowNo(),"IsSOTrx" ).equals( "Y" )){
        		 sql.append( " AND C_BPartner.IsCustomer='Y' " );
            } else {
                sql.append( " AND C_BPartner.IsVendor='Y' " );
            }
        } else if( columnName.equals( "C_Order_ID" )) {
            sql.append( "SELECT C_Order_ID FROM C_Order WHERE UPPER(DocumentNo) LIKE " ).append( DB.TO_STRING( text ));
        } else if( columnName.equals( "C_Invoice_ID" )) {
            sql.append( "SELECT C_Invoice_ID FROM C_Invoice WHERE UPPER(DocumentNo) LIKE " ).append( DB.TO_STRING( text ));
        } else if( columnName.equals( "M_InOut_ID" )) {
            sql.append( "SELECT M_InOut_ID FROM M_InOut WHERE UPPER(DocumentNo) LIKE " ).append( DB.TO_STRING( text ));
        } else if( columnName.equals( "C_Payment_ID" )) {
            sql.append( "SELECT C_Payment_ID FROM C_Payment WHERE UPPER(DocumentNo) LIKE " ).append( DB.TO_STRING( text ));
        } else if( m_columnName.equals( "GL_JournalBatch_ID" )) {
            sql.append( "SELECT GL_JournalBatch_ID FROM GL_JournalBatch WHERE UPPER(DocumentNo) LIKE " ).append( DB.TO_STRING( text ));
        } else if( columnName.equals( "SalesRep_ID" )) {
            sql.append( "SELECT AD_User_ID FROM AD_User WHERE UPPER(Name) LIKE " ).append( DB.TO_STRING( text ));
            m_tableName     = "AD_User";
            m_keyColumnName = "AD_User_ID";
        }
    	
    	if( sql.length() > 0 ) {
            String wc = getWhereClause();

            if( (wc != null) && (wc.length() > 0) ) {
                sql.append( " AND " ).append( wc );
            }
            if( columnName.equals( "M_Product_ID" )==false) 
                sql.append( " AND IsActive='Y'" );

            // ***

            log.finest( "(predefined) " + sql.toString());

            return MRole.getDefault().addAccessSQL( sql.toString(),m_tableName,MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        }
    	
    	return sql.toString();
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    private String getDirectAccessSQL( String text ) {
        StringBuffer sql = new StringBuffer();

        m_tableName     = m_columnName.substring( 0,m_columnName.length() - 3 );
        m_keyColumnName = m_columnName;

        //
        sql.append(getDirectAccessSQLForSpecificColumn(m_columnName,text));
        if(!Util.isEmpty(sql.toString(),true)){
        	return sql.toString();
        }

        // Check if it is a Table Reference

        if( (m_lookup != null) && (m_lookup instanceof MLookup) ) {
            int AD_Reference_ID = (( MLookup )m_lookup ).getAD_Reference_Value_ID();

            if( AD_Reference_ID != 0 ) {
                String query = "SELECT kc.ColumnName, dc.ColumnName, t.TableName " + "FROM AD_Ref_Table rt" + " INNER JOIN AD_Column kc ON (rt.AD_Key=kc.AD_Column_ID)" + " INNER JOIN AD_Column dc ON (rt.AD_Display=dc.AD_Column_ID)" + " INNER JOIN AD_Table t ON (rt.AD_Table_ID=t.AD_Table_ID) " + "WHERE rt.AD_Reference_ID=?";
                String            displayColumnName = null;
                PreparedStatement pstmt             = null;

                try {
                    pstmt = DB.prepareStatement( query );
                    pstmt.setInt( 1,AD_Reference_ID );

                    ResultSet rs = pstmt.executeQuery();

                    if( rs.next()) {
                        m_keyColumnName   = rs.getString( 1 );
                        displayColumnName = rs.getString( 2 );
                        m_tableName       = rs.getString( 3 );
                    }

                    rs.close();
                    pstmt.close();
                    pstmt = null;
                } catch( Exception e ) {
                    log.log( Level.SEVERE,"getDirectAccessSQL",e );
                }

                try {
                    if( pstmt != null ) {
                        pstmt.close();
                    }

                    pstmt = null;
                } catch( Exception e ) {
                    pstmt = null;
                }

                // Added by Matías Cap
                // ------------------------------------------------------------
				// Verificar nuevamente si se puede obtener el sql específico
				// por la columna clave
                sql.append(getDirectAccessSQLForSpecificColumn(m_keyColumnName,text));
                if(!Util.isEmpty(sql.toString(),true)){
                	return sql.toString();
                }
                // ------------------------------------------------------------               
                
                if( displayColumnName != null ) {
                    sql = new StringBuffer();
                    sql.append( "SELECT " ).append( m_keyColumnName ).append( " FROM " ).append( m_tableName ).append( " WHERE UPPER(" ).append( displayColumnName ).append( ") LIKE " ).append( DB.TO_STRING( text )).append( " AND IsActive='Y'" );

                    String wc = getWhereClause();

                    if( (wc != null) && (wc.length() > 0) ) {
                        sql.append( " AND " ).append( wc );
                    }

                    // ***

                    log.finest( "(Table) " + sql.toString());

                    return MRole.getDefault().addAccessSQL( sql.toString(),m_tableName,MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
                }
            }    // Table Reference
        }        // MLookup

        String query = "SELECT t.TableName, c.ColumnName " + "FROM AD_Column c " + " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID AND t.IsView='N') " + "WHERE (c.ColumnName IN ('DocumentNo', 'Value', 'Name') OR c.IsIdentifier='Y')" + " AND c.AD_Reference_ID IN (10,14,11,12,22,37)" + " AND EXISTS (SELECT * FROM AD_Column cc WHERE cc.AD_Table_ID=t.AD_Table_ID" + " AND cc.IsKey='Y' AND cc.ColumnName=?)";

        m_keyColumnName = m_columnName;
        sql             = new StringBuffer();

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( query );
            pstmt.setString( 1,m_keyColumnName );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( sql.length() != 0 ) {
                    sql.append( " OR " );
                }

                m_tableName = rs.getString( 1 );
                sql.append( "UPPER(" ).append( rs.getString( 2 )).append( "::bpchar) LIKE " ).append( DB.TO_STRING( text ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getDirectAccessSQL",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        if( sql.length() == 0 ) {
            log.log( Level.SEVERE,"(TableDir) - no standard/identifier columns" );

            return "";
        }

        //

        StringBuffer retValue = new StringBuffer( "SELECT " ).append( m_columnName ).append( " FROM " ).append( m_tableName ).append( " WHERE " ).append( sql ).append( " AND IsActive='Y'" );
        String wc = getWhereClause();

        if( (wc != null) && (wc.length() > 0) ) {
            retValue.append( " AND " ).append( wc );
        }

        // ***

        log.finest( "(TableDir) " + sql.toString());

        return MRole.getDefault().addAccessSQL( retValue.toString(),m_tableName,MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
    }    // getDirectAccessSQL

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     */

    private void actionBPartner( boolean newRecord ) {
        VBPartner vbp = new VBPartner( Env.getFrame( this ),m_lookup.getWindowNo());
        int BPartner_ID = 0;

        // if update, get current value

        if( !newRecord ) {
            if( m_value instanceof Integer ) {
                BPartner_ID = (( Integer )m_value ).intValue();
            } else if( m_value != null ) {
                BPartner_ID = Integer.parseInt( m_value.toString());
            }
        }

        vbp.loadBPartner( BPartner_ID );
        vbp.setVisible(true);

        // get result

        int result = vbp.getC_BPartner_ID();

        if( (result == 0                            // 0 = not saved
                ) && (result == BPartner_ID) ) {    // the same
            return;
        }

        // Maybe new BPartner - put in cache

        m_lookup.getDirect( new Integer( result ),false,true );
        actionCombo( new Integer( result ));    // data binding
    }                                           // actionBPartner

 
    
    /**
     * Descripción de Método
     *
     *
     * @param selectedItem
     */

    private void actionZoom( Object selectedItem ) {
        if( m_lookup == null ) {
            return;
        }

        //

        MQuery zoomQuery = m_lookup.getZoomQuery();
        Object value     = getValue();

        if( value == null ) {
            value = selectedItem;
        }

        // If not already exist or exact value

        if( (zoomQuery == null) || (value != null) ) {
            zoomQuery = new MQuery();    // ColumnName might be changed in MTab.validateQuery
            // zoomQuery.addRestriction( m_mField.getReferenceColumnName(),MQuery.EQUAL,value );
            // m_mField might be null if caller didn't set it (forms usually don't set it).
            String columnName = m_mField != null 
            		? m_mField.getReferenceColumnName()
            		: m_columnName;
            zoomQuery.addRestriction( columnName,MQuery.EQUAL,value );            
        }

        int AD_Window_ID = m_lookup.getZoom( zoomQuery );

        //

        log.info( "AD_Window_ID=" + AD_Window_ID + " - Query=" + zoomQuery + " - Value=" + value );

        //

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,zoomQuery )) {
            setCursor( Cursor.getDefaultCursor());

            ValueNamePair pp  = CLogger.retrieveError();
            String        msg = (pp == null)
                                ?"AccessTableNoView"
                                :pp.getValue();

            ADialog.error( m_lookup.getWindowNo(),this,msg,(pp == null)
                    ?""
                    :pp.getName());
        } else {
        	try
        	{
	        	// si es tabledir, buscar pestaña y columna
	        	if (m_lookup.getDisplayType() == DisplayType.TableDir)
	        	{
	        		// determinar el nombre de la tabla
	        		String refColumName = (m_mField != null ? m_mField.getReferenceColumnName() : m_columnName);
	        		String tableName = refColumName.substring(0, refColumName.length()-3);
	        		frame.irATablaRegistro( tableName, (Integer)value);
	        	}
        	}
        	catch (Exception e) { e.printStackTrace(); }
            AEnv.showCenterScreen( frame );
        }

        // async window - not able to get feedback

        frame = null;

        //

        setCursor( Cursor.getDefaultCursor());
    }    // actionZoom

    /**
     * Descripción de Método
     *
     */

    private void actionRefresh() {
        if( m_lookup == null ) {
            return;
        }

        //

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        Object obj = m_combo.getSelectedItem();

        log.info( "#" + m_lookup.getSize() + ", Selected=" + obj );
        m_lookup.refresh();

        if( m_lookup.isValidated()) {
            m_lookup.fillComboBox( isMandatory(),false,false,false );
        } else {
            m_lookup.fillComboBox( isMandatory(),true,false,false );
        }

        m_combo.setSelectedItem( obj );

        // m_combo.revalidate();
        //

        setCursor( Cursor.getDefaultCursor());
        log.info( "#" + m_lookup.getSize() + ", Selected=" + m_combo.getSelectedItem());
    }    // actionRefresh

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {
    	
        if( (e.getSource() != m_combo) || e.isTemporary() || m_haveFocus || (m_lookup == null) ) {
        	log.fine("Retornando");
            return;
        }
        
        if( m_lookup.isValidated() &&!m_lookup.hasInactive()) {
        	log.fine("Retornando 1");
            return;
        }


        m_haveFocus    = true;    // prevents calling focus gained twice
        m_settingFocus = true;    // prevents actionPerformed

        //

        Object obj = m_lookup.getSelectedItem();

        log.config( m_columnName + " - Start    Count=" + m_combo.getItemCount() + ", Selected=" + obj );

        // log.fine( "VLookupHash=" + this.hashCode());

        m_lookup.fillComboBox( isMandatory(),true,true,true );    // only validated & active & temporary
        log.config( m_columnName + " - Update   Count=" + m_combo.getItemCount() + ", Selected=" + m_lookup.getSelectedItem());
        m_lookup.setSelectedItem( obj );
        log.config( m_columnName + " - Selected Count=" + m_combo.getItemCount() + ", Selected=" + m_lookup.getSelectedItem());

        //
        m_settingFocus = false;
    }    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {
        if( e.isTemporary() || (m_lookup == null) ||!m_button.isEnabled()) {    // set by actionButton
            return;
        }

        // Text Lost focus

        if( e.getSource() == m_text ) {
            String text = m_text.getText();

            log.config( "(Text) " + m_columnName + " = " + m_value + " - " + text );
            m_haveFocus = false;

            // Skip if empty

            if( ( (m_value == null) && (m_text.getText().length() == 0) ) ) {
                return;
            }

            if( m_lastDisplay.equals( text )) {
                return;
            }

            //

            actionText();    // re-display

            return;
        }

        // Combo lost focus

        if( e.getSource() != m_combo ) {
            return;
        }

        if( m_lookup.isValidated() &&!m_lookup.hasInactive()) {
            return;
        }

        //

        m_settingFocus = true;    // prevents actionPerformed

        //

        log.config( m_columnName + " = " + m_combo.getSelectedItem());

        Object obj = m_combo.getSelectedItem();

        // set original model

        if( !m_lookup.isValidated()) {
            m_lookup.fillComboBox( true );    // previous selection
        }

        // Set value

        if( obj != null ) {
            m_combo.setSelectedItem( obj );

            // original model may not have item

            if( !m_combo.getSelectedItem().equals( obj )) {
                log.fine( m_columnName + " - added to combo - " + obj );
                m_combo.addItem( obj );
                m_combo.setSelectedItem( obj );
            }
        }

        // actionCombo(getValue()); Original, la descomentamos.
        //actionCombo(getValue());

        m_settingFocus = false;
        m_haveFocus    = false;    // can gain focus again
    }                              // focusLost

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setToolTipText( String text ) {
        super.setToolTipText( text );
        m_button.setToolTipText( text );
        m_text.setToolTipText( text );
        m_combo.setToolTipText( text );
    }    // setToolTipText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int refresh() {
        if( m_lookup == null ) {
            return -1;
        }

        return m_lookup.refresh();
    }    // refresh
    
    
    protected void showInfoProduct(String queryValue, String whereClause) {
        // Reset

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID","0" );
        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID","0" );

        // Replace Value with name if no value exists

        if( (queryValue.length() == 0) && (m_text.getText().length() > 0) ) {
            queryValue = "@" + m_text.getText() + "@";    // Name indicator - otherwise Value
        }

        int M_Warehouse_ID = Env.getContextAsInt( Env.getCtx(),m_lookup.getWindowNo(),"M_Warehouse_ID" );
        int M_PriceList_ID = Env.getContextAsInt( Env.getCtx(),m_lookup.getWindowNo(),"M_PriceList_ID" );

        // Show Info

        /* Logica para plugins - Redefinicion de clases Info  */
        Info ip = PluginInfoUtils.getInfo("M_Product", frame, false, m_lookup.getWindowNo(), "", false, "", Env.getContextAsInt( Env.getCtx(),m_lookup.getWindowNo(),"M_Warehouse_ID" ), Env.getContextAsInt( Env.getCtx(),m_lookup.getWindowNo(),"M_PriceList_ID" ), null, null);
        
        if (ip==null)
        	ip = Info.factoryProduct( frame,true,m_lookup.getWindowNo(),M_Warehouse_ID,M_PriceList_ID,queryValue,false,whereClause );
        
        ip.setVisible(true);
        cancelled  = ip.isCancelled();
        result     = ip.getSelectedKey();
        resetValue = true;
        ip.requestFocus();
    }
    
    protected void showInfoBPartner(String queryValue, String whereClause) {
        // Replace Value with name if no value exists

        if( (queryValue.length() == 0) && (m_text.getText().length() > 0) ) {
            queryValue = m_text.getText();
        }

        boolean isSOTrx = true;    // default

        if( Env.getContext( Env.getCtx(),m_lookup.getWindowNo(),"IsSOTrx" ).equals( "N" )) {
            isSOTrx = false;
        }

        /* Logica para plugins - Redefinicion de clases Info  */
        Info ip = PluginInfoUtils.getInfo("C_BPartner", frame, false, m_lookup.getWindowNo(), "", false, "", null, null, isSOTrx, null);
        
        if (ip==null)
        	ip = new InfoBPartner( frame,true,m_lookup.getWindowNo(),queryValue,isSOTrx,false,whereClause );

        ip.setVisible(true);
        cancelled = ip.isCancelled();
        result    = ip.getSelectedKey();
    }
    
    protected void showGeneralInfo(int AD_Table_ID, String queryValue, String whereClause) {
    	// Inicio desarrollo Dataware S.L.
    	if (AD_Table_ID == 0 && whereClause.equals("isRecordID")){
    		cancelled = true;
  		}
    	// Fin desarrollo Dataware S.L.
    	else{
            if( m_tableName == null ) {    // sets table name & key column
                getDirectAccessSQL( "*" );
            }

            Info ig = Info.create( frame,true,m_lookup.getWindowNo(),m_tableName,m_keyColumnName,queryValue,false,whereClause );

            ig.setVisible(true);
            cancelled = ig.isCancelled();
            result    = ig.getSelectedKey();
        // Inicio desarrollo Dataware S.L.
    	}
    	// Fin desarrollo Dataware S.L.
    }

	public Lookup getM_lookup() {
		return m_lookup;
	}

	protected void setM_lookup(Lookup m_lookup) {
		this.m_lookup = m_lookup;
	}

	public CTextField getM_text() {
		return m_text;
	}

	protected void setM_text(CTextField m_text) {
		this.m_text = m_text;
	}

	/**
	 * @return Returns the m_button.
	 */
	public CButton getM_button() {
		return m_button;
	}

	/**
	 * @param m_button The m_button to set.
	 */
	protected void setM_button(CButton m_button) {
		this.m_button = m_button;
	}

	class ToolTipComboBoxRenderer extends BasicComboBoxRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			String text = (value == null) ? "" : value.toString();
			int stringWidth = getCombo().getFontMetrics(getCombo().getFont()).stringWidth(text);
			String toolTipText = stringWidth > getCombo().getWidth()?text:null;
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index) {
					list.setToolTipText(toolTipText);
				}
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(text);
			return this;
		}
		
		public Point getToolTipLocation(MouseEvent event) {
			return new Point(0, 0);
		}
	}
	
	protected VComboBox getCombo() {
		return m_combo;
	}
	
	private boolean showInfo = true;

	/**
	 * @return the showInfo
	 */
	public boolean isShowInfo() {
		return showInfo;
	}

	/**
	 * @param showInfo the showInfo to set
	 */
	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}

	@Override
	public void requestFocus() {
		if (m_text != null) {
			m_text.requestFocus();
		} else {
			super.requestFocus();
		}
	}

	@Override
	public boolean hasFocus() {
		if (m_text != null) {
			return m_text.hasFocus();
		} else {
			return super.hasFocus();
		}
	}
	
	public boolean isComboActive() {
		return m_comboActive;
	}
	
	public JComboBox getComboBox() {
		return m_combo;
	}

	public Object getM_value() {
		return m_value;
	}
    
	
}    // VLookup


/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VLookup_mouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VLookup_mouseAdapter( VLookup adaptee ) {
        this.adaptee = adaptee;
    }    // VLookup_mouseAdapter

    /** Descripción de Campos */

    private VLookup adaptee;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // System.out.println("mouseClicked " + e.getID() + " " + e.getSource().getClass().toString());
        // popup menu

        if( SwingUtilities.isRightMouseButton( e )) {
            adaptee.popupMenu.show(( Component )e.getSource(),e.getX(),e.getY());
        }
    }    // mouse Clicked
    
    

}    // VLookup_mouseAdapter


/*
 *  @(#)VLookup.java   02.07.07
 * 
 *  Fin del fichero VLookup.java
 *  
 *  Versión 2.1
 *
 */

