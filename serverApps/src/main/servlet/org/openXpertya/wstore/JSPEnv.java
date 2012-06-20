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



package org.openXpertya.wstore;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MClient;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.WebSessionCtx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class JSPEnv {

    /** Descripción de Campos */

    static private CLogger s_log = CLogger.getCLogger( JSPEnv.class );

    /** Descripción de Campos */

    public final static String CTX_SERVER_CONTEXT = "context";

    /** Descripción de Campos */

    public final static String CTX_DOCUMENT_DIR = "documentDir";

    /** Descripción de Campos */

    public final static String HDR_MESSAGE = "hdrMessage";

    /** Descripción de Campos */

    public final static String HDR_INFO = "hdrInfo";

    /** Descripción de Campos */

    private static CCache s_cacheCtx = new CCache( "JSPEnvCtx",2,60 );    // 60 minute refresh

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static Properties getCtx( HttpServletRequest request ) {
        WebSessionCtx wsc     = WebSessionCtx.get( request );
        HttpSession   session = request.getSession( true );

        // New Context

        if( wsc == null ) {
            Locale locale = request.getLocale();

            s_log.info( "getCtx - new (" + request.getRemoteAddr() + " - " + locale + ")" );
            wsc = WebSessionCtx.get( request,true );

            // Add Servlet Init Parameters (webStore/src/web/WEB-INF/web.xml)

            ServletContext sc = session.getServletContext();
            Enumeration    en = sc.getInitParameterNames();

            while( en.hasMoreElements()) {
                String key   = ( String )en.nextElement();
                String value = sc.getInitParameter( key );

                wsc.ctx.setProperty( key,value );
            }

            // Default Client

            int AD_Client_ID = Env.getAD_Client_ID( wsc.ctx );

            if( AD_Client_ID == 0 ) {
                AD_Client_ID = DB.getSQLValue( null,"SELECT AD_Client_ID FROM AD_Client WHERE AD_Client_ID > 11 AND IsActive='Y'" );

                if( AD_Client_ID < 0 ) {
                    AD_Client_ID = 11;    // GardenWorld
                }

                Env.setContext( wsc.ctx,"#AD_Client_ID",AD_Client_ID );
            }

            // Add Defaults

            wsc.ctx = getDefaults( wsc.ctx,AD_Client_ID );

            // ServerContext   - dev2/wstore

            wsc.ctx.put( CTX_SERVER_CONTEXT,request.getServerName() + request.getContextPath());

            // Make Context directly availabe to jsp's

            session.setAttribute( "ctx",wsc.ctx );

            // save it

            s_log.fine( "getCtx - new #" + wsc.ctx.size());

            // s_log.fine("getCtx - " + ctx);

        }

        // Add/set current user

        WebUser wu = WebUser.get( request );

        if( wu != null ) {
            int AD_User_ID = wu.getAD_User_ID();

            Env.setContext( wsc.ctx,"#AD_User_ID",AD_User_ID );    // security
        }

        // Finish

        session.setMaxInactiveInterval( 1800 );    // 30 Min  HARDCODED

        String info = ( String )wsc.ctx.get( HDR_INFO );

        if( info != null ) {
            session.setAttribute( HDR_INFO,info );
        }

        return wsc.ctx;
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     *
     * @return
     */

    private static Properties getDefaults( Properties ctx,int AD_Client_ID ) {
        Integer    key    = new Integer( AD_Client_ID );
        Properties newCtx = ( Properties )s_cacheCtx.get( key );

        if( newCtx == null ) {
            s_log.info( "getDefaults - AD_Client_ID=" + AD_Client_ID );
            newCtx = new Properties();

            // copy explicitly

            Enumeration e = ctx.keys();

            while( e.hasMoreElements()) {
                String pKey = ( String )e.nextElement();

                newCtx.setProperty( pKey,ctx.getProperty( pKey ));
            }

            // Default Trx! Org

            int AD_Org_ID = Env.getContextAsInt( newCtx,"#AD_Org_ID" );

            if( AD_Org_ID == 0 ) {
                AD_Org_ID = DB.getSQLValue( null,"SELECT AD_Org_ID FROM AD_Org " + "WHERE AD_Client_ID=? AND IsActive='Y' AND IsSummary='N' ORDER BY 1",AD_Client_ID );

                if( AD_Org_ID > 0 ) {
                    Env.setContext( newCtx,"#AD_Org_ID",AD_Org_ID );
                } else {
                    s_log.warning( "getDefaults - AD_Org_ID=" + AD_Org_ID );
                }
            }

            // Default User

            if( Env.getContextAsInt( newCtx,"#AD_User_ID" ) == 0 ) {
                int AD_User_ID = 0;    // HARDCODED - System

                Env.setContext( newCtx,"#AD_User_ID",AD_User_ID );
            }

            // Default Role for access

            if( Env.getContextAsInt( newCtx,"#AD_Role_ID" ) == 0 ) {
                int AD_Role_ID = 0;    // HARDCODED - System

                Env.setContext( newCtx,"#AD_Role_ID",AD_Role_ID );
            }

            // Warehouse of Org

            if( Env.getContextAsInt( newCtx,"#M_Warehouse_ID" ) == 0 ) {
                int M_Warehouse_ID = DB.getSQLValue( null,"SELECT M_Warehouse_ID FROM M_Warehouse " + "WHERE AD_Org_ID=? AND IsActive='Y' ORDER BY 1",AD_Org_ID );

                if( M_Warehouse_ID > 0 ) {
                    Env.setContext( newCtx,"#M_Warehouse_ID",M_Warehouse_ID );
                } else {
                    s_log.warning( "getDefaults - M_Warehouse_ID=" + M_Warehouse_ID );
                }
            }

            // Sales Rep

            if( Env.getContextAsInt( newCtx,"#SalesRep_ID" ) == 0 ) {
                int SalesRep_ID = 0;    // HARDCODED - Syatem

                Env.setContext( newCtx,"#SalesRep_ID",SalesRep_ID );
            }

            // Payment Term

            if( Env.getContextAsInt( newCtx,"#C_PaymentTerm_ID" ) == 0 ) {
                int C_PaymentTerm_ID = DB.getSQLValue( null,"SELECT C_PaymentTerm_ID FROM C_PaymentTerm " + "WHERE AD_Client_ID=? AND IsDefault='Y' ORDER BY NetDays",AD_Client_ID );

                Env.setContext( newCtx,"#C_PaymentTerm_ID",C_PaymentTerm_ID );
            }

            // Read from disk

            MClient client = MClient.get( newCtx,AD_Client_ID );

            // Name,Description, SMTPHost,RequestEMail,RequestUser, RequestUserPw

            Env.setContext( newCtx,"name",client.getName());
            Env.setContext( newCtx,"description",client.getDescription());
            Env.setContext( newCtx,"SMTPHost",client.getSMTPHost());
            Env.setContext( newCtx,EMail.CTX_REQUEST_EMAIL,client.getRequestEMail());
            Env.setContext( newCtx,EMail.CTX_REQUEST_EMAIL_USER,client.getRequestUser());
            Env.setContext( newCtx,EMail.CTX_REQUEST_EMAIL_USERPW,client.getRequestUserPW());

            // AD_Language, WebDir, WebParam1,WebParam2,WebParam3,WebParam4, WebOrderEMail

            if( (newCtx.getProperty( "#AD_Language" ) == null) && (client.getAD_Language() != null) ) {
                Env.setContext( newCtx,"#AD_Language",client.getAD_Language());
            }

            Env.setContext( newCtx,"webDir",client.getWebDir());

            String s = client.getWebParam1();

            Env.setContext( newCtx,"webParam1",(s == null)
                                               ?""
                                               :s );
            s = client.getWebParam2();
            Env.setContext( newCtx,"webParam2",(s == null)
                                               ?""
                                               :s );
            s = client.getWebParam3();
            Env.setContext( newCtx,"webParam3",(s == null)
                                               ?""
                                               :s );
            s = client.getWebParam4();
            Env.setContext( newCtx,"webParam4",(s == null)
                                               ?""
                                               :s );
            s = client.getWebParam5();
            Env.setContext( newCtx,"webParam5",(s == null)
                                               ?""
                                               :s );
            s = client.getWebParam6();
            Env.setContext( newCtx,"webParam6",(s == null)
                                               ?""
                                               :s );
            s = client.getWebOrderEMail();
            Env.setContext( newCtx,"webOrderEMail",(s == null)
                    ?""
                    :s );
            s = client.getWebInfo();

            if( (s != null) && (s.length() > 0) ) {
                Env.setContext( newCtx,HDR_INFO,s );
            }

            // M_PriceList_ID, DocumentDir

            Env.setContext( newCtx,"#M_PriceList_ID",client.getInfo().getM_PriceList_ID());
            s = client.getDocumentDir();
            Env.setContext( newCtx,CTX_DOCUMENT_DIR,(s == null)
                    ?""
                    :s );

            // Default Language

            if( newCtx.getProperty( "#AD_Language" ) == null ) {
                Env.setContext( newCtx,"#AD_Language","en_US" );
            }

            // Save - Key is AD_Client_ID

            s_cacheCtx.put( key,newCtx );
        }

        // return new Properties (pp);     seems not to work with JSP

        Enumeration e = newCtx.keys();

        while( e.hasMoreElements()) {
            String pKey = ( String )e.nextElement();

            ctx.setProperty( pKey,newCtx.getProperty( pKey ));
        }

        return ctx;
    }    // getDefaults

    /** Descripción de Campos */

    private final static String COOKIE_NAME = "CompiereWebUser";

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static String getCookieWebUser( HttpServletRequest request ) {
        Cookie[] cookies = request.getCookies();

        if( cookies == null ) {
            return null;
        }

        for( int i = 0;i < cookies.length;i++ ) {
            if( COOKIE_NAME.equals( cookies[ i ].getName())) {
                return cookies[ i ].getValue();
            }
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param webUser
     */

    public static void addCookieWebUser( HttpServletRequest request,HttpServletResponse response,String webUser ) {
        Cookie cookie = new Cookie( COOKIE_NAME,webUser );

        cookie.setComment( "OpenXpertya Web User" );
        cookie.setPath( request.getContextPath());
        cookie.setMaxAge( 2592000 );    // 30 days in seconds   60*60*24*30
        response.addCookie( cookie );
    }                                   // setCookieWebUser

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     */

    public static void deleteCookieWebUser( HttpServletRequest request,HttpServletResponse response ) {
        Cookie cookie = new Cookie( COOKIE_NAME," " );

        cookie.setComment( "OpenXpertya Web User" );
        cookie.setPath( request.getContextPath());
        cookie.setMaxAge( 1 );    // second
        response.addCookie( cookie );
    }                             // deleteCookieWebUser

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param to
     * @param subject
     * @param message
     *
     * @return
     */

    public static String sendEMail( Properties ctx,String to,String subject,String message ) {
        MClient client = MClient.get( ctx );

        //

        EMail email = new EMail( client,null,to,subject,message );

        //

        String webOrderEMail = ctx.getProperty( "webOrderEMail" );

        if( (webOrderEMail != null) && (webOrderEMail.length() > 0) ) {
            email.addBcc( webOrderEMail );
        }

        //

        return email.send();
    }    // sendEMail
}    // JSPEnv



/*
 *  @(#)JSPEnv.java   12.10.07
 * 
 *  Fin del fichero JSPEnv.java
 *  
 *  Versión 2.2
 *
 */
