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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPassword;
import org.openXpertya.model.MField;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VPassword extends CPassword implements VEditor,KeyListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VPassword() {
        this( "Password",false,false,true,30,30,"" );
    }    // VPassword

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
     */

    public VPassword( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayLength,int fieldLength,String VFormat ) {
        super( (displayLength > VString.MAXDISPLAY_LENGTH)
               ?VString.MAXDISPLAY_LENGTH
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

        // Editable

        if( isReadOnly ||!isUpdateable ) {
            setEditable( false );
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        }

        this.addKeyListener( this );
        this.addActionListener( this );
        setForeground( CompierePLAF.getTextColor_Normal());
        setBackground( CompierePLAF.getFieldBackground_Normal());
    }    // VPassword

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_mField = null;
    }    // dispose

    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private String m_oldText;

    /** Descripción de Campos */

    private String m_VFormat;

    /** Descripción de Campos */

    private int m_fieldLength;

    /** Descripción de Campos */

    private volatile boolean m_setting = false;

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

        if( !m_setting ) {
            setText( m_oldText );
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
        return String.valueOf( getPassword());
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return String.valueOf( getPassword());
    }    // getDisplay

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
        String newText = String.valueOf( getPassword());

        m_setting = true;

        try {
            fireVetoableChange( m_columnName,m_oldText,newText );
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
        String newText = String.valueOf( getPassword());

        // Data Binding

        try {
            fireVetoableChange( m_columnName,m_oldText,newText );
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
    }    // setField
}    // VPassword



/*
 *  @(#)VPassword.java   02.07.07
 * 
 *  Fin del fichero VPassword.java
 *  
 *  Versión 2.2
 *
 */
