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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MClick;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebEnv;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Click extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static public final String NAME = "click";

    /** Descripción de Campos */

    static public final String PARA_TARGET = "target";

    /** Descripción de Campos */

    static public final String DEFAULT_TARGET = "http://www.openxpertya.org/";

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );

        if( !WebEnv.initWeb( config )) {
            throw new ServletException( "Click.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Click Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

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

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        long time = System.currentTimeMillis();

        request.getSession( true );    // force create session for ctx

        //

        String url = getTargetURL( request );

        response.sendRedirect( url );
        response.flushBuffer();
        log.fine( "redirect - " + url );

        // Save Click

        saveClick( request,url );

        //

        log.fine( url + " - " + ( System.currentTimeMillis() - time ) + "ms" );
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

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    private String getTargetURL( HttpServletRequest request ) {

        // Get Named Parameter             -       /click?target=www...

        String url = request.getParameter( PARA_TARGET );

        // Check parameters                -       /click?www...

        if( (url == null) || (url.length() == 0) ) {
            Enumeration e = request.getParameterNames();

            if( e.hasMoreElements()) {
                url = ( String )e.nextElement();
            }
        }

        // Check Path                              -       /click/www...

        if( (url == null) || (url.length() == 0) ) {
            url = request.getPathInfo();

            if( url != null ) {
                url = url.substring( 1 );    // cut off initial /
            }
        }

        // Still nothing

        if( (url == null) || (url.length() == 0) ) {
            url = DEFAULT_TARGET;
        }

        // add http protocol

        if( url.indexOf( "://" ) == -1 ) {
            url = "http://" + url;
        }

        return url;
    }    // getTargetURL

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param url
     *
     * @return
     */

    private boolean saveClick( HttpServletRequest request,String url ) {
        Properties ctx = JSPEnv.getCtx( request );

        //

        MClick mc = new MClick( ctx,url,null );

        mc.setRemote_Addr( request.getRemoteAddr());
        mc.setRemote_Host( request.getRemoteHost());

        String ref = request.getHeader( "referer" );

        if( (ref == null) || (ref.length() == 0) ) {
            ref = request.getRequestURL().toString();
        }

        mc.setReferrer( ref );

        //

        mc.setAcceptLanguage( request.getHeader( "accept-language" ));
        mc.setUserAgent( request.getHeader( "user-agent" ));

        //

        HttpSession session = request.getSession( false );

        if( session != null ) {
            WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

            if( wu != null ) {
                mc.setEMail( wu.getEmail());
                mc.setAD_User_ID( wu.getAD_User_ID());
            }
        }

        return mc.save();
    }    // saveClick
}    // Click



/*
 *  @(#)Click.java   12.10.07
 * 
 *  Fin del fichero Click.java
 *  
 *  Versión 2.2
 *
 */
