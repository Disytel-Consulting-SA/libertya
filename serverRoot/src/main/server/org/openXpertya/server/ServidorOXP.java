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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAcctProcessor;
import org.openXpertya.model.MAlertProcessor;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MRequestProcessor;
import org.openXpertya.model.MScheduler;
import org.openXpertya.model.MSystem;
import org.openXpertya.model.ProcesadorLogOXP;
import org.openXpertya.model.ProcesadorOXP;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.wf.MWorkflowProcessor;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class ServidorOXP extends Thread {

    /**
     * Descripción de Método
     *
     *
     * @param model
     *
     * @return
     */

    public static ServidorOXP create( ProcesadorOXP model ) {
        if( model instanceof MRequestProcessor ) {
            return new RequestProcessor(( MRequestProcessor )model );
        }

        if( model instanceof MWorkflowProcessor ) {
            return new WorkflowProcessor(( MWorkflowProcessor )model );
        }

        if( model instanceof MAcctProcessor ) {
            return new AcctProcessor(( MAcctProcessor )model );
        }

        if( model instanceof MAlertProcessor ) {
            return new AlertProcessor(( MAlertProcessor )model );
        }

        if( model instanceof MScheduler ) {
            return new Scheduler(( MScheduler )model );
        }

        //

        throw new IllegalArgumentException( "Unknown Processor" );
    }    // create

    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     * @param initialNap
     */

    protected ServidorOXP( ProcesadorOXP model,int initialNap ) {
        super( GrupoServidorOXP.get(),null,model.getName(),0 );
        p_model = model;
        m_ctx   = new Properties( model.getCtx());

        if( p_system == null ) {
            p_system = MSystem.get( m_ctx );
        }

        p_client = MClient.get( m_ctx );
        Env.setContext( m_ctx,"#AD_Client_ID",p_client.getAD_Client_ID());
        m_initialNap = initialNap;

        // log.info(model.getName() + " - " + getThreadGroup());

    }    // ServerBase

    /** Descripción de Campos */

    protected ProcesadorOXP p_model;

    /** Descripción de Campos */

    private int m_initialNap = 0;

    /** Descripción de Campos */

    private long m_sleepMS = 600000;

    /** Descripción de Campos */

    private volatile boolean m_sleeping = false;

    /** Descripción de Campos */

    private long m_start = 0;

    /** Descripción de Campos */

    protected int p_runCount = 0;

    /** Descripción de Campos */

    protected long p_startWork = 0;

    /** Descripción de Campos */

    private long m_runLastMS = 0;

    /** Descripción de Campos */

    private long m_runTotalMS = 0;

    /** Descripción de Campos */

    private long m_nextWork = 0;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /** Descripción de Campos */

    protected static MSystem p_system = null;

    /** Descripción de Campos */

    protected MClient p_client = null;

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

    public long getSleepMS() {
        return m_sleepMS;
    }    // getSleepMS

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean sleep() {
        if( isInterrupted()) {
            log.info( getName() + ": interrupted" );

            return false;
        }

        log.fine( getName() + ": sleeping " + TimeUtil.formatElapsed( m_sleepMS ));
        m_sleeping = true;

        try {
            sleep( m_sleepMS );
        } catch( InterruptedException e ) {
            log.info( getName() + ": interrupted" );
            m_sleeping = false;

            return false;
        }

        m_sleeping = false;

        return true;
    }    // sleep

    /**
     * Descripción de Método
     *
     */

    public void runNow() {
        log.info( getName());
        p_startWork = System.currentTimeMillis();
        doWork();

        long now = System.currentTimeMillis();

        // ---------------

        p_runCount++;
        m_runLastMS  = now - p_startWork;
        m_runTotalMS += m_runLastMS;

        //

        p_model.setDateLastRun( new Timestamp( now ));
        p_model.save();

        //

        log.fine( getName() + ": " + getStatistics());
    }    // runNow

    /**
     * Descripción de Método
     *
     */

    public void run() {
        try {
            log.fine( getName() + ": pre-nap - " + m_initialNap );
            sleep( m_initialNap * 1000 );
        } catch( InterruptedException e ) {
            log.log( Level.SEVERE,getName() + ": pre-nap interrupted",e );

            return;
        }

        m_start = System.currentTimeMillis();

        while( true ) {
            if( m_nextWork == 0 ) {
                Timestamp dateNextRun = getDateNextRun( true );

                if( dateNextRun != null ) {
                    m_nextWork = dateNextRun.getTime();
                }
            }

            long now = System.currentTimeMillis();

            if( m_nextWork > now ) {
                m_sleepMS = m_nextWork - now;

                if( !sleep()) {
                    break;
                }
            }

            if( isInterrupted()) {
                log.info( getName() + ": interrupted" );

                break;
            }

            // ---------------

            p_startWork = System.currentTimeMillis();
            doWork();
            now = System.currentTimeMillis();

            // ---------------

            p_runCount++;
            m_runLastMS  = now - p_startWork;
            m_runTotalMS += m_runLastMS;

            //

            m_sleepMS  = calculateSleep();
            m_nextWork = now + m_sleepMS;

            //

            p_model.setDateLastRun( new Timestamp( now ));
            p_model.setDateNextRun( new Timestamp( m_nextWork ));
            p_model.save();

            //

            log.fine( getName() + ": " + getStatistics());

            if( !sleep()) {
                break;
            }
        }

        m_start = 0;
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatistics() {
        return "Run #" + p_runCount + " - Last=" + TimeUtil.formatElapsed( m_runLastMS ) + " - Total=" + TimeUtil.formatElapsed( m_runTotalMS ) + " - Next " + TimeUtil.formatElapsed( m_nextWork - System.currentTimeMillis());
    }    // getStatistics

    /**
     * Descripción de Método
     *
     */

    protected abstract void doWork();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract String getServerInfo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return p_model.getServerID();
    }    // getServerID

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public Timestamp getDateNextRun( boolean requery ) {
        return p_model.getDateNextRun( requery );
    }    // getDateNextRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateLastRun() {
        return p_model.getDateLastRun();
    }    // getDateLastRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return p_model.getDescription();
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorOXP getModel() {
        return p_model;
    }    // getModel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private long calculateSleep() {
        String frequencyType = p_model.getFrequencyType();
        int    frequency     = p_model.getFrequency();

        if( frequency < 1 ) {
            frequency = 1;
        }

        //

        long typeSec = 600;    // 10 minutes

        if( frequencyType == null ) {
            typeSec = 300;    // 5 minutes
        } else if( MRequestProcessor.FREQUENCYTYPE_Minute.equals( frequencyType )) {
            typeSec = 60;
        } else if( MRequestProcessor.FREQUENCYTYPE_Hour.equals( frequencyType )) {
            typeSec = 3600;
        } else if( MRequestProcessor.FREQUENCYTYPE_Day.equals( frequencyType )) {
            typeSec = 86400;
        }

        //

        return typeSec * 1000 * frequency;    // ms
    }                                         // calculateSleep

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSleeping() {
        return m_sleeping;
    }    // isSleeping

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( getName()).append( ",Prio=" ).append( getPriority()).append( "," ).append( getThreadGroup()).append( ",Alive=" ).append( isAlive()).append( ",Sleeping=" ).append( m_sleeping ).append( ",Last=" ).append( getDateLastRun());

        if( m_sleeping ) {
            sb.append( ",Next=" ).append( getDateNextRun( false ));
        }

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSecondsAlive() {
        if( m_start == 0 ) {
            return 0;
        }

        long now = System.currentTimeMillis();
        long ms  = ( now - m_start ) / 1000;

        return( int )ms;
    }    // getSecondsAlive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStartTime() {
        if( m_start == 0 ) {
            return null;
        }

        return new Timestamp( m_start );
    }    // getStartTime

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs() {
        return p_model.getLogs();
    }    // getLogs
}    // ServidorOXP



/*
 *  @(#)ServidorOXP.java   24.03.06
 * 
 *  Fin del fichero ServidorOXP.java
 *  
 *  Versión 2.2
 *
 */
