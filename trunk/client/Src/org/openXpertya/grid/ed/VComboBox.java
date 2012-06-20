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

import java.util.logging.Level;

import javax.swing.ComboBoxModel;

import org.compiere.swing.CComboBox;
import org.openXpertya.model.MLocator;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VComboBox extends CComboBox {

    /**
     * Constructor de la clase ...
     *
     */

    public VComboBox() {
        super();

//              common_init();

    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param items
     */

    public VComboBox( Object[] items ) {
        super( items );

//              common_init();

    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     */

    public VComboBox( ComboBoxModel model ) {
        super( model );

//              common_init();

    }    // VComboBox

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VComboBox.class );

    /**
     * Descripción de Método
     *
     *
     * @param key
     */

    public void setValue( Object key ) {
        if( key == null ) {
            this.setSelectedIndex( -1 );

            return;
        }

        ComboBoxModel model = getModel();
        int           size  = model.getSize();

        for( int i = 0;i < size;i++ ) {
            Object element = model.getElementAt( i );
            String ID      = null;

            if( element instanceof NamePair ) {
                ID = (( NamePair )element ).getID();
            } else if( element instanceof MLocator ) {
                ID = String.valueOf((( MLocator )element ).getM_Locator_ID());
            } else {
                log.log( Level.SEVERE,"VComboBox.setValue - Element not NamePair - " + element.getClass().toString());
            }

            if( (key == null) || (ID == null) ) {
                if( (key == null) && (ID == null) ) {
                    setSelectedIndex( i );

                    return;
                }
            } else if( ID.equals( key.toString())) {
                setSelectedIndex( i );

                return;
            }
        }

        setSelectedIndex( -1 );
        setSelectedItem( null );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param key
     */

    public void setValue( int key ) {
        setValue( String.valueOf( key ));
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        NamePair p = ( NamePair )getSelectedItem();

        if( p == null ) {
            return null;
        }

        //

        if( p instanceof KeyNamePair ) {
            if( p.getID() == null ) {    // -1 return null
                return null;
            }

            return new Integer((( KeyNamePair )p ).getID());
        }

        return p.getID();
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        if( getSelectedIndex() == -1 ) {
            return "";
        }

        //

        NamePair p = ( NamePair )getSelectedItem();

        if( p == null ) {
            return "";
        }

        return p.getName();
    }    // getDisplay
}    // VComboBox



/*
 *  @(#)VComboBox.java   02.07.07
 * 
 *  Fin del fichero VComboBox.java
 *  
 *  Versión 2.2
 *
 */
