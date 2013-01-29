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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;
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

public class CLogErrorBuffer extends Handler {

    /**
     * Descripción de Método
     *
     *
     * @param create
     *
     * @return
     */

    public static CLogErrorBuffer get( boolean create ) {
        if( (s_handler == null) && create ) {
            s_handler = new CLogErrorBuffer();
        }

        return s_handler;
    }    // get

    /** Descripción de Campos */

    private static CLogErrorBuffer s_handler;

    /**
     * Constructor de la clase ...
     *
     */

    public CLogErrorBuffer() {
        if( s_handler == null ) {
            s_handler = this;
        } else {
            reportError( "Error Handler exists already",new IllegalStateException( "Existing Handler" ),ErrorManager.GENERIC_FAILURE );
        }

        initialize();
    }    // CLogErrorBuffer

    /** Descripción de Campos */

    private static final int ERROR_SIZE = 20;

    /** Descripción de Campos */

    private LinkedList m_errors = new LinkedList();

    /** Descripción de Campos */

    private LinkedList m_history = new LinkedList();

    /** Descripción de Campos */

    private static final int LOG_SIZE = 100;

    /** Descripción de Campos */

    private LinkedList m_logs = new LinkedList();

    /**
     * Descripción de Método
     *
     */

    private void initialize() {

        // System.out.println("CLogConsole.initialize");

        // Foratting

        setFormatter( CLogFormatter.get());

        // Default Level

        super.setLevel( Level.INFO );

        // Filter

        setFilter( CLogFilter.get());
    }    // initialize

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

        if( newLevel == Level.OFF ) {
            super.setLevel( Level.SEVERE );
        } else if( (newLevel == Level.ALL) || (newLevel == Level.FINEST) || (newLevel == Level.FINER) ) {
            super.setLevel( Level.FINE );
        } else {
            super.setLevel( newLevel );
        }
    }    // SetLevel

    /**
     * Descripción de Método
     *
     *
     * @param record
     */

    public void publish( LogRecord record ) {
        if( !isLoggable( record ) || (m_logs == null) ) {
            return;
        }

        // Output

        synchronized( m_logs ) {
            if( m_logs.size() >= LOG_SIZE ) {
                m_logs.removeFirst();
            }

            m_logs.add( record );
        }

        // We have an error

        if( record.getLevel() == Level.SEVERE ) {
            if( m_errors.size() >= ERROR_SIZE ) {
                m_errors.removeFirst();
                m_history.removeFirst();
            }

            // Add Error

            m_errors.add( record );
            record.getSourceClassName();    // forces Class Name eval

            // Create History

            ArrayList history = new ArrayList();

            for( int i = m_logs.size() - 1;i >= 0;i-- ) {
                LogRecord rec = ( LogRecord )m_logs.get( i );

                if( rec.getLevel() == Level.SEVERE ) {
                    if( history.size() == 0 ) {
                        history.add( rec );
                    } else {
                        break;    // don't incluse previous error
                    }
                } else {
                    history.add( rec );

                    if( history.size() > 10 ) {
                        break;    // no more then 10 history records
                    }
                }
            }

            LogRecord[] historyArray = new LogRecord[ history.size()];
            int         no           = 0;

            for( int i = history.size() - 1;i >= 0;i-- ) {
                historyArray[ no++ ] = ( LogRecord )history.get( i );
            }

            m_history.add( historyArray );
        }
    }                             // publish

    /**
     * Descripción de Método
     *
     */

    public void flush() {}    // flush

    /**
     * Descripción de Método
     *
     *
     * @throws SecurityException
     */

    public void close() throws SecurityException {
        if( m_logs != null ) {
            m_logs.clear();
        }

        m_logs = null;

        if( m_errors != null ) {
            m_errors.clear();
        }

        m_errors = null;

        if( m_history != null ) {
            m_history.clear();
        }

        m_history = null;
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public Vector getColumnNames( Properties ctx ) {
        Vector cn = new Vector();

        cn.add( "Time" );
        cn.add( "Level" );

        //

        cn.add( "Class.Method" );
        cn.add( "Message" );

        // 2

        cn.add( "Parameter" );
        cn.add( "Trace" );

        //

        return cn;
    }    // getColumnNames

    /**
     * Descripción de Método
     *
     *
     * @param errorsOnly
     *
     * @return
     */

    public Vector getLogData( boolean errorsOnly ) {
        LogRecord[] records = getRecords( errorsOnly );

        // System.out.println("getLogData - " + events.length);

        Vector rows = new Vector( records.length );

        for( int i = 0;i < records.length;i++ ) {
            LogRecord record = records[ i ];
            Vector    cols   = new Vector();

            //

            cols.add( new Timestamp( record.getMillis()));
            cols.add( record.getLevel().getName());

            //

            cols.add( CLogFormatter.getClassMethod( record ));
            cols.add( record.getMessage());

            //

            cols.add( CLogFormatter.getParameters( record ));
            cols.add( CLogFormatter.getExceptionTrace( record ));

            //

            rows.add( cols );
        }

        return rows;
    }    // getData

    /**
     * Descripción de Método
     *
     *
     * @param errorsOnly
     *
     * @return
     */

    public LogRecord[] getRecords( boolean errorsOnly ) {
        LogRecord[] retValue = null;

        if( errorsOnly ) {
            synchronized( m_errors ) {
                retValue = new LogRecord[ m_errors.size()];
                m_errors.toArray( retValue );
            }
        } else {
            synchronized( m_logs ) {
                retValue = new LogRecord[ m_logs.size()];
                m_logs.toArray( retValue );
            }
        }

        return retValue;
    }    // getEvents

    /**
     * Descripción de Método
     *
     *
     * @param errorsOnly
     */

    public void resetBuffer( boolean errorsOnly ) {
        synchronized( m_errors ) {
            m_errors.clear();
            m_history.clear();
        }

        if( !errorsOnly ) {
            synchronized( m_logs ) {
                m_logs.clear();
            }
        }
    }    // resetBuffer

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param errorsOnly
     *
     * @return
     */

    public String getErrorInfo( Properties ctx,boolean errorsOnly ) {
        StringBuffer sb = new StringBuffer();

        //

        if( errorsOnly ) {
            for( int i = 0;i < m_history.size();i++ ) {
                sb.append( "-------------------------------\n" );

                LogRecord[] records = ( LogRecord[] )m_history.get( i );

                for( int j = 0;j < records.length;j++ ) {
                    LogRecord record = records[ j ];

                    sb.append( getFormatter().format( record ));
                }
            }
        } else {
            for( int i = 0;i < m_logs.size();i++ ) {
                LogRecord record = ( LogRecord )m_logs.get( i );

                sb.append( getFormatter().format( record ));
            }
        }

        sb.append( "\n" );
        CLogMgt.getInfo( sb );
        CLogMgt.getInfoDetail( sb,ctx );

        //

        return sb.toString();
    }    // getErrorInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "CLogErrorBuffer[" );

        sb.append( "Errors=" ).append( m_errors.size()).append( ",History=" ).append( m_history.size()).append( ",Logs=" ).append( m_logs.size()).append( ",Level=" ).append( getLevel()).append( "]" );

        return sb.toString();
    }    // toString
}    // CLogErrorBuffer



/*
 *  @(#)CLogErrorBuffer.java   25.03.06
 * 
 *  Fin del fichero CLogErrorBuffer.java
 *  
 *  Versión 2.2
 *
 */
