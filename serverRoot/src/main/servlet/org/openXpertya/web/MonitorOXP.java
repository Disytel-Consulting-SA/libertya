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



package org.openXpertya.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.HtmlColor;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.font;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.h2;
import org.apache.ecs.xhtml.hr;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.select;
import org.apache.ecs.xhtml.strong;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.OpenXpertya;
import org.openXpertya.db.BaseDatosOXP;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MSystem;
import org.openXpertya.model.ProcesadorLogOXP;
import org.openXpertya.server.ServidorMgrOXP;
import org.openXpertya.server.ServidorOXP;
import org.openXpertya.util.CLogFile;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Ini;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MonitorOXP extends HttpServlet {

    /** Descripción de Campos */

    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la clase ...
     *
     */

    public MonitorOXP() {}    // MonitorOXP

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MonitorOXP.class );

    /** Descripción de Campos */

    private ServidorMgrOXP m_serverMgr = null;

    /** Descripción de Campos */

    private p m_message = null;

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    protected void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        if( processLogParameter( request,response )) {
            return;
        }

        if( processTraceParameter( request,response )) {
            return;
        }

        //

        if( processRunNowParameter( request )) {
            ;
        } else {
            processActionParameter( request );
        }

        createSummaryPage( request,response );
    }    // doGet

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    protected void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @return
     *
     * @throws IOException
     */

    @SuppressWarnings( "unused" )
    private boolean processLogParameter( HttpServletRequest request,HttpServletResponse response ) throws IOException {
        String serverID = WebUtil.getParameter( request,"Log" );

        if( (serverID == null) || (serverID.length() == 0) ) {
            return false;
        }

        log.info( "ServerID=" + serverID );

        ServidorOXP server = m_serverMgr.getServer( serverID );

        if( server == null ) {
            m_message = new p();
            m_message.addElement( new strong( "Server not found: " ));
            m_message.addElement( serverID );

            return false;
        }

        WebDoc doc = WebDoc.create( "openXpertya Server Monitor Log" );

        // Body

        body b = doc.getBody();

        //

        p para = new p();
        a link = new a( "MonitorOXP#" + serverID,"Return" );

        para.addElement( link );
        b.addElement( para );

        //

        b.addElement( new h2( server.getName()));

        //

        table table = new table();

        table.setBorder( 1 );
        table.setCellSpacing( 2 );
        table.setCellPadding( 2 );

        // Header

        tr line = new tr();

        line.addElement( new th().addElement( "Created" ));
        line.addElement( new th().addElement( "Summary" ));

        // line.addElement(new th().addElement("Error"));

        line.addElement( new th().addElement( "Reference" ));
        line.addElement( new th().addElement( "TextMsg" ));

        // line.addElement(new th().addElement("Description"));

        table.addElement( line );

        ProcesadorLogOXP[] logs = server.getLogs();

        for( int i = 0;i < logs.length;i++ ) {
            ProcesadorLogOXP log = logs[ i ];

            line = new tr();
            line.addElement( new td().addElement( WebEnv.getCellContent( log.getCreated())));
            line.addElement( new td().addElement( WebEnv.getCellContent( log.getSummary())));
            line.addElement( new td().addElement( WebEnv.getCellContent( log.getReference())));
            line.addElement( new td().addElement( WebEnv.getCellContent( log.getTextMsg())));
            table.addElement( line );
        }

        //

        b.addElement( table );
        link = new a( "#top","Top" );
        b.addElement( link );

        // fini

        WebUtil.createResponse( request,response,this,null,doc,false );

        return true;
    }    // processLogParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     *
     * @throws IOException
     */

    @SuppressWarnings( "unused" )
    private boolean processRunNowParameter( HttpServletRequest request ) throws IOException {
        String serverID = WebUtil.getParameter( request,"RunNow" );

        if( (serverID == null) || (serverID.length() == 0) ) {
            return false;
        }

        log.info( "ServerID=" + serverID );

        ServidorOXP server = m_serverMgr.getServer( serverID );

        if( server == null ) {
            m_message = new p();
            m_message.addElement( new strong( "Servidor no encontrado: " ));
            m_message.addElement( serverID );

            return false;
        }

        //

        server.runNow();

        //

        return true;
    }    // processRunParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     */

    private void processActionParameter( HttpServletRequest request ) {
        m_message = null;

        String action = WebUtil.getParameter( request,"Action" );

        if( (action == null) || (action.length() == 0) ) {
            return;
        }

        log.info( "Acción=" + action );

        try {
            boolean start = action.startsWith( "Start" );

            m_message = new p();

            String msg = ( start
                           ?"Started"
                           :"Stopped" ) + ": ";

            m_message.addElement( new strong( msg ));

            //

            String  serverID = action.substring( action.indexOf( "_" ) + 1 );
            boolean ok       = false;

            if( serverID.equals( "All" )) {
                if( start ) {
                    ok = m_serverMgr.startAll();
                } else {
                    ok = m_serverMgr.stopAll();
                }

                m_message.addElement( "All" );
            } else {
                ServidorOXP server = m_serverMgr.getServer( serverID );

                if( server == null ) {
                    m_message = new p();
                    m_message.addElement( new strong( "Servidor no encontrado: " ));
                    m_message.addElement( serverID );

                    return;
                }

                if( start ) {
                    ok = m_serverMgr.start( serverID );
                } else {
                    ok = m_serverMgr.stop( serverID );
                }

                m_message.addElement( server.getName());
            }

            m_message.addElement( ok
                                  ?" - OK"
                                  :" - Error!" );
        } catch( Exception e ) {
            m_message = new p();
            m_message.addElement( new strong( "Error processing parameter: " + action ));
            m_message.addElement( new br());
            m_message.addElement( e.toString());
        }
    }    // processActionParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @return
     *
     * @throws IOException
     * @throws ServletException
     */

    @SuppressWarnings( "unused" )
    private boolean processTraceParameter( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        String traceCmd   = WebUtil.getParameter( request,"Trace" );
        String traceLevel = WebUtil.getParameter( request,"TraceLevel" );

        if( (traceLevel != null) && (traceLevel.length() > 0) ) {
            log.info( "New Level: " + traceLevel );
            CLogMgt.setLevel( traceLevel );
            Ini.setProperty( Ini.P_TRACELEVEL,traceLevel );
            Ini.saveProperties( false );

            return false;
        }

        if( (traceCmd == null) || (traceCmd.length() == 0) ) {
            return false;
        }

        log.info( "Command: " + traceCmd );

        CLogFile fileHandler = CLogFile.get( false,null );

        //

        if( traceCmd.equals( "ROTATE" )) {
            if( fileHandler != null ) {
                fileHandler.rotateLog();
            }

            return false;    // re-display
        } else if( traceCmd.equals( "DELETE" )) {
            File logDir = fileHandler.getLogDirectory();

            if( (logDir != null) && logDir.isDirectory()) {
                File[] logs = logDir.listFiles();

                for( int i = 0;i < logs.length;i++ ) {
                    String fileName = logs[ i ].getAbsolutePath();

                    if( fileName.equals( fileHandler.getFileName())) {
                        continue;
                    }

                    if( logs[ i ].delete()) {
                        log.warning( "Deleted: " + fileName );
                    } else {
                        log.warning( "Not Deleted: " + fileName );
                    }
                }
            }

            return false;    // re-display
        }

        // Display current log File

        if( (fileHandler != null) && fileHandler.getFileName().equals( traceCmd )) {
            fileHandler.flush();
        }

        // Spool File

        File file = new File( traceCmd );

        if( !file.exists()) {
            log.warning( "Did not find File: " + traceCmd );

            return false;
        }

        if( file.length() == 0 ) {
            log.warning( "File Length=0: " + traceCmd );

            return false;
        }

        // Stream Log

        log.info( "Streaming: " + traceCmd );

        try {
            long   time       = System.currentTimeMillis();    // timer start
            int    fileLength = ( int )file.length();
            int    bufferSize = 2048;                          // 2k Buffer
            byte[] buffer     = new byte[ bufferSize ];

            //

            response.setContentType( "text/plain" );
            response.setBufferSize( bufferSize );
            response.setContentLength( fileLength );

            //

            FileInputStream     fis  = new FileInputStream( file );
            ServletOutputStream out  = response.getOutputStream();
            int                 read = 0;

            while(( read = fis.read( buffer )) > 0 ) {
                out.write( buffer,0,read );
            }

            out.flush();
            out.close();
            fis.close();

            //

            time = System.currentTimeMillis() - time;

            double speed = ( fileLength / 1024 ) / (( double )time / 1000 );

            log.info( "length=" + fileLength + " - " + time + " ms - " + speed + " kB/sec" );
        } catch( IOException ex ) {
            log.log( Level.SEVERE,"stream" + ex );
        }

        return true;
    }    // processTraceParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     */

    private void createSummaryPage( HttpServletRequest request,HttpServletResponse response ) throws IOException {
        WebDoc doc = WebDoc.create( "OpenXpertya Server Monitor" );

        // log.info("ServletConfig=" + getServletConfig());
        // GrupoServidorOXP.get().dump();

        // Body

        body b = doc.getBody();

        // Message

        if( m_message != null ) {
            b.addElement( m_message );
        }

        // Summary

        table table = new table();

        table.setBorder( 1 );
        table.setCellSpacing( 2 );
        table.setCellPadding( 2 );

        //

        tr line = new tr();

        line.addElement( new th().addElement( OpenXpertya.getName()));
        line.addElement( new td().addElement( OpenXpertya.getVersion()));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( OpenXpertya.getImplementationVendor()));
        line.addElement( new td().addElement( OpenXpertya.getImplementationVersion()));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( "Manager" ));
        line.addElement( new td().addElement( WebEnv.getCellContent( m_serverMgr.getDescription())));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( "Start - Elapsed" ));
        line.addElement( new td().addElement( WebEnv.getCellContent( m_serverMgr.getStartTime()) + " - " + TimeUtil.formatElapsed( m_serverMgr.getStartTime())));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( "Servers" ));
        line.addElement( new td().addElement( WebEnv.getCellContent( m_serverMgr.getServerCount())));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( "Last Updated" ));
        line.addElement( new td().addElement( new Timestamp( System.currentTimeMillis()).toString()));
        table.addElement( line );
        b.addElement( table );

        //

        p para = new p();
        a link = new a( "MonitorOXP?Action=Start_All","Start All" );

        para.addElement( link );
        para.addElement( " - " );
        link = new a( "MonitorOXP?Action=Stop_All","Stop All" );
        para.addElement( link );
        para.addElement( " - " );
        link = new a( "MonitorOXP","Refresh" );
        para.addElement( link );
        b.addElement( para );

        // ***** Server Links *****

        b.addElement( new hr());
        para = new p();

        ServidorOXP[] servers = m_serverMgr.getAll();

        for( int i = 0;i < servers.length;i++ ) {
            if( i > 0 ) {
                para.addElement( new br());
            }

            ServidorOXP server = servers[ i ];

            link = new a( "#" + server.getServerID(),server.getName());
            para.addElement( link );

            font status = null;

            if( server.isAlive()) {
                status = new font().setColor( HtmlColor.GREEN ).addElement( " (Running)" );
            } else {
                status = new font().setColor( HtmlColor.RED ).addElement( " (Stopped)" );
            }

            para.addElement( status );
        }

        b.addElement( para );

        // **** Log Management ****

        createLogMgtPage( b );

        // ***** Server Details *****

        for( int i = 0;i < servers.length;i++ ) {
            ServidorOXP server = servers[ i ];

            b.addElement( new hr());
            b.addElement( new a().setName( server.getServerID()));
            b.addElement( new h2( server.getName()));

            //

            table = new table();
            table.setBorder( 1 );
            table.setCellSpacing( 2 );
            table.setCellPadding( 2 );

            // Status

            line = new tr();

            if( server.isAlive()) {
                String msg = "Stop";

                if( server.isInterrupted()) {
                    msg += " (Interrupted)";
                }

                link = new a( "MonitorOXP?Action=Stop_" + server.getServerID(),msg );

                if( server.isSleeping()) {
                    line.addElement( new th().addElement( "Sleeping" ));
                    line.addElement( new td().addElement( link ));
                } else {
                    line.addElement( new th().addElement( "Running" ));
                    line.addElement( new td().addElement( link ));
                }

                table.addElement( line );
                line = new tr();
                line.addElement( new th().addElement( "Start - Elapsed" ));
                line.addElement( new td().addElement( WebEnv.getCellContent( server.getStartTime()) + " - " + TimeUtil.formatElapsed( server.getStartTime())));
            } else {
                String msg = "Start";

                if( server.isInterrupted()) {
                    msg += " (Interrupted)";
                }

                line.addElement( new th().addElement( "Not Started" ));
                link = new a( "MonitorOXP?Action=Start_" + server.getServerID(),msg );
                line.addElement( new td().addElement( link ));
            }

            table.addElement( line );

            //

            line = new tr();
            line.addElement( new th().addElement( "Description" ));
            line.addElement( new td().addElement( WebEnv.getCellContent( server.getDescription())));
            table.addElement( line );

            //

            line = new tr();
            line.addElement( new th().addElement( "Last Run" ));
            line.addElement( new td().addElement( WebEnv.getCellContent( server.getDateLastRun())));
            table.addElement( line );
            line = new tr();
            line.addElement( new th().addElement( "Info" ));
            line.addElement( new td().addElement( WebEnv.getCellContent( server.getServerInfo())));
            table.addElement( line );

            //

            line = new tr();
            line.addElement( new th().addElement( "Next Run" ));

            td td = new td();

            td.addElement( WebEnv.getCellContent( server.getDateNextRun( false )));
            td.addElement( " - " );
            link = new a( "MonitorOXP?RunNow=" + server.getServerID(),"(Run Now)" );
            td.addElement( link );
            line.addElement( td );
            table.addElement( line );

            //

            line = new tr();
            line.addElement( new th().addElement( "Statistics" ));
            line.addElement( new td().addElement( server.getStatistics()));
            table.addElement( line );

            //

            // Add table to Body

            b.addElement( table );
            link = new a( "#top","Top" );
            b.addElement( link );
            b.addElement( " - " );
            link = new a( "MonitorOXP?Log=" + server.getServerID(),"Log" );
            b.addElement( link );
            b.addElement( " - " );
            link = new a( "MonitorOXP","Refresh" );
            b.addElement( link );
        }

        // fini

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // createSummaryPage

    /**
     * Descripción de Método
     *
     *
     * @param bb
     */

    private void createLogMgtPage( body bb ) {
        bb.addElement( new hr());

        // Ini Parameters

        table table = new table();

        table.setBorder( 1 );
        table.setCellSpacing( 2 );
        table.setCellPadding( 2 );

        //

        Properties ctx    = new Properties();
        MSystem    system = MSystem.get( ctx );
        tr         line   = new tr();

        line.addElement( new th().addElement( system.getDBAddress()));
        line.addElement( new td().addElement( Ini.getOXPHome()));
        table.addElement( line );

        // OS + Name

        line = new tr();

        String info = System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" );
        String s = System.getProperty( "sun.os.patch.level" );

        if( (s != null) && (s.length() > 0) ) {
            info += " (" + s + ")";
        }

        line.addElement( new th().addElement( info ));
        info = system.getName();

        if( system.getCustomPrefix() != null ) {
            info += " (" + system.getCustomPrefix() + ")";
        }

        line.addElement( new td().addElement( info ));
        table.addElement( line );

        // Java + email

        line = new tr();
        info = System.getProperty( "java.vm.name" ) + " " + System.getProperty( "java.vm.version" );
        line.addElement( new th().addElement( info ));
        line.addElement( new td().addElement( system.getUserName()));
        table.addElement( line );

        // DB + Instance

        line = new tr();

        BaseDatosOXP db = CConnection.get().getDatabase();

        info = db.getDescription();
        line.addElement( new th().addElement( info ));
        line.addElement( new td().addElement( system.getDBInstance()));
        table.addElement( line );
        line = new tr();
        line.addElement( new th().addElement( "Processor/Support" ));
        line.addElement( new td().addElement( system.getNoProcessors() + "/" + system.getSupportUnits()));
        table.addElement( line );

        //

        line = new tr();
        line.addElement( new th().addElement( new label( "TraceLevel" ).addElement( "Trace Log Level" )));

        form myForm = new form( "MonitorOXP",form.METHOD_POST,form.ENC_DEFAULT );

        // LogLevel Selection

        option[] options = new option[ CLogMgt.LEVELS.length ];

        for( int i = 0;i < options.length;i++ ) {
            options[ i ] = new option( CLogMgt.LEVELS[ i ].getName());
            options[ i ].addElement( CLogMgt.LEVELS[ i ].getName());

            if( CLogMgt.LEVELS[ i ] == CLogMgt.getLevel()) {
                options[ i ].setSelected( true );
            }
        }

        select sel = new select( "TraceLevel",options );

        myForm.addElement( sel );
        myForm.addElement( new input( input.TYPE_SUBMIT,"Set","Set" ));
        line.addElement( new td().addElement( myForm ));
        table.addElement( line );

        //

        line = new tr();

        CLogFile fileHandler = CLogFile.get( true,null );

        line.addElement( new th().addElement( "Trace File" ));
        line.addElement( new td().addElement( new a( "MonitorOXP?Trace=" + fileHandler.getFileName(),"Current" )));
        table.addElement( line );

        //

        line = new tr();
        line.addElement( new td().addElement( new a( "MonitorOXP?Trace=ROTATE","Rotate Trace Log" )));
        line.addElement( new td().addElement( new a( "MonitorOXP?Trace=DELETE","Delete all Trace Logs" )));
        table.addElement( line );

        //

        bb.addElement( table );

        // List Log Files

        p p = new p();

        p.addElement( new b( "All Log Files: " ));

        // All in dir

        File logDir = fileHandler.getLogDirectory();

        if( (logDir != null) && logDir.isDirectory()) {
            File[] logs = logDir.listFiles();

            for( int i = 0;i < logs.length;i++ ) {
                if( i != 0 ) {
                    p.addElement( " - " );
                }

                String fileName = logs[ i ].getAbsolutePath();
                a      link     = new a( "MonitorOXP?Trace=" + fileName,fileName );

                p.addElement( link );

                int size = ( int )( logs[ i ].length() / 1024 );

                if( size < 1024 ) {
                    p.addElement( " (" + size + "k)" );
                } else {
                    p.addElement( " (" + size / 1024 + "M)" );
                }
            }
        }

        bb.addElement( p );
    }    // createLogMgtPage

    /**
     * Descripción de Método
     *
     *
     * @param config
     */

    public void init( ServletConfig config ) {
        WebEnv.initWeb( config );
        log.info( "" );
        m_serverMgr = ServidorMgrOXP.get();
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
        m_serverMgr = null;
    }    // destroy

    /**
     * Descripción de Método
     *
     *
     * @param message
     * @param e
     */

    public void log( String message,Throwable e ) {
        if( e == null ) {
            log.warning( message );
        }

        log.log( Level.SEVERE,message,e );
    }    // log

    /**
     * Descripción de Método
     *
     *
     * @param message
     */

    public void log( String message ) {
        log.fine( message );
    }    // log

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletName() {
        return "MonitorOXP";
    }    // getServletName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "openXpertya Server Monitor";
    }    // getServletName
}    // MonitorOXP



/*
 *  @(#)MonitorOXP.java   24.03.06
 * 
 *  Fin del fichero MonitorOXP.java
 *  
 *  Versión 2.2
 *
 */
