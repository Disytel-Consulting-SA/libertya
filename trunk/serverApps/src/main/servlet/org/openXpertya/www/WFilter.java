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



package org.openXpertya.www;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.p;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class WFilter implements javax.servlet.Filter {

    /** Descripción de Campos */

    private FilterConfig m_filterConfig = null;

    /** Descripción de Campos */

    private boolean m_timing = false;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param filterConfig
     *
     * @throws ServletException
     */

    public void init( FilterConfig filterConfig ) throws ServletException {
        m_filterConfig = filterConfig;
        WebEnv.initWeb( filterConfig.getServletContext());

        // List all Parameters

        log.info( filterConfig.getFilterName());

        Enumeration en = filterConfig.getInitParameterNames();

        while( en.hasMoreElements()) {
            String name  = en.nextElement().toString();
            String value = filterConfig.getInitParameter( name );

            log.config( name + "=" + value );

            if( name.equals( "Timing" ) && value.equals( "Y" )) {
                m_timing = true;
            }
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        m_filterConfig = null;
    }    // destroy

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

        // Get URI

        String uri = "";

        if( request instanceof HttpServletRequest ) {
            HttpServletRequest req = ( HttpServletRequest )request;

            uri = req.getRequestURI();
        }

        // Ignore static content

        boolean check = true;

        if( !uri.startsWith( WebEnv.DIR_BASE )    
                || uri.endsWith( ".gif" ) || uri.endsWith( ".html" ) || uri.endsWith( ".css" ) || uri.endsWith( ".js" )) {
            check = false;
        }

        //

        boolean pass = true;

        // We need to check

        StringBuffer sb = new StringBuffer( "| Parameters" );

        if( check ) {

            // print parameter

            Enumeration en = request.getParameterNames();

            while( en.hasMoreElements()) {
                String name = ( String )en.nextElement();

                sb.append( " - " ).append( name ).append( "=" ).append( request.getParameter( name ));
            }

            if( uri.endsWith( "WWindowStatus" )) {
                pass = false;
            }
        }

        if( pass && check ) {
            log.info( "doFilter - Start " + uri + sb.toString());
        }

        // Timing

        long myTime = 0l;

        if( pass && check && m_timing ) {
            myTime = System.currentTimeMillis();
        }

        // **  Start   **

        if( pass ) {
            chain.doFilter( request,response );
        } else {
            log.warning( "doFilter - Rejected " + uri );

            String msg = "Error: Access Rejected";
            WebDoc doc = WebDoc.create( msg );

            // Body

            body b = doc.getBody();

            b.addElement( new p( uri,AlignType.CENTER ));

            // fini

            response.setContentType( "text/html" );

            PrintWriter out = new PrintWriter( response.getOutputStream());

            doc.output( out );
            out.close();
        }

        // Post

        if( check && pass ) {
            if( m_timing ) {
                myTime = System.currentTimeMillis() - myTime;
            }

            log.info( "doFilter - End   " + uri + "| " + ( m_timing
                    ?String.valueOf( myTime )
                    :null ));
        }
    }    // doFilter

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        if( m_filterConfig == null ) {
            return( "WFilter[]" );
        }

        StringBuffer sb = new StringBuffer( "WFilter[" );

        sb.append( m_filterConfig );
        sb.append( "]" );

        return( sb.toString());
    }    // toString
}    // Filter



/*
 *  @(#)WFilter.java   23.03.06
 * 
 *  Fin del fichero WFilter.java
 *  
 *  Versión 2.2
 *
 */
