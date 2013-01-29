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



package org.openXpertya.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MultiMap<K,V> implements Map<K,V>,Serializable {

    /**
     * Constructor de la clase ...
     *
     */

    public MultiMap() {
        this( 10 );
    }    // MultiMap

    /**
     * Constructor de la clase ...
     *
     *
     * @param initialCapacity
     */

    public MultiMap( int initialCapacity ) {
        m_keys   = new ArrayList( initialCapacity );
        m_values = new ArrayList( initialCapacity );
    }    // MultiMap

    /** Descripción de Campos */

    private ArrayList m_keys = null;

    /** Descripción de Campos */

    private ArrayList m_values = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int size() {
        return m_keys.size();
    }    // size

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEmpty() {
        return( m_keys.size() == 0 );
    }    // isEmpty

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public boolean containsKey( Object key ) {
        return m_keys.contains( key );
    }    // containsKey

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    public boolean containsValue( Object value ) {
        return m_values.contains( value );
    }    // containsKey

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public V get( Object key ) {
        return (V)getValues( key );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public ArrayList getValues( Object key ) {
        ArrayList list = new ArrayList();

        // We don't have it

        if( !m_keys.contains( key )) {
            return list;
        }

        // go through keys

        int size = m_keys.size();

        for( int i = 0;i < size;i++ ) {
            if( m_keys.get( i ).equals( key )) {
                if( !list.contains( m_values.get( i ))) {
                    list.add( m_values.get( i ));
                }
            }
        }

        return list;
    }    // getValues

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    public ArrayList getKeys( Object value ) {
        ArrayList list = new ArrayList();

        // We don't have it

        if( !m_values.contains( value )) {
            return list;
        }

        // go through keys

        int size = m_values.size();

        for( int i = 0;i < size;i++ ) {
            if( m_values.get( i ).equals( value )) {
                if( !list.contains( m_keys.get( i ))) {
                    list.add( m_keys.get( i ));
                }
            }
        }

        return list;
    }    // getKeys

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     *
     * @return
     */

    public Object put( Object key,Object value ) {
        m_keys.add( key );
        m_values.add( value );

        return null;
    }    // put

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public V remove( Object key ) {
        throw new java.lang.UnsupportedOperationException( "Method remove() not implemented." );
    }    // remove

    /**
     * Descripción de Método
     *
     *
     * @param t
     */

    public void putAll( Map t ) {
        throw new java.lang.UnsupportedOperationException( "Method putAll() not implemented." );
    }    // putAll

    /**
     * Descripción de Método
     *
     */

    public void clear() {
        m_keys.clear();
        m_values.clear();
    }    // clear

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Set keySet() {
        HashSet keys = new HashSet( m_keys );

        return keys;
    }    // keySet

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Collection values() {
        return m_values;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Set entrySet() {
        throw new java.lang.UnsupportedOperationException( "Method entrySet() not implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */

    public boolean equals( Object o ) {
        throw new java.lang.UnsupportedOperationException( "Method equals() not implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MultiMap #" + m_keys.size();
    }

    /**
     * Descripción de Método
     *
     */

    public void printToLog() {
        CLogger log = CLogger.getCLogger( getClass());

        log.fine( "MultiMap.printToLog" );

        int size = m_keys.size();

        for( int i = 0;i < size;i++ ) {
            Object k = m_keys.get( i );
            Object v = m_values.get( i );

            log.finest( (k == null)
                        ?"null"
                        :(k.toString() + "=" + v == null)
                         ?"null"
                         :v.toString());
        }
    }    // printToLog
}    // MultiMap



/*
 *  @(#)MultiMap.java   02.07.07
 * 
 *  Fin del fichero MultiMap.java
 *  
 *  Versión 2.2
 *
 */
