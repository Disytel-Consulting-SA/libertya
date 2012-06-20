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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CacheMgt {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static CacheMgt get() {
        if( s_cache == null ) {
            s_cache = new CacheMgt();
        }

        return s_cache;
    }    // get

    /** Descripción de Campos */

    private static CacheMgt s_cache = null;

    /**
     * Constructor de la clase ...
     *
     */

    private CacheMgt() {}    // CacheMgt

    /** Descripción de Campos */

    private ArrayList m_instances = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_tableNames = new ArrayList();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( CacheMgt.class );

    /**
     * Descripción de Método
     *
     *
     * @param instance
     *
     * @return
     */

    public synchronized boolean register( CacheInterface instance ) {
        if( instance == null ) {
            return false;
        }

        if( instance instanceof CCache ) {
            m_tableNames.add((( CCache )instance ).getName());
        }

        return m_instances.add( instance );
    }    // register

    /**
     * Descripción de Método
     *
     *
     * @param instance
     *
     * @return
     */

    public boolean unregister( CacheInterface instance ) {
        if( instance == null ) {
            return false;
        }

        boolean found = false;

        // Could be included multiple times

        for( int i = m_instances.size() - 1;i >= 0;i-- ) {
            CacheInterface stored = ( CacheInterface )m_instances.get( i );

            if( instance.equals( stored )) {
                m_instances.remove( i );
                found = true;
            }
        }

        return found;
    }    // unregister

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int reset() {
        int counter = 0;
        int total   = 0;

        for( int i = 0;i < m_instances.size();i++ ) {
            CacheInterface stored = ( CacheInterface )m_instances.get( i );

            if( (stored != null) && (stored.size() > 0) ) {
                log.fine( stored.toString());
                total += stored.reset();
                counter++;
            }
        }

        log.info( "#" + counter + " (" + total + ")" );

        return total;
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @param tableName
     *
     * @return
     */

    public int reset( String tableName ) {
        return reset( tableName,0 );
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @param tableName
     * @param Record_ID
     *
     * @return
     */

    public int reset( String tableName,int Record_ID ) {
        if( tableName == null ) {
            return reset();
        }

        // if (tableName.endsWith("Set"))
        // tableName = tableName.substring(0, tableName.length()-3);

        if( !m_tableNames.contains( tableName )) {
            return 0;
        }

        //

        int counter = 0;
        int total   = 0;

        for( int i = 0;i < m_instances.size();i++ ) {
            CacheInterface stored = ( CacheInterface )m_instances.get( i );

            if( (stored != null) && (stored instanceof CCache) ) {
                CCache cc = ( CCache )stored;

                if( cc.getName().startsWith( tableName ))    // reset lines/dependent too
                {

                    // if (Record_ID == 0)

                    {
                        log.fine( "(all) - " + stored );
                        total += stored.reset();
                        counter++;
                    }
                }
            }
        }

        log.info( tableName + ": #" + counter + " (" + total + ")" );

        // Update Server

        if( DB.isRemoteObjects()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    int serverTotal = server.cacheReset( tableName,0 );

                    if( CLogMgt.isLevelFinest()) {
                        log.fine( "Server => " + serverTotal );
                    }
                }
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"AppsServer error",ex );
            }
        }

        return total;
    }    // reset
}    // CCache



/*
 *  @(#)CacheMgt.java   25.03.06
 * 
 *  Fin del fichero CacheMgt.java
 *  
 *  Versión 2.2
 *
 */
