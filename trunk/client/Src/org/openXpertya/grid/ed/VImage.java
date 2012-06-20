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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.JButton;

import org.openXpertya.model.MImage;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VImage extends JButton implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param WindowNo
     */

    public VImage( int WindowNo ) {
        super( "-/-" );
        m_WindowNo = WindowNo;
        super.addActionListener( this );
    }    // VImage

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_mImage = null;
    }    // dispose

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private MImage m_mImage = null;

    /** Descripción de Campos */

    private boolean m_mandatory = false;

    /** Descripción de Campos */

    private static final String COLUMN_NAME = "AD_Image_ID";

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VImage.class );

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        log.config( "=" + value );

        int newValue = 0;

        if( (value == null) && (value instanceof Integer) ) {
            newValue = (( Integer )value ).intValue();
        }

        // Get/Create Image

        if( (m_mImage == null) || (newValue != m_mImage.getID())) {
            m_mImage = new MImage( Env.getCtx(),newValue,null );
        }

        //

        log.fine( m_mImage.toString());

        // super.setIcon(m_mImage.getImage());

        super.setToolTipText( m_mImage.getName());
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        if( m_mImage.getID() == 0 ) {
            return null;
        }

        return new Integer( m_mImage.getID());
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return m_mImage.getName();
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param rw
     */

    public void setReadWrite( boolean rw ) {
        if( isEnabled() != rw ) {
            setEnabled( rw );
        }
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return super.isEnabled();
    }    // getReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_mandatory = mandatory;
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
     * @param color
     */

    public void setBackground( Color color ) {}    // setBackground

    /**
     * Descripción de Método
     *
     */

    public void setBackground() {}    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {}    // setBackground

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
        VImageDialog vid = new VImageDialog( Env.getWindow( m_WindowNo ),m_mImage );

        vid.setVisible(true);    // m_mImage is updated

        //

        Integer value = null;

        if( m_mImage.getID() != 0 ) {
            value = new Integer( m_mImage.getID());
        }

        try {
            fireVetoableChange( COLUMN_NAME,null,value );
        } catch( PropertyVetoException pve ) {
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {}    // setField
}    // VImage



/*
 *  @(#)VImage.java   02.07.07
 * 
 *  Fin del fichero VImage.java
 *  
 *  Versión 2.2
 *
 */
