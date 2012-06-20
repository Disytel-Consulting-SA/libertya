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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.model.Obscure;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VString extends CTextField implements VEditor,ActionListener,FocusListener {

    /** Descripción de Campos */

    public static final int MAXDISPLAY_LENGTH = org.openXpertya.model.MField.MAXDISPLAY_LENGTH;

    /**
     * Constructor de la clase ...
     *
     */

    public VString() {
        this( "String",false,false,true,10,10,"",null );
    }    // VString

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param displayLength
     * @param fieldLength
     * @param VFormat
     * @param ObscureType
     */

    public VString( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayLength,int fieldLength,String VFormat,String ObscureType ) {
        super( (displayLength > MAXDISPLAY_LENGTH)
               ?MAXDISPLAY_LENGTH
               :displayLength );
        super.setName( columnName );
        m_columnName = columnName;

        if( VFormat == null ) {
            VFormat = "";
        }

        m_VFormat     = VFormat;
        m_fieldLength = fieldLength;

        if( (m_VFormat.length() != 0) || (m_fieldLength != 0) ) {
            setDocument( new MDocString( m_VFormat,m_fieldLength,this ));
        }

        if( m_VFormat.length() != 0 ) {
            setCaret( new VOvrCaret());
        }

        //

        setMandatory( mandatory );

        if( (ObscureType != null) && (ObscureType.length() > 0) ) {
            m_obscure     = new Obscure( "",ObscureType );
            m_stdFont     = getFont();
            m_obscureFont = new Font( "SansSerif",Font.ITALIC,m_stdFont.getSize());
            addFocusListener( this );
        }

        // Editable

        if( isReadOnly ||!isUpdateable ) {
            setEditable( false );
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        }

        this.addKeyListener( this );
        this.addActionListener( this );

        // Popup for Editor

        if( fieldLength > displayLength ) {
            addMouseListener( new VString_mouseAdapter( this ));
            mEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"Editor" ),Env.getImageIcon( "Editor16.gif" ));
            mEditor.addActionListener( this );
            popupMenu.add( mEditor );
        }

        setForeground( CompierePLAF.getTextColor_Normal());
        setBackground( CompierePLAF.getFieldBackground_Normal());
    }    // VString

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_mField = null;
    }    // dispose

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mEditor;

    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private String m_oldText;

    /** Descripción de Campos */

    private String m_initialText;

    /** Descripción de Campos */

    private String m_VFormat;

    /** Descripción de Campos */

    private int m_fieldLength;

    /** Descripción de Campos */

    private Obscure m_obscure = null;

    /** Descripción de Campos */

    private Font m_stdFont = null;

    /** Descripción de Campos */

    private Font m_obscureFont = null;

    /** Descripción de Campos */

    private volatile boolean m_setting = false;

    /** Descripción de Campos */

    private volatile boolean m_infocus = false;

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {

        // log.config( "VString.setValue", value);

        if( value == null ) {
            m_oldText = "";
        } else {
            m_oldText = value.toString();
        }

        // only set when not updated here

        if( m_setting ) {
            return;
        }

        setText( m_oldText );
        m_initialText = m_oldText;

        // If R/O left justify

        if( !isEditable() ||!isEnabled()) {
            setCaretPosition( 0 );
        }
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

    public Object getValue() {
        return getText();
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return super.getText();    // optionally obscured
    }                              // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyReleased( KeyEvent e ) {

        // ESC

        if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            setText( m_initialText );
        }

        m_setting = true;

        try {
            String clear = getText();

            fireVetoableChange( m_columnName,m_oldText,clear );
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

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ValuePreference.NAME )) {
            if( MRole.getDefault().isShowPreference()) {
                ValuePreference.start( m_mField,getValue());
            }

            return;
        }

        // Invoke Editor

        if( e.getSource() == mEditor ) {
            String s = Editor.startEditor( this,Msg.translate( Env.getCtx(),m_columnName ),getText(),isEditable());

            setText( s );
        }

        // Data Binding

        try {
            fireVetoableChange( m_columnName,m_oldText,getText());
        } catch( PropertyVetoException pve ) {
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {
        m_mField = mField;

        if( (m_mField != null) && MRole.getDefault().isShowPreference() && 
        	!m_mField.getColumnName().equals("Value") &&
        	!m_mField.getColumnName().equals("DocumentNo")){
            ValuePreference.addMenu( this,popupMenu );
        }
    }    // setField

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setText( String text ) {
        if( (m_obscure != null) &&!m_infocus ) {
            super.setFont( m_obscureFont );
            super.setText( m_obscure.getObscuredValue( text ));
            super.setForeground( Color.gray );
        } else {
            if( m_stdFont != null ) {
                super.setFont( m_stdFont );
                super.setForeground( CompierePLAF.getTextColor_Normal());
            }

            super.setText( text );
        }
    }    // setText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getText() {
        String text = super.getText();

        if( (m_obscure != null) && (text != null) && (text.length() > 0) ) {
            if( text.equals( m_obscure.getObscuredValue())) {
                text = m_obscure.getClearValue();
            }
        }

        return text;
    }    // getText

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {
        m_infocus = true;
        setText( getText());    // clear
    }                           // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {
        m_infocus = false;
        setText( getText());    // obscure
    }                           // focus Lost
}    // VString


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VString_mouseAdapter extends MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VString_mouseAdapter( VString adaptee ) {
        this.adaptee = adaptee;
    }    // VString_mouseAdapter

    /** Descripción de Campos */

    private VString adaptee;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // popup menu

        if( SwingUtilities.isRightMouseButton( e )) {
            adaptee.popupMenu.show(( Component )e.getSource(),e.getX(),e.getY());
        }
    }    // mouseClicked
}    // VText_mouseAdapter



/*
 *  @(#)VString.java   02.07.07
 * 
 *  Fin del fichero VString.java
 *  
 *  Versión 2.2
 *
 */
