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

import java.awt.Toolkit;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Formatter;
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

public class CLogFormatter extends Formatter {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static CLogFormatter get() {
        if( s_formatter == null ) {
            s_formatter = new CLogFormatter();
        }

        return s_formatter;
    }    // get

    /** Descripción de Campos */

    private static CLogFormatter s_formatter = null;

    /** Descripción de Campos */

    public static String NL = System.getProperty( "line.separator" );

    /**
     * Constructor de la clase ...
     *
     */

    private CLogFormatter() {
        super();
    }    // CLogFormatter

    /** Descripción de Campos */

    private Timestamp m_ts = new Timestamp( System.currentTimeMillis());

    /** Descripción de Campos */

    private boolean m_shortFormat = false;

    /**
     * Descripción de Método
     *
     *
     * @param record
     *
     * @return
     */

    public String format( LogRecord record ) {
        StringBuffer sb = new StringBuffer();
        long         ms = record.getMillis();

        if( ms == 0 ) {
            m_ts.setTime( System.currentTimeMillis());
        }

        String tstr = "";

        try {
            tstr = m_ts.toString() + "00";
        } catch( Exception e ) {
            System.err.println( "CLogFormatter.format: " + e.getMessage());

            // 1   5    1    5    2    5

            tstr = "_________________________";
        }

        if( record.getLevel() == Level.SEVERE ) {            // 12:12:12.123
            sb.append( "===========> " );

            if( Ini.isClient()) {
                Toolkit.getDefaultToolkit().beep();
            }
        } else if( record.getLevel() == Level.WARNING ) {    // 12:12:12.123
            sb.append( "-----------> " );
        } else {
            sb.append( tstr.substring( 11,23 ));

            int spaces = 11;

            if( record.getLevel() == Level.INFO || record.getLevel() == TimeStatsLevel.TSTATS) {
                spaces = 1;
            } else if( record.getLevel() == Level.CONFIG ) {
                spaces = 3;
            } else if( record.getLevel() == Level.FINE ) {
                spaces = 5;
            } else if( record.getLevel() == Level.FINER ) {
                spaces = 7;
            } else if( record.getLevel() == Level.FINEST ) {
                spaces = 9;
            }

            sb.append( "                          ".substring( 0,spaces ));
        }

        if( !m_shortFormat ) {
            sb.append( getClassMethod( record )).append( ": " );
        }

        sb.append( record.getMessage());

        String parameters = getParameters( record );

        if( parameters.length() > 0 ) {
            sb.append( " (" ).append( parameters ).append( ")" );
        }

        if( record.getThreadID() != 10 ) {
            sb.append( " [" ).append( record.getThreadID()).append( "]" );
        }

        //

        sb.append( NL );

        if( record.getThrown() != null ) {
            sb.append( getExceptionTrace( record )).append( NL );
        }

        return sb.toString();
    }    // format

    /**
     * Descripción de Método
     *
     *
     * @param h
     *
     * @return
     */

    public String getHead( Handler h ) {
        String className = h.getClass().getName();
        int    index     = className.lastIndexOf( '.' );

        if( index != -1 ) {
            className = className.substring( index + 1 );
        }

        StringBuffer sb = new StringBuffer().append( "*** " ).append( new Timestamp( System.currentTimeMillis())).append( " OpenXpertya Log (" ).append( className ).append( ") ***" ).append( NL );

        return sb.toString();
    }    // getHead

    /**
     * Descripción de Método
     *
     *
     * @param h
     *
     * @return
     */

    public String getTail( Handler h ) {
        String className = h.getClass().getName();
        int    index     = className.lastIndexOf( '.' );

        if( index != -1 ) {
            className = className.substring( index + 1 );
        }

        StringBuffer sb = new StringBuffer().append( NL ).append( "*** " ).append( new Timestamp( System.currentTimeMillis())).append( " OpenXpertya Log (" ).append( className ).append( ") ***" ).append( NL );

        return sb.toString();
    }    // getTail

    /**
     * Descripción de Método
     *
     *
     * @param shortFormat
     */

    public void setFormat( boolean shortFormat ) {
        m_shortFormat = shortFormat;
    }    // setFormat

    /**
     * Descripción de Método
     *
     *
     * @param record
     *
     * @return
     */

    public static String getClassMethod( LogRecord record ) {
        StringBuffer sb        = new StringBuffer();
        String       className = record.getLoggerName();

        if( (className == null) || (className.indexOf( "default" ) != -1    // anonymous logger
                ) || (className.indexOf( "global" ) != -1) ) {    // global logger
            className = record.getSourceClassName();
        }

        if( className != null ) {
            int index = className.lastIndexOf( '.' );

            if( index != -1 ) {
                sb.append( className.substring( index + 1 ));
            } else {
                sb.append( className );
            }
        } else {
            sb.append( record.getLoggerName());
        }

        if( record.getSourceMethodName() != null ) {
            sb.append( "." ).append( record.getSourceMethodName());
        }

        String retValue = sb.toString();

        if( retValue.equals( "Trace.printStack" )) {
            return "";
        }

        return retValue;
    }    // getClassMethod

    /**
     * Descripción de Método
     *
     *
     * @param record
     *
     * @return
     */

    public static String getParameters( LogRecord record ) {
        StringBuffer sb         = new StringBuffer();
        Object[]     parameters = record.getParameters();

        if( (parameters != null) && (parameters.length > 0) ) {
            for( int i = 0;i < parameters.length;i++ ) {
                if( i > 0 ) {
                    sb.append( ", " );
                }

                sb.append( parameters[ i ] );
            }
        }

        return sb.toString();
    }    // getParameters

    /**
     * Descripción de Método
     *
     *
     * @param record
     *
     * @return
     */

    public static String getExceptionTrace( LogRecord record ) {
        Throwable thrown = record.getThrown();

        if( thrown == null ) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        try {
            fillExceptionTrace( sb,"",thrown );
        } catch( Exception ex ) {
        }

        return sb.toString();
    }    // getException

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param hdr
     * @param thrown
     */

    private static void fillExceptionTrace( StringBuffer sb,String hdr,Throwable thrown ) {
        boolean firstError = hdr.length() == 0;

        sb.append( hdr ).append( thrown.toString());

        if( thrown instanceof SQLException ) {
            SQLException ex = ( SQLException )thrown;

            sb.append( "; State=" ).append( ex.getSQLState()).append( "; ErrorCode=" ).append( ex.getErrorCode());
        }

        sb.append( NL );

        //

        StackTraceElement[] trace           = thrown.getStackTrace();
        boolean             openxpertyaTrace   = false;
        int                 openxpertyaTraceNo = 0;

        for( int i = 0;i < trace.length;i++ ) {
            openxpertyaTrace = trace[ i ].getClassName().startsWith( "org.openXpertya." );

            if( (thrown instanceof ServerException    // RMI
                    ) || openxpertyaTrace ) {
                if( openxpertyaTrace ) {
                    sb.append( "\tat " ).append( trace[ i ] ).append( NL );
                }
            } else if( (i > 20) || ( (i > 10) && (openxpertyaTraceNo > 8) ) ) {
                break;
            } else {
                sb.append( "\tat " ).append( trace[ i ] ).append( NL );
            }

            if( openxpertyaTrace ) {
                openxpertyaTraceNo++;
            }
        }

        //

        Throwable cause = thrown.getCause();

        if( cause != null ) {
            fillExceptionTrace( sb,"caused by: ",cause );
        }
    }    // fillExceptionTrace
}    // CLogFormatter



/*
 *  @(#)CLogFormatter.java   25.03.06
 * 
 *  Fin del fichero CLogFormatter.java
 *  
 *  Versión 2.2
 *
 */
