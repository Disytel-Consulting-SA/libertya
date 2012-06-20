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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebEnv;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Counter extends HttpServlet implements Runnable {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Counter.class );

    /** Descripción de Campos */

    static public final String NAME = "counter";

    /** Descripción de Campos */

    private List m_requests = Collections.synchronizedList( new ArrayList());

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
            throw new ServletException( "Counter.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Counter";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "" );
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
        m_requests.add( request );
        new Thread( this ).start();
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
     */

    public void run() {
        long time = System.currentTimeMillis();

        // get Request

        HttpServletRequest request = null;

        if( m_requests.size() > 0 ) {
            request = ( HttpServletRequest )m_requests.remove( 0 );
        }

        if( request == null ) {
            log.log( Level.SEVERE,"Nothing in queue" );

            return;
        }

        Properties ctx = JSPEnv.getCtx( request );
        String     ref = request.getHeader( "referer" );

        if( (ref == null) || (ref.length() == 0) ) {
            ref = request.getRequestURL().toString();
        }

        log.info( "Referer=" + request.getHeader( "referer" ) + " - URL=" + request.getRequestURL());
    }    // run
}    // Counter



/*
 *  @(#)Counter.java   12.10.07
 * 
 *  Fin del fichero Counter.java
 *  
 *  Versión 2.2
 *
 */
