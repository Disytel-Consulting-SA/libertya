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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VDate extends JComponent implements VEditor,ActionListener,KeyListener,FocusListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VDate() {
        this( DisplayType.Date );
    }    // VDate

    /**
     * Constructor de la clase ...
     *
     *
     * @param displayType
     */

    public VDate( int displayType ) {
        this( "Date",false,false,true,displayType,"Date" );
    }    // VDate

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param displayType
     * @param title
     */

    public VDate( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayType,String title ) {
        super();
        super.setName( columnName );
        m_columnName = columnName;
        m_title      = title;

        //

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());
        this.setFocusable( false );

        // Size

        this.setPreferredSize( m_text.getPreferredSize());

        int height = m_text.getPreferredSize().height;

        setMinimumSize( new Dimension( 30,height ));

        VDate_mouseAdapter mouse = new VDate_mouseAdapter( this );    // popup

        m_text.addMouseListener( mouse );

        // ***     Text    ***

        m_text.setBorder( null );
        m_text.setHorizontalAlignment( JTextField.TRAILING );

        if( m_displayType == DisplayType.Date ) {
            m_text.addFocusListener( this );
            m_text.addKeyListener( this );
            m_text.setCaret( new VOvrCaret());
        } else if( m_displayType == DisplayType.DateTime ) {
            m_text.setColumns( 20 );
        }

        // Background

        setMandatory( mandatory );
        this.add( m_text,BorderLayout.CENTER );

        //

        if( (displayType == DisplayType.DateTime) || (displayType == DisplayType.Time) ) {
            m_displayType = displayType;    // default = Date
        }

        setFormat();

        // ***     Button  ***

        m_button.setIcon( Env.getImageIcon( "Calendar10.gif" ));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setPreferredSize( new Dimension( height,height ));
        m_button.addActionListener( this );
        m_button.setFocusable( false );
        this.add( m_button,BorderLayout.EAST );

        // Prefereed Size

        this.setPreferredSize( this.getPreferredSize());    // causes r/o to be the same length

        // ReadWrite

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }
    }    // VDate

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text   = null;
        m_button = null;
        m_mField = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param doc
     */

    protected void setDocument( Document doc ) {
        m_text.setDocument( doc );
    }    // getDocument

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    protected int m_displayType = DisplayType.Date;

    /** Descripción de Campos */

    private String m_title;

    /** Descripción de Campos */

    private boolean m_setting;

    /** Descripción de Campos */

    private String m_oldText;

    /** Descripción de Campos */

    private String m_initialText;

    //

    /** Descripción de Campos */

    private SimpleDateFormat m_format;

    //

    /** Descripción de Campos */

    private boolean m_readWrite;

    /** Descripción de Campos */

    private boolean m_mandatory;

    /** Descripción de Campos */

    private CTextField m_text = new CTextField( 12 );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    // Popup

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    // Field for Value Preference

    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VDate.class );

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        m_readWrite = value;

        // this.setFocusable(value);
        // editor

        if( m_displayType == DisplayType.Date ) {
            m_text.setReadWrite( value );    // sets Background
        } else {
            m_text.setEditable( false );
            m_text.setFocusable( false );
            setBackground( false );
        }

        // Don't show button if not ReadWrite

        if( m_button.isVisible() != value ) {
            m_button.setVisible( value );
        }

        if( m_button.isEnabled() != value ) {
            m_button.setEnabled( value );
        }
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_readWrite;
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_mandatory = mandatory;
        m_text.setMandatory( mandatory );
        setBackground( false );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_mandatory;
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        if( error ) {
            m_text.setBackground( CompierePLAF.getFieldBackground_Error());
        } else if( !m_readWrite ) {
            m_text.setBackground( CompierePLAF.getFieldBackground_Inactive());
        } else if( m_mandatory ) {
            m_text.setBackground( CompierePLAF.getFieldBackground_Mandatory());
        } else {
            m_text.setBackground( CompierePLAF.getFieldBackground_Normal());
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param fg
     */

    public void setForeground( Color fg ) {
        m_text.setForeground( fg );
    }    // setForeground

    /**
     * Descripción de Método
     *
     */

    public void setFormat() {
        m_format = DisplayType.getDateFormat( m_displayType );

        if( m_displayType == DisplayType.Date ) {
            m_text.setDocument( new MDocDate( m_displayType,m_format,m_text,m_title ));
        }
    }    // setFormat

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        log.finest( "Value=" + value );
        m_oldText = "";

        if( value == null ) {
            ;
        } else if( value instanceof java.util.Date ) {
            m_oldText = m_format.format( value );
        } else {
            String strValue = value.toString();

            // String values - most likely in YYYY-MM-DD       (JDBC format)

            try {
                java.util.Date date = DisplayType.getDateFormat_JDBC().parse( strValue );

                m_oldText = m_format.format( date );    // convert to display value
            } catch( ParseException pe0 ) {

                // Try local string format

                try {
                    java.util.Date date = m_format.parse( strValue );

                    m_oldText = m_format.format( date );
                } catch( ParseException pe1 ) {
                    log.log( Level.SEVERE,"setValue - " + pe1.getMessage());
                    m_oldText = "";
                }
            }
        }

        if( m_setting ) {
            return;
        }

        m_text.setText( m_oldText );
        m_initialText = m_oldText;
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {
        if( evt.getPropertyName().equals( org.openXpertya.model.MField.PROPERTY )) {
            setValue( evt.getNewValue());
        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getTimestamp() {
        if( m_text == null ) {
            return null;
        }

        String value = m_text.getText();

        if( (value == null) || (value.length() == 0) ) {
            return null;
        }

        //

        Timestamp ts = null;

        try {
            java.util.Date date = m_format.parse( value );

            ts = new Timestamp( date.getTime());
        } catch( ParseException pe ) {
            log.fine( pe.getMessage());
        }

        return ts;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        return getTimestamp();
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return m_text.getText();
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Preference

        if( e.getActionCommand().equals( ValuePreference.NAME )) {
            if( MRole.getDefault().isShowPreference()) {
                ValuePreference.start( m_mField,getValue(),getDisplay());
            }

            return;
        }

        if( e.getSource() == m_button ) {
        	openCalendar();
        }
    }    // actionPerformed

    // addActionListener

    private void openCalendar() {
        m_button.setEnabled( false );
        setValue( startCalendar( this,getTimestamp(),m_format,m_displayType,m_title ));

        try {
            fireVetoableChange( m_columnName,m_oldText,getValue());
        } catch( PropertyVetoException pve ) {
        }

        m_button.setEnabled( true );
        m_text.requestFocus();
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
        log.finest( "Key=" + e.getKeyCode());

        // ESC

        if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            m_text.setText( m_initialText );
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        	openCalendar();
        }
        
        m_setting = true;

        try {
            Timestamp ts = getTimestamp();    // getValue

            if( ts == null ) {                // format error - just indicate change
                fireVetoableChange( m_columnName,m_oldText,null );
            } else {
                fireVetoableChange( m_columnName,m_oldText,ts );
            }
        } catch( PropertyVetoException pve ) {
        }

        m_setting = false;
    }    // keyReleased

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {}    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {

        // did not get Focus first

        if( e.isTemporary()) {
            return;
        }

        // log.config( "VDate.focusLost");

        if( (m_text == null) || (m_text.getText() == null) ) {
            return;
        }

        Object value = getValue();
        
        // Disytel.  El calendario molesta
/*
        if( (value == null) && isMandatory()) {
            setValue( startCalendar( this,getTimestamp(),m_format,m_displayType,m_title ));
        } else {*/
            setValue( value );
/*        }*/
    }    // focusLost

    /**
     * Descripción de Método
     *
     *
     * @param jc
     * @param value
     * @param format
     * @param displayType
     * @param title
     *
     * @return
     */

    public static Timestamp startCalendar( Container jc,Timestamp value,SimpleDateFormat format,int displayType,String title ) {
        log.config( "Date=" + value );

        // Find frame

        Frame frame = Env.getFrame( jc );

        // Actual Call

        Calendar cal = new Calendar( frame,title,value,displayType );

        AEnv.showCenterWindow( frame,cal );

        Timestamp result = cal.getTimestamp();

        cal = null;

        //

        log.config( "Result=" + result );

        if( result != null ) {
            return result;
        } else {
            return value;    // original value
        }
    }                        // startCalendar

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {
        m_mField = mField;

        if( (m_mField != null) && MRole.getDefault().isShowPreference()) {
            ValuePreference.addMenu( this,popupMenu );
        }
    }    // setField

    /**
     * Descripción de Método
     *
     *
     * @param enabled
     */

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        m_text.setEnabled( enabled );
        m_button.setEnabled( enabled );

        if( enabled ) {
            m_button.setReadWrite( m_readWrite );
        }
    }    // setEnabled

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public void removeActionListener( ActionListener l ) {
        listenerList.remove( ActionListener.class,l );
    }    // removeActionListener

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public void addActionListener( ActionListener l ) {
        listenerList.add( ActionListener.class,l );
    }    // addActionListener
    
    public JTextField getTextField() {
    	return m_text;
    }
}    // VDate


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VDate_mouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VDate_mouseAdapter( VDate adaptee ) {
        this.adaptee = adaptee;
    }    // VLookup_mouseAdapter

    /** Descripción de Campos */

    private VDate adaptee;

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
 *  @(#)VDate.java   02.07.07
 * 
 *  Fin del fichero VDate.java
 *  
 *  Versión 2.2
 *
 */
