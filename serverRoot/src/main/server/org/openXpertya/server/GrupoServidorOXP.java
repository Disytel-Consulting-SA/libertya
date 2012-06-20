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



package org.openXpertya.server;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class GrupoServidorOXP extends ThreadGroup {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static GrupoServidorOXP get() {
        if( (s_group == null) || s_group.isDestroyed()) {
            s_group = new GrupoServidorOXP();
        }

        return s_group;
    }    // get

    /** Descripción de Campos */

    private static GrupoServidorOXP s_group = null;

    /**
     * Constructor de la clase ...
     *
     */

    private GrupoServidorOXP() {
        super( "OXPServers" );
        setDaemon( true );
        setMaxPriority( Thread.MAX_PRIORITY );
        log.info( getName() + " - Parent=" + getParent());
    }    // GrupoServidorOXP

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param t
     * @param e
     */

    public void uncaughtException( Thread t,Throwable e ) {
        log.info( "uncaughtException = " + e.toString());
        super.uncaughtException( t,e );
    }    // uncaughtException

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getName();
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void dump() {
        log.fine( getName() + ( isDestroyed()
                                ?" (destroyed)"
                                :"" ));
        log.fine( "- Parent=" + getParent());

        Thread[] list = new Thread[ activeCount()];

        log.fine( "- Count=" + enumerate( list,true ));

        for( int i = 0;i < list.length;i++ ) {
            log.fine( "-- " + list[ i ] );
        }
    }    // dump
}    // GrupoServidorOXP



/*
 *  @(#)GrupoServidorOXP.java   24.03.06
 * 
 *  Fin del fichero GrupoServidorOXP.java
 *  
 *  Versión 2.2
 *
 */
