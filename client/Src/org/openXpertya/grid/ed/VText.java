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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CTextArea;
import org.openXpertya.apps.ScriptEditor;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VText extends CTextArea implements VEditor,KeyListener,ActionListener {

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
     */

    public VText( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayLength,int fieldLength ) {
        super((fieldLength < 300)?1:3,displayLength );
        
        super.setName( columnName );
        
        LookAndFeel.installBorder( this,"TextField.border" );

        // Create Editor

        setColumns( (displayLength > VString.MAXDISPLAY_LENGTH)?VString.MAXDISPLAY_LENGTH:displayLength );    // 46
        setForeground( CompierePLAF.getTextColor_Normal());
        setBackground( CompierePLAF.getFieldBackground_Normal());
        setLineWrap( true );
        setWrapStyleWord( true );
        setMandatory( mandatory );
        m_columnName = columnName;

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        }

        addKeyListener( this );

        // Popup

        addMouseListener( new VText_mouseAdapter( this ));

        if( columnName.equals( "Script" )) {
            menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"Script" ),Env.getImageIcon( "Script16.gif" ));
        } else {
            menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"Editor" ),Env.getImageIcon( "Editor16.gif" ));
        }

        menuEditor.addActionListener( this );
        popupMenu.add( menuEditor );
    }    // VText

    /**
     * Descripción de Método
     *
     */

    public void dispose() {}    // dispose

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem menuEditor;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private String m_oldText;

    /** Descripción de Campos */

    private String m_initialText;

    /** Descripción de Campos */

    private volatile boolean m_setting = false;

    private MField m_Field;
    
    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        if( value == null ) {
            m_oldText = "";
        } else {
            m_oldText = value.toString();
        }

        if( m_setting ) {
            return;
        }

        super.setValue( m_oldText );
        m_initialText = m_oldText;

        // Always position Top

        setCaretPosition( 0 );
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
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        if( e.getActionCommand().equals( ValuePreference.NAME )) {
            if( MRole.getDefault().isShowPreference()) {
                ValuePreference.start( m_Field,getValue());
            }

            return;
        }

    	
    	
    	if( e.getSource() == menuEditor ) {
            menuEditor.setEnabled( false );

            String s = null;

            if( m_columnName.equals( "Script" )) {
                s = ScriptEditor.start( Msg.translate( Env.getCtx(),m_columnName ),getText(),isEditable(),0 );
            } else {
                s = Editor.startEditor( this,Msg.translate( Env.getCtx(),m_columnName ),getText(),isEditable());
            }

            menuEditor.setEnabled( true );

            // Data Binding

            try {
                fireVetoableChange( m_columnName,m_oldText,s );
            } catch( PropertyVetoException pve ) {
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {}    // addActionListener

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

        // ESC

        if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            setText( m_initialText );
        }

        m_setting = true;

        try {
            fireVetoableChange( m_columnName,m_oldText,getText());
        } catch( PropertyVetoException pve ) {
        }

        m_setting = false;
    }    // keyReleased

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {
        m_Field = mField;
    	if( (mField != null) && MRole.getDefault().isShowPreference()) {
            ValuePreference.addMenu( this,popupMenu );
        }
    	
    }    // setField
}    // VText


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VText_mouseAdapter extends MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VText_mouseAdapter( VText adaptee ) {
        this.adaptee = adaptee;
    }    // VText_mouseAdapter

    /** Descripción de Campos */

    private VText adaptee;

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
    }    // mouse Clicked
}    // VText_mouseAdapter



/*
 *  @(#)VText.java   02.07.07
 * 
 *  Fin del fichero VText.java
 *  
 *  Versión 2.2
 *
 */
