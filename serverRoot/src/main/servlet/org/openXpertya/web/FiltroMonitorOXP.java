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

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

import sun.misc.BASE64Decoder;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FiltroMonitorOXP implements Filter {

    /**
     * Constructor de la clase ...
     *
     */

    public FiltroMonitorOXP() {
        super();
        m_authorization = new Long( System.currentTimeMillis());
    }    // FiltroMonitorOXP

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private static final String AUTHORIZATION = "OXPAuthorization";

    /** Descripción de Campos */

    private Long m_authorization = null;

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( FilterConfig config ) throws ServletException {
        log.info( "" );
    }    // Init

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param chain
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doFilter( ServletRequest request,ServletResponse response,FilterChain chain ) throws IOException,ServletException {
        boolean error     = false;
        String  errorPage = "/error.html";
        boolean pass      = false;

        try {
            if( !( (request instanceof HttpServletRequest) && (response instanceof HttpServletResponse) ) ) {
                request.getRequestDispatcher( errorPage ).forward( request,response );

                return;
            }

            HttpServletRequest  req  = ( HttpServletRequest )request;
            HttpServletResponse resp = ( HttpServletResponse )response;

            // Previously checked

            HttpSession session = req.getSession( true );
            Long        compare = ( Long )session.getAttribute( AUTHORIZATION );

            if( (compare != null) && (compare.compareTo( m_authorization ) == 0) ) {
                pass = true;
            } else if( checkAuthorization( req.getHeader( "Authorization" ))) {
                session.setAttribute( AUTHORIZATION,m_authorization );
                pass = true;
            }

            // --------------------------------------------

            if( pass ) {
                chain.doFilter( request,response );
            } else {
                resp.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                resp.setHeader( "WWW-Authenticate","BASIC realm=\"OpenXpertya Server\"" );
            }

            return;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"filter",e );
        }

        request.getRequestDispatcher( errorPage ).forward( request,response );
    }    // doFilter

    /**
     * Descripción de Método
     *
     *
     * @param authorization
     *
     * @return
     */

    private boolean checkAuthorization( String authorization ) {
        if( authorization == null ) {
            return false;
        }

        try {
            String        userInfo     = authorization.substring( 6 ).trim();
            BASE64Decoder decoder      = new BASE64Decoder();
            String        namePassword = new String( decoder.decodeBuffer( userInfo ));

            // log.fine("checkAuthorization - Name:Password=" + namePassword);

            int    index    = namePassword.indexOf( ":" );
            String name     = namePassword.substring( 0,index );
            String password = namePassword.substring( index + 1 );
            MUser  user     = MUser.get( Env.getCtx(),name,password );

            if( user == null ) {
                log.warning( "User not found: '" + name + "/" + password + "'" );

                return false;
            }

            if( !user.isAdministrator()) {
                log.warning( "Not a Sys Admin = " + name );

                return false;
            }

            log.info( "Name=" + name );

            return true;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"check",e );
        }

        return false;
    }    // check

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "" );
    }    // destroy
}    // FiltroMonitorOXP



/*
 *  @(#)FiltroMonitorOXP.java   24.03.06
 * 
 *  Fin del fichero FiltroMonitorOXP.java
 *  
 *  Versión 2.2
 *
 */
