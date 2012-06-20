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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CTextArea;
import org.openXpertya.apps.ScriptEditor;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VMemo extends CTextArea implements VEditor,KeyListener,FocusListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VMemo() {
        this( "",false,false,true,60,4000 );
    }    // VMemo

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

    public VMemo( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayLength,int fieldLength ) {
        super( fieldLength / 80,50 );
        super.setName( columnName );
        LookAndFeel.installBorder( this,"TextField.border" );
        this.addFocusListener( this );    // to activate editor

        // Create Editor

        setColumns( (displayLength > VString.MAXDISPLAY_LENGTH)
                    ?VString.MAXDISPLAY_LENGTH
                    :displayLength );               // 46
        setForeground( CompierePLAF.getTextColor_Normal());
        setBackground( CompierePLAF.getFieldBackground_Normal());
        setLineWrap( true );
        setWrapStyleWord( true );
        addFocusListener( this );
        setInputVerifier( new CInputVerifier());    // Must be set AFTER addFocusListener in order to work
        setMandatory( mandatory );
        m_columnName = columnName;

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        }

        addKeyListener( this );

        // Popup

        addMouseListener( new VMemo_mouseAdapter( this ));

        if( columnName.equals( "Script" )) {
            menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"Script" ),Env.getImageIcon( "Script16.gif" ));
        } else {
            menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"Editor" ),Env.getImageIcon( "Editor16.gif" ));
        }

        menuEditor.addActionListener( this );
        popupMenu.add( menuEditor );
    }    // VMemo

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

    private String m_oldText = "";

    /** Descripción de Campos */

    private boolean m_firstChange;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VMemo.class );

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        super.setValue( value );
        m_firstChange = true;

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
        if( e.getSource() == menuEditor ) {
            menuEditor.setEnabled( false );

            String s = null;

            if( m_columnName.equals( "Script" )) {
                s = ScriptEditor.start( Msg.translate( Env.getCtx(),m_columnName ),getText(),isEditable(),0 );
            } else {
                s = Editor.startEditor( this,Msg.translate( Env.getCtx(),m_columnName ),getText(),isEditable());
            }

            menuEditor.setEnabled( true );
            setValue( s );

            try {
                fireVetoableChange( m_columnName,null,getText());
                m_oldText = getText();
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

        if( (e.getKeyCode() == KeyEvent.VK_ESCAPE) &&!getText().equals( m_oldText )) {
            log.fine( "VMemo.keyReleased - ESC" );
            setText( m_oldText );

            return;
        }

        // Indicate Change

        if( m_firstChange &&!m_oldText.equals( getText())) {
            log.fine( "VMemo.keyReleased - firstChange" );
            m_firstChange = false;

            try {
                String text = getText();

                fireVetoableChange( m_columnName,text,null );    // No data committed - done when focus lost !!!
            } catch( PropertyVetoException pve ) {
            }
        }    // firstChange
    }        // keyReleased

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {
        log.config( e.paramString());

        if( e.getSource() instanceof VMemo ) {
            requestFocus();
        } else {
            m_oldText = getText();
        }
    }    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {

        // log.config( "VMemo.focusLost " + e.getSource(), e.paramString());
        // something changed?

        return;
    }    // focusLost

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {}    // setField

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private class CInputVerifier extends InputVerifier {

        /**
         * Descripción de Método
         *
         *
         * @param input
         *
         * @return
         */

        public boolean verify( JComponent input ) {

            // NOTE: We return true no matter what since the InputVerifier is only introduced to fireVetoableChange in due time

            if( (getText() == null) && (m_oldText == null) ) {
                return true;
            } else if( getText().equals( m_oldText )) {
                return true;
            }

            //

            try {
                String text = getText();

                fireVetoableChange( m_columnName,null,text );
                m_oldText = text;

                return true;
            } catch( PropertyVetoException pve ) {
            }

            return true;
        }    // verify
    }    // CInputVerifier
}        // VMemo


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VMemo_mouseAdapter extends MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VMemo_mouseAdapter( VMemo adaptee ) {
        this.adaptee = adaptee;
    }    // VMemo_mouseAdapter

    /** Descripción de Campos */

    private VMemo adaptee;

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
}    // VMemo_mouseAdapter



/*
 *  @(#)VMemo.java   02.07.07
 * 
 *  Fin del fichero VMemo.java
 *  
 *  Versión 2.2
 *
 */
