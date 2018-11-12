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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MLocationLookup;
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

public class VLocation extends JComponent implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param mLocation
     */

    public VLocation( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,MLocationLookup mLocation ) {
        super();
        super.setName( columnName );
        m_columnName = columnName;
        m_mLocation  = mLocation;

        //

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());

        // Size

        this.setPreferredSize( m_text.getPreferredSize());    // causes r/o to be the same length

        int height = m_text.getPreferredSize().height;

        // Button

        m_button.setIcon( Env.getImageIcon( "Location10.gif" ));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setPreferredSize( new Dimension( height,height ));
        m_button.addActionListener( this );
        m_button.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					actionPerformed(new ActionEvent(m_button, ActionEvent.ACTION_PERFORMED, null));
				}
			}
        	
        });
        this.add( m_button,BorderLayout.EAST );

        // ***     Button & Text   ***

        m_text.setBorder( null );
        m_text.setEditable( false );
        m_text.setFocusable( false );
        m_text.setFont( CompierePLAF.getFont_Field());
        m_text.setForeground( CompierePLAF.getTextColor_Normal());
        m_text.addMouseListener( new VLocation_mouseAdapter( this ));
        this.add( m_text,BorderLayout.CENTER );

        // Editable

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        setMandatory( mandatory );

        //

        mDelete = new JMenuItem( Msg.getMsg( Env.getCtx(),"Delete" ),Env.getImageIcon( "Delete16.gif" ));
        mDelete.addActionListener( this );
        popupMenu.add( mDelete );
    }    // VLocation

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text      = null;
        m_button    = null;
        m_mLocation = null;
    }    // dispose

    /** Descripción de Campos */

    private JTextField m_text = new JTextField( VLookup.DISPLAY_LENGTH );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    private MLocationLookup m_mLocation;

    /** Descripción de Campos */

    private MLocation m_value;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VLocation.class );

    // Popup

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mDelete;

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        m_button.setReadWrite( value );

        if( m_button.isVisible() != value ) {
            m_button.setVisible( value );
        }

        setBackground( false );
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_button.isReadWrite();
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_button.setMandatory( mandatory );
        setBackground( false );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_button.isMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setBackground( Color color ) {
        if( !color.equals( m_text.getBackground())) {
            m_text.setBackground( color );
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        if( error ) {
            setBackground( CompierePLAF.getFieldBackground_Error());
        } else if( !isReadWrite()) {
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        } else if( isMandatory()) {
            setBackground( CompierePLAF.getFieldBackground_Mandatory());
        } else {
            setBackground( CompierePLAF.getFieldBackground_Normal());
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
     *
     * @param value
     */

    public void setValue( Object value ) {
        if( value == null ) {
            m_value = null;
            m_text.setText( null );
        } else {
            m_value = m_mLocation.getLocation( value,null );

            if( m_value == null ) {
                m_text.setText( "<" + value + ">" );
            } else {
                m_text.setText( m_value.toString());
            }
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
        if( m_value == null ) {
            return null;
        }

        return new Integer( m_value.getC_Location_ID());
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Location_ID() {
        if( m_value == null ) {
            return 0;
        }

        return m_value.getC_Location_ID();
    }    // getC_Location_ID

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
        if( e.getSource() == mDelete ) {
            m_value = null;    // create new
        }

        //

        log.config( "actionPerformed - " + m_value );

        VLocationDialog ld = new VLocationDialog( Env.getFrame( this ),Msg.getMsg( Env.getCtx(),"Location" ),m_value );

        ld.setVisible( true );
        m_value = ld.getValue();

        //

        if( e.getSource() == mDelete ) {
            ;
        } else if( !ld.isChanged()) {
            return;
        }

        // Data Binding

        try {
            int C_Location_ID = 0;

            if( m_value != null ) {
                C_Location_ID = m_value.getC_Location_ID();
            }

            Integer ii = new Integer( C_Location_ID );

            // force Change - user does not realize that embedded object is already saved.

            fireVetoableChange( m_columnName,null,null );    // resets m_mLocation

            if( C_Location_ID != 0 ) {
                fireVetoableChange( m_columnName,null,ii );
            }

            setValue( ii );
        } catch( PropertyVetoException pve ) {
            log.log( Level.SEVERE,"VLocation.actionPerformed",pve );
        }
    }                                                        // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {
        m_text.addActionListener( listener );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {}    // setField

	public CButton getM_button() {
		return m_button;
	}

	public void setM_button(CButton m_button) {
		this.m_button = m_button;
	}
}    // VLocation


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VLocation_mouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VLocation_mouseAdapter( VLocation adaptee ) {
        this.adaptee = adaptee;
    }    // VLookup_mouseAdapter

    /** Descripción de Campos */

    private VLocation adaptee;

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
}    // VLocation_mouseAdapter



/*
 *  @(#)VLocation.java   02.07.07
 * 
 *  Fin del fichero VLocation.java
 *  
 *  Versión 2.2
 *
 */
