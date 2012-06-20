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
import java.io.PrintWriter;
import java.util.logging.Level;

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
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class StoreFilter implements javax.servlet.Filter {

    /** Descripción de Campos */

    private static CLogger log = null;

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( FilterConfig config ) throws ServletException {
        WebEnv.initWeb( config.getServletContext());

        if( log == null ) {
            log = CLogger.getCLogger( StoreFilter.class );
        }

        log.info( config.getFilterName());
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void destroy() {}    // destroy

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

        boolean check = uri.indexOf( "Servlet" ) != -1;
        boolean pass  = true;

        // We need to check

        if( check ) {
            String enc = request.getCharacterEncoding();

            try {
                enc = request.getCharacterEncoding();

                if( enc == null ) {
                    request.setCharacterEncoding( WebEnv.ENCODING );
                }

                if( enc == null ) {
                    log.finer( "Checked=" + uri );
                } else {
                    log.finer( "Checked=" + uri + " - Enc=" + enc );
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"Set CharacterEndocung=" + enc + "->" + WebEnv.ENCODING,e );
            }
        }

        // else
        // log.finer("NotChecked=" + uri);

        // **  Start   **

        if( pass ) {
            chain.doFilter( request,response );
        } else {
            log.warning( "Rejected " + uri );

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
    }    // doFilter
}    // StoreFilter



/*
 *  @(#)StoreFilter.java   12.10.07
 * 
 *  Fin del fichero StoreFilter.java
 *  
 *  Versión 2.2
 *
 */
