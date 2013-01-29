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

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.openXpertya.util.Ini;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 21.04.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CLogFile extends Handler {

    /**
     * Descripción de Método
     *
     *
     * @param create
     * @param OXPHome
     *
     * @return
     */

    public static CLogFile get( boolean create,String OXPHome ) {
        if( (s_logFile == null) && create ) {
            s_logFile = new CLogFile( OXPHome,true );
        }

        return s_logFile;
    }    // get

    /** Descripción de Campos */

    private static CLogFile s_logFile = null;

    /**
     * Constructor de la clase ...
     *
     */

    public CLogFile() {
        this( null,true );
    }    // CLogFile

    public CLogFile( String OXPHome,boolean createLogDir) {
    	this(OXPHome, createLogDir, false);
    }
    /**
     * Constructor de la clase ...
     *
     *
     * @param OXPHome
     * @param createLogDir
     * @param isClient
     */

    public CLogFile( String OXPHome,boolean createLogDir, boolean isClient) {
        if( s_logFile == null ) {
            s_logFile = this;
        } else {
            reportError( "El manejador del fichero ya existe ",new IllegalStateException( "Existing Handler" ),ErrorManager.GENERIC_FAILURE );
        }

        //

        if( (OXPHome != null) && (OXPHome.length() > 0) ) {
            m_OXPHome = OXPHome;
        } else {
            m_OXPHome = Ini.findOXPHome();
        }

        initialize( m_OXPHome,createLogDir, isClient );
    }    // CLogFile

    /** Descripción de Campos */

    private String m_OXPHome = null;

    /** Descripción de Campos */

    private boolean m_doneHeader = false;

    /** Descripción de Campos */

    private File m_file = null;

    /** Descripción de Campos */

    private FileWriter m_writer = null;

    /** Descripción de Campos */

    private String m_fileNameDate = "";

    /** Descripción de Campos */

    private int m_records = 0;

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     * @param createLogDir
     */

    private void initialize( String fileName,boolean createLogDir, boolean isClient ) {

        // System.out.println("CLogFile.initialize");
        // Close Old File

        if( m_writer != null ) {
            close();
        }

        m_doneHeader = false;

        // New File Name

        if( !createFile( fileName,createLogDir, isClient )) {
            return;
        }

        // New Writer

        try {
            m_writer  = new FileWriter( m_file,true );
            m_records = 0;
        } catch( Exception ex ) {
            reportError( "writer",ex,ErrorManager.OPEN_FAILURE );
            m_writer = null;
        }

        // System.out.println(getFileName());

        // Formateador del fichero de log

        setFormatter( CLogFormatter.get());

        // Este es el nivel de salida del fichero de log del servidor.
        // de entre los posibles:  OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
        // este nivel es el nivel de filtro del log. Son filtrados tan solo los niveles inferiores 

        setLevel( Level.SEVERE );
        //setLevel(Level.ALL);
        // Filter

        setFilter( CLogFilter.get());
    }    // initialize

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     * @param createLogDir
     *
     * @return
     */

    private boolean createFile( String fileName,boolean createLogDir, boolean isClient ) {
        try {

            // Test OXPHome

            if( fileName != null ) {
                File dir = new File( fileName );

                if( !dir.exists() ||!dir.isDirectory()) {
                    reportError( "Directorio base incorrecto: " + fileName,null,ErrorManager.OPEN_FAILURE );
                    fileName = null;
                }
            }

            // Test/Create OXPHome/lib

            if( (fileName != null) && createLogDir ) {
                fileName += File.separator + "log";

                File dir = new File( fileName );

                if( !dir.exists()) {
                    dir.mkdir();
                }

                if( !dir.exists() ||!dir.isDirectory()) {
                    reportError( "Directorio de logs incorrecto: " + fileName,null,ErrorManager.OPEN_FAILURE );
                    fileName = null;
                }
            }

            // Test/Create OXPHome/lib/file

            if( fileName != null ) {
                m_fileNameDate = getFileNameDate( System.currentTimeMillis());
                fileName       += File.separator + (isClient?"client_":"") + "ServidorOXP_" + m_fileNameDate + "_";

                // hasta un máximo de 1.440 ficheros por día. Uno por minuto. Suficiente.
                for( int i = 0;i < 1440;i++ ) {
                    String finalName = fileName + i + ".log";
                    File   file      = new File( finalName );

                    if( !file.exists()) {
                        m_file = file;

                        break;
                    }
                }
            }

            if( m_file == null ) {    // Fallback create temp file
                m_file = File.createTempFile( "openXpertya",".log" );
            }
        } catch( Exception ex ) {
            reportError( "fichero",ex,ErrorManager.OPEN_FAILURE );
            m_file = null;

            return false;
        }

        return true;
    }    // createFile

    /**
     * Descripción de Método
     *
     *
     * @param time
     *
     * @return
     */

    public static String getFileNameDate( long time ) {
        Timestamp ts = new Timestamp( time );
        String    s  = ts.toString();

        return s.substring( 0,10 );
    }    // getFileNameDate

    /**
     * Descripción de Método
     *
     *
     * @param time
     */

    private void rotateLog( long time ) {
        if( (m_fileNameDate == null) || m_fileNameDate.equals( getFileNameDate( time ))) {
            return;
        }

        rotateLog();
    }    // rotateLog

    /**
     * Descripción de Método
     *
     */

    public void rotateLog() {
        initialize( m_OXPHome,true, Ini.isClient() );
    }    // rotateLog

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFileName() {
        if( m_file != null ) {
            return m_file.getAbsolutePath();
        }

        return "";
    }    // getFileName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public File getLogDirectory() {
        if( m_file != null ) {
            return m_file.getParentFile();
        }

        return null;
    }    // getLogDirectory

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
    }    // setLevel

    /**
     * Descripción de Método
     *
     *
     * @param record
     */

    public void publish( LogRecord record ) {
        if( !isLoggable( record ) || (m_writer == null) ) {
            return;
        }

        rotateLog( record.getMillis());

        // Format

        String msg = null;

        try {
            msg = getFormatter().format( record );
        } catch( Exception ex ) {
            reportError( "formateando",ex,ErrorManager.FORMAT_FAILURE );

            return;
        }

        // Output

        try {
            if( !m_doneHeader ) {
                m_writer.write( getFormatter().getHead( this ));
                m_doneHeader = true;
            }

            //

            m_writer.write( msg );
            m_records++;

            //

            if( (record.getLevel() == Level.SEVERE) || (record.getLevel() == Level.WARNING) || (m_records % 10 == 0) ) {    // flush every 10 records
                flush();
            }
        } catch( Exception ex ) {
            reportError( "escribiendo",ex,ErrorManager.WRITE_FAILURE );
        }
    }    // publish

    /**
     * Descripción de Método
     *
     */

    public void flush() {
        try {
            if( m_writer != null ) {
                m_writer.flush();
            }
        } catch( Exception ex ) {
            reportError( "flush",ex,ErrorManager.FLUSH_FAILURE );
        }
    }    // flush

    /**
     * Descripción de Método
     *
     *
     * @throws SecurityException
     */

    public void close() throws SecurityException {
        if( m_writer == null ) {
            return;
        }

        // Write Tail

        try {
            if( !m_doneHeader ) {
                m_writer.write( getFormatter().getHead( this ));
            }

            //

            m_writer.write( getFormatter().getTail( this ));
        } catch( Exception ex ) {
            reportError( "tail",ex,ErrorManager.WRITE_FAILURE );
        }

        //

        flush();

        // Close

        try {
            m_writer.close();
        } catch( Exception ex ) {
            reportError( "close",ex,ErrorManager.CLOSE_FAILURE );
        }

        m_writer = null;
        m_file   = null;
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "CLogFile[" );

        sb.append( getFileName()).append( ",Level=" ).append( getLevel()).append( "]" );

        return sb.toString();
    }    // toString
}    // CLogFile



/*
 *  @(#)CLogFile.java   21.04.07
 * 
 *  Fin del fichero CLogFile.java
 *  
 *  Versión 2.2
 *
 */
