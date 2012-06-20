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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MClient;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CLogMgt {

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     */

    public static void initialize( boolean isClient ) {
        if( s_handlers != null ) {
            return;
        }

        LogManager mgr = LogManager.getLogManager();

        try {
            String fileName = "logClient.properties";

            if( !isClient ) {
                fileName = "logServer.properties";
            }

            InputStream         in  = CLogMgt.class.getResourceAsStream( fileName );
            BufferedInputStream bin = new BufferedInputStream( in );

            mgr.readConfiguration( bin );
            in.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }

        // Create Handler List

        s_handlers = new ArrayList();

        try {
            Logger rootLogger = Logger.getLogger( "" );

            // System.out.println(rootLogger.getName() + " (" + rootLogger + ")");

            Handler[] handlers = rootLogger.getHandlers();

            for( int i = 0;i < handlers.length;i++ ) {

                // System.out.println("  > " + handlers[i]);

                if( !s_handlers.contains( handlers[ i ] )) {
                    s_handlers.add( handlers[ i ] );
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        // System.out.println("Handlers=" + s_handlers.size());

        // Check Loggers

        if( CLogErrorBuffer.get( false ) == null ) {
            addHandler( CLogErrorBuffer.get( true ));
        }

        if( (CLogConsole.get( false ) == null) && isClient ) {
            addHandler( CLogConsole.get( true ));
        }

        CLogFile fh = CLogFile.get( false,null );

        if( (fh == null) &&!isClient ) {
            fh = CLogFile.get( true,null );
            addHandler( fh );
        }

        if( (fh != null) &&!isClient ) {
            System.out.println( fh );
        }

        setFormatter( CLogFormatter.get());
        setFilter( CLogFilter.get());

        // setLevel(s_currentLevel);
        // setLoggerLevel(Level.ALL, null);
        //

        CLogMgtLog4J.initialize( isClient );

        // System.out.println("Handlers=" + s_handlers.size() + ", Level=" + s_currentLevel);

    }    // initialize

    /** Descripción de Campos */

    private static ArrayList s_handlers = null;

    /** Descripción de Campos */

    private static Level s_currentLevel = Level.INFO;

    /** Descripción de Campos */

    private static Logger log = Logger.getAnonymousLogger();

    /** Descripción de Campos */

    public static final Level[] LEVELS = new Level[] {
        Level.OFF,Level.SEVERE,Level.WARNING,TimeStatsLevel.TSTATS,Level.INFO,Level.CONFIG,Level.FINE,Level.FINER,Level.FINEST,Level.ALL
    };

    /** Descripción de Campos */

    private static final String NL = System.getProperty( "line.separator" );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected static Handler[] getHandlers() {
        Handler[] handlers = new Handler[ s_handlers.size()];

        for( int i = 0;i < s_handlers.size();i++ ) {
            handlers[ i ] = ( Handler )s_handlers.get( i );
        }

        return handlers;
    }    // getHandlers

    /**
     * Descripción de Método
     *
     *
     * @param handler
     */

    public static void addHandler( Handler handler ) {
        if( handler == null ) {
            return;
        }

        Logger rootLogger = Logger.getLogger( "" );

        rootLogger.addHandler( handler );

        //

        s_handlers.add( handler );
        log.log( Level.CONFIG,"addHandler=" + handler );
    }    // addHandler

    /**
     * Descripción de Método
     *
     *
     * @param formatter
     */

    protected static void setFormatter( java.util.logging.Formatter formatter ) {
        for( int i = 0;i < s_handlers.size();i++ ) {
            Handler handler = ( Handler )s_handlers.get( i );

            handler.setFormatter( formatter );
        }

        // log.log(Level.CONFIG, "setFormatter=" + formatter);

    }    // setFormatter

    /**
     * Descripción de Método
     *
     *
     * @param filter
     */

    protected static void setFilter( Filter filter ) {
        for( int i = 0;i < s_handlers.size();i++ ) {
            Handler handler = ( Handler )s_handlers.get( i );

            handler.setFilter( filter );
        }

        // log.log(Level.CONFIG, "setFilter=" + filter);

    }    // setFilter

    /**
     * Descripción de Método
     *
     *
     * @param level
     * @param loggerNamePart
     */

    public static void setLoggerLevel( Level level,String loggerNamePart ) {
        if( level == null ) {
            return;
        }

        LogManager  mgr = LogManager.getLogManager();
        Enumeration en  = mgr.getLoggerNames();

        while( en.hasMoreElements()) {
            String name = en.nextElement().toString();

            if( (loggerNamePart == null) || (name.indexOf( loggerNamePart ) != -1) ) {
                Logger lll = Logger.getLogger( name );

                lll.setLevel( level );
            }
        }
    }    // setLoggerLevel

    /**
     * Descripción de Método
     *
     *
     * @param level
     */

    public static void setLevel( Level level ) {
        if( level == null ) {
            return;
        }

        for( int i = 0;i < s_handlers.size();i++ ) {
            Handler handler = ( Handler )s_handlers.get( i );

            handler.setLevel( level );
        }

        // JDBC if ALL

        setJDBCDebug( s_currentLevel.intValue() == Level.ALL.intValue());

        //

        if( level.intValue() != s_currentLevel.intValue()) {
            log.config( level.toString());
        }

        s_currentLevel = level;
    }    // setHandlerLevel

    /**
     * Descripción de Método
     *
     *
     * @param intLevel
     */

    public static void setLevel( int intLevel ) {
        setLevel( String.valueOf( intLevel ));
    }    // setLevel

    /**
     * Descripción de Método
     *
     *
     * @param levelString
     */

    public static void setLevel( String levelString ) {
        if( levelString == null ) {
            return;
        }

        //

        for( int i = 0;i < LEVELS.length;i++ ) {
            if( LEVELS[ i ].getName().equals( levelString )) {
                setLevel( LEVELS[ i ] );

                return;
            }
        }

        log.log( Level.CONFIG,"Ignored: " + levelString );
    }    // setLevel

    /**
     * Descripción de Método
     *
     *
     * @param enable
     */

    public static void setJDBCDebug( boolean enable ) {
        if( enable ) {
            DriverManager.setLogWriter( new PrintWriter( System.err ));
        } else {
            DriverManager.setLogWriter( null );
        }
    }    // setJDBCDebug

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Level getLevel() {
        return s_currentLevel;
    }    // getLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static int getLevelAsInt() {
        return s_currentLevel.intValue();
    }    // getLevel

    /**
     * Descripción de Método
     *
     *
     * @param level
     *
     * @return
     */

    public static boolean isLevel( Level level ) {
        if( level == null ) {
            return false;
        }

        return level.intValue() >= s_currentLevel.intValue();
    }    // isLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isLevelAll() {
        return Level.ALL.intValue() == s_currentLevel.intValue();
    }    // isLevelFinest

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isLevelFinest() {
        return Level.FINEST.intValue() >= s_currentLevel.intValue();
    }    // isLevelFinest

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isLevelFiner() {
        return Level.FINER.intValue() >= s_currentLevel.intValue();
    }    // isLevelFiner

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isLevelFine() {
        return Level.FINE.intValue() >= s_currentLevel.intValue();
    }    // isLevelFine

    /**
     * Descripción de Método
     *
     *
     * @param enableLogging
     */

    public static void enable( boolean enableLogging ) {
        if( enableLogging ) {
            setLevel( s_currentLevel );
        } else {
            Level level = s_currentLevel;

            setLevel( Level.OFF );
            s_currentLevel = level;
        }
    }    // enable

    /**
     * Descripción de Método
     *
     */

    public static void shutdown() {
        LogManager mgr = LogManager.getLogManager();

        mgr.reset();
    }    // shutdown

    /**
     * Descripción de Método
     *
     *
     * @param p
     * @param description
     * @param logIt
     */

    public static void printProperties( Properties p,String description,boolean logIt ) {
        if( p == null ) {
            return;
        }

        if( logIt ) {
            log.info( description + " - Size=" + p.size() + ", Hash=" + p.hashCode() + "\n" + getLocalHost());
        } else {
            System.out.println( "Log.printProperties = " + description + ", Size=" + p.size() + ", Hash=" + p.hashCode() + "\n" + getLocalHost());
        }

        Object[] pp = p.keySet().toArray();

        Arrays.sort( pp );

        for( int i = 0;i < pp.length;i++ ) {
            String key   = pp[ i ].toString();
            String value = p.getProperty( key );

            if( logIt ) {
                log.config( key + "=" + value );
            } else {
                System.out.println( "  " + key + " = " + value );
            }
        }
    }    // printProperties

    /**
     * Descripción de Método
     *
     *
     * @param sb
     *
     * @return
     */

    public static StringBuffer getInfo( StringBuffer sb ) {
        if( sb == null ) {
            sb = new StringBuffer();
        }

        final String eq = " = ";

        sb.append( getMsg( "Host" )).append( eq ).append( getServerInfo()).append( NL );
        sb.append( getMsg( "Database" )).append( eq ).append( getDatabaseInfo()).append( NL );
        sb.append( getMsg( "Schema" )).append( eq ).append( CConnection.get().getDbUid()).append( NL );

        //

        sb.append( getMsg( "AD_User_ID" )).append( eq ).append( Env.getContext( Env.getCtx(),"#AD_User_Name" )).append( NL );
        sb.append( getMsg( "AD_Role_ID" )).append( eq ).append( Env.getContext( Env.getCtx(),"#AD_Role_Name" )).append( NL );

        //

        sb.append( getMsg( "AD_Client_ID" )).append( eq ).append( Env.getContext( Env.getCtx(),"#AD_Client_Name" )).append( NL );
        sb.append( getMsg( "AD_Org_ID" )).append( eq ).append( Env.getContext( Env.getCtx(),"#AD_Org_Name" )).append( NL );

        //

        sb.append( getMsg( "Date" )).append( eq ).append( Env.getContext( Env.getCtx(),"#Date" )).append( NL );
        sb.append( getMsg( "Printer" )).append( eq ).append( Env.getContext( Env.getCtx(),"#Printer" )).append( NL );

        //

        Manifest mf = ZipUtil.getManifest( "CClient.jar" );

        if( mf == null ) {
            mf = ZipUtil.getManifest( "OXPTools.jar" );
        }

        if( mf != null ) {
            Attributes atts = mf.getMainAttributes();

            if( atts != null ) {
                Iterator it = atts.keySet().iterator();

                while( it.hasNext()) {
                    Object key = it.next();

                    if( key.toString().startsWith( "Impl" ) || key.toString().startsWith( "Spec" )) {
                        sb.append( key ).append( eq ).append( atts.get( key )).append( NL );
                    }
                }
            }
        }

        sb.append( "OXPHome = " ).append( OpenXpertya.getOXPHome()).append( NL );
        sb.append( Env.getLanguage( Env.getCtx())).append( NL );

        MClient client = MClient.get( Env.getCtx());

        sb.append( client ).append( NL );
        sb.append( getMsg( "IsMultiLingualDocument" )).append( eq ).append( client.isMultiLingualDocument()).append( NL );
        sb.append( "BaseLanguage = " ).append( Env.isBaseLanguage( Env.getCtx(),"AD_Window" )).append( "/" ).append( Env.isBaseLanguage( Env.getCtx(),"C_UOM" )).append( NL );
        sb.append( "PDF License=" ).append( true ).append( NL );
        sb.append( OpenXpertya.getJavaInfo()).append( NL );
        sb.append( OpenXpertya.getOSInfo());

        //

        return sb;
    }    // getInfo

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param ctx
     *
     * @return
     */

    public static StringBuffer getInfoDetail( StringBuffer sb,Properties ctx ) {
        if( sb == null ) {
            sb = new StringBuffer();
        }

        if( ctx == null ) {
            ctx = Env.getCtx();
        }

        // Envoronment

        CConnection cc = CConnection.get();

        sb.append( NL ).append( "=== Environment === " ).append( OpenXpertya.getCheckSum()).append( NL ).append( OpenXpertya.getSummaryAscii()).append( NL ).append( getLocalHost()).append( NL ).append( cc.toStringLong()).append( NL ).append( cc.getInfo()).append( NL );

        // Context

        sb.append( NL ).append( "=== Context ===" ).append( NL );

        String[] context = Env.getEntireContext( ctx );

        Arrays.sort( context );

        for( int i = 0;i < context.length;i++ ) {
            sb.append( context[ i ] ).append( NL );
        }

        // System

        sb.append( NL ).append( "=== System ===" ).append( NL );

        Object[] pp = System.getProperties().keySet().toArray();

        Arrays.sort( pp );

        for( int i = 0;i < pp.length;i++ ) {
            String key   = pp[ i ].toString();
            String value = System.getProperty( key );

            sb.append( key ).append( "=" ).append( value ).append( NL );
        }

        return sb;
    }    // getInfoDetail

    /**
     * Descripción de Método
     *
     *
     * @param msg
     *
     * @return
     */

    private static String getMsg( String msg ) {
        if( DB.isConnected()) {
            return Msg.translate( Env.getCtx(),msg );
        }

        return msg;
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private static String getServerInfo() {
        StringBuffer sb = new StringBuffer();

        // Host

        sb.append( CConnection.get().getAppsHost()).append( " : " ).append( CConnection.get().getAppsPort()).append( " (" );

        // Server

        if( CConnection.get().isAppsServerOK( false )) {
            sb.append( CConnection.get().getServerVersion());
        } else {
            sb.append( getMsg( "NotActive" ));
        }

        //

        sb.append( ")\n\tTunnel=" ).append( CConnection.get().isRMIoverHTTP() && CConnection.get().isAppsServerOK( false )).append( ", Objects=" ).append( DB.isRemoteObjects()).append( ", Process=" ).append( DB.isRemoteProcess());

        return sb.toString();
    }    // getServerInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private static String getDatabaseInfo() {
        StringBuffer sb = new StringBuffer();

        sb.append( CConnection.get().getDbHost()).append( " : " ).append( CConnection.get().getDbPort()).append( " / " ).append( CConnection.get().getDbName());

        // Connection Manager

        if( CConnection.get().isViaFirewall()) {
            sb.append( getMsg( "via" )).append( " " ).append( CConnection.get().getFwHost()).append( " : " ).append( CConnection.get().getFwPort());
        }

        return sb.toString();
    }    // getDatabaseInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private static String getLocalHost() {
        try {
            InetAddress id = InetAddress.getLocalHost();

            return id.toString();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLocalHost",e );
        }

        return "-no local host info -";
    }    // getLocalHost

    /**
     * Constructor de la clase ...
     *
     */

    public CLogMgt() {
        testLog();
    }

    /**
     * Descripción de Método
     *
     */

    private void testLog() {
        final CLogger log = CLogger.getCLogger( "test" );

        //

        log.log( Level.SEVERE,"severe" );
        log.warning( "warning" );
        log.info( "Info" );
        log.config( "config" );
        log.fine( "fine" );
        log.finer( "finer" );
        log.entering( "myClass","myMethod","parameter" );
        log.exiting( "myClass","myMethod","result" );
        log.finest( "finest" );
        new Thread() {
            public void run() {
                log.info( "thread info" );
            }
        }.start();

        try {
            Integer.parseInt( "ABC" );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"error message",e );
        }

        log.log( Level.INFO,"info message 1","1Param" );
        log.log( Level.INFO,"info message n",new Object[]{ "1Param","2Param" } );
    }    // testLog

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        initialize( true );
        new CLogMgt();
    }    // CLogMgt
}    // CLogMgt



/*
 *  @(#)CLogMgt.java   25.03.06
 * 
 *  Fin del fichero CLogMgt.java
 *  
 *  Versión 2.2
 *
 */
