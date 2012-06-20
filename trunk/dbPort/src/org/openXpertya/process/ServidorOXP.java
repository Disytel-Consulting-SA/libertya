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

import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class ServidorOXP extends Thread {

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     */

    public ServidorOXP( String name ) {
        super( s_threadGroup,name );
    }    // ServidorOXP

    /** Descripción de Campos */

    private static ThreadGroup s_threadGroup = new ThreadGroup( "ServidorOXP" );

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private volatile boolean m_working = false;

    /** Descripción de Campos */

    private int m_count = 0;

    /** Descripción de Campos */

    private int m_pollCount = 0;

    /** Descripción de Campos */

    private volatile int m_time = 0;

    /** Descripción de Campos */

    private volatile long m_start = 0;

    /** Descripción de Campos */

    private volatile long m_lastStart = 0;

    /** Descripción de Campos */

    private int m_sleepSeconds = 10;

    /** Descripción de Campos */

    protected PO p_processor = null;

    /** Descripción de Campos */

    private boolean m_canContinue = true;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorking() {
        return m_working;
    }    // isWorking

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPollCount() {
        return m_pollCount;
    }    // getPollCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWorkCount() {
        return m_count;
    }    // getWorkCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWorkTime() {
        return m_time;
    }    // getWorkTime

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStart() {
        if( m_start == 0 ) {
            return null;
        }

        return new Timestamp( m_start );
    }    // getStart

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getLastStart() {
        if( m_lastStart == 0 ) {
            return null;
        }

        return new Timestamp( m_lastStart );
    }    // getLastStart

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSleepSeconds() {
        return m_sleepSeconds;
    }    // getSleepSeconds

    /**
     * Descripción de Método
     *
     *
     * @param sleepSeconds
     */

    public void setSleepSeconds( int sleepSeconds ) {
        m_sleepSeconds = sleepSeconds;
    }    // setSleepSeconds

    /**
     * Descripción de Método
     *
     *
     * @param processor
     */

    public void setProcessor( PO processor ) {
        p_processor = processor;
        setName( getProcessorName());
    }    // setProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatistics() {
        StringBuffer sb = new StringBuffer();

        sb.append( "Alive=" ).append( isAlive()).append( ", Start=" ).append( getStart()).append( ", WorkCount=" ).append( getWorkCount()).append( ", WorkTime=" ).append( getWorkTime()).append( ", PollCount=" ).append( getPollCount()).append( ", Working=" ).append( isWorking()).append( ", Last=" ).append( getLastStart())

        // .append(", SleepSec=").append(getSleepSeconds())

        ;

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "ServidorOXP[" );

        sb.append( getStatistics()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public final void run() {
        if( m_start == 0 ) {
            m_start = System.currentTimeMillis();
        }

        m_canContinue = true;

        while( m_canContinue ) {
            if( isInterrupted()) {
                return;
            }

            m_lastStart = System.currentTimeMillis();
            m_working   = true;

            try {
                m_pollCount++;

                if( canDoWork()) {
                    m_canContinue = doWork();
                    m_count++;
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"run",e );
            }

            m_working = false;

            long end = System.currentTimeMillis();

            m_time += ( end - m_lastStart );

            if( isInterrupted()) {
                return;
            }

            try {
                log.fine( "sleeping ... " + m_sleepSeconds );
                sleep( m_sleepSeconds * 1000 );
            } catch( InterruptedException e1 ) {
                log.warning( "run - " + e1.getLocalizedMessage());

                return;
            }
        }    // while
    }        // run

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract String getProcessorName();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean canDoWork();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean doWork();
}    // ServidorOXP



/*
 *  @(#)ServidorOXP.java   25.03.06
 * 
 *  Fin del fichero ServidorOXP.java
 *  
 *  Versión 2.2
 *
 */
