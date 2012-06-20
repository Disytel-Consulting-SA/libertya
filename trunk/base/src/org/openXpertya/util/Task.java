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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Task extends Thread {

    /**
     * Constructor de la clase ...
     *
     *
     * @param cmd
     */

    public Task( String cmd ) {
        m_cmd = cmd;
    }    // Task

    /** Descripción de Campos */

    private String m_cmd;

    /** Descripción de Campos */

    private Process m_child = null;

    /** Descripción de Campos */

    private StringBuffer m_out = new StringBuffer();

    /** Descripción de Campos */

    private StringBuffer m_err = new StringBuffer();

    /** Descripción de Campos */

    private InputStream m_outStream;

    /** Descripción de Campos */

    private InputStream m_errStream;

    /** Descripción de Campos */

    private OutputStream m_inStream;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Task.class );

    /** Descripción de Campos */

    private Thread m_outReader = new Thread() {
        public void run() {
            log.fine( "outReader.run" );

            try {
                int c;

                while(( c = m_outStream.read()) != -1 &&!isInterrupted()) {

                    // System.out.print((char)c);

                    m_out.append(( char )c );
                }

                m_outStream.close();
            } catch( IOException ioe ) {
                log.log( Level.SEVERE,"outReader",ioe );
            }

            log.fine( "outReader.run - done" );
        }    // run
    };    // m_outReader

    /** Descripción de Campos */

    private Thread m_errReader = new Thread() {
        public void run() {
            log.fine( "errReader.run" );

            try {
                int c;

                while(( c = m_errStream.read()) != -1 &&!isInterrupted()) {

                    // System.err.print((char)c);

                    m_err.append(( char )c );
                }

                m_errStream.close();
            } catch( IOException ioe ) {
                log.log( Level.SEVERE,"errReader",ioe );
            }

            log.fine( "errReader.run - done" );
        }    // run
    };    // m_errReader

    /**
     * Descripción de Método
     *
     */

    public void run() {
        log.config( "run" );

        try {
            m_child = Runtime.getRuntime().exec( m_cmd );

            //

            m_outStream = m_child.getInputStream();
            m_errStream = m_child.getErrorStream();
            m_inStream  = m_child.getOutputStream();

            //

            if( checkInterrupted()) {
                return;
            }

            m_outReader.start();
            m_errReader.start();

            //

            try {
                if( checkInterrupted()) {
                    return;
                }

                m_errReader.join();

                if( checkInterrupted()) {
                    return;
                }

                m_outReader.join();

                if( checkInterrupted()) {
                    return;
                }

                m_child.waitFor();
            } catch( InterruptedException ie ) {

                // log.log(Level.SEVERE,"run (ie)", ie);

            }

            // ExitValue

            try {
                if( m_child != null ) {
                    log.fine( "run - ExitValue=" + m_child.exitValue());
                }
            } catch( Exception e ) {
            }

            log.config( "run - done" );
        } catch( IOException ioe ) {
            log.log( Level.SEVERE,"run (ioe)",ioe );
        }
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean checkInterrupted() {
        if( isInterrupted()) {
            log.config( "checkInterrupted - true" );

            // interrupt child processes

            if( m_child != null ) {
                m_child.destroy();
            }

            m_child = null;

            if( (m_outReader != null) && m_outReader.isAlive()) {
                m_outReader.interrupt();
            }

            m_outReader = null;

            if( (m_errReader != null) && m_errReader.isAlive()) {
                m_errReader.interrupt();
            }

            m_errReader = null;

            // close Streams

            if( m_inStream != null ) {
                try {
                    m_inStream.close();
                } catch( Exception e ) {
                }
            }

            m_inStream = null;

            if( m_outStream != null ) {
                try {
                    m_outStream.close();
                } catch( Exception e ) {
                }
            }

            m_outStream = null;

            if( m_errStream != null ) {
                try {
                    m_errStream.close();
                } catch( Exception e ) {
                }
            }

            m_errStream = null;

            //

            return true;
        }

        return false;
    }    // checkInterrupted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public StringBuffer getOut() {
        return m_out;
    }    // getOut

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public StringBuffer getErr() {
        return m_err;
    }    // getErr

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public OutputStream getInStream() {
        return m_inStream;
    }    // getInStream
}    // Task



/*
 *  @(#)Task.java   02.07.07
 * 
 *  Fin del fichero Task.java
 *  
 *  Versión 2.2
 *
 */
