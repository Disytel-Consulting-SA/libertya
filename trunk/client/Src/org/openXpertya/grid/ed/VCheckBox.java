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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.compiere.swing.CCheckBox;
import org.openXpertya.model.MField;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VCheckBox extends CCheckBox implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VCheckBox() {
        this( "",false,false,true,"",null,false );
    }    // VCheckBox

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param title
     * @param description
     * @param tableEditor
     */

    public VCheckBox( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,String title,String description,boolean tableEditor ) {
        super();
        super.setName( columnName );
        m_columnName = columnName;
        setMandatory( mandatory );

        //

        if( isReadOnly ||!isUpdateable ) {
            setEditable( false );
        } else {
            setEditable( true );
        }

        // Normal

        if( !tableEditor ) {
            setText( title );

            if( (description != null) && (description.length() > 0) ) {
                setToolTipText( description );
            }
        } else {
            setHorizontalAlignment( JLabel.CENTER );
        }

        //

        this.addActionListener( this );
    }    // VCheckBox

    /**
     * Descripción de Método
     *
     */

    public void dispose() {}    // dispose

    /** Descripción de Campos */

    private String m_columnName;
    
    private MField field;

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setEditable( boolean value ) {
        super.setReadWrite( value );
    }    // setEditable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEditable() {
        return super.isReadWrite();
    }    // isEditable

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        boolean sel = false;

        if( value != null ) {
            if( value instanceof Boolean ) {
                sel = (( Boolean )value ).booleanValue();
            } else {
                sel = "Y".equals( value );
            }
        }

        setSelected( sel );
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
        return new Boolean( isSelected());
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        String value = isSelected()
                       ?"Y"
                       :"N";

		return field != null && field.isExportRealValue() ? value : Msg.translate(Env.getCtx(), value);
    }    // getDisplay

    /**
     * Descripción de Método
     *
     */

    public void setBackground() {}    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // ADebug.info("VCheckBox.actionPerformed");

        try {
            fireVetoableChange( m_columnName,null,getValue());
        } catch( PropertyVetoException pve ) {
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {
    	field = mField;
    }    // setField
    
    public void addAction(String actionName, KeyStroke keyStroke, Action action) {
    	getInputMap().put(keyStroke, actionName);
    	getActionMap().put(actionName, action);
    }
}    // VCheckBox



/*
 *  @(#)VCheckBox.java   02.07.07
 * 
 *  Fin del fichero VCheckBox.java
 *  
 *  Versión 2.2
 *
 */
