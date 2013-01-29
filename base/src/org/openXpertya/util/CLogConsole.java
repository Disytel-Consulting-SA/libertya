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

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CLogConsole extends Handler {

    /**
     * Descripción de Método
     *
     *
     * @param create
     *
     * @return
     */

    public static CLogConsole get( boolean create ) {
        if( (s_console == null) && create ) {
            s_console = new CLogConsole();
        }

        return s_console;
    }    // get

    /** Descripción de Campos */

    private static CLogConsole s_console = null;

    /**
     * Constructor de la clase ...
     *
     */

    public CLogConsole() {
        if( s_console == null ) {
            s_console = this;
        } else {
            reportError( "Console Handler exists already",new IllegalStateException( "Existing Handler" ),ErrorManager.GENERIC_FAILURE );
        }

        initialize();
    }    // CLogConsole

    /** Descripción de Campos */

    private boolean m_doneHeader = false;

    /** Descripción de Campos */

    private PrintWriter m_writerOut = null;

    /** Descripción de Campos */

    private PrintWriter m_writerErr = null;

    /**
     * Descripción de Método
     *
     */

    private void initialize() {

        // System.out.println("CLogConsole.initialize");
        // Set Writers

        String encoding = getEncoding();

        if( encoding != null ) {
            try {
                m_writerOut = new PrintWriter( new OutputStreamWriter( System.out,encoding ));
                m_writerErr = new PrintWriter( new OutputStreamWriter( System.err,encoding ));
            } catch( UnsupportedEncodingException ex ) {
                reportError( "Opening encoded Writers",ex,ErrorManager.OPEN_FAILURE );
            }
        }

        if( m_writerOut == null ) {
            m_writerOut = new PrintWriter( System.out );
        }

        if( m_writerErr == null ) {
            m_writerErr = new PrintWriter( System.err );
        }

        // Foratting

        setFormatter( CLogFormatter.get());

        // Default Level

        setLevel( Level.INFO );

        // Filter

        setFilter( CLogFilter.get());

        //

    }    // initialize

    /**
     * Descripción de Método
     *
     *
     * @param encoding
     *
     * @throws SecurityException
     * @throws java.io.UnsupportedEncodingException
     */

    public void setEncoding( String encoding ) throws SecurityException,java.io.UnsupportedEncodingException {
        super.setEncoding( encoding );

        // Replace the current writer with a writer for the new encoding.

        flush();
        initialize();
    }    // setEncoding

    /**
     * Descripción de Método
     *
     *
     * @param newLevel
     *
     * @throws SecurityException
     */

    public synchronized void setLevel( Level newLevel ) throws SecurityException {
        if( newLevel == null ) {
            return;
        }

        super.setLevel( newLevel );

        boolean enableJDBC = newLevel == Level.FINEST;

        if( enableJDBC ) {
            DriverManager.setLogWriter( m_writerOut );    // lists Statements
        } else {
            DriverManager.setLogWriter( null );
        }
    }                                                     // setLevel

    /**
     * Descripción de Método
     *
     *
     * @param record
     */

    public void publish( LogRecord record ) {
        if( !isLoggable( record ) || (m_writerOut == null) ) {
            return;
        }

        // Format

        String msg = null;

        try {
            msg = getFormatter().format( record );
        } catch( Exception ex ) {
            reportError( "formatting",ex,ErrorManager.FORMAT_FAILURE );

            return;
        }

        // Output

        try {
            if( !m_doneHeader ) {
                m_writerOut.write( getFormatter().getHead( this ));
                m_doneHeader = true;
            }

            if( (record.getLevel() == Level.SEVERE) || (record.getLevel() == Level.WARNING) ) {
                flush();
                m_writerErr.write( msg );
                flush();
            } else {
                m_writerOut.write( msg );
                m_writerOut.flush();
            }
        } catch( Exception ex ) {
            reportError( "writing",ex,ErrorManager.WRITE_FAILURE );
        }
    }    // publish

    /**
     * Descripción de Método
     *
     */

    public void flush() {
        try {
            if( m_writerOut != null ) {
                m_writerOut.flush();
            }
        } catch( Exception ex ) {
            reportError( "flush out",ex,ErrorManager.FLUSH_FAILURE );
        }

        try {
            if( m_writerErr != null ) {
                m_writerErr.flush();
            }
        } catch( Exception ex ) {
            reportError( "flush err",ex,ErrorManager.FLUSH_FAILURE );
        }
    }    // flush

    /**
     * Descripción de Método
     *
     *
     * @throws SecurityException
     */

    public void close() throws SecurityException {
        if( m_writerOut == null ) {
            return;
        }

        // Write Tail

        try {
            if( !m_doneHeader ) {
                m_writerOut.write( getFormatter().getHead( this ));
            }

            //

            m_writerOut.write( getFormatter().getTail( this ));
        } catch( Exception ex ) {
            reportError( "tail",ex,ErrorManager.WRITE_FAILURE );
        }

        //

        flush();

        // Close

        try {
            m_writerOut.close();
        } catch( Exception ex ) {
            reportError( "close out",ex,ErrorManager.CLOSE_FAILURE );
        }

        m_writerOut = null;

        try {
            m_writerErr.close();
        } catch( Exception ex ) {
            reportError( "close err",ex,ErrorManager.CLOSE_FAILURE );
        }

        m_writerErr = null;
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "CLogConsole[" );

        sb.append( "Level=" ).append( getLevel()).append( "]" );

        return sb.toString();
    }    // toString
}    // CLogConsole



/*
 *  @(#)CLogConsole.java   25.03.06
 * 
 *  Fin del fichero CLogConsole.java
 *  
 *  Versión 2.2
 *
 */
