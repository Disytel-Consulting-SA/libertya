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

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.comment;
import org.apache.ecs.xhtml.h3;
import org.apache.ecs.xhtml.hr;
import org.apache.ecs.xhtml.img;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.OpenXpertya;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebEnv {

    /** Descripción de Campos */

    public static boolean DEBUG = true;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WebEnv.class );

    /** Descripción de Campos */

    public static final String DIR_BASE = "/openXpertya";    // /openXpertya

    /** Descripción de Campos */

    private static final String DIR_IMAGE = "images";    // /openXpertya/images

    /** Descripción de Campos */

    private static final String STYLE_STD = "standard.css";    // /openXpertya/standard.css

    /** Descripción de Campos */

    private static final String LOGO = "LogoSmall.gif";    // /openXpertya/LogoSmall.gif

    /** Descripción de Campos */

    private static final String DIR_STORE = "store";    // /openXpertya/store

    /** Descripción de Campos */

    public static final String TARGET_CMD = "WCmd";

    /** Descripción de Campos */

    public static final String TARGET_MENU = "WMenu";

    /** Descripción de Campos */

    public static final String TARGET_WINDOW = "WWindow";

    /** Descripción de Campos */

    public static final String TARGET_POPUP = "WPopUp";

    /** Descripción de Campos */

    public static final String CHARACTERSET = "utf-8";    // Default: UNKNOWN

    /** Descripción de Campos */

    public static final String ENCODING = "UTF-8";

    /** Descripción de Campos */

    public static final String COOKIE_INFO = "OxpInfo";

    /** Descripción de Campos */

    public static final int TIMEOUT = 15 * 60;

    /** Descripción de Campos */

    private static boolean s_initOK = false;

    /** Descripción de Campos */

    public static String NBSP = "&nbsp;";

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @return
     */

    public static boolean initWeb( ServletConfig config ) {
        if( s_initOK ) {
            log.info( config.getServletName());

            return true;
        }

        Enumeration en = config.getInitParameterNames();

        while( en.hasMoreElements()) {
            String name  = en.nextElement().toString();
            String value = config.getInitParameter( name );

            System.setProperty( name,value );
        }

        boolean retValue = initWeb( config.getServletContext());

        // Logging now initiated

        log.info( "Servlet Init Parameter: " + config.getServletName());
        en = config.getInitParameterNames();

        while( en.hasMoreElements()) {
            String name  = en.nextElement().toString();
            String value = config.getInitParameter( name );

            log.config( name + "=" + value );
        }

        return retValue;
    }    // initWeb

    /**
     * Descripción de Método
     *
     *
     * @param context
     *
     * @return
     */

    public static boolean initWeb( ServletContext context ) {
        if( s_initOK ) {
            return true;
        }

        // Load Environment Variables (serverApps/src/web/WEB-INF/web.xml)

        Enumeration en = context.getInitParameterNames();

        while( en.hasMoreElements()) {
            String name  = en.nextElement().toString();
            String value = context.getInitParameter( name );

            System.setProperty( name,value );
        }

        try {
            s_initOK = OpenXpertya.startup( false );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"startup",ex );
        }

        if( !s_initOK ) {
            return false;
        }

        log.info( "Servlet Context Init Parameters: " + context.getServletContextName());
        en = context.getInitParameterNames();

        while( en.hasMoreElements()) {
            String name  = en.nextElement().toString();
            String value = context.getInitParameter( name );

            log.config( name + "=" + value );
        }

        return s_initOK;
    }    // initWeb

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @return
     */

    public static int getAD_Client_ID( ServletConfig config ) {

        // Get Client from Servlet init

        String oo = config.getInitParameter( "AD_Client_ID" );

        // Get Client from Web Context

        if( oo == null ) {
            oo = config.getServletContext().getInitParameter( "AD_Client_ID" );
        }

        if( oo == null ) {
            log.log( Level.SEVERE,"AD_Client_ID is null" );

            return 0;
        }

        int AD_Client_ID = 0;

        try {
            AD_Client_ID = Integer.parseInt( oo );
        } catch( NumberFormatException ex ) {
            log.log( Level.SEVERE,"AD_Client_ID=" + oo,ex );
        }

        return AD_Client_ID;
    }    // getAD_Client_ID

    /**
     * Descripción de Método
     *
     *
     * @param servlet
     *
     * @return
     */

    public static Connection getConnection( HttpServlet servlet ) {

        // Get Info from Servlet Context

        String user = servlet.getInitParameter( "dbUID" );
        String pwd  = servlet.getInitParameter( "dbPWD" );

        // Get Client Web Context

        if( user == null ) {
            user = servlet.getServletContext().getInitParameter( "dbUID" );
        }

        if( pwd == null ) {
            pwd = servlet.getServletContext().getInitParameter( "dbPWD" );
        }

        // Defaults

        if( user == null ) {
            user = "openxp";
        }

        if( user == null ) {
            user = "openxp";
        }

        return null;
    }    // getDB_UID

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @return
     */

    public static String getDB_UID( ServletConfig config ) {

        // Get DB User from Servlet init

        String user = config.getInitParameter( "dbUID" );

        // Get Client from Web Context

        if( user == null ) {
            user = config.getServletContext().getInitParameter( "dbUID" );
        }

        if( user == null ) {
            log.log( Level.SEVERE,"DB_UID is null" );

            return "openxp";
        }

        return user;
    }    // getDB_UID

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @return
     */

    public static String getDB_PWD( ServletConfig config ) {

        // Get DB User from Servlet init

        String pwd = config.getInitParameter( "dbPWD" );

        // Get Client from Web Context

        if( pwd == null ) {
            pwd = config.getServletContext().getInitParameter( "dbPWD" );
        }

        if( pwd == null ) {
            log.log( Level.SEVERE,"DB_PWD is null" );

            return "openxp";
        }

        return pwd;
    }    // getDB_PWD

    /**
     * Descripción de Método
     *
     *
     * @param entry
     *
     * @return
     */

    public static String getBaseDirectory( String entry ) {
        StringBuffer sb = new StringBuffer( DIR_BASE );

        if( !entry.startsWith( "/" )) {
            sb.append( "/" );
        }

        sb.append( entry );

        return sb.toString();
    }    // getBaseDirectory

    /**
     * Descripción de Método
     *
     *
     * @param entry
     *
     * @return
     */

    public static String getImageDirectory( String entry ) {
        StringBuffer sb = new StringBuffer( DIR_BASE );

        sb.append( "/" ).append( DIR_IMAGE );

        if( !entry.startsWith( "/" )) {
            sb.append( "/" );
        }

        sb.append( entry );

        return sb.toString();
    }    // getImageDirectory

    /**
     * Descripción de Método
     *
     *
     * @param entry
     *
     * @return
     */

    public static String getStoreDirectory( String entry ) {
        StringBuffer sb = new StringBuffer( DIR_BASE );

        sb.append( "/" ).append( DIR_STORE );

        if( !entry.startsWith( "/" )) {
            sb.append( "/" );
        }

        sb.append( entry );

        return sb.toString();
    }    // getStoreDirectory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getLogoURL() {
        return getBaseDirectory( LOGO );
    }    // getLogoPath

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static img getLogo() {
        return new img( getLogoURL()).setAlign( AlignType.RIGHT )

        // Changing the copyright notice in any way violates the license
        // and you'll be held liable for any damage claims

        .setAlt( "&copy; FUNDESLE/openXpertya" );
    }    // getLogo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getStylesheetURL() {
        return getBaseDirectory( STYLE_STD );
    }    // getStylesheetURL

    /**
     * Descripción de Método
     *
     *
     * @param content
     *
     * @return
     */

    public static String getCellContent( Object content ) {
        if( content == null ) {
            return NBSP;
        }

        String str = content.toString();

        if( str.length() == 0 ) {
            return NBSP;
        }

        return str;
    }    // getCellContent

    /**
     * Descripción de Método
     *
     *
     * @param content
     *
     * @return
     */

    public static String getCellContent( int content ) {
        return String.valueOf( content );
    }    // getCellContent

    /**
     * Descripción de Método
     *
     *
     * @param config
     */

    public static void dump( ServletConfig config ) {
        log.config( "ServletConfig " + config.getServletName());
        log.config( "- Context=" + config.getServletContext());

        if( !CLogMgt.isLevelFiner()) {
            return;
        }

        boolean     first = true;
        Enumeration e     = config.getInitParameterNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "InitParameter:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = config.getInitParameter( key );

            log.finer( "- " + key + " = " + value );
        }
    }    // dump (ServletConfig)

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    public static void dump( ServletContext ctx ) {
        log.config( "ServletContext " + ctx.getServletContextName());
        log.config( "- ServerInfo=" + ctx.getServerInfo());

        if( !CLogMgt.isLevelFiner()) {
            return;
        }

        boolean     first = true;
        Enumeration e     = ctx.getInitParameterNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "InitParameter:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = ctx.getInitParameter( key );

            log.finer( "- " + key + " = " + value );
        }

        first = true;
        e     = ctx.getAttributeNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "Attributes:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = ctx.getAttribute( key );

            log.finer( "- " + key + " = " + value );
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param session
     */

    public static void dump( HttpSession session ) {
        log.config( "Session " + session.getId());
        log.config( "- Created=" + new Timestamp( session.getCreationTime()));

        if( !CLogMgt.isLevelFiner()) {
            return;
        }

        boolean     first = true;
        Enumeration e     = session.getAttributeNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "Attributes:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = session.getAttribute( key );

            log.finer( "- " + key + " = " + value );
        }
    }    // dump (session)

    /**
     * Descripción de Método
     *
     *
     * @param request
     */

    public static void dump( HttpServletRequest request ) {
        log.config( "Request " + request.getProtocol() + " " + request.getMethod());

        if( !CLogMgt.isLevelFiner()) {
            return;
        }

        log.finer( "- Server=" + request.getServerName() + ", Port=" + request.getServerPort());
        log.finer( "- ContextPath=" + request.getContextPath() + ", ServletPath=" + request.getServletPath() + ", Query=" + request.getQueryString());
        log.finer( "- URI=" + request.getRequestURI() + ", URL=" + request.getRequestURL());
        log.finer( "- AuthType=" + request.getAuthType());
        log.finer( "- Secure=" + request.isSecure());
        log.finer( "- PathInfo=" + request.getPathInfo() + " - " + request.getPathTranslated());
        log.finer( "- UserPrincipal=" + request.getUserPrincipal());

        //

        boolean     first = true;
        Enumeration e     = request.getHeaderNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "- Header:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = request.getHeader( key );

            log.finer( "  - " + key + " = " + value );
        }

        first = true;
        e     = request.getParameterNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "- Parameter:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            String value = WebUtil.getParameter( request,key );

            log.finer( "  - " + key + " = " + value );
        }

        first = true;
        e     = request.getAttributeNames();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "- Attributes:" );
            }

            first = false;

            String key   = ( String )e.nextElement();
            Object value = request.getAttribute( key );

            log.finer( "  - " + key + " = " + value );
        }

        Cookie[] ccc = request.getCookies();

        if( ccc != null ) {
            for( int i = 0;i < ccc.length;i++ ) {
                if( i == 0 ) {
                    log.finer( "- Cookies:" );
                }

                log.finer( "  - " + ccc[ i ].getName() + ", Domain=" + ccc[ i ].getDomain() + ", Path=" + ccc[ i ].getPath() + ", MaxAge=" + ccc[ i ].getMaxAge());
            }
        }

        log.finer( "- Encoding=" + request.getCharacterEncoding());
        log.finer( "- Locale=" + request.getLocale());
        first = true;
        e     = request.getLocales();

        while( e.hasMoreElements()) {
            if( first ) {
                log.finer( "- Locales:" );
            }

            first = false;
            log.finer( "  - " + e.nextElement());
        }
    }    // dump (Request)

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     * @param body
     */

    public static void addFooter( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,body body ) {
        body.addElement( new hr());
        body.addElement( new comment( " --- Footer Start --- " ));

        // Command Line

        p footer = new p();

        footer.addElement( "&copy; 2006 FUNDESLE\n" ).setAlign("center");
        body.addElement( footer );

        // Add ServletInfo

        body.addElement( new br());
        body.addElement( getServletInfo( request,response,servlet ));
        body.addElement( new script( "hide('DEBUG');" ));
        body.addElement( new comment( " --- Footer End --- " ));
    }    // getFooter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     *
     * @return
     */

    private static table getServletInfo( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet ) {
        table table = new table();

        table.setID( "DEBUG" );

        Enumeration e;
        tr          space = new tr().addElement( new td().addElement( "." ));

        // Request Info

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Request Info" ))));
        table.addElement( new tr().addElement( new td().addElement( "Method" )).addElement( new td().addElement( request.getMethod())));
        table.addElement( new tr().addElement( new td().addElement( "Protocol" )).addElement( new td().addElement( request.getProtocol())));
        table.addElement( new tr().addElement( new td().addElement( "URI" )).addElement( new td().addElement( request.getRequestURI())));
        table.addElement( new tr().addElement( new td().addElement( "Context Path" )).addElement( new td().addElement( request.getContextPath())));
        table.addElement( new tr().addElement( new td().addElement( "Servlet Path" )).addElement( new td().addElement( request.getServletPath())));
        table.addElement( new tr().addElement( new td().addElement( "Path Info" )).addElement( new td().addElement( request.getPathInfo())));
        table.addElement( new tr().addElement( new td().addElement( "Path Translated" )).addElement( new td().addElement( request.getPathTranslated())));
        table.addElement( new tr().addElement( new td().addElement( "Query String" )).addElement( new td().addElement( request.getQueryString())));
        table.addElement( new tr().addElement( new td().addElement( "Content Length" )).addElement( new td().addElement( "" + request.getContentLength())));
        table.addElement( new tr().addElement( new td().addElement( "Content Type" )).addElement( new td().addElement( request.getContentType())));
        table.addElement( new tr().addElement( new td().addElement( "Character Encoding" )).addElement( new td().addElement( request.getCharacterEncoding())));
        table.addElement( new tr().addElement( new td().addElement( "Locale" )).addElement( new td().addElement( request.getLocale().toString())));
        table.addElement( new tr().addElement( new td().addElement( "Schema" )).addElement( new td().addElement( request.getScheme())));
        table.addElement( new tr().addElement( new td().addElement( "Server Name" )).addElement( new td().addElement( request.getServerName())));
        table.addElement( new tr().addElement( new td().addElement( "Server Port" )).addElement( new td().addElement( "" + request.getServerPort())));
        table.addElement( new tr().addElement( new td().addElement( "Remote User" )).addElement( new td().addElement( request.getRemoteUser())));
        table.addElement( new tr().addElement( new td().addElement( "Remote Address" )).addElement( new td().addElement( request.getRemoteAddr())));
        table.addElement( new tr().addElement( new td().addElement( "Remote Host" )).addElement( new td().addElement( request.getRemoteHost())));
        table.addElement( new tr().addElement( new td().addElement( "Authorization Type" )).addElement( new td().addElement( request.getAuthType())));
        table.addElement( new tr().addElement( new td().addElement( "User Principal" )).addElement( new td().addElement( (request.getUserPrincipal() == null)
                ?""
                :request.getUserPrincipal().toString())));
        table.addElement( new tr().addElement( new td().addElement( "IsSecure" )).addElement( new td().addElement( request.isSecure()
                ?"true"
                :"false" )));

        // Request Attributes

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Request Attributes" ))));
        e = request.getAttributeNames();

        while( e.hasMoreElements()) {
            String name   = e.nextElement().toString();
            String attrib = request.getAttribute( name ).toString();

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( attrib )));
        }

        // Request Parameter

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Req Parameters" ))));
        e = request.getParameterNames();

        while( e.hasMoreElements()) {
            String name = ( String )e.nextElement();
            String para = WebUtil.getParameter( request,name );

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( para )));
        }

        // Request Header

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Req Header" ))));
        e = request.getHeaderNames();

        while( e.hasMoreElements()) {
            String name = ( String )e.nextElement();

            if( !name.equals( "Cockie" )) {
                String hdr = request.getHeader( name );

                table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( hdr )));
            }
        }

        // Request Cookies

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Req Cookies" ))));

        Cookie[] cc = request.getCookies();

        if( cc != null ) {
            for( int i = 0;i < cc.length;i++ ) {

                // Name and Comment

                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName())).addElement( new td().addElement( cc[ i ].getValue())));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Comment" )).addElement( new td().addElement( cc[ i ].getComment())));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Domain" )).addElement( new td().addElement( cc[ i ].getDomain())));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Max Age" )).addElement( new td().addElement( "" + cc[ i ].getMaxAge())));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Path" )).addElement( new td().addElement( cc[ i ].getPath())));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Is Secure" )).addElement( new td().addElement( cc[ i ].getSecure()
                        ?"true"
                        :"false" )));
                table.addElement( new tr().addElement( new td().addElement( cc[ i ].getName() + ": Version" )).addElement( new td().addElement( "" + cc[ i ].getVersion())));
            }
        }    // Cookies

        // Request Session Info

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Req Session" ))));

        HttpSession session = request.getSession( true );

        table.addElement( new tr().addElement( new td().addElement( "Session ID" )).addElement( new td().addElement( session.getId())));

        Timestamp ts = new Timestamp( session.getCreationTime());

        table.addElement( new tr().addElement( new td().addElement( "Created" )).addElement( new td().addElement( ts.toString())));
        ts = new Timestamp( session.getLastAccessedTime());
        table.addElement( new tr().addElement( new td().addElement( "Accessed" )).addElement( new td().addElement( ts.toString())));
        table.addElement( new tr().addElement( new td().addElement( "Request Session ID" )).addElement( new td().addElement( request.getRequestedSessionId())));
        table.addElement( new tr().addElement( new td().addElement( ".. via Cookie" )).addElement( new td().addElement( "" + request.isRequestedSessionIdFromCookie())));
        table.addElement( new tr().addElement( new td().addElement( ".. via URL" )).addElement( new td().addElement( "" + request.isRequestedSessionIdFromURL())));
        table.addElement( new tr().addElement( new td().addElement( "Valid" )).addElement( new td().addElement( "" + request.isRequestedSessionIdValid())));

        // Request Session Attributes

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Session Attributes" ))));
        e = session.getAttributeNames();

        while( e.hasMoreElements()) {
            String name   = ( String )e.nextElement();
            String attrib = session.getAttribute( name ).toString();

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( attrib )));
        }

        // Response Info

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Response" ))));
        table.addElement( new tr().addElement( new td().addElement( "Buffer Size" )).addElement( new td().addElement( String.valueOf( response.getBufferSize()))));
        table.addElement( new tr().addElement( new td().addElement( "Character Encoding" )).addElement( new td().addElement( response.getCharacterEncoding())));
        table.addElement( new tr().addElement( new td().addElement( "Locale" )).addElement( new td().addElement( (response.getLocale() == null)
                ?"null"
                :response.getLocale().toString())));

        // Servlet

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Servlet" ))));
        table.addElement( new tr().addElement( new td().addElement( "Name" )).addElement( new td().addElement( servlet.getServletName())));
        table.addElement( new tr().addElement( new td().addElement( "Info" )).addElement( new td().addElement( servlet.getServletInfo())));

        // Servlet Init

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Servlet Init Parameter" ))));
        e = servlet.getInitParameterNames();

        // same as:  servlet.getServletConfig().getInitParameterNames();

        while( e.hasMoreElements()) {
            String name = ( String )e.nextElement();
            String para = servlet.getInitParameter( name );

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( para )));
        }

        // Servlet Context

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Servlet Context" ))));

        ServletContext servCtx = servlet.getServletContext();

        e = servCtx.getAttributeNames();

        while( e.hasMoreElements()) {
            String name   = ( String )e.nextElement();
            String attrib = servCtx.getAttribute( name ).toString();

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( attrib )));
        }

        // Servlet Context

        table.addElement( space );
        table.addElement( new tr().addElement( new td().addElement( new h3( "Servlet Context Init Parameter" ))));
        e = servCtx.getInitParameterNames();

        while( e.hasMoreElements()) {
            String name   = ( String )e.nextElement();
            String attrib = servCtx.getInitParameter( name ).toString();

            table.addElement( new tr().addElement( new td().addElement( name )).addElement( new td().addElement( attrib )));
        }

        /*  */

        return table;
    }    // getServletInfo
}    // WEnv



/*
 *  @(#)WebEnv.java   02.07.07
 * 
 *  Fin del fichero WebEnv.java
 *  
 *  Versión 2.2
 *
 */