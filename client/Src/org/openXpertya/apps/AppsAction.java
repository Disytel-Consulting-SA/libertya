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



package org.openXpertya.apps;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.compiere.swing.CButton;
import org.compiere.swing.CToggleButton;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class AppsAction extends AbstractAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param action
     * @param accelerator
     * @param toggle
     */

    public AppsAction( String action,KeyStroke accelerator,boolean toggle ) {
        super();
        m_action = action;

        // Data

        String text = Msg.getMsg( Env.getCtx(),action );
        int    pos  = text.indexOf( "&" );

        if( pos != -1 )    // We have a nemonic
        {
            Character ch = new Character( text.toUpperCase().charAt( pos + 1 ));

            text = text.substring( 0,pos ) + text.substring( pos + 1 );
            putValue( Action.MNEMONIC_KEY,new Integer( ch.hashCode()));
        }

        //

        Icon small        = getIcon( action,true );
        Icon large        = getIcon( action,false );
        Icon smallPressed = null;
        Icon largePressed = null;

        m_toggle = toggle;

        // ToggleIcons have the pressed name with X

        if( toggle ) {
            smallPressed = getIcon( action + "X",true );

            if( smallPressed == null ) {
                smallPressed = small;
            }

            largePressed = getIcon( action + "X",false );

            if( largePressed == null ) {
                largePressed = large;
            }
        }

        // Attributes

        putValue( Action.NAME,text );                      // Display
        putValue( Action.SMALL_ICON,small );               // Icon
        putValue( Action.SHORT_DESCRIPTION,text );         // Tooltip
        putValue( Action.ACTION_COMMAND_KEY,m_action );    // ActionCammand
        putValue( Action.ACCELERATOR_KEY,accelerator );    // KeyStroke

        // putValue(Action.MNEMONIC_KEY, new Integer(0));      //  Mnemonic
        // putValue(Action.DEFAULT, text);                                         //      Not Used

        // Create Button

        if( toggle ) {
            m_button = new CToggleButton( this );
            m_button.setSelectedIcon( largePressed );
        } else {
            m_button = new CButton( this );
        }

        m_button.setName( action );

        // Correcting Action items

        if( large != null ) {
            m_button.setIcon( large );
            m_button.setText( null );
        }

        m_button.setActionCommand( m_action );
        m_button.setMargin( BUTTON_INSETS );
        m_button.setSize( BUTTON_SIZE );

        // Create Menu

        if( toggle ) {
            m_menu = new JCheckBoxMenuItem( this );
            m_menu.setSelectedIcon( smallPressed );
        } else {
            m_menu = new JMenuItem( this );
        }

        m_menu.setAccelerator( accelerator );
        m_menu.setActionCommand( m_action );
    }    // Action

    /** Descripción de Campos */

    public static final Dimension BUTTON_SIZE = new Dimension( 28,28 );

    /** Descripción de Campos */

    public static final Insets BUTTON_INSETS = new Insets( 0,0,0,0 );

    /** Descripción de Campos */

    private AbstractButton m_button;

    /** Descripción de Campos */

    private JMenuItem m_menu;

    /** Descripción de Campos */

    private String m_action;

    /** Descripción de Campos */

    private ActionListener m_delegate = null;

    /** Descripción de Campos */

    private boolean m_toggle = false;

    /** Descripción de Campos */

    private boolean m_pressed = false;

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param small
     *
     * @return
     */

    private ImageIcon getIcon( String name,boolean small ) {
        String fullName = name + ( small
                                   ?"16.gif"
                                   :"24.gif" );

        return Env.getImageIcon( fullName );
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_action;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public AbstractButton getButton() {
        return m_button;
    }    // getButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public JMenuItem getMenuItem() {
        return m_menu;
    }    // getMenuItem

    /**
     * Descripción de Método
     *
     *
     * @param al
     */

    public void setDelegate( ActionListener al ) {
        m_delegate = al;
    }    // setDelegate

    /**
     * Descripción de Método
     *
     *
     * @param pressed
     */

    public void setPressed( boolean pressed ) {
        if( !m_toggle ) {
            return;
        }

        m_pressed = pressed;

        // Set Button

        m_button.setSelected( pressed );

        // Set Menu

        m_menu.setSelected( pressed );
    }    // setPressed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPressed() {
        return m_pressed;
    }    // isPressed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.info( "AppsAction.actionPerformed", e.getActionCommand());
        // Toggle Items

        if( m_toggle ) {
            setPressed( !m_pressed );
        }

        // Inform

        if( m_delegate != null ) {
            m_delegate.actionPerformed( e );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_button = null;
        m_menu   = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "AppsAction - " );

        sb.append( m_action ).append( " Acc=" ).append( getValue( Action.ACCELERATOR_KEY ));

        return sb.toString();
    }    // toString
}    // AppsAction



/*
 *  @(#)AppsAction.java   02.07.07
 * 
 *  Fin del fichero AppsAction.java
 *  
 *  Versión 2.2
 *
 */
