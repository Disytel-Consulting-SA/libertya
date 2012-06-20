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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MAcctProcessor;
import org.openXpertya.model.MAlertProcessor;
import org.openXpertya.model.MRequestProcessor;
import org.openXpertya.model.MScheduler;
import org.openXpertya.model.MSession;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWorkflowProcessor;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ServidorMgrOXP {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static ServidorMgrOXP get() {
        if( m_serverMgr == null ) {

            // for faster subsequent calls

            m_serverMgr = new ServidorMgrOXP();
            m_serverMgr.startServers();
            m_serverMgr.log.info( m_serverMgr.toString());
        }

        return m_serverMgr;
    }    // get

    /** Descripción de Campos */

    private static ServidorMgrOXP m_serverMgr = null;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Constructor de la clase ...
     *
     */

    private ServidorMgrOXP() {
        super();
        startEnvironment();

        // m_serverMgr.startServers();

    }    // ServidorMgrOXP

    /** Descripción de Campos */

    private ArrayList m_servers = new ArrayList();

    /** Descripción de Campos */

    private Properties m_ctx = Env.getCtx();

    /** Descripción de Campos */

    private Timestamp m_start = new Timestamp( System.currentTimeMillis());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean startEnvironment() {
        OpenXpertya.startup( false );
        log.info( "" );

        // Set Session

        MSession session = MSession.get( getCtx(),true );

        session.setWebStoreSession( false );
        session.setWebSession( "Server" );
        session.save();

        //

        return true;
    }    // startEnvironment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean startServers() {
        log.info( "" );

        int noServers = 0;

        // Accounting

        MAcctProcessor[] acctModels = MAcctProcessor.getActive( m_ctx );

        for( int i = 0;i < acctModels.length;i++ ) {
            MAcctProcessor pModel = acctModels[ i ];
            ServidorOXP    server = ServidorOXP.create( pModel );

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            m_servers.add( server );
        }

        // Request

        MRequestProcessor[] requestModels = MRequestProcessor.getActive( m_ctx );

        for( int i = 0;i < requestModels.length;i++ ) {
            MRequestProcessor pModel = requestModels[ i ];
            ServidorOXP       server = ServidorOXP.create( pModel );

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            m_servers.add( server );
        }

        // Workflow

        MWorkflowProcessor[] workflowModels = MWorkflowProcessor.getActive( m_ctx );

        for( int i = 0;i < workflowModels.length;i++ ) {
            MWorkflowProcessor pModel = workflowModels[ i ];
            ServidorOXP        server = ServidorOXP.create( pModel );

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            m_servers.add( server );
        }

        // Alert

        MAlertProcessor[] alertModels = MAlertProcessor.getActive( m_ctx );

        for( int i = 0;i < alertModels.length;i++ ) {
            MAlertProcessor pModel = alertModels[ i ];
            ServidorOXP     server = ServidorOXP.create( pModel );

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            m_servers.add( server );
        }

        // Scheduler

        MScheduler[] schedulerModels = MScheduler.getActive( m_ctx );

        for( int i = 0;i < schedulerModels.length;i++ ) {
            MScheduler  pModel = schedulerModels[ i ];
            ServidorOXP server = ServidorOXP.create( pModel );

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            m_servers.add( server );
        }

        log.fine( "#" + noServers );

        return startAll();
    }    // startEnvironment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx() {
        return m_ctx;
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean startAll() {
        log.info( "" );

        ServidorOXP[] servers = getInActive();

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            try {
                if( server.isAlive()) {
                    continue;
                }

                // Wait until dead

                if( server.isInterrupted()) {
                    int maxWait = 10;    // 10 iterations = 1 sec

                    while( server.isAlive()) {
                        if( maxWait-- == 0 ) {
                            log.severe( "Wait timeout for interruped " + server );

                            break;
                        }

                        try {
                            Thread.sleep( 100 );    // 1/10 sec
                        } catch( InterruptedException e ) {
                            log.log( Level.SEVERE,"While sleeping",e );
                        }
                    }
                }

                // Do start

                if( !server.isAlive()) {

                    // replace

                    server = ServidorOXP.create( server.getModel());

                    if( server == null ) {
                        m_servers.remove( i );
                    } else {
                        m_servers.set( i,server );
                    }

                    server.start();
                    server.setPriority( Thread.NORM_PRIORITY - 2 );
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"Server: " + server,e );
            }
        }    // for all servers

        // Final Check

        int noRunning = 0;
        int noStopped = 0;

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            try {
                if( server.isAlive()) {
                    log.info( "Alive: " + server );
                    noRunning++;
                } else {
                    log.warning( "Dead: " + server );
                    noStopped++;
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(checking) - " + server,e );
                noStopped++;
            }
        }

        log.fine( "Running=" + noRunning + ", Stopped=" + noStopped );
        GrupoServidorOXP.get().dump();

        return noStopped == 0;
    }    // startAll

    /**
     * Descripción de Método
     *
     *
     * @param serverID
     *
     * @return
     */

    public boolean start( String serverID ) {
        ServidorOXP server = getServer( serverID );

        if( server == null ) {
            return false;
        }

        if( server.isAlive()) {
            return true;
        }

        try {

            // replace

            int index = m_servers.indexOf( server );

            server = ServidorOXP.create( server.getModel());

            if( server == null ) {
                m_servers.remove( index );
            } else {
                m_servers.set( index,server );
            }

            server.start();
            server.setPriority( Thread.NORM_PRIORITY - 2 );
            Thread.yield();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Server=" + serverID,e );

            return false;
        }

        log.info( server.toString());
        GrupoServidorOXP.get().dump();

        if( server == null ) {
            return false;
        }

        return server.isAlive();
    }    // startIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean stopAll() {
        log.info( "" );

        ServidorOXP[] servers = getActive();

        // Interrupt

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            try {
                if( server.isAlive() &&!server.isInterrupted()) {
                    server.setPriority( Thread.MAX_PRIORITY - 1 );
                    server.interrupt();
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(interrupting) - " + server,e );
            }
        }    // for all servers

        Thread.yield();

        // Wait for death

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            try {
                int maxWait = 10;           // 10 iterations = 1 sec

                while( server.isAlive()) {
                    if( maxWait-- == 0 ) {
                        log.severe( "Wait timeout for interruped " + server );

                        break;
                    }

                    Thread.sleep( 100 );    // 1/10
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(waiting) - " + server,e );
            }
        }                                   // for all servers

        // Final Check

        int noRunning = 0;
        int noStopped = 0;

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            try {
                if( server.isAlive()) {
                    log.warning( "Alive: " + server );
                    noRunning++;
                } else {
                    log.info( "Stopped: " + server );
                    noStopped++;
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(checking) - " + server,e );
                noRunning++;
            }
        }

        log.fine( "Running=" + noRunning + ", Stopped=" + noStopped );
        GrupoServidorOXP.get().dump();

        return noRunning == 0;
    }    // stopAll

    /**
     * Descripción de Método
     *
     *
     * @param serverID
     *
     * @return
     */

    public boolean stop( String serverID ) {
        ServidorOXP server = getServer( serverID );

        if( server == null ) {
            return false;
        }

        if( !server.isAlive()) {
            return true;
        }

        try {
            server.interrupt();
            Thread.sleep( 10 );    // 1/100 sec
        } catch( Exception e ) {
            log.log( Level.SEVERE,"stop",e );

            return false;
        }

        log.info( server.toString());
        GrupoServidorOXP.get().dump();

        return !server.isAlive();
    }    // stop

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "" );
        stopAll();
        m_servers.clear();
    }    // destroy

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected ServidorOXP[] getActive() {
        ArrayList list = new ArrayList();

        for( int i = 0;i < m_servers.size();i++ ) {
            ServidorOXP server = ( ServidorOXP )m_servers.get( i );

            if( (server != null) && server.isAlive() &&!server.isInterrupted()) {
                list.add( server );
            }
        }

        ServidorOXP[] retValue = new ServidorOXP[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected ServidorOXP[] getInActive() {
        ArrayList list = new ArrayList();

        for( int i = 0;i < m_servers.size();i++ ) {
            ServidorOXP server = ( ServidorOXP )m_servers.get( i );

            if( (server != null) && ( !server.isAlive() ||!server.isInterrupted())) {
                list.add( server );
            }
        }

        ServidorOXP[] retValue = new ServidorOXP[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getInActive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ServidorOXP[] getAll() {
        ServidorOXP[] retValue = new ServidorOXP[ m_servers.size()];

        m_servers.toArray( retValue );

        return retValue;
    }    // getAll

    /**
     * Descripción de Método
     *
     *
     * @param serverID
     *
     * @return
     */

    public ServidorOXP getServer( String serverID ) {
        if( serverID == null ) {
            return null;
        }

        for( int i = 0;i < m_servers.size();i++ ) {
            ServidorOXP server = ( ServidorOXP )m_servers.get( i );

            if( serverID.equals( server.getServerID())) {
                return server;
            }
        }

        return null;
    }    // getServer

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "ServidorMgrOXP[" );

        sb.append( "Servers=" ).append( m_servers.size()).append( ",ContextSize=" ).append( m_ctx.size()).append( ",Started=" ).append( m_start ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return "$Revision: 1.9 $";
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerCount() {
        int noRunning = 0;
        int noStopped = 0;

        for( int i = 0;i < m_servers.size();i++ ) {
            ServidorOXP server = ( ServidorOXP )m_servers.get( i );

            if( server.isAlive()) {
                noRunning++;
            } else {
                noStopped++;
            }
        }

        String info = String.valueOf( m_servers.size()) + " - Running=" + noRunning + " - Stopped=" + noStopped;

        return info;
    }    // getServerCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStartTime() {
        return m_start;
    }    // getStartTime
}    // ServidorMgrOXP



/*
 *  @(#)ServidorMgrOXP.java   24.03.06
 * 
 *  Fin del fichero ServidorMgrOXP.java
 *  
 *  Versión 2.2
 *
 */
