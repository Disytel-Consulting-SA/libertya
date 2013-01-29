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



package org.openXpertya.util;

import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CCache<K,V> extends HashMap<K,V> implements CacheInterface, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param initialCapacity
     */

    public CCache( String name,int initialCapacity ) {
        this( name,initialCapacity,120 );
    }    // CCache

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param initialCapacity
     * @param expireMinutes
     */

    public CCache( String name,int initialCapacity,int expireMinutes ) {
        super( initialCapacity );
        m_name = name;
        setExpireMinutes( expireMinutes );
        CacheMgt.get().register( this );
    }    // CCache

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private int m_expire = 0;

    /** Descripción de Campos */

    private volatile long m_timeExp = 0;

    /** Descripción de Campos */

    private boolean m_justReset = true;

    /** Descripción de Campos */

    private VetoableChangeSupport m_changeSupport = null;

    /** Descripción de Campos */

    private static String PROPERTYNAME = "cache";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param expireMinutes
     */

    public void setExpireMinutes( int expireMinutes ) {
        if( expireMinutes > 0 ) {
            m_expire = expireMinutes;

            long addMS = 60000L * m_expire;

            m_timeExp = System.currentTimeMillis() + addMS;
        } else {
            m_expire  = 0;
            m_timeExp = 0;
        }
    }    // setExpireMinutes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getExpireMinutes() {
        return m_expire;
    }    // getExpireMinutes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReset() {
        return m_justReset;
    }    // isReset

    /**
     * Descripción de Método
     *
     */

    public void setUsed() {
        m_justReset = false;
    }    // setUsed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int reset() {
        int no = super.size();

        clear();

        return no;
    }    // reset

    /**
     * Descripción de Método
     *
     */

    private void expire() {
        if( (m_expire != 0) && (m_timeExp < System.currentTimeMillis())) {

            // System.out.println ("------------ Expired: " + getName() + " --------------------");

            reset();
        }
    }    // expire

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "CCache[" + m_name + ",Exp=" + getExpireMinutes() + ", #" + super.size() + "]";
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void clear() {
        if( m_changeSupport != null ) {
            try {
                m_changeSupport.fireVetoableChange( PROPERTYNAME,super.size(),0 );
            } catch( Exception e ) {
                System.out.println( "CCache.clear - " + e );

                return;
            }
        }

        // Clear

        super.clear();

        if( m_expire != 0 ) {
            long addMS = 60000L * m_expire;

            m_timeExp = System.currentTimeMillis() + addMS;
        }

        m_justReset = true;
    }    // clear

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public boolean containsKey( Object key ) {
        expire();

        return super.containsKey( key );
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
        expire();

        return super.containsValue( value );
    }    // containsValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Set entrySet() {
        expire();

        return super.entrySet();
    }    // entrySet

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public V get( Object key ) {
        expire();

        return super.get( key );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     *
     * @return
     */

    public V put(K key, V value) {
        expire();
        m_justReset = false;

        return super.put( key,value );
    }    // put

    /**
     * Descripción de Método
     *
     *
     * @param m
     */

    public void putAll( Map m ) {
        expire();
        m_justReset = false;
        super.putAll( m );
    }    // putAll

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEmpty() {
        expire();

        return super.isEmpty();
    }    // isEmpty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Set keySet() {
        expire();

        return super.keySet();
    }    // keySet

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int size() {
        expire();

        return super.size();
    }    // size

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Collection values() {
        expire();

        return super.values();
    }    // values

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addVetoableChangeListener( VetoableChangeListener listener ) {
        if( m_changeSupport == null ) {
            m_changeSupport = new VetoableChangeSupport( this );
        }

        if( listener != null ) {
            m_changeSupport.addVetoableChangeListener( listener );
        }
    }    // addVetoableChangeListener

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void removeVetoableChangeListener( VetoableChangeListener listener ) {
        if( (m_changeSupport != null) && (listener != null) ) {
            m_changeSupport.removeVetoableChangeListener( listener );
        }
    }    // removeVetoableChangeListener
}    // CCache



/*
 *  @(#)CCache.java   25.03.06
 * 
 *  Fin del fichero CCache.java
 *  
 *  Versión 2.2
 *
 */
