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



package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.PO;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ServicioOXP extends StateEngine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param processor
     * @param serverClass
     */

    public ServicioOXP( PO processor,Class serverClass ) {
        super();
        m_processor   = processor;
        m_serverClass = serverClass;
    }    // ServidorOXP

    /** Descripción de Campos */

    private ServidorOXP m_server = null;

    /** Descripción de Campos */

    private Class m_serverClass = null;

    /** Descripción de Campos */

    private PO m_processor = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ServidorOXP getOXPServer() {
        getState();

        return m_server;
    }    // getOXPServer

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getState() {
        if( isRunning()) {
            if( (m_server == null) ||!m_server.isAlive()) {
                terminate();
            }
        }

        return super.getState();
    }    // getState

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean start() {
        if( !super.start()) {
            return false;
        }

        boolean ok = false;

        try {
            m_server = ( ServidorOXP )m_serverClass.newInstance();
            m_server.setProcessor( m_processor );
            m_server.start();
            ok = true;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"start",e );
            ok = false;
        }

        if( !ok ) {
            return abort();
        }

        log.info( "start - " + ok );
        getState();

        return ok;
    }    // start

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean resume() {
        if( !super.resume()) {
            return false;
        }

        boolean ok = false;

        try {
            m_server = ( ServidorOXP )m_serverClass.newInstance();
            m_server.setProcessor( m_processor );
            m_server.start();
            ok = true;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"resume",e );
            ok = false;
        }

        if( !ok ) {
            return abort();
        }

        log.info( "resume - " + ok );
        getState();

        return ok;
    }    // resume

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean complete() {
        if( !super.complete()) {
            return false;
        }

        boolean ok = false;

        if( (m_server != null) && m_server.isAlive()) {
            try {
                m_server.interrupt();
                m_server.join();
                ok = true;
            } catch( Exception e ) {
                return abort();
            }
        }

        log.info( "complete - " + ok );

        return ok;
    }    // complete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean suspend() {
        if( !super.suspend()) {
            return false;
        }

        boolean ok = false;

        if( (m_server != null) && m_server.isAlive()) {
            try {
                m_server.interrupt();
                m_server.join();
                ok = true;
            } catch( Exception e ) {
                return abort();
            }
        }

        log.info( "suspend - " + ok );

        return ok;
    }    // suspend

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean abort()    // raises CannotStop, NotRunning
    {
        if( super.abort()) {
            if( (m_server != null) && m_server.isAlive()) {
                try {
                    m_server.interrupt();
                } catch( Exception e ) {
                }
            }

            log.info( "abort - done" );

            return true;
        }

        return false;
    }    // abort

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean terminate() {
        if( super.terminate()) {
            if( (m_server != null) && m_server.isAlive()) {
                try {
                    m_server.interrupt();
                } catch( Exception e ) {
                }
            }

            log.info( "terminate - done" );

            return true;
        }

        return false;
    }    // terminate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "ServicioOXP[" );

        sb.append( getStateInfo()).append( " - " ).append( m_server );
        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // ServicioOXP



/*
 *  @(#)ServicioOXP.java   25.03.06
 * 
 *  Fin del fichero ServicioOXP.java
 *  
 *  Versión 2.2
 *
 */
