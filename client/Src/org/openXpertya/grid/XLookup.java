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



package org.openXpertya.grid;

import java.util.ArrayList;
import java.util.Collections;

import org.openXpertya.model.Lookup;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class XLookup extends Lookup {

    /**
     * Constructor de la clase ...
     *
     *
     * @param keyColumn
     */

    public XLookup( String keyColumn ) {
        super( DisplayType.TableDir,0 );
        m_keyColumn = keyColumn;
    }    // XLookup

    /** Descripción de Campos */

    private String m_keyColumn;

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public String getDisplay( Object key ) {

        // linear seatch in m_data

        for( int i = 0;i < p_data.size();i++ ) {
            Object oo = p_data.get( i );

            if( (oo != null) && (oo instanceof NamePair) ) {
                NamePair pp = ( NamePair )oo;

                if( pp.getID().equals( key )) {
                    return pp.getName();
                }
            }
        }

        return "<" + key + ">";
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public boolean containsKey( Object key ) {

        // linear seatch in p_data

        for( int i = 0;i < p_data.size();i++ ) {
            Object oo = p_data.get( i );

            if( (oo != null) && (oo instanceof NamePair) ) {
                NamePair pp = ( NamePair )oo;

                if( pp.getID().equals( key )) {
                    return true;
                }
            }
        }

        return false;
    }    // containsKey

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public NamePair get( Object key ) {

        // linear seatch in m_data

        for( int i = 0;i < p_data.size();i++ ) {
            Object oo = p_data.get( i );

            if( (oo != null) && (oo instanceof NamePair) ) {
                NamePair pp = ( NamePair )oo;

                if( pp.getID().equals( key )) {
                    return pp;
                }
            }
        }

        return null;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     * @param onlyValidated
     * @param onlyActive
     * @param temporary
     *
     * @return
     */

    public ArrayList getData( boolean mandatory,boolean onlyValidated,boolean onlyActive,boolean temporary ) {
        ArrayList list = new ArrayList( p_data );

        // Sort Data

        if( m_keyColumn.endsWith( "_ID" )) {
            KeyNamePair p = new KeyNamePair( -1,"" );

            if( !mandatory ) {
                list.add( p );
            }

            Collections.sort( list,p );
        } else {
            ValueNamePair p = new ValueNamePair( null,"" );

            if( !mandatory ) {
                list.add( p );
            }

            Collections.sort( list,p );
        }

        return list;
    }    // getArray

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int refresh() {
        return p_data.size();
    }    // refresh

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        return m_keyColumn;
    }    // getColumnName
}    // XLookup



/*
 *  @(#)XLookup.java   02.07.07
 * 
 *  Fin del fichero XLookup.java
 *  
 *  Versión 2.2
 *
 */
